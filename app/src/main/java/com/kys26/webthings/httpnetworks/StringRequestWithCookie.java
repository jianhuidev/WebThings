package com.kys26.webthings.httpnetworks;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kys_26使用者：徐建强  on Date: 2015-12-04-14-${Minutes}
 * @function:继承volley底部类StringRequest完成string数据请求并附带cookie值
 * @return null
 */
public class StringRequestWithCookie  extends StringRequest{
    /**heads添加Map*/
    private Map mHeaders=new HashMap(1);
    /**
     *@ Created by 徐建强 on 2014/12/18.
     * @function:自定义volley请求方法
     * @param errorListener
     * @param listener
     * @param url
     * @return null
     */
    public StringRequestWithCookie(String url, Response.Listener listener,
                                 Response.ErrorListener errorListener) {
        super(url,listener, errorListener);
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
