package com.kys26.webthings.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.kys26.webthings.util.DensityUtil;
import com.zhangyx.MyGestureLock.R;

/**
 杨铭 Created by kys_8 on 17/6/24,0024. <p>Email:771365380@qq.com</p> <p>Mobile phone:15133350726</p>
 */
public class WaveProgressView extends View//implements View.OnClickListener
{
    /**画笔*/
    private Paint mPaint;
    /**半径*/
    private int radius;
    /**字体颜色*/
    private int textColor;
    /**字体大小*/
    private int textSize;
    /**水位进度条颜色*/
    private int progressColor;
    /**应该是大圆的颜色*/
    private int backgroundColor;
    /**进度*/
    private int progress;
    /**进度最大值*/
    private int maxProgress;
    private int width,height;
    /**
     波浪Path 类*/
    private Path mPath;
    /**
     一个波浪长度*/
    private int mWaveLength = 1000;
    /**
     波浪个数*/
    private int mWaveCount;
    /**
     波浪平移量*/
    private int mOffset;
    /**
     波浪的中间轴*/
    private int mCenterY;
    /**
     屏幕高度*/
    private int mScreenHeight;
    /**
     屏幕宽度*/
    private int mScreenWidth;
    private successInintGate mSuccessInitGate;
    public WaveProgressView(Context context)
    {
        this(context,null);
    }
    public WaveProgressView(Context context, AttributeSet attrs)
    {
        this(context,attrs,0);
    }
    public WaveProgressView(Context context,AttributeSet attrs,int defStyle)
    {
        super(context,attrs,defStyle);
        mPaint = new Paint();
        mPath = new Path();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaveProgressView);
        //radius = (int) a.getDimension(R.styleable.WaveProgressView_radius,150);
        //        textColor = a.getColor(R.styleable.WaveProgressView_progress_text_color, Color.GREEN);
        //        textSize = a.getDimensionPixelSize(R.styleable.WaveProgressView_progress_text_size,20);
        progressColor = a.getColor(R.styleable.WaveProgressView_progress_color, Color.BLUE);
        backgroundColor = a.getColor(R.styleable.WaveProgressView_background_color, Color.WHITE);
        //        progress = a.getInt(R.styleable.WaveProgressView_progress,0);
        //        maxProgress = a.getInt(R.styleable.WaveProgressView_maxProgress,100);
        a.recycle();
        //setOnClickListener(this);
    }

    /**
     * onSizeChanged()实在布局发生变化时的回调函数，间接回去调用onMeasure, onLayout函数重新布局
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mScreenHeight = h;
        mScreenWidth = w;
        //加1.5：至少保证波纹有2个，至少2个才能实现平移效果
        mWaveCount = (int) Math.round(mScreenWidth / mWaveLength + 1.5);
        mCenterY = mScreenHeight/2;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        mPaint.setColor(backgroundColor);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0,0,mScreenWidth,mScreenHeight,mPaint);
        /**画水波*/
        mPaint.setColor(progressColor);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPath.reset();//清空路径上所有曲线。不是必须。 清空所有已经画过的path至原始状态。重置
        //移到屏幕最左边
        int newH = mCenterY*2 - TheHeight.theY  ;//205  差一点
        mPath.moveTo(-mWaveLength + mOffset,newH);
        for (int i = 0;i < mWaveCount;i++){
            //正弦曲线
            mPath.quadTo((-mWaveLength * 3 / 4) + (i * mWaveLength) + mOffset,
                         newH + 60,
                         (-mWaveLength / 2) + (i * mWaveLength) + mOffset,
                         newH);
            mPath.quadTo((-mWaveLength / 4) + (i * mWaveLength) + mOffset,
                         newH - 60,
                         i * mWaveLength + mOffset,
                         newH);
        }
        mPath.lineTo(mScreenWidth,mScreenHeight);
        mPath.lineTo(0,mScreenHeight);
        mPath.close();
        canvas.drawPath(mPath,mPaint);
    }
    /**
     * 开启水波波动动画，还有上升动画
     * 将动画从0 到mWaveLength 之内的变化值赋给 mOffset
     */
    public void controlWave(final successInintGate mSuccessInitGate){
        this.mSuccessInitGate=mSuccessInitGate;
        ValueAnimator upAnimator = ValueAnimator.ofInt(0,DensityUtil.dip2px(getContext(),150));
        upAnimator.setDuration(5*60*1000);
        upAnimator.setInterpolator(new LinearInterpolator());
        upAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                TheHeight.theY = (int) animation.getAnimatedValue();//上升
                Log.e("水波dip2px", TheHeight.theY +"");
            }
        });
        upAnimator.start();

        final ValueAnimator waveAnimator = ValueAnimator.ofInt(0, mWaveLength);
        waveAnimator.setDuration(1000);
        waveAnimator.setRepeatCount(ValueAnimator.INFINITE);
        waveAnimator.setInterpolator(new LinearInterpolator());
        waveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mOffset = (int) animation.getAnimatedValue();
                postInvalidate();
                if (TheHeight.theY == DensityUtil.dip2px(getContext(),150))
                {
                    waveAnimator.cancel();
                    declineWave();
                    mSuccessInitGate.onSuccessInintGate();
                    Log.e("水波", "取消了");
                }
            }
        });
        if (TheHeight.theY<DensityUtil.dip2px(getContext(),150)) {
            waveAnimator.start();
        }
    }

    /**
     * 控制水波下降的动画
     */
    private void declineWave(){
        Toast.makeText(getContext(),"创建成功了",Toast.LENGTH_SHORT).show();
        final ValueAnimator downAnimator = ValueAnimator.ofInt(DensityUtil.dip2px(getContext(),150), 0);
        downAnimator.setDuration(2*1000);
        downAnimator.setInterpolator(new LinearInterpolator());
        downAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mOffset = (int) animation.getAnimatedValue();
                TheHeight.theY = (int) animation.getAnimatedValue();//下降
                Log.e("下降水波421", TheHeight.theY+"");
                postInvalidate();
                if (TheHeight.theY == 0){
                    downAnimator.cancel();
                    Log.e("下降水波", TheHeight.theY+"");
                }
            }
        });
        downAnimator.start();
    }
    public interface successInintGate{
        void onSuccessInintGate();
    }
}
