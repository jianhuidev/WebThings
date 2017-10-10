package com.kys26.webthings.main;

import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kys26.webthings.fragment.FarmFrgment;
import com.kys26.webthings.fragment.GWFragment;
import com.kys26.webthings.fragment.PersonalFragment;
import com.kys26.webthings.history.HistoryActivity;
import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.httpnetworks.MyImageRequest;
import com.kys26.webthings.httpnetworks.VolleyStringRequest;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.model.ClientInforData;
import com.kys26.webthings.model.FarmData;
import com.kys26.webthings.model.GwAndDeviceDate;
import com.kys26.webthings.model.NodeControlData;
import com.kys26.webthings.model.NodeDeviceData;
import com.kys26.webthings.model.NodeVideoData;
import com.kys26.webthings.personalcenter.Data;
import com.kys26.webthings.personalcenter.NoticeActivity;
import com.kys26.webthings.util.MyToast;
import com.kys26.webthings.util.SharedPreferencesUtils;
import com.kys26.webthings.video.RtmpActivity;
import com.kys26.webthings.video.VideoActivity;
import com.kys26.webthings.wifi.SettingWifi;
import com.lidroid.xutils.ViewUtils;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import static com.kys26.webthings.httpconstant.Code.COOLING_NODE_NAME;


/**
 * @author:Created by kys_26:徐建强 on 2015/11/5.
 * @function:MainActivyty , the main tab
 */
public class MainActivity extends BaseActivity {
    @InjectView(R.id.fragment)
    FrameLayout fragment;
    @InjectView(R.id.btn_bottom_left)
    RelativeLayout mBtnBottomLeft;
    @InjectView(R.id.btn_bottom_middle)
    RelativeLayout mBtnBottomMiddle;
    @InjectView(R.id.btn_bottom_right)
    RelativeLayout mBtnBottomRight;
    @InjectView(R.id.background)
    LinearLayout mBackground;
    /**
     * 输出标识
     */
    private String TAG = "MainActivity";

    /**
     * 双击退出
     */
    private long clickTime = 0;
    /**
     * fragment实例，用来标记当前显示的是哪一个fragment
     */
    private Fragment mFragment;
    /**
     * 农场选择
     */
    public FarmFrgment mFarmFrgment;
    /**
     * 个人设置
     */
    public PersonalFragment mPersonalFragment;
    /**
     * 农场内部
     */
    public GWFragment mGwfragment;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    /**
     * 第几个农场
     */
    public int position = 0;
    private String leftTitle = "养殖场选择";
    private String middleTitle = "";
    private String rightTitle = "个人中心";
    private ScheduledExecutorService HeartService;
    /**
     * 设置标记，用于判断当前是哪个Frgment
     */
    private String mark = "mFarmFrgment";
    private int gatewayPosition = -1;

    public int gwState = 0;
    private String firstName = "0";
    private boolean isOpen = false;

    /**
     * 节点是否有数据
     */
    public boolean video = false;
    public boolean wind = false;
    public boolean cool = false;
    public boolean feed = false;
    public boolean clear = false;
    public boolean light = false;
    public boolean heat = false;
    public boolean hum = false;
    public boolean fillLight = false;
    public boolean sterilization = false;


    private MyImageRequest myImageRequest;

