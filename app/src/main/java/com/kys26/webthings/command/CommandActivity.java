package com.kys26.webthings.command;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kys26.webthings.adapter.VideoNodeAdapter;
import com.kys26.webthings.adapter.WindNodeGridAdapter;
import com.kys26.webthings.dialog.CustomDialog;
import com.kys26.webthings.fragment.GWFragment;
import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.httpnetworks.VolleyStringRequest;
import com.kys26.webthings.interfac.NodeType;
import com.kys26.webthings.main.BaseActivity;
import com.kys26.webthings.main.MainActivity;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.model.NodeControlData;
import com.kys26.webthings.model.NodeVideoData;
import com.kys26.webthings.model.VideoChannelData;
import com.kys26.webthings.util.MyToast;
import com.kys26.webthings.util.StringUtil;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.widget.ListPopupWindow.MATCH_PARENT;
import static com.kys26.webthings.httpconstant.Code.AUTOMODEL_TMP_CLOSE;
import static com.kys26.webthings.httpconstant.Code.AUTOMODEL_TMP_OPEN;
import static com.kys26.webthings.httpconstant.Code.AUTO_ALL_OPEN;
import static com.kys26.webthings.httpconstant.Code.AUTO_MODEL_OPEN;
import static com.kys26.webthings.httpconstant.Code.AUTO_NH3_OPEN;
import static com.kys26.webthings.httpconstant.Code.COOLING_NODE_NAME;
import static com.kys26.webthings.httpconstant.Code.HANDLE_MODEL_CLOSE;
import static com.kys26.webthings.httpconstant.Code.HANDLE_MODEL_OPEN;
import static com.kys26.webthings.httpconstant.Code.TIME_MODEL_CLOSE;
import static com.kys26.webthings.httpconstant.Code.TIME_MODEL_OPEN;
import static com.kys26.webthings.method.MethodTools.mCoolingDataList;
import static com.kys26.webthings.method.MethodTools.mVideoChannelData;

/**
 * @author 李赛鹏
 * @class Created by kys_9 on 2017/5/5.
 */

public class CommandActivity extends BaseActivity implements View.OnClickListener {
    private FrameLayout left_btn, right_btn;
    private Button open_all_btn, close_all_btn;
    private static final String TAG = CommandActivity.class.getSimpleName();
    private LinearLayout controll_layout;
    //点击node的position
    public int typePosition = 0;
    //点击farm的position
    public int parentPosition = 0;
    private JSONObject jb = new JSONObject();
    //自定义dialog
    CustomDialog mCustomDialog;
    EditText edit;
    private GridView windNodeList;
    private WindNodeGridAdapter windNodeadapter;
    private ScheduledExecutorService HeartService;
    private int Model = 0;
    private int AUTO_MODEL = 1;
    private int TIME_MODEL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnableRefresh(true, new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RequestWebData();
            }
        });
        initView();
        setRightImg(R.drawable.cmd_right_setting);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_command;
    }

    @Override
    protected void onClickRight() {
        super.onClickRight();
    }

    /**
     * @function 初始化view
     */
    private void initView() {
        Intent intent = getIntent();
        typePosition = intent.getIntExtra("type", 0);
        parentPosition = intent.getIntExtra("position", 0);
        left_btn = (FrameLayout) findViewById(R.id.left_btn);
        left_btn.setOnClickListener(this);
        right_btn = (FrameLayout) findViewById(R.id.right_btn);
        right_btn.setOnClickListener(this);
        open_all_btn = (Button) findViewById(R.id.open_all_btn);
        open_all_btn.setOnClickListener(this);
        close_all_btn = (Button) findViewById(R.id.close_all_btn);
        close_all_btn.setOnClickListener(this);
        windNodeList = (GridView) findViewById(R.id.wind_node_list);
        setTitle(GWFragment.names[typePosition]);
        controll_layout = (LinearLayout) findViewById(R.id.control_layout);
        controll_layout.setVisibility(View.GONE);
        if (typePosition == 0 || typePosition > 2) {
            setRightBtnVisible(false);
        } else {
            setRightBtnVisible(true);
        }
        HeartTask();
    }

    /**
     * 心跳数据
     */
    private void HeartTask() {
        final Handler HeartHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        RequestWebData();
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
        HeartService.scheduleAtFixedRate(runnable, 30, 30, TimeUnit.SECONDS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initHandler();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MethodTools.handlerJson = null;
        if (windNodeadapter != null) {
            windNodeadapter.closeFan();
        }
    }

    /**
     * 初始化Handler
     */
    private void initHandler() {
        //重写Handler中的message方法，获取数据后进行异步加载
        RequestWebData();
        MethodTools.HnadlerVOlleyJson = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {// 处理监控的
                    case Code.SUCCESS:
                        JSONArray array = null;
                        if (mVideoChannelData.size() != 0) {
                            mVideoChannelData.clear();
                        }
                        try {
                            array = new JSONArray(msg.obj.toString());
                            for (int i = 0; i < array.length(); i++) {
                                mVideoChannelData.add(VideoChannelData.analysis(array.getJSONObject(i)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        VideoNodeAdapter mVideoNodeAdapter = new VideoNodeAdapter(CommandActivity.this, mVideoChannelData);
                        windNodeList.setAdapter(mVideoNodeAdapter);
                        break;
                    case Code.FAILURE:
                        break;
                }
            }
        };
        MethodTools.handlerJson = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (getRefreshView().isRefreshing()) {
                    getRefreshView().setRefreshing(false);
                }
                switch (msg.what) {
                    case NODE_LIST:// 处理通风的
                        Model = 0;
                        try {
                            JSONObject jsonObject = new JSONObject(msg.obj.toString());
                            JSONArray dataOb = jsonObject.getJSONArray("gDevice");
                            List<NodeControlData> windnodelist = new ArrayList<>();
                            for (int i = 0; i < dataOb.length(); i++) {
                                windnodelist.add(NodeControlData.analysis((JSONObject) dataOb.get(i)));
                            }
                            if (windnodelist.size() > 0) {
                                controll_layout.setVisibility(View.VISIBLE);
                            }
                            switch (typePosition) {
                                case 0://监控

                                    break;
                                case 1://通风
                                    MethodTools.farmDataList.get(parentPosition)
                                            .setNodeSideWind(windnodelist);
                                    //判断当前是否处于自动模式
                                    for (int i = 0; i < MethodTools.farmDataList.get(parentPosition)
                                            .getNodeSideWind().size(); i++) {
                                        int kidStatus = Integer.valueOf(MethodTools.farmDataList.get(parentPosition)
                                                .getNodeSideWind().get(i).getKid_stat());
                                        if ((AUTO_ALL_OPEN <= kidStatus && kidStatus <= AUTOMODEL_TMP_CLOSE)) {
                                            Model = AUTO_MODEL;
                                            break;
                                        } else if (kidStatus == TIME_MODEL_CLOSE || kidStatus == TIME_MODEL_OPEN) {
                                            Model = TIME_MODEL;
                                            break;
                                        } else {
                                            Model = 0;
                                        }
                                    }

                                    break;
                                case 2://降温
                                    MethodTools.farmDataList.get(parentPosition)
                                            .setCooling(windnodelist);
                                    break;
                                case 3:
                                    MethodTools.farmDataList.get(parentPosition)
                                            .setFeed(windnodelist);
                                    break;
                                case 4:
                                    MethodTools.farmDataList.get(parentPosition)
                                            .setDung(windnodelist);
                                    break;
                                case 5:
                                    MethodTools.farmDataList.get(parentPosition)
                                            .setLight(windnodelist);
                                    break;
                                case 6:
                                    MethodTools.farmDataList.get(parentPosition)
                                            .setHeating(windnodelist);
                                    break;
                                case 7:
                                    MethodTools.farmDataList.get(parentPosition)
                                            .setHumidification(windnodelist);
                                    break;
                                case 8:
                                    MethodTools.farmDataList.get(parentPosition)
                                            .setFillLight(windnodelist);
                                    break;
                                case 9:
                                    MethodTools.farmDataList.get(parentPosition)
                                            .setSterilization(windnodelist);
                                    break;
                            }
                            if (typePosition != 2)
                                ShowWindListView(windnodelist);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case Code.FAILURE:
                        MyToast.makeImgAndTextToast(CommandActivity.this, getResources().getDrawable(R.drawable.tips_sry),
                                "请求失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
                        MethodTools.state = false;
                        break;
                    case WIND_CONTROL:
//                        dismissDialog();
                        MethodTools.handlerJson.sendEmptyMessageDelayed(UPDATE_NODENAME, 8000);
                        break;
                    case UPDATE_NODENAME:
                        RequestWebData();
                        break;
                    case GET_COOLING_NODE:// 处理降温
//                        controll_layout.setVisibility(View.VISIBLE);
                        mCoolingDataList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(msg.obj.toString());
                            JSONArray dataOb = jsonObject.getJSONArray("gDevice");
                            List<NodeControlData> NodeControlDataList = new ArrayList<>();
                            for (int i = 0; i < dataOb.length(); i++) {
                                JSONObject object = dataOb.getJSONObject(i);
                                mCoolingDataList.add(NodeControlData.analysis((JSONObject) dataOb.get(i)));
                                if (object.get("ndname").toString().equals(COOLING_NODE_NAME))
                                    NodeControlDataList.add(NodeControlData.analysis((JSONObject) dataOb.get(i)));
                            }
                            if (NodeControlDataList.size() > 0) {
                                controll_layout.setVisibility(View.VISIBLE);
                            }
                            /**
                             * 判断当前是否处于自动模式
                             */
                            for (int i = 0; i < NodeControlDataList.size(); i++) {
                                int kidStatus = Integer.valueOf(NodeControlDataList.get(i).getKid_stat());
                                if ((AUTO_ALL_OPEN <= kidStatus && kidStatus <= AUTO_NH3_OPEN)
                                        || kidStatus == AUTOMODEL_TMP_OPEN || kidStatus == AUTOMODEL_TMP_CLOSE
                                        || kidStatus == AUTO_MODEL_OPEN) {
                                    Model = AUTO_MODEL;
                                    break;
                                } else if (kidStatus == TIME_MODEL_CLOSE || kidStatus == TIME_MODEL_OPEN) {
                                    Model = TIME_MODEL;
                                    break;
                                } else {
                                    Model = 0;
                                }
                            }
                            MethodTools.farmDataList.get(parentPosition)
                                    .setCooling(NodeControlDataList);
                            ShowWindListView(NodeControlDataList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                dismissProgress();
            }
        };
    }

    /**
     * 请求web数据
     */
    private void RequestWebData() {
        jb = new JSONObject();
        try {
            switch (typePosition) {
                case 0:
                    open_all_btn.setVisibility(View.GONE);
                    close_all_btn.setVisibility(View.GONE);
                    List<NodeVideoData> mNodeVideoData = MethodTools.farmDataList.get(parentPosition).getVideoNodeList();
                    if (mNodeVideoData.size() >= 1)
                        VolleyStringRequest.stringRequest(CommandActivity.this, Path.videoHost + Path.URL_GET_CHANNEL +
                                "?videoNodeId=" + mNodeVideoData.get(0).getVideoNodeId(), Code.SUCCESS);
                    break;
                case 1:
                    getNodeListRequest(MethodTools.farmDataList.get(parentPosition).getFarm_id(), StringUtil.setNodeType(NodeType.WIND));
                    break;
                case 2:
                    if (parentPosition < MethodTools.farmDataList.size()) {
                        jb.put("farmid", MethodTools.farmDataList.get(parentPosition).getFarm_id());
                        jb.put("ndname", StringUtil.setNodeType(NodeType.COOLING));
                        doRegist(Path.host + Path.URL_GET_COOLING, jb, GET_COOLING_NODE);
                    }
                    break;
                case 3:
                    getNodeListRequest(MethodTools.farmDataList.get(parentPosition).getFarm_id(), StringUtil.setNodeType(NodeType.FEED));
                    break;
                case 4:
                    getNodeListRequest(MethodTools.farmDataList.get(parentPosition).getFarm_id(), StringUtil.setNodeType(NodeType.DUNG));
                    break;
                case 5:
                    getNodeListRequest(MethodTools.farmDataList.get(parentPosition).getFarm_id(), StringUtil.setNodeType(NodeType.LIGHT));
                    break;
                case 6:
                    getNodeListRequest(MethodTools.farmDataList.get(parentPosition).getFarm_id(), StringUtil.setNodeType(NodeType.HEATING));
                    break;
                case 7:
                    getNodeListRequest(MethodTools.farmDataList.get(parentPosition).getFarm_id(), StringUtil.setNodeType(NodeType.HUMIDIFICATION));
                    break;
                case 8:
                    getNodeListRequest(MethodTools.farmDataList.get(parentPosition).getFarm_id(), StringUtil.setNodeType(NodeType.FILL_LIGHT));
                    break;
                case 9:
                    getNodeListRequest(MethodTools.farmDataList.get(parentPosition).getFarm_id(), StringUtil.setNodeType(NodeType.STERILIZATION));
                    break;
            }
//            if (typePosition != 2 && typePosition != 0)
//                doRegist(Path.host + Path.URL_GET_Control, jb, NODE_LIST);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取节点数组的请求
     *
     * @param farmId   请求的农场的id
     * @param nodeType 请求的节点的类型
     */
    private void getNodeListRequest(String farmId, String nodeType) {
        if (parentPosition < MethodTools.farmDataList.size()) {
            try {
                jb.put("farmid", farmId);
                jb.put("ndname", nodeType);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            doRegist(Path.host + Path.URL_GET_Control, jb, NODE_LIST);
        }
    }

    /**
     * 显示风机的列表
     *
     * @param List
     */
    private void ShowWindListView(List<NodeControlData> List) {
        open_all_btn.setEnabled(true);
        open_all_btn.setClickable(true);
        close_all_btn.setEnabled(true);
        close_all_btn.setClickable(true);
        switch (typePosition) {
            case 1:// 风机
                windNodeadapter = new WindNodeGridAdapter(this, MethodTools.farmDataList.get(parentPosition).getNodeSideWind());
                windNodeList.setAdapter(windNodeadapter);
                break;
            case 2:// 降温
                windNodeadapter = new WindNodeGridAdapter(this, List);
                windNodeList.setAdapter(windNodeadapter);
                break;
            case 3:// 喂食
                windNodeadapter = new WindNodeGridAdapter(this, List);
                windNodeList.setAdapter(windNodeadapter);
                break;
            case 4:// 除粪
                windNodeadapter = new WindNodeGridAdapter(this, List);
                windNodeList.setAdapter(windNodeadapter);
                break;
            case 5:// 照明
                windNodeadapter = new WindNodeGridAdapter(this, List);
                windNodeList.setAdapter(windNodeadapter);
                break;
            case 6:// 加温
                windNodeadapter = new WindNodeGridAdapter(this, List);
                windNodeList.setAdapter(windNodeadapter);
                break;
            case 7:// 加湿
                windNodeadapter = new WindNodeGridAdapter(this, List);
                windNodeList.setAdapter(windNodeadapter);
                break;
            case 8:// 补光
                windNodeadapter = new WindNodeGridAdapter(this, List);
                windNodeList.setAdapter(windNodeadapter);
                break;
            case 9:// 杀菌
                windNodeadapter = new WindNodeGridAdapter(this, List);
                windNodeList.setAdapter(windNodeadapter);
                break;

        }
    }

    @Override
    protected void onClickLeft() {
        super.onClickLeft();
        this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
            case R.id.right_btn:
                if (getProgressBar().getVisibility() == View.INVISIBLE) {
                    Intent intent = new Intent(this, WarningSettingActivity.class);
                    intent.putExtra("typePosition", typePosition);
                    intent.putExtra("parentPosition", parentPosition);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "数据正在获取,请耐心等待", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.open_all_btn:
                if (Model == AUTO_MODEL) {
                    ShowModelDialog("自动模式", true);
                } else if (Model == TIME_MODEL) {
                    ShowModelDialog("定时模式", true);
                } else {
                    SendCmdToAllNode(HANDLE_MODEL_OPEN);
                }
                break;
            case R.id.close_all_btn:
                if (Model == AUTO_MODEL) {
                    ShowModelDialog("自动模式", false);
                } else if (Model == TIME_MODEL) {
                    ShowModelDialog("定时模式", false);
                } else {
                    SendCmdToAllNode(HANDLE_MODEL_CLOSE);
                }
                break;
        }
    }

    /**
     * 发送的指令
     *
     * @param cmd 指令
     */
    public void SendCmdToAllNode(int cmd) {
        JSONObject object = new JSONObject();
        open_all_btn.setClickable(false);
        open_all_btn.setEnabled(false);
        close_all_btn.setClickable(false);
        close_all_btn.setEnabled(false);
        if (MethodTools.farmDataList.size() > 0) {
            try {
                object.put("command", "0x12");
                JSONArray dataArray = new JSONArray();
                switch (typePosition) {
                    case 1:// 通风
                        for (int i = 0; i < MethodTools.farmDataList.get(parentPosition).getNodeSideWind().size(); i++) {
                            JSONObject dataOb = new JSONObject();
                            dataOb.put("gwid", MethodTools.farmDataList.get(parentPosition).getgw_Id());
                            dataOb.put("nodeid", MethodTools.farmDataList.get(parentPosition).getNodeSideWind().get(i).getNodeid());
                            dataOb.put("kid_stat", cmd);
                            dataOb.put("kid2_stat", cmd);
                            dataArray.put(dataOb);
                        }
                        break;
                    case 2:// 降温
                        for (int i = 0; i < MethodTools.farmDataList.get(parentPosition).getCooling().size(); i++) {
                            if (MethodTools.farmDataList.get(parentPosition).getCooling().get(i).getndname().equals(String.valueOf(COOLING_NODE_NAME))) {
                                JSONObject dataOb = new JSONObject();
                                dataOb.put("gwid", MethodTools.farmDataList.get(parentPosition).getgw_Id());
                                dataOb.put("nodeid", MethodTools.farmDataList.get(parentPosition).getCooling().get(i).getNodeid());
                                dataOb.put("kid_stat", cmd);
                                dataOb.put("kid2_stat", cmd);
                                dataArray.put(dataOb);
                            }
                        }
                        break;
                    case 3:// 喂食
                        for (int i = 0; i < MethodTools.farmDataList.get(parentPosition).getFeed().size(); i++) {
                            JSONObject dataOb = new JSONObject();
                            dataOb.put("gwid", MethodTools.farmDataList.get(parentPosition).getgw_Id());
                            dataOb.put("nodeid", MethodTools.farmDataList.get(parentPosition).getFeed().get(i).getNodeid());
                            dataOb.put("kid_stat", cmd);
                            dataOb.put("kid2_stat", cmd);
                            dataArray.put(dataOb);
                        }
                        break;
                    case 4:// 除粪
                        for (int i = 0; i < MethodTools.farmDataList.get(parentPosition).getDung().size(); i++) {
                            JSONObject dataOb = new JSONObject();
                            dataOb.put("gwid", MethodTools.farmDataList.get(parentPosition).getgw_Id());
                            dataOb.put("nodeid", MethodTools.farmDataList.get(parentPosition).getDung().get(i).getNodeid());
                            dataOb.put("kid_stat", cmd);
                            dataOb.put("kid2_stat", cmd);
                            dataArray.put(dataOb);
                        }
                        break;
                    case 5:// 照明
                        for (int i = 0; i < MethodTools.farmDataList.get(parentPosition).getLight().size(); i++) {
                            JSONObject dataOb = new JSONObject();
                            dataOb.put("gwid", MethodTools.farmDataList.get(parentPosition).getgw_Id());
                            dataOb.put("nodeid", MethodTools.farmDataList.get(parentPosition).getLight().get(i).getNodeid());
                            dataOb.put("kid_stat", cmd);
                            dataOb.put("kid2_stat", cmd);
                            dataArray.put(dataOb);
                        }
                        break;
                    case 6:// 加温
                        for (int i = 0; i < MethodTools.farmDataList.get(parentPosition).getHeating().size(); i++) {
                            JSONObject dataOb = new JSONObject();
                            dataOb.put("gwid", MethodTools.farmDataList.get(parentPosition).getgw_Id());
                            dataOb.put("nodeid", MethodTools.farmDataList.get(parentPosition).getHeating().get(i).getNodeid());
                            dataOb.put("kid_stat", cmd);
                            dataOb.put("kid2_stat", cmd);
                            dataArray.put(dataOb);
                        }
                        break;
                    case 7:// 加湿
                        for (int i = 0; i < MethodTools.farmDataList.get(parentPosition).getHumidification().size(); i++) {
                            JSONObject dataOb = new JSONObject();
                            dataOb.put("gwid", MethodTools.farmDataList.get(parentPosition).getgw_Id());
                            dataOb.put("nodeid", MethodTools.farmDataList.get(parentPosition).getHumidification().get(i).getNodeid());
                            dataOb.put("kid_stat", cmd);
                            dataOb.put("kid2_stat", cmd);
                            dataArray.put(dataOb);
                        }
                        break;
                    case 8:// 补光
                        for (int i = 0; i < MethodTools.farmDataList.get(parentPosition).getFillLight().size(); i++) {
                            JSONObject dataOb = new JSONObject();
                            dataOb.put("gwid", MethodTools.farmDataList.get(parentPosition).getgw_Id());
                            dataOb.put("nodeid", MethodTools.farmDataList.get(parentPosition).getFillLight().get(i).getNodeid());
                            dataOb.put("kid_stat", cmd);
                            dataOb.put("kid2_stat", cmd);
                            dataArray.put(dataOb);
                        }
                        break;
                    case 9:// 杀菌
                        for (int i = 0; i < MethodTools.farmDataList.get(parentPosition).getSterilization().size(); i++) {
                            JSONObject dataOb = new JSONObject();
                            dataOb.put("gwid", MethodTools.farmDataList.get(parentPosition).getgw_Id());
                            dataOb.put("nodeid", MethodTools.farmDataList.get(parentPosition).getSterilization().get(i).getNodeid());
                            dataOb.put("kid_stat", cmd);
                            dataOb.put("kid2_stat", cmd);
                            dataArray.put(dataOb);
                        }
                        break;
                }
                object.put("data", dataArray);
                doRegist(Path.host + Path.URL_COMMAND, object, WIND_CONTROL);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 显示更改风机名字的dialog
     */
    public void ShowDialog(final int position) {
        mCustomDialog = new CustomDialog();
        mCustomDialog.setTitle("更改名字");
        mCustomDialog.setTitleSize(18);
        edit = new EditText(this);
        edit.setTextSize(16);
        DisplayMetrics dm = getResources().getDisplayMetrics();//获取屏幕宽度
        int editHeight = (dm.widthPixels) / 6;
        edit.setHeight(editHeight);
        Drawable d = ContextCompat.getDrawable(CommandActivity.this, R.drawable.null_select);
        edit.setBackgroundDrawable(d);
        mCustomDialog.setOnDialogListener(new CustomDialog.OnDialogListener() {
            @Override
            public void sureListener() {
                if (!StringUtil.isEmpty(edit.getText().toString())) {
                    JSONObject object = new JSONObject();
                    try {
                        switch (typePosition) {
                            case 0:

                                break;
                            case 1:
                                object.put("nodeid", MethodTools.farmDataList.get(parentPosition).getNodeSideWind().get(position).getNodeid());
                                object.put("nodename", edit.getText());
                                object.put("nodedescribe", MethodTools.farmDataList.get(parentPosition).getNodeSideWind().get(position).getNode_describe());
                                break;
                            case 2:
                                object.put("nodeid", MethodTools.farmDataList.get(parentPosition).getCooling().get(position).getNodeid());
                                object.put("nodename", edit.getText());
                                object.put("nodedescribe", MethodTools.farmDataList.get(parentPosition).getCooling().get(position).getNode_describe());
                                break;
                            case 3:
                                object.put("nodeid", MethodTools.farmDataList.get(parentPosition).getFeed().get(position).getNodeid());
                                object.put("nodename", edit.getText());
                                object.put("nodedescribe", MethodTools.farmDataList.get(parentPosition).getFeed().get(position).getNode_describe());
                                break;
                            case 4:
                                object.put("nodeid", MethodTools.farmDataList.get(parentPosition).getDung().get(position).getNodeid());
                                object.put("nodename", edit.getText());
                                object.put("nodedescribe", MethodTools.farmDataList.get(parentPosition).getDung().get(position).getNode_describe());
                                break;
                            case 5:
                                object.put("nodeid", MethodTools.farmDataList.get(parentPosition).getLight().get(position).getNodeid());
                                object.put("nodename", edit.getText());
                                object.put("nodedescribe", MethodTools.farmDataList.get(parentPosition).getLight().get(position).getNode_describe());
                                break;
                            case 6:
                                object.put("nodeid", MethodTools.farmDataList.get(parentPosition).getHeating().get(position).getNodeid());
                                object.put("nodename", edit.getText());
                                object.put("nodedescribe", MethodTools.farmDataList.get(parentPosition).getHeating().get(position).getNode_describe());
                                break;
                            case 7:
                                object.put("nodeid", MethodTools.farmDataList.get(parentPosition).getHumidification().get(position).getNodeid());
                                object.put("nodename", edit.getText());
                                object.put("nodedescribe", MethodTools.farmDataList.get(parentPosition).getHumidification().get(position).getNode_describe());
                                break;
                            case 8:
                                object.put("nodeid", MethodTools.farmDataList.get(parentPosition).getFillLight().get(position).getNodeid());
                                object.put("nodename", edit.getText());
                                object.put("nodedescribe", MethodTools.farmDataList.get(parentPosition).getFillLight().get(position).getNode_describe());
                                break;
                            case 9:
                                object.put("nodeid", MethodTools.farmDataList.get(parentPosition).getSterilization().get(position).getNodeid());
                                object.put("nodename", edit.getText());
                                object.put("nodedescribe", MethodTools.farmDataList.get(parentPosition).getSterilization().get(position).getNode_describe());
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    doRegist(Path.host + Path.URL_UPDATE_NODENAME, object, MainActivity.UPDATE_NODENAME);
                } else {
                    Toast.makeText(getApplicationContext(), "名称不能为空", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void cancelListener() {
                mCustomDialog.DissMissDialog();
            }
        });

        mCustomDialog.setView(edit);
        mCustomDialog.show(getFragmentManager(), "TAG");
    }

    /**
     * 跳转界面
     *
     * @param position 根据是哪一个position跳转
     */
    public void StartIntent(int position) {
        Intent intent = new Intent(CommandActivity.this, WindControlActivity.class);
        intent.putExtra("gwid", MethodTools.farmDataList.get(parentPosition).getgw_Id());
        switch (typePosition) {
            case 1:
                intent.putExtra("node_id", MethodTools.farmDataList.get(parentPosition).getNodeSideWind().get(position).getNodeid());
                break;
            case 2:
                intent.putExtra("node_id", MethodTools.farmDataList.get(parentPosition).getCooling().get(position).getNodeid());
                break;
            case 3:// 喂食
                intent.putExtra("node_id", MethodTools.farmDataList.get(parentPosition).getFeed().get(position).getNodeid());
                break;
            case 4:// 除粪
                intent.putExtra("node_id", MethodTools.farmDataList.get(parentPosition).getDung().get(position).getNodeid());
                break;
            case 5:// 照明
                intent.putExtra("node_id", MethodTools.farmDataList.get(parentPosition).getLight().get(position).getNodeid());
                break;
            case 6:// 加温
                intent.putExtra("node_id", MethodTools.farmDataList.get(parentPosition).getHeating().get(position).getNodeid());
                break;
            case 7:// 加湿
                intent.putExtra("node_id", MethodTools.farmDataList.get(parentPosition).getHumidification().get(position).getNodeid());
                break;
            case 8:// 补光
                intent.putExtra("node_id", MethodTools.farmDataList.get(parentPosition).getFillLight().get(position).getNodeid());
                break;
            case 9:// 杀菌
                intent.putExtra("node_id", MethodTools.farmDataList.get(parentPosition).getSterilization().get(position).getNodeid());
                break;

        }
        intent.putExtra("parentPosition", parentPosition);
        intent.putExtra("position", position);
        intent.putExtra("typePosition", typePosition);
        startActivity(intent);
    }

    /**
     * 显示自动切换成手动的dialog
     */
    public void ShowModelDialog(String model_text, final boolean IsOpen) {
        CustomDialog mCustomDialog = new CustomDialog();
        mCustomDialog.setTitle("切换模式");
        mCustomDialog.setTitleSize(18);
        TextView txt = new TextView(this);
        txt.setWidth(MATCH_PARENT);
        DisplayMetrics dm = getResources().getDisplayMetrics();//获取屏幕宽度
        int txtHeight = (dm.widthPixels) / 4;
        txt.setHeight(txtHeight);
        txt.setTextSize(16);
        //txt.setHeight(DensityUtil.px2dip(this, 200));
        txt.setGravity(Gravity.CENTER);
        txt.setText("现在是" + model_text + ",是否切换成手动模式?");
        mCustomDialog.setOnDialogListener(new CustomDialog.OnDialogListener() {
            @Override
            public void sureListener() {
                if (IsOpen) {
                    SendCmdToAllNode(HANDLE_MODEL_OPEN);
                } else {
                    SendCmdToAllNode(HANDLE_MODEL_CLOSE);
                }
            }

            @Override
            public void cancelListener() {
//                mCustomDialog.DissMissDialog();
            }
        });
        mCustomDialog.setView(txt);
        mCustomDialog.show(getFragmentManager(), "TAG");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (HeartService != null) {
            HeartService.shutdownNow();
        }
        MethodTools.handlerJson.removeCallbacksAndMessages(null);
    }
}