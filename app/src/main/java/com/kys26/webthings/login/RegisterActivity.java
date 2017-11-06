package com.kys26.webthings.login;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.httpnetworks.MyJsonRequestWithCookie;
import com.kys26.webthings.main.MainActivity;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.util.MyToast;
import com.kys26.webthings.util.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class RegisterActivity extends Activity {
    @InjectView(R.id.nickname_edit)
    EditText mNicknameEdit;
    @InjectView(R.id.true_name_edit)
    EditText mTruenameEdit;
    @InjectView(R.id.password_edit)
    EditText mPasswordEdit;
    @InjectView(R.id.email_edit)
    EditText mEmailEdit;
    @InjectView(R.id.phone_edit)
    EditText mPhoneEdit;
    @InjectView(R.id.cmd_left_back)
    ImageView mCmdLeftBack;
    @InjectView(R.id.left_btn)
    FrameLayout mLeftBtn;
    @InjectView(R.id.relativeLayout)
    RelativeLayout mRelativeLayout;
    @InjectView(R.id.regist_btn)
    Button mRegistBtn;
    @InjectView(R.id.edit_layout)
    LinearLayout mEditLayout;

    public static final int REGISTER = 31;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        ButterKnife.inject(this);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        //初始化注解
        ViewUtils.inject(this);
    }

    @OnClick({R.id.left_btn, R.id.regist_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.regist_btn:
                if (isPerfect()) {//成功了之后就可以发送请求了
                    register();
                }
                break;
            case R.id.left_btn:
                RegisterActivity.this.finish();
                break;
        }
    }


    /**
     * 判断是否输入完全
     *
     * @return
     */
    private boolean isPerfect() {
        if (!StringUtil.isEmpty(mNicknameEdit.getText().toString())
                & !StringUtil.isEmpty(mTruenameEdit.getText().toString())
                & !StringUtil.isEmpty(mPasswordEdit.getText().toString())
                & !StringUtil.isEmpty(mEmailEdit.getText().toString())
                & !StringUtil.isEmpty(mPhoneEdit.getText().toString())) {

            if (StringUtil.isMobileNO(mPhoneEdit.getText().toString())) {
                if (StringUtil.isEmail(mEmailEdit.getText().toString())) {
                    return true;
                } else {
                    Toast.makeText(getApplicationContext(), "请输入正确的邮箱地址", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "请完善所有信息", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    private void register(){
        JSONObject jb = new JSONObject();
        try {

            jb.put("nickName",mNicknameEdit.getText().toString());
            jb.put("trueName",mTruenameEdit.getText().toString());
            jb.put("passWord",mPasswordEdit.getText().toString());
            jb.put("contactPhone",mPhoneEdit.getText().toString());
            jb.put("email",mEmailEdit.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            doRegist(Path.host+Path.URL_REGISTER,jb,REGISTER);
    }

    public void doRegist(String url, JSONObject jsonObject, int what) {

        //调用静态SharedPreferences获取cookie值
        MethodTools.sPreFerCookie = getSharedPreferences("Cookies",
                Context.MODE_PRIVATE);
        MyJsonRequestWithCookie.newhttpPost(url, jsonObject,
                MethodTools.sPreFerCookie.getString("cookie", "null"), what);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initHandler();
    }

    private void initHandler(){
        MethodTools.handlerJson = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case REGISTER:
                        Log.e("register",msg.obj.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(msg.obj.toString());
                            if ("Success".equals(jsonObject.get("Status"))){
                                MyToast.makeTextToast(RegisterActivity.this,
                                "注册成功", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
                                finish();
                            } else if ("PhoneExist".equals(jsonObject.get("Status"))){
                                Toast.makeText(getApplicationContext(), "手机号已经注册过了", Toast.LENGTH_SHORT).show();
                            }else {
                                MyToast.makeTextToast(RegisterActivity.this,
                                "注册失败，请检查网络和填写的信息", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
    }
}
