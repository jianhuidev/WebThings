package com.kys26.webthings.wifi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kys26.webthings.adapter.WifiListAdapter;
import com.kys26.webthings.dialog.CustomDialog;
import com.kys26.webthings.main.BaseActivity;
import com.kys26.webthings.model.WifiBean;
import com.kys26.webthings.util.WifiUtils;
import com.zhangyx.MyGestureLock.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李赛鹏
 *         Created by kys_9 on 2017/3/9.
 */

public class WifiActivity extends BaseActivity {
    //显示wifi的list
    private ListView wifilist;
    //wifiUtils
    private WifiUtils mWifiUtils;
    //单个mScanResult对象(其中包含BSSID,SSID,level,capabilities四种需要的数据)
    private ScanResult mScanResult;
    //扫描出来的ScanResultList
    private List<ScanResult> mScanResultList;
    //wifibean
    private WifiBean mWifiBean;
    private static final String TAG = WifiActivity.class.getSimpleName();
    //wifilistAdapter
    private WifiListAdapter mWifiListAdapter;
    //下拉刷新
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //当前连接的wifi的密码
    private String Psw;
    private boolean isFirst = true;
    private CustomDialog mCustomDialog;
    private Handler refreshWifiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    getWifiPermission();
//                    getAllNetWorkList();
                    break;
                case 2:
                    getAllNetWorkList();
                    break;
                case 3://时延3S,确保wifi已连接
                    // 连接成功，刷新UI
                    mCustomDialog.DissMissDialog();
                    Toast.makeText(getApplicationContext(), "连接成功",
                            Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(WifiActivity.this,AddNewNodeActivity.class));
                    break;

            }
            super.handleMessage(msg);
        }
    };
    private List<WifiBean> wifiBeanList = new ArrayList<>();
    //当前wifi的id号
    private int index;
    private final int WIFI_SCAN_PERMISSION_CODE = 111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    /**
     * @function 初始化view
     */
    private void initView() {
        wifilist = (ListView) findViewById(R.id.wifi_list);
        setTitle("连接wifi");
        initWifi();
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mSwipeRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendMessage();
            }
        });
    }

    /**
     * @function 初始化wifi
     */
    private void initWifi() {
        //打开wifi
        mWifiUtils = new WifiUtils(this);
        mWifiUtils.openWifi();
        new refreshWifiThread().run();
        //wificonfiguration初始化
        initWifiClick();

    }

    /**
     * @function 初始化wifiListClick
     */
    private void initWifiClick() {
        wifilist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                // mWifiUtils.c
                String WifiSSid = null;
                WifiSSid = mScanResultList.get(i).SSID;
                //返回wifiitemId是networkId;
                mWifiUtils.getConfiguration();
                int wifiItemId = mWifiUtils.IsConfiguration(WifiSSid);
                Log.e(TAG, "wifiItemId:" + wifiItemId);
//                if (wifiItemId != -1) {
//                    //如果wifi保存有密码,连接wifi
//                    if (mWifiUtils.ConnectWifi(wifiItemId)) {
////                         连接已保存密码的WiFi根据networkId
//                        Toast.makeText(getApplicationContext(), "正在连接",
//                                Toast.LENGTH_SHORT).show();
//                        sendMessage();
//                        index = i;
//                    }
//                } else {
                    //wifi没有保存密码
                    final CustomDialog pswDialog = new CustomDialog();
                    View dialogview = LayoutInflater.from(getApplicationContext()).inflate(R.layout.wifi_layout, null);
                    final EditText pswedit = (EditText) dialogview.findViewById(R.id.wifi_password_edit);
                    pswDialog.setView(dialogview);
                    pswDialog.setOnDialogListener(new CustomDialog.OnDialogListener() {
                        @Override
                        public void sureListener() {
                            Psw = pswedit.getText().toString();
                            if (Psw != null) {
                                int netId = mWifiUtils
                                        .AddWifiConfig(mScanResultList,
                                                mScanResultList.get(i).SSID,
                                                Psw);
                                Log.e(TAG,"netid:"+netId);
                                if (netId != -1) {
//                                    mWifiUtils.getConfiguration();// 添加了配置信息，要重新得到配置信息
                                    if (mWifiUtils
                                            .ConnectWifi(netId)) {
                                        //保存当前的wifi名称(ssid)和密码;
//                                        SharedPreferencesUtil.SaveSettings(getApplicationContext(),"WifiSSID",mScanResultList.get(i).SSID);
//                                        SharedPreferencesUtil.SaveSettings(getApplicationContext(),"WifiPassword",Psw);
                                        Log.e(TAG,"ssid:"+mScanResultList.get(i).SSID+"password:"+Psw);
                                        //**************************************************************************************************************
                                        TextView tv = new TextView(getApplicationContext());
                                        tv.setText("正在链接，请稍候");
                                        tv.setTextColor(Color.BLACK);
                                        mCustomDialog = new CustomDialog().setView(tv);
                                        mCustomDialog.show(getFragmentManager(),"dialog");
                                                new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Thread.sleep(000);
                                                    refreshWifiHandler.sendEmptyMessage(3);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                        index = i;
                                        sendMessage();
                                    }
                                } else {
                                    // 网络连接错误
                                    Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void cancelListener() {
                        }
                    });
                    pswDialog.show(getFragmentManager(), TAG);
//                }
            }
        });
    }

    /**
     * @function 发送Message
     */
    private void sendMessage() {
        Message msg = Message.obtain();
        msg.what = 2;
        refreshWifiHandler.sendMessage(msg);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_wifi;
    }

    public class refreshWifiThread implements Runnable {
        @Override
        public void run() {
            try {
                //不该加上这个玩意
                //Thread.sleep(3000);
                Message message = Message.obtain();
                message.what = 1;
                refreshWifiHandler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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
            getAllNetWorkList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WIFI_SCAN_PERMISSION_CODE:
                Log.e(TAG, "grantResults.length:" + grantResults.length + "grantResults[0]:" + grantResults[0]);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 允许
                    Toast.makeText(getApplicationContext(), "允许", Toast.LENGTH_SHORT).show();
                    getAllNetWorkList();
                } else {
                    // 不允许
                    Log.e(TAG, "grantResults:" + grantResults[0]);
                    Toast.makeText(this, "请允许wifi开启", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * @function 扫描wifi, 连接wifi
     */
    public void getAllNetWorkList() {
        //开始扫描
        mWifiUtils.startScan(this);
        //得到wifi扫描的list列表
        mScanResultList = mWifiUtils.getWifiList();
        if (wifiBeanList.size() != 0) {
//            Toast.makeText(getApplicationContext(),"size:"+wifiBeanList.size(),Toast.LENGTH_SHORT).show();
            wifiBeanList.clear();
        }
        if (mScanResultList != null) {
            for (int i = 0; i < mScanResultList.size(); i++) {
                mScanResult = mScanResultList.get(i);
                int Level = WifiManager.calculateSignalLevel(
                        mScanResult.level, 5);
                mWifiBean = new WifiBean(mScanResult.BSSID, mScanResult.SSID, mScanResult.capabilities, Level);
                wifiBeanList.add(mWifiBean);
            }
            mWifiListAdapter = new WifiListAdapter(wifiBeanList, this, new WifiListAdapter.ChangeState() {
                @Override
                public int setConnect() {
                    return index;
                }
            });
//            if(isFirst) {
//                Toast.makeText(getApplicationContext(),"第一次显示List",Toast.LENGTH_SHORT).show();
            wifilist.setAdapter(mWifiListAdapter);
            wifilist.setVisibility(View.VISIBLE);
            isFirst = false;
//            }
//            else{
//                Toast.makeText(getApplicationContext(),"不是第一次显示",Toast.LENGTH_SHORT).show();
//                mWifiListAdapter.notifyDataSetChanged();
//                wifilist.postInvalidate();
//            }
            //这个去掉会导致刷新停止不了
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        } else {
            Log.e(TAG, "没有发现可以连接的wifi");
        }
    }
}
