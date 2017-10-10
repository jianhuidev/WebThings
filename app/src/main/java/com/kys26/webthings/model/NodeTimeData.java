package com.kys26.webthings.model;

import com.google.gson.Gson;
import com.kys26.webthings.util.DataUtil;
import com.kys26.webthings.util.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by kys-36 on 2017/6/2.
 *
 * @param
 * @author
 * @function
 */

public class NodeTimeData {
    private String id;//定时项查询唯一键
    private String deviceId;//节点ID
    private int kid1 = 1;//谁知道这是啥
    private int kid1Stat;//状态 86:开 87:关
    private int kid1Value;//开始时间
    private int kid1Tim;//结束时间
    private int kid2 = 1;//类比以上，在代码中不需要用这些
    private int kid2Stat;//节点状态
    private int kid2Value;//开始时间
    private int kid2Tim;//结束时间
    private int TimingWhat = 0;
    private ScheduledExecutorService TimingService;
    private boolean isInTime = false;//判断是否在定时内;

    public static List<NodeTimeData> getNodeTimeData(JSONArray jsonArray) {
        List<NodeTimeData> list = new ArrayList<>();
        Gson gson = new Gson();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                list.add(gson.fromJson(jsonObject.toString(), NodeTimeData.class));
                String kidTim = jsonObject.getString("kid1Tim");
                String kidValue = jsonObject.getString("kid1Value");
                //判断当前时间是否在时间内;
                if (DataUtil.isTimeBetwenTimeSolt(Integer.valueOf(kidValue), Integer.valueOf(kidTim))) {
                    list.get(i).setInTime(true);
                } else {
                    list.get(i).setInTime(false);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static NodeTimeData getNodeTimeData(JSONObject jsonObject) {
        Gson gson = new Gson();
        return gson.fromJson(jsonObject.toString(), NodeTimeData.class);
    }

    public JSONObject toObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("deviceId", deviceId);
            jsonObject.put("kid1", kid1);
            jsonObject.put("kid1Stat", kid1Stat);
            jsonObject.put("kid1Value", kid1Value);
            jsonObject.put("kid1Tim", kid1Tim);
            jsonObject.put("kid2", kid1);
            jsonObject.put("kid2Stat", kid1Stat);
            jsonObject.put("kid2Value", kid1Value);
            jsonObject.put("kid2Tim", kid1Tim);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getKid1() {
        return kid1;
    }

    public void setKid1(int kid1) {
        this.kid1 = kid1;
    }

    public int getKid1Stat() {
        return kid1Stat;
    }

    public void setKid1Stat(int kid1Stat) {
        this.kid1Stat = kid1Stat;
    }

    public int getKid1Value() {
        return kid1Value;
    }

    public void setKid1Value(int kid1Value) {
        this.kid1Value = kid1Value;
    }

    public int getKid1Tim() {
        return kid1Tim;
    }

    public void setKid1Tim(int kid1Tim) {
        this.kid1Tim = kid1Tim;
    }

    public int getKid2() {
        return kid2;
    }

    public void setKid2(int kid2) {
        this.kid2 = kid2;
    }

    public int getKid2Stat() {
        return kid2Stat;
    }

    public void setKid2Stat(int kid2Stat) {
        this.kid2Stat = kid2Stat;
    }

    public int getKid2Value() {
        return kid2Value;
    }

    public void setKid2Value(int kid2Value) {
        this.kid2Value = kid2Value;
    }

    public int getKid2Tim() {
        return kid2Tim;
    }

    public void setKid2Tim(int kid2Tim) {
        this.kid2Tim = kid2Tim;
    }

    public int getTimingWhat() {
        return TimingWhat;
    }

    public void setTimingWhat(int timingWhat) {
        this.TimingWhat = timingWhat;
    }

    public ScheduledExecutorService getTimingService() {
        return TimingService;
    }

    public void setTimingService(ScheduledExecutorService timingService) {
        this.TimingService = timingService;
    }

    public void setInTime(boolean inTime) {
        isInTime = inTime;
    }

    public boolean isInTime() {
        return isInTime;
    }
}
