/**
 * BaseActivity.java [V 1..0.0]
 * classes : com.zhangyx.MyGestureLock.BaseActivity
 * zhangyx Create at 2015-1-16 下午2:23:13
 */
package com.kys26.webthings.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kys26.webthings.dialog.IntentDialog;
import com.kys26.webthings.httpnetworks.MyJsonRequestWithCookie;
import com.kys26.webthings.httpnetworks.VolleyJsonRequest;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.util.DensityUtil;
import com.kys26.webthings.view.VerticalSwipeRefreshLayout;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONObject;

/**
 *
 */
public abstract class BaseActivity extends AppCompatActivity {
    private RelativeLayout title_layout;
    private TextView title_txt;
    //	private ImageView left_iv;
//	private ImageView right_iv;
    private RelativeLayout warning_layout;
    private   TextView warning_layout_num;
    private FrameLayout left_btn, right_btn;
    private ImageView left_img, right_img;
    public ViewGroup contentView;
    private View titlebar;
    private static final String TAG = BaseActivity.class.getSimpleName();
    private VerticalSwipeRefreshLayout mVerticalSwipeRefreshLayout;
    /**
     * dialog
     */
    private Dialog dialog;
    /**
     * 农场请求的Code
     */
    public static final int GET_FARM = 1;
    /**
     * 监测节点请求的Code
     */
    public static final int GET_DEVICE = 2;
    public static final int NODE_LIST = 3;//获得控制节点列表
    /**
     * 控制控制节点
     */
    public static final int WIND_CONTROL = 4;
    /**
     * 获取节点状态
     */
    public static final int CONTROL_STATE = 5;
    /**
     * 获得历史数据查询
     */
    public static final int History = 6;
    /**
     * 农场名字
     */
    public static final int UPDATE_FARM = 7;
    /**
     * 计时验证
     */
    public static final int TIMING_VALIDATE = 8;
    /**
     * 定时验证
     */
    public static final int SWITCH_VALIDATE = 9;
    /**
     * 得到节点下所有定时信息
     */
    public static final int GET_ALL_TIME = 10;
    /**
     * 获取某一条定时项的状态
     */
    public static final int GET_TIME_STATE = 11;
    /**
     * 删除定时
     */
    public static final int DELETE_TIME = 12;
    /**
     * 打开关闭定时
     */
    public static final int CONTROL_TIME = 13;
    /**
     * 保存定时信息
     */
    public static final int SAVE_TIMING = 14;
    /**
     * 更新（修改）定时项
     */
    public static final int UNDATE_TIME = 15;

    /**
     * 更改风机节点名称
     */
    public static final int UPDATE_NODENAME = 16;
    /**
     * 获取告警
     */
    public static final int GET_WARNING = 17;
    /**
     * 更新告警
     */
    public static final int UPDATE_WARNING = 18;
    /**
     * 自动控制
     */
    public static final int CONTROL_AUTO = 19;
    /**
     * 获取告警的节点状态
     */
    public static final int GET_KIDSTATE=20;
    /**
     * 获取降温哪里的值
     */
    public static final int GET_COOLING_NODE=21;
    /**
     * 退出哪里
     */
    public static final int EXIT=22;
    /**
     * 获取网关状态的值
     */
    public static final int GET_GATEWAYSTATE=23;

    /**
     * 监控频道
     * */
    public static final int VIDEO_CHANNEL = 25;
    /**
     * 现在处于定时内，开关验证定时的状态
     */
    public static final int INTIME_VALIDATE_FANSTATE=26;
    /**
     * 节点状态
     * */
    public static final int NODE_STATE=27;
    public static final int CLIENT = 28;
    public static final int UPDATA_CLIENT = 29;
    public static final int HEAD_PORTRAIT = 30;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
//        ButterKnife.inject(this);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        // 透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        mVerticalSwipeRefreshLayout = (VerticalSwipeRefreshLayout) findViewById(R.id.swipeRefresh_layout);
        right_img = (ImageView) findViewById(R.id.cmd_right_setting);
        left_img = (ImageView) findViewById(R.id.cmd_left_back);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        warning_layout = (RelativeLayout) findViewById(R.id.warning_layout);
        warning_layout_num = (TextView) findViewById(R.id.top_warn_num);

        setTranslucentStatus();
        int titlebarResId = getTitlebarResId();
        if (titlebarResId != 0) {
            LinearLayout view = (LinearLayout) findViewById(R.id.base_view);
            view.removeViewAt(0);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(this, 50));
            ViewGroup titleView = (ViewGroup) View.inflate(this, titlebarResId, null);
            view.addView(titleView, 0, lp);
            view.setBackgroundDrawable(titleView.getBackground());
            titlebar = titleView;
        } else {
            titlebar = findViewById(R.id.base_titlebar);
            left_btn = (FrameLayout) findViewById(R.id.left_btn);
            left_btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    onClickLeft();
                }
            });
            right_btn = (FrameLayout) findViewById(R.id.right_btn);
            right_btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    onClickRight();
                }
            });
            title_txt = (TextView) findViewById(R.id.title_text);
        }
        contentView = (ViewGroup) findViewById(R.id.base_contentview);
        contentView.addView(View.inflate(this, getContentView(), null));
        setRightBtnVisible(false);
