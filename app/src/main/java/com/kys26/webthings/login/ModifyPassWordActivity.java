package com.kys26.webthings.login;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.main.BaseActivity;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.util.MyToast;
import com.kys26.webthings.util.StringUtil;
import com.kys26.webthings.util.TimeCount;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lee on 2017/8/31.   有这个可以证明成功
 */

public class ModifyPassWordActivity extends BaseActivity {
//    private EditText newPassWord_edit, oldPassWord_edit, reNewPassWord_edit;
    private LinearLayout line_modify;
    private Button modify_sure_btn;

    private TextView verifyCode_text;
    //private ImageView img;
    private TimeCount mTimeCount;
    private EditText contactPhone_modify,email_modify,verifyCode_modify,newPassword_modify,affirm_Password;
    public static final int SENDEMAIL = 32;
    public static final int AFFIRM_VERIFY_CODE = 33;
    public static final int RETRIEVE_PASSWORD = 34;//retrievePassword
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initHandler();
    }

    @Override
    protected void onClickLeft() {
        super.onClickLeft();
        this.finish();
    }

    /**
     * 初始化视图
     */
    private void initView() {
//        newPassWord_edit = (EditText) findViewById(R.id.newPassword_edit);
//        oldPassWord_edit = (EditText) findViewById(R.id.oldPassword_edit);
//        reNewPassWord_edit = (EditText) findViewById(R.id.reNewPassword_edit);

        line_modify = (LinearLayout)findViewById(R.id.line_modify);
        verifyCode_text = (TextView) findViewById(R.id.verifyCode_text);
        mTimeCount = new TimeCount(verifyCode_text,60000,1000);
        contactPhone_modify = (EditText) findViewById(R.id.contactPhone_modify);
        email_modify = (EditText) findViewById(R.id.email_modify);
        verifyCode_modify = (EditText) findViewById(R.id.verifyCode_modify);
        newPassword_modify = (EditText) findViewById(R.id.newPassword_modify);
        affirm_Password = (EditText) findViewById(R.id.affirm_Password);

        modify_sure_btn = (Button) findViewById(R.id.modify_sure_btn);
        verifyCode_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("text","点击");
                Toast.makeText(getApplicationContext(), "点击", Toast.LENGTH_SHORT).show();
                if (obtainVerifyCode()) {
                    requestVerifyCode();
                }
            }
        });
        modify_sure_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyPassWord();
            }
        });
    }

    private boolean obtainVerifyCode(){
        if (!StringUtil.isEmpty(contactPhone_modify.getText().toString())
                &!StringUtil.isEmpty(email_modify.getText().toString())){
            mTimeCount.start();

            //不是空，就可以进行了
            return true;
        }else {
            Toast.makeText(getApplicationContext(), "请填写手机号或邮箱", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 获取验证码
     */
    private void requestVerifyCode(){
        JSONObject jb = new JSONObject();
        try {
            jb.put("contactPhone",contactPhone_modify.getText().toString());
            jb.put("email",email_modify.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        doRegist(Path.host+Path.URL_SENDEMAIL,jb,SENDEMAIL);
    }
    /**
     * 核实验证码
     */
    private void affirmVerifyCode() {
        Log.e("modify","zx");
        JSONObject jb = new JSONObject();
        try {
            jb.put("verifyCode", verifyCode_modify.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        doRegist(Path.host + Path.URL_MAILBOX_VER, jb, AFFIRM_VERIFY_CODE);
    }

    /**
     * 重设密码
     */
    private void retrievePassword() {
        JSONObject jb = new JSONObject();
        try {
            jb.put("password", affirm_Password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        doRegist(Path.host + Path.URL_RETRIEVE_PASSWORD, jb, RETRIEVE_PASSWORD);

    }

    /**
     * 判断修改密码
     */
    private void modifyPassWord() {
        if (!StringUtil.isEmpty(verifyCode_modify.getText().toString())){
            affirmVerifyCode();
        }else {
            Toast.makeText(getApplicationContext(), "请填写验证码", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_modifypassword;
    }


    private void initHandler(){
        MethodTools.handlerJson = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case SENDEMAIL:
                        Log.e("modify",msg.obj.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(msg.obj.toString());
                            if ("true".equals(jsonObject.get("success"))){
                                Toast.makeText(ModifyPassWordActivity.this,(String)jsonObject.get("message"),Toast.LENGTH_SHORT).show();
                                line_modify.setVisibility(View.VISIBLE);
                                ObjectAnimator animator = ObjectAnimator.ofFloat(line_modify, "alpha", 0f, 1f);
                                animator.setDuration(800);
                                animator.start();
                            }else {
                                Toast.makeText(ModifyPassWordActivity.this,(String)jsonObject.get("message"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case AFFIRM_VERIFY_CODE:
                        Log.e("modify",msg.obj.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(msg.obj.toString());
                            if ("true".equals(jsonObject.get("success"))){
                                if (!StringUtil.isEmpty(newPassword_modify.getText().toString())
                                        & !StringUtil.isEmpty(affirm_Password.getText().toString())) {
                                    retrievePassword();
                                } else {
                                    Toast.makeText(getApplicationContext(), "请填写新的密码", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(ModifyPassWordActivity.this,(String)jsonObject.get("message"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case RETRIEVE_PASSWORD:
                        Log.e("modify3",msg.obj.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(msg.obj.toString());
                            if ("true".equals(jsonObject.get("success"))){
                                finish();
                                Toast.makeText(ModifyPassWordActivity.this,(String)jsonObject.get("message"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
    }

//    public class TimeCount extends CountDownTimer {
//
//        public TimeCount(long millisInFuture, long countDownInterval) {
//            super(millisInFuture, countDownInterval);
//        }
//
//        @Override
//        public void onTick(long millisUntilFinished) {
//
//            verifyCode_text.setText(millisUntilFinished/1000 +"秒后重新发送");
//            verifyCode_text.setClickable(false);
//        }
//
//        @Override
//        public void onFinish() {
//            verifyCode_text.setText("获取验证码");
//            verifyCode_text.setClickable(true);
//        }
//    }
}
