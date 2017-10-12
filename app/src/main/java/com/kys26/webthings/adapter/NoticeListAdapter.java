package com.kys26.webthings.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kys26.webthings.main.MainActivity;
import com.kys26.webthings.method.MethodTools;

import com.kys26.webthings.personalcenter.Data;
import com.kys26.webthings.view.CustomSlide;
import com.zhangyx.MyGestureLock.R;

import java.util.List;

/**
 * Created by lenovo on 2017/7/20.
 */

public class NoticeListAdapter extends RecyclerView.Adapter<NoticeListAdapter.ViewHolder>
        implements CustomSlide.CustomSlideListener {
    /**
     * 定义接口以实现item 的点击事件
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    private List<String> list;
    private CustomSlide mSlide;
    private Context mContext;

    public NoticeListAdapter(Context context, List list) {
        this.list = list;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item_layout, parent, false);

        DisplayMetrics dm = parent.getResources().getDisplayMetrics();
        int dm_width = dm.widthPixels;
        RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.notice_layout);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl.getLayoutParams();
        params.width = dm_width;
        rl.setLayoutParams(params);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String data = list.get(position);
        holder.textUp.setText(data);
        holder.imageView.setImageResource(R.drawable.icon_app);
        holder.tv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int p = position + 1;
                Toast.makeText(mContext, "删除通知" + p, Toast.LENGTH_SHORT).show();
                list.remove(position);
                Data.pushMsg = "";
                Data.position = list.size();
                close();
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public void onMenuIsOpen(CustomSlide customSlide) {
        mSlide = customSlide;
    }

    @Override
    public void onDownOrMove(CustomSlide customSlide) {
        if (IsOpen()) {
            if (mSlide != customSlide) {
                close();
            }
        }
    }

    @Override
    public void onMyClick(CustomSlide customSlide) {
        Data.flag = 1;
        Intent intent = new Intent(mContext, MainActivity.class);
        for (int i = 0; i < MethodTools.farmDataList.size(); i++) {
            if (Data.gwId.equals(MethodTools.farmDataList.get(i).getGw_Id())) {
                intent.putExtra("farmPosition", i);
            }
        }
        mContext.startActivity(intent);
    }

    private void close() {
        mSlide.closeSlide();
        mSlide = null;
    }

    /**
     * 判断是否有滑动打开
     */
    private boolean IsOpen() {
        if (mSlide != null) {
            return true;
        }
        return false;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textUp, tv_del;
        CustomSlide mCustomSlide;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.notice_image);
            textUp = (TextView) view.findViewById(R.id.text_up);
            tv_del = (TextView) view.findViewById(R.id.tv_del);
            mCustomSlide = (CustomSlide) view.findViewById(R.id.custom_slide);
            mCustomSlide.setCustomSlideListener(NoticeListAdapter.this);
        }
    }
}
