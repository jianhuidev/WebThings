package com.kys26.webthings.model;

import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by Lee on 2017/8/1.
 */

public class VideoChannelData {
    private String channel;
    private String channelName;

    public static VideoChannelData analysis(JSONObject object) {
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), VideoChannelData.class);
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannel() {
        return channel;
    }

    public String getChannelName() {
        return channelName;
    }
}
