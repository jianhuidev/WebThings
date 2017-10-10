package com.kys26.webthings.util;

import android.util.Log;

import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.model.NodeTimeData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

/**
 * Created by qinghua on 2015/6/16.
 */
public class DataUtil {
    /**
     * 将时间格式转换为毫秒
     *
     * @param format 时间格式 例如 yyyy-MM-dd HH:mm:ss   yyyy-MM-dd  HH:mm:ss
     * @param data
     * @return
     */
    public static long dataToMillisecond(String format, String data) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(data).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 将毫秒转换为时间格式
     *
     * @param format   时间格式 例如 yyyy-MM-dd HH:mm:ss   yyyy-MM-dd  HH:mm:ss
     * @param dateTime
     * @return
     */
    public static String getFormatedDateTime(String format, long dateTime) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(format);
        return sDateFormat.format(new Date(dateTime + 0));
    }

    /**
     * 返回当前日期 无时间
     *
     * @return
     */
    public static String getCurrentDate() {
        return getFormatDateTime(new Date(), "yyyy/MM/dd");
    }

    /**
     * 返回当前日期+时间
     *
     * @return
     */
    public static String getCurrentDateTime() {
        return getFormatDateTime(new Date(), "yyyy/MM/dd HH:mm:ss");
    }

    /**
     * 格式化
     *
     * @param date
     * @param format
     * @return
     */
    public static String getFormatDateTime(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 判断当前日期是星期几
     *
     * @param pTime 设置的需要判断的时间  //格式如2012-09-08
     * @return dayForWeek 判断结果
     * @Exception 发生异常
     */

//  String pTime = "2012-03-12";
    public static String getWeek(String pTime) {
        String Week = "星期";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            Week += "一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            Week += "二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            Week += "三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            Week += "四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            Week += "五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            Week += "六";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            Week += "日";
        }
        return Week;
    }

    /**
     * 更改日期格式
     *
     * @param string
     * @return
     */
    public static String changeFormat(String string, int i) {
        StringBuffer stringBuffer = new StringBuffer();
        StringTokenizer tokenizer;
        switch (i) {
            case 1:
                if (string.contains("年")) {
                    tokenizer = new StringTokenizer(string, "年");
                    tokenizer.nextToken();
                    stringBuffer.append(tokenizer.nextToken());
                } else if (string.contains("-")) {
                    tokenizer = new StringTokenizer(string, "-");
                    tokenizer.nextToken();
                    stringBuffer.append(tokenizer.nextToken());
                    stringBuffer.append("-");
                    stringBuffer.append(tokenizer.nextToken());
                }
                return stringBuffer.toString();
            case 2:
                if (string.contains("年")) {
                    tokenizer = new StringTokenizer(string, "年");
                    tokenizer.nextToken();
                    stringBuffer.append(tokenizer.nextToken());
                } else if (string.contains("-")) {
                    tokenizer = new StringTokenizer(string, "-");
                    tokenizer.nextToken();
                    stringBuffer.append(tokenizer.nextToken());
                }
                return stringBuffer.toString();
            case 3:
//                string.replace('-','/');
                return string;
        }
        return "";
    }

    public static boolean isAllowed(int start, int end, int position) {
        if (start >= end) {
            return false;
        }
        for (int i = 0; i < MethodTools.timelist.size(); i++) {
            if (i != position) {
                if (start <= MethodTools.timelist.get(i).getStart() && end >= MethodTools.timelist.get(i).getEnd()) {
                    return false;
                } else if (start >= MethodTools.timelist.get(i).getStart() && start <= MethodTools.timelist.get(i).getEnd()) {
                    return false;
                } else if (end >= MethodTools.timelist.get(i).getStart() && end <= MethodTools.timelist.get(i).getEnd()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断当前时间是否在两个时间段内
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 是否
     */
    public static boolean isTimeBetwenTimeSolt(int start, int end) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
        return new Date().before(new Date(zero.getTime() + end * 60 * 1000)) && new Date().after(new Date(zero.getTime() + start * 60 * 1000));
    }

    /**
     * 时间排序
     *
     * @param list
     */
    public static void sorting(List<NodeTimeData> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            int k = i;
            for (int j = k + 1; j < list.size(); j++) {
                if (list.get(j).getKid1Value() < list.get(k).getKid1Value()) {
                    k = j;
                }
            }
            if (i != k) {
                NodeTimeData timeData = list.get(i);
                list.remove(i);
                if (i < list.size()) {
                    list.add(i, list.get(k - 1));
                } else {
                    list.add(list.get(k - 1));
                }
                list.remove(k);
                if (k < list.size()) {
                    list.add(k, timeData);
                } else {
                    list.add(timeData);
                }
            }
        }
    }

    public static long getTodayZero() {
        long current = System.currentTimeMillis();
        long zero = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
        return zero;
    }

    public static String getTimeNow() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        Log.e("当前时间", format.format(new Date()));
        return format.format(new Date());
    }
}
