/**
 * LoginActivity.java [V 1..0.0]
 * zhangyx Create at 2014-11-26 下午4:22:46
 */
package com.kys26.webthings.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kys26.webthings.app.MyApplication;
import com.kys26.webthings.dialog.IntentDialog;
import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.httpnetworks.MyImageRequest;
import com.kys26.webthings.httpnetworks.MyJsonRequestWithCookie;
import com.kys26.webthings.main.MainActivity;
import com.kys26.webthings.method.MethodTools;

import com.kys26.webthings.personalcenter.Data;
import com.kys26.webthings.util.AnimationUtil;
import com.kys26.webthings.util.MyToast;
import com.kys26.webthings.util.VolleyUtil;
import com.lidroid.xutils.ViewUtils;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONObject;

import static com.kys26.webthings.httpconstant.Path.verify;

/**
 * @author Admin-徐建强
 * @author 李赛鹏
 * @fuction 登录界面 LoginActivity
 * @time 2014-11-26 下午4:22:46
 * @time 2016年10月17日12:35:22
 */
public class LoginActivity extends Activity {

    //    //布局控件初始化
//    @ViewInject(R.id.cet_userNo)
//    private ClearEditText userName;
//    @ViewInject(R.id.cet_userPwd)
//    private ClearEditText userPwd;
//    @ViewInject(R.id.tv_login)
//    private TextView loginBtn;
//    @ViewInject(R.id.forgetPassword)
//    private Button forgetPassword_btn;
//    @ViewInject(R.id.cet_verifycode)
//    private ClearEditText verifycode;
//    @ViewInject(R.id.verify)
//    private ImageView verify;
//    @ViewInject(R.id.verifyframe)
//    private FrameLayout verifyframelayout;
//    /**用户注册按钮*/
//    @ViewInject(R.id.noaccount)
//    private Button noAccount;
//
    //声明图片请求类
    private MyImageRequest imageRequest = new MyImageRequest();
    //    //刷新验证码的进度显示
//    @ViewInject(R.id.verifyprogress)
//    private  ProgressBar verifyprogressBar;
    //声明SharedPreferences保存cookie
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor editor;
    /**
     * 设置cookie获取状态
     */
    private boolean cookieRight = false;
    /**
     * 设置cookie保存值
     */
    private String cookie = null;
    private ImageView HeadImg;
    private ImageView verifyCodeImg;
    private Button login_btn;
    private EditText nickname_edit, password_edit, verifyCode_edit;
    private TextView forgetpassword_txt;
    private ProgressBar mProgressBar;
    private FrameLayout left_btn;
    private CheckBox rember_checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        if (Build.VERSION.SDK_INT >= 21) {
//            View decorView = getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            decorView.setSystemUiVisibility(option);
//            getWindow().setNavigationBarColor(Color.TRANSPARENT);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
        // 初始化IOC注解
        ViewUtils.inject(this);
        //初始化UI组件
        initView();

    }

//    @Override
//    protected int getContentView() {
//        return R.layout.activity_login;
//    }

//    public void setLeft_Img(ImageView left_Img) {
//        this.left_Img = left_Img;
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //
    private void initView() {
//        // TODO Auto-generated method stub
//        //获取初始验证码
        login_btn = (Button) findViewById(R.id.login_btn);
        nickname_edit = (EditText) findViewById(R.id.nickname_edit);
        password_edit = (EditText) findViewById(R.id.password_edit);
        verifyCode_edit = (EditText) findViewById(R.id.verifyCode_edit);
        left_btn = (FrameLayout) findViewById(R.id.left_btn);
        verifyCodeImg = (ImageView) findViewById(R.id.verifyCode_img);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        SharedPreferences s = getSharedPreferences("savePassword", MODE_PRIVATE);
        nickname_edit.setText(s.getString("nickName", ""));
        password_edit.setText(s.getString("passWord", ""));
        rember_checkbox = (CheckBox) findViewById(R.id.rember_checkbox);
        rember_checkbox.setChecked(true);
        forgetpassword_txt = (TextView) findViewById(R.id.forgetpassword_txt);
        getVerify();
        //初始换界面图片的设置
//		initImgs();
//        //getActivityView and Login
        login_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mProgressBar.getVisibility() == View.INVISIBLE) {
                    doLoginClickExecute();
                } else {
                    Toast.makeText(LoginActivity.this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //forgetPasswodr and to find
        forgetpassword_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ModifyPassWordActivity.class));
            }
        });
//        MethodTools.HnadlerVOlleyJson=new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//            }
//        };
        //点击获取新的验证码
        verifyCodeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getVerify();
            }
        });