    //private String token3 = "vbU4RxpGg3aRcB9vzlvx6+kb3TX4d55V/0ub3soTmbWxHcXoyd8P9yzUV6Gadyy7qwhQ7ooE80ubppMZIy+fxg==";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**链接融云*/
        connect();
        Log.e(TAG, "saveInstanceState:" + savedInstanceState);
//        setContentView(R.layout.activity_main_new);
        ButterKnife.inject(this);
        // 初始化IOC注解
        ViewUtils.inject(this);
        //初始化UI组件
        if (savedInstanceState != null) {  // “内存重启”时调用
            boolean isSupportHidden = savedInstanceState.getBoolean("STATE_SAVE_IS_HIDDEN");
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(mFragment);
            } else {
                ft.show(mFragment);
            }
            ft.commit();
        }
        initView();
    }

    /**
     * 融云连接部分
     */
    private void connect() {
        /**
         * 融云的链接设置
         * */
        RongIM.connect(Data.token, new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String userId) {
                Log.e("RongSuccess", "    链接成功了" + userId);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
            }

            @Override
            public void onTokenIncorrect() {
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main_new;
    }

    private void initHandler() {
        //重写Handler中的message方法，获取数据后进行异步加载
        MethodTools.handlerJson = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == GET_FARM) {
                    try {
                        JSONArray jsonArray = new JSONArray(msg.obj.toString());
                        //Log.e("qingqiu",msg.obj.toString());
                        MethodTools.farmDataList = new ArrayList<>();
                        /**添加农场数据*/
                        for (int i = 0; i < jsonArray.length(); i++) {
                            if (!("未绑定".equals(jsonArray.getJSONObject(i).get("gw_Id")))) {
                                MethodTools.farmDataList.add(FarmData.analysis(jsonArray.getJSONObject(i)));
                            }
                        }
                        /**验证网关状态*/
                        //getGwState();
                        getGwAndDeviceDate();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == GET_GATEWAYSTATE) {
                    Log.e("get_gateWayState", msg.obj.toString());
                    try {
                        JSONArray jsonArray = new JSONArray(msg.obj.toString());
                        MethodTools.mGwAndDeviceDates = new ArrayList<>();
                        MethodTools.mGWStateList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            MethodTools.mGwAndDeviceDates.add(GwAndDeviceDate.analysis(jsonArray.getJSONObject(i)));
                            if ("00-00-00-00".equals(GwAndDeviceDate.analysis(jsonArray.getJSONObject(i)).getNodeid())) {
                                MethodTools.mGWStateList.add(GwAndDeviceDate.analysis(jsonArray.getJSONObject(i)));
                            }
                        }
                        /**通知fragment刷新页面*/
                        mFarmFrgment.changeUI(MainActivity.this);
                        for (int i = 0; i < MethodTools.mGWStateList.size(); i++) {
                            if (MethodTools.mGWStateList.get(i).getKid_stat() == 85) {
                                firstName = MethodTools.mGWStateList.get(i).getFarmname();
                                position = i;
                                break;
                            }
                        }
                        getRed5Ip();
                        isOpen = true;//只有请求到了，才能开启养殖场GWFragment
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == NODE_STATE) {
                    try {
                        List<Integer> ndNameList = new ArrayList<>();
                        JSONArray jsonArray = new JSONArray(msg.obj.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String gwName = MethodTools.farmDataList.get(position).getFarm_name();
                            String nodeName = GwAndDeviceDate.analysis(jsonArray.getJSONObject(i)).getFarmname();
                            if ((gwName).equals(nodeName)) {

                                /**把相应名称下的ndName 存一下*/
                                ndNameList.add(GwAndDeviceDate.analysis(jsonArray.getJSONObject(i)).getNdname());
                            }
                        }
                        if (ndNameList.contains(1))
                            wind = true;
                        else
                            wind = false;
                        if (ndNameList.contains(2))
                            cool = true;
                        else
                            cool = false;
                        if (ndNameList.contains(3))
                            feed = true;
                        else
                            feed = false;
                        if (ndNameList.contains(4))
                            clear = true;
                        else
                            clear = false;
                        if (ndNameList.contains(5))
                            light = true;
                        else
                            light = false;
                        if (ndNameList.contains(6))
                            heat = true;
                        else
                            heat = false;
                        if (ndNameList.contains(7))
                            hum = true;
                        else
                            hum = false;
                        if (ndNameList.contains(8))
                            fillLight = true;
                        else
                            fillLight = false;
                        if (ndNameList.contains(9))
                            sterilization = true;
                        else
                            sterilization = false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == CLIENT) {
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        MethodTools.mClientInforData = new ArrayList<>();
                        MethodTools.mClientInforData.add(ClientInforData.analysis(jsonObject));

                        //imageRequest.getImage(bitmap,MethodTools.mClientInforData.get(0).getAvatarUrl());
                        if (mPersonalFragment != null) {
                            myImageRequest.getHeadImage(mPersonalFragment.head_portrait, MethodTools.mClientInforData.get(0).getAvatarUrl());
                            mPersonalFragment.name.setText(MethodTools.mClientInforData.get(0).getTrueName());
                        }
                        Log.e("CLIENT11*", msg.obj.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == UPDATA_CLIENT) {
                    getClientInfor();
                    Log.e("UPDATA_CLIENT", msg.obj.toString());

                } else if (msg.what == UPDATE_FARM) {
                    doRegist(Path.host + Path.URL_MANAGE_FARM, null, MainActivity.GET_FARM);
                } else if (msg.what == GET_DEVICE) {
                    try {
                        //Log.e("GET_DEVICE",msg.obj.toString());
                        JSONObject jsonobject = new JSONObject(msg.obj.toString());
                        JSONArray jsonArray = jsonobject.getJSONArray("list");
                        Log.e("jsonArray", jsonArray.toString());
                        List<NodeDeviceData> list = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            list.add(NodeDeviceData.analysis(jsonArray.getJSONObject(i)));
                        }
                        MethodTools.farmDataList.get(position).setNodeDeviceList(list);
                        SharedPreferencesUtils.saveObject(getApplicationContext(),
                                "farmList", MethodTools.farmDataList);
                        SharedPreferencesUtils.saveObject(getApplicationContext(), "position", position);
                        if (mGwfragment != null) {
                            mGwfragment.changeUI(MainActivity.this);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == Code.FAILURE) {
                    //消除等待进度弹出框
                    MyToast.makeImgAndTextToast(MainActivity.this, getResources().getDrawable(R.drawable.tips_sry),
                            "请求失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
                    MethodTools.state = false;
                } else if (msg.what == EXIT) {
                    finish();
                }
                MainActivity.this.dismissProgress();
                //如果下拉刷新显示，则弹回
                if (getRefreshView().isRefreshing()) {
                    getRefreshView().setRefreshing(false);
                }
            }
        };
        HeartDataTask();

    }

    private void initVideoHandler() {
        MethodTools.HnadlerVOlleyJson = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == Code.SUCCESS) {
                    try {/**监控Red5 处理*/
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        List<NodeVideoData> list = new ArrayList<>();
                        list.add(NodeVideoData.analysis(jsonObject));
                        MethodTools.farmDataList.get(position).setVideoNodeList(list);
                        //MethodTools.farmDataList.get(position).getVideoNodeList().get(0).setIp(jsonObject.getString("ip"));
                        MethodTools.farmDataList.get(position).getVideoNodeList().get(0)
                                .setVideoNodeId((Integer) jsonArray.getJSONObject(0).get("videoNodeId"));
                        MethodTools.farmDataList.get(position).getVideoNodeList().get(0)
                                .setVideoNodeName((String) jsonArray.getJSONObject(0).get("videoNodeName"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == VIDEO_CHANNEL) {
                    try {/**监控channel 处理*/
                        Log.e("VIDEO_CHANNEL", msg.obj.toString());
                        JSONArray jsonArray = new JSONArray(msg.obj.toString());
                        Log.e("VIDEO_CHANNEL", msg.obj.toString());
                        if (jsonArray.length() > 0) {
                            video = true;
                        } else {
                            video = false;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == NODE_LIST) {
                    try {/**风机处理*/
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        JSONArray jsonArray = jsonObject.getJSONArray("gDevice");

                        if (jsonArray.length() > 0) {
                            wind = true;
                        } else {
                            wind = false;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == GET_COOLING_NODE) {
                    try {/**降温处理*/
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        JSONArray jsonArray = jsonObject.getJSONArray("gDevice");
                        List<NodeControlData> coolingList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            if (object.get("ndname").toString().equals(COOLING_NODE_NAME))
                                coolingList.add(NodeControlData.analysis(object));
                        }
                        if (coolingList.size() > 0) {
                            cool = true;
                        } else {
                            cool = false;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == Code.FAILURE) {
                    //Toast.makeText(MainActivity.this, "请求失败1", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        getClientInfor();
        if (Data.flag != 0) {//进行判断，从通知那，才通过
            Intent intent = getIntent();
            position = intent.getIntExtra("farmPosition", position);
            ChangeToMiddle(position);
            Data.flag = 0;
        }
        super.onResume();
        initHandler();
        initVideoHandler();
    }

    /**
     * 获取网关状态
     */
    public void getGwAndDeviceDate() {
        doRegist(Path.host + Path.URL_G_A_D, null, MainActivity.GET_GATEWAYSTATE);
    }

    public void RequestNodeState(int farmPosition) {
        doRegist(Path.host + Path.URL_G_A_D, null, MainActivity.NODE_STATE);
    }

    /**
     * 请求web 数据，获取相应节点的数据
     */
    public void RequestWebData(int farmPosition) {
        /**监控请求*/
        if (MethodTools.farmDataList.get(farmPosition).getVideoNodeList().size() > 0) {
            VolleyStringRequest.stringRequest(MainActivity.this, Path.videoHost + Path.URL_GET_CHANNEL + "?videoNodeId="
                    + MethodTools.farmDataList.get(farmPosition).getVideoNodeList().get(0).getVideoNodeId(), VIDEO_CHANNEL);
        }

//        JSONObject jb = new JSONObject();
//        Log.e("MainActivity", "***" + farmPosition);
//        try {
//            /**监控请求*/
//            if (MethodTools.farmDataList.get(farmPosition).getVideoNodeList().size()>0){
//            VolleyStringRequest.stringRequest(MainActivity.this, Path.videoHost + Path.URL_GET_CHANNEL + "?videoNodeId="
//                    + MethodTools.farmDataList.get(farmPosition).getVideoNodeList().get(0).getVideoNodeId(),VIDEO_CHANNEL);
//            }
//            /**风机请求*/
//            jb.put("farmid", MethodTools.farmDataList.get(farmPosition).getFarm_id());
//            jb.put("ndname", StringUtil.setNodeType(NodeType.WIND));
//            doVolleyRegist(Path.host + Path.URL_GET_Control, this,jb,NODE_LIST);
//            /**降温请求*/
//            jb.put("ndname", StringUtil.setNodeType(NodeType.COOLING));
//            doVolleyRegist(Path.host + Path.URL_GET_COOLING, this,jb,GET_COOLING_NODE);
//
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onClickLeft() {
        super.onClickLeft();
        if (mark.equals("mFarmFrgment")) {//显示mFarmFrgment 的情况
            Intent intent = new Intent(MainActivity.this, SettingWifi.class);
            startActivity(intent);
        } else if (mark.equals("mGwfragment")) {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            intent.putExtra("gwid", MethodTools.farmDataList.get(position).getgw_Id());
            intent.putExtra("position", position);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, NoticeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onClickRight() {
        super.onClickRight();
        //根据当前实例判断是当前界面哪一个fragment
        if (mFragment instanceof FarmFrgment) {
            startActivity(new Intent(this, VideoActivity.class));
        } else if (mFragment instanceof PersonalFragment) {
            Toast.makeText(getApplicationContext(), "个人设置哪里", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @author:Created by kys_26:徐建强 on 2015/11/5.
     * @function:初始化UI组件
     */
    private void initView() {
        setLeftBtnVisible(true);
        //setRightImg(R.drawable.video_right);
        setRightBtnVisible(false);
        setLeft_img(R.drawable.top_left_btn_bg);
        setTitle("养殖场选择");
        setSelector(mBtnBottomLeft);
        mFragmentManager = getFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        if (mFarmFrgment == null) {
            mFarmFrgment = new FarmFrgment();
            mFragmentTransaction.add(R.id.fragment, mFarmFrgment);
            mFragment = mFarmFrgment;
        } else {
            mFragmentTransaction.show(mFarmFrgment);
            mFragmentTransaction.hide(mFragment);
            mFragment = mFarmFrgment;
        }
        mFragmentTransaction.commit();
        setEnableRefresh(true, mFarmFrgment.refreshListener);
        myImageRequest = new MyImageRequest();
    }


    /**
     * 返回键监听
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * @function 退出事件
     */
    private void exit() {
        if ((System.currentTimeMillis() - clickTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次后退键退出程序",
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, "再按一次后退键退出程序" + System.currentTimeMillis());
            clickTime = System.currentTimeMillis();
        } else {
            Log.e(TAG, "exit application");
            int currentVersion = Build.VERSION.SDK_INT;
            if (currentVersion > Build.VERSION_CODES.ECLAIR_MR1) {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                System.exit(0);
            } else {// android2.1
                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                am.restartPackage(getPackageName());
            }
        }
    }

    /**
     * 用来判断服务是否运行.
     *
     * @param mContext
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    @OnClick({R.id.btn_bottom_left, R.id.btn_bottom_middle, R.id.btn_bottom_right})
    public void onClick(View view) {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        switch (view.getId()) {
            case R.id.btn_bottom_left:
                //Log.e("farmfragment","xxx");
                mark = "mFarmFrgment";
                setLeft_img(R.drawable.top_left_btn_bg);
                //setRightImg(R.drawable.video_right);
                setTitle(leftTitle);
                setSelector(view);
                getLeft_btn().setSelected(false);
                setLeftBtnVisible(true);
                setRightBtnVisible(false);
                mFragmentTransaction.hide(mFragment);
                if (mFarmFrgment == null) {
                    mFarmFrgment = new FarmFrgment();
                    mFragmentTransaction.add(R.id.fragment, mFarmFrgment);
                    mFragment = mFarmFrgment;
                } else {
                    mFragmentTransaction.show(mFarmFrgment);
                    mFragment = mFarmFrgment;
                }
                mFragmentTransaction.commit();
                setEnableRefresh(true, mFarmFrgment.refreshListener);
                getGwAndDeviceDate();
                break;
            case R.id.btn_bottom_middle:
                if (isOpen) {
                    for (int i = 0; i < MethodTools.mGWStateList.size(); i++) {
                        if (MethodTools.mGWStateList.get(i).getKid_stat() == 85) {
                            gwState = MethodTools.mGWStateList.get(i).getKid_stat();
                            //firstName = MethodTools.mGWStateList.get(i).getFarmname();
                            position = i;
                            break;
                        }
                    }
                    if (gwState != 85) {
                        Toast.makeText(this, "网关下线，关闭养殖场", Toast.LENGTH_SHORT).show();
                    } else {
//                        for (int i = 0; i < MethodTools.farmDataList.size(); i++) {
//                            if (firstName.equals(MethodTools.farmDataList.get(i).getFarm_name())) {
//                                position = i;
//                                break;
//                            }
//                        }
                        mark = "mGwfragment";
                        setLeft_img(R.drawable.top_left_btn_bg);
                        setSelector(view);
                        getLeft_btn().setSelected(true);
                        setLeftBtnVisible(true);
                        setRightBtnVisible(false);
                        mFragmentTransaction.hide(mFragment);
                        if (mGwfragment == null) {
                            mGwfragment = new GWFragment();
                            mFragmentTransaction.add(R.id.fragment, mGwfragment);
                            mFragment = mGwfragment;
                        } else {
                            mFragmentTransaction.show(mGwfragment);
                            mFragment = mGwfragment;
                        }
                        setEnableRefresh(true, mGwfragment.refreshListener);
                        mFragmentTransaction.commit();
                        SendWebData(position);
//                        getRed5Ip();
                        RequestWebData(position);
                        RequestNodeState(position);
                    }
                } else {
                    Toast.makeText(this, "网关正在初始化，关闭养殖场", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_bottom_right:
                mark = "mPersonalFragment";
                setLeft_img(R.drawable.topright_btn);
                //addRightWidget(2);
                setTitle(rightTitle);
                setSelector(view);
                setLeftBtnVisible(true);
                setRightBtnVisible(false);
                mFragmentTransaction.hide(mFragment);
                if (mPersonalFragment == null) {
                    mPersonalFragment = new PersonalFragment();
                    mFragmentTransaction.add(R.id.fragment, mPersonalFragment);
                    mFragment = mPersonalFragment;
                } else {
                    mFragmentTransaction.show(mPersonalFragment);
                    mFragment = mPersonalFragment;
                }
                mFragmentTransaction.commit();
                setEnableRefresh(false, null);
                break;
        }
    }

    private void setSelector(View view) {
        mBtnBottomLeft.setSelected(false);
        mBtnBottomMiddle.setSelected(false);
        mBtnBottomRight.setSelected(false);
        view.setSelected(true);
    }

    public void changeBackground(int degree) {
        switch (degree) {
            case 0:
                mBackground.setBackgroundResource(R.color.warning_1);
                break;
            case 1:
                mBackground.setBackgroundResource(R.color.warning_2);
                break;
            case 2:
                mBackground.setBackgroundResource(R.color.warning_3);
                break;
            case 3:
                mBackground.setBackgroundResource(R.color.warning_4);
                break;
        }
    }

//    @Override
//    protected void onDestroy() {
//        RongIM.getInstance().disconnect();
//        Log.e("disconnect","      调用disconnect");
//        super.onDestroy();
////        HeartService.shutdown();
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (HeartService != null)
//            HeartService.shutdown();
//        MethodTools.handlerJson.removeCallbacksAndMessages(null);
//    }

    @Override
    protected void onStop() {
        RongIM.getInstance().disconnect();
        super.onStop();
        if (HeartService != null) {
            HeartService.shutdown();
        }
        MethodTools.handlerJson.removeCallbacksAndMessages(null);
        //MethodTools.handlerJson=null;
    }

    /**
     * 心跳数据 1分钟一次
     */
    public void HeartDataTask() {
        final Handler HeartHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        getGwAndDeviceDate();
                        SendWebData(position);
                        break;
                }
            }
        };
        Runnable runnable = new Runnable() {
            public void run() {
                // task to run goes here
                Message msg = new Message();
                msg.what = 1;
                HeartHandler.sendMessage(msg);
            }
        };
        HeartService = Executors
                .newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        HeartService.scheduleAtFixedRate(runnable, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * 发送web请求
     */
    public void SendWebData(int farmPosition) {
        //Log.e("xxxsize", MethodTools.farmDataList.size() + "");
        if (MethodTools.farmDataList.size() > 0) {
            JSONObject jb = new JSONObject();
            try {
                jb.put("farmid", MethodTools.farmDataList.get(farmPosition).getFarm_id());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            doRegist(Path.host + Path.URL_GET_DEVICE, jb, MainActivity.GET_DEVICE);
        }
    }

    public void getClientInfor() {
        doRegist(Path.host + Path.URL_GET_CLIENT, null, CLIENT);
    }

    /**
     * 获取Vedio 的id
     */
    public void getRed5Ip() {
        VolleyStringRequest.stringRequest(this, Path.videoHost + Path.URL_GET_ARM, Code.SUCCESS);
    }

    /**
     * 改变界面到中间的fragment
     */
    public void ChangeToMiddle(int farmPosition) {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        setTitle(middleTitle);
        setSelector(findViewById(R.id.btn_bottom_middle));
        setLeft_img(R.drawable.top_left_btn_bg);
        getLeft_btn().setSelected(true);
        setLeftBtnVisible(true);
        mark = "mGwfragment";
        mFragmentTransaction.hide(mFragment);
        if (mGwfragment == null) {
            mGwfragment = new GWFragment();
            mFragmentTransaction.add(R.id.fragment, mGwfragment);
            mFragment = mGwfragment;
        } else {
            mFragmentTransaction.show(mGwfragment);
            mFragment = mGwfragment;
        }
        mFragmentTransaction.commit();
//        getRed5Ip();
        SendWebData(farmPosition);
    }
}
