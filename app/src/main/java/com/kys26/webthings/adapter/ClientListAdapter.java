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
 * @class 新建项目中客户信息一输入就显示的listView的适配器
 * Created by Administrator on 2016/12/7.
 */
public class ClientListAdapter extends BaseAdapter {
    private Context mContext;
    private List<HashMap<String, Object>> clientlist;
    public ClientListAdapter(Context context, List<HashMap<String, Object>> list) {
        this.mContext = context;
        this.clientlist = list;
    }

    @Override
    public int getCount() {
        return clientlist.size();
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
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_client, null);
            holder.trueName = (TextView) view.findViewById(R.id.list_item_trueNametv);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.trueName.setText("账号:"+clientlist.get(i).get("nickName").toString());
        //holder.Priceet.setText(list.get(i).toString());
        return view;
    }

    class ViewHolder {
        TextView trueName;
    }
}
