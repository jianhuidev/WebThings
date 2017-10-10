package com.kys26.webthings.gateway;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.kys26.webthings.main.BaseActivity;
import com.kys26.webthings.progress.CircleProgressBar;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhangyx.MyGestureLock.R;

/**
 * Created by kys_26 on 2015/10/8.
 */
public class InitializeSubmitProgress extends BaseActivity {

    /**
     * 进度条所需参数与控件
     */
    private CircleProgressBar mBar;
    boolean wheelRunning;
    int wheelProgress = 0;
    //remide text
    @ViewInject(R.id.reminder)
    private TextView textReminder;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置为屏幕处于dialog时候点击空白无效
        InitializeSubmitProgress.this.setFinishOnTouchOutside(false);
        // 初始化IOC注解
        ViewUtils.inject(this);

        //get UI
        initView();
        //get progress
        initProgress();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_initializeprogress;
    }

    @Override
    protected void onClickLeft() {
        super.onClickLeft();
    }

    //get UI
    private void initView() {
        setTitle("初始化网关");
        textReminder.setText(R.string.string_activity_IniaProgress_getAccount);
    }

    //set progress
    private void initProgress() {

        mBar = (CircleProgressBar) findViewById(R.id.initializeprogress_myProgress);
        new Thread(r).start();

       /* MyToast.makeImgAndTextToast(SubmitActivity.this,
                getResources().getDrawable(R.drawable.tips_smile),
                "初始化设置成功", MyToast.SHOW_TIME, Gravity.BOTTOM,120).show();*/
    }

    final Runnable r = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            wheelRunning = true;
            while (wheelProgress < 100) {
                wheelProgress++;
                mBar.setProgressNotInUiThread(wheelProgress);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            wheelRunning = false;
            // InitializeSubmitProgress.this.finish();
        }
    };

    //exit remide
    public boolean onKeyDown(int KeyCode, KeyEvent event) {
        switch (KeyCode) {
            case KeyEvent.KEYCODE_BACK:
                Toast.makeText(InitializeSubmitProgress.this, R.string.string_activity_IniaProgress_brokeInitialize, Toast.LENGTH_LONG).show();
                InitializeSubmitProgress.this.finish();
                return true;
            default:
                break;
        }
        return super.onKeyDown(KeyCode, event);
    }
}
