package com.kys26.webthings.httpconstant;

/**
 * Created by 徐建强 on 2015/8/1.
 */
public class Path {
    /**
     * 测试阶段服务器请求地址
     */
    public final static String host = "http://123.207.167.208:8080/";
    //  public final static String host = "http://192.168.87.59:8080/";
    public final static String videoHost = "http://192.168.87.136/video/";
    public final static String videoIp = "192.158.87.59";
    //public final static String host = "http://172.18.1.101:8080/controller/";
    /**
     * 登陆请求地址
     */
    public final static String login = "loginCheck.do";
    /**
     * 折线图请求地址(历史查询)
     */
    public final static String curveChart = "historyQuery.do";
    /**
     * 初始化网关向后台提交的用户信息
     */
    public final static String checkgateway = "checkgateway.do";
    /**
     * 用户注册账号接口
     */
    public final static String registerAccount = "registerClient.do";
    /**
     * 初始化网关选择的wifi检查联通性地址
     */
    public final static String linkCheck = "linkcheck.do";//*******************************************************
    /**
     * 登陆密码修改通信地址
     */
    public final static String retrievepassword = "updatePsw.do";
    /**
     * 实时获取农场列表信息数据
     */
    public final static String framData = "realTimeQueryByMobile.do";//********************************************
    /**
     * 验证码获取地址
     */
    public final static String verify = "getVerifyCode.do";
    /**
     * 提交个人用户名，获取拥有农场，网关信息等查看历史数据连接地址
     */
    public final static String userFramListHistory = "getFarmAndGw.do";
    /**
     * 网关连接所需IP地址
     */
    public final static String gatewagHost = "192.168.16.254";
    /**
     * 网关连接时候所需的端口号
     */
    public final static int gatewayPort = 5000;
    /**
     * 互易无线短信验证地址
     */
    public static String urlSMS = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";
    /**
     * @auther 窦文
     */
    public static final String URL_GET_USERALL = "userEmployeeManager.do";//获取农场主角色下的所有用户
    public static final String URL_CHARGE = "getRoles.do";//获取农场主管理用户
    public static final String URL_GET_USER = "getEmployees.do";//获取对应角色下的用户
    public static final String URL_NEW_USER = "registerClient.do";//新增用户
    public static final String URL_DELET_USER = "delClient2.do";//删除用户
    public static final String URL_CHANGE_USER = "updateClient2.do";//修改用户
    public static final String URL_MANAGE_FARM = "userGetFarmData.do";//管理兔场
    public static final String URL_CHANGE_FARM = "updateFarm.do";//修改兔场
    public static final String URL_MANAGE_GROUND = "getGateWayData.do";//管理场地
    public static final String URL_CHANGE_GROUND = "updateGateWay.do";//修改场地
    public static final String URL_DEVICEDATA = "userGetDeviceData.do";//设备管理
    public static final String URL_CHANGEDEVICE = "updateOneDevice.do";//设备修改
    public static final String URL_HISTORY = "historyQuery.do";//历史数据查询
    public static final String URL_GET_LIGHT = "userGetDengData.do";//获取灯状态
    public static final String URL_GET_FAN = "userGetShanData.do";//获取风扇状态
    public static final String URL_GET_QUERY = "userGetQueryData.do";//获取某个网关下传感器数据
    public static final String URL_COMMAND = "sendCommand.do";//控制命令
    public static final String URL_OPEN_CLOSE = "userGetGateWayData.do";//启用禁用接口
    public static final String URL_GET_CONTROL = "getEmployees2.do";//获取浏览/控制用户;
    /***
     * @author 李赛鹏
     */
    //新建项目模块
    public static final String URL_GET_CLIENTNAME = "getClientByName.do";//获取查询客户信息
    public static final String URL_GET_FARMTYPE = "getApplicationType.do";//获取查询农场类型
    public static final String URL_GET_FEEDTYPE = "getFeedingType.do";//获取查询客户信息接口
    public static final String URL_GET_ALLCLIENT = "getAllClient.do";//获取所有客户信息
    public static final String URL_NEXT_STEP = "addProjectBasicInfo.do";//新建项目下步接口
    public static final String URL_ADD_CLIENT = "addFarmOwner.do";//获取添加客户接口
    public static final String URL_GETGWNODE = "userGetGwNode.do";
    //添加网关(节点)模块
    public static final String URL_GET_DEVICETYPE = "getDeviceTypeData.do";//添加网关节点设备类型
    public static final String URL_ADD_NETDEVICE = "addProjectDevice.do";//添加项目设备
    public static final String URL_UPDATE_PROJECT = "updateProject.do";//更新项目
    public static final String URL_GET_NODE = "userGetGwNode.do";//获取网关节点
    //第四步更新网关位置
    public static final String URL_UPDATE_POSITION = "updataPosition.do";
    //Rtmp视频地址
    //public static String RTMPURL="rtmp://live.hkstv.hk.lxdns.com/live/hks";
    public static String RTMPURL = "rtmp://192.168.0.105/chapter9";
    // public static String RTMPURL="rtmp://192.168.0.102/chapter9/167772160_chn1_live";
    //视频那块
    public static final String URL_GET_ARM = "getRed5Ip.do";//视频获取开发板等数据
    public static final String URL_GET_CHANNEL = "getChannel.do";//视频获取通道等信息
    public static final String URL_GET_PRESETTING = "getYuZhiWei.do";//获得预置位
    public static final String URL_GET_Control = "userGetNodeDataState.do";//获得可控节点
    public static final String URL_SAVE_WARNING = "saveWarning.do";//保存告警信息
    public static final String URL_WARNING = "warningControl.do";//轮询接口（拟定）
    public static final String URL_GET_ONEWARNING = "getonewarning.do";//得到某个告警信息
    public static final String URL_GET_DENGFEN = "getdengandfengji.do";//获取灯和风机（gwid）
    public static final String URL_GRT_CONTROLER_STATE = "getNodeDataStateByNodeid.do";//获取节点状态
    public static final String URL_GET_DEVICE = "userGetAll2DeviceData.do";//获取所有检测节点信息
    public static final String URL_QUERY_TIME = "getTimings.do";//获取所有定时信息
    public static final String URL_GET_TIME_STATE = "getTimingData.do";//获得某个定时的状态
    public static final String URL_SAVE_TIME = "saveTiming.do";//保存一个定时
    public static final String URL_UPDATE_TIME = "updateTiming.do";//更新（修改）一个定时
    public static final String URL_DELETE_TIM = "deleteTiming.do";//删除一个定时
    public static final String URL_CONTROL_TIM = "sendCommand.do";//开关定时
    public static final String URL_UPDATE_FARM = "updateOneFarm.do";//更改农场名称
    public static final String URL_UPDATE_NODENAME = "updateOneDevice.do";//更改风机节点名称
    public static final String URL_GET_WARNING = "getWarningDate.do";//获取告警信息
    public static final String URL_UPDATE_WARNING = "saveWarning.do";//更新告警信息
    public static final String URL_GET_KIDSTATE = "getDeviceDatasByNicknameAndGwid.do";//获取所有节点的状态
    public static final String URL_GET_COOLING = "userGetNodeDataState1.do";//单独获取降温这里
    public static final String URL_EXIT = "quit.do";//退出接口
    public static final String URL_G_A_D = "getGwAndDeviceDate.do";

    public static final String URL_GET_CLIENT = "clientInformationShow.do";//获取用户信息
    public static final String URL_UPDATA_CLIENT = "updateClient.do";//更改用户信息
    public static final String URL_UPDATA_CLIENTIMG = "updateClientimg.do";//上传用户头像
}