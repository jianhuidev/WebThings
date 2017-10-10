package com.kys26.webthings.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kys26.webthings.model.WifiBean;
import com.zhangyx.MyGestureLock.R;

import java.util.List;

import static com.kys26.webthings.projectmanage.AddGateWayActivity.TAG;

/**
 * @author 李赛鹏
 *         Created by kys_9 on 2017/3/11.
 */

public class WifiListAdapter extends BaseAdapter {
    //scanResult list;
    private List<WifiBean> beanlist;
    //
    private Context mContext;
    ChangeState mChangeState;

    public WifiListAdapter(List<WifiBean> list, Context mContext, ChangeState mChangestate) {
        this.beanlist = list;
        this.mContext = mContext;
        this.mChangeState = mChangestate;
    }

    @Override
    public int getCount() {
        Log.e(TAG, "size:" + beanlist.size());
        return beanlist.size();
    }

    @Override
    public Object getItem(int i) {
        return beanlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder mViewHolder = null;
        if (mViewHolder == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.wifi_item_adapter, null);
            mViewHolder = new ViewHolder();
            mViewHolder.SSID = (TextView) view.findViewById(R.id.ssid);
            mViewHolder.level = (ImageView) view.findViewById(R.id.wifi_level);
            mViewHolder.state_img = (ImageView) view.findViewById(R.id.wifi_state_img);
            mViewHolder.connectstate = (TextView) view.findViewById(R.id.ConnectState);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }
        mViewHolder.SSID.setText(beanlist.get(i).GetSSID());
        // mViewHolder.level.setImageDrawable(beanlist.get(i).GetWifiLevel());
        if (beanlist.get(i).getCapabilities().contains("WPA") || beanlist.get(i).getCapabilities().contains("wpa")) {
            Log.i("river", "wpa");
            mViewHolder.level.setImageResource(R.drawable.wifi_lock);
        } else if (beanlist.get(i).getCapabilities().contains("WEP") || beanlist.get(i).getCapabilities().contains("wep")) {
            Log.i("river", "wep");
            mViewHolder.level.setImageResource(R.drawable.wifi_lock);
        } else {
            mViewHolder.level.setImageResource(R.drawable.wifi_unlock);
            Log.i("river", "no");
        }
//        if (mChangeState != null) {
//            if (i == mChangeState.setConnect()) {
//                Log.e(TAG,"接口回调");
//                mViewHolder.connectstate.setText("已连接");
//                mViewHolder.state_img.setImageResource(R.drawable.check_blue);
//                mViewHolder.state_img.setVisibility(View.VISIBLE);
//            }
//        }
        mViewHolder.level.setImageLevel(beanlist.get(i).GetWifiLevel());
        return view;
    }

    private final class ViewHolder {
        TextView SSID;
        ImageView level;
        ImageView state_img;
        TextView connectstate;
    }

    public interface ChangeState {
        int setConnect();

    }

}
