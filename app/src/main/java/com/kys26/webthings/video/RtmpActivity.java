package com.kys26.webthings.video;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kys26.webthings.method.MethodTools;
import com.smaxe.uv.client.INetConnection;
import com.smaxe.uv.client.NetConnection;
import com.smaxe.uv.client.video.FlvVideo;
import com.zhangyx.MyGestureLock.R;

import java.util.Map;

import static com.kys26.webthings.method.MethodTools.mVideoChannelData;


/**
 * @author 李赛鹏
 * @class Created by kys_9 on 2017/6/3.
 */

public class RtmpActivity extends Activity implements SurfaceHolder.Callback {
    private Button rtmp_con_btn;
    String Videourl = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
    String externUID = "userId";
    String username = "userName";

    private String sv;

    private Object callId = "75050";//回掉时服务器提供的
    final UltraNetConnection connection = new UltraNetConnection();
    // fields
    private volatile boolean disconnected = false;
    private UltraNetStream stream;
    //SurfaceView
    SurfaceHolder surfaceHolder;
    SurfaceView surfaceView;
    private int clickPosition;//上一个界面点击的position;
    private int farmPosition;//农场哪里的position;
    private Handler msgHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 横屏
        setContentView(R.layout.activity_rtmp);
        connection.addEventListener(new NetConnectionListener());
        connection.client(RtmpActivity.this);
        initView();
    }

    /**
     * 初始化view
     */
    private void initView() {
        Intent intent = getIntent();
        clickPosition = intent.getIntExtra("clickPosition", 0);
        farmPosition = intent.getIntExtra("farmPosition", farmPosition);
//        rtmp_con_btn = (Button) findViewById(R.id.rtmp_con_btn);
        rtmp_con_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rtmp_Connect();
            }
        });
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        surfaceView.setDrawingCacheEnabled(true);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        msgHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Toast.makeText(getApplicationContext(), "打开视频监控失败,请检查摄像头是否可用", Toast.LENGTH_SHORT).show();
            }
        };
    }

    /**
     * rtmp连接类
     */
    public void Rtmp_Connect() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
//                connection.connect(url, null);
                while (!connection.connected() && !disconnected) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
//                if (!disconnected) {
//                    stream = new UltraNetStream(connection);
//                    stream.addEventListener(new NetStream.ListenerAdapter() {
//                        @Override
//                        public void onNetStatus(final INetStream source,
//                                                final Map<String, Object> info) {
//                            System.out
//                                    .println("NetStream#onNetStatus: " + info);
//                        }
//                    });
//                }
                while (!disconnected) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {/* ignore */
                        Log.e("ThreadException", "连接失败\n\n" + e.toString());
                    }
                }
                connection.close();
                return null;
            }
        }.execute((Void) null);
    }

    // 3、
    public void successfullyJoinedVoiceConferenceCallback(String publishName,
                                                          String playName, String codec) {
        System.out.println("publishName:" + publishName + "\n\nplayName:"
                + playName + "\n\ncodec:" + codec);
        try {
            // stream.receiveAudio(true);
            // stream.receiveVideo(false);
            if (stream == null) {
//                stream = new UltraNetStream(connection);
            }
            //调用该方法可以传递直播音频流，playname是连接成功后,red5返回给的speex音频名称
            stream.play(new FlvVideo(playName + ".flv"), playName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String videoUrl = "rtmp://" + MethodTools.farmDataList.get(farmPosition).getVideoNodeList().get(0).getIp()
                        + ":1935/chapter9/" + mVideoChannelData.get(clickPosition).getChannel();
//                sv = "http://192.168.87.59:8080/";
                int status = VideoPlayer.play(surfaceHolder.getSurface(), videoUrl);
                if (status == -1) {
                    Message msg = new Message();
                    msg.what = 0;
                    msgHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        VideoPlayer.stop();
        connection.close();
        holder.getSurface().release();
        holder.removeCallback(this);
        surfaceView.destroyDrawingCache();
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                VideoPlayer.close();
//            }
//        }.start();
//        VideoPlayer.close();
    }

    /**
     * 2 、 <code>NetConnectionListener</code> - {@link UltraNetConnection}
     * listener implementation.
     */
    public class NetConnectionListener extends NetConnection.ListenerAdapter {
        /**
         * Constructor.
         */
        public NetConnectionListener() {
        }

        @Override
        public void onAsyncError(final INetConnection source,
                                 final String message, final Exception e) {
            System.out.println("NetConnection#onAsyncError: " + message + " "
                    + e);
        }

        @Override
        public void onIOError(final INetConnection source, final String message) {
            System.out.println("NetConnection#onIOError: " + message);
        }

        @Override
        public void onNetStatus(final INetConnection source,
                                final Map<String, Object> info) {
            // 2
            System.out.println("NetConnection#onNetStatus: " + info);
            final Object code = info.get("code");
            if (NetConnection.CONNECT_SUCCESS.equals(code)) {
//                connection.call("voiceconf.call", null, "default", username,
//                        callId);
            } else {
                disconnected = true;
            }
        }
    }
}
