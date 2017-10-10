package com.kys26.webthings.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kys26.webthings.command.WindControlActivity;
import com.kys26.webthings.dialog.CustomDialog;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.model.NodeTimeData;
import com.kys26.webthings.util.DataUtil;
import com.kys26.webthings.util.DensityUtil;
import com.kys26.webthings.util.StringUtil;
import com.kys26.webthings.view.SlidView;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.kys26.webthings.httpconstant.Code.TIMING_CLOSE;
import static com.kys26.webthings.httpconstant.Code.TIMING_OPEN;
import static com.kys26.webthings.main.BaseActivity.CONTROL_TIME;
import static com.kys26.webthings.main.BaseActivity.DELETE_TIME;
import static com.kys26.webthings.main.BaseActivity.UNDATE_TIME;
import static com.kys26.webthings.method.MethodTools.farmDataList;

/**
 * @author 李赛鹏
 * @class Created by kys_9 on 2017/5/10.
 */

public class TimingAdapter extends RecyclerView.Adapter<TimingAdapter.MyViewHolder> implements View.OnClickListener, SlidView.IonSlidingButtonListener {
    private WindControlActivity mContext;
    private IonSlidingViewClickListener mIDeleteBtnClickListener;
    private List<NodeTimeData> mDatas;
    private SlidView mMenu = null;
    //自定义dialog
    CustomDialog mCustomDialog = new CustomDialog();
    /**
     * 定时dialog
     */
    private CustomDialog dialog;
    TimePicker start_picker, end_picker;
//    private OnDeleteTimingListener mOnDeleteTimingListener;
//    private OnControlTimingListener mOnControlTimingListener;

