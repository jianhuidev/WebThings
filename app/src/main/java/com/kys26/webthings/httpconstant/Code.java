package com.kys26.webthings.httpconstant;

/**
 * Created by 徐建强 on 2015/8/1.
 */
public class Code {

    /**
     * handler异步加载回调code，
     * 1表示成功
     * 0表示超时失败
     * 2表示登陆时需要保存最新cookie值确定`
     * 命令控制功能下，值表示为两位数，第一位固定为 1
     */
    public static final int SUCCESS = 1;
    public static final int FAILURE = 0;
    public static final int SAVECOOKIE = 2;
    public static final int MENAGE_OPEN = 10;
    public static final int MENAGE_LIGHT = 11;
    public static final int MENAGE_FAN = 12;
    public static final int MENAGE_FIND = 13;
    public static final int LUCKYSEVEN = 7;
    /**
     * http请求返回相应确认码
     */
    public static final int OK = 200;
    /**向网关发送的配置信息*
     * head表示头
     */
    /**=====================*/
    /**
     * 特殊说明
     * 这里有一个地方，硬件配置中换行必须是"\r\n"标识符
     * ========================
     */
    public final static String head = "at+netmode=2\r\n" + "at+wifi_conf=";
    public final static String middle = ",wpa2_aes,";
    public final static String tail = "\r\nat+dhcpc=1\r\n" + "at+remoteip=";
    public final static String portBefor = "\r\nat+remoteport=";
    public final static String last = "\r\nat+remotepro=udp\r\n" +
            "at+timeout=0\r\n" +
            "at+mode=client\r\n" +
            "at+uart=115200,8,n,1\r\n" +
            "at+uartpacklen=500\r\n" +
            "at+uartpacktimeout=10\r\n" +
            "at+net_commit=1\r\n" +
            "at+reconn=1";
    public static final int TIMING_OPEN = 86;
    public static final int TIMING_CLOSE = 87;
    public static final int HANDLE_MODEL_OPEN = 85;
    public static final int HANDLE_MODEL_CLOSE = 34;
    public static final int NH3_OPEN = 89;
    public static final int NH3_CLOSE = 96;
    public static final int COOLING_OPEN = 97;
    public static final int COOLING_CLOSE = 98;
    //定时模式
    public static final int TIME_MODEL_CLOSE = 30;
    public static final int TIME_MODEL_OPEN = 31;
    //普通自动模式
    public static final int AUTO_MODEL_OPEN = 32;
    public static final int AUTO_MODEL_CLOSE = 33;
    //NH3风机自动模式
    public static final int AUTO_ALL_OPEN = 35;
    public static final int AUTO_ALL_CLOSE = 36;
    public static final int AUTO_TMP_OPEN=37;
    public static final int AUTO_NH3_OPEN=38;
    public static final int AUTOMODEL_NH3_OPEN=39;
    public static final int AUTOMODEL_NH3_CLOSE=40;
    public static final int AUTOMODEL_TMP_OPEN=41;
    public static final int AUTOMODEL_TMP_CLOSE=42;
    //节点类型
    public static final int COLLECTION_NODE_NDNAME = 4098;//采集节点，氨气节点ndname;
    public static final int CONTROL_NODE_NDNAME = 4096;//控制节点，类似温湿度节点ndname;
    //节点的ndname
    public static final String FAN_NODE_NDNAME = "1";//风机节点的ndname;
    public static final String COOLING_NODE_NAME = "2";
    public static final int[] AUTOMODEL_NH3=new int []{};
    public static final int[] AUTO_FAN_OPEN= new int[]{32, 35, 37, 38, 39, 41};
    public static final int[] AUTOMODEL_TMP=new int []{};
    public static final int[] AUTO_FAN_CLOSE=new int []{33,36,40,42};
}
