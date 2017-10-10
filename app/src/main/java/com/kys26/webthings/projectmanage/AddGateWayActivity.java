package com.kys26.webthings.projectmanage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.kys26.webthings.adapter.ProjectListAdapter;
import com.kys26.webthings.global.Constant;
import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.httpnetworks.MyJsonRequestWithCookie;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.util.MyToast;
import com.kys26.webthings.util.ToastHelper;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.kys26.webthings.dialog.IntentDialog.dialog;
import static com.kys26.webthings.global.Constant.dataList;
import static com.kys26.webthings.global.Constant.map;
import static com.kys26.webthings.global.Constant.videonum;
import static com.kys26.webthings.method.MethodTools.state;

/**
 * @author 李赛鹏
 * @class 添加网关
 * Created by kys_9 on 2016/12/2.
 */

public class AddGateWayActivity extends Activity {
    @InjectView(R.id.addgateway_left_iv)
    ImageView mAddgatewayLeftIv;
    @InjectView(R.id.addgateway_tittle)
    TextView mAddgatewayTittle;
    @InjectView(R.id.addgateway_datalist)
    ListView mAddgatewayDatalist;
    @InjectView(R.id.addgateway_gwspinner)
    Spinner mAddgatewayGwspinner;
    @InjectView(R.id.addgateway_additemiv)
    ImageView mAddgatewayAdditemiv;
    @InjectView(R.id.addGateWay_NextStep)
    TextView mAddGateWayNextStep;
    @InjectView(R.id.addGateWay_FarmName)
    TextView mAddGateWayFarmName;
    @InjectView(R.id.gwlist_tv)
    TextView mGwlistTv;
    private List<String> datalist;
    private List<String> videolist;
    public static String TAG = "AddGateWayActivity.class";
    //数据网关价格
    private static String dataprice = "";
    //视频网关价格
    private static String videoprice = "";
    //获取网关价格类型index,下一步类型index,以及更新步骤
    private static final int PriceType = 0, NextType = 1, UpdateType = 2;
    //视频网关的Adapter
    ProjectListAdapter videoAdapter;
    //数据网关的adapter
    ProjectListAdapter dataAdapter;
    //网关数据
    private List<String> gwlist;
    //好吧，其实他是判断是否全部填写完成
    private boolean isOk = true;
    //设备类型(编号)目前数据网关是7936,目前视频网关是7935;
    private static String datadeviceType;
    private static String videodeviceType;
    //数据网关起始位和停止位id,视频网关起始位和停止位
    private static String dataidBegin;
    private static String dataidEnd;
    private static String videoidBegin;
    private static String videoidEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_addgateway);
        ButterKnife.inject(this);
        initView();
        initList();
    }

    /***
     * @fuction 初始化view
     */
    private void initView() {
        JSONObject priceob = new JSONObject();
        try {
            priceob.put("type", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //获取price价格
        getWebData(Path.URL_GET_DEVICETYPE, priceob, PriceType);
        datalist = new ArrayList<String>();
        videolist = new ArrayList<String>();
        gwlist = new ArrayList<String>();
        Intent intent = getIntent();
        mAddGateWayFarmName.setText(intent.getStringExtra("farmname"));
        gwlist.add("数据网关");
        gwlist.add("视频网关");
        ArrayAdapter<String> gwadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, gwlist);
        mAddgatewayGwspinner.setAdapter(gwadapter);
        mAddgatewayGwspinner.setSelection(0);
        Constant.GwNodeList.add(0, Constant.dataList);
        Constant.GwNodeList.add(1, Constant.videoList);
        Constant.dataList.add(Constant.datapositionList);
        Constant.videoList.add(Constant.videopositionList);
    }

    /***
     * @fuction 初始化listview
     */
    private void initList() {
        dataAdapter = new ProjectListAdapter(this, datalist, 0);
        videoAdapter = new ProjectListAdapter(this, videolist, 1);
        mAddgatewayDatalist.setAdapter(dataAdapter);
        Constant.GwNodeList = new ArrayList<List>();
//        mAddgatewayVideolist.setAdapter(videoAdapter);
        mAddgatewayGwspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    dataAdapter.isAgain();
                    mAddgatewayDatalist.setAdapter(dataAdapter);
                    mGwlistTv.setText("数据网关(设备编码范围:" + dataidBegin + "-" + dataidEnd + ")");
                } else {
                    videoAdapter.isAgain();
                    mAddgatewayDatalist.setAdapter(videoAdapter);
                    mGwlistTv.setText("视频网关(设备编码范围:" + videoidBegin + "-" + videoidEnd + ")");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /***
     * @param URL     获取网页数据的URL
     * @param send_jb 需要发送的json数据
     * @fuction 获取网页数据
     */
    public void getWebData(String URL, JSONObject send_jb, final int Type) {
        MethodTools.sPreFerCookie = getSharedPreferences("Cookies",
                Context.MODE_PRIVATE);
        MyJsonRequestWithCookie.newhttpPost(Path.host + URL, send_jb, MethodTools.sPreFerCookie.getString("cookie", "null"), 7);
        MethodTools.handlerJson = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == Code.LUCKYSEVEN) {
                    //获取数据后进行处理
                    if (Type == PriceType)
                        analysis(msg.obj.toString(), PriceType);
                    else if (Type == NextType)
                        analysis(msg.obj.toString(), NextType);
                    //else
//                    Log.e(TAG, msg.obj.toString());
                    mGwlistTv.setText("数据网关(设备编码范围:" + dataidBegin + "-" + dataidEnd + ")");
                    //  IntentDialog.dialog.cancel();
                } else if (msg.what == Code.FAILURE) {
                    //消除等待进度弹出框
                    dialog.cancel();
                    MyToast.makeImgAndTextToast(AddGateWayActivity.this, getResources().getDrawable(R.drawable.tips_sry),
                            "请求失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
                    state = false;
                }
            }
        };
    }

    /***
     * @param data 获取得到的json数据
     * @param Type 判断是否是价格数据或者下一步的index
     * @fuction 分析json数据
     */
    private void analysis(String data, int Type) {
        //将获取的数据保存在静态存储区里
        MethodTools.framData = data;
        //声明一个map将所有fram数据装载起来
        if (Type == PriceType) {
            try {
                //数据是一个json数组，开始进行筛选得到所有用户
                JSONArray jsonArray = new JSONArray(data.toString());
                dataprice = jsonArray.getJSONObject(0).get("deviceprice").toString();
                videoprice = jsonArray.getJSONObject(1).get("deviceprice").toString();
                datadeviceType = jsonArray.getJSONObject(0).get("ndname").toString();
                videodeviceType = jsonArray.getJSONObject(1).get("ndname").toString();
                dataidBegin = jsonArray.getJSONObject(0).get("idbegin").toString();
                dataidEnd = jsonArray.getJSONObject(0).get("idend").toString();
                videoidBegin = jsonArray.getJSONObject(1).get("idbegin").toString();
                videoidEnd = jsonArray.getJSONObject(1).get("idend").toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Type == NextType) {
            try {
                JSONObject ob = new JSONObject(data.toString());
                if (ob.get("Status").equals("Success")) {
//                    Log.e(TAG, "添加网关成功");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (Type == UpdateType) {
            try {
                JSONObject ob  = new JSONObject(data.toString());
                if (ob.get("Status").equals("Success")) {
//                    Log.e(TAG, "更新步骤成功");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    /***
     * @fuction 判断是否可以进行下一步, 应当建立for循环去遍历查询
     */
    private void judgeNextstep() throws JSONException {
//        Log.e(TAG, "判断是否进行下一步");
        isOk = true;
        JSONObject ob = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject dataob = new JSONObject();
        JSONObject videoob = new JSONObject();
        //View list_item = getLayoutInflater().from(this).inflate(R.layout.project_list_item, null);
        if (Constant.datanum == 0 && Constant.videonum == 0) {
            ToastHelper.show(this, "请至少添加一个数据网关或者视频网关");
        } else {
            for (int i = 0; i < Constant.datanum; i++) {
                Log.e(TAG, "map:" + map.toString());
                if (null != Constant.positionList.get(0).get("Idet" + i) &&
                        (null != Constant.positionList.get(0).get("Describeet" + i)) &&
                        (null != Constant.positionList.get(0).get("Nameet" + i)) &&
                        isOk) {
                    Log.e("mapdd", "ddOK");
                } else {
//                    ToastHelper.show(this, "数据网关第" + (i+1) + "个数据没有填写完整,请填写完整");
                    Log.e("mapdd", "ddfalse");
                    isOk = false;
                }
            }
            for (int i = 0; i < videonum; i++) {
                if ((null != Constant.positionList.get(1).get("Idet" + i)) &&
                        (null != Constant.positionList.get(1).get("Describeet" + i)) &&
                        (null != Constant.positionList.get(1).get("Nameet" + i)) &&
                        isOk) {
                    Log.e("mapdd", "vvOK" + Constant.positionList.get(1).get("Idet" + i));
                } else {
//                        ToastHelper.show(this, "视频网关第" + (i+1) + "个数据没有填写完整,请填写完整");
                    Log.e("mapdd", "ddfalse");
                    isOk = false;
                }
            }
            if (isOk) {
                /**
                 * 注意：这么写很麻烦，需要后期优化，由于之前的逻辑是这么写的，所以暂时这么写
                 * 再次注意：这货写的命名太恶心人，看清楚datalist和videolist里的“l”非大写
                 * 自己写的时候不允许这么写
                 */
                for(int i = 0;i<datalist.size();i++){
                    Constant.positionList.get(0).put("Price"+i,datalist.get(i));
                }
                for (int i = 0;i<videolist.size();i++){
                    Constant.positionList.get(1).put("Price"+i,videolist.get(i));
                }
                Log.e("mapintent",dataList.size()+" "+Constant.positionList.toString());
                Intent intent = new Intent();
                intent.setClass(AddGateWayActivity.this, AddNodeActivity.class);
//*******************************************************************************************************************************
                startActivity(intent);
                ToastHelper.show(this, "行了行了e行了，赶紧跳转吧");
            } else {
                Log.e("mapbb", Constant.positionList.toString());
                ToastHelper.show(this, "有信息未填写");
            }
        }
    }

    public void listHandler() {
        MethodTools.listHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        datalist.add(msg.obj.toString());
                        dataAdapter.notifyDataSetChanged();
                        Log.e("mapadd"," "+datalist.size());
                        break;
                    case 1:
                        videolist.add(msg.obj.toString());
                        videoAdapter.notifyDataSetChanged();
                        break;
                }
            }
        };
    }

    @OnClick({R.id.addGateWay_NextStep, R.id.addgateway_left_iv, R.id.addgateway_additemiv})
    public void onClick(View view) {
        listHandler();
        switch (view.getId()) {
            case R.id.addgateway_additemiv:
                if (mAddgatewayGwspinner.getSelectedItemId() == 0) {
                    Message datamsg = new Message();
                    datamsg.what = 0;
                    datamsg.obj = dataprice;
                    Constant.datapositionList.add(datalist.size(), map);
                    MethodTools.listHandler.sendMessage(datamsg);
                } else {
                    Message videomsg = new Message();
                    videomsg.what = 1;
                    videomsg.obj = videoprice;
                    Constant.videopositionList.add(videolist.size(), map);
                    MethodTools.listHandler.sendMessage(videomsg);
                }
                break;
            case R.id.addgateway_left_iv:
                this.finish();
                break;
            case R.id.addGateWay_NextStep:
                try {
                    judgeNextstep();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
