
package com.kys26.webthings.recieve;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

//
///**
// * @author 李赛鹏
// * @class Created by kys_9 on 2017/6/20.
// */
//
public class WifiConnectReceiver extends BroadcastReceiver {
    connectWifi mConnectWifi;

    public WifiConnectReceiver(connectWifi mConnectWifi) {
        this.mConnectWifi = mConnectWifi;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            //不对
//            case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
//                int error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0);
//                if (WifiManager.ERROR_AUTHENTICATING == error) {
//                    //密码错误,认证失败
//                    Toast.makeText(context, "wifi密码错误", Toast.LENGTH_SHORT).show();
//                    mConnectWifi.OnErrorConnected();
//                }
//                break;
            //没用
//            case WifiManager.WIFI_STATE_CHANGED_ACTION:
//                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
//                int wifiError = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123);
//                switch (wifiState) {
//                    case WifiManager.WIFI_STATE_DISABLED:
////                        Log.e("H3c", "wifiState:");
//                        break;
//                    case WifiManager.WIFI_STATE_DISABLING:
////                        Log.e("H3c", "wifiState" + wifiState);
//                        break;
//                    //
//                }
//                break;
            // 这个监听wifi的连接状态即是否连上了一个有效无线路由，当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
            // 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，当然刚打开wifi肯定还没有连接到有效的无线
            case WifiManager.NETWORK_STATE_CHANGED_ACTION: {
                Parcelable parcelableExtra = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();
                    boolean isConnected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态
                    if (isConnected) {
                        //连接上之后获取当前连接的wifi名称
                        WifiManager mWifiManager = (WifiManager) context
                                .getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
                        mConnectWifi.OnConnectedWifi(mWifiInfo.getSSID());
//                        Toast.makeText(context, "wifi连接成功", Toast.LENGTH_SHORT).show();
                    } else if (state == NetworkInfo.State.DISCONNECTED) {

                    }
                }
            }
            break;
        }
    }

    public interface connectWifi {
        void OnConnectedWifi(String ConnectedSSID);
    }
}
