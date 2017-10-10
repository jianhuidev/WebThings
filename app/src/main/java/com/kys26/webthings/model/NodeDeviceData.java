package com.kys26.webthings.model;

import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by kys-36 on 2017/5/17.
 *
 * @param
 * @author
 * @function
 */

public class NodeDeviceData {
    private String node_desvribe;//位置
    private String nodeTypeName;//类型
    private String farmname;//所在农场名称
    private String node_state2;//状态码
    private String wendu;//温度
    private String shidu;//湿度
    private String NH3;//NH3
    private String gwname;//网关名称？？
    private String id2;//二进制ID
    private String node_state;//工作状态
    private String state;//开/关
    private String ndName;//节点类型ID
    private String node_name;//节点名称
    private String node_id;//谁知道这是啥

    public static NodeDeviceData analysis(JSONObject object){
        Gson gson = new Gson();
        return gson.fromJson(object.toString(),NodeDeviceData.class);
    }

    public String getNode_desvribe() {
        return node_desvribe;
    }

    public void setNode_desvribe(String node_desvribe) {
        this.node_desvribe = node_desvribe;
    }

    public String getNodeTypeName() {
        return nodeTypeName;
    }

    public void setNodeTypeName(String nodeTypeName) {
        this.nodeTypeName = nodeTypeName;
    }

    public String getFarmname() {
        return farmname;
    }

    public void setFarmname(String farmname) {
        this.farmname = farmname;
    }

    public String getNode_state2() {
        return node_state2;
    }

    public void setNode_state2(String node_state2) {
        this.node_state2 = node_state2;
    }

    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public String getShidu() {
        return shidu;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public String getNH3() {
        return NH3;
    }

    public void setNH3(String NH3) {
        this.NH3 = NH3;
    }

    public String getGwname() {
        return gwname;
    }

    public void setGwname(String gwname) {
        this.gwname = gwname;
    }

    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public String getNode_state() {
        return node_state;
    }

    public void setNode_state(String node_state) {
        this.node_state = node_state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNdName() {
        return ndName;
    }

    public void setNdName(String ndName) {
        this.ndName = ndName;
    }

    public String getNode_name() {
        return node_name;
    }

    public void setNode_name(String node_name) {
        this.node_name = node_name;
    }

    public String getNode_id() {
        return node_id;
    }

    public void setNode_id(String node_id) {
        this.node_id = node_id;
    }
}
