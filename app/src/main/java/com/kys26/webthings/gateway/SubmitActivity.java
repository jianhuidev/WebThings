package com.kys26.webthings.gateway;

import android.os.Bundle;

import com.ab.activity.AbActivity;
import com.kys26.webthings.progress.CircleProgressBar;
import com.lidroid.xutils.ViewUtils;
import com.zhangyx.MyGestureLock.R;

/**
 * Created by 徐建强 on 2015/7/23.
 */
public class SubmitActivity extends AbActivity {

    /**进度条所需参数与控件*/
    private CircleProgressBar mBar;
    /**设置进度刷新标识*/
    private boolean wheelRunning;
    /**实时进度值*/
    private int wheelProgress = 0;
    /**设置进度最大值*/
    private int wheelMax=50;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customsprogress);
        // 初始化IOC注解
        ViewUtils.inject(this);
        //开启实时进度显示
        initProgress();
    }
    /**
     * @function:实时进度值条更新
     * @Created by 徐建强 on 2015/7/24.
     * @return null
     */
    private void initProgress(){
        //初始化控件查找
        mBar = (CircleProgressBar) findViewById(R.id.myProgress);
        //启动线程进行数据更新
        new Thread(r).start();
    }
    /**
     * @function:启动线程进行进度值更新
     * @Created by 徐建强 on 2015/7/24.
     * @return null
     */
    final  Runnable r=new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            wheelRunning = true;
            while(wheelRunning==true) {
                while (wheelProgress < wheelMax) {
                    //如果到了实时进度值到了最大，设置循环跳出
                    if(wheelProgress==99){
                        //设置刷新表示为false
                       wheelRunning=false;
                    }
                    wheelProgress++;
                    mBar.setProgressNotInUiThread(wheelProgress);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            SubmitActivity.this.finish();
        }
    };
}
