package com.kys26.webthings.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.kys26.webthings.method.MethodTools;

import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

/**
 * @author 李赛鹏
 * @function cookie的工具类
 * Created by kys_9 on 2017/3/28.
 */

public class CookieUtil {
    public static void storecoo(URI uri, String strcoo) {
        // 将规则改掉，接受所有的 Cookie
        MethodTools.CookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        // 保存这个定制的 CookieManager
        CookieHandler.setDefault(MethodTools.CookieManager);
        // 接受 HTTP 请求的时候，得到和保存新的 Cookie
        HttpCookie cookie = new HttpCookie("Cookie: ", strcoo);
        //cookie.setMaxAge(60000);//没这个也行。
        MethodTools.CookieManager.getCookieStore().add(uri, cookie);
    }

    public static String getcookies() {
        HttpCookie res = null;
        // 使用 Cookie 的时候：
        // 取出 CookieStore
        CookieStore store = MethodTools.CookieManager.getCookieStore();
        // 得到所有的 URI
        List<URI> uris = store.getURIs();
        for (URI ur : uris) {
            // 筛选需要的 URI
            // 得到属于这个 URI 的所有 Cookie
            List<HttpCookie> cookies = store.get(ur);
            for (HttpCookie coo : cookies) {
                res = coo;
            }
        }
        return res.toString();
    }

    /**
     * 保存Cookie
     *
     * @param cookie
     */
    public static void putCookie(String cookie) {
        try {
            SharedPreferences.Editor editor = MethodTools.cookiePerferences.edit();
            editor.putString("cookie", cookie);
            editor.apply();
        } catch (Exception e) {
            Log.e("CookieUtil", "e:" + e);
        }
    }

    /**
     * 得到cookie
     *
     * @param context
     * @return
     */
    public static String getCookie(Context context) {
        MethodTools.cookiePerferences = context.getSharedPreferences("cookie", Context.MODE_PRIVATE);
        return MethodTools.cookiePerferences.getString("cookie", "null");
    }
}
