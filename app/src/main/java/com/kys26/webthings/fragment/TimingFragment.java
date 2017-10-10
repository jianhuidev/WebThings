package com.kys26.webthings.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kys26.webthings.adapter.TimingAdapter;
import com.kys26.webthings.command.WindControlActivity;
import com.kys26.webthings.dialog.CustomDialog;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.main.BaseActivity;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.model.NodeTimeData;
import com.kys26.webthings.model.TimeData;
import com.kys26.webthings.util.DataUtil;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.kys26.webthings.httpconstant.Code.TIMING_CLOSE;

/**
 * @author 李赛鹏
 * @class Created by kys_9 on 2017/5/10.
 */

public class TimingFragment extends Fragment {
    public RecyclerView timinglist;
    public TimingAdapter mAdapter;
    public TextView create_timing_txt;
    private CustomDialog dialog;
    TimePicker start_picker, end_picker;
    private TextView point_txt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timing, null);
        initRegist();
        initView(view);
        return view;
    }

    private void initRegist() {
        JSONObject jb = new JSONObject();
        try {
            jb.put("deviceId", ((WindControlActivity) getActivity()).nodeid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ((WindControlActivity) getActivity()).doRegist(Path.host + Path.URL_QUERY_TIME, jb, BaseActivity.GET_ALL_TIME);
    }

    /**
     * @param view
     * @function 初始化view
     */
    private void initView(View view) {
        create_timing_txt = (TextView) view.findViewById(R.id.create_time_txt);
        create_timing_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog();
            }
        });
        timinglist = (RecyclerView) view.findViewById(R.id.timing_list);
        timinglist.setLayoutManager(new LinearLayoutManager(getActivity()));
        timinglist.setItemAnimator(new DefaultItemAnimator());
        point_txt = (TextView) view.findViewById(R.id.point_txt);
    }

    //显示dialog
    private void ShowDialog() {
        dialog = new CustomDialog();
        dialog.setTitle("添加定时项");
        dialog.setTitleSize(24);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_dialog_timing, null);
        start_picker = (TimePicker) view.findViewById(R.id.start_picker);
        end_picker = (TimePicker) view.findViewById(R.id.end_picker);
        start_picker.setIs24HourView(true);
        end_picker.setIs24HourView(true);
        dialog.setView(view);
        dialog.setDismiss(false);
        dialog.setOnDialogListener(new CustomDialog.OnDialogListener() {
            @Override
            public void sureListener() {
//                edit.getText();
                int start = start_picker.getCurrentHour() * 60 + start_picker.getCurrentMinute();
                int end = end_picker.getCurrentHour() * 60 + end_picker.getCurrentMinute();
                if (DataUtil.isAllowed(start, end, -1)) {
                    NodeTimeData nodeTimeData = new NodeTimeData();
                    nodeTimeData.setDeviceId(((WindControlActivity) getActivity()).nodeid);
                    nodeTimeData.setKid1Stat(TIMING_CLOSE);
                    nodeTimeData.setKid1Value(start);
                    nodeTimeData.setKid1Tim(end);
                    ((WindControlActivity) getActivity()).doRegist(Path.host + Path.URL_SAVE_TIME, nodeTimeData.toObject(), BaseActivity.SAVE_TIMING);
                    dialog.dismiss();
                    //回调添加alarm
//                  mOnAddTimingListener.onAdd();
                } else {
                    Toast.makeText(getActivity(), "请选择正确的时间", Toast.LENGTH_SHORT).show();
                }
//                CreateAlarmReceiver();
                //这里写请求
            }

            @Override
            public void cancelListener() {
                dialog.DissMissDialog();
            }
        });
//        dialog.setView(edit);
        dialog.show(getFragmentManager(), "TAG");
    }

    /**
     * 更改界面
     *
     * @param mNodeTimeDataList
     */
    public void changeUI(List<NodeTimeData> mNodeTimeDataList) {
        if (((WindControlActivity) getActivity()).mControlTypeTxt.getText().equals("自动控制")) {
            timinglist.setVisibility(View.GONE);
            create_timing_txt.setVisibility(View.GONE);
            point_txt.setText("现在是自动模式,不可操作定时");
            point_txt.setVisibility(View.VISIBLE);
//            Toast.makeText(getActivity().getApplicationContext(), "现在是自动模式，不可操作定时", Toast.LENGTH_SHORT).show();
        } else {
            timinglist.setVisibility(View.VISIBLE);
            create_timing_txt.setVisibility(View.VISIBLE);
            point_txt.setVisibility(View.GONE);
        }
        if (timinglist != null) {
            timinglist.setAdapter(mAdapter = new TimingAdapter((WindControlActivity) getActivity(), new TimingAdapter.IonSlidingViewClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Log.e("tag", "点击:" + position);
                }

                @Override
                public void onDeleteBtnCilck(View view, int position) {
                    mAdapter.removeData(position);
                }
            }, mNodeTimeDataList));
            MethodTools.timelist = new ArrayList<>();
            if (mNodeTimeDataList != null) {
                for (int i = 0; i < mNodeTimeDataList.size(); i++) {
                    TimeData timeData = new TimeData();
                    timeData.setStart(mNodeTimeDataList.get(i).getKid1Value());
                    timeData.setEnd(mNodeTimeDataList.get(i).getKid1Tim());
                    if (mNodeTimeDataList.get(i).getKid1Stat() == 86) {
                        timeData.setOpen(true);
                    } else {
                        timeData.setOpen(false);
                    }
                    MethodTools.timelist.add(timeData);
                }
            }
        }
    }
}