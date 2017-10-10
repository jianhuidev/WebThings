package com.kys26.webthings.model;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by kys-36 on 2017/6/20.
 *
 * @param
 * @author
 * @function
 */

public class WarningData {
    private int warningValue;//告警值
    private String gwid;//网关ID
    private int autoControl;//自动控制是否打开
    private String ndname;//类型ID
    private List<String> warningDevice;//开启的节点ID
    public static WarningData analysis(JSONObject jsonObject) {
        Gson gson = new Gson();
        return gson.fromJson(jsonObject.toString(), WarningData.class);
    }
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public int getWarningValue() {
        return warningValue;
    }

    public void setWarningValue(int warningValue) {
        this.warningValue = warningValue;
    }

    public String getGwid() {
        return gwid;
    }

    public void setGwid(String gwid) {
        this.gwid = gwid;
    }

    public int getautoControl() {
        return autoControl;
    }

    public void setautoControl(int autoControl) {
        this.autoControl = autoControl;
    }

    public String getNdname() {
        return ndname;
    }

    public void setNdname(String ndname) {
        this.ndname = ndname;
    }

    public List<String> getWarningDevice() {
        return warningDevice;
    }

    public void setWarningDevice(List<String> warningDevice) {
        this.warningDevice = warningDevice;
    }
}
