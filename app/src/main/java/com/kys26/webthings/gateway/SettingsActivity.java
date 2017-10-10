//package com.kys26.webthings.settings;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ListView;
//import android.widget.SimpleAdapter;
//
//import com.ab.activity.AbActivity;
//import com.kys26.webthings.dialog.IntentDialog;
//import com.kys26.webthings.gesture.CreateGesturePasswordActivity;
//import com.kys26.webthings.login.RetrievePassword;
//import com.kys26.webthings.main.BaseActivity;
//import com.kys26.webthings.util.network.NetworksInfo;
//import com.kys26.webthings.util.network.SetNetwork;
//import com.kys26.webthings.wifi.SettingWifi;
//import com.zhangyx.MyGestureLock.R;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
// * Created by 徐建强 on 2015/7/20.
// */
//public class SettingsActivity extends AbActivity {
//
//    /**lists适配器*/
//    private SimpleAdapter listItemAdapter;
//    /**arrayList装载项*/
//    private ArrayList<HashMap<String, Object>> listItems;
//    /**ListView控件*/
//    private ListView setList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setAbContentView(R.layout.activitiy_settings);
//
//        //初始化UI组件
//        initView();
//    }
//
//    private  void initView(){
//        this.setTitleText(R.string.string_activity_InitiaGateway_title);
//        this.setTitleTextSize(20);
//        this.setTitleTextMargin(BaseActivity.marginToLeftTitleNormal, 0, 0, 0);
//        this.setLogo(R.drawable.button_selector_back);
//        this.setTitleLayoutBackground(R.drawable.top_bg);
//        this.setLogoLine(R.drawable.line);
//
//        setList=(ListView)findViewById(R.id.settingsList);
//        //初始化ListView控件项
//        btnSetList();
//    }
//
//    private  void btnSetList(){
//        //List适配器
//        listItems = new ArrayList<HashMap<String, Object>>();
//        for(int i=0;i<5;i++) {
//            HashMap<String, Object> map = new HashMap<String, Object>();
//            switch (i){
//                case 0:
//                    map.put("ItemTitle","更改登陆密码");//文字
//                    map.put("ItemImage", R.drawable.settings_items_bg);//图片
//                    map.put("ItemTab", R.drawable.splash_shadow);//图片
//                    break;
//                case 1:
//                    map.put("ItemTitle","更改农场信息");//文字
//                    map.put("ItemImage", R.drawable.settings_items_bg);//图片
//                    map.put("ItemTab", R.drawable.splash_shadow);//图片
//                    break;
//                case 2:
//                    map.put("ItemTitle","初始化网关");//文字
//                    map.put("ItemImage", R.drawable.settings_items_bg);//图片
//                    map.put("ItemTab", R.drawable.splash_shadow);//图片
//                    break;
//             /*   case 3:
//                    map.put("ItemTitle","更改WiFi密码");//文字
//                    map.put("ItemImage", R.drawable.settings_items_bg);//图片
//                    map.put("ItemTab", R.drawable.splash_shadow);//图片
//                    break;*/
//                case 3:
//                    map.put("ItemTitle","更改手势锁密码");//文字
//                    map.put("ItemImage", R.drawable.settings_items_bg);//图片
//                    map.put("ItemTab", R.drawable.splash_shadow);//图片
//                    break;
//                case 4:
//                    map.put("ItemTitle", "退出");//文字
//                    map.put("ItemImage", R.drawable.settings_items_bg);//图片
//                    map.put("ItemTab", R.drawable.splash_shadow);//图片
//                    break;
//                default:
//                    break;
//            }
//            listItems.add(map);
//        }
//        // listItems数据源
//        listItemAdapter = new SimpleAdapter(this,listItems,
//                //ListItem的XML布局实现
//                R.layout.list_item_layout,
//                //动态数组与ImageItem对应的子项
//                new String[] {"ItemTitle","ItemImage","ItemTab"},
//                new int[ ] {R.id.tv, R.id.listImage, R.id.listImageTab});
//        //绑定适配器
//        setList.setAdapter(listItemAdapter);
//        //为ListView绑定监听
//        setList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//
//                Intent it = new Intent();
//                switch (position) {
//                    case 0:
//                        it.setClass(SettingsActivity.this, RetrievePassword.class);
//                        startActivity(it);
//                        break;
//                    case 1:
//
//                        break;
//                    case 2:
//                        //跳入初始化设置
//                        it.setClass(SettingsActivity.this,
//                                SettingWifi.class);
//                        startActivity(it);
//                        break;
//                    case 3:
//                        it.setClass(SettingsActivity.this,
//                                CreateGesturePasswordActivity.class);
//                        startActivity(it);
//                        //进入手势更改界面后销毁当前Activity
//                        SettingsActivity.this.finish();
//                        break;
//                    case 4:
//                        IntentDialog.showExitDialog(SettingsActivity.this);
//                        break;
//                    default:
//                        break;
//                }
//
//            }
//        });
//    }
//
//    //判断当前是否有可用网络
//    private  void Network(){
//
//        boolean net= NetworksInfo.checkNet(SettingsActivity.this);
//        if(net==true){
//            Intent intent=new Intent(SettingsActivity.this,InitializeGateway.class);
//            startActivity(intent);
//        }else {
//            setNetwork();
//          }
//
//    }
//    //弹出框开启网卡
//    public  void setNetwork(){
//
//        SetNetwork.setNetwork(SettingsActivity.this);
//       /* AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
//        builder.setIcon(R.drawable.alert_img_1);
//        builder.setTitle(R.string.string_activity_SetNetWork_dialog_title);
//        builder.setMessage(R.string.string_activity_SetNetWork_dialog_message);
//        builder.setPositiveButton(R.string.string_activity_SetNetWork_dialog_Positive, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //开启无线网卡
//                WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
//                wifiManager.setWifiEnabled(true);
//                MyToast.makeTextToast(SettingsActivity.this,
//                        "正在开启网络，请耐心等待", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
//            }
//        });
//
//        builder.setNegativeButton(R.string.string_activity_SetNetWork_dialog_Negative, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            }
//        });
//        builder.create();
//        builder.show();*/
//    }
//}

