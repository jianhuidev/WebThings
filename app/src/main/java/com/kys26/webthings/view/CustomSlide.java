package com.kys26.webthings.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.zhangyx.MyGestureLock.R;

/**
 * Created by lenovo on 2017/7/19.
 */

public class CustomSlide extends HorizontalScrollView {

    /**
     * 滚动条的宽度
     */
    private int mScrollWidth;

    /**
     * 是否打开
     */
    private boolean isOpen = false;

    /**
     * 是否是第一次打开
     */
    private boolean once = false;

    private TextView mTextView;

    /**
     * 滚动条的监听
     */
    private CustomSlideListener mCustomSlideListener;
    public CustomSlide(Context context) {
        super(context,null);
    }

    public CustomSlide(Context context, AttributeSet attrs) {
        super(context, attrs,0);
    }

    public CustomSlide(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOverScrollMode(OVER_SCROLL_NEVER);
    }

        @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (!once) {
                mTextView = (TextView) findViewById(R.id.tv_del);
                once = true;
            }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            this.scrollTo(0, 0);
            //获取水平滚动条可以滑动的范围，即右侧按钮的宽度
            mScrollWidth = mTextView.getWidth()*2;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                mCustomSlideListener.onDownOrMove(this);
                break;
            case MotionEvent.ACTION_UP:
                if (getScrollX() == 0){
                        mCustomSlideListener.onMyClick(this);
                }
                changeScrollx();
                return true;
            case MotionEvent.ACTION_CANCEL:
                changeScrollx();
                return true;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

      @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }


    /**
     * 按滚动条被拖动距离判断关闭或打开菜单
     */
    public void changeScrollx() {
        if (getScrollX() >= (mScrollWidth / 2)) {//x 方向移动一定的距离
            this.smoothScrollTo(mScrollWidth, 0);
            isOpen = true;
            mCustomSlideListener.onMenuIsOpen(this);
        } else {
            this.smoothScrollTo(0, 0);
            isOpen = false;
        }
    }

    /**
     * 打开滚动条
     */
    public void openSlide() {
        if (isOpen) {
            return;
        }
        this.smoothScrollTo(mScrollWidth, 0);
        isOpen = true;
        mCustomSlideListener.onMenuIsOpen(this);
    }

    /**
     * 关闭滚动条
     */
    public void closeSlide() {
        if (!isOpen) {
            return;
        }
        this.smoothScrollTo(0, 0);
        isOpen = false;
    }

    /**
     * 为滚动条设置监听
     * @param listener 滚动条监听
     */
     public void setCustomSlideListener(CustomSlideListener listener) {
        mCustomSlideListener = listener;
    }
    public interface CustomSlideListener {
        /**
         * 滚动条是否打开的监听
         * @param customSlide 滚动条监听
         */
        void onMenuIsOpen(CustomSlide customSlide);

        /**
         * 滚动条按下与滚动的监听
         * @param customSlide 滚动条监听
         */
        void onDownOrMove(CustomSlide customSlide);

        /**
         * 滚动条点击的监听
         * @param customSlide 滚动条监听
         */
        void onMyClick(CustomSlide customSlide);
    }

}
