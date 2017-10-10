package com.kys26.webthings.wifi;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kys26.webthings.gateway.CaptureActivity;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.httpnetworks.VolleyJsonRequest;
import com.kys26.webthings.main.BaseActivity;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.recieve.WifiConnectReceiver;
import com.kys26.webthings.util.StringUtil;
import com.zhangyx.MyGestureLock.R;

import java.util.ArrayList;
import java.util.HashMap;


public class SettingWifi extends BaseActivity {
    Button wifiBg;
    TextView wifiName;
    TextView wifiReminderText;
    ListView wifiList;
    /**
     * 初始化动画
     */
    private AnimationDrawable animation;
    /**
     * 声明Hanler处理UI
     */
    private Handler mhandler;
    /**
     * 声明全局的List,装载扫描到的wifi
     */
    private ArrayList<HashMap<String, Object>> listItems;
    /**wifiListView控件*/
    /**
     * 声明SimpleAdapter适配器
     */
    private SimpleAdapter listItemAdapter;
    /**
     * 进度条弹出框
     */
    private Dialog dialogProgress;
    /**
     * 选择的wifi以及其密码
     */
    private String wifiSSID, wifiPassword;
    /**
     * 用于存储扫描到wifi的SSID
     */
    private String[] SSIDs;
    /**
     * 用SharedPreferences保存SSID和密码
     */
    private SharedPreferences sharedPreferences;
    /**
     * 扫描wifi循环的堆栈
     */
    private Runnable runWifiActive;
    /**
     * 检测重置网络的循环设定一个跳出值
     */
    private boolean checkRuning = false;
    /**
     * 设置一个值，记录扫描wifi所用耗时
     */
    int timeWifiActive = 0;
    /**
     * 设置一个值，记录检测重置网络可用性所用耗时
     */
    int timeWifiInfo = 0;
    /**
     * 设置一个值，为计时器的最大值,经过debug得到这两个值的最大值设置为6000比较合适，
     * 一般开启wifi的时间是2-3秒多，再加上线程的睡眠时间足够，一旦超过这个时间则说明是已经连接超时
     */
    int timeMax = 3500;
    /**
     * 初始化wifi工具类
     */
    private WifiAdmin wifiAdmin;
    /**
     * SettingWiFi类的标识
     */
    private final String TAG = "SettingWiFi";

