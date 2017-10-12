package com.kys26.webthings.model;

import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by lenovo on 2017/9/16.
 */

public class ClientInforData {

    private String sex;
    private String contactPhone;
    private String trueName;
    private String email;
    private String avatarUrl;
    private String workingAddress;
    private String qq;


    public static ClientInforData analysis(JSONObject object) {
        Gson gson = new Gson();
        return gson.fromJson(object.toString(), ClientInforData.class);
    }
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getWorkingAddress() {
        return workingAddress;
    }

    public void setWorkingAddress(String workingAddress) {
        this.workingAddress = workingAddress;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }
}
