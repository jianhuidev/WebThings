package com.kys26.webthings.util.network;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;

import com.zhangyx.MyGestureLock.R;


/**
 * Created by kys_26 on 2015/5/21.
 */
public  final class SetNetwork {
    public static void setNetwork(final Activity act){

        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setIcon(R.drawable.alert_img_1);
        builder.setTitle(R.string.string_activity_SetNetWork_dialog_title);
        builder.setMessage(R.string.string_activity_SetNetWork_dialog_message);
        builder.setPositiveButton(R.string.string_activity_SetNetWork_dialog_Positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = null;
                /**
                 * 判断手机系统的版本！如果API大于10 就是3.0+
                 * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
                 */
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                } else {
                    intent = new Intent();
                    ComponentName component = new ComponentName(
                            "com.android.settings",
                            "com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                act.startActivity(intent);
            }
        });

        builder.setNegativeButton(R.string.string_activity_SetNetWork_dialog_Negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create();
        builder.show();
    }

}
