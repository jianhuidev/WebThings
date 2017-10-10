//package com.kys26.webthings.login;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import com.ab.activity.AbActivity;
//import com.kys26.webthings.main.BaseActivity;
//import com.kys26.webthings.util.MyToast;
//import com.kys26.webthings.util.Sessionutil;
//import com.lidroid.xutils.ViewUtils;
//import com.lidroid.xutils.view.annotation.ViewInject;
//import com.zhangyx.MyGestureLock.R;
//
///**
// * @author:Created by 徐建强 .
// * @function:短信验证之填写验证码
// * @time:2016/3/8.
// */
//public class SmsSetcodeActivity extends AbActivity {
//
//    //手机号码控件
//    private EditText mobilecode;
//    //按钮控件
//    @ViewInject(R.id.sms_next)
//    private Button nextBtn;
//
//    @ViewInject(R.id.noSms)
//    private Button noSmsBtn;
//
//    private String SMSCode;
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setAbContentView(R.layout.activity_sms_setcode);
//
//        // 初始化注解
//        ViewUtils.inject(this);
//        //初始化UI组件
//        initUI();
//        //初始化监听
//        initListener();
//        //获取上层传递的验证码
//        Intent intent=getIntent();
//        SMSCode=intent.getStringExtra("mobileCode");
//    }
//
//    /**
//     * @author:Created by 徐建强 .
//     * @function:初始化UI组件
//     * @time:2016/3/8.
//     */
//    private void initUI(){
//        //标题引导栏初始化设置
//        this.setTitleText("短信验证");
//        this.setTitleTextSize(20);
//        this.setTitleTextMargin(BaseActivity.marginToLeftTitleNormal, 0, 0, 0);
//        this.setLogo(R.drawable.button_selector_back);
//        this.setTitleLayoutBackground(R.drawable.top_bg);
//        this.setLogoLine(R.drawable.line);
//
//        mobilecode=(EditText)findViewById(R.id.mobileCode);
//    }
//    /**
//     * @author:Created by 徐建强 .
//     * @function:初始化监听
//     * @time:2016/3/8.
//     */
//    private void initListener(){
//      nextBtn.setOnClickListener(new View.OnClickListener() {
//          @Override
//          public void onClick(View view) {
//              checkCode();
//          }
//      });
//
//        noSmsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(SmsSetcodeActivity.this, SmsSetphoneActivity.class));
//                SmsSetcodeActivity.this.finish();
//            }
//        });
//    }
//    /**
//     * @author:Created by 徐建强 .
//     * @function:验证码对接
//     * @time:2016/3/8.
//     */
//    private void checkCode(){
//
//        //验证对接
//        if(SMSCode.equals(mobilecode.getText().toString())){
//            startActivity(new Intent(SmsSetcodeActivity.this, RetrievePassword.class));
//            SmsSetcodeActivity.this.finish();
//        }else{
//            MyToast.makeTextToast(SmsSetcodeActivity.this,
//                    "验证码不正确", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
//        }
//    }
//}
