package com.kys26.webthings.recieve;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kys26.webthings.personalcenter.Data;
import com.kys26.webthings.personalcenter.NoticeActivity;

import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

/**
 * Created by lenovo on 2017/7/20.
 */

public class MyNoticeReceiver extends PushMessageReceiver {

//    public OnAddLeftListener mListener;
//    private int a = 0;
////
////
//    public MyNoticeReceiver(){
//
//    }
//    public MyNoticeReceiver(OnAddLeftListener leftListener){
//        mListener = leftListener;
//        mListener.onAddLeft(a);
//    }

    @Override
    public boolean onNotificationMessageArrived(Context context, PushNotificationMessage message) {
        return false; // 返回 false, 会弹出融云 SDK 默认通知; 返回 true, 融云 SDK 不会弹通知, 通知需要由您自定义。
    }

    @Override
    public boolean onNotificationMessageClicked(Context context, PushNotificationMessage message) {

        Intent intent = new Intent(context, NoticeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        String pushMsg = message.getPushContent();
        String[] pushArr = pushMsg.split("；");
        Data.pushMsg = pushArr[0];
        Data.gwId = pushArr[1];
        Data.pushMsg = message.getPushContent();//获取推送的内容
        Log.e("splitmsg",pushArr[0]+"[]"+pushArr[1]);
        context.startActivity(intent);
        return true; // 返回 false, 会走融云 SDK 默认处理逻辑, 即点击该通知会打开会话列表或会话界面; 返回 true, 则由您自定义处理逻辑。
    }

//    public void setListener(OnAddLeftListener listener) {
//        mListener = listener;
//    }
//
//    public interface OnAddLeftListener {
//        public void onAddLeft(int x);
//    }
}