    public TimingAdapter(WindControlActivity mContext, IonSlidingViewClickListener listener, List<NodeTimeData> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mIDeleteBtnClickListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_timinglist, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        //给布局设置点击和长点击监听
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.timing_Layout.getLayoutParams().width = DensityUtil.initScreenWidth((Activity) mContext);
        if (mDatas.get(position).getKid1Stat() == TIMING_OPEN) {
            holder.timing_switch.setChecked(true);
        } else {
            holder.timing_switch.setChecked(false);
        }
        holder.timing_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mProgressBar.setVisibility(View.VISIBLE);
                holder.timing_switch.setClickable(false);
                if (holder.timing_switch.isChecked()) {
                    SendControlCommand(TIMING_OPEN, position);
                } else {
                    SendControlCommand(TIMING_CLOSE, position);
                }
                mContext.TimingTask(position);
                holder.timing_switch.setChecked(!holder.timing_switch.isChecked());
//                mContext.getAllTiming();
            }
        });
        holder.btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.timing_switch.isChecked())
                    ShowDialog(position, holder, v);
                else
                    Toast.makeText(mContext, "请先关闭定时，再删除", Toast.LENGTH_SHORT).show();
            }
        });
        holder.tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.timing_switch.isChecked()) {
                    dialog = new CustomDialog();
                    dialog.setTitle("修改定时项");
                    dialog.setTitleSize(24);
                    View view = LayoutInflater.from(mContext).inflate(R.layout.item_dialog_timing, null);
                    start_picker = (TimePicker) view.findViewById(R.id.start_picker);
                    end_picker = (TimePicker) view.findViewById(R.id.end_picker);
                    start_picker.setIs24HourView(true);
                    end_picker.setIs24HourView(true);
                    int time[] = StringUtil.getTime(holder.time_txt.getText().toString());
                    start_picker.setCurrentHour(time[0]);
                    start_picker.setCurrentMinute(time[1]);
                    end_picker.setCurrentHour(time[2]);
                    end_picker.setCurrentMinute(time[3]);
                    dialog.setView(view);
                    dialog.setDismiss(false);
                    dialog.setOnDialogListener(new CustomDialog.OnDialogListener() {
                        @Override
                        public void sureListener() {
                            editTiming(mContext.typePosition, position);
//                            switch (mContext.typePosition) {
//                                case 0:
//
//                                    break;
//                                case 1:
//                                    editTiming(mContext.typePosition, position);
//                                    break;
//                                case 2:
//                                    editTiming(mContext.typePosition, position);
//                                    break;
//                                default:
//                                    break;
//                            }
                        }

                        @Override
                        public void cancelListener() {
                            dialog.DissMissDialog();
                        }
                    });
                    dialog.show(mContext.getFragmentManager(), "TAG");
                } else {
                    Toast.makeText(mContext, "请先关闭定时，再修改", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.start_txt.setText("开始时间");
        holder.end_txt.setText("结束时间");
        holder.time_txt.setText(mDatas.get(position).getKid1Value() / 60 + ":" + mDatas.get(position).getKid1Value() % 60
                + "-" + mDatas.get(position).getKid1Tim() / 60 + ":" + mDatas.get(position).getKid1Tim() % 60);
    }

    /**
     * 修改定时
     *
     * @param typePosition 判断是哪一个节点
     */
    private void editTiming(int typePosition, int position) {
        int start = start_picker.getCurrentHour() * 60 + start_picker.getCurrentMinute();
        int end = end_picker.getCurrentHour() * 60 + end_picker.getCurrentMinute();
        if (DataUtil.isAllowed(start, end, position)) {
            NodeTimeData nodeTimeData = null;
            switch (typePosition) {
                case 0:

                    break;
                case 1:
                    nodeTimeData = MethodTools.farmDataList.get(mContext.parentPosition).getNodeSideWind()
                            .get(mContext.position).getNodeTimeDataList().get(position);
                    break;
                case 2:
                    nodeTimeData = MethodTools.farmDataList.get(mContext.parentPosition).getCooling()
                            .get(mContext.position).getNodeTimeDataList().get(position);
                    break;
                case 3:
                    nodeTimeData = MethodTools.farmDataList.get(mContext.parentPosition).getFeed()
                            .get(mContext.position).getNodeTimeDataList().get(position);
                    break;
                case 4:
                    nodeTimeData = MethodTools.farmDataList.get(mContext.parentPosition).getDung()
                            .get(mContext.position).getNodeTimeDataList().get(position);
                    break;
                case 5:
                    nodeTimeData = MethodTools.farmDataList.get(mContext.parentPosition).getLight()
                            .get(mContext.position).getNodeTimeDataList().get(position);
                    break;
                case 6:
                    nodeTimeData = MethodTools.farmDataList.get(mContext.parentPosition).getHeating()
                            .get(mContext.position).getNodeTimeDataList().get(position);
                    break;
                case 7:
                    nodeTimeData = MethodTools.farmDataList.get(mContext.parentPosition).getHumidification()
                            .get(mContext.position).getNodeTimeDataList().get(position);
                    break;
                case 8:
                    nodeTimeData = MethodTools.farmDataList.get(mContext.parentPosition).getFillLight()
                            .get(mContext.position).getNodeTimeDataList().get(position);
                    break;
                case 9:
                    nodeTimeData = MethodTools.farmDataList.get(mContext.parentPosition).getSterilization()
                            .get(mContext.position).getNodeTimeDataList().get(position);
                    break;
            }
            if (nodeTimeData != null) {
                nodeTimeData.setKid1Value(start_picker.getCurrentHour() * 60 + start_picker.getCurrentMinute());
                nodeTimeData.setKid1Tim(end_picker.getCurrentHour() * 60 + end_picker.getCurrentMinute());
                mContext.doRegist(Path.host + Path.URL_UPDATE_TIME, nodeTimeData.toObject(), UNDATE_TIME);
            }
            mContext.GetFanState();
            dialog.dismiss();
        } else {
            Toast.makeText(mContext, "请选择正确的时间", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 发送控制命令
     *
     * @param cmd 控制命令
     */
    private void SendControlCommand(int cmd, int position) {
        JSONObject object = new JSONObject();
        try {
            object.put("command", "0x12");
            JSONArray dataArray = new JSONArray();
            JSONObject dataob = new JSONObject();
            switch (mContext.typePosition) {
                case 0:

                    break;
                case 1:
                    dataob.put("nodeid", farmDataList.get(
                            mContext.parentPosition).getNodeSideWind().
                            get(mContext.position).getNodeid());
                    break;
                case 2:
                    dataob.put("nodeid", farmDataList.get(
                            mContext.parentPosition).getCooling().
                            get(mContext.position).getNodeid());
                    break;
                case 3:
                    dataob.put("nodeid", farmDataList.get(
                            mContext.parentPosition).getFeed().
                            get(mContext.position).getNodeid());
                    break;
                case 4:
                    dataob.put("nodeid", farmDataList.get(
                            mContext.parentPosition).getDung().
                            get(mContext.position).getNodeid());
                    break;
                case 5:
                    dataob.put("nodeid", farmDataList.get(
                            mContext.parentPosition).getLight().
                            get(mContext.position).getNodeid());
                    break;
                case 6:
                    dataob.put("nodeid", farmDataList.get(
                            mContext.parentPosition).getHeating().
                            get(mContext.position).getNodeid());
                    break;
                case 7:
                    dataob.put("nodeid", farmDataList.get(
                            mContext.parentPosition).getHumidification().
                            get(mContext.position).getNodeid());
                    break;
                case 8:
                    dataob.put("nodeid", farmDataList.get(
                            mContext.parentPosition).getFillLight().
                            get(mContext.position).getNodeid());
                    break;
                case 9:
                    dataob.put("nodeid", farmDataList.get(
                            mContext.parentPosition).getSterilization().
                            get(mContext.position).getNodeid());
                    break;
            }
            dataob.put("kid_value", mDatas.get(position).getKid1Value());
            dataob.put("kid_tim", mDatas.get(position).getKid1Tim());
            dataob.put("kid2_value", mDatas.get(position).getKid2Value());
            dataob.put("kid2_tim", mDatas.get(position).getKid2Tim());
            dataob.put("kid_stat", cmd);
            dataob.put("kid2_stat", cmd);
            dataob.put("gwid", farmDataList.get(mContext.parentPosition).getgw_Id());
            dataArray.put(dataob);
            object.put("data", dataArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mContext.doRegist(Path.host + Path.URL_CONTROL_TIM, object, CONTROL_TIME);
    }


    @Override
    public int getItemCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v);
        }
    }

    /**
     * 删除菜单打开信息接收
     *
     * @param view
     */
    @Override
    public void onMenuIsOpen(View view) {
        mMenu = (SlidView) view;
    }

    /**
     * 滑动或者Item监听
     *
     * @param slidingButtonView
     */
    @Override
    public void onDownOrMove(SlidView slidingButtonView) {
        if (menuIsOpen()) {
            if (mMenu != slidingButtonView) {
                closeMenu();
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        //        TextView date, data;
        TextView start_txt, end_txt, time_txt, btn_Delete, tv_edit;
        Switch timing_switch;
        ViewGroup timing_Layout;
        ProgressBar mProgressBar;

        public MyViewHolder(View view) {
            super(view);
            btn_Delete = (TextView) view.findViewById(R.id.tv_delete);
            start_txt = (TextView) view.findViewById(R.id.start_txt);
            end_txt = (TextView) view.findViewById(R.id.end_txt);
            time_txt = (TextView) view.findViewById(R.id.time_txt);
            timing_switch = (Switch) view.findViewById(R.id.timing_switch);
            timing_Layout = (ViewGroup) view.findViewById(R.id.item_timinglayout);
            tv_edit = (TextView) view.findViewById(R.id.tv_edit);
            mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
            ((SlidView) view.findViewById(R.id.slid_view)).setSlidingButtonListener(TimingAdapter.this);
        }
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void addData(int position, String data) {
//        mDatas.add(position, data);
        notifyItemInserted(position);
    }

    public void removeData(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
    }


    /**
     * 关闭菜单
     */
    public void closeMenu() {
        mMenu.closeMenu();
        mMenu = null;
    }

    /**
     * 判断是否有菜单打开
     */
    public Boolean menuIsOpen() {
        if (mMenu != null) {
            return true;
        }
        return false;
    }

    public interface IonSlidingViewClickListener {
        void onItemClick(View view, int position);

        void onDeleteBtnCilck(View view, int position);
    }

    /**
     * 显示dialog
     */
    private void ShowDialog(final int position, final RecyclerView.ViewHolder holder, final View v) {
        mCustomDialog.setTitle("删除定时");
        TextView text = new TextView(mContext);
        text.setWidth(MATCH_PARENT);
        text.setHeight(DensityUtil.px2dip(mContext, 200));
        text.setText("确定要删除定时么？");
        text.setTextColor(Color.BLACK);
        text.setGravity(Gravity.CENTER);
        text.setTextSize(16);
        mCustomDialog.setView(text);
        mCustomDialog.setTitleSize(24);
        mCustomDialog.setOnDialogListener(new CustomDialog.OnDialogListener() {
            @Override
            public void sureListener() {
                int n = holder.getLayoutPosition();
                JSONObject object = new JSONObject();
                switch (mContext.typePosition) {
                    case 0:

                        break;
                    case 1:
                        try {
                            object.put("id", MethodTools.farmDataList.get(mContext.parentPosition)
                                    .getNodeSideWind().get(mContext.position).getNodeTimeDataList().get(position).getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        try {
                            object.put("id", MethodTools.farmDataList.get(mContext.parentPosition)
                                    .getCooling().get(mContext.position).getNodeTimeDataList().get(position).getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }

                mContext.doRegist(Path.host + Path.URL_DELETE_TIM, object, DELETE_TIME);
                mIDeleteBtnClickListener.onDeleteBtnCilck(v, n);
            }

            @Override
            public void cancelListener() {
                mCustomDialog.DissMissDialog();
            }
        }).show(mContext.getFragmentManager(), "TAG");
//        mCustomDialog.show(mContext.getFragmentManager(), "TAG");
    }

//    public void setOnControlTimingListener(OnControlTimingListener listener) {
//        mOnControlTimingListener = listener;
//    }
//
//    public void setOnDeleteTimingListener(OnDeleteTimingListener listener) {
//        mOnDeleteTimingListener = listener;
//    }
}