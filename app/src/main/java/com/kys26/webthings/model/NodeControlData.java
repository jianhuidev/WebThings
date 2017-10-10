package com.kys26.webthings.model;

import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by kys-36 on 2017/5/18.
 *
 * @param
 * @author
 * @function
 */

public class NodeControlData {
    private String gwname;//网关名称？？
    private String gwid;//网关ID
    private String node_describe;//类型
    private String node_state;//节点状态
    private String ndName;//节点类型
    private String ndname;//节点名称
    private String nodeid;//节点ID
    private String kid_stat;//子节点1状态
    private String kid2_stat;//子节点2状态
    private String state;//节点状态
    private String deviceName;//节点名称
    private List<NodeTimeData> mNodeTimeDataList;//定时项
    private boolean isFanOpen;//判断风机是否开启和关闭
    private ScheduledExecutorService SwitchService;//风机的service
    private int SwitchIndex;
    private ImageView mImageView;//CommandActivity的Item
    private RotateAnimation mAnimator;

    public ImageView getImageView() {
        return mImageView;
    }

    public void setImageView(ImageView imageView) {
        mImageView = imageView;
    }

    public static NodeControlData analysis(JSONObject jsonObject) {
        Gson gson = new Gson();
        return gson.fromJson(jsonObject.toString(), NodeControlData.class);
    }

    public String getGwid() {
        return gwid;
    }

    public void setGwid(String gwid) {
        this.gwid = gwid;
    }

    public String getNdname() {
        return ndname;
    }

    public void setNdname(String ndname) {
        this.ndname = ndname;
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getKid_stat() {
        return kid_stat;
    }

    public void setKid_stat(String kid_stat) {
        this.kid_stat = kid_stat;
    }

    public String getKid2_stat() {
        return kid2_stat;
    }

    public void setKid2_stat(String kid2_stat) {
        this.kid2_stat = kid2_stat;
    }

    public List<NodeTimeData> getNodeTimeDataList() {
        return mNodeTimeDataList;
    }

    public void setNodeTimeDataList(List<NodeTimeData> nodeTimeDataList) {
        mNodeTimeDataList = nodeTimeDataList;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getGwname() {
        return gwname;
    }

    public void setGwname(String gwname) {
        this.gwname = gwname;
    }

    public String getNode_describe() {
        if (node_describe == null || node_describe.equals("") || node_describe.length() == 0) {
            return " ";
        }
        return node_describe;
    }

    public void setNode_describe(String node_describe) {
        this.node_describe = node_describe;
    }

    public String getNode_state() {
        return node_state;
    }

    public void setNode_state(String node_state) {
        this.node_state = node_state;
    }

    public String getNdName() {
        return ndName;
    }

    public void setNdName(String ndName) {
        this.ndName = ndName;
    }

    public String getndname() {
        return ndname;
    }

    public void setndname(String ndname) {
        this.ndname = ndname;
    }

    public String getkid_stat() {
        return kid_stat;
    }

    public void setkid_stat(String kid_stat) {
        kid_stat = kid_stat;
    }

    public String getkid2_stat() {
        return kid2_stat;
    }

    public void setkid2_stat(String kid2_stat) {
        kid2_stat = kid2_stat;
    }

    public boolean isFanOpen() {
        return isFanOpen;
    }

    public void setFanOpen(boolean fanOpen) {
        isFanOpen = fanOpen;
    }

    public void setSwitchService(ScheduledExecutorService switchService) {
        SwitchService = switchService;
    }

    public ScheduledExecutorService getSwitchService() {
        return SwitchService;
    }

    public void setSwitchIndex(int switchIndex) {
        SwitchIndex = switchIndex;
    }

    public int getSwitchIndex() {
        return SwitchIndex;
    }

    public void setAnimator(RotateAnimation animator) {
        mAnimator = animator;
    }

    public RotateAnimation getAnimator() {
        return mAnimator;
    }
}
