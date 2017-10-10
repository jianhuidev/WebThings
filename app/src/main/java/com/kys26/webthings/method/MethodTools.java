package com.kys26.webthings.method;

import android.content.SharedPreferences;
import android.os.Handler;

import com.google.gson.Gson;
import com.kys26.webthings.bean.GatewayIdBean;
import com.kys26.webthings.model.ClientInforData;
import com.kys26.webthings.model.FarmData;
import com.kys26.webthings.model.GwAndDeviceDate;
import com.kys26.webthings.model.NodeControlData;
import com.kys26.webthings.model.TimeData;
import com.kys26.webthings.model.VideoChannelData;
import com.kys26.webthings.util.Sessionutil;

import java.net.CookieManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by kys_26 徐建强 on 2015/6/7.
 */
public final class MethodTools {

    /**
     * pass by value of activity
     */
    public static Sessionutil session = Sessionutil.getSessionUtil();
    /**
     * Gson of Google
     */
    public static Gson gson = new Gson();
    /**
     * 自定义Httpjson请求类中的异步加载器
     */
    public static Handler HnadlerVOlleyJson;
    /**
     * 存储舒适度数据
     */
    public static SharedPreferences comShare;
    public static SharedPreferences.Editor comEdit;
    /**
     * serviceHandler
     */
    public static Handler serviceHandler;
    /**
     * 农场实时查询获取后台农场列表信息数据
     */
    public static String framData;
    /**
     * App中小型数据保存SharedPreferences声明
     */
    public static SharedPreferences sPreFerCookie;
    /**
     * App中小型数据保存SharedPreferences编辑器声明
     */
    public static SharedPreferences.Editor editor;
    /**
     * 这个Handler是用来将cookie值返回给相应的请求类
     */
    public static Handler handlerCookieVerify;
    /**
     * 登陆请求时用来传服务端返回值的Handler
     */
    public static Handler handlerJson;
    /**
     * 保存农场信息数据
     */
    public static List<HashMap<String, Object>> listdata_farm;
    /**
     * 保存兔舍信息数据
     */
    public static List<HashMap<String, Object>> listdata_gw = new ArrayList<HashMap<String, Object>>();
    /**
     * 下拉加载判断
     */
    public static boolean state = false;
    /**
     * 保存节点信息 第一层：网关  第二层：节点
     */
    public static ArrayList<List<HashMap<String, Object>>> nodeList = new ArrayList<List<HashMap<String, Object>>>();
    /**
     * 保存告警信息
     */
    public static List<HashMap<String, Object>> warningList = new ArrayList<HashMap<String, Object>>();
    /**
     * 保存控制节点信息
     */
    public static List<HashMap<String, Object>> controlList = new ArrayList<HashMap<String, Object>>();
    /*保存温度*/
    public static List<HashMap<String, Object>> tempList = new ArrayList<HashMap<String, Object>>();
    /*保存湿度*/
    public static List<HashMap<String, Object>> humList = new ArrayList<HashMap<String, Object>>();
    /*新建项目中的一个获取所有客户的Handler*/
    public static Handler NewJsonHandler;
    /*新建项目第二步和第三的listHandler*/
    public static Handler listHandler;
    //定义一个全局的cookieManager
    public static java.net.CookieManager CookieManager = new CookieManager();
    /**
     * 定义一个SharedPerferences保存Cookie和其他数据
     */
    public static SharedPreferences cookiePerferences;

    /**
     * 保存farm
     */
    public static List<FarmData> farmDataList = new ArrayList<>();
    /**
     * 时间列表（秒的形式保存）
     */
    public static List<TimeData> timelist = new ArrayList<>();
    /**
     * 视频通道节点
     */
    public static List<VideoChannelData> mVideoChannelData = new ArrayList<>();
    /**
     * 要初始化的网关的id;
     */
    public static GatewayIdBean mGatewayIdBean = new GatewayIdBean();
    /**
     * 储存风机和降温的List
     */
    public static List<NodeControlData> mCoolingDataList = new ArrayList<>();
    /**
     *
     */
    public static List<GwAndDeviceDate> mGwAndDeviceDates = new ArrayList<>();
    /**
     * 获取Nodeid() 为"00-00-00-00"  的网关数据
     */
    public static List<GwAndDeviceDate> mGWStateList = new ArrayList<>();

    public static List<ClientInforData> mClientInforData = new ArrayList<>();//用户信息
}
