package com.kys26.webthings.httpnetworks;

import android.app.Activity;
import android.os.Message;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.util.VolleyUtil;

import org.json.JSONObject;

/**
 * @function:volley框架json数据请求方法封装类
 * @author:Created by 徐建强 on 2015/8/10.
 */
public class VolleyJsonRequest {

    /**
     * VolleyJsonRequest打印标识
     */
    private static String TAG = "VolleyJsonRequest";
    /**
     * 定义一个JSONObject将发送的数据进行类型强制转换
     */
    private static JSONObject jsonObject;

    /**
     * @function:volley框架中不带cookie型访问
     * @author:Created by 徐建强 on 2015/11/23.
     * @return:null
     * @param: act, path, json
     */
    public static void JsonRequest(Activity act, String path, JSONObject json, final int what) {
        //对发送数据的情况进行判断，一种为空，一种为json数据
//        if (json != null) {
//            //将要发送的数据先进行强制转换
//            try {
//                jsonObject = new JSONObject(json);
//                Log.i(TAG, "jsonObject" + jsonObject);
//            } catch (Exception e) {
//                Log.i(TAG, "jsonObject强制转换异常" + e.toString());
//            }
//        } else {
//            jsonObject = null;
//        }
        //请求之前，先取消之前的请求（取消还没有进行完的请求）
        //VolleyUtil.getQueue(act).cancelAll(act);
        JsonRequestWithCookie jr = new JsonRequestWithCookie(path, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Message msg = new Message();
                msg.obj = jsonObject.toString();
                msg.what = what;
                MethodTools.HnadlerVOlleyJson.sendMessage(msg);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Message msg = new Message();
                msg.obj = volleyError.toString();
                msg.what = Code.FAILURE;
                MethodTools.HnadlerVOlleyJson.sendMessage(msg);
            }
        });
        // 请求加上Tag,用于取消请求
        jr.setTag(act);
        jr.setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jr.setCookie(MethodTools.sPreFerCookie.getString("cookie", "null"));
        VolleyUtil.getQueue(act).add(jr);
    }

    /**
     * @function:volley框架中带cookie型访问
     * @author:Created by 徐建强 on 2015/11/23.
     * @return:null
     * @param: act, url, json, cookie
     */
    public static void JsonRequestWithCookie(Activity act, String url, String json, String cookie) {
        //对发送数据的情况进行判断，一种为空，一种为json数据
        if (json != null) {
            //将要发送的数据先进行强制转换
            try {
                jsonObject = new JSONObject(json);
                Log.e("lsp", "jsonObject" + jsonObject);
                Log.i(TAG, "jsonObject" + jsonObject);
            } catch (Exception e) {
                Log.i(TAG, "jsonObject强制转换异常" + e.toString());
            }
        } else {
            jsonObject = null;
        }
        //请求之前，先取消之前的请求（取消还没有进行完的请求）
        VolleyUtil.getQueue(act).cancelAll(act);
        //调用volley框架方法发起请求
        JsonRequestWithCookie jsonObjectPostRequest = new JsonRequestWithCookie(url,
                jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Message msg = new Message();
                msg.obj = response.toString();
                msg.what = Code.SUCCESS;
                MethodTools.HnadlerVOlleyJson.sendMessage(msg);
                //打印输出返回结果
                Log.i(TAG, "请求成功" + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Message msg = new Message();
                msg.obj = error.toString();
                msg.what = Code.FAILURE;
                MethodTools.HnadlerVOlleyJson.sendMessage(msg);
                //打印输出返回结果
                Log.i(TAG, "请求失败" + error.toString());
            }
        });
        // 请求加上Tag,用于取消请求
        jsonObjectPostRequest.setTag(act);
        //向服务器发起post请求时加上cookie字段
        jsonObjectPostRequest.setCookie(cookie);
        //添加请求到队列中
        VolleyUtil.getQueue(act).add(jsonObjectPostRequest);
    }
}
