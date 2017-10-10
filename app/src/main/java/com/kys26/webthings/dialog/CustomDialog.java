package com.kys26.webthings.dialog;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.zhangyx.MyGestureLock.R;


/**
 * Created by kys-36 on 2017/3/6.
 *
 * @param
 * @author douwen
 * @function
 */

public class CustomDialog extends android.app.DialogFragment {
    private String title_str = "丛云";//标题
    private OnDialogListener mOnDialogListener;//按钮监听接口
    private int titleColor = Color.WHITE;//标题颜色
    private float titleSize = 15;//标题文字大小,layout中设置无效.
    private View mView = null;
    private boolean isSure = true;
    private boolean isCancel = true;
    private boolean isTitle = true;
    private boolean allowDismiss = true;


    public interface OnDialogListener {
        void sureListener();

        void cancelListener();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    public void setDismiss(boolean allowDismiss) {
        this.allowDismiss = allowDismiss;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.customdialog, ((ViewGroup) window.findViewById(android.R.id.content)), false);//需要用andrid.R.id.content这个view
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处
        Button title = (Button) view.findViewById(R.id.dialog_title);
        title.setText(title_str);
        title.setTextColor(titleColor);
        title.setTextSize(titleSize);
        if (!isTitle) {
            title.setVisibility(View.GONE);
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.view);
        layout.setLayoutParams(lp);//设置布局参数
        layout.setOrientation(LinearLayout.VERTICAL);// 设置子View的Linearlayout// 为垂直方向布局
        if (mView != null) {
            layout.addView(mView);
        }
        Button sure = (Button) view.findViewById(R.id.sure);
        if (!isSure) {
            sure.setVisibility(View.GONE);
        }
        Button cancel = (Button) view.findViewById(R.id.cancel);
        if (!isCancel) {
            cancel.setVisibility(View.GONE);
        }
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mOnDialogListener.sureListener();
                } catch (Exception e) {
                    Log.e("Dialog", "dialog问题e:" + e.toString());
                }
                if (allowDismiss) {
                    getDialog().dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mOnDialogListener.cancelListener();
                } catch (Exception e) {
                    Log.e("Dialog", "dialog问题e:" + e.toString());
                }
                getDialog().dismiss();
            }
        });
        Log.i("num", "1");
        return view;
    }

    /**
     * @param onDialogListener
     * @function 按钮点击监听
     */
    public CustomDialog setOnDialogListener(OnDialogListener onDialogListener) {
        mOnDialogListener = onDialogListener;
        return this;
    }

    /**
     * @param str
     * @return
     * @function 设置标题
     */
    public CustomDialog setTitle(String str) {
        title_str = str;
        Log.i("num", "2" + title_str);
        return this;
    }

    /**
     * @param titleSize
     * @return
     * @function 设置标题文字大小
     */
    public CustomDialog setTitleSize(float titleSize) {
        this.titleSize = titleSize;
        return this;
    }

    /**
     * @param titleColor
     * @return
     * @function 设置标题颜色
     */
    public CustomDialog setTitleColor(int titleColor) {
        this.titleColor = titleColor;
        return this;
    }

    /**
     * @param mview
     * @return
     * @function 添加自定义View
     */
    public CustomDialog setView(View mview) {
        this.mView = mview;
        return this;
    }

    /**
     * @param isSure
     * @return
     * @function 设置确定按钮是否显示
     */
    public CustomDialog setSureIsVisible(boolean isSure) {
        this.isSure = isSure;
        return this;
    }

    /**
     * @param isCancel
     * @return
     * @function 设置取消按钮是否显示
     */
    public CustomDialog setCancelIsVisible(boolean isCancel) {
        this.isCancel = isCancel;
        return this;
    }

    /**
     * @param isTitle
     * @return
     */
    public CustomDialog setTitleIsVisible(boolean isTitle) {
        this.isTitle = isTitle;
        return this;
    }

    public void DissMissDialog() {
        getDialog().dismiss();
    }
}
