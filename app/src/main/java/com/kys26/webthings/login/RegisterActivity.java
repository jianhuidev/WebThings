package com.kys26.webthings.login;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kys26.webthings.util.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.zhangyx.MyGestureLock.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class RegisterActivity extends Activity {
    @InjectView(R.id.nickname_edit)
    EditText mNicknameEdit;
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
                if (isPerfect()) {

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
                & !StringUtil.isEmpty(mEmailEdit.getText().toString())
                & !StringUtil.isEmpty(mPasswordEdit.getText().toString())
                & !StringUtil.isEmpty(mNicknameEdit.getText().toString())) {
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
}
