package com.kys26.webthings.util;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.zhangyx.MyGestureLock.R;

/**
 * @author zjh
 *         Created by kys_8 on 2017/11/5.
 */
public class TimeCount extends CountDownTimer {

    private TextView mTextView;
    public TimeCount(TextView textView,long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.mTextView = textView;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        mTextView.setClickable(false); //设置不可点击
        mTextView.setText(millisUntilFinished / 1000 + "秒后可重新发送");  //设置倒计时时间
        mTextView.setBackgroundResource(R.drawable.verify_shape2);
    }

    @Override
    public void onFinish() {

        mTextView.setText("重新获取验证码");
        mTextView.setClickable(true);//重新获得点击
        mTextView.setBackgroundResource(R.drawable.verify_shape1);
    }
}
