package com.kys26.webthings.video;

import com.smaxe.uv.client.INetConnection;
import com.smaxe.uv.client.NetConnection;

import java.util.Map;

/**
 * @author 李赛鹏
 * @class Created by kys_9 on 2017/6/3.
 */

public class NetConnectionListener extends NetConnection.ListenerAdapter {
    /**
     * Constructor.
     */
    public NetConnectionListener() {
    }

    @Override
    public void onAsyncError(final INetConnection source,
                             final String message, final Exception e) {
        System.out.println("NetConnection#onAsyncError: " + message + " "
                + e);
    }

    @Override
    public void onIOError(final INetConnection source, final String message) {
        System.out.println("NetConnection#onIOError: " + message);
    }

    @Override
    public void onNetStatus(final INetConnection source,
                            final Map<String, Object> info) {
        // 2
        System.out.println("NetConnection#onNetStatus: " + info);

        final Object code = info.get("code");

//        if (NetConnection.CONNECT_SUCCESS.equals(code)) {
//            connection.call("voiceconf.call", null, "default", username,
//                    callId);
//        } else {
//            disconnected = true;
//        }
    }
}
