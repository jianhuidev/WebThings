package com.kys26.webthings.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kys26.webthings.method.MethodTools;
import com.zhangyx.MyGestureLock.R;

/**
 * Created by 窦文 on 2017/1/16.
 */
public class QueryListAdapter extends BaseAdapter {

    private Context mContext;
    private int whitch;
    private String[] types = {"温度","湿度","NH3浓度","光照强度"};
    private String[] values = new String[4];

    /**
     * @param context
     */
    public QueryListAdapter(Context context,int whitch){
        for (int i = 0;i<MethodTools.nodeList.get(whitch).size();i++){
            Log.d("adapter","111"+MethodTools.nodeList.get(whitch).size());
            if ("温湿度监测器".equals(MethodTools.nodeList.get(whitch).get(i).get("type").toString())) {
                values[1] = MethodTools.nodeList.get(whitch).get(i).get("hum").toString()+"%";
                values[0] = MethodTools.nodeList.get(whitch).get(i).get("temp").toString()+"℃";
            } else if ("NH3监测器".equals(MethodTools.nodeList.get(whitch).get(i).get("type"))) {
                values[2] = MethodTools.nodeList.get(whitch).get(i).get("NH3").toString()+"ppm";
            }
            values[3] = "中等";
        }
        Log.d("adapter333",values[0]+" "+values[1]+" "+values[2]);
        mContext = context;
    }
    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_query_message,null);
            viewHolder.type = (TextView) convertView.findViewById(R.id.item_type);
            viewHolder.value = (TextView) convertView.findViewById(R.id.item_value);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.type.setText(types[position]);
        viewHolder.value.setText(values[position]);
        return convertView;
    }

    class ViewHolder{
        TextView type;
        TextView value;
    }
}
