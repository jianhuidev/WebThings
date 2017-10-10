package com.kys26.webthings.adapter;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kys26.webthings.command.CommandActivity;
import com.kys26.webthings.fragment.GWFragment;
import com.kys26.webthings.model.VideoChannelData;
import com.kys26.webthings.video.VideoActivity;
import com.zhangyx.MyGestureLock.R;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Lee on 2017/8/1.
 */

public class VideoNodeAdapter extends BaseAdapter {
    private List<VideoChannelData> mNodeList;
    private CommandActivity mContext;
//    //风扇旋转动画
//    ObjectAnimator animator;

    public VideoNodeAdapter(CommandActivity context, List<VideoChannelData> mNodeSideWindList) {
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
        WindNodeGridAdapter.ViewHolder holder = null;
        if (convertView == null) {
            holder = new WindNodeGridAdapter.ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_grid_windnode, null);
            holder.textView = (TextView) convertView.findViewById(R.id.wind_node_txt);
            holder.mImageView = (GifImageView) convertView.findViewById(R.id.wind_node_gifimg);
            holder.mCardView = (CardView) convertView.findViewById(R.id.wind_node_card);
            convertView.setTag(holder);
        } else {
            holder = (WindNodeGridAdapter.ViewHolder) convertView.getTag();
        }
        holder.textView.setText(mNodeList.get(position).getChannelName());
        holder.mImageView.setImageResource(GWFragment.gifView[mContext.typePosition]);
        holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onLongClick(View v) {
                mContext.ShowDialog(position);
                return true;
            }
        });
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext.typePosition == 0) {
                    if (position < 1) {
                        Intent intent = new Intent(mContext, VideoActivity.class);
                        intent.putExtra("clickPosition", position);
                        intent.putExtra("farmPosition",mContext.parentPosition);
                        mContext.startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "该视频节点暂时不可用", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mContext.StartIntent(position);
                }
            }
        });
        return convertView;
    }

    public static class ViewHolder {
        TextView textView;
        CardView mCardView;
        ImageView mImageView;
    }
}
