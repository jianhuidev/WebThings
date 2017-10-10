package com.kys26.webthings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhangyx.MyGestureLock.R;

import java.util.HashMap;
import java.util.List;

/**
 * @author 李赛鹏
 * @class 完成项目那里d的list
 * Created by Administrator on 2016/12/19.
 */
public class ComProjectListAdapter extends BaseAdapter{
    public Context mContext;
    public List<HashMap<String,Object>> list;
    public ComProjectListAdapter(Context context, List<HashMap<String,Object>>list){
        this.mContext=context;
        this.list=list;
    }
    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder=null;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.comproject_item, null);
            holder.id = (TextView) view.findViewById(R.id.comproject_itemid);
            holder.name=(TextView)view.findViewById(R.id.comproject_itemname);
            holder.price=(TextView)view.findViewById(R.id.comproject_itemprice);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.id.setText("设备编号:xxxx");
        holder.name.setText("设备名字:xxx");
        holder.price.setText("金额:xx");
        //holder.Priceet.setText(list.get(i).toString());
        return view;
    }
    class ViewHolder{
        TextView id;
        TextView name;
        TextView price;
    }
}
