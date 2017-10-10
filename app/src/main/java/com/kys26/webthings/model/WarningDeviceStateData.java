package com.kys26.webthings.model;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Lee on 2017/7/15.
 */

public class WarningDeviceStateData {
    public String gwid;
    public String nodeId;
    public String ndname;
    public String kid;
    public String kidStat;
    public String kidTim;
    public String kidValue;
    public String kid2;
    public String kid2Stat;
    public String kid2Tim;
    public String kid2Value;

    public static WarningDeviceStateData analysis(JSONObject object) {
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), WarningDeviceStateData.class);
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getGwid() {
        return gwid;
    }

    public void setGwid(String gwid) {
        this.gwid = gwid;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNdname() {
        return ndname;
    }

    public void setNdname(String ndname) {
        this.ndname = ndname;
    }

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    public String getKidStat() {
        return kidStat;
    }

    public void setKidStat(String kidstat) {
        this.kidStat = kidstat;
    }

    public String getKidTim() {
        return kidTim;
    }

    public void setKidTim(String kidTim) {
        this.kidTim = kidTim;
    }

    public String getKidValue() {
        return kidValue;
    }

    public void setKidValue(String kidValue) {
        this.kidValue = kidValue;
    }

    public String getKid2() {
        return kid2;
    }

    public void setKid2(String kid2) {
        this.kid2 = kid2;
    }

    public String getKid2Stat() {
        return kid2Stat;
    }

    public void setKid2Stat(String kid2Stat) {
        this.kid2Stat = kid2Stat;
    }

    public String getKid2Tim() {
        return kid2Tim;
    }

    public void setKid2Tim(String kid2Tim) {
        this.kid2Tim = kid2Tim;
    }

    public String getKid2Value() {
        return kid2Value;
    }

    public void setKid2Value(String kid2Value) {
        this.kid2Value = kid2Value;
    }

}
