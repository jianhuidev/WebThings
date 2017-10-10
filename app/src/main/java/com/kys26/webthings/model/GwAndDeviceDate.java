package com.kys26.webthings.model;

import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by lenovo on 2017/8/5.
 */

public class GwAndDeviceDate {

    private String farmname;
    private int kid_value;
    private int kid;
    private int kid2_value;
    private int ndname;
    private int kid_tim;
    private String gwid;
    private int kid2_tim;
    private int kid_stat;
    private String nodeid;
    private int kid2;
    private int kid2_stat;

    public static GwAndDeviceDate analysis(JSONObject object) {
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), GwAndDeviceDate.class);
    }


    public String getFarmname() {
        return farmname;
    }

    public void setFarmname(String farmname) {
        this.farmname = farmname;
    }

    public int getKid_value() {
        return kid_value;
    }

    public void setKid_value(int kid_value) {
        this.kid_value = kid_value;
    }

    public int getKid() {
        return kid;
    }

    public void setKid(int kid) {
        this.kid = kid;
    }

    public int getKid2_value() {
        return kid2_value;
    }

    public void setKid2_value(int kid2_value) {
        this.kid2_value = kid2_value;
    }

    public int getNdname() {
        return ndname;
    }

    public void setNdname(int ndname) {
        this.ndname = ndname;
    }

    public int getKid_tim() {
        return kid_tim;
    }

    public void setKid_tim(int kid_tim) {
        this.kid_tim = kid_tim;
    }

    public String getGwid() {
        return gwid;
    }

    public void setGwid(String gwid) {
        this.gwid = gwid;
    }

    public int getKid2_tim() {
        return kid2_tim;
    }

    public void setKid2_tim(int kid2_tim) {
        this.kid2_tim = kid2_tim;
    }

    public int getKid_stat() {
        return kid_stat;
    }

    public void setKid_stat(int kid_stat) {
        this.kid_stat = kid_stat;
    }

    public String getNodeid() {
        return nodeid;
    }

    public void setNodeid(String nodeid) {
        this.nodeid = nodeid;
    }

    public int getKid2() {
        return kid2;
    }

    public void setKid2(int kid2) {
        this.kid2 = kid2;
    }

    public int getKid2_stat() {
        return kid2_stat;
    }

    public void setKid2_stat(int kid2_stat) {
        this.kid2_stat = kid2_stat;
    }
}
