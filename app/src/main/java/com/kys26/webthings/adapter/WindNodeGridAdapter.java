package com.kys26.webthings.adapter;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kys26.webthings.command.CommandActivity;
import com.kys26.webthings.fragment.GWFragment;
import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.model.NodeControlData;
import com.kys26.webthings.video.VideoActivity;
import com.zhangyx.MyGestureLock.R;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

import static com.kys26.webthings.httpconstant.Code.AUTOMODEL_NH3_OPEN;
import static com.kys26.webthings.httpconstant.Code.AUTO_TMP_OPEN;

/**
 * @author 李赛鹏
 * @class Created by kys_9 on 2017/6/7.
 */

public class WindNodeGridAdapter extends BaseAdapter {
    private List<NodeControlData> mNodeList;
    private CommandActivity mContext;
    //风扇旋转动画
//    ObjectAnimator animator;
    RotateAnimation animation;

    public WindNodeGridAdapter(CommandActivity context, List<NodeControlData> mNodeSideWindList) {
        this.mContext = context;
        this.mNodeList = mNodeSideWindList;
    }

    @Override
    public int getCount() {
        return mNodeList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_grid_windnode, null);
            holder.textView = (TextView) convertView.findViewById(R.id.wind_node_txt);
            holder.mImageView = (GifImageView) convertView.findViewById(R.id.wind_node_gifimg);
            holder.mCardView = (CardView) convertView.findViewById(R.id.wind_node_card);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(mNodeList.get(position).getDeviceName());
        holder.mImageView.setImageResource(GWFragment.gifView[mContext.typePosition]);
        holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mContext.typePosition != 0)
                    mContext.ShowDialog(position);
                return true;
            }
        });
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext.typePosition == 0) {
                    Intent intent = new Intent(mContext, VideoActivity.class);
                    mContext.startActivity(intent);
                } else {
                    mContext.StartIntent(position);
                }

            }
        });
        mNodeList.get(position).setImageView(holder.mImageView);
        int state = Integer.valueOf(mNodeList.get(position).getKid_stat());
        if (state == Code.HANDLE_MODEL_OPEN
                || state == Code.AUTO_MODEL_OPEN
                || state == Code.TIME_MODEL_OPEN
                || state == Code.AUTO_ALL_OPEN
                || (state >= AUTO_TMP_OPEN && state <= AUTOMODEL_NH3_OPEN)
                || state == Code.AUTOMODEL_TMP_OPEN
                ) {
            ControlFan(holder.mImageView, true);
            mNodeList.get(position).setFanOpen(true);
        } else {
            ControlFan(holder.mImageView, false);
            mNodeList.get(position).setFanOpen(false);
        }

        if (mContext.typePosition == 0) {
            ControlFan(holder.mImageView, false);
        }
        return convertView;
    }

    public static class ViewHolder {
        TextView textView;
        //ImageView imageView;
        CardView mCardView;
        ImageView mImageView;
    }

    /**
     * 控制风扇
     *
     * @param Open 开启,关闭。
     */
    public void ControlFan(ImageView img, boolean Open) {
        if (Open) {
            animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);//设置旋转中心点
            animation.setDuration(1000);//设置旋转速度
            LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
            animation.setInterpolator(lin);
            animation.setFillAfter(true);
            animation.setRepeatCount(Animation.INFINITE);
            img.setAnimation(animation);
//            animator = ObjectAnimator.ofFloat(img, "rotation", 0f, 360f);
//            animator.setInterpolator(new LinearInterpolator());
//            animator.setDuration(700);
//            animator.setRepeatCount(-1);
//            animator.start();
        } else {
            if (animation != null) {
                if (animation.isInitialized())
                    animation.cancel();
            }
        }
    }

    /**
     * 关闭所有
     */
    public void closeFan() {
        for (int i = 0; i < mNodeList.size(); i++) {
            ControlFan(mNodeList.get(i).getImageView(), false);
        }
    }
}
