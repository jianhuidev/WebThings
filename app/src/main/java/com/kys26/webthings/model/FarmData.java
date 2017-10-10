package com.kys26.webthings.model;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kys-36 on 2017/5/16.
 *
 * @param
 * @author
 * @function
 */

public class FarmData {
    private String farm_address;//地址
    private String nickName;//用户
    private String ichnography;//平面图？？
    private String gw_Id;//网关ID？？
    private String describe;
    private String farm_id;//农场id
    private String farm_name;//农场名称
    private List<NodeDeviceData> mNodeDeviceList = new ArrayList<>();   //农场下检测节点
    private List<NodeVideoData> mVideoNodeList = new ArrayList<>();
    private List<NodeControlData> mNodeSideWind = new ArrayList<>();    //农场下通风节点(这是一个坑)
    private List<NodeControlData> mCooling = new ArrayList<>();         //农场下降温节点(这是一个坑)
    private List<NodeControlData> mHumidification = new ArrayList<>();  //农场下加湿节点(这是一个坑)
    private List<NodeControlData> mHeating = new ArrayList<>();         //农场下加温节点(这是一个坑)
    private List<NodeControlData> mDung = new ArrayList<>();            //农场下除粪节点(这是一个坑)
    private List<NodeControlData> mLight = new ArrayList<>();           //农场下照明节点(这是一个坑)
    private List<NodeControlData> mFeed = new ArrayList<>();            //农场下喂食节点(这是一个坑)
    private List<NodeControlData> mFillLight = new ArrayList<>();       //农场下补光节点(这是一个坑)
    private List<NodeControlData> mSterilization = new ArrayList<>();   //农场下杀菌节点(这是一个坑)

    private List<HistoryData> mHistoryDatas = new ArrayList<>();//农场下历史记录

    public static FarmData analysis(JSONObject object) {
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), FarmData.class);
    }

    public List<NodeVideoData> getVideoNodeList() {
        return mVideoNodeList;
    }

    public void setVideoNodeList(List<NodeVideoData> mVideoNodeList) {
        this.mVideoNodeList = mVideoNodeList;
    }

    public String getGw_Id() {
        return gw_Id;
    }

    public void setGw_Id(String gw_Id) {
        this.gw_Id = gw_Id;
    }

    public List<NodeControlData> getCooling() {
        return mCooling;
    }

    public void setCooling(List<NodeControlData> cooling) {
        mCooling = cooling;
    }

    public List<NodeControlData> getHumidification() {
        return mHumidification;
    }

    public void setHumidification(List<NodeControlData> humidification) {
        mHumidification = humidification;
    }

    public List<NodeControlData> getHeating() {
        return mHeating;
    }

    public void setHeating(List<NodeControlData> heating) {
        mHeating = heating;
    }

    public List<NodeControlData> getDung() {
        return mDung;
    }

    public void setDung(List<NodeControlData> dung) {
        mDung = dung;
    }

    public List<NodeControlData> getLight() {
        return mLight;
    }

    public void setLight(List<NodeControlData> light) {
        mLight = light;
    }

    public List<NodeControlData> getFeed() {
        return mFeed;
    }

    public void setFeed(List<NodeControlData> feed) {
        mFeed = feed;
    }

    public List<NodeControlData> getFillLight() {
        return mFillLight;
    }

    public void setFillLight(List<NodeControlData> fillLight) {
        mFillLight = fillLight;
    }

    public List<NodeControlData> getSterilization() {
        return mSterilization;
    }

    public void setSterilization(List<NodeControlData> sterilization) {
        mSterilization = sterilization;
    }

    public List<HistoryData> getHistoryDatas() {
        return mHistoryDatas;
    }

    public void setHistoryDatas(List<HistoryData> historyDatas) {
        mHistoryDatas = historyDatas;
    }

    public List<NodeControlData> getNodeSideWind() {
        return mNodeSideWind;
    }

    public void setNodeSideWind(List<NodeControlData> nodeSideWind) {
        mNodeSideWind = nodeSideWind;
    }

    public List<NodeDeviceData> getNodeDeviceList() {
        return mNodeDeviceList;
    }

    public void setNodeDeviceList(List<NodeDeviceData> list) {
        mNodeDeviceList = list;
    }

    public String getFarm_address() {
        return farm_address;
    }

    public void setFarm_address(String farm_address) {
        this.farm_address = farm_address;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getIchnography() {
        return ichnography;
    }

    public void setIchnography(String ichnography) {
        this.ichnography = ichnography;
    }

    public String getgw_Id() {
        return gw_Id;
    }

    public void setgw_Id(String gw_Id) {
        this.gw_Id = gw_Id;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getFarm_id() {
        return farm_id;
    }

    public void setFarm_id(String farm_id) {
        this.farm_id = farm_id;
    }

    public String getFarm_name() {
        return farm_name;
    }

    public void setFarm_name(String farm_name) {
        this.farm_name = farm_name;
    }
}
