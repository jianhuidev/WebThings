package com.kys26.webthings.command;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kys26.webthings.adapter.CmdCtlListAdapter;
import com.kys26.webthings.dialog.IntentDialog;
import com.kys26.webthings.dialog.MyWheelAlertDialog;
import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.httpnetworks.MyJsonRequestWithCookie;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.util.ToastHelper;
import com.lidroid.xutils.ViewUtils;
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
 * @author kys_09使用者：李赛鹏 2016/9/15.
 * @function:命令控制查询明细
 */
public class CmdCtlDetailActivity extends Activity {
    @InjectView(R.id.querydetail_title)
    TextView mQuerydetailTitle;
    @InjectView(R.id.querydetail_choicegw)
    Spinner mQuerydetailChoicegw;
    @InjectView(R.id.message_list)
    ListView mMessageList;
    @InjectView(R.id.querydetail_bg)
    LinearLayout mQuerydetailBg;
    /**
     * 输出标识
     */
    private String TAG = "com.kys26.webthings.command.ComCtlDetailActivity";
    /**
     * dialog
     */
    private Dialog dialog;
    private final int GETNODE = 11;
    private final int command_what = 22;
    private int whitch = 0;
    private Intent intent;
    private CmdCtlListAdapter cmdAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmdctldetail);
        ButterKnife.inject(this);
        // 初始化IOC注解
        ViewUtils.inject(this);
        initHandler();
        MethodTools.listdata_gw = new ArrayList<HashMap<String, Object>>();
        MethodTools.nodeList = new ArrayList<List<HashMap<String, Object>>>();

        //初始化UI组件
        initView();
        initRequest();
    }

    /**
     * 初始化请求
     */
    private void initRequest() {

        JSONObject send_jb = new JSONObject();
        try {
            send_jb.put("farmid", MethodTools.listdata_farm.get(intent.getIntExtra("position", 0)).get("farm_id").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.show();
        //开始进行发送数据
        MyJsonRequestWithCookie.newhttpPost(Path.host + Path.URL_GET_LIGHT, send_jb, MethodTools.sPreFerCookie.getString("cookie", "null"), GETNODE);
    }

    /**
     * 初始化Handler
     */
    private void initHandler() {
        MethodTools.handlerJson = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GETNODE:
                        analysis(msg.obj.toString());

                        break;
                    case command_what:
                        anaIsOk(msg.obj.toString());
                        break;
                    case Code.FAILURE:
                        ToastHelper.show(getApplication(), "请求失败，请重新尝试");
                        Log.e(TAG, "连接错误：" + msg.obj.toString());
                        break;

                }
            }
        };
    }

    /**
     * @param s
     * @function 判断命令是否执行
     */
    private void anaIsOk(String s) {
        try {
            JSONObject jb = new JSONObject(s);
            if ("Success".equals(jb.get("Status"))){
                Toast.makeText(getApplicationContext(),"命令执行成功",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"错误，请重新尝试",Toast.LENGTH_SHORT).show();
            }
//            Thread.currentThread().sleep(3000);
//            initRequest();

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        dialog.dismiss();

    }

    /**
     * @param s
     * @function 解析数据并处理
     */
    private void analysis(String s) {
        //打印获取到的数据，用于开发人员进行数据分析
        Log.i(TAG, s.toString());
        try {
            //数据是一个json数组，开始进行删选得到农场列表
            JSONArray jsonArray = new JSONArray(s);
            //声明一个map将所有fram数据装载起来
//            MethodTools.listdata_gw = new ArrayList<HashMap<String, Object>>();
//            MethodTools.nodeList = new ArrayList<List<HashMap<String, Object>>>();
            Log.e("socketttt","  ++++"+jsonArray.length());
            //利用for对数据进行剥离
            for (int i = 0; i < jsonArray.length(); i++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                Log.d("change", " " + jsonObject.toString());
                //json数组中第一个数据是农场信息列表
//                    map.put("status", jsonObject.getString("status"));//网关状态
//                    map.put("state", jsonObject.getString("state"));//网关状态代码  85：打开  34：关闭  255：禁用
//                    map.put("farm_name", jsonObject.getString("farm_name"));//所属农场名
                map.put("gwname", jsonObject.getString("gwname"));//网关名
                map.put("gwid", jsonObject.getString("gwid"));//网关ID
                MethodTools.listdata_gw.add(map);
                JSONArray nodeArray = jsonObject.getJSONArray("listdata");
                List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
                if (nodeArray.length() == 0) {

                } else {
                    for (int j = 0; j < nodeArray.length(); j++) {
                        //声明一个map将所有fram数据装载起来
                        JSONObject jb = nodeArray.getJSONObject(j);
                        HashMap<String, Object> nodemap = new HashMap<String, Object>();
                        nodemap.put("nodeid", jb.getString("id"));//节点ID
                        nodemap.put("status", jb.getString("status"));//工作状态
                        nodemap.put("name", jb.getString("name"));//节点名称
                        nodemap.put("state", jb.getString("state"));//工作状态代码  85：打开  34：关闭  255：禁用
                        nodemap.put("kid_stat", jb.getString("kid_stat"));//工作状态代码  85：打开  34：关闭  255：禁用
                        nodemap.put("kid2_stat", jb.getString("kid2_stat"));//工作状态代码  85：打开  34：关闭  255：禁用
                        list.add(nodemap);
                    }
                }
                MethodTools.nodeList.add(list);

            }
            dialog.dismiss();
        } catch (Exception e) {
            Log.i(TAG, "json解析异常" + e.toString());
        }

        conView();
    }

    /**
     * 继续更改界面
     */
    private void conView() {
        String[] gw = new String[MethodTools.listdata_gw.size()];
        Log.e("socketttt"," "+MethodTools.listdata_gw.size());
        for (int i = 0; i < MethodTools.listdata_gw.size(); i++) {
            gw[i] = MethodTools.listdata_gw.get(i).get("gwname").toString();
            Log.e("socketttt"," -----"+MethodTools.listdata_gw.get(i).get("gwname").toString());
        }
        ArrayAdapter<String> gwadapter = new ArrayAdapter<String>(this, R.layout.spinner_item, R.id.spinner_itemText, gw);
        mQuerydetailChoicegw.setAdapter(gwadapter);
        cmdAdapter = new CmdCtlListAdapter(getApplication(), whitch);
        mMessageList.setAdapter(cmdAdapter);
        mQuerydetailChoicegw.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                whitch = position;
                mMessageList.setAdapter(cmdAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mMessageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String kid1 = "节点1出错";
                String kid2 = "节点2出错";
                switch (Integer.valueOf(MethodTools.nodeList.get(whitch).get(position).get("kid_stat").toString())) {
                    case 34:
                        kid1 = "打开节点1";
                        break;
                    case 85:
                        kid1 = "关闭节点1";
                        break;
                }
                switch (Integer.valueOf(MethodTools.nodeList.get(whitch).get(position).get("kid2_stat").toString())) {
                    case 34:
                        kid2 = "打开节点2";
                        break;
                    case 85:
                        kid2 = "关闭节点2";
                        break;
                }

                new MyWheelAlertDialog(CmdCtlDetailActivity.this)
                        .builder()
                        .setTitle("开关设置")
                        .setNegativeButton(kid1, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                JSONObject jsonObject = new JSONObject();
                                JSONArray jsonArray = new JSONArray();
                                try {

                                    jsonObject.put("command","0x12");

                                    JSONObject arrayObject = new JSONObject();
                                    arrayObject.put("nodeid",MethodTools.nodeList.get(whitch).get(position).get("nodeid").toString());
                                    arrayObject.put("gwid",MethodTools.listdata_gw.get(whitch).get("gwid").toString());
                                    if (Integer.valueOf(MethodTools.nodeList.get(whitch).get(position).get("kid_stat").toString())==34){
                                        arrayObject.put("kid_stat","85");
                                        MethodTools.nodeList.get(whitch).get(position).put("kid_stat","85");
                                        if ("85".equals(MethodTools.nodeList.get(whitch).get(position).get("kid2_stat"))) {
                                            MethodTools.nodeList.get(whitch).get(position).put("state", "85");
                                            cmdAdapter.notifyDataSetChanged();
                                        }
                                    }else {
                                        arrayObject.put("kid_stat","34");
                                        MethodTools.nodeList.get(whitch).get(position).put("kid_stat","34");
                                        if ("34".equals(MethodTools.nodeList.get(whitch).get(position).get("kid2_stat"))) {
                                            MethodTools.nodeList.get(whitch).get(position).put("state", "34");
                                            cmdAdapter.notifyDataSetChanged();
                                        }
                                    }
                                    arrayObject.put("kid2_stat",MethodTools.nodeList.get(whitch).get(position).get("kid2_stat").toString());
                                    jsonArray.put(arrayObject);
                                    jsonObject.put("data",jsonArray);
                                    MyJsonRequestWithCookie.newhttpPost(Path.host+Path.URL_COMMAND,jsonObject,MethodTools.sPreFerCookie.getString("cookie", "null"),command_what);
                                    dialog.show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setPositiveButton(kid2, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                JSONObject jsonObject = new JSONObject();
                                JSONArray jsonArray = new JSONArray();
                                try {

                                    jsonObject.put("command","0x12");

                                    JSONObject arrayObject = new JSONObject();
                                    arrayObject.put("nodeid",MethodTools.nodeList.get(whitch).get(position).get("nodeid").toString());
                                    arrayObject.put("gwid",MethodTools.listdata_gw.get(whitch).get("gwid").toString());
                                    if (Integer.valueOf(MethodTools.nodeList.get(whitch).get(position).get("kid2_stat").toString())==34){
                                        arrayObject.put("kid2_stat","85");
                                        MethodTools.nodeList.get(whitch).get(position).put("kid2_stat","85");
                                        if ("85".equals(MethodTools.nodeList.get(whitch).get(position).get("kid_stat"))) {
                                            MethodTools.nodeList.get(whitch).get(position).put("state", "85");
                                            cmdAdapter.notifyDataSetChanged();
                                        }
                                    }else {
                                        arrayObject.put("kid2_stat","34");
                                        MethodTools.nodeList.get(whitch).get(position).put("kid2_stat","34");
                                        if ("34".equals(MethodTools.nodeList.get(whitch).get(position).get("kid_stat"))) {
                                            MethodTools.nodeList.get(whitch).get(position).put("state", "34");
                                            cmdAdapter.notifyDataSetChanged();
                                        }
                                    }
                                    arrayObject.put("kid_stat",MethodTools.nodeList.get(whitch).get(position).get("kid_stat").toString());
                                    jsonArray.put(arrayObject);
                                    jsonObject.put("data",jsonArray);
                                    MyJsonRequestWithCookie.newhttpPost(Path.host+Path.URL_COMMAND,jsonObject,MethodTools.sPreFerCookie.getString("cookie", "null"),command_what);
                                    dialog.show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .show();
            }
        });
    }

    /**
     * 初始化界面
     */
    private void initView() {
        intent = getIntent();
        mQuerydetailTitle.setText(MethodTools.listdata_farm.get(intent.getIntExtra("position", 0)).get("farm_name").toString());
        dialog = IntentDialog.createDialog(CmdCtlDetailActivity.this, "正在获取数据......");
        dialog.setCanceledOnTouchOutside(false);
    }

    @OnClick({R.id.left_iv, R.id.open_all, R.id.close_all})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_iv:
                finish();
                break;
            case R.id.open_all:
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                try {

                    jsonObject.put("command", "0x12");

                    JSONObject arrayObject = new JSONObject();
                    if (MethodTools.listdata_gw.size()!=0&&MethodTools.listdata_gw.size()!=0) {
                        for (int i = 0; i < MethodTools.nodeList.get(whitch).size(); i++) {

                            arrayObject.put("nodeid", MethodTools.nodeList.get(whitch).get(i).get("nodeid").toString());
                            arrayObject.put("gwid", MethodTools.listdata_gw.get(whitch).get("gwid").toString());
                            arrayObject.put("kid_stat", "85");
                            arrayObject.put("kid2_stat", "85");
                            jsonArray.put(arrayObject);
                        }

                        jsonObject.put("data", jsonArray);
                        for (int i = 0; i < MethodTools.nodeList.get(whitch).size(); i++) {
                            MethodTools.nodeList.get(whitch).get(i).put("kid_stat", "85");
                            MethodTools.nodeList.get(whitch).get(i).put("kid2_stat", "85");
                            MethodTools.nodeList.get(whitch).get(i).put("state", "85");
                        }
                        cmdAdapter.notifyDataSetChanged();
                        MyJsonRequestWithCookie.newhttpPost(Path.host + Path.URL_COMMAND, jsonObject, MethodTools.sPreFerCookie.getString("cookie", "null"), command_what);
                        dialog.show();
                    }else{
                        Toast.makeText(getApplicationContext(),"网络故障，请重新尝试",Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.close_all:
                JSONObject jsonObject1 = new JSONObject();
                JSONArray jsonArray1 = new JSONArray();
                try {

                    jsonObject1.put("command","0x12");

                    JSONObject arrayObject = new JSONObject();
                    if (MethodTools.listdata_gw.size()!=0&&MethodTools.listdata_gw.size()!=0) {
                    for (int i =0;i<MethodTools.nodeList.get(whitch).size();i++) {
                        arrayObject.put("nodeid", MethodTools.nodeList.get(whitch).get(i).get("nodeid").toString());
                        arrayObject.put("gwid", MethodTools.listdata_gw.get(whitch).get("gwid").toString());
                        arrayObject.put("kid_stat", "34");
                        arrayObject.put("kid2_stat", "34");
                        jsonArray1.put(arrayObject);
                        MethodTools.nodeList.get(whitch).get(i).put("state", "34");
                    }
                        cmdAdapter.notifyDataSetChanged();
                    jsonObject1.put("data",jsonArray1);
                    for (int i=0;i<MethodTools.nodeList.get(whitch).size();i++) {
                        MethodTools.nodeList.get(whitch).get(i).put("kid_stat","34");
                        MethodTools.nodeList.get(whitch).get(i).put("kid2_stat","34");

                    }
                    MyJsonRequestWithCookie.newhttpPost(Path.host+Path.URL_COMMAND,jsonObject1,MethodTools.sPreFerCookie.getString("cookie", "null"),command_what);
                    dialog.show();
                    }else{
                        Toast.makeText(getApplicationContext(),"网络故障，请重新尝试",Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
