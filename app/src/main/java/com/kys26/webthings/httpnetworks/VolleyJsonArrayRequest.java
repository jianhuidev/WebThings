package com.kys26.webthings.httpnetworks;

import android.app.Activity;
import android.os.Message;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.util.VolleyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author kys_26使用者：徐建强  on Date: 2015-11-26-20
 * @function:访问服务端的jsonArray数据
 * @return webData by Handler
 */
public class VolleyJsonArrayRequest {

    /**
     * VolleyJsonRequest打印标识
     */
    private String TAG = "com.kys26.webthings.networks.VolleyJsonArrayRequest";

    /**
     * @param act
     * @param path
     * @return webData by Handler
     * @author kys_26使用者：徐建强  on Date: 2015-11-26-20
     * @function:访问服务端的jsonArray数据 不带cookie型访问
     */
    public static void JsonArrayRequest(Activity act, String path) {
        //请求之前，先取消之前的请求（取消还没有进行完的请求）
        VolleyUtil.getQueue(act).cancelAll(act);
        //调用自定义volley框架的JsonArray请求，可带cookie型，不带cookie型如下声明即可
        //JsonArrayRequest jsonArrayRequest=new JsonArrayRequest();
        JosnArrayRequestWithCookie jr = new JosnArrayRequestWithCookie(path,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Message msg = new Message();
                        msg.obj = response.toString();
                        msg.what = Code.SUCCESS;
                        MethodTools.HnadlerVOlleyJson.sendMessage(msg);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Message msg = new Message();
                msg.obj = error.toString();
                Log.e("tag", "msg.obj:" + msg.obj.toString());
                msg.what = Code.FAILURE;
                MethodTools.HnadlerVOlleyJson.sendMessage(msg);
            }
        });
        // 请求加上Tag,用于取消请求
        jr.setTag(act);
        //发送时添加cookie
//        jr.setCookie(cookie);
        VolleyUtil.getQueue(act).add(jr);
    }

    public static void StringRequest(Activity act, String path, String cookie) {
        //请求之前，先取消之前的请求（取消还没有进行完的请求）
        VolleyUtil.getQueue(act).cancelAll(act);
        //调用自定义volley框架的JsonArray请求，可带cookie型，不带cookie型如下声明即可
        //JsonArrayRequest jsonArrayRequest=new JsonArrayRequest();
        JosnArrayRequestWithCookie jr = new JosnArrayRequestWithCookie(path,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Message msg = new Message();
                        msg.obj = response.toString();
                        msg.what = Code.SUCCESS;
                        MethodTools.handlerJson.sendMessage(msg);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Message msg = new Message();
                msg.obj = error.toString();
                Log.e("stringrespone", "msg.obj:" + msg);
                msg.what = Code.FAILURE;
                MethodTools.handlerJson.sendMessage(msg);
            }
        });
        // 请求加上Tag,用于取消请求
        jr.setTag(act);
        //发送时添加cookie
        jr.setCookie(cookie);
        VolleyUtil.getQueue(act).add(jr);
    }
}
