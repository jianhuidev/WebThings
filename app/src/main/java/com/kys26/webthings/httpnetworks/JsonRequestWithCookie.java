package com.kys26.webthings.httpnetworks;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 *@ Created by 徐建强 on 2014/12/18.
 * @function:自定义volley请求
 * 继承JsonObjectRequest复写getHeaders方法添加cookies
 * @return null
 */
public class JsonRequestWithCookie extends JsonObjectRequest {

    /**heads添加Map*/
    private Map mHeaders=new HashMap(1);
    /**
     *@ Created by 徐建强 on 2014/12/18.
     * @function:自定义volley请求方法
     * @param errorListener
     * @param jsonRequest
     * @param listener
     * @param url
     * @return null
     */
    public JsonRequestWithCookie(String url, JSONObject jsonRequest, Response.Listener listener,
                         Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }
    /**
     *@ Created by 徐建强 on 2014/12/18.
     * @function:自定义volley请求方法
     * @param errorListener
     * @param jsonRequest
     * @param listener
     * @param url
     * @return null
     */
    public JsonRequestWithCookie(int method, String url, JSONObject jsonRequest, Response.Listener listener,
                         Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }
    /**
     *@ Created by 徐建强 on 2014/12/18.
     * @function添加相应的cookie值
     * @return null
     */
    public void setCookie(String cookie){
        mHeaders.put("Cookie", cookie);
    }

    /**
     *@ Created by 徐建强 on 2014/12/18.
     * @function:复写getHeaders方法
     * @return null
     */
    @Override
    public Map getHeaders() throws AuthFailureError {
        return mHeaders;
    }

}