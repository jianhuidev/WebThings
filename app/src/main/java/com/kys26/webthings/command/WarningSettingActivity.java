package com.kys26.webthings.command;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kys26.webthings.dialog.CustomDialog;
import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.interfac.NodeType;
import com.kys26.webthings.main.BaseActivity;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.model.WarningData;
import com.kys26.webthings.model.WarningDeviceStateData;
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

import butterknife.ButterKnife;

import static com.kys26.webthings.httpconstant.Code.AUTOMODEL_NH3_CLOSE;
import static com.kys26.webthings.httpconstant.Code.AUTOMODEL_TMP_CLOSE;
import static com.kys26.webthings.httpconstant.Code.AUTOMODEL_TMP_OPEN;
import static com.kys26.webthings.httpconstant.Code.AUTO_ALL_OPEN;
import static com.kys26.webthings.httpconstant.Code.AUTO_MODEL_OPEN;
import static com.kys26.webthings.httpconstant.Code.AUTO_NH3_OPEN;
import static com.kys26.webthings.httpconstant.Code.COOLING_NODE_NAME;
import static com.kys26.webthings.httpconstant.Code.FAN_NODE_NDNAME;
import static com.kys26.webthings.method.MethodTools.farmDataList;

/**
 * @author 李赛鹏
 * @class Created by kys_9 on 2017/5/10.
 */

