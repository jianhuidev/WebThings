package com.kys26.webthings.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kys26.webthings.command.WindControlActivity;
import com.kys26.webthings.dialog.CustomDialog;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.interfac.OnValidateSwitchListener;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.model.NodeControlData;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.widget.ListPopupWindow.MATCH_PARENT;
import static com.kys26.webthings.httpconstant.Code.HANDLE_MODEL_CLOSE;
import static com.kys26.webthings.httpconstant.Code.HANDLE_MODEL_OPEN;
import static com.kys26.webthings.main.BaseActivity.WIND_CONTROL;

/**
 * @author 李赛鹏
 * @class Created by kys_9 on 2017/5/10.
 */

public class SwitchFragment extends Fragment {
    private CardView switch_btn;
    private String gwId, nodeId;
    private int parentPosition;
    private static final String TAG = SwitchFragment.class.getSimpleName();
    private int position;
    private OnValidateSwitchListener mOnValidateSwitchListener;
    private ProgressBar switch_progressbar;
    private int typePosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_switch, null);
        //判断当前的value值
        initView(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * @param view
     * @function 初始化view
     */
    private void initView(View view) {
        switch_btn = (CardView) view.findViewById(R.id.switch_btn);
        Intent intent = getActivity().getIntent();
        gwId = intent.getStringExtra("gwid");
        nodeId = intent.getStringExtra("node_id");
        parentPosition = intent.getIntExtra("parentPosition", 0);
        typePosition = intent.getIntExtra("typePosition", 0);
        position = intent.getIntExtra("position", 0);
        switch_progressbar = (ProgressBar) view.findViewById(R.id.switch_progressbar);
        switch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((WindControlActivity) (getActivity())).mControlTypeTxt.getText().toString().equals("自动控制")) {
                    ShowModelDialog("自动控制");
                } else if (((WindControlActivity) (getActivity())).mControlTypeTxt.getText().toString().equals("定时控制")) {
                    ShowModelDialog("定时控制");
                } else {
                    onSwitchClick();
                }
            }
        });
    }

    public void onSwitchClick() {
        if (!judgeFanOpen()) {
            SendSwitchCmd(HANDLE_MODEL_OPEN);
        } else {
            SendSwitchCmd(HANDLE_MODEL_CLOSE);
        }
        StartSwitchTiming();
    }

    /**
     * 判断风扇打开
     */
    private boolean judgeFanOpen() {
        boolean isFanOpen = false;
        switch (typePosition) {
            case 1:
                isFanOpen = MethodTools.farmDataList.get(parentPosition).getNodeSideWind().get(position).isFanOpen();
                return isFanOpen;
            case 2:
                isFanOpen = MethodTools.farmDataList.get(parentPosition).getCooling().get(position).isFanOpen();
                return isFanOpen;
            case 3:
                isFanOpen = MethodTools.farmDataList.get(parentPosition).getFeed().get(position).isFanOpen();
                return isFanOpen;
            case 4:
                isFanOpen = MethodTools.farmDataList.get(parentPosition).getDung().get(position).isFanOpen();
                return isFanOpen;
            case 5:
                isFanOpen = MethodTools.farmDataList.get(parentPosition).getLight().get(position).isFanOpen();
                return isFanOpen;
            case 6:
                isFanOpen = MethodTools.farmDataList.get(parentPosition).getHeating().get(position).isFanOpen();
                return isFanOpen;
            case 7:
                isFanOpen = MethodTools.farmDataList.get(parentPosition).getHumidification().get(position).isFanOpen();
                return isFanOpen;
            case 8:
                isFanOpen = MethodTools.farmDataList.get(parentPosition).getFillLight().get(position).isFanOpen();
                return isFanOpen;
            case 9:
                isFanOpen = MethodTools.farmDataList.get(parentPosition).getSterilization().get(position).isFanOpen();
                return isFanOpen;
            default:
                return isFanOpen;
        }
    }

    /**
     * 开始定时计时
     */
    private void StartSwitchTiming() {
        switch_btn.setVisibility(View.GONE);
        switch_progressbar.setVisibility(View.VISIBLE);
        mOnValidateSwitchListener.onValidateSwitch();
    }

    /**
     * 获取到数据，进行显示
     */
    public void StopSwitchTiming() {
        if (switch_progressbar != null)
            switch_progressbar.setVisibility(View.GONE);
        if (switch_btn != null)
            switch_btn.setVisibility(View.VISIBLE);
    }

    /**
     * 发送控制命令
     *
     * @param Cmd 控制命令
     */
    public void SendSwitchCmd(int Cmd) {
        JSONObject object = new JSONObject();
        try {
            object.put("command", "0x12");
            JSONArray dataArray = new JSONArray();
            JSONObject dataOb = new JSONObject();
            dataOb.put("nodeid", nodeId);
            dataOb.put("gwid", gwId);
            dataOb.put("kid_stat", Cmd);
            dataOb.put("kid2_stat", Cmd);
            dataArray.put(dataOb);
            object.put("data", dataArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ((WindControlActivity) getActivity()).doRegist(Path.host + Path.URL_COMMAND, object, WIND_CONTROL);
    }

    /**
     * 验证开关接口
     *
     * @param listener 接口实例
     */
    public void setOnValidateSwitchListener(OnValidateSwitchListener listener) {
        this.mOnValidateSwitchListener = listener;
    }

    /**
     * 显示自动切换成手动的dialog
     */
    public void ShowModelDialog(String model_text) {
        final CustomDialog mCustomDialog = new CustomDialog();
        mCustomDialog.setTitle("切换模式");
        mCustomDialog.setTitleSize(18);
        TextView txt = new TextView(getActivity());
        txt.setWidth(MATCH_PARENT);
        DisplayMetrics dm = getResources().getDisplayMetrics();//获取屏幕宽度
        int txtHeight = (dm.widthPixels) / 4;
        txt.setHeight(txtHeight);
        txt.setTextSize(16);
        //txt.setHeight(DensityUtil.px2dip(getActivity(), 200));
        txt.setGravity(Gravity.CENTER);
        txt.setText("现在是" + model_text + ",是否切换成手动模式?");
        mCustomDialog.setOnDialogListener(new CustomDialog.OnDialogListener() {
            @Override
            public void sureListener() {
                mCustomDialog.DissMissDialog();
                onSwitchClick();
            }

            @Override
            public void cancelListener() {
            }
        });
        mCustomDialog.setView(txt);
        mCustomDialog.show(getFragmentManager(), "TAG");
    }

    /**
     * 更改UI
     */
    public void changeUI() {
        if (!judgeFanOpen()) {
            switch_btn.setForeground(ContextCompat.getDrawable(getActivity(), R.drawable.exit_normal));
        } else {
            switch_btn.setForeground(ContextCompat.getDrawable(getActivity(), R.drawable.exit_checked));
        }
    }

    /**
     * 数据发生改变
     * @param isOpen   判断是否打开
     * @param position 位置
     */
    public void isDataChanged(boolean isOpen, int position) {
        if (!judgeFanOpen() == isOpen) {
            Log.e(TAG, "状态验证成功");
            ((WindControlActivity) getActivity()).ControlFanAnimation(position, isOpen);
            switch (typePosition) {
                case 1:
                    StopSwitchValidate(MethodTools.farmDataList.get(parentPosition).getNodeSideWind().get(position), isOpen);
                    break;
                case 2:
                    StopSwitchValidate(MethodTools.farmDataList.get(parentPosition).getCooling().get(position), isOpen);
                    break;
                case 3:
                    StopSwitchValidate(MethodTools.farmDataList.get(parentPosition).getFeed().get(position), isOpen);
                    break;
                case 4:
                    StopSwitchValidate(MethodTools.farmDataList.get(parentPosition).getDung().get(position), isOpen);
                    break;
                case 5:
                    StopSwitchValidate(MethodTools.farmDataList.get(parentPosition).getLight().get(position), isOpen);
                    break;
                case 6:
                    StopSwitchValidate(MethodTools.farmDataList.get(parentPosition).getHeating().get(position), isOpen);
                    break;
                case 7:
                    StopSwitchValidate(MethodTools.farmDataList.get(parentPosition).getHumidification().get(position), isOpen);
                    break;
                case 8:
                    StopSwitchValidate(MethodTools.farmDataList.get(parentPosition).getFillLight().get(position), isOpen);
                    break;
                case 9:
                    StopSwitchValidate(MethodTools.farmDataList.get(parentPosition).getSterilization().get(position), isOpen);
                    break;
            }
            changeUI();
            StopSwitchTiming();
        }
    }

    /**
     * 停止开关验证
     *
     * @param mNodeControlData 开关的model
     * @param isOpen           是否打开风扇
     */
    private void StopSwitchValidate(NodeControlData mNodeControlData, boolean isOpen) {
        mNodeControlData.setSwitchIndex(0);
        while (!mNodeControlData.getSwitchService().isShutdown()) {
            mNodeControlData.getSwitchService().shutdownNow();
        }
        mNodeControlData.setSwitchService(null);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mNodeControlData.setFanOpen(isOpen);
    }
}
