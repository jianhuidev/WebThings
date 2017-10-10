package com.kys26.webthings.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhangyx.MyGestureLock.R;

import java.util.List;

/**
 * Created by kys_9 on 2016/9/27.
 */
public class SpinnerAdapter extends BaseAdapter {
    private Activity activity = null;
    private List<String>mlist;
    public SpinnerAdapter(Activity activity,List<String> list){
        this.activity = activity;
        this.mlist = list;
    }
    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int i) {
        return mlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            //下拉项布局
            view = LayoutInflater.from(activity).inflate(R.layout.spinner_item, null);
            holder.textView = (TextView) view.findViewById(R.id.spinner_itemText);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.textView.setText(mlist.get(i));
        return view;
    }
    class ViewHolder {
        TextView textView;
    }
}

