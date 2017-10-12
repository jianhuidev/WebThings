package com.kys26.webthings.video;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kys26.webthings.method.MethodTools;
import com.smaxe.uv.Responder;
import com.smaxe.uv.client.INetConnection;
import com.smaxe.uv.client.NetConnection;
import com.zhangyx.MyGestureLock.R;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.kys26.webthings.method.MethodTools.mVideoChannelData;

public class VideoActivity extends Activity implements SurfaceHolder.Callback, View.OnTouchListener {
    @InjectView(R.id.surface_view)
    SurfaceView surfaceView;
    @InjectView(R.id.right_image)
    ImageView rightImage;
    @InjectView(R.id.v_left)
    ImageView vLeft;
    @InjectView(R.id.v_up)
    ImageView vUp;
    @InjectView(R.id.v_right)
    ImageView vRight;
    @InjectView(R.id.v_down)
    ImageView vDown;

    @InjectView(R.id.nav)
    RelativeLayout nav;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    //    @InjectView(R.id.videoPlayProgress)
//    ProgressBar videoPlayProgress;
//    @InjectView(R.id.video_play_btn)
//    Button videoPlayBtn;
    String videoUrl = "rtmp://192.168.0.105:1935/chapter9/";
    String userName = "sunhan";//用户
    @InjectView(R.id.v_stop)
    ImageView vStop;
    @InjectView(R.id.linear_scan_btn)
    Button linearScanBtn;
    @InjectView(R.id.far_btn)
    Button farBtn;
    @InjectView(R.id.near_btn)
    Button nearBtn;
    private int videoNodeId;//通道
    final UltraNetConnection connection = new UltraNetConnection();
    // fields
    private UltraNetStream stream;
    //SurfaceView
    SurfaceHolder surfaceHolder;
    private int clickPosition;//上一个界面(Command)点击的position;
    private int farmPosition;//农场哪里的position;
    private Handler msgHandler;
    //通道id
    private String ChannelId;
    //控制回应
    private Responder controlResponder;
    //    private MediaCodec mCodec;//暂时不考虑硬解码
    private boolean isPlay = false;
    //正常播放和播放失败等等
    private final int PLAYINDEX = -1, PLAYERROR = 0, PLAYNORMAL = 1, CONNECTFAILED = 2;
    private String addressCode = "01";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.video_slide);
        ButterKnife.inject(this);
        initView();
    }

    /**
     * 初始化view
     */
    private void initView() {
        videoNodeId = MethodTools.farmDataList.get(farmPosition).getVideoNodeList().get(clickPosition).getVideoNodeId();
        connection.addEventListener(new NetConnectionListener());
        connection.client(VideoActivity.this);
        Intent intent = getIntent();
        clickPosition = intent.getIntExtra("clickPosition", 0);
        farmPosition = intent.getIntExtra("farmPosition", farmPosition);
        surfaceView.setDrawingCacheEnabled(true);
        surfaceHolder = surfaceView.getHolder();
        surfaceView.setKeepScreenOn(true);
        surfaceHolder.addCallback(VideoActivity.this);
//        drawerLayout.setScrimColor(Color.TRANSPARENT);
        //设置滑动模式为全屏
//        setDrawerLeftEdgeSize(this, drawerLayout, 1);
        initListener();
        ChannelId = mVideoChannelData.get(clickPosition).getChannel();
        char[] channelList = ChannelId.toCharArray();
        addressCode = "0" + channelList[13];
        vLeft.setOnTouchListener(this);
        vRight.setOnTouchListener(this);
        vUp.setOnTouchListener(this);
        vDown.setOnTouchListener(this);
        Rtmp_Connect();
        initResponder();
    }

    /**
     * 初始化回应
     */
    private void initResponder() {
        msgHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case PLAYINDEX:
//                        if (isPlay) {
//                            VideoPlayer.stop();
//                            isPlay = false;
//                            videoPlayBtn.setBackgroundResource(R.drawable.play_fill);
//                        } else {
                        final String channel = mVideoChannelData.get(clickPosition).getChannel();
//                        videoPlayBtn.setVisibility(View.GONE);
//                        videoPlayProgress.setVisibility(View.VISIBLE);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int status = VideoPlayer.play(surfaceHolder.getSurface(), videoUrl + channel);
                                if (status == -1) {
                                    Message msg = new Message();
                                    msg.what = 0;
                                    msgHandler.sendMessage(msg);
                                } else {
                                    Message msg = new Message();
                                    msg.what = 1;
                                    msgHandler.sendMessage(msg);
                                }
                            }
                        }).start();
