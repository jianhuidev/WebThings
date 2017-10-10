package com.kys26.webthings.recieve;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kys26.webthings.interfac.OnControlTimingListener;

/**
 * @author 李赛鹏
 * @class Created by kys_9 on 2017/6/5.
 */

public class TimingReceiver extends BroadcastReceiver {
    OnControlTimingListener mOnControlTimingListener;

    public TimingReceiver(OnControlTimingListener mOnControlTimingListener) {
        this.mOnControlTimingListener = mOnControlTimingListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean action = intent.getBooleanExtra("action", false);
        int position = intent.getIntExtra("position", -1);
        String intentaction = intent.getAction();
        try {
            if (position != -1) {
                if (action)
                    mOnControlTimingListener.onAlarmOpen(position);
                else
                    mOnControlTimingListener.onAlarmClose(position);
            } else {

            }
        } catch (Exception e) {
            Log.e("定时", "接收定时异常:" + e);
        }
    }
}