    private final int WIFI_SCAN_PERMISSION_CODE = 111;
    private String WifiServiceTag = "android.intent.action.WifiServiceTag";
    private WifiConnectReceiver mWifiCOnnectReceiver;
    private boolean isConnectedWifi = false;

    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //实时接收子线程中wifi网络状态
        disposeUI();
        //初始化UI
        initView();
        //利用子线程让循环执行起来实时监测网络,这样写防止重复操作使cpu负担累积
        new Thread(runWifiActive).start();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_setting_wifi;
    }

    /**
     * @function:初始化UI组件
     * @author kys_26使用者：徐建强 2015-9-1
     */
    private void initView() {
        setEnableRefresh(false, null);
        setTitle("选择网络");
        wifiBg = (Button) findViewById(R.id.wifibg);
        wifiList = (ListView) findViewById(R.id.wifi_list);
        wifiName = (TextView) findViewById(R.id.wifiNameText);
        wifiReminderText = (TextView) findViewById(R.id.reminderWifiText);
        wifiBg.setBackgroundResource(R.drawable.wifi01);
        wifiAdmin = new WifiAdmin(SettingWifi.this);
        //检查wifi是否开启,如果没有自动开启
        openWifi();
    }

    /**
     * @return null
     * @function:开启wifi
     * @author kys_26使用者：徐建强 2015-9-1
     */
    private void openWifi() {
        if (wifiAdmin.checkNetCardState() == 1) {
            //打开无线网卡
            wifiAdmin.OpenWifi();
        }
        //进度条转动
        progressDialog();
        //检测网络，如果网卡可使用，检测附近wifi
        checkNetWork();
    }

    /**
     * @return null
     * @function:检查网络
     * @author kys_26使用者：徐建强 2015-9-1
     */
    private void checkNetWork() {
        //每当跳入这个方法，就重置计时器的数值为起始值
        timeWifiActive = 0;
        //利用线程进行循环是为了防止UI线程卡死
        runWifiActive = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "线程一执行，正在检测网络");
                while (true) {
                    timeWifiActive++;
                    //在执行之前先让线程睡两秒，网卡的打开检测网络需要一点儿时间，这个时间定为两秒，测试得到两秒时间比较合适，网卡打开的时间在2-3秒之间
                    try {
                        Thread.sleep(2200);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i(TAG, "线程睡眠出现异常");
                    }
                    //线程睡眠一秒之后开始检测网络
                    if (wifiAdmin.checkNetCardState() == 3) {
                        //发现wifi，交给handler处理UI
                        Message msg = new Message();
                        msg.what = 1;
                        mhandler.sendMessage(msg);
                        break;
                    } else if (timeWifiActive > timeMax) {
                        //连接超时，交给handler处理提示信息
                        Message msg = new Message();
                        msg.what = 0;
                        mhandler.sendMessage(msg);
                        break;
                    }
                }
            }
        };
    }

    @Override
    protected void onClickLeft() {
        super.onClickLeft();
        this.finish();
    }

    /**
     * @return null
     * @function:当线程中需要处理UI时交给Handler
     * @author kys_26使用者：徐建强 2015-9-1
     */
    private void disposeUI() {
        //利用Handler处理UI界面
        mhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        Toast.makeText(SettingWifi.this, R.string.string_activity_setWIFI_timeout, Toast.LENGTH_LONG).show();
                        //进度条消失
                        dialogProgress.cancel();
                        break;
                    case 1:
                        //当wifi打开后，开始扫描周边网络
                        getWifiPermission();
                        break;
                    case 2:
                        if (msg.arg1 == 1) {
                            //保存wifi的SSid和密码,这里wifiSSId和wifiPassword是全局变量，执行到这步，说明已经捕捉到ssid和密码了
                            saveWifi(wifiSSID, wifiPassword);
                            try {
                                unregisterReceiver(mWifiCOnnectReceiver);
                            } catch (Exception e) {
                                Log.e(TAG, "e:" + e);
                            }
                        } else {
                            Toast.makeText(SettingWifi.this, "wifi连接失败，可能是密码错误", Toast.LENGTH_LONG).show();
                            //设置wifi状态背景图为无连接
                            wifiBg.setBackgroundResource(R.drawable.wifi01);
                            //设置wifi状态提示语为连接失败
                            wifiReminderText.setText("wifi连接失败，请重试");
                            wifiReminderText.setTextColor(Color.RED);
                        }
                        break;
                    case 3:
                        Toast.makeText(SettingWifi.this, R.string.string_activity_setWIFI_timeout, Toast.LENGTH_LONG).show();
                        //设置wifi状态背景图为无连接
                        wifiBg.setBackgroundResource(R.drawable.wifi01);
                        //设置wifi状态提示语为连接失败
                        wifiReminderText.setText(R.string.string_activity_setWIFI_connectfail);
                        wifiReminderText.setTextColor(Color.RED);
                        break;
                    case 4:
                        Toast.makeText(SettingWifi.this, "密码错误,请重新连接", Toast.LENGTH_LONG).show();
                        //设置wifi状态背景图为无连接
                        wifiBg.setBackgroundResource(R.drawable.wifi01);
                        //设置wifi状态提示语为连接失败
                        wifiReminderText.setText("wifi密码错误,请重试");
                        wifiReminderText.setTextColor(Color.RED);
                        try {
                            unregisterReceiver(mWifiCOnnectReceiver);
                        } catch (Exception e) {
                            Log.e(TAG, "e:" + e);
                            Toast.makeText(getApplicationContext(), "销毁广播出现问题:" + e, Toast.LENGTH_SHORT).show();
                        }
                    default:
                        break;
                }
            }
        };
    }

    /**
     * @param ssid
     * @param password
     * @author kys_26使用者：徐建强 2015-10-20
     * @function:利用SharedPreferences保存SSID和密码
     */
    private void saveWifi(String ssid, String password) {
        //利用SharedPreferences将SSID和密码保存
        sharedPreferences = getSharedPreferences("wifi", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("SSID", ssid).apply();
        sharedPreferences.edit().putString("Password", password).apply();
        //设置wifi背景图为满格
        wifiBg.setBackgroundResource(R.drawable.wifi05);
        //设置wifi状态提示语为已连接
        wifiReminderText.setText(R.string.string_activity_setWIFI_connect);
        wifiReminderText.setTextColor(Color.GRAY);
        //调用静态SharedPreferences获取cookie值
        MethodTools.sPreFerCookie = getSharedPreferences("Cookies",
                Context.MODE_PRIVATE);
        //开始进行发送数据
        VolleyJsonRequest.JsonRequestWithCookie(SettingWifi.this,
                Path.host + Path.linkCheck, null,
                MethodTools.sPreFerCookie.getString("cookie", "null"));
        //重写Handler中的message方法，获取数据后进行异步加载
        MethodTools.HnadlerVOlleyJson = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
//                if (msg.what == Code.SUCCESS) {
                //检测公网可用
                //设置wifi状态提示语公网可用，开始跳转
                wifiReminderText.setText("公网检测可用,正在进行下一步.");
                wifiReminderText.setTextColor(Color.GRAY);
                //跳转初始化界面
                startInitiazeGateway();
                Log.i(TAG, msg.obj.toString());
//                } else if (msg.what == Code.FAILURE) {
//                    //设置wifi状态提示语公网可用，开始跳转
//                    wifiReminderText.setText("检测公网不可用,必须连接一个可用网络.");
//                    wifiReminderText.setTextColor(Color.RED);
//                    //跳转初始化界面
//                    startInitiazeGateway();
//                    Log.i(TAG, msg.obj.toString());
//                }
            }
        };
    }

    /**
     * @return null
     * @function:扫描当前附近wifi
     * @author kys_26使用者：徐建强 2015-9-1
     */
    private void getSSID() {
        String AllSSid = wifiAdmin.getScanResult();
        //我在扫描网络时对数据进行了规格
        if (StringUtil.isEmpty(AllSSid)) {
            Toast.makeText(getApplicationContext(), "请把gps打开再试一试", Toast.LENGTH_SHORT).show();
        }
        SSIDs = AllSSid.split("/");

        //将扫描到的wifi传入List表中
        setWifiList(SSIDs);
        //进度条消失
        dialogProgress.cancel();
    }

    /**
     * @param ssid
     * @return null
     * @function:将扫描到的wifi填充到ListView
     * @author kys_26使用者：徐建强 2015-9-1
     */
    private void setWifiList(String[] ssid) {
        //List适配器
        listItems = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < ssid.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", R.drawable.wifi_bg);//文字
            map.put("ItemText", ssid[i]);//图片
            listItems.add(map);
            //将ssid保存在一个数组中，用于后面取值
        }
        // listItems数据源
        listItemAdapter = new SimpleAdapter(this, listItems,
                //ListItem的XML布局实现
                R.layout.list_litem_wifi_layout,
                //动态数组与ImageItem对应的子项
                new String[]{"ItemImage", "ItemText"},
                new int[]{R.id.listImage_wifi, R.id.listtext_wifi});
        wifiList.setAdapter(listItemAdapter);
        wifiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showDialogWifiPw(SSIDs[i]);
            }
        });
    }

    /**
     * @return null
     * @function:底部弹出框输入wifi密码
     * @author kys_26使用者：徐建强 2015-9-1
     */
    private void showDialogWifiPw(final String string) {
        final Dialog dialog = new Dialog(SettingWifi.this);
        //去掉title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        Window window = dialog.getWindow();
        // 设置布局
        window.setContentView(R.layout.activity_settingwifi_dialog_wifipassword);
        // 设置宽高
        window.setLayout(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        // 设置弹出的动画效果
        window.setWindowAnimations(R.style.AnimBottom);
        // 设置监听
        final EditText wifiPw = (EditText) window.findViewById(R.id.wifipassword);
        TextView textView = (TextView) window.findViewById(R.id.wifiNow);
        //显示当前选择Wifi
        textView.append(string);
        //点击事件处理
        Button ok = (Button) window.findViewById(R.id.ok_btn);
        Button cancel = (Button) window.findViewById(R.id.cancel_btn);
        final ImageView right_img = (ImageView) window.findViewById(R.id.right_img);
        right_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!flag) {
                    right_img.setImageResource(R.drawable.ck_s);
                    wifiPw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    flag = true;
                } else {
                    right_img.setImageResource(R.drawable.ck_k);
                    wifiPw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    flag = false;
                }
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (wifiPw.getText().toString().equals("")) {
                    Toast.makeText(SettingWifi.this, R.string.string_activity_setWIFI_openingWifi_nopassword, Toast.LENGTH_LONG).show();
                } else {
                    //得到SSID以及其密码保存在字符串中
                    wifiPassword = wifiPw.getText().toString();
                    wifiSSID = string;
                    //弹出框消失
                    dialog.cancel();
                    //***********************************************************************************
//                    RegisterWifiService();
                    setWiFi(wifiSSID, wifiPassword);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.cancel();
            }
        });
        // 因为用的是windows的方法，所以不管ok活cancel都要加上“dialog.cancel()”这句话，
    }

    /**
     * @param ssid
     * @param password
     * @return null
     * @function:配置wifi并进行测试网络 同时保存ssid以及密码，用于初始化下一步进行
     * @author kys_26使用者：徐建强 2015-10-20
     */
    private void setWiFi(String ssid, String password) {
        //配置的时候让wifi图动起来
        setBackgroundResource();
        //更新提示Text控件中提示词
        wifiName.setText(ssid);
        wifiReminderText.setText(R.string.string_activity_setWIFI_setting);
        wifiReminderText.setTextColor(Color.GRAY);
        //配置指定wifi网络
//        wifiAdmin.Connect(ssid, password, WifiAdmin.WifiCipherType.WIFICIPHER_WPA);
//        isConnectedWifi = false;
//        // 注册wifi广播
//        IntentFilter filter = new IntentFilter();
////        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//        final String SSID = "\"" + ssid + "\"";
//        timeWifiInfo = 0;
        Message msg = new Message();
        msg.what = 2;
        msg.arg1 = 1;
        mhandler.sendMessage(msg);
//        isConnectedWifi = true;
//        mWifiCOnnectReceiver = new WifiConnectReceiver(new WifiConnectReceiver.connectWifi() {
//            @Override
//            public void OnConnectedWifi(String ConnectedSSID) {
//                if (SSID.equals(ConnectedSSID)) {
//
//                } else {
//                    Message msg = new Message();
//                    msg.what = 4;
//                    mhandler.sendMessage(msg);
//                    isConnectedWifi = true;
//                }
//            }
//        });
//        registerReceiver(mWifiCOnnectReceiver, filter);
//        //计时wifi连接的handler
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (!isConnectedWifi) {
//                    if (timeWifiInfo < 30 * 10) {
//                        timeWifiInfo++;
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        timeWifiInfo = 0;
//                        Message msg = new Message();
//                        msg.what = 3;
//                        mhandler.sendMessage(msg);
//                        break;
//                    }
//                }
//            }
//        });
//        thread.start();
    }

    /**
     * setBackgroundResource配置动态背景
     * OnPreDrawListener实例化动画监听
     *
     * @return null
     * @author kys_26使用者：徐建强 2015-9-1
     */
    private void setBackgroundResource() {
        // TODO Auto-generated method stub
        wifiBg.setBackgroundResource(R.drawable.animtion_wifi);
        animation = (AnimationDrawable) wifiBg.getBackground();
        wifiBg.post(new Runnable() {
            @Override
            public void run() {
                animation.start();
            }
        });
    }

    /**
     * @author kys_26使用者：徐建强 2015-9-1
     * @function:得到自定义的progressDialog显示等待
     */
    public void progressDialog() {
        LayoutInflater inflater = LayoutInflater.from(SettingWifi.this);
        View v = inflater.inflate(R.layout.coustom_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ProgressBar spaceshipImage = (ProgressBar) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                SettingWifi.this, R.anim.custom_dialog_anim);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(R.string.string_activity_setWIFI_getWifi);// 设置加载信息
        dialogProgress = new Dialog(SettingWifi.this, R.style.loading_dialog);// 创建自定义样式dialog
        dialogProgress.setCancelable(false);// 不可以用“返回键”取消
        dialogProgress.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));// 设置布局
        dialogProgress.show();
    }

    /**
     * @return null
     * @function:连接网络成功以及公网检测通过 开始跳入初始化界面
     * @author kys_26使用者：徐建强 2015-11-10
     */
    private void startInitiazeGateway() {
        //用handler机制延迟一秒后跳入初始化界面
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SettingWifi.this, CaptureActivity.class));
                SettingWifi.this.finish();
            }
        }, 500);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @function android6.0以上wifi获取权限
     */
    public void getWifiPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 获取wifi连接需要定位权限,没有获取权限
//                if (shouldShowRequestPermissionRationale(Manifest.permission_group.LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_WIFI_STATE,
                }, WIFI_SCAN_PERMISSION_CODE);
                return;
//                }
            }
        } else {
            getSSID();
//            getAllNetWorkList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WIFI_SCAN_PERMISSION_CODE:
                Log.e(TAG, "grantResults.length:" + grantResults.length + "grantResults[0]:" + grantResults[0]);
                if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                        (permissions.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                        || (permissions.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                    // 允许
                    //Settings.Secure.putInt(getContentResolver(), Settings.Secure.LOCATION_MODE, 1);
                    getSSID();
//                    getAllNetWorkList();
                } else {
                    // 不允许
                    Log.e(TAG, "grantResults:" + grantResults[0]);
                    dialogProgress.cancel();
                    Toast.makeText(this, "请允许wifi开启", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
