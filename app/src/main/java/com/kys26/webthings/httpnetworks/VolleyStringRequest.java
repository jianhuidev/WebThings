package com.kys26.webthings.httpnetworks;

import android.app.Activity;
import android.app.Service;
import android.os.Message;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.util.VolleyUtil;

/**
 * @function:volley框架String数据请求方法封装类
 * @author:Created by 徐建强 on 2015/8/10.
 */
public class VolleyStringRequest {

    /**
     * @param act
     * @param cookie
     * @param url
     * @return null
     * @function:volley框架String数据请求方法
     * @author:Created by 徐建强 on 2015/8/10.
     */
    public static void stringRequestWithCookie(Activity act, String url, String cookie) {
        /**输出标识*/
        final String TAG = "com.kys26.webthings.httpnetworks.VolleyStringRequest";
        //请求之前，先取消之前的请求（取消还没有进行完的请求）
        VolleyUtil.getQueue(act).cancelAll(act);
        //实例化请求实体
        StringRequestWithCookie stringRequestWithCookie = new StringRequestWithCookie(url,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object o) {
                        Message msg = new Message();
                        msg.obj = o.toString();
                        msg.what = Code.SUCCESS;
                        MethodTools.HnadlerVOlleyJson.sendMessage(msg);
                        //打印输出返回结果
                        Log.i(TAG, "请求成功" + o.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Message msg = new Message();
                        msg.obj = error.toString();
                        msg.what = Code.FAILURE;
                        MethodTools.HnadlerVOlleyJson.sendMessage(msg);
                        //打印输出错误结果
                        Log.i(TAG, "请求失败" + error.toString());
                    }
                });
        // 请求加上Tag,用于取消请求
        stringRequestWithCookie.setTag(act);
        //向服务器发起post请求时加上cookie字段
        stringRequestWithCookie.setCookie(cookie);
        //添加请求到队列中
        VolleyUtil.getQueue(act).add(stringRequestWithCookie);
    }

    public static void serviceStringRequestWithCookie(Service act, String url, String cookie) {
        /**输出标识*/
        final String TAG = "com.kys26.webthings.httpnetworks.VolleyStringRequest";
        //请求之前，先取消之前的请求（取消还没有进行完的请求）
        VolleyUtil.getQueue(act).cancelAll(act);
        //实例化请求实体
        StringRequestWithCookie stringRequestWithCookie = new StringRequestWithCookie(url,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object o) {
                        Message msg = new Message();
                        msg.obj = o.toString();
                        msg.what = Code.SUCCESS;
                        MethodTools.serviceHandler.sendMessage(msg);
                        //打印输出返回结果
                        Log.i(TAG, "请求成功" + o.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Message msg = new Message();
                        msg.obj = error.toString();
                        msg.what = Code.FAILURE;
                        MethodTools.serviceHandler.sendMessage(msg);
                        //打印输出错误结果
                        Log.i(TAG, "请求失败" + error.toString());
                    }
                });
        // 请求加上Tag,用于取消请求
        stringRequestWithCookie.setTag(act);
        //向服务器发起post请求时加上cookie字段
        stringRequestWithCookie.setCookie(cookie);
        //添加请求到队列中
        VolleyUtil.getQueue(act).add(stringRequestWithCookie);
    }


    /**用这个*/
    public static void stringRequest(Activity act, String url, final int what) {
        /**输出标识*/
        final String TAG = "VolleyStringRequest";
        //请求之前，先取消之前的请求（取消还没有进行完的请求）
//        VolleyUtil.getQueue(act).cancelAll(act);
        //实例化请求实体
        StringRequestWithCookie stringRequestWithCookie = new StringRequestWithCookie(url,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object o) {
                        Message msg = new Message();
                        msg.obj = o.toString();
                        msg.what = what;
                        MethodTools.HnadlerVOlleyJson.sendMessage(msg);
                        //打印输出返回结果
                        Log.i(TAG, "请求成功" + o.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Message msg = new Message();
                        msg.obj = error.toString();
                        msg.what = Code.FAILURE;
                        MethodTools.HnadlerVOlleyJson.sendMessage(msg);
                        //打印输出错误结果
                        Log.i(TAG, "请求失败" + error.toString());
                    }
                }) {
        };
        stringRequestWithCookie.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // 请求加上Tag,用于取消请求
        stringRequestWithCookie.setTag(act);
        //添加请求到队列中
        VolleyUtil.getQueue(act).add(stringRequestWithCookie);
    }
}