//        }
    }

//        @Override
//        public void overridePendingTransition(int enterAnim, int exitAnim) {
//            super.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//        }

    /**
     * 设置状态栏背景状态
     */
    private void setTranslucentStatus() {
        //判断当前SDK版本号，如果是4.4以上，就是支持沉浸式状态栏的
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Window window = getApplicationContext().getWindow();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getApplicationContext().getResources().getColor(R.color.blue));
        }
    }

    /**
     * 下拉刷新
     *
     * @param isEnable
     */
    public void setEnableRefresh(boolean isEnable, VerticalSwipeRefreshLayout.OnRefreshListener onRefreshListener) {
        if (isEnable) {
            mVerticalSwipeRefreshLayout.setEnabled(true);
            mVerticalSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        } else {
            mVerticalSwipeRefreshLayout.setEnabled(false);
        }
    }

//    protected void SetRefreshData() {
//
//    }

    /**
     * 点击左侧按钮
     * 默认什么不做
     */
    protected void onClickLeft() {

    }

    /**
     * 点击右侧按钮
     * 默认什么都不做
     */
    protected void onClickRight() {

    }

    /**
     * 显示顶部ProgressBar
     */
    protected void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    protected void dismissProgress() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * @return 返回progressbar对象
     */
    protected ProgressBar getProgressBar() {
        return mProgressBar;
    }

    /**
     * 设置左侧按钮显示与隐藏
     *
     * @param visible
     */
    public void setLeftBtnVisible(Boolean visible) {
        if (left_btn != null) {
            if (visible) {
                left_btn.setVisibility(View.VISIBLE);
            } else {
                left_btn.setVisibility(View.GONE);
            }
        }
    }


    public View getLeft_btn() {
        return left_btn;
    }
    public View getTopNum(){
        return warning_layout_num;
    }

    /**
     * 设置左侧按钮图片
     *
     * @param img
     */
    public void setLeft_img(int img) {
        left_img.setBackgroundResource(img);
    }

    /**
     * 设置右侧按钮显示与隐藏
     *
     * @param visible
     */
    public void setRightBtnVisible(Boolean visible) {
        if (right_btn != null) {
            if (visible) {
                right_btn.setVisibility(View.VISIBLE);
            } else {
                right_btn.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 获取自定义标题栏
     * 如果子类复写并返回不等于0的布局文件，将会覆盖默认标题
     * 返回0 将会采用默认标题
     *
     * @return
     */
    protected int getTitlebarResId() {
        return 0;
    }

    /**
     * 设置中间标题
     *
     * @param title
     */
    public void setTitle(String title) {
        if (title_txt != null) {
            if (title_txt != null) {
                title_txt.setText(title);
            }
        }
    }

    /**
     * 设置右边图片属性
     *
     * @param imgId
     */
    public void setRightImg(int imgId) {
        if (warning_layout.getVisibility() == View.VISIBLE) {
            warning_layout.setVisibility(View.GONE);
        }
        right_img.setVisibility(View.VISIBLE);
        right_img.setImageResource(imgId);
//        right_btn.addView(right_img);
    }

    public View getTitleBar() {
        return titlebar;
    }

    /**
     * @param x 右上角消息的值
     */
    protected void addRightWidget(int x) {
        if (right_img.getVisibility() == View.VISIBLE) {
            right_img.setVisibility(View.GONE);
        }
        warning_layout.setVisibility(View.VISIBLE);
        warning_layout_num.setText(String.valueOf(x));
    }

//    /**
//     * @param x 左上角消息的值
//     */
//    public void addLeftWidget(int x) {
//        warning_layout_num.setVisibility(View.VISIBLE);
//        warning_layout_num.setText(String.valueOf(x));
//    }
public VerticalSwipeRefreshLayout getRefreshView() {
        return mVerticalSwipeRefreshLayout;
    }


    /**
     * 获取中间内容显示区
     *
     * @return
     */
    protected abstract int getContentView();

    /**
     * 初始化请求(农场列表)
     *
     * @param url        请求链接
     * @param jsonObject 请求消息
     */
    public void doRegist(String url, JSONObject jsonObject, int what) {
        //调用静态SharedPreferences获取cookie值
        MethodTools.sPreFerCookie = getSharedPreferences("Cookies",
                Context.MODE_PRIVATE);
        MyJsonRequestWithCookie.newhttpPost(url, jsonObject,
                MethodTools.sPreFerCookie.getString("cookie", "null"), what);
        showProgress();
//        dismissDialog();
//        showDialog();
    }

    /**
     * 初始化请求(请求农场数据)
     *
     * @param url        请求链接
     * @param json 请求消息
     */
    public void doVolleyRegist(String url,Activity activity,JSONObject json,int what){
        MethodTools.sPreFerCookie = getSharedPreferences("Cookies",
                Context.MODE_PRIVATE);
        VolleyJsonRequest.JsonRequest(activity,url,json,what);
        showProgress();
    }


    /**
     * 显示Dialog
     */
    public void showDialog() {
        if (dialog != null) {
            if (!dialog.isShowing()) {
                dialog.show();
            }
        } else {
            dialog = IntentDialog.createDialog(this, "正在获取数据......");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    /**
     * dialog消失
     */
    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
