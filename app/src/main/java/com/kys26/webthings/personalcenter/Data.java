package com.kys26.webthings.personalcenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/7/24.
 */

public class Data {

    public static int position ;

    /**融云链接是用的token*/
    public static String token;
    /**推送的信息*/
    public static String pushMsg  = "";
    /**通知*/
    public static List<Object> noticeList = new ArrayList<>();
    /**通知的个数*/
    //public static int x = 0;
    public static int flag = 0;

    public static String gwId = "";
}
