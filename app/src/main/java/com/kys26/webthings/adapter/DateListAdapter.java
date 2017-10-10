package com.kys26.webthings.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhangyx.MyGestureLock.R;

import java.util.List;

/**
 * @author 李赛鹏
 * @class Created by kys_9 on 2017/5/10.
 */

public class DateListAdapter extends RecyclerView.Adapter<DateListAdapter.MyViewHolder> implements View.OnClickListener {
    public Context mContext;
    public List<String> date;
    public List<Float> data;

    public DateListAdapter(Context context, List<String> date, List<Float> data) {
        this.mContext = context;
        this.date = date;
        this.data = data;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()
        ).inflate(R.layout.item_datelist, parent,
                false);//这个布局就是一个imageview用来显示图片
        MyViewHolder holder = new MyViewHolder(view);
        //给布局设置点击和长点击监听
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.data.setText(date.get(position).toString());//这两个是反着的，为啥反 @sp
        if (data.size()!=0) {
            holder.date.setText(data.get(position).toString());
        }
    }


    @Override
    public int getItemCount() {
        return date.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date, data;
        public MyViewHolder(View view) {
            super(view);
            data = (TextView) view.findViewById(R.id.item_data_tv);
            date = (TextView) view.findViewById(R.id.item_date_tv);
        }
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}
