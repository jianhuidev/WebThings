package com.kys26.webthings.httpnetworks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.util.BitmapToRoundUtil;
import com.kys26.webthings.util.VolleyUtil;
import com.zhangyx.MyGestureLock.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 徐建强 on 2015/8/2.
 */
public class MyImageRequest {
    private static final String TAG = MyImageRequest.class.getSimpleName();

    /**
     * volley验证码图片请求
     *
     * @param act
     * @param ima
     * @author Created by 徐建强 on 2015/8/2.
     */
    public static void verifyImage(Activity act, ImageView ima) {
        final ImageView image = ima;
        //请求之前，先取消之前的请求（取消还没有进行完的请求）
        VolleyUtil.getQueue(act).cancelAll(act);

        com.android.volley.toolbox.ImageRequest imgRequest =
                new com.android.volley.toolbox.ImageRequest(Path.host + Path.verify,
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap arg0) {
                                // TODO Auto-generated method stub
                                image.setImageBitmap(arg0);
                            }
                        }, 300, 200, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        image.setImageResource(R.drawable.ic_empty);
                    }
                });
        // 请求加上Tag,用于取消请求
        imgRequest.setTag(act);
        VolleyUtil.getQueue(act).add(imgRequest);
    }

    //声明一个Bitmap,用作将图片流装换为Bitmap图
    private Bitmap bitmap = null;
    //声明一个Imageview的全局变量，用作获得传过来的Imageview的控件，好异步加载
    private ImageView imageVerify;
    //声明BaseActivity，此时在这个类中只调用一个方法，既是保存cookies
//    private BaseActivity baseActivity=new BaseActivity();
    //声明一个boolean判断请求结果
    boolean resultIma = false;
    //声明一个进度条，用来显示图片加载进度
    private ProgressBar verifyProBar;
    private final int HEAD_PORTRAIT = 10;

    /**
     * 通过Get获取验证码,自定义底部Http请求
     *
     * @param ima
     * @param proBar
     * @param url
     * @return intcode
     * @date 2014.08.02
     */
    public int getImage(ImageView ima, ProgressBar proBar, final String url) {
        imageVerify = ima;
        verifyProBar = proBar;
        //显示进度条
        verifyProBar.setVisibility(View.VISIBLE);
        // 启动一个后台线程
//        Thread thread=new Thread(new Runnable() {
//            @Override
//            public void run() {
//                DefaultHttpClient httpclient = new DefaultHttpClient();
//                HttpPost httpPost = new HttpPost(url);
//                try {
//                    HttpResponse resp = httpclient.execute(httpPost);
//                    //判断是否正确执行
//                    if (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {
//                        //将返回内容转换为bitmap
//                        HttpEntity entity = resp.getEntity();
//                        InputStream in = entity.getContent();
//                        bitmap = BitmapFactory.decodeStream(in);
//                        //向handler发送消息，执行显示图片操作
//                        Message msg = new Message();
//                        msg.what = Code.SUCCESS;
//                        handlerVerify.sendMessage(msg);
//                        //保存验证码cookie值
//                        Message msgCookie=new Message();
//                        msgCookie.what=Code.SUCCESS;
//                        msgCookie.obj=getCookie(httpclient).toString();
//                        MethodTools.handlerCookieVerify.sendMessage(msgCookie);
//                        Log.e("验证码cookie值", getCookie(httpclient).toString());
//                    }
//                    resultIma=true;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.e("验证码加载异常", e.toString());
//                    resultIma=false;
//                } finally {
//                    httpclient.getConnectionManager().shutdown();
//                }
//            }
//        });
//        thread.start();
//        imageVerify = ima;
//        verifyProBar = proBar;
        //显示进度条
//        verifyProBar.setVisibility(View.VISIBLE);
        // 启动一个后台线程
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL imageUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                    connection.setRequestMethod("POST");
                    /**字面意思，发送数据*/
                    connection.setDoOutput(true);
                    /**接收数据*/
                    connection.setDoInput(true);
                    /**设置传送的内容是可序列话的java对象*/
                    connection.setRequestProperty("Content-type", "application/x-java-serialized-object");
                    connection.setRequestProperty("accept", "*/*");
                    connection.setRequestProperty("connection", "Keep-Alive");
                    connection.connect();
                    String cookie = connection.getHeaderField("Set-Cookie");
                    bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                    /**发送消息，说明图片接收到了*/
                    Message msg = new Message();
                    msg.what = Code.SUCCESS;
                    handlerVerify.sendMessage(msg);
                    /**保存验证码cookie值*/
                    Message msgCookie = new Message();
                    connection.getHeaderFields();
//                    String headerName = null;
//                    for (int i = 1; (headerName = connection.getHeaderFieldKey(i)) != null; i++) {
//                        Log.e(TAG, "Header Nme:" + headerName);
//                        Log.e(TAG, "Header Nme:" + connection.getHeaderField(i));
//                    }
                    msgCookie.what = Code.SUCCESS;
                    msgCookie.obj = cookie;
                    MethodTools.handlerCookieVerify.sendMessage(msgCookie);
                    resultIma = true;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        if (resultIma = false) {
            return Code.FAILURE;
        } else {
            return Code.SUCCESS;
        }
    }

    public void getHeadImage(ImageView image, final String url) {
        imageVerify = image;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL imageurl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) imageurl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8 * 1000);
                    connection.setReadTimeout(8 * 1000);
                    //                connection.setDoInput(true);
                    //                connection.setDoOutput(true);
                    Bitmap bitmap = BitmapFactory.decodeStream(connection.getInputStream());//BitmapFactory的decodeStream方法解码获取图片。
                    Message msg = new Message();
                    msg.obj = bitmap;
                    msg.what = HEAD_PORTRAIT;
                    handlerVerify.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    /**
     * 这里重写handleMessage方法，接受到子线程数据后更新UI
     **/
    public Handler handlerVerify = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Code.SUCCESS:
                    //获取验证码成功后进行加载
                    imageVerify.setImageBitmap(bitmap);
                    //隐藏进度条
                    verifyProBar.setVisibility(View.INVISIBLE);
                    break;
                case HEAD_PORTRAIT:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    BitmapToRoundUtil toRound = new BitmapToRoundUtil();
                    bitmap = toRound.toRoundBitmap(bitmap);
                    imageVerify.setImageBitmap(bitmap);
                    break;
            }
        }
    };
//    /**
//     * @author:Created by kys_26 徐建强 on 2015/6/7.
//     * @function:获取标准 Cookie ，并存储
//     * @param httpClient
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
//        Log.e("cookie验证码", sb.toString());
//        return sb.toString();
//    }
}
