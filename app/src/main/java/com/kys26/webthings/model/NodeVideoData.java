package com.kys26.webthings.model;

import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by lenovo on 2017/7/31.
 */

public class NodeVideoData {

    private String yuntaitype;
    private String password;
    private String tongxin;
    private String ip;
    private String red5Id;
    private String mingwen;
    private String userId;
    private int videoNodeId;
    private String videoNodeName;

    public static NodeVideoData analysis(JSONObject object) {
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), NodeVideoData.class);
    }

    public int getVideoNodeId() {
        return videoNodeId;
    }

    public void setVideoNodeId(int videoNodeId) {
        this.videoNodeId = videoNodeId;
    }

    public String getVideoNodeName() {
        return videoNodeName;
    }

    public void setVideoNodeName(String videoNodeName) {
        this.videoNodeName = videoNodeName;
    }

    public String getYuntaitype() {
        return yuntaitype;
    }

    public void setYuntaitype(String yuntaitype) {
        this.yuntaitype = yuntaitype;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTongxin() {
        return tongxin;
    }

    public void setTongxin(String tongxin) {
        this.tongxin = tongxin;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRed5Id() {
        return red5Id;
    }

    public void setRed5Id(String red5Id) {
        this.red5Id = red5Id;
    }

    public String getMingwen() {
        return mingwen;
    }

    public void setMingwen(String mingwen) {
        this.mingwen = mingwen;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