//                        }
                        break;
                    case PLAYERROR:
//                        videoPlayBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "打开视频监控失败,请检查摄像头是否可用", Toast.LENGTH_SHORT).show();
                        break;
                    case PLAYNORMAL:
//                        isPlay = true;
//                        videoPlayBtn.setBackgroundResource(R.drawable.stop);
//                        videoPlayBtn.setVisibility(View.VISIBLE);
//                        videoPlayProgress.setVisibility(View.GONE);
                        break;
                    case CONNECTFAILED:
                        Toast.makeText(VideoActivity.this, "连接视频服务器失败", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        controlResponder = new Responder() {
            @Override
            public void onResult(Object o) {
                Log.e("controller", "o:" + o.toString());
            }

            @Override
            public void onStatus(Map<String, Object> map) {

            }
        };
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) nav.getLayoutParams();
        DisplayMetrics dm = getResources().getDisplayMetrics();//获取屏幕宽度
        params.width = (dm.widthPixels) * 7 / 18;
        nav.setLayoutParams(params);
//        seekbar.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                drawerLayout.requestDisallowInterceptTouchEvent(true);//意思就是告诉mDrawerLayout ，不要处理Touch事件。
//                return false;
//            }
//        });
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View view, float v) {
                drawerLayout.bringChildToFront(view);
                drawerLayout.requestLayout();
            }

            @Override
            public void onDrawerOpened(View view) {
//                int windowsHeight = DensityUtil.initScreenHeight(VideoActivity.this);
//                ViewGroup.LayoutParams leftParams = view.getLayoutParams();
//                leftParams.height = windowsHeight;
//                view.setLayoutParams(leftParams);
            }

            @Override
            public void onDrawerClosed(View view) {

            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
    }

    /**
     * rtmp连接类
     */
    public void Rtmp_Connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                connection.connect(videoUrl, "board", userName);
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (connection.connected())
            VideoPlay();
    }
    //    // 3、
