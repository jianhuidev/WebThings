package com.kys26.webthings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.util.StringUtil;
import com.zhangyx.MyGestureLock.R;

/**
 * Created by 窦文 on 2017/1/17.
 */
public class CmdCtlListAdapter extends BaseAdapter {
    private Context mContext;
    private int whitch;

    /**
     * @param context
     */
    public CmdCtlListAdapter(Context context,int whitch){
        this.whitch = whitch;
        mContext = context;
    }
    @Override
    public int getCount() {
        if (MethodTools.nodeList.size()!=0) {
            return MethodTools.nodeList.get(whitch).size();
        }else {
            return 0;
        }
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_cmd_message,null);
            viewHolder.type = (TextView) convertView.findViewById(R.id.item_type);
            viewHolder.value = (TextView) convertView.findViewById(R.id.item_value);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.type.setText(MethodTools.nodeList.get(whitch).get(position).get("name").toString());
        viewHolder.value.setText(StringUtil.getState(Integer.valueOf(MethodTools.nodeList.get(whitch).get(position).get("state").toString())));
        return convertView;
    }

    class ViewHolder{
        TextView type;
        TextView value;
    }

}
