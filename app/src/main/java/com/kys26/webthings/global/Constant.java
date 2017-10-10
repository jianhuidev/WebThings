package com.kys26.webthings.global;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Constant {

    public static final boolean DEBUG = true;
    public static final String sharePath = "chxue8_share";
    public static final String USERSID = "user";
    public static final String CITYID = "cityId";
    public static final String CITYNAME = "cityName";
    public static final String DEFAULTCITYID = "1001";
    public static final String DEFAULTCITYNAME = "�Ͼ�";

    public static final String USERNAMECOOKIE = "chxue8Name";
    public static final String USERPASSWORDCOOKIE = "chxue8Password";
    public static final String USERPASSWORDREMEMBERCOOKIE = "chxue8remember";

    public static final int timeOut = 12000;
    public static final int connectOut = 12000;
    public static final int getOut = 60000;

    public static final int downloadComplete = 1;
    public static final int undownLoad = 0;
    public static final int downInProgress = 2;
    public static final int downLoadPause = 3;

    public static final String BASEURL = "http://www.chxue8.com/";
    public final static String ADURL = BASEURL + "PublicServlet?methodName=getSetting";

    public final static String ADDOVERLAYURL = BASEURL + "action/gfoverlay.do?methodName=addOverlayMobile";

    //public static TreeSet<String> GwNodeTree;
    public static List<List> GwNodeList = new ArrayList<List>();//下有两层
    public static List<List> dataList = new ArrayList<List>();//层1
    public static List<List> videoList = new ArrayList<List>();//层2
    /**
     * 总List,各层：0：数据网关 1：视频网关
     */
    public static List<HashMap<String , Object>> positionList = new ArrayList<HashMap<String, Object>>();
    public static int datanum = 0;
    public static int videonum = 0;
    public static int lightnum=0;
    public static int windnum=0;
    public static int tmpnum=0;
    public static int sunnum=0;
    public static int nh3num=0;
    public static int co2num=0;
    public static int rednum=0;
    //shujuwangguan
    public static List<HashMap<String, Object>> datapositionList = new ArrayList<HashMap<String, Object>>();
    //shipinwangguan
    public static List<HashMap<String, Object>> videopositionList = new ArrayList<HashMap<String, Object>>();
    //存入价格类型数据
    public static List<HashMap<String, Object>> pricelist = new ArrayList<HashMap<String, Object>>();
    //添加节点下的二级列表
    public static List<ArrayList<HashMap<String,Object>>> childposition= new ArrayList<ArrayList<HashMap<String, Object>>>();

    public static List<HashMap<String,Object>> lightpositionList=new ArrayList<HashMap<String, Object>>();
    public static List<HashMap<String,Object>> windpositionList=new ArrayList<HashMap<String, Object>>();
    public static List<HashMap<String,Object>> tmppositionList=new ArrayList<HashMap<String, Object>>();
    public static List<HashMap<String,Object>> sunpositionList=new ArrayList<HashMap<String, Object>>();
    public static List<HashMap<String,Object>> nh3positionList=new ArrayList<HashMap<String, Object>>();
    public static List<HashMap<String,Object>> co2positionList=new ArrayList<HashMap<String, Object>>();
    public static List<HashMap<String,Object>> redpositionList=new ArrayList<HashMap<String, Object>>();
    public static HashMap map = new HashMap();
    public static String ProjectId;
}
