package com.kys26.webthings.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.model.NodeDeviceData;
import com.zhangyx.MyGestureLock.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kys-36 on 2017/5/10.
 *
 * @param
 * @author
 * @function
 */

public class NodeDetailAdapter extends RecyclerView.Adapter<NodeDetailAdapter.ViewHolder> {
    private int num;
    private String TAG = "NodeDetailAdapter";
    private int[] images = new int[]{R.drawable.icon_nh3, R.drawable.temp_left, R.drawable.hum_left, R.drawable.sun_left};
    private String[] name = new String[]{"空气质量监测节点", "温度监测节点", "湿度监测节点", "光照强度检测节点"};
    private int type;
    private List<Integer> temp = new ArrayList<>();
    private List<Integer> hum = new ArrayList<>();
    private List<Integer> NH3 = new ArrayList<>();
    private List<Integer> sunPower = new ArrayList<>();
    private List<Integer> nodeDataList;

    public NodeDetailAdapter(int list, int type, ArrayList<Integer> nodeDataList) {
        num = list;
        this.type = type;
        this.nodeDataList = nodeDataList;
//        List<NodeDeviceData> nodeDeviceDatas = MethodTools.farmDataList
//                .get(num).getNodeDeviceList();
//        if (type == 0) {//pm
//            for (int i = 0; i < nodeDeviceDatas.size(); i++) {
//                if (null != nodeDeviceDatas.get(i).getNH3()) {
//                    Log.i(TAG, "PM:" + nodeDeviceDatas.get(i).getNH3());
//                    NH3.add(Math.round(Integer.decode(nodeDeviceDatas.get(i).getNH3()) / 100f));
//                }
//            }
//        } else if (type == 1) {//temp
//            for (int i = 0; i < nodeDeviceDatas.size(); i++) {
//                if (null != nodeDeviceDatas.get(i).getWendu()) {
//                    Log.i(TAG, "PM:" + nodeDeviceDatas.get(i).getWendu());
//                    temp.add(Math.round(Integer.decode(nodeDeviceDatas.get(i).getWendu()) / 100f));
//                }
//            }
//        } else if (type == 2) {//hum
//            for (int i = 0; i < nodeDeviceDatas.size(); i++) {
//                if (null != nodeDeviceDatas.get(i).getShidu()) {
//                    Log.i(TAG, "PM:" + nodeDeviceDatas.get(i).getShidu());
//                    hum.add(Math.round(Integer.decode(nodeDeviceDatas.get(i).getShidu()) / 100f));
//                }
//            }
//        } else if (type == 3) {//sunpower
//
//        }
//        Log.i("测试数据长度：", "" + num);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_node_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (type == 0) {//pm
            holder.mNum.setText(nodeDataList.get(position) + "ppm");
        } else if (type == 1) {//temp
            holder.mNum.setText(nodeDataList.get(position) + "°");
        } else if (type == 2) {//hum
            holder.mNum.setText(nodeDataList.get(position) + "%");
        } else if (type == 3) {//sunpower
            holder.mNum.setText(nodeDataList.get(position) + "lx");
        }
        holder.mName.setText(name[type] + position);
        holder.mImageView.setImageResource(images[type]);
    }

    @Override
    public int getItemCount() {
        return nodeDataList.size();
//        if (type == 0) {//pm
//            return NH3.size();
//        } else if (type == 1) {//temp
//            return temp.size();
//        } else if (type == 2) {//hum
//            return hum.size();
//        } else {//sunpower
//            return sunPower.size();
//        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mName;
        TextView mNum;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.icon);
            mName = (TextView) itemView.findViewById(R.id.name);
            mNum = (TextView) itemView.findViewById(R.id.num);
        }
    }
}
