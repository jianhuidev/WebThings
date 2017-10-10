package com.kys26.webthings.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.httpnetworks.VolleyStringRequest;
import com.kys26.webthings.method.MethodTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 窦文 on 2017/1/17.
 */
public class WarningService extends Service {
    private Timer timer;
    private TimerTask timertask;
    private final int GOON = 11;
    private final int GETOK = 21;
    private String TAG;
    private List<HashMap<String, Object>> list;
    private String gwName;
    private String nodeName;
    private String warning;
    private String say = "请尽快处理";
    private int type;
    /**
     * 铃声
     */
    private MediaPlayer mMediaPlayer;
    /**
     * 震动
     */
    private Vibrator vibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        TAG = getClass().getName();
        initHandler();
        timer = new Timer();
        timertask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = GOON;
                MethodTools.serviceHandler.sendMessage(msg);
            }
        };
        timer.schedule(timertask, 1 * 1000, 1 * 1000 * 60);
    }

    /**
     * 初始化Handler
     */
    private void initHandler() {
        MethodTools.serviceHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Code.SUCCESS:
                        analysis(msg.obj.toString());
                        break;
                    case Code.FAILURE:
//                        ToastHelper.show(getApplication(), "获取请求失败，请重新尝试");
                        Log.e(TAG,"error:"+"获取失败");
                        break;
                    case GOON:
                        VolleyStringRequest.serviceStringRequestWithCookie(WarningService.this, Path.host + Path.URL_WARNING, null);
                        break;
                }
            }
        };
    }

    /**
     * @param s
     * @function 解析数据并处理
     */
    private void analysis(String s) {
        Log.e(TAG, "" + s);
        try {
            list = new ArrayList<HashMap<String, Object>>();
            JSONArray jsonArray = new JSONArray(s);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jb = (JSONObject) jsonArray.get(i);
                Log.d(TAG,"  "+jb.get("warningSign").toString());
                if (Integer.valueOf(jb.get("warningSign").toString()) != 0) {
                    Log.i(TAG,"************************************************");
                    switch (Integer.valueOf(jb.get("warningSign").toString())) {
                        case 1:
                            warning = "偏低";
                            break;
                        case 2:
                            warning = "严重偏低";
                            break;
                        case 3:
                            warning = "偏高";
                            break;
                        case 4:
                            warning = "严重偏高";
                            break;
                    }

                    gwName = jb.get("gwName").toString();
                    nodeName = jb.get("deviceTypeName").toString();
                    type = Integer.valueOf(jb.get("phoneAction").toString());

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(gwName+"\n"+nodeName+"\n"+warning+"\n"+say);
                    switch (type){
                        case 1:
                            PlaySound1();
                            break;
                        case 2:
                            SetVibrator();
                            break;
                        case 3:
                            PlaySound1();
                            SetVibrator();
                            break;
                    }
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // to do
                                mMediaPlayer.stop();
                            if (null!=vibrator){
                                vibrator.cancel();
                            }
                            dialog.dismiss();
                        }
                    });
                    final AlertDialog dialog = builder.create();
                    //在dialog show前添加此代码，表示该dialog属于系统dialog。
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
                    dialog.show();
                    Log.e(TAG,"aaaaa");
                    new Thread() {
                        public void run() {
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.show();
                                    Log.e(TAG,"aaaaa");
                                }
                            });
                        }
                    }.start();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void PlaySound1() {
        // 使用来电铃声的铃声路径
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        // 如果为空，才构造，不为空，说明之前有构造过
        if (null == mMediaPlayer) {
            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setDataSource(this, uri);
                mMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mMediaPlayer.start();
        /**停止*/
        // mMediaPlayer.stop();
    }

    /*震动*/
    public void SetVibrator() {
        vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
        // 等待3秒，震动3秒，从第0个索引开始，一直循环
        vibrator.vibrate(new long[]{3000, 3000}, 0);
        //取消
        // vibrator.cancel();
    }

}
