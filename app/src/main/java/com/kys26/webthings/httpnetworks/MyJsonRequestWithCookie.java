package com.kys26.webthings.httpnetworks;

import android.os.Message;
import android.util.Log;

import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.util.CookieUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 徐建强 on 2015/8/4.
 */
public class MyJsonRequestWithCookie {
    private static final String TAG = MyJsonRequestWithCookie.class.getSimpleName();

    /**
     * @param url
     * @param cookie
     * @param json
     * @return null
     * @author kys_26使用者：徐建强  on 2015-11-2
     * @function:自己写的httpPost类请求 带有cookie型访问
     */
    public static void httpPost(final String url, final JSONObject json, final String cookie) {
        /**传入请求地址*/
        final String postUrl = url;
        /**发送的请求数据*/
        final JSONObject jsonObject = json;
        /**请求结果*/
        final StringBuffer responseResult = new StringBuffer();
        //启动线程开始请求
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /**传入请求地址*/
                    URL posturl = new URL(url);
                    Log.e(TAG, "请求接口为：" + url);
                    /**启动连接*/
                    HttpURLConnection connection = (HttpURLConnection) posturl.openConnection();
                    connection.setRequestMethod("POST");
                    /**连接设置通用的请求属性*/
                    /**设定传送的内容类型是可序列化的java对象*/
                    /** (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)*/
                    connection.setRequestProperty("Content-type", "application/json;charset=UTF-8");
                    connection.setRequestProperty("accept", "*/*");
                    connection.setRequestProperty("connection", "Keep-Alive");
                    connection.setRequestProperty("Cookie", cookie);
//                    connection.setRequestProperty("username","test");
//                    connection.setRequestProperty("password","test");
                    /**发送POST请求必须设置如下两行*/
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    //设置连接时间为10秒
                    connection.setConnectTimeout(10000);
                    connection.connect();
                    /**获取URLConnection对象对应的输出流*/
                    PrintWriter mprintwriter = new PrintWriter(connection.getOutputStream());
                    mprintwriter.write(jsonObject.toString());
                    Log.e(TAG, "请求的信息为：" + jsonObject.toString());
                    mprintwriter.flush();
                    if (null == cookie || cookie.equals("null") || cookie.equals("")) {
                        if (connection.getHeaderField("Set-Cookie") != null)
                            CookieUtil.putCookie(connection.getHeaderField("Set-Cookie"));
                    }
                    int responseCode = connection.getResponseCode();
                    if (responseCode != 200) {
                        Log.e(TAG, "连接错误:错误码为:" + responseCode);
                        Message msg = Message.obtain();
                        msg.what = Code.FAILURE;
                        msg.obj = responseCode;
                        MethodTools.handlerJson.sendMessage(msg);
                    } else {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            responseResult.append("\n").append(line);
                        }
                        Message msg = Message.obtain();
                        msg.what = Code.SUCCESS;
                        msg.obj = responseResult.toString();
                        Log.e(TAG, "http返回数据为:" + responseResult.toString());
                        MethodTools.handlerJson.sendMessage(msg);
                        Log.e(TAG, "连接成功");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

//    /**
//     * @param httpClient
//     * @author:Created by kys_26 徐建强 on 2015/6/7.
//     * @function:获取标准 Cookie ，并存储
//     */
//    private String getCookie(DefaultHttpClient httpClient) {
//
//        List<Cookie> cookies = httpClient.getCookieStore().getCookies();
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < cookies.size(); i++) {
//            Cookie cookie = cookies.get(i);
//            String cookieName = cookie.getName();
//            String cookieValue = cookie.getValue();
//            if (!TextUtils.isEmpty(cookieName)
//                    && !TextUtils.isEmpty(cookieValue)) {
//                sb.append(cookieName + "=");
//                sb.append(cookieValue + ";");
//            }
//        }
//        Log.e("cookie登陆", sb.toString());
//        return sb.toString();
//    }

    /**
     * @param url
     * @param json
     * @param cookie
     * @param what
     * @author窦文
     */
    public static void newhttpPost(final String url, final JSONObject json, final String cookie, final int what) {
        /**传入请求地址*/
        final String postUrl = url;
        /**发送的请求数据*/
        final JSONObject jsonObject = json;
        /**请求结果*/
        final StringBuffer responseResult = new StringBuffer();
        //启动线程开始请求
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
//                HttpURLConnection connection=(HttpURLConnection)
//                DefaultHttpClient httpclient = new DefaultHttpClient();
//                HttpPost post = new HttpPost(postUrl);
                try {
                    /**传入请求地址*/
                    URL posturl = new URL(url);
                    Log.e(TAG, "请求接口为：" + url);

                    /**启动连接*/
                    HttpURLConnection connection = (HttpURLConnection) posturl.openConnection();
                    connection.setRequestMethod("POST");
                    /**连接设置通用的请求属性*/
                    /**设定传送的内容类型是可序列化的java对象*/
                    /** (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)*/
                    connection.setRequestProperty("Content-type", "application/json;charset=UTF-8");
                    connection.setRequestProperty("accept", "*/*");
                    connection.setRequestProperty("connection", "Keep-Alive");
                    connection.setRequestProperty("Cookie", cookie);
                    /**发送POST请求必须设置如下两行*/
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    //设置连接时间为10秒
                    connection.setConnectTimeout(10000);
                    connection.connect();
                    /**获取URLConnection对象对应的输出流*/
                    PrintWriter mprintwriter = new PrintWriter(connection.getOutputStream());
                    if (jsonObject != null) {
                        mprintwriter.write(jsonObject.toString());
                        Log.e(TAG, "请求的信息为：" + jsonObject.toString());
                        mprintwriter.flush();
                    }
                    if (null == cookie || cookie.equals("null") || cookie.equals("")) {
                        CookieUtil.putCookie(connection.getHeaderField("Set-Cookie"));
                    }
                    int responseCode = connection.getResponseCode();
                    if (responseCode != 200) {
                        Log.e(TAG, "连接错误:错误码为:" + responseCode);
                        Message msg = Message.obtain();
                        msg.what = Code.FAILURE;
                        msg.obj = responseCode;
                        MethodTools.handlerJson.sendMessage(msg);
                    } else {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            responseResult.append("\n").append(line);
                        }
                        Message msg = Message.obtain();
                        msg.what = what;
                        msg.obj = responseResult.toString();
                        Log.e(TAG, "http返回数据为:" + responseResult.toString());
                        if (MethodTools.handlerJson != null) {
                            MethodTools.handlerJson.sendMessage(msg);
                        }
                        Log.e(TAG, "连接成功");
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = Code.FAILURE;
                    MethodTools.handlerJson.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = Code.FAILURE;
                    MethodTools.handlerJson.sendMessage(msg);
                } finally {
                }
            }
        });
        thread.start();
    }

    /***
     * @param url
     * @param object
     */
    public static void httpPostJson(final String url, final JSONObject object) {
//        /**传入请求地址*/
//        final String postUrl = url;
//        /**发送的请求数据*/
//        final JSONObject jsonObject = json;
        /**请求结果*/
        final StringBuffer responseResult = new StringBuffer();
        //启动线程开始请求
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /**传入请求地址*/
                    URL posturl = new URL(url);
                    /**启动连接*/
                    HttpURLConnection connection = (HttpURLConnection) posturl.openConnection();
                    connection.setRequestMethod("POST");
                    /**连接设置通用的请求属性*/
                    /**设定传送的内容类型是可序列化的java对象*/
                    /** (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)*/
                    connection.setRequestProperty("Content-type", "text/html;charset=UTF-8");
                    connection.setRequestProperty("accept", "*/*");
                    connection.setRequestProperty("connection", "Keep-Alive");
                    /**发送POST请求必须设置如下两行*/
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    //设置连接时间为10秒
                    connection.setConnectTimeout(10000);
                    connection.connect();
                    /**获取URLConnection对象对应的输出流*/
                    if (object != null) {
                        PrintWriter mprintwriter = new PrintWriter(connection.getOutputStream());
                        mprintwriter.write(object.toString());
                        mprintwriter.flush();
                    }
                    int responseCode = connection.getResponseCode();
                    if (responseCode != 200) {
                        Log.e(TAG, "连接错误:错误码为:" + responseCode);
                        Message msg = Message.obtain();
                        msg.what = Code.FAILURE;
                        msg.obj = responseCode;
                        MethodTools.handlerJson.sendMessage(msg);
                    } else {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            responseResult.append("\n").append(line);
                        }
                        Message msg = Message.obtain();
                        msg.what = Code.SUCCESS;
                        msg.obj = responseResult.toString();
                        Log.e(TAG, "http返回数据为:" + responseResult.toString());
                        MethodTools.handlerJson.sendMessage(msg);
                        Log.e(TAG, "连接成功");
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
