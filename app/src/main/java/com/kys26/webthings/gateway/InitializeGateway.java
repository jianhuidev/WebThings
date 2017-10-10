package com.kys26.webthings.gateway;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.httpnetworks.VolleyJsonRequest;
import com.kys26.webthings.main.BaseActivity;
import com.kys26.webthings.main.MainActivity;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.model.GwAndDeviceDate;
import com.kys26.webthings.progress.CircleProgressBar;
import com.kys26.webthings.progress.NodeProgress;
import com.kys26.webthings.socketnetwork.SocketNetwork;
import com.kys26.webthings.util.MyToast;
import com.kys26.webthings.util.network.NetworksInfo;
import com.kys26.webthings.view.criclebutton.CircleButton;
import com.kys26.webthings.wifi.WifiAdmin;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.kys26.webthings.method.MethodTools.mGatewayIdBean;


/**
 * @function:初始化网关
 * @author:Created by 徐建强 on 2015/7/23.
 */
public class InitializeGateway extends BaseActivity {

    /**
     * TAG of InitializeGateway
     */
    private final String TAG = "InitializeGateway ";
    /**
     * Socket网络连接
     */
    private SocketNetwork socketThread;
    /**
     * Wifi工具类
     */
    private WifiAdmin mWifiAdmin;
    /**
     * 接收数据异步加载的Handler和发送数据的Handler
     */
    Handler sendhandler;
    /**
     * socket类中需要传入一个handler对象
     */
    Handler mhandler;
    /**
     * 进度条进入循环进行增值的标识
     */
    boolean wheelRunning;
    /**
     * 实时进度值
     */
    int wheelProgress = 0;
    /**
     * 当前进度条的实时上限值，不言则名，最大到100结束，初始值设为0，根据初始化的步骤实现进行实时增加
     */
    private int maxProgress = 0;
    /**
     * 节点进度条
     */
    private NodeProgress ssl;
    /**
     * 圆形进度条声明
     */
    private CircleProgressBar progressBar;
    /**
     * 顶层进度提示动画背景
     */
    private RelativeLayout prgressBg;
    /**
     * 顶层进度提示动画
     */
    private AnimationDrawable animationCriclProgress = null;
    /**
     * 圆形进度圈的左侧划出动画
     */
    private TranslateAnimation alphaAnimationCrlcleProgress;
    /**
     * 圆形控件左侧进入动画
     */
    private TranslateAnimation alphaAnimationCrlcleButton;
    /**
     * 圆形Button控件类
     */
    private CircleButton circleButton;
    /**
     * 绘制圆形进度条的堆栈
     */
    private Runnable r;
    /**
     * 提示信息TextView控件声明
     */
    @ViewInject(R.id.textReminder)
    private TextView textRemin;
    /**
     * 初始一个boolean值，作为wifi连接状态的检测标识
     */
    private boolean checkRuning = false;
    /**
     * 设置一个值，为计时器的最大值,经过debug得到这两个值的最大值设置为3800比较合适，
     * 一般开启wifi的时间是2-3秒多，再加上线程的睡眠时间足够，
     * 一旦超过这个时间则说明是已经连接超时·
     */
    private int timeMax = 3800;
    /**
     * 设置一个值，记录检测重置网络可用性所用耗时
     */
    private int timeWifiInfo = 0;
    /**
     * 文字提示信息动画
     */
    private TranslateAnimation reminderAnimation;
    /**
     * 声明一个Handler,用来处理子线程中UI事件
     */
    private Handler nhandler;
    /**
     * 用SharedPreferences读取用户选择的公网路由的SSID和密码
     */
    private SharedPreferences sharedPreferences;
    /**
     * 发送给网关的数据字符串
     */
    private String data;
    /**
     * 恢复wifi连接循环进入标识
     */
    private boolean isCheckRuning = false;
    /**
     * 恢复wifi连接时的耗时
     */
    private int timeWifiActive;
    private final int WIFI_SCAN_PERMISSION_CODE = 111;
    private ScheduledExecutorService validateService;
    private int validateIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化IOC注解
        ViewUtils.inject(this);
        //初始化UI组件
        initView();
        //Progress Thread start
        initProgress();
        //开启线程实时绘制圆形进度值
        new Thread(r).start();
        //Handler处于实时监听接收子线程中UI处理命令
        myHandlerUI();
        //利用Handler时刻等待网关配置的返回结果
        myHandlerSocket();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_initializgateway;
    }

    @Override
    protected void onClickLeft() {
        super.onClickLeft();
        this.finish();
    }

    /**
     * @function:加载UI
     * @author:Created by 徐建强 on 2015/9/23.
     * @return:null
     * @param: null
     */
    private void initView() {
        setTitle("初始化网关");
        //实例化wifi工具类
        mWifiAdmin = new WifiAdmin(InitializeGateway.this);
        //设置为屏幕处于dialog时候点击空白无效
        InitializeGateway.this.setFinishOnTouchOutside(false);
        //圆形进度效果背景图
        prgressBg = (RelativeLayout) findViewById(R.id.cricleProgress);
        //为圆形进度条动态背景初始化动画
        setBackgroundResource();
        //圆形进度
        progressBar = (CircleProgressBar) findViewById(R.id.tasks_view);
        //圆形重试控件
        circleButton = (CircleButton) findViewById(R.id.buttonAgain);
        //圆形控件添加监听
        circleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //调用重新初始化方法
                restartInitialize();
            }
        });
        //节点进度
        ssl = (NodeProgress) findViewById(R.id.ssl);
        //设置初始值为1
        ssl.setProgressByNode(1);
        //文字提示信息进入动画方法调用
        reminderStart("正在初始化,大约需要几分钟的时间,请勿断开.", -16777216);
        //进入界面时候圆形左侧滑动进入
        cricleProgressAnimInit();
        //开始初始化第一步，调用获取后台数据方法
        getWebData();
    }

    /**
     * @functon:从后台获取配置信息
     * @author:Created by 徐建强 on 2015/9/23.
     * @return:null
     */
    private void getWebData() {
        //调用静态SharedPreferences获取cookie值
        MethodTools.sPreFerCookie = getSharedPreferences("Cookies",
                Context.MODE_PRIVATE);
        //发起带cookie的请求
        VolleyJsonRequest.JsonRequestWithCookie(InitializeGateway.this,
                Path.host + Path.linkCheck, null,
                MethodTools.sPreFerCookie.getString("cookie", "null"));
        //重写Handler中的message方法，获取数据后进行异步加载
        MethodTools.HnadlerVOlleyJson = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == Code.SUCCESS) {
                    analysis(msg.obj.toString());
                } else if (msg.what == Code.FAILURE) {
                    analysis(null);
                    MyToast.makeTextToast(InitializeGateway.this,
                            "请求失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
                }
            }
        };
    }

    /**
     * @param webdata
     * @function:分析从web获取的数据
     * @author:Created by 徐建强 on 2015/10/21.
     * @return:null
     */
    private void analysis(String webdata) {
        //节点进度更新到第二步
        ssl.setProgressByNode(2);
        //圆形进度条信息进度最大值更新到25%
        maxProgress = 25;
        //开始初始化第二步，既是扫描当前网关wifi
        getWifiPermission();
    }

    /**
     * @function android6.0以上wifi获取权限
     */
    public void getWifiPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 获取wifi连接需要定位权限,没有获取权限
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_WIFI_STATE,
                }, WIFI_SCAN_PERMISSION_CODE);
                return;
            }
        } else {
            new AsyncTask<Integer, Integer, Integer>() {

                @Override
                protected Integer doInBackground(Integer... params) {
                    getWifi();
                    return null;
                }

            }.execute();
//            new Thread() {
//                @Override
//                public void run() {
//                    super.run();
//                    getWifi();
//                }
//            }.start();
        }
    }

    /**
     * 获取wifi权限
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WIFI_SCAN_PERMISSION_CODE:
                Log.e(TAG, "grantResults.length:" + grantResults.length + "grantResults[0]:" + grantResults[0]);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 允许
                    getWifi();
                } else {
                    // 不允许
                    Log.e(TAG, "grantResults:" + grantResults[0]);
                    Toast.makeText(this, "请允许wifi开启", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * @function:加载当前初始化的实时进度值显示在圆形进度条中
     * @author:Created by 徐建强 on 2015/10/21.
     * @return:null
     */
    private void initProgress() {
        //线程开始对进度计量进行校准,这样写是为了防止重复操作导致创建多余的废用线程，会使内存负担加大
        r = new Runnable() {
            @Override
            public void run() {
                wheelRunning = true;
                //用两个while循环，第一个是让第二个while一直进行
                while (wheelRunning == true) {
                    //第二个是为了当最大值变更时就重新更新进度
                    while (wheelProgress < maxProgress) {
                        //如果当前进度值和最大进度值相等，说明已经完成，此时让循环停止，不然无休止的循环下去浪费内存和CPU
                        if (wheelProgress == maxProgress) {
                            //当加载到当前最大值时，跳出循环
                            wheelRunning = false;
                        }
                        //每次循环进度值加一
                        wheelProgress++;
                        //设置当前进度值到圆形进度条中
                        progressBar.setProgressNotInUiThread(wheelProgress);
                        try {
                            //线程睡眠80毫秒
                            Thread.sleep(80);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Log.i(TAG, "Thread sleep Exception");
                        }
                    }
                }
            }
        };
    }

    /**
     * @function:扫瞄当前附近wifi并 查找是否存在网关wifi
     * @author:Created by 徐建强 on 2015/10/21.
     * @return:null
     */
    private void getWifi() {
        //将扫描到的网关赋给一个字符串
        String ssids = mWifiAdmin.getScanResult();
        //切割扫描到的wifi字符串
        String[] SSID = ssids.split("/");
        //利用for循环循环遍历数组，检索是否附近有网关wifi
        boolean isFindWifi = false;
        for (int i = 0; i < SSID.length; i++) {
            //如果检测到有网关wifi存在，开始进行初始化第三步
            if (SSID[i].endsWith("shannondatagateway")) {
                //扫描到网关wifi后，进行初始化第三步，开始连接网，同时，也结束循环查找
                setGatewayWifi();
                return;
            }
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getWifi();
    }

    /**
     * @function:连接网关wifi
     * @author:Created by 徐建强 on 2015/10/21.
     * @return:null
     */
    private void setGatewayWifi() {
        mWifiAdmin.disconnectWifi();
        //开始连接网关wifi
        mWifiAdmin.Connect("shannondatagateway", "shannon01", WifiAdmin.WifiCipherType.WIFICIPHER_WPA);

        //满足循环进入条件，既是设置监测标识为true
        checkRuning = true;
        //利用子线程进入一个循环，实时监测是否连接网关wifi成功
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (checkRuning) {
                    timeWifiInfo++;
                    //实时判断连接的网关wifi是否联通
                    if (NetworksInfo.checkNet(InitializeGateway.this)) {
                        //成功，交给Handler去处理UI
                        Message msg = new Message();
                        msg.what = 1;
                        nhandler.sendMessage(msg);
                        //改变标识，并跳出循环
                        checkRuning = false;
                        break;
                    } else if (timeWifiInfo > timeMax) {
                        //失败，交给Handler去处理UI
                        Message msg = new Message();
                        msg.what = 0;
                        nhandler.sendMessage(msg);
                        //改变标识，并跳出循环
                        checkRuning = false;
                        break;
                    }
                    //让线程每次循环睡眠1500毫秒，防止wifi连接所用时间过长以至于错过预定时间
                    try {
                        Thread.sleep(1500);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i(TAG, "Thread sleep Exception");
                    }
                }
            }
        });
        //开启线程
        thread.start();
    }

    /**
     * @return null
     * @function:启动一个Handler来随时接收子线程中的UI处理信息指令
     * @author kys_26使用者：徐建强 2015-10-20
     */
    private void myHandlerUI() {
        nhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        //如果传入值是0，说明连接网关wifi超时
                        initializeError("连接网关WiFi超时.");
                        break;
                    case 1:
                        //更新节点进度值到第三步，圆形进度条的进度值添加到50%
                        ssl.setProgressByNode(3);
                        maxProgress = 50;
                        try {
                            Thread.currentThread().sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //开始初始化第四步,向链接的网关发送指配置信息.
                        startSocket();
                        break;
                    case 2:
                        //开始初始化第五步，已经接近尾声
                        initializeGatewayOver("配置完成,正在准备跳转.", true);
                        break;
                    case 3:
                        //开始初始化第五步，已经接近尾声
                        initializeGatewayOver("网络故障或者硬件初始化失败", false);
                        break;
                    case 4:
                        //开始验证网关
                        startValidateGate();
                        break;
                    case 5:
                        //每次验证网关
                        doRegist(Path.host + Path.URL_G_A_D, null, GET_GATEWAYSTATE);
                        break;
                    case 6:
                        //三分钟之后才去验证
                        delayValidateGate();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    /**
     * 三分钟之后才去验证
     */
    private void delayValidateGate() {
        MethodTools.handlerJson = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case GET_GATEWAYSTATE:
                        if (validateIndex == 3) {
                            validateService.shutdown();
                            validateIndex = 0;
                            Message validateMsg = new Message();
                            validateMsg.what = 3;
                            nhandler.sendMessage(validateMsg);
                            Log.e(TAG, "初始化失败");
                        } else {
                            validateIndex++;
                            Toast.makeText(getApplicationContext(), "第" + validateIndex + "次验证 ValidateIndex:" + validateIndex, Toast.LENGTH_LONG).show();
                            Log.e(TAG, "第" + validateIndex + "次验证 ValidateIndex:" + validateIndex);
                        }
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(msg.obj.toString());
                            MethodTools.mGwAndDeviceDates = new ArrayList<>();
                            List<GwAndDeviceDate> mGWStateList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                MethodTools.mGwAndDeviceDates.add(GwAndDeviceDate.analysis(jsonArray.getJSONObject(i)));
                                if ("00-00-00-00".equals(GwAndDeviceDate.analysis(jsonArray.getJSONObject(i)).getNodeid())) {
                                    mGWStateList.add(GwAndDeviceDate.analysis(jsonArray.getJSONObject(i)));
                                }
                            }
                            for (int i = 0; i < mGWStateList.size(); i++) {
                                String gwid = mGWStateList.get(i).getGwid();
                                gwid = gwid.replaceAll("-", "");
                                if (Integer.parseInt(gwid, 16) == Integer.valueOf(mGatewayIdBean.getGwId())) {
                                    if (mGWStateList.get(i).getKid_stat() == 85) {
                                        validateService.shutdown();
                                        validateIndex = 0;
                                        Message validateMsg = new Message();
                                        validateMsg.what = 2;
                                        nhandler.sendMessage(validateMsg);
                                        Log.e(TAG, "初始化成功");
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
        Runnable runnable = new Runnable() {
            public void run() {
                // task to run goes here
                Message validateMsg = Message.obtain();
                validateMsg.what = 5;
                nhandler.sendMessage(validateMsg);
            }
        };
        //开关的线程池
        validateService = Executors
                .newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        validateService.scheduleAtFixedRate(runnable, 0, 30, TimeUnit.SECONDS);
    }

    /**
     * @param reminder
     * @return null
     * @function:初始化中发生错误时跳入此方法处理
     * @author kys_26使用者：徐建强 2015-9-1
     */
    private void initializeError(String reminder) {
        //初始化出现错误，执行动画中圆形重试控件左侧划出
        setAnim(0);
        //使等待进度效果图处于暂停
        prgressBg.setBackgroundResource(R.drawable.img00000);
        //提示信息显示错误信息
        reminderStart(reminder, -65536);
    }

    /**
     * @return null
     * @function:重新开始初始化方法
     * @author kys_26使用者：徐建强 2015-10-22
     */
    private void restartInitialize() {
        //点击重试后跳出进度条
        setAnim(1);
        //重试时进度恢复为零
        initProgress();
        //重试点击后动态进度指示图转动
        setBackgroundResource();
        //节点进度恢复到零，既是重新开始
        ssl.setProgressByNode(1);
        //使当前进度值为0，既是重新开始
        wheelProgress = 0;
        //是当前进度最大值为2，意味重新开始
        maxProgress = 2;
        //设置当前圆形进度条为0，既是重新开始
        progressBar.setProgressNotInUiThread(wheelProgress);
        //更新提示信息
        reminderStart("正在初始化网关，请勿关闭程序.", -65536);
        //重新调用初始化方法
        getWebData();
    }

    /**
     * @return null
     * @function:利用socket类向网关发送数据
     * @author kys_26使用者：徐建强 2015-9-1
     */
    public void startSocket() {
        //得到可用选择wifi的ssid和密码
        sharedPreferences = getSharedPreferences("wifi", Context.MODE_PRIVATE);
        //得到后台返回网关的Ip地址和端口
        //调用静态SharedPreferences获取cookie值
        MethodTools.sPreFerCookie = getSharedPreferences("IpAndPort",
                Context.MODE_PRIVATE);
        //拼接配置信息
        data = Code.head + sharedPreferences.getString("SSID", "ssid is null")
                + Code.middle + sharedPreferences.getString("Password", "password is null")
                + Code.tail + MethodTools.sPreFerCookie.getString("ip", "ip is null")
                + Code.portBefor + MethodTools.sPreFerCookie.getString("port", "port is null")
                + Code.last;
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        Log.e(TAG, "12345socket" + data + "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        //进行初始化第四步，开始向网关发送配置信息
        socketThread = new SocketNetwork(mhandler, data, InitializeGateway.this);
        socketThread.conn();
    }

    /**
     * @return null
     * @function:利用Handler时刻等待网关配置的返回结果
     * @author kys_26使用者：徐建强 2015-10-22
     */
    private void myHandlerSocket() {
        mhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        //初始化第四步中网关无响应
                        initializeError("没有可用的网关连接.");
                        //关闭socket和输入输出流
                        socketThread.close();
                        break;
                    case 1:
                        //初始化第四步完成，更新最大进度为75%以及节点进度为第四节
                        maxProgress = 75;
                        ssl.setProgressByNode(4);
                        //关闭socket和输入输出流
                        socketThread.close();
                        //并开始初始化第五步
                        initializeGatewaySuccess();
                        break;
                    case 2:
                        //初始化第四步中网关中没有可用连接
                        initializeError("网关无响应.请重试");
                        break;
                    default:
                        break;
                }
            }
        };
    }

    /**
     * @return null
     * @function:网关配置成功
     * @author kys_26使用者：徐建强 2015-9-1
     */
    private void initializeGatewaySuccess() {
        //得到可用选择wifi的ssid和密码
        sharedPreferences = getSharedPreferences("wifi", Context.MODE_PRIVATE);
        //恢复用户选择的wifi连接
        mWifiAdmin.Connect(sharedPreferences.getString("SSID", "ssid is null"), sharedPreferences.getString("Password", "password is null"), WifiAdmin.WifiCipherType.WIFICIPHER_WPA);
        //设置循环进入条件为允许
        isCheckRuning = true;
        //利用子线程进入一个循环，实时监测是否连接网关wifi成功
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isCheckRuning) {
                    timeWifiActive++;
                    //实时判断连接的网关wifi是否联通
                    if (NetworksInfo.checkNet(InitializeGateway.this)) {
                        //成功，交给Handler去处理UI
                        Message msg = new Message();
                        msg.what = 4;
                        nhandler.sendMessage(msg);
                        //改变标识，并跳出循环
                        isCheckRuning = false;
                        break;
                    } else if (timeWifiInfo > timeMax) {
                        //失败，交给Handler去处理UI
                        Message msg = new Message();
                        msg.what = 3;
                        nhandler.sendMessage(msg);
                        //改变标识，并跳出循环
                        isCheckRuning = false;
                        break;
                    }
                    //让线程每次循环睡眠1500毫秒，防止wifi连接所用时间过长以至于错过预定时间
                    try {
                        Thread.sleep(1500);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i(TAG, "Thread sleep Exception of WiFiBack");
                    }
                }
            }
        });
        //开启线程
        thread.start();
    }

    /**
     * 验证网关是否成功初始化
     */
    private void startValidateGate() {
        Message delayMsg = new Message();
        delayMsg.what = 6;
        nhandler.sendMessageDelayed(delayMsg, 3 * 60 * 1000);
    }

    /**
     * @return null
     * @function:网关配置第五步结束 处理一些UI和跳转
     * @author kys_26使用者：徐建强 2015-10-22
     */
    private void initializeGatewayOver(String reminString, final boolean isSuccess) {
        //更新初始化进度值到第五步，节点进度条更新到第五节
        maxProgress = 100;
        ssl.setProgressByNode(5);
        //文本提示信息
        textRemin.setText(reminString);
        //初始化正常则显示黑色
        textRemin.setTextColor(Color.BLACK);
        //设置两秒后跳转
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //所有初始化步骤完成，动态等待图设为停止
                prgressBg.setBackgroundResource(R.drawable.img00000);
                //开始跳转
                if (isSuccess)
                    Toast.makeText(getApplicationContext(), "添加养殖场成功", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "初始化失败，请重新初始化", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(InitializeGateway.this, MainActivity.class));
                InitializeGateway.this.finish();
            }
        }, 1000);
    }

    /**
     * @return null
     * @function:setBackgroundResource配置动态背景 OnPreDrawListener实例化动画监听
     * @author kys_26使用者：徐建强 2015-9-1
     */
    private void setBackgroundResource() {
        // TODO Auto-generated method stub
        prgressBg.setBackgroundResource(R.drawable.animtion_progress_cricle);
        animationCriclProgress = (AnimationDrawable) prgressBg.getBackground();
        prgressBg.post(new Runnable() {
            @Override
            public void run() {
                animationCriclProgress.start();
            }
        });
    }

    /**
     * @return null
     * @function:隐藏控件退出动画
     * @author kys_26使用者：徐建强 2015-9-1
     */
    private void setAnim(int i) {
        // TODO Auto-generated method stub
        if (i == 0) {
            //重试控件添加进入效果
            circleButton.setVisibility(View.VISIBLE);
            alphaAnimationCrlcleButton = new TranslateAnimation(1000f, 0f, 0f, 0f);
            alphaAnimationCrlcleButton.setDuration(1000);
            circleButton.setAnimation(alphaAnimationCrlcleButton);
            //圆形进度添加退出滑动效果
            alphaAnimationCrlcleProgress = new TranslateAnimation(0f, -1000f, 0f, 0f);
            alphaAnimationCrlcleProgress.setDuration(1000);
            progressBar.setAnimation(alphaAnimationCrlcleProgress);
            //调用setPostDely方法，让进度圈影藏
            setPostDely(0);
        } else if (i == 1) {
            // 圆形进度添加进入滑动效果
            progressBar.setVisibility(View.VISIBLE);
            alphaAnimationCrlcleProgress = new TranslateAnimation(1000f, 0f, 0f, 0f);
            alphaAnimationCrlcleProgress.setDuration(1000);
            progressBar.setAnimation(alphaAnimationCrlcleProgress);
            //重试控件添加退出效果
            alphaAnimationCrlcleButton = new TranslateAnimation(0f, -1000f, 0f, 0f);
            alphaAnimationCrlcleButton.setDuration(1000);
            circleButton.setAnimation(alphaAnimationCrlcleButton);
            //调用setPostDely方法，让重试控件影藏
            setPostDely(1);
        }
    }

    /**
     * @return null
     * @function:加载UI的时候 , 圆形进入条设置一个动画
     * @author kys_26使用者：徐建强 2015-10-22
     */
    private void cricleProgressAnimInit() {
        //设置自定义重试控件为暂时不可见
        circleButton.setVisibility(View.INVISIBLE);
        // 圆形进度添加进入滑动效果
        progressBar.setVisibility(View.VISIBLE);
        alphaAnimationCrlcleProgress = new TranslateAnimation(1000f, 0f, 0f, 0f);
        alphaAnimationCrlcleProgress.setDuration(1000);
        progressBar.setAnimation(alphaAnimationCrlcleProgress);
    }

    /**
     * @param i
     * @return null
     * @function:处理推迟一秒处理的事件 传入int型的参数来判断是要做哪种处理
     * @author kys_26使用者：徐建强 2015-10-22
     */
    private void setPostDely(int i) {
        // TODO Auto-generated method stub
        final int what = i;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (what) {
                    case 0:
                        //设置为圆形进度条不可见
                        progressBar.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        //设置为圆形控件不可见
                        circleButton.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        break;
                }
            }
        }, 1000);
    }

    /**
     * @param text,color
     * @return null
     * @function:提示信息设置一个动画进入和退出 这里需要传入连个值，一个是要显示的内容，另一个是文字字体的颜色
     * @author kys_26使用者：徐建强 2015-9-1
     */
    private void reminderStart(String text, int color) {
        //设置提示信息
        textRemin.setText(text);
        //设置提示为醒目的红色
        textRemin.setTextColor(color);
        //设置动画路径方向
        reminderAnimation = new TranslateAnimation(-1000f, 0f, 0f, 0f);
        //设置动画执行时间
        reminderAnimation.setDuration(1000);
        //为控件装载动画
        textRemin.setAnimation(reminderAnimation);
    }

    /**
     * @param event
     * @param KeyCode
     * @return null
     * @function:exit remid
     * @author kys_26使用者：徐建强 2015-9-1
     */
    public boolean onKeyDown(int KeyCode, KeyEvent event) {
        switch (KeyCode) {
            case KeyEvent.KEYCODE_BACK:
                Toast.makeText(InitializeGateway.this, R.string.string_activity_IniaProgress_brokeInitialize, Toast.LENGTH_LONG).show();
                InitializeGateway.this.finish();
                wheelRunning = false;
                return true;
            default:
                break;
        }
        return super.onKeyDown(KeyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (validateService != null)
            validateService.shutdownNow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nhandler.removeCallbacksAndMessages(null);
    }
}
