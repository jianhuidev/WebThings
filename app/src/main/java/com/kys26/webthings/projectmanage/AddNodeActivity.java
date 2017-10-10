package com.kys26.webthings.projectmanage;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.kys26.webthings.adapter.ExpandListAdapter;
import com.kys26.webthings.dialog.IntentDialog;
import com.kys26.webthings.global.Constant;
import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.httpnetworks.MyJsonRequestWithCookie;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.util.MyToast;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author 李赛鹏
 * @class 添加节点
 * Created by Administrator on 2016/12/6.
 */

public class AddNodeActivity extends Activity {
    @InjectView(R.id.addgateway_left_iv)
    ImageView mAddgatewayLeftIv;
    @InjectView(R.id.addNode_GwSpiner)
    Spinner mAddNodeGwSpiner;
    @InjectView(R.id.addgateway_step)
    TextView mAddgatewayStep;
    @InjectView(R.id.addnode_gwid)
    TextView mAddnodeGwid;
    @InjectView(R.id.addnode_gwname)
    TextView mAddnodeGwname;
    @InjectView(R.id.addnode_exlist)
    ExpandableListView mAddnodeExlist;
    @InjectView(R.id.addNode_NextStep)
    TextView mAddNodeNextStep;
    //右上角spinner的list
    private static List<String> Gwlist = new ArrayList<String>();
    //TAG标记
    private static String TAG = "AddNodeActivity";
    private static final int PriceType = 0, NextType = 1,UpdateType=2;
    //二级列表构造器
    private ExpandListAdapter adapter;
    //数据网关个数
    private int num_datagate;
    //视频网关个数
    private int num_vediogate;
    //childposition
    private List<ArrayList<HashMap<String,Object>>> childposition= new ArrayList<ArrayList<HashMap<String, Object>>>();
    //dialog
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_addnode);
        ButterKnife.inject(this);
        dialog = IntentDialog.createDialog(AddNodeActivity.this, "正在获取数据......");
        dialog.dismiss();
        initListHandler();
        initView();
    }

    /***
     * @fuction 初始化视图
     */
    private void initView() {

        Gwlist = new ArrayList<String>();
        num_datagate = Constant.positionList.get(0).size()/4;
        num_vediogate = Constant.positionList.get(1).size()/4;
        Log.e(TAG," "+num_datagate+"  "+num_vediogate);
        if (Constant.positionList.get(0).size()!=0) {
            mAddnodeGwname.setText("网关名称："+Constant.positionList.get(0).get("Nameet" + 0).toString());
            mAddnodeGwid.setText("网关ID："+Constant.positionList.get(0).get("Idet" + 0).toString());
        }else {
            mAddnodeGwname.setText("网关名称："+Constant.positionList.get(1).get("Nameet" + 0).toString());
            mAddnodeGwid.setText("网关ID："+Constant.positionList.get(1).get("Idet" + 0).toString());
        }
        for (int i = 0;i<num_datagate;i++){
            Gwlist.add(Constant.positionList.get(0).get("Nameet"+i).toString());
        }
        for (int i = 0;i<num_vediogate;i++){
            Gwlist.add(Constant.positionList.get(1).get("Nameet"+i).toString());
        }
        ArrayAdapter<String> gwadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Gwlist);
        mAddNodeGwSpiner.setAdapter(gwadapter);
        JSONObject ob = new JSONObject();

        try {
            ob.put("type", "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getWebData(Path.URL_GET_DEVICETYPE, ob, PriceType);
    }




    /**
     *
     * @fuction 初始化listView
     */
    private void initList() {

        //*********************************************************************************************************************************
        for(int i=0;i<Constant.pricelist.size();i++){
            Constant.childposition.add(i, new ArrayList<HashMap<String, Object>>());
        }
        mAddNodeGwSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position<num_datagate){//数据网关
                    ExpandListAdapter adapter = new ExpandListAdapter(getApplicationContext(), Constant.pricelist,childposition,0);
                    mAddnodeExlist.setAdapter(adapter);
                    mAddnodeGwname.setText("网关名称："+Constant.positionList.get(0).get("Nameet"+position).toString());
                    mAddnodeGwid.setText("网关ID："+Constant.positionList.get(0).get("Idet"+position).toString());
                }else{
                    ExpandListAdapter adapter = new ExpandListAdapter(getApplicationContext(), Constant.pricelist,childposition,1);
                    mAddnodeExlist.setAdapter(adapter);
                    mAddnodeGwname.setText("网关名称："+Constant.positionList.get(1).get("Nameet"+(position-num_datagate)).toString());
                    mAddnodeGwid.setText("网关ID："+Constant.positionList.get(1).get("Idet"+(position-num_datagate)).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (0<num_datagate){//数据网关
            adapter = new ExpandListAdapter(getApplicationContext(), Constant.pricelist,childposition,0);
            mAddnodeExlist.setAdapter(adapter);
        }else{
            adapter = new ExpandListAdapter(getApplicationContext(), Constant.pricelist,childposition,1);
            mAddnodeExlist.setAdapter(adapter);
        }
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
                    //  else if(Type==)
                    Log.e(TAG, msg.obj.toString());
                      dialog.dismiss();
                    initList();
                } else if (msg.what == Code.FAILURE) {
                    //消除等待进度弹出框
                    dialog.cancel();
                    MyToast.makeImgAndTextToast(AddNodeActivity.this, getResources().getDrawable(R.drawable.tips_sry),
                            "请求失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
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
                for (int i = 0; i < jsonArray.length(); i++) {

                    Log.e(TAG,"data:"+jsonArray.getJSONObject(i).toString());
                    HashMap<String,Object> map=new HashMap<String, Object>();
                    map.put("price", jsonArray.getJSONObject(i).get("deviceprice").toString());
                    map.put("name", jsonArray.getJSONObject(i).get("devicetypename").toString());
                    map.put("idbegin", jsonArray.getJSONObject(i).get("idbegin").toString());
                    map.put("idend", jsonArray.getJSONObject(i).get("idend").toString());
                    Constant.pricelist.add(i,map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Type == NextType) {
            try {
                JSONObject ob = new JSONObject(data.toString());
                if (ob.get("Status").equals("Success")) {
                    Log.e(TAG, "添加网关成功");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(Type==UpdateType){
            try {
                JSONObject ob = new JSONObject(data.toString());
                if(ob.get("Status").equals("Success")){
                    Log.e(TAG,"更新步骤成功");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @OnClick({R.id.addgateway_left_iv, R.id.addNode_NextStep})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addgateway_left_iv:
                this.finish();
                break;
            case R.id.addNode_NextStep:
                Intent intent=new Intent();
                intent.setClass(AddNodeActivity.this,CompleteProject.class);
                startActivity(intent);
                break;
        }
    }
    /**
     * @function expandList的有关Handler
     *           what:  0:刷新列表
     */
    public void initListHandler(){
        MethodTools.listHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        adapter.notifyDataSetChanged();
                        mAddnodeExlist.expandGroup(Integer.valueOf(msg.obj.toString()));
                        break;
                    case 1:

                        break;
                }
            }
        };
    }
}
