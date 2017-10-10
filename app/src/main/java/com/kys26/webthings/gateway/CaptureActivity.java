package com.kys26.webthings.gateway;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.kys26.webthings.camera.CameraManager;
import com.kys26.webthings.decoding.CaptureActivityHandler;
import com.kys26.webthings.decoding.InactivityTimer;
import com.kys26.webthings.dialog.IntentDialog;
import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.httpnetworks.VolleyJsonRequest;
import com.kys26.webthings.main.BaseActivity;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.util.MyToast;
import com.kys26.webthings.view.ClearEditText;
import com.kys26.webthings.view.ViewfinderView;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Vector;

import static com.kys26.webthings.method.MethodTools.mGatewayIdBean;

/**
 * Initial the camera
 *
 * @author 徐建强
 */
public class CaptureActivity extends BaseActivity implements Callback {

    /**
     * 扫描到信息后通知UI进行异步加载的Handler
     */
    private CaptureActivityHandler handler;
    /**
     * ViewfinderView工具类声明
     */
    private ViewfinderView viewfinderView;
    /**
     * 扫描结果确认标识
     */
    private boolean hasSurface;
    /**
     * 扫描码处理声明
     */
    private Vector<BarcodeFormat> decodeFormats;
    /**
     * 扫描的结果码
     */
    private String characterSet;
    /**
     * 扫描计时工具
     */
    private InactivityTimer inactivityTimer;
    /**
     * 媒体处理类工具
     */
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    /**
     * 扫描线位置
     */
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    /**
     * 退出按钮
     */
    private Button btn_capture_finish;
    /**
     * 确认提交按钮
     */
    private Button btn_confirmId;
    /**
     * 网关ID填写输入框
     */
    private ClearEditText gatewayId_cet;
    /**
     * 输出标识
     */
    private String TAG = "CaptureActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_capture);
        // 初始化IOC注解
//        ViewUtils.inject(this);
        //初始化UI组件
        initView();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_capture;
    }

    @Override
    protected void onClickLeft() {
        super.onClickLeft();
        finish();
    }

    /**
     * @return null
     * @function:初始化UI组件
     * @author: kys_26使用者：徐建强 2015-11-10
     */
    private void initView() {
        setTitle("扫描网关");
        setEnableRefresh(false, null);
//		btn_capture_finish = (Button) findViewById(R.id.capture_finish);
        btn_confirmId = (Button) findViewById(R.id.tv_confirmId);
        gatewayId_cet = (ClearEditText) findViewById(R.id.cet_gatewayId);
        Toast.makeText(getApplicationContext(), "将二维码或条形码对准在扫描镜框中。", Toast.LENGTH_SHORT).show();

        //初始化二维码扫描镜头
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        //quite this
//        btn_capture_finish.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				CaptureActivity.this.finish();
//			}
//		});
        //手动输入确定
        btn_confirmId.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断ID是否为空
                if (doLoginClickExecute()) {
//					startActivity(new Intent(CaptureActivity.this, SubmitActivity.class));
                    //将网关ID传递给提交界面
                    sendGatewayData();
                }
            }
        });
    }

    /**
     * @return null
     * @function:ID输入框检测是否为空
     * @author: kys_26使用者：徐建强 2015-11-10
     */
    private boolean doLoginClickExecute() {
        //判断输入框的id是否为空
        if (TextUtils.isEmpty(gatewayId_cet.getText().toString())) {
            Toast.makeText(getApplicationContext(), "ID不可为空。", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    /**
     * @return null
     * @function:提交数据给后台
     * @Created by 徐建强 on 2015/7/24.
     */
    private void sendGatewayData() {
        //调用静态SharedPreferences获取cookie值
        MethodTools.sPreFerCookie = getSharedPreferences("Cookies",
                Context.MODE_PRIVATE);
        //将发送的网关信息打包成json数据
        mGatewayIdBean.setNickName(MethodTools.sPreFerCookie.getString("userName", "sunhanfei"));
        mGatewayIdBean.setGwId(gatewayId_cet.getText().toString());
        Log.d("WifiJson", " " + mGatewayIdBean.toString());
        //启动一个弹出进度等待框
        IntentDialog.createDialog(CaptureActivity.this, "正在提交...").show();
        //提交时刻使Activity处于onPause状态
        onPause();
        //开始进行发送数据
        VolleyJsonRequest.JsonRequestWithCookie(CaptureActivity.this,
                Path.host + Path.checkgateway,
                MethodTools.gson.toJson(mGatewayIdBean),
                MethodTools.sPreFerCookie.getString("cookie", "null"));
        //重写Handler中的message方法，获取数据后进行异步加载
        MethodTools.HnadlerVOlleyJson = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == Code.SUCCESS) {
                    //获取数据后进行处理
                    analysis(msg.obj.toString());
                    //弹出框消失
                    IntentDialog.dialog.cancel();
                    //结果处理之后将Activity处于onResume
                    onResume();
                } else if (msg.what == Code.FAILURE) {
                    //弹出进度条消失
                    IntentDialog.dialog.cancel();
                    //结果处理之后将Activity处于onResume
                    onResume();
                    MyToast.makeImgAndTextToast(CaptureActivity.this, getResources().getDrawable(R.drawable.tips_sry),
                            "请求失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
                }
            }
        };
    }

    /**
     * @param result //后台返回的数据
     * @return null
     * @function:提交数据给后台
     * @Created by 徐建强 on 2015/7/24.
     */
    private void analysis(String result) {
        //进行数据解析
        try {
            Log.d("WifiJson", result);
            JSONObject jsonObject = new JSONObject(result);
            Log.e(TAG, "返回数据:" + jsonObject.toString());
            result = jsonObject.getString("Status");
            //对结果进行判断
            if (result.endsWith("Fail")) {
                MyToast.makeImgAndTextToast(CaptureActivity.this, getResources().getDrawable(R.drawable.tips_sry),
                        "请求失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
            } else if (result.endsWith("NotHaveUser")) {
                MyToast.makeImgAndTextToast(CaptureActivity.this, getResources().getDrawable(R.drawable.tips_sry),
                        "用户不存在", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
            } else if (result.endsWith("NotHaveGw")) {
                MyToast.makeImgAndTextToast(CaptureActivity.this, getResources().getDrawable(R.drawable.tips_sry),
                        "此网关ID不存在", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
            } else if (result.endsWith("gwNotBelongUser")) {
                MyToast.makeImgAndTextToast(CaptureActivity.this, getResources().getDrawable(R.drawable.tips_sry),
                        "此网关ID与用户不匹配", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
            } else if (result.endsWith("Success")) {
                //数据保存
                MethodTools.sPreFerCookie = getSharedPreferences("IpAndPort",
                        Context.MODE_PRIVATE);
                MethodTools.editor = MethodTools.sPreFerCookie.edit();
                MethodTools.editor.putString("ip", jsonObject.getString("ip"));
                MethodTools.editor.putString("port", jsonObject.getString("port"));
                MethodTools.editor.commit();
                MyToast.makeImgAndTextToast(CaptureActivity.this, getResources().getDrawable(R.drawable.tips_sry),
                        "请求成功,跳转初始化", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
                //进行跳转初始化
                startActivity(new Intent(CaptureActivity.this, InitializeGateway.class));
                //输出数据
                Log.i(TAG, "IP地址" + jsonObject.getString("ip"));
                this.finish();
            }
        } catch (Exception e) {
            Log.i(TAG, "json数据解析异常");
        }
    }

    /**
     * @return null
     * @function:复写onResume方法 当系统休眠后重新使用二维码扫描时，调用照相机扫描二维码方法
     * @author: kys_26使用者：徐建强 2015-11-10
     */
    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            /**
             * SURFACE_TYPE_PUSH_BUFFERS表明该Surface不包含原生数据，Surface用到的数据由其他对象提供，
             * 在Camera图像预览中就使用该类型的Surface，有Camera负责提供给预览Surface数据，这样图像预览会比较流畅。
             */
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    /**
     * @return null
     * @function:复写onPause方法 当系统休眠时或者屏幕此时没有获取焦点时不再调用照相机，这种情况是在提交数据的时候
     * @author: kys_26使用者：徐建强 2015-11-10
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    /**
     * @return null
     * @function:复写onDestroy方法 当前activity销毁时，同时也销毁计时器
     * @author: kys_26使用者：徐建强 2015-11-10
     */
    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * @param result
     * @param barcode
     * @function:Handler scan result
     * @author: kys_26使用者：徐建强 2015-11-10
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        //FIXME
        if (resultString.equals("")) {
            Toast.makeText(CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {
            //将获取的扫描信息负载在编辑框里
            gatewayId_cet.setText(resultString);
            sendGatewayData();
            //提示用户扫面完成
            MyToast.makeImgAndTextToast(CaptureActivity.this, getResources().getDrawable(R.drawable.tips_smile),
                    "扫描完成,正在确认提交", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
        }
    }

    /**
     * @return null
     * @function:照相机调用
     * @author: kys_26使用者：徐建强 2015-11-10
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

}