package com.kys26.webthings.command;

import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.kys26.webthings.fragment.GWFragment;
import com.kys26.webthings.fragment.SwitchFragment;
import com.kys26.webthings.fragment.TimingFragment;
import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.interfac.OnValidateSwitchListener;
import com.kys26.webthings.main.BaseActivity;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.model.AlarmData;
import com.kys26.webthings.model.NodeControlData;
import com.kys26.webthings.model.NodeTimeData;
import com.kys26.webthings.util.DataUtil;
import com.kys26.webthings.util.MyToast;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.kys26.webthings.httpconstant.Code.AUTOMODEL_NH3_CLOSE;
import static com.kys26.webthings.httpconstant.Code.AUTOMODEL_NH3_OPEN;
import static com.kys26.webthings.httpconstant.Code.AUTOMODEL_TMP_CLOSE;
import static com.kys26.webthings.httpconstant.Code.AUTOMODEL_TMP_OPEN;
import static com.kys26.webthings.httpconstant.Code.AUTO_FAN_CLOSE;
import static com.kys26.webthings.httpconstant.Code.AUTO_FAN_OPEN;
import static com.kys26.webthings.httpconstant.Code.HANDLE_MODEL_CLOSE;
import static com.kys26.webthings.httpconstant.Code.HANDLE_MODEL_OPEN;
import static com.kys26.webthings.httpconstant.Code.TIME_MODEL_CLOSE;
import static com.kys26.webthings.httpconstant.Code.TIME_MODEL_OPEN;
import static com.kys26.webthings.httpconstant.Code.TIMING_CLOSE;
import static com.kys26.webthings.httpconstant.Code.TIMING_OPEN;
import static com.zhangyx.MyGestureLock.R.id.timing_switch;


/**
 * @author 李赛鹏
 * @class Created by kys_9 on 2017/5/10.
 */

public class WindControlActivity extends BaseActivity implements View.OnClickListener {
    //    @InjectView(R.id.control_type_txt)
    public TextView mControlTypeTxt;//控制模式
    private FrameLayout left_btn, right_btn, content;
    private TextView title_txt, wind_control_txt;
    private SwitchFragment switchFragment;
    private TimingFragment timingFragment;
    private RelativeLayout timer_layout, switch_layout;
    private ImageView timer_layout_img, switch_layout_img, switch_fan_img;
    private TextView timer_layout_txt, switch_layout_txt;
    public String nodeid;//节点id
    public int position;//点击第几个grid
    public int typePosition;//控制节点类型
    public int parentPosition;//第几个养殖场，养殖场保存在MethodTools.farmDataList里
    private List<NodeTimeData> mNodeTimeDataList;//节点下定时列表
    private List<NodeControlData> mNodeControlData = new ArrayList<>();//节点开关的model
    private String TAG;
    //用来验证信息是否正确的handler
    Handler ValidateHandler;
    private List<AlarmData> alarmDataList = new ArrayList<>();
    RecyclerView.LayoutManager manager;//recyclerview的布局管理器
    private ScheduledExecutorService modelService;//模式的定时器
    String kid_stat;//节点状态
    private ScheduledExecutorService HeartService;//心跳service

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        TAG = getClass().getName();
        setEnableRefresh(false, null);
        //在这里获取到上一个界面获取到的数据
        nodeid = intent.getStringExtra("node_id");
        position = intent.getIntExtra("position", 0);
        typePosition = intent.getIntExtra("typePosition", 0);
        Log.e(TAG, "typePosition:" + typePosition);
        parentPosition = intent.getIntExtra("parentPosition", 0);
        initView();
        setTitle(GWFragment.names[typePosition]);
        setRightBtnVisible(false);
        addRightWidget(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initHandler();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MethodTools.handlerJson = null;
    }

