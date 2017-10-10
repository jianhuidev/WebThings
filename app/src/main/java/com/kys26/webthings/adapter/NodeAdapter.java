package com.kys26.webthings.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kys26.webthings.main.MainActivity;
import com.zhangyx.MyGestureLock.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kys-36 on 2017/5/9.
 *
 * @param
 * @author
 * @function
 */

public class NodeAdapter extends RecyclerView.Adapter<NodeAdapter.ViewHolder>{
    public interface onClickListener {
        void onClick(View view,int position);
    }
    onClickListener mOnClickListener;
    public void setOnClickListener(onClickListener onClickListener){
        this.mOnClickListener = onClickListener;

    }
    /**
     * 节点信息
     */
    private List<HashMap<String ,Object>> nodeList;
    private MainActivity mActivity;

    public NodeAdapter(MainActivity mActivity, List<HashMap<String, Object>> nodeList){
        this.mActivity = mActivity;
        this.nodeList = nodeList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_node,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.nodeImage.setImageResource(Integer.decode(nodeList.get(position).get("image").toString()));
        holder.nodeName.setText(nodeList.get(position).get("name").toString());
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onClick(v,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return nodeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        CardView mCardView;
        TextView nodeName;
        ImageView nodeImage;
        public ViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.item_node_card);
            nodeName = (TextView) itemView.findViewById(R.id.node_name);
            nodeImage = (ImageView) itemView.findViewById(R.id.node_image);
        }
    }
}