public class WarningSettingActivity extends BaseActivity implements View.OnClickListener {
    private CheckBox warn_setting_switch;
    private FrameLayout left_btn;
    private int typePosition;
    private TextView secText, value;
    private int parentPosition = 0;
    private WarningData mWarningData;//告警
    private RelativeLayout warningBtn;//预警值
    private ProgressBar progessBar;
    private final static String TAG = WarningSettingActivity.class.getSimpleName();
    private Handler ValidateHandler;//验证的handler
    private JSONObject jb = new JSONObject();
    private ScheduledExecutorService mScheduledExecutorService;
    private int WarningIndex = 0;
    private WarningDeviceStateData mWarningDeviceStateData;
    private List<WarningDeviceStateData> deviceStateDataList = new ArrayList<>();
    private boolean IsKidStataOpen = false;//判断节点状态是否有一个处于自动模式
    private RelativeLayout action_btn;//告警动作
    private ScheduledExecutorService HeartService;//心跳的30秒线程池
    private TextView unitText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        initView();
        setRightBtnVisible(false);
        setTitle("预警设置");
        addRightWidget(1);
        setEnableRefresh(false, null);
        HeartTask();
    }

    /**
     * 心跳任务
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

    /**
     * 初始化所有Hnadler
     */
    private void initHandler() {
        RequestWebData();
        //重写Handler中的message方法，获取数据后进行异步加载
        MethodTools.handlerJson = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == GET_KIDSTATE) {//获取节点状态
                    try {
                        //每次请求前都把数据全部清空了
                        deviceStateDataList.clear();
                        JSONArray array = new JSONArray(msg.obj.toString());
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            mWarningDeviceStateData = WarningDeviceStateData.analysis(object);
                            switch (typePosition) {
                                case 1://风机节点
                                    if (mWarningDeviceStateData.getNdname().equals(String.valueOf(FAN_NODE_NDNAME))) {
                                        deviceStateDataList.add(mWarningDeviceStateData);
                                        int NH3State = Integer.valueOf(deviceStateDataList.get(deviceStateDataList.size() - 1).getKidStat());
                                        if ((AUTO_ALL_OPEN <= NH3State && NH3State <= AUTOMODEL_NH3_CLOSE)
                                                ) {
                                            if (!warn_setting_switch.isChecked()) {
                                                shutdownService();
                                            }
                                            changUI(true);
                                            IsKidStataOpen = true;
                                            break;
                                        }
                                    }
                                    break;
                                case 2://降温节点
                                    if (mWarningDeviceStateData.getNdname().equals(String.valueOf(FAN_NODE_NDNAME))
                                            || mWarningDeviceStateData.getNdname().equals(String.valueOf(COOLING_NODE_NAME))) {
                                        deviceStateDataList.add(mWarningDeviceStateData);
                                        int TmpState = Integer.valueOf(deviceStateDataList.get(deviceStateDataList.size() - 1).getKidStat());
                                        if ((AUTO_ALL_OPEN <= TmpState && TmpState <= AUTO_NH3_OPEN)
                                                || TmpState == AUTOMODEL_TMP_OPEN || TmpState == AUTOMODEL_TMP_CLOSE
                                                || TmpState == AUTO_MODEL_OPEN) {
                                            if (!warn_setting_switch.isChecked()) {
                                                shutdownService();
                                            }
                                            changUI(true);
                                            IsKidStataOpen = true;
                                            break;
                                        }
                                    }
                                    break;
                            }
                        }
                        if (!IsKidStataOpen) {
                            if (warn_setting_switch.isChecked()) {
                                shutdownService();
                            }
                            changUI(false);
                        }
                        IsKidStataOpen = false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == UPDATE_WARNING) {//更新告警信息(打开那几个告警设备和告警的值)
                    try {
                        JSONObject jb = new JSONObject(msg.obj.toString());
                        if (jb.getString("Status").equals("Success")) {
                            if (warn_setting_switch.isChecked())
                                changUI(true);
                            else
                                changUI(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == CONTROL_AUTO) {//自动控制
                    try {
                        JSONObject jb = new JSONObject(msg.obj.toString());
                        if (jb.getString("Status").equals("Success")) {
                            WarningTask();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == Code.FAILURE) {
                    //消除等待进度弹出框
                    MyToast.makeImgAndTextToast(WarningSettingActivity.this, getResources().getDrawable(R.drawable.tips_sry),
                            "请求失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
                    MethodTools.state = false;
                } else if (msg.what == GET_WARNING) {//获取所有告警设备
                    try {
                        mWarningData = WarningData.analysis(new JSONObject(msg.obj.toString()));
                        String Value = String.valueOf(mWarningData.getWarningValue());
                        value.setText(Value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                dismissProgress();
            }
        };
    }


    /**
     * 获取web数据
     */
    private void RequestWebData() {
        jb = new JSONObject();
        try {
            jb.put("gwid", farmDataList.get(parentPosition).getgw_Id());
            switch (typePosition) {
                case 1://通风
                    unitText.setText("ppm");
                    jb.put("ndname", StringUtil.setNodeType(NodeType.WIND));
                    break;
                case 2:
                    unitText.setText("℃");
                    jb.put("ndname", StringUtil.setNodeType(NodeType.COOLING));
                    break;
                case 3:
                    jb.put("ndname", StringUtil.setNodeType(NodeType.HEATING));
                    break;
                case 4:
                    jb.put("ndname", StringUtil.setNodeType(NodeType.DUNG));
                    break;
                case 5:
                    jb.put("ndname", StringUtil.setNodeType(NodeType.LIGHT));
                    break;
                case 6:
                    jb.put("ndname", StringUtil.setNodeType(NodeType.FEED));
                    break;
                case 7:
                    jb.put("ndname", StringUtil.setNodeType(NodeType.HUMIDIFICATION));
                    break;
                case 8:
                    jb.put("ndname", StringUtil.setNodeType(NodeType.FILL_LIGHT));
                    break;
                case 9:
                    jb.put("ndname", StringUtil.setNodeType(NodeType.STERILIZATION));
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        doRegist(Path.host + Path.URL_GET_WARNING, jb, GET_WARNING);
        JSONObject object = new JSONObject();
        try {
            object.put("gwid", farmDataList.get(parentPosition).getgw_Id());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        doRegist(Path.host + Path.URL_GET_KIDSTATE, object, GET_KIDSTATE);
    }

    /**
     * 改变界面
     *
     * @param IsOpen 是否打开自动控制
     */
    private void changUI(boolean IsOpen) {
        warn_setting_switch.setChecked(IsOpen);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_warnsetting;
    }

    @Override
    protected void onClickLeft() {
        super.onClickLeft();
        this.finish();
    }

    /**
     * @function 初始化view
     */
    private void initView() {
        initValidateHandler();
        Intent intent = getIntent();
        typePosition = intent.getIntExtra("typePosition", 0);//对应的值
        parentPosition = intent.getIntExtra("parentPosition", 0);//farm对应的position
        warn_setting_switch = (CheckBox) findViewById(R.id.warn_setting_switch);
        progessBar = (ProgressBar) findViewById(R.id.auto_progressbar);
        warningBtn = (RelativeLayout) findViewById(R.id.warning_btn);
        warningBtn.setOnClickListener(this);
        action_btn = (RelativeLayout) findViewById(R.id.action_btn);
        action_btn.setOnClickListener(this);
        left_btn = (FrameLayout) findViewById(R.id.left_btn);
        secText = (TextView) findViewById(R.id.sec_text);
        value = (TextView) findViewById(R.id.value);
        unitText = (TextView) findViewById(R.id.unit);
        warn_setting_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        //告警自动控制的开关打开与关闭
        warn_setting_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWarningData.getWarningDevice().size() == 0 && warn_setting_switch.isChecked()) {
                    warn_setting_switch.setChecked(!warn_setting_switch.isChecked());
                    Toast.makeText(getApplicationContext(), "至少打开一个告警设备", Toast.LENGTH_SHORT).show();
                } else {
                    progessBar.setVisibility(View.VISIBLE);
                    switch (typePosition) {
                        case 1:
                            if (warn_setting_switch.isChecked()) {
                                SendControlCommand(Code.NH3_OPEN);
                            } else {
                                SendControlCommand(Code.NH3_CLOSE);
                            }
                            break;
                        case 2:
                            if (warn_setting_switch.isChecked()) {
                                SendControlCommand(Code.COOLING_OPEN);
                            } else {
                                SendControlCommand(Code.COOLING_CLOSE);
                            }
                            break;
                    }
                    warn_setting_switch.setClickable(false);
                    warn_setting_switch.setChecked(!warn_setting_switch.isChecked());
                }
            }
        });
        left_btn.setOnClickListener(this);
        switch (typePosition) {
            case 0:
                break;
            case 1:
                secText.setText("氨气浓度高预警值");

                break;
            case 2:
                secText.setText("温度高预警");

                break;
//            case 3:
//                secText.setText("温度低预警");
//                break;
//            case 6:
//                secText.setText("湿度低预警");
//                break;
//            case 4:
//                secText.setText("光照强度低预警");
//                break;
            default:
                break;
        }
    }

    /**
     * 初始化验证handler
     */
    private void initValidateHandler() {
        ValidateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        //如果告警值等于3，意味验证失败，停止验证。
                        if (WarningIndex == 5) {
                            WarningIndex = 0;
                            if (mScheduledExecutorService != null) {
                                mScheduledExecutorService.shutdownNow();
                            }
                            progessBar.setVisibility(View.INVISIBLE);
                            warn_setting_switch.setClickable(true);
                            Toast.makeText(getApplicationContext(), "网络错误或硬件故障", Toast.LENGTH_SHORT).show();
                        } else {
                            WarningIndex++;
//                            mWarningData.setWarningIndex(temp);
                            JSONObject object = new JSONObject();
                            try {
                                object.put("gwid", farmDataList.get(parentPosition).getgw_Id());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            doRegist(Path.host + Path.URL_GET_KIDSTATE, object, GET_KIDSTATE);
                        }
                        break;
                }
            }
        };
    }

    /**
     * 取消定时
     */
    private void shutdownService() {
        if (mScheduledExecutorService != null)
            mScheduledExecutorService.shutdownNow();
        WarningIndex = 0;
        progessBar.setVisibility(View.INVISIBLE);
        warn_setting_switch.setClickable(true);
//        warn_setting_switch.setChecked(!warn_setting_switch.isChecked());
    }

    /**
     * 发送控制命令
     *
     * @param cmd 控制命令
     */
    private void SendControlCommand(int cmd) {
        JSONObject object = new JSONObject();
        try {
            object.put("command", "0x12");
            JSONArray dataArray = new JSONArray();
            for (int i = 0; i < mWarningData.getWarningDevice().size(); i++) {
                JSONObject dataob = new JSONObject();
                dataob.put("nodeid", mWarningData.getWarningDevice().get(i));
                dataob.put("kid_value", mWarningData.getWarningValue());
                dataob.put("kid2_value", mWarningData.getWarningValue());
                dataob.put("kid_stat", cmd);
                dataob.put("kid2_stat", cmd);
                dataob.put("gwid", farmDataList.get(parentPosition).getgw_Id());
                dataArray.put(dataob);
            }
            object.put("data", dataArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        doRegist(Path.host + Path.URL_CONTROL_TIM, object, CONTROL_AUTO);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
            case R.id.warning_btn:
                if (!warn_setting_switch.isChecked())
                    ShowDialog();
                else
                    Toast.makeText(getApplicationContext(), "请先关闭自动控制", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_btn:
                if (warn_setting_switch.isClickable()) {
                    ArrayList<String> list = new ArrayList<>();
                    Intent intent = new Intent(WarningSettingActivity.this, WarningActionActivity.class);
                    if (mWarningData != null) {
                        for (int i = 0; i < mWarningData.getWarningDevice().size(); i++) {
                            list.add(mWarningData.getWarningDevice().get(i));
                        }
                        intent.putStringArrayListExtra("list", list);
                    }
                    intent.putExtra("typePosition", typePosition);
                    intent.putExtra("parentPosition", parentPosition);
                    intent.putExtra("jsonObject", jb.toString());
                    intent.putExtra("IsSwitchOpen", warn_setting_switch.isChecked());
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "正在获取数据,请等待", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 显示对话框
     */
    public void ShowDialog() {
        CustomDialog dialog = new CustomDialog();
        dialog.setTitle(secText.getText().toString());
        dialog.setTitleSize(20);
        dialog.setTitleColor(R.color.blue);
        final EditText edit = new EditText(this);
        edit.setWidth(-1);
        dialog.setCancelable(true);
        edit.setInputType(InputType.TYPE_CLASS_NUMBER);
        edit.setText(value.getText());
        dialog.setView(edit);
        dialog.setOnDialogListener(new CustomDialog.OnDialogListener() {
            @Override
            public void sureListener() {
                if (!StringUtil.isEmpty(value.getText().toString())) {
                    mWarningData.setWarningValue(Integer.decode(edit.getText().toString()));
                    try {
                        String Value = edit.getText().toString();
                        value.setText(Value);
                        doRegist(Path.host + Path.URL_UPDATE_WARNING, new JSONObject(mWarningData.toJson()), UPDATE_WARNING);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "温度高预警不能为空", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void cancelListener() {
            }
        });
        dialog.show(getFragmentManager(), "exit");
    }

    /**
     * warning定时验证任务
     */
    public void WarningTask() {
        if (WarningIndex < 6 && WarningIndex > 0) {
            Log.e(TAG, "点击失败,正在执行告警定时任务");
        } else {
            Runnable runnable = new Runnable() {
                public void run() {
                    // task to run goes here
                    Log.e(TAG, "执行第" + WarningIndex + "次告警定时任务");
                    Message msg = new Message();
                    msg.what = 1;
                    //当前第几个告警++;
                    ValidateHandler.sendMessage(msg);
                }
            };
            //开关的线程池
            mScheduledExecutorService = Executors
                    .newSingleThreadScheduledExecutor();
            // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
            mScheduledExecutorService.scheduleAtFixedRate(runnable, 0, 3, TimeUnit.SECONDS);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (HeartService != null) {
            HeartService.shutdownNow();
        }
    }
}