//        //注册新用户
//        noAccount.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
//            }
//        });
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.this.finish();
            }
        });
    }

    private String userNo;
    private String userPwdT;
    private String userMsg;

    private void doLoginClickExecute() {
        userNo = nickname_edit.getText().toString();
        userPwdT = password_edit.getText().toString();
        userMsg = verifyCode_edit.getText().toString();
        if (TextUtils.isEmpty(userNo)) {
            Toast.makeText(getApplicationContext(), "用户名不能为空", Toast.LENGTH_LONG).show();
//            showToast("用户名不能为空");
            nickname_edit.setFocusable(true);
            nickname_edit.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(userPwdT)) {
//            showToast("密码不能为空");
            Toast.makeText(getApplicationContext(), "密码不能为空", Toast.LENGTH_LONG).show();
            password_edit.setFocusable(true);
            password_edit.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(userMsg)) {
//            showToast("验证码不能为空");
            Toast.makeText(getApplicationContext(), "验证码不能为空", Toast.LENGTH_LONG).show();
            verifyCode_edit.setFocusable(true);
            verifyCode_edit.requestFocus();
            return;
        }
        MyApplication app = new MyApplication();
        app.setUserName(userNo);
        //网络请求登陆
        doSubmit(userNo, userPwdT);
    }

    /**
     * @param userNo
     * @param userPwdT
     * @author Admin-徐建强 create at 2014-7-26 下午4:22:46
     */
    private void doSubmit(String userNo, String userPwdT) {
        //显示等待进度条
        if (rember_checkbox.isChecked()) {
            savePassWord(userNo, userPwdT);
        }
        IntentDialog.createDialog(LoginActivity.this, "正在提交...").show();
        //包装json数据
        JSONObject jsonObjectPack = new JSONObject();
        try {
            jsonObjectPack.put("nickName", userNo);
            jsonObjectPack.put("passWord", userPwdT);
            jsonObjectPack.put("verifyCode", verifyCode_edit.getText().toString());
//            jsonObjectPack.put("nickName", AESUtils.aesEncrypt(userNo));
//            jsonObjectPack.put("passWord", AESUtils.aesEncrypt(userPwdT));
//            jsonObjectPack.put("verifyCode", AESUtils.aesEncrypt(verifyCode_edit.getText().toString()));
//            jsonObjectPack.put("verification", AESUtils.aesEncrypt(DataUtil.getTimeNow()));
        } catch (Exception e) {
            Log.i("json打包异常", e.toString());
        }
        //调用方法，开始请求
        MyJsonRequestWithCookie.httpPost(Path.host + Path.login,
                jsonObjectPack, cookie);
        //利用hander接收子线程提交登陆后的response
        MethodTools.handlerJson = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //判断返回结果类型
                switch (msg.what) {
                    //请求成功，开始分析数据
                    case Code.SUCCESS:
                        analysis(msg.obj.toString());
                        //等待进度条消失
                        IntentDialog.dialog.cancel();
                        break;
                    //请求失败，弹出提示
                    case Code.FAILURE:
                        getVerify();
                        //等待进度条消失
                        IntentDialog.dialog.cancel();
                        //请求超时
                        MyToast.makeTextToast(LoginActivity.this,
                                "请求超时", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    /**
     * 保存用户的账号密码
     */
    private void savePassWord(String nickName, String passWord) {
        SharedPreferences mPreferences = getSharedPreferences("savePassword", MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mPreferences.edit();
        mEditor.putString("nickName", nickName);
        mEditor.putString("passWord", passWord);
        mEditor.apply();
    }

    /**
     * @param result 解析的数据
     * @author Admin-徐建强 create at 2014-8-2 下午4:22:46
     * @fuction 解析json数据
     */
    private void analysis(String result) {
        int code = 0;
        try {
            JSONObject jsonObjectAnl = new JSONObject(result);
            result = jsonObjectAnl.getString("Status");
            /**获取融云链接所用的token*/
            Data.token = jsonObjectAnl.getString("token");
            code = jsonObjectAnl.getInt("sessionCode");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//        if (code==200){
//            if (code != 500) {
            try {
//                if (AESUtils.aesDecrypt(result).equals("Success")) {
                if (result.equals("Success")) {
                    //开始跳转
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    AnimationUtil.finishActivityAnimation(LoginActivity.this);
                    //保存cookie值
                    saveCookieVerify(cookie);
                } else if (result.equals("NotHaveUser")) {
                    getVerify();
                    MyToast.makeTextToast(LoginActivity.this,
                            "用户名不存在", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
                } else if (result.equals("PasswordError")) {
                    getVerify();
                    MyToast.makeTextToast(LoginActivity.this,
                            "密码错误", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
                } else if (result.equals("VerifyCodeError")) {
                    getVerify();
                    MyToast.makeTextToast(LoginActivity.this,
                            "验证码错误", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//            else {
//                getVerify();
//                MyToast.makeTextToast(LoginActivity.this,
//                        "该账号已经登录，请勿重复登录", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
//            }
//        }
    }

    /**
     * @function:获取验证码
     * @date 2014.08.02
     */
    private void getVerify() {
        if (imageRequest.getImage(verifyCodeImg, mProgressBar, Path.host + verify) == Code.SUCCESS) {
            //利用hander得知是否得到cookie成功，如果得到就保存
            MethodTools.handlerCookieVerify = new Handler() {
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == Code.SUCCESS) {
                        //暂存cookie值
                        cookie = msg.obj.toString();
                        Log.e("cookie cookie",cookie);

                        //设置cookie状态值为获取到
                        cookieRight = true;
                    }
                }
            };
        } else {
            MyToast.makeTextToast(LoginActivity.this,
                    "验证码加载失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
        }
    }

    /**
     * @param cookiestr
     * @author:Created by kys_26 徐建强 on 2015/6/7.
     * @function:声明saveCookie方法 用来保存http请求头, 既是cookie, 这里保存的是验证码请求cookie值
     */
    public void saveCookieVerify(String cookiestr) {
        //打印保存的cookie值
        mSharedPreferences = getSharedPreferences("Cookies",
                Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        editor.putString("cookie", cookiestr);
        editor.putString("userName", userNo);
        editor.commit();
    }

    /**
     * @return null
     * @author kys_26使用者：徐建强  on 2015-11-2
     * @function:复写重载onResume
     */
    @Override
    protected void onResume() {
        //每次处于焦点使cookie的状态设置为false
        cookieRight = false;
        //设置cookie为空
        cookie = null;
        super.onResume();
    }

    /**
     * @param event
     * @author:Created by kys_26 徐建强 on 2015/6/7.
     * @function:返回键复写
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                //取消所有请求
                VolleyUtil.getQueue(LoginActivity.this).cancelAll(this);
                finish();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
