package com.kys26.webthings.projectmanage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kys26.webthings.dialog.IntentDialog;
import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.httpnetworks.MyJsonRequestWithCookie;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.util.MyToast;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.kys26.webthings.dialog.IntentDialog.dialog;

/**
 * @author 李赛鹏
 * @class 配置平面图
 * Created by Administrator on 2016/12/18.
 */
public class ConfigurationPlanActivity extends Activity implements View.OnTouchListener {
    @InjectView(R.id.planlayout)
    RelativeLayout mPlanlayout;
    //设置几个view
    int x = 10;
    //屏幕的高度和宽度
    private static int width;
    private static int height;
    @InjectView(R.id.confplan_left_iv)
    ImageView mConfplanLeftIv;
    @InjectView(R.id.confplan_NextStep)
    TextView mConfplanNextStep;
    private int widthsum;
    //控件的高度和宽度
    private static int viewwidth = 200;
    private static int viewheight = 60;
    //记录高度和宽度的值
    private static int heightindex = 0;
    private static int widthindex = 0;
    Button button[] = new Button[x];
    private static final String TAG = "ConfigurationPlanActivity";
    //拖动之后最后的view的x和y值
    private int lastx;
    private int lasty;
    private int GetGwNum = 0, NextType = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_configurationplan);
        ButterKnife.inject(this);
        initView();
    }

    /***
     * @fuction 初始化视图
     */
    private void initView() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        //RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        // mPlanlayout.setLayoutParams(params);
        for (int i = 0; i < x; i++) {
            button[i] = new Button(this);
            button[i].setTag(i);
            button[i].setId(i);
            button[i].setText("网关" + i);
            button[i].setWidth(200);
            button[i].setHeight(60);
            button[i].setBackgroundResource(R.drawable.rectangle);
            button[i].setX(0);
            button[i].setY(dp2px(this, 60));
            // button[i].setX(0 + widthindex * 100);
            // button[i].setY(0 + heightindex * 60);
            mPlanlayout.addView(button[i]);
            button[i].setOnTouchListener(this);
//            widthsum = widthsum + 200;
//            widthindex++;
//            if (widthsum + 200 > width) {
//                heightindex = heightindex + 1;
//                widthindex = 0;
//                widthsum = 0;
//            }
        }
        JSONObject ob=new JSONObject();
        try {
            ob.put("projectid","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getWebData(Path.URL_GET_DEVICETYPE,ob,0);
    }

    /***
     * @param URL     获取网页数据的URL
     * @param send_jb 需要发送的json数据
     * @fuction 获取网页数据
     */
    public void getWebData(String URL, JSONObject send_jb, final int Type) {
        MethodTools.sPreFerCookie = getSharedPreferences("Cookies",
                Context.MODE_PRIVATE);
        MyJsonRequestWithCookie.newhttpPost(Path.host + URL, send_jb, MethodTools.sPreFerCookie.getString("cookie", "null"), 7);
        MethodTools.handlerJson = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == Code.LUCKYSEVEN) {
                    //获取数据后进行处理
                    if (Type == GetGwNum)
                        analysis(msg.obj.toString(), GetGwNum);
                    //  else if(Type==)
                    Log.e(TAG, msg.obj.toString());
                    IntentDialog.dialog.cancel();
                    //initList();
                } else if (msg.what == Code.FAILURE) {
                    //消除等待进度弹出框
                    dialog.cancel();
                    MyToast.makeImgAndTextToast(ConfigurationPlanActivity.this, getResources().getDrawable(R.drawable.tips_sry),
                            "请求失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
                }
            }
        };
    }

    /***
     * @param data 获取得到的json数据
     * @param Type 判断是否是价格数据或者下一步的index
     * @fuction 分析json数据
     */
    private void analysis(String data, int Type) {
        //将获取的数据保存在静态存储区里
        MethodTools.framData = data;
        //声明一个map将所有fram数据装载起来
        if (Type == GetGwNum) {
            try {
                //数据是一个json数组，开始进行筛选得到所有用户
                JSONArray jsonArray = new JSONArray(data.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    Log.e(TAG, "data:" + jsonArray.getJSONObject(i).toString());
                    HashMap<String, Object> map = new HashMap<String, Object>();
//                    map.put("price", jsonArray.getJSONObject(i).get("deviceprice").toString());
//                    map.put("name", jsonArray.getJSONObject(i).get("devicetypename").toString());
//                    map.put("idbegin", jsonArray.getJSONObject(i).get("idbegin").toString());
//                    map.put("idend", jsonArray.getJSONObject(i).get("idend").toString());
//                    list.add(i,map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Type == NextType) {
//            try {
//                JSONObject ob = new JSONObject(data.toString());
//                if (ob.get("Status").equals("Success")) {
//                    Log.e(TAG, "添加网关成功");
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastx = (int) motionEvent.getRawX();
                lasty = (int) motionEvent.getRawY();
                Log.e("zzzz", "motionEventX:" + motionEvent.getRawX() + "motionEventY:" + motionEvent.getRawY());
                //ToastHelper.show(this,"按下");
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) motionEvent.getRawX() - lastx;
                int dy = (int) motionEvent.getRawY() - lasty;
                Log.e(TAG, "left:" + view.getLeft() + "top:" + view.getTop() + "right:" + view.getTop() + "bottom:" + view.getBottom());
                int left = view.getLeft() + dx;
                int top = view.getTop() + dy;
                int right = view.getRight() + dx;
                int bottom = view.getBottom() + dy;
                if (left < 0) {
                    left = 0;
                    right = left + view.getWidth();
                }
                if (right > width) {
                    right = width;
                    left = right - view.getWidth();
                }
                if (top < 0) {
                    top = 0;
                    bottom = top + view.getHeight();
                }
                if (bottom > height) {
                    bottom = height;
                    top = bottom - view.getHeight();
                }
                view.layout(left, top, right, bottom);
                Log.i("@@@@@@", "position位置:" + left + ", " + top + ", " + right + ", " + bottom);
                lastx = (int) motionEvent.getRawX();
                lasty = (int) motionEvent.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return false;
    }

    public int caculatordistance(Button t1, Button t2) {
        //if(t1.getX())
        return 0;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    @OnClick({R.id.confplan_left_iv, R.id.confplan_NextStep})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confplan_left_iv:
                this.finish();
                break;
            case R.id.confplan_NextStep:
                Intent intent = new Intent();
                intent.setClass(ConfigurationPlanActivity.this, CompleteProject.class);
                startActivity(intent);
                break;
        }
    }


}
