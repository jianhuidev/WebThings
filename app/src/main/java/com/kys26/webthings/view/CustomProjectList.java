package com.kys26.webthings.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * @author 李赛鹏
 * @fuction 预计解决scrollView和listView滑动冲突问题
 * Created by Administrator on 2016/12/7.
 */

public class CustomProjectList extends ListView{
    public CustomProjectList(Context context) {
        super(context);
    }
    public CustomProjectList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CustomProjectList(Context context, AttributeSet attrs,
                             int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
