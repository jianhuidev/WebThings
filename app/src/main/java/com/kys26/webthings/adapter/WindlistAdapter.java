package com.kys26.webthings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.kys26.webthings.command.WarningActionActivity;
import com.kys26.webthings.interfac.ChangeList;
import com.kys26.webthings.model.NodeControlData;
import com.zhangyx.MyGestureLock.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李赛鹏
 * @class Created by kys_9 on 2017/5/6.
 */

public class WindlistAdapter extends BaseAdapter {
    private Context mContext;
    private List<NodeControlData> dataList;
    private ChangeList mChangeList;
    private List<String> nodeList;//打开的节点ID
    private View view;

    public WindlistAdapter(WarningActionActivity mContext, List<NodeControlData> datalist, ChangeList changeList, List<String> nodeList) {
        this.mContext = mContext;
        this.dataList = datalist;
        this.mChangeList = changeList;
        this.nodeList = nodeList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

//    public View getChildView(){
//        return view;
//    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        //View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.item_windlist, null);
            viewHolder.mTextView = (TextView) view.findViewById(R.id.wind_txt);
            viewHolder.mSwitch = (CheckBox) view.findViewById(R.id.switch_btn);
            view.setTag(viewHolder);//讲ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();//重获取viewHolder
        }
        for (int j = 0; j < nodeList.size(); j++) {
            if (nodeList.get(j).equals(dataList.get(position).getNodeid())) {
                viewHolder.mSwitch.setChecked(true);
                j = nodeList.size();
            } else {
                viewHolder.mSwitch.setChecked(false);
            }
        }
        viewHolder.mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mChangeList.setList(position);
                } else {
                    mChangeList.resetList(position);
                }
            }
        });
        viewHolder.mTextView.setText(dataList.get(position).getDeviceName());
        return view;
    }
    //内部类
    class ViewHolder {
        CheckBox mSwitch;
        TextView mTextView;
    }
}
