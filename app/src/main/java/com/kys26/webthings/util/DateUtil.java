package com.kys26.webthings.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Lee on 2017/7/20.
 */

public class DateUtil {
    public static String getTimeNow() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        Log.e("当前时间", format.format(new Date()));
        return format.format(new Date());
    }
}