    /**
     * 初始化handler
     */
    private void initHandler() {
        //重写Handler中的message方法，获取数据后进行异步加载
        MethodTools.handlerJson = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.e(TAG, "what:" + msg.what);
                if (msg.what == CONTROL_STATE) {//判断模式的状态,更改模式的ui界面
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        JSONObject jsonObject1 = jsonObject.getJSONObject("gDevice");
                        refreshAllModel();
                        kid_stat = NodeControlData.analysis(jsonObject1).getkid_stat();
                        if (kid_stat.equals(String.valueOf(HANDLE_MODEL_CLOSE))) {
                            ChangeModel("手动控制", false);
                        } else if (kid_stat.equals(String.valueOf(HANDLE_MODEL_OPEN))) {
                            ChangeModel("手动控制", true);
                        } else if (kid_stat.equals(String.valueOf(AUTOMODEL_NH3_OPEN))
                                || kid_stat.equals(String.valueOf(AUTOMODEL_TMP_OPEN))) {
                            mControlTypeTxt.setText("自动控制");
                        } else if (kid_stat.equals(String.valueOf(AUTOMODEL_NH3_CLOSE))
                                || kid_stat.equals(String.valueOf(AUTOMODEL_TMP_CLOSE))) {
                            mControlTypeTxt.setText("自动控制");
                        } else if (kid_stat.equals(String.valueOf(TIME_MODEL_OPEN))) {
                            ChangeModel("定时控制", true);
                        } else if (kid_stat.equals(String.valueOf(TIME_MODEL_CLOSE))) {
                            ChangeModel("定时控制", false);
                        } else if (kid_stat.equals(String.valueOf(TIMING_CLOSE))) {
                            mControlTypeTxt.setText("手动控制");
                        } else if (kid_stat.equals(String.valueOf(TIMING_OPEN))) {
                            mControlTypeTxt.setText("手动控制");
                        }
                        for (int i = 0; i < AUTO_FAN_OPEN.length; i++) {
                            if (Integer.valueOf(kid_stat) == AUTO_FAN_OPEN[i]) {
                                if (modelService == null)
                                    ModelTask();
                                ChangeModel("自动控制", true);
                                break;
                            }
                        }
                        for (int i = 0; i < AUTO_FAN_CLOSE.length; i++) {
                            if (Integer.valueOf(kid_stat) == AUTO_FAN_CLOSE[i]) {
                                if (modelService == null)
                                    ModelTask();
                                ChangeModel("自动控制", false);
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == GET_ALL_TIME) {//获取所有定时的handler,
                    switch (typePosition) {
                        case 1:
                            try {
                                MethodTools.farmDataList.get(parentPosition).getNodeSideWind()
                                        .get(position).setNodeTimeDataList(NodeTimeData.getNodeTimeData(new JSONArray(msg.obj.toString())));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mNodeTimeDataList = MethodTools.farmDataList.get(parentPosition).getNodeSideWind()
                                    .get(position).getNodeTimeDataList();
                            break;
                        case 2:
                            try {
                                MethodTools.farmDataList.get(parentPosition).getCooling()
                                        .get(position).setNodeTimeDataList(NodeTimeData.getNodeTimeData(new JSONArray(msg.obj.toString())));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mNodeTimeDataList = MethodTools.farmDataList.get(parentPosition).getCooling()
                                    .get(position).getNodeTimeDataList();
                            break;
                        case 3:
                            try {
                                MethodTools.farmDataList.get(parentPosition).getFeed()
                                        .get(position).setNodeTimeDataList(NodeTimeData.getNodeTimeData(new JSONArray(msg.obj.toString())));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mNodeTimeDataList = MethodTools.farmDataList.get(parentPosition).getFeed()
                                    .get(position).getNodeTimeDataList();
                            break;
                        case 4:
                            try {
                                MethodTools.farmDataList.get(parentPosition).getDung()
                                        .get(position).setNodeTimeDataList(NodeTimeData.getNodeTimeData(new JSONArray(msg.obj.toString())));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mNodeTimeDataList = MethodTools.farmDataList.get(parentPosition).getDung()
                                    .get(position).getNodeTimeDataList();
                            break;
                        case 5:
                            try {
                                MethodTools.farmDataList.get(parentPosition).getLight()
                                        .get(position).setNodeTimeDataList(NodeTimeData.getNodeTimeData(new JSONArray(msg.obj.toString())));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mNodeTimeDataList = MethodTools.farmDataList.get(parentPosition).getLight()
                                    .get(position).getNodeTimeDataList();
                            break;
                        case 6:
                            try {
                                MethodTools.farmDataList.get(parentPosition).getHeating()
                                        .get(position).setNodeTimeDataList(NodeTimeData.getNodeTimeData(new JSONArray(msg.obj.toString())));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mNodeTimeDataList = MethodTools.farmDataList.get(parentPosition).getHeating()
                                    .get(position).getNodeTimeDataList();
                            break;
                        case 7:
                            try {
                                MethodTools.farmDataList.get(parentPosition).getHumidification()
                                        .get(position).setNodeTimeDataList(NodeTimeData.getNodeTimeData(new JSONArray(msg.obj.toString())));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mNodeTimeDataList = MethodTools.farmDataList.get(parentPosition).getHumidification()
                                    .get(position).getNodeTimeDataList();
                            break;
                        case 8:
                            try {
                                MethodTools.farmDataList.get(parentPosition).getFillLight()
                                        .get(position).setNodeTimeDataList(NodeTimeData.getNodeTimeData(new JSONArray(msg.obj.toString())));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mNodeTimeDataList = MethodTools.farmDataList.get(parentPosition).getFillLight()
                                    .get(position).getNodeTimeDataList();
                            break;
                        case 9:
                            try {
                                MethodTools.farmDataList.get(parentPosition).getSterilization()
                                        .get(position).setNodeTimeDataList(NodeTimeData.getNodeTimeData(new JSONArray(msg.obj.toString())));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mNodeTimeDataList = MethodTools.farmDataList.get(parentPosition).getSterilization()
                                    .get(position).getNodeTimeDataList();
                            break;

                        default:
                            break;
                    }
                    DataUtil.sorting(mNodeTimeDataList);
                    timingFragment.changeUI(mNodeTimeDataList);
                } else if (msg.what == WIND_CONTROL) {//获取节点的状态
                    GetFanState();
                    return;
                } else if (msg.what == SAVE_TIMING) {//保存定时时间的handler
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        if (jsonObject.get("Status").equals("Success")) {
                            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            alarmDataList.add(new AlarmData());
                            alarmDataList.get(alarmDataList.size() - 1).setAlarmManager(am);
                            Toast.makeText(getApplicationContext(), "新建成功", Toast.LENGTH_SHORT).show();
                            getAllTiming();
                        } else {
                            Toast.makeText(getApplicationContext(), "新建失败，可能是网络问题,请重新尝试", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == UNDATE_TIME) {//更改定时时间的handler
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        if (jsonObject.get("Status").equals("Success")) {
                            Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
                            getAllTiming();
                        } else {
                            Toast.makeText(getApplicationContext(), "修改失败，请重新尝试", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == DELETE_TIME) {//删除定时的handler
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(msg.obj.toString());
                        if (jsonObject.get("Status").equals("Success")) {
                            getAllTiming();
                            Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == CONTROL_TIME) {//控制定时的Handler
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(msg.obj.toString());
                        if (jsonObject.get("Status").equals("Success")) {
                            Log.e(TAG, "控制成功");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == TIMING_VALIDATE) {//定时的验证
                    JSONObject object = null;
                    int kid1_stat = 0, kid2_stat = 0;
                    String id = "";
                    try {
                        object = new JSONObject(msg.obj.toString());
                        kid1_stat = object.getInt("kid1Stat");
                        kid2_stat = object.getInt("kid2Stat");
                        id = object.getString("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    switch (typePosition) {
                        case 1:
                            mNodeTimeDataList = MethodTools.farmDataList.get(parentPosition).getNodeSideWind().get(position).getNodeTimeDataList();
                            break;
                        case 2:
                            mNodeTimeDataList = MethodTools.farmDataList.get(parentPosition).getCooling().get(position).getNodeTimeDataList();
                            break;
                        case 3:
                            mNodeTimeDataList = MethodTools.farmDataList.get(parentPosition).getFeed().get(position).getNodeTimeDataList();
                            break;
                        case 4:
                            mNodeTimeDataList = MethodTools.farmDataList.get(parentPosition).getDung().get(position).getNodeTimeDataList();
                            break;
                        case 5:
                            mNodeTimeDataList = MethodTools.farmDataList.get(parentPosition).getLight().get(position).getNodeTimeDataList();
                            break;
                        case 6:
                            mNodeTimeDataList = MethodTools.farmDataList.get(parentPosition).getHeating().get(position).getNodeTimeDataList();
                            break;
                        case 7:
                            mNodeTimeDataList = MethodTools.farmDataList.get(parentPosition).getHumidification().get(position).getNodeTimeDataList();
                            break;
                        case 8:
                            mNodeTimeDataList = MethodTools.farmDataList.get(parentPosition).getFillLight().get(position).getNodeTimeDataList();
                            break;
                        case 9:
                            mNodeTimeDataList = MethodTools.farmDataList.get(parentPosition).getSterilization().get(position).getNodeTimeDataList();
                            break;
                    }
                    int position = 0;
                    for (int i = 0; i < mNodeTimeDataList.size(); i++) {
                        if (mNodeTimeDataList.get(i).getId().equals(id)) {
                            position = i;
                        }
                    }
                    //获取recycleview的布局管理器。
                    manager = timingFragment.timinglist.getLayoutManager();
                    //通过布局管理器获取到固定Position的item;
                    View view = manager.findViewByPosition(position);
                    //根据固定的position的item转成linearlayout;
                    LinearLayout ll = (LinearLayout) view;
                    //强转
                    Switch timing_Switch = (Switch) ll.findViewById(timing_switch);
                    //用来判断获取到kid1_stat和当前的kid1_stat状态是否一致。
                    Log.e(TAG, "现在定时的状态:" + kid1_stat + "得到数据的定时状态" + mNodeTimeDataList.get(position).getKid1Stat());
                    int isTimingOpen;
                    if (timing_Switch.isChecked()) {
                        isTimingOpen = TIMING_OPEN;
                    } else {
                        isTimingOpen = TIMING_CLOSE;
                    }
                    if (kid1_stat != isTimingOpen) {//获取到的定时状态已更改
                        if (kid1_stat == TIMING_OPEN) {
                            timing_Switch.setChecked(true);
                        } else {
                            timing_Switch.setChecked(false);
                        }
                        timing_Switch.setClickable(true);
                        ll.findViewById(R.id.progressbar).setVisibility(View.GONE);
                        if (mNodeTimeDataList.get(position).getTimingService() != null)
                            mNodeTimeDataList.get(position).getTimingService().shutdownNow();
                        if (mNodeTimeDataList.get(position).isInTime())
                            InTimeValidateFanState();
                        //只用更新状态,不用更新其他的，否则会出问题。(不要更新Model)
                        mNodeTimeDataList.get(position).setKid1Stat(kid1_stat);
                        mNodeTimeDataList.get(position).setKid2Stat(kid2_stat);
                        mNodeTimeDataList.get(position).setTimingWhat(0);
//                        mNodeTimeDataList.set(position, NodeTimeData.getNodeTimeData(object));
                        Log.e(TAG, "定时数据对应,结束");
                    } else {//获取到的定时状态未更改
                        if (mNodeTimeDataList.get(position).getTimingWhat() == 2) {
                            Log.e(TAG, "定时停止验证");
                            if (mNodeTimeDataList.get(position).getTimingService() != null)
                                mNodeTimeDataList.get(position).getTimingService().shutdownNow();
                            mNodeTimeDataList.set(position, NodeTimeData.getNodeTimeData(object));
                            timing_Switch.setClickable(true);
                            ll.findViewById(R.id.progressbar).setVisibility(View.GONE);
                            position++;
                            Toast.makeText(getApplicationContext(), "第" + position + "个定时验证超时", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "定时数据不对应");
                            int index = mNodeTimeDataList.get(position).getTimingWhat();
                            index++;
                            mNodeTimeDataList.get(position).setTimingWhat(index);
                        }
                    }
                } else if (msg.what == SWITCH_VALIDATE) {//节点的开关状态的验证
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        JSONObject jsonObject1 = jsonObject.getJSONObject("gDevice");
                        int SwitchIndex = 0;
                        switch (typePosition) {
                            case 1:
                                mNodeControlData = MethodTools.farmDataList.get(parentPosition).getNodeSideWind();
                                SwitchIndex = MethodTools.farmDataList.get(parentPosition).getNodeSideWind().get(position).getSwitchIndex();
                                SwitchIndex++;
                                MethodTools.farmDataList.get(parentPosition).getNodeSideWind().get(position).setSwitchIndex(SwitchIndex);
                                break;
                            case 2:
                                mNodeControlData = MethodTools.farmDataList.get(parentPosition).getCooling();
                                SwitchIndex = MethodTools.farmDataList.get(parentPosition).getCooling().get(position).getSwitchIndex();
                                SwitchIndex++;
                                MethodTools.farmDataList.get(parentPosition).getCooling().get(position).setSwitchIndex(SwitchIndex);
                                break;
                            case 3:
                                mNodeControlData = MethodTools.farmDataList.get(parentPosition).getFeed();
                                SwitchIndex = MethodTools.farmDataList.get(parentPosition).getFeed().get(position).getSwitchIndex();
                                SwitchIndex++;
                                MethodTools.farmDataList.get(parentPosition).getFeed().get(position).setSwitchIndex(SwitchIndex);
                                break;
                            case 4:
                                mNodeControlData = MethodTools.farmDataList.get(parentPosition).getDung();
                                SwitchIndex = MethodTools.farmDataList.get(parentPosition).getDung().get(position).getSwitchIndex();
                                SwitchIndex++;
                                MethodTools.farmDataList.get(parentPosition).getDung().get(position).setSwitchIndex(SwitchIndex);
                                break;
                            case 5:
                                mNodeControlData = MethodTools.farmDataList.get(parentPosition).getLight();
                                SwitchIndex = MethodTools.farmDataList.get(parentPosition).getLight().get(position).getSwitchIndex();
                                SwitchIndex++;
                                MethodTools.farmDataList.get(parentPosition).getLight().get(position).setSwitchIndex(SwitchIndex);
                                break;
                            case 6:
                                mNodeControlData = MethodTools.farmDataList.get(parentPosition).getHeating();
                                SwitchIndex = MethodTools.farmDataList.get(parentPosition).getHeating().get(position).getSwitchIndex();
                                SwitchIndex++;
                                MethodTools.farmDataList.get(parentPosition).getHeating().get(position).setSwitchIndex(SwitchIndex);
                                break;
                            case 7:
                                mNodeControlData = MethodTools.farmDataList.get(parentPosition).getHumidification();
                                SwitchIndex = MethodTools.farmDataList.get(parentPosition).getHumidification().get(position).getSwitchIndex();
                                SwitchIndex++;
                                MethodTools.farmDataList.get(parentPosition).getHumidification().get(position).setSwitchIndex(SwitchIndex);
                                break;
                            case 8:
                                mNodeControlData = MethodTools.farmDataList.get(parentPosition).getFillLight();
                                SwitchIndex = MethodTools.farmDataList.get(parentPosition).getFillLight().get(position).getSwitchIndex();
                                SwitchIndex++;
                                MethodTools.farmDataList.get(parentPosition).getFillLight().get(position).setSwitchIndex(SwitchIndex);
                                break;
                            case 9:
                                mNodeControlData = MethodTools.farmDataList.get(parentPosition).getSterilization();
                                SwitchIndex = MethodTools.farmDataList.get(parentPosition).getSterilization().get(position).getSwitchIndex();
                                SwitchIndex++;
                                MethodTools.farmDataList.get(parentPosition).getSterilization().get(position).setSwitchIndex(SwitchIndex);
                                break;
                        }
//                        switch (typePosition) {
//                            case 1:
//                                break;
//                            case 2:
//
//                                break;
//                        }
                        GetFanState();
                        //数据已更改
                        if (NodeControlData.analysis(jsonObject1).getkid_stat().equals(String.valueOf(HANDLE_MODEL_OPEN))) {
                            switchFragment.isDataChanged(true, position);
                        }
                        //数据未更改
                        else if (NodeControlData.analysis(jsonObject1).getkid_stat().equals(String.valueOf(HANDLE_MODEL_CLOSE))) {
                            Log.e(TAG, "请求开关验证");
                            switchFragment.isDataChanged(false, position);
                        }
                        //5次验证过后数据仍未更改
                        if (SwitchIndex == 5) {
                            Toast.makeText(WindControlActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                            switch (typePosition) {
                                case 1:
                                    MethodTools.farmDataList.get(parentPosition).getNodeSideWind().get(position).setSwitchIndex(0);
                                    while (!MethodTools.farmDataList.get(parentPosition).getNodeSideWind().get(position).getSwitchService().isShutdown())
                                        MethodTools.farmDataList.get(parentPosition).getNodeSideWind().get(position).getSwitchService().shutdownNow();
                                    break;
                                case 2:
                                    MethodTools.farmDataList.get(parentPosition).getCooling().get(position).setSwitchIndex(0);
                                    while (!MethodTools.farmDataList.get(parentPosition).getCooling().get(position).getSwitchService().isShutdown())
                                        MethodTools.farmDataList.get(parentPosition).getCooling().get(position).getSwitchService().shutdown();
                                    break;
                                case 3:
                                    MethodTools.farmDataList.get(parentPosition).getFeed().get(position).setSwitchIndex(0);
                                    while (!MethodTools.farmDataList.get(parentPosition).getFeed().get(position).getSwitchService().isShutdown())
                                        MethodTools.farmDataList.get(parentPosition).getFeed().get(position).getSwitchService().shutdown();
                                    break;
                                case 4:
                                    MethodTools.farmDataList.get(parentPosition).getDung().get(position).setSwitchIndex(0);
                                    while (!MethodTools.farmDataList.get(parentPosition).getDung().get(position).getSwitchService().isShutdown())
                                        MethodTools.farmDataList.get(parentPosition).getDung().get(position).getSwitchService().shutdown();
                                    break;
                                case 5:
                                    MethodTools.farmDataList.get(parentPosition).getLight().get(position).setSwitchIndex(0);
                                    while (!MethodTools.farmDataList.get(parentPosition).getLight().get(position).getSwitchService().isShutdown())
                                        MethodTools.farmDataList.get(parentPosition).getLight().get(position).getSwitchService().shutdown();
                                    MethodTools.farmDataList.get(parentPosition).getLight().get(position).setSwitchService(null);
                                    break;
                                case 6:
                                    MethodTools.farmDataList.get(parentPosition).getHeating().get(position).setSwitchIndex(0);
                                    while (!MethodTools.farmDataList.get(parentPosition).getHeating().get(position).getSwitchService().isShutdown())
                                        MethodTools.farmDataList.get(parentPosition).getHeating().get(position).getSwitchService().shutdown();
                                    MethodTools.farmDataList.get(parentPosition).getHeating().get(position).setSwitchService(null);
                                    break;
                                case 7:
                                    MethodTools.farmDataList.get(parentPosition).getHumidification().get(position).setSwitchIndex(0);
                                    while (!MethodTools.farmDataList.get(parentPosition).getHumidification().get(position).getSwitchService().isShutdown())
                                        MethodTools.farmDataList.get(parentPosition).getHumidification().get(position).getSwitchService().shutdown();
                                    MethodTools.farmDataList.get(parentPosition).getHumidification().get(position).setSwitchService(null);
                                    break;
                                case 8:
                                    MethodTools.farmDataList.get(parentPosition).getFillLight().get(position).setSwitchIndex(0);
                                    while (!MethodTools.farmDataList.get(parentPosition).getFillLight().get(position).getSwitchService().isShutdown())
                                        MethodTools.farmDataList.get(parentPosition).getFillLight().get(position).getSwitchService().shutdown();
                                    MethodTools.farmDataList.get(parentPosition).getFillLight().get(position).setSwitchService(null);
                                    break;
                                case 9:
                                    MethodTools.farmDataList.get(parentPosition).getSterilization().get(position).setSwitchIndex(0);
                                    while (!MethodTools.farmDataList.get(parentPosition).getSterilization().get(position).getSwitchService().isShutdown())
                                        MethodTools.farmDataList.get(parentPosition).getSterilization().get(position).getSwitchService().shutdown();
                                    MethodTools.farmDataList.get(parentPosition).getSterilization().get(position).setSwitchService(null);
                                    break;
                            }
                            switchFragment.StopSwitchTiming();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dismissProgress();
                } else if (msg.what == INTIME_VALIDATE_FANSTATE) {//倘若现在是定时时间内，去控制定时的开关
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        JSONObject jsonObject1 = jsonObject.getJSONObject("gDevice");
                        refreshAllModel();
                        kid_stat = NodeControlData.analysis(jsonObject1).getkid_stat();
                        Log.e(TAG, "在定时内,控制定时开关");
                        if (kid_stat.equals(String.valueOf(TIMING_CLOSE))) {
//                            mControlTypeTxt.setText("手动控制");
                            ChangeModel("手动控制", false);
                        } else if (kid_stat.equals(String.valueOf(TIMING_OPEN))) {
                            ChangeModel("定时控制", false);
//                            mControlTypeTxt.setText("手动控制");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == Code.FAILURE) {
                    //消除等待进度弹出框
                    MyToast.makeImgAndTextToast(WindControlActivity.this, getResources().getDrawable(R.drawable.tips_sry),
                            "请求失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
                    MethodTools.state = false;
                }
                dismissProgress();
            }
        };
    }

    /**
     * 重新获取所有节点的model
     */
    private void refreshAllModel() {
        switch (typePosition) {
            case 1:
                mNodeControlData = MethodTools.farmDataList.get(parentPosition).getNodeSideWind();
                break;
            case 2:
                mNodeControlData = MethodTools.farmDataList.get(parentPosition).getCooling();
                break;
            case 3:
                mNodeControlData = MethodTools.farmDataList.get(parentPosition).getFeed();
                break;
            case 4:
                mNodeControlData = MethodTools.farmDataList.get(parentPosition).getDung();
                break;
            case 5:
                mNodeControlData = MethodTools.farmDataList.get(parentPosition).getLight();
                break;
            case 6:
                mNodeControlData = MethodTools.farmDataList.get(parentPosition).getHeating();
                break;
            case 7:
                mNodeControlData = MethodTools.farmDataList.get(parentPosition).getHumidification();
                break;
            case 8:
                mNodeControlData = MethodTools.farmDataList.get(parentPosition).getFillLight();
                break;
            case 9:
                mNodeControlData = MethodTools.farmDataList.get(parentPosition).getSterilization();
                break;
        }
    }

    /**
     * 改变手动(自动,定时)等模式
     *
     * @param isFanOpen 风扇是否打开
     * @param modelName 模式名字
     */
    private void ChangeModel(String modelName, boolean isFanOpen) {
        if (modelService != null) {
            modelService.shutdownNow();
        }
        ControlFanAnimation(position, isFanOpen);
        mControlTypeTxt.setText(modelName);
        switch (typePosition) {
            case 1:
                MethodTools.farmDataList.get(parentPosition).getNodeSideWind().get(position).setFanOpen(isFanOpen);
                break;
            case 2:
                MethodTools.farmDataList.get(parentPosition).getCooling().get(position).setFanOpen(isFanOpen);
                break;
            case 3:
                MethodTools.farmDataList.get(parentPosition).getFeed().get(position).setFanOpen(isFanOpen);
                break;
            case 4:
                MethodTools.farmDataList.get(parentPosition).getDung().get(position).setFanOpen(isFanOpen);
                break;
            case 5:
                MethodTools.farmDataList.get(parentPosition).getLight().get(position).setFanOpen(isFanOpen);
                break;
            case 6:
                MethodTools.farmDataList.get(parentPosition).getHeating().get(position).setFanOpen(isFanOpen);
                break;
            case 7:
                MethodTools.farmDataList.get(parentPosition).getHumidification().get(position).setFanOpen(isFanOpen);
                break;
            case 8:
                MethodTools.farmDataList.get(parentPosition).getFillLight().get(position).setFanOpen(isFanOpen);
                break;
            case 9:
                MethodTools.farmDataList.get(parentPosition).getSterilization().get(position).setFanOpen(isFanOpen);
                break;
        }
        switchFragment.changeUI();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_windcontrol;
    }

    /**
     * 初始化视图
     */
    private void initView() {
        left_btn = (FrameLayout) findViewById(R.id.left_btn);
        right_btn = (FrameLayout) findViewById(R.id.right_btn);
        title_txt = (TextView) findViewById(R.id.title_text);
        content = (FrameLayout) findViewById(R.id.content);
        mControlTypeTxt = (TextView) findViewById(R.id.control_type_txt);
        wind_control_txt = (TextView) findViewById(R.id.wind_control_txt);
        left_btn.setOnClickListener(this);
        timer_layout = (RelativeLayout) findViewById(R.id.timer_layout);
        switch_layout = (RelativeLayout) findViewById(R.id.switch_layout);
        timer_layout_img = (ImageView) findViewById(R.id.timer_layout_img);
        switch_layout_img = (ImageView) findViewById(R.id.switch_layout_img);
        switch_layout_txt = (TextView) findViewById(R.id.switch_layout_txt);
        timer_layout_txt = (TextView) findViewById(R.id.timer_layout_txt);
        switch_fan_img = (ImageView) findViewById(R.id.wind_control_img);
        switch_fan_img.setBackgroundResource(GWFragment.gifView[typePosition]);
        switch (typePosition) {
            case 1://侧窗通风
                wind_control_txt.setText(MethodTools.farmDataList.get(parentPosition).getNodeSideWind()
                        .get(position).getDeviceName());
                break;
            case 2:
                wind_control_txt.setText(MethodTools.farmDataList.get(parentPosition).getCooling()
                        .get(position).getDeviceName());
                break;
            case 3:
                wind_control_txt.setText(MethodTools.farmDataList.get(parentPosition).getFeed()
                        .get(position).getDeviceName());
                break;
            case 4:
                wind_control_txt.setText(MethodTools.farmDataList.get(parentPosition).getDung()
                        .get(position).getDeviceName());
                break;
            case 5:
                wind_control_txt.setText(MethodTools.farmDataList.get(parentPosition).getLight()
                        .get(position).getDeviceName());
                break;
            case 6:
                wind_control_txt.setText(MethodTools.farmDataList.get(parentPosition).getHeating()
                        .get(position).getDeviceName());
                break;
            case 7:
                wind_control_txt.setText(MethodTools.farmDataList.get(parentPosition).getHumidification()
                        .get(position).getDeviceName());
                break;
            case 8:
                wind_control_txt.setText(MethodTools.farmDataList.get(parentPosition).getFillLight()
                        .get(position).getDeviceName());
                break;
            case 9:
                wind_control_txt.setText(MethodTools.farmDataList.get(parentPosition).getSterilization()
                        .get(position).getDeviceName());
                break;
        }
        initValidateHandler();
        HeartTask();
        initFragment();
        switchFragment.setOnValidateSwitchListener(new OnValidateSwitchListener() {
            @Override
            public void onValidateSwitch() {
                SwitchTask();
            }
        });
    }

    /**
     * 获取全部定时
     */
    public void getAllTiming() {
        JSONObject jb = new JSONObject();
        try {
            jb.put("deviceId", nodeid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        doRegist(Path.host + Path.URL_QUERY_TIME, jb, BaseActivity.GET_ALL_TIME);
    }

    /**
     * 初始化验证的handler
     */
    private void initValidateHandler() {
        ValidateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    //现在不在定时内，正常验证定时的状态;
                    case 0:
                        ValidateTimingState((int) msg.obj);
                        break;
                    //获取验证风扇的状态
                    case 1:
                        ValidateFanState();
                        break;
                    //获取风扇的状态
                    case 2:
                        GetFanState();
                        break;
                    case 3:

                        break;
                }
            }
        };
    }

    /**
     * 验证定时状态
     *
     * @param timingposition
     */
    public void ValidateTimingState(int timingposition) {
        JSONObject jb = new JSONObject();
        switch (typePosition) {
            case 1:
                if (MethodTools.farmDataList.get(parentPosition).getNodeSideWind().get(position).getNodeTimeDataList() != null) {
                    try {
                        jb.put("id", MethodTools.farmDataList.get(parentPosition).getNodeSideWind().get(position).getNodeTimeDataList().get(timingposition).getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                try {
                    jb.put("id", MethodTools.farmDataList.get(parentPosition).getCooling().get(position).getNodeTimeDataList().get(timingposition).getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                try {
                    jb.put("id", MethodTools.farmDataList.get(parentPosition).getFeed().get(position).getNodeTimeDataList().get(timingposition).getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                try {
                    jb.put("id", MethodTools.farmDataList.get(parentPosition).getDung().get(position).getNodeTimeDataList().get(timingposition).getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 5:
                try {
                    jb.put("id", MethodTools.farmDataList.get(parentPosition).getLight().get(position).getNodeTimeDataList().get(timingposition).getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 6:
                try {
                    jb.put("id", MethodTools.farmDataList.get(parentPosition).getHeating().get(position).getNodeTimeDataList().get(timingposition).getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 7:
                try {
                    jb.put("id", MethodTools.farmDataList.get(parentPosition).getHumidification().get(position).getNodeTimeDataList().get(timingposition).getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 8:
                try {
                    jb.put("id", MethodTools.farmDataList.get(parentPosition).getFillLight().get(position).getNodeTimeDataList().get(timingposition).getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 9:
                try {
                    jb.put("id", MethodTools.farmDataList.get(parentPosition).getSterilization().get(position).getNodeTimeDataList().get(timingposition).getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        doRegist(Path.host + Path.URL_GET_TIME_STATE, jb, BaseActivity.TIMING_VALIDATE);
    }

    /**
     * 验证节点的状态
     */
    private void ValidateFanState() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nodeId", nodeid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "请求开关验证");
        doRegist(Path.host + Path.URL_GRT_CONTROLER_STATE, jsonObject, SWITCH_VALIDATE);
    }

    /**
     * 得到节点的状态
     */
    public void GetFanState() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nodeId", nodeid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "请求开关验证");
        doRegist(Path.host + Path.URL_GRT_CONTROLER_STATE, jsonObject, CONTROL_STATE);
    }

    /**
     * 在定时内,需要去改变定时开关，此时风扇也需要跟着改,所以需要验证风扇的状态
     */
    public void InTimeValidateFanState() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nodeId", nodeid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "请求开关验证");
        doRegist(Path.host + Path.URL_GRT_CONTROLER_STATE, jsonObject, INTIME_VALIDATE_FANSTATE);
    }

    /**
     * 初始化fragment
     */
    private void initFragment() {
        FragmentManager manager = getFragmentManager();
        //开启Fragment事务
        FragmentTransaction transaction = manager.beginTransaction();
        switchFragment = new SwitchFragment();
        timingFragment = new TimingFragment();
        transaction.add(R.id.content, switchFragment);
        transaction.commit();
        SetSelection(0);
//        Switch_radio.setChecked(true);
        switch_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager switchManager = getFragmentManager();
                FragmentTransaction switchTransaction = switchManager.beginTransaction();
                if (!timingFragment.isHidden()) {
                    switchTransaction.hide(timingFragment);
                }
                if (switchFragment == null) {
                    switchFragment = new SwitchFragment();
                    switchTransaction.add(R.id.content, switchFragment);
                }
                if (!switchFragment.isAdded()) {
                    switchTransaction.add(R.id.content, timingFragment);
                }
                switchTransaction.show(switchFragment);
                SetSelection(0);
                switchTransaction.commitAllowingStateLoss();
            }
        });
        timer_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager timerManager = getFragmentManager();
                FragmentTransaction timerTransaction = timerManager.beginTransaction();
                if (!switchFragment.isHidden()) {
                    timerTransaction.hide(switchFragment);
                }
                if (timingFragment == null) {
                    timingFragment = new TimingFragment();
                    timerTransaction.add(R.id.content, timingFragment);
                }
                if (!timingFragment.isAdded()) {
                    timerTransaction.add(R.id.content, timingFragment);
                }
                timerTransaction.show(timingFragment);
//                if (mNodeTimeDataList != null) {
//                    AddAlarm(mNodeTimeDataList);
//                }
                SetSelection(1);
                timerTransaction.commitAllowingStateLoss();
            }
        });
    }

    /**
     * 控制节点的动画
     *
     * @param Open 开启,关闭。
     */
    public void ControlFanAnimation(int position, boolean Open) {
        if (Open) {
            RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);//设置旋转中心点
            animation.setDuration(1000);//设置旋转速度
            LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
            animation.setInterpolator(lin);
            animation.setFillAfter(true);
            animation.setRepeatCount(Animation.INFINITE);
            switch_fan_img.startAnimation(animation);
            mNodeControlData.get(position).setAnimator(animation);
        } else {
            if (mNodeControlData.get(position).getAnimator() != null) {
                mNodeControlData.get(position).getAnimator().cancel();
                switch_fan_img.clearAnimation();
            }
        }
    }

    /**
     * @function Fragment显示对应的图标
     */
    private void SetSelection(int Selection) {
        if (Selection == 0) {
            switch_layout.setBackgroundResource(R.color.blue);
            switch_layout_txt.setTextColor(ContextCompat.getColor(this, R.color.white));
            switch_layout_img.setImageResource(R.drawable.switch_open);
            timer_layout.setBackgroundResource(R.color.white);
            timer_layout_img.setImageResource(R.drawable.timer_clock_normal);
            timer_layout_txt.setTextColor(ContextCompat.getColor(this, R.color.blue));
        } else if (Selection == 1) {
            switch_layout.setBackgroundResource(R.color.white);
            switch_layout_txt.setTextColor(ContextCompat.getColor(this, R.color.blue));
            switch_layout_img.setImageResource(R.drawable.switch_close);
            timer_layout.setBackgroundResource(R.color.blue);
            timer_layout_img.setImageResource(R.drawable.timer_clock_checked);
            timer_layout_txt.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
        }
    }

    /**
     * 定时三秒循环三次
     */
    public void TimingTask(final int position) {
        int index = mNodeTimeDataList.get(position).getTimingWhat();
        if (index == 2 || index == 3 || index == 1) {
            Log.e(TAG, "正在执行定时任务");
        } else {
            Runnable runnable = new Runnable() {
                public void run() {
                    // task to run goes here
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = position;
                    ValidateHandler.sendMessage(msg);
                }
            };
            ScheduledExecutorService TimingService = Executors
                    .newSingleThreadScheduledExecutor();
            // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
            TimingService.scheduleAtFixedRate(runnable, 0, 3, TimeUnit.SECONDS);
            mNodeTimeDataList.get(position).setTimingService(TimingService);
        }
    }

    /**
     * 开关三秒循环三次
     */
    public void SwitchTask() {
        Runnable runnable = new Runnable() {
            public void run() {
                // task to run goes here
                Message msg = new Message();
                msg.what = 1;
                ValidateHandler.sendMessage(msg);
            }
        };
        //开关的线程池
        ScheduledExecutorService SwitchService = Executors
                .newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        SwitchService.scheduleAtFixedRate(runnable, 0, 3, TimeUnit.SECONDS);
        switch (typePosition) {
            case 1:
                MethodTools.farmDataList.get(parentPosition).getNodeSideWind().get(position).setSwitchIndex(0);
                MethodTools.farmDataList.get(parentPosition).getNodeSideWind().get(position).setSwitchService(SwitchService);
                break;
            case 2:
                MethodTools.farmDataList.get(parentPosition).getCooling().get(position).setSwitchIndex(0);
                MethodTools.farmDataList.get(parentPosition).getCooling().get(position).setSwitchService(SwitchService);
                break;
            case 3:
                MethodTools.farmDataList.get(parentPosition).getFeed().get(position).setSwitchIndex(0);
                MethodTools.farmDataList.get(parentPosition).getFeed().get(position).setSwitchService(SwitchService);
                break;
            case 4:
                MethodTools.farmDataList.get(parentPosition).getDung().get(position).setSwitchIndex(0);
                MethodTools.farmDataList.get(parentPosition).getDung().get(position).setSwitchService(SwitchService);
                break;
            case 5:
                MethodTools.farmDataList.get(parentPosition).getLight().get(position).setSwitchIndex(0);
                MethodTools.farmDataList.get(parentPosition).getLight().get(position).setSwitchService(SwitchService);
                break;
            case 6:
                MethodTools.farmDataList.get(parentPosition).getHeating().get(position).setSwitchIndex(0);
                MethodTools.farmDataList.get(parentPosition).getHeating().get(position).setSwitchService(SwitchService);
                break;
            case 7:
                MethodTools.farmDataList.get(parentPosition).getHumidification().get(position).setSwitchIndex(0);
                MethodTools.farmDataList.get(parentPosition).getHumidification().get(position).setSwitchService(SwitchService);
                break;
            case 8:
                MethodTools.farmDataList.get(parentPosition).getFillLight().get(position).setSwitchIndex(0);
                MethodTools.farmDataList.get(parentPosition).getFillLight().get(position).setSwitchService(SwitchService);
                break;
            case 9:
                MethodTools.farmDataList.get(parentPosition).getSterilization().get(position).setSwitchIndex(0);
                MethodTools.farmDataList.get(parentPosition).getSterilization().get(position).setSwitchService(SwitchService);
                break;
        }
    }

    /**
     * 查询模式三次
     */
    public void ModelTask() {
        Runnable runnable = new Runnable() {
            public void run() {
                // task to run goes here
                Message msg = new Message();
                msg.what = 2;
                ValidateHandler.sendMessage(msg);
            }
        };
        //开关的线程池
        modelService = Executors
                .newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        modelService.scheduleAtFixedRate(runnable, 0, 3, TimeUnit.SECONDS);
    }

    /**
     * 心跳任务(即每隔30秒获取一次数据)
     */
    private void HeartTask() {
        final Handler HeartHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        GetFanState();
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
        HeartService.scheduleAtFixedRate(runnable, 0, 30, TimeUnit.SECONDS);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (modelService != null)
            modelService.shutdownNow();
        if (HeartService != null)
            HeartService.shutdownNow();
        for (int i = 0; i < mNodeControlData.size(); i++) {
            if (mNodeControlData.get(i).getAnimator() != null) {
                mNodeControlData.get(i).getAnimator().cancel();
                mNodeControlData.get(i).setAnimator(null);
            }
            if (mNodeControlData.get(i).getSwitchService() != null) {
                mNodeControlData.get(i).getSwitchService().shutdown();
                mNodeControlData.get(i).setSwitchIndex(0);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MethodTools.handlerJson.removeCallbacksAndMessages(null);
    }
}