//    public void streamPlayCallback(String publishName,
//                                   String playName, String codec) {
//        try {
//            if (stream == null) {
//                stream = new UltraNetStream(connection, this);
//            }
//            stream.play(new FlvVideo(playName), playName);
////            stream.play(new MyVideo(stream, surfaceHolder, DensityUtil.initScreenWidth(this), DensityUtil.initScreenHeight(this)), playName);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        videoOrAudioInfo.stopCodec();
//        if (stream != null)
//            stream.close();
        connection.close();
        VideoPlayer.stop();
        holder.removeCallback(this);
        holder.getSurface().release();
    }

    @OnClick({R.id.right_image, R.id.v_stop, R.id.linear_scan_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.right_image:
//                if (drawerLayout.isShown())
//                    drawerLayout.closeDrawer(Gravity.END);
//                else
//                    drawerLayout.openDrawer(Gravity.END);
                drawerLayout.openDrawer(Gravity.END);
                break;
            case R.id.v_stop:
                CallCommand(new String[]{"FF", addressCode, "00", "00", "00", "00"});
                break;
            case R.id.linear_scan_btn:
                CallCommand(new String[]{"FF", addressCode, "00", "07", "00", "59"});
                break;
//            case R.id.v_left:
//                CallCommand(new String[]{"FF", addressCode, "00", "04", "07", "00"});
//                break;
//            case R.id.v_up:
//                CallCommand(new String[]{"FF", addressCode, "00", "08", "00", "10"});
//                break;
//            case R.id.v_right:
//                CallCommand(new String[]{"FF", addressCode, "00", "02", "10", "00"});
//                break;
//            case R.id.v_down:
//                CallCommand(new String[]{"FF", addressCode, "00", "10", "00", "10"});
//                break;
        }
    }

    /**
     * 视频播放
     */
    private synchronized void VideoPlay() {
        Object[] connectOb = new Object[4];
        connectOb[0] = mVideoChannelData.get(clickPosition).getChannel();
        connectOb[1] = videoNodeId;
        connectOb[2] = validataCode(new String[]{"00", addressCode, "00", "00", "00", "01"});
        connectOb[3] = userName;
        connection.call("call.call_board", new Responder() {
            @Override
            public void onResult(Object o) {
                Message msg = new Message();
                msg.what = PLAYINDEX;
                msgHandler.sendMessage(msg);
            }

            @Override
            public void onStatus(Map<String, Object> map) {
            }
        }, connectOb);
    }

    /**
     * 调用red5的命令方法
     */
    private void CallCommand(String[] commandData) {
        Object[] dataOb = new Object[4];
        dataOb[0] = ChannelId;
        dataOb[1] = videoNodeId;
        dataOb[2] = validataCode(commandData);
        dataOb[3] = userName;
        Log.e("显示", "command:" + dataOb[2]);
        connection.call("call.call_board", controlResponder, dataOb);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()) {
                    case R.id.v_left:
                        CallCommand(new String[]{"FF", addressCode, "00", "04", "07", "00"});
                        break;
                    case R.id.v_up:
                        CallCommand(new String[]{"FF", addressCode, "00", "08", "00", "10"});
                        break;
                    case R.id.v_right:
                        CallCommand(new String[]{"FF", addressCode, "00", "02", "10", "00"});
                        break;
                    case R.id.v_down:
                        CallCommand(new String[]{"FF", addressCode, "00", "10", "00", "10"});
                        break;
                    case R.id.far_btn:
                        break;
                    case R.id.near_btn:
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                CallCommand(new String[]{"FF", addressCode, "00", "00", "00", "00"});
                break;
        }
        return false;
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
            Log.e("Connection#AsyncError: ", "message:" + message + " e"
                    + e);
        }

        @Override
        public void onIOError(final INetConnection source, final String message) {
            Log.e("Connection#onIOError: ", "message:" + message);
        }

        @Override
        public void onNetStatus(final INetConnection source,
                                final Map<String, Object> info) {
//            System.out.println("NetConnection#onNetStatus: " + info);
            Log.i("Connection#onNetStatus:", "info" + info);
            final Object code = info.get("code");
            if (NetConnection.CONNECT_SUCCESS.equals(code)) {
                VideoPlay();
            } else {
                Message msg = new Message();
                msg.what = CONNECTFAILED;
                msgHandler.sendMessage(msg);
                connection.close();
            }
        }
    }

    /**
     * 计算校验码
     *
     * @param dataList
     * @return 返回正确的7位数据
     */

    private String validataCode(String[] dataList) {
        int result = 0;
        StringBuffer dataBuffer = new StringBuffer();
        for (int i = 0; i < dataList.length; i++) {
            if (i > 0)
                result += Integer.valueOf(dataList[i], 16);
            dataBuffer.append(dataList[i]);
        }
        if (result % 256 < 16)
            dataBuffer.append("0");
        return dataBuffer.append(Integer.toHexString(result % 256)).toString();
    }

    /**
     * 不要去，用来接受连接检查时回应的方法
     *
     * @param obj
     */
    public void onBWCheck(Object obj) {
        Log.e("onBWCheck", "obj" + obj);
    }

    /**
     * 不要去，用来接受已连接回应的方法
     *
     * @param obj
     */
    public void onBWDone(Object obj) {
        Log.e("onBWDone", "obj" + obj);
    }

    /**
     * 接受调用方法的参数
     *
     * @param obj
     */
    public void onRep(Object obj) {
        Log.e("onRep", "obj" + obj.toString());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

}