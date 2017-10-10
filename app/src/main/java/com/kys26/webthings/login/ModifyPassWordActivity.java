package com.kys26.webthings.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kys26.webthings.main.BaseActivity;
import com.zhangyx.MyGestureLock.R;

/**
 * Created by Lee on 2017/8/31.
 */

public class ModifyPassWordActivity extends BaseActivity {
    private EditText newPassWord_edit, oldPassWord_edit, reNewPassWord_edit;
    private Button modify_sure_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
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
        newPassWord_edit = (EditText) findViewById(R.id.newPassword_edit);
        oldPassWord_edit = (EditText) findViewById(R.id.oldPassword_edit);
        reNewPassWord_edit = (EditText) findViewById(R.id.reNewPassword_edit);
        modify_sure_btn = (Button) findViewById(R.id.modify_sure_btn);
        modify_sure_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (judgeModifyPassWord()) {
                }
            }
        });
    }

    /**
     * 判断修改密码
     */
    private boolean judgeModifyPassWord() {
        if (newPassWord_edit.getText().toString().trim().length() <= 5) {
            Toast.makeText(getApplicationContext(), "新密码长度太短", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (oldPassWord_edit.getText().toString().trim().length() <= 0) {
            Toast.makeText(getApplicationContext(), "旧密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!reNewPassWord_edit.getText().toString().equals(newPassWord_edit.getText().toString())) {
            Toast.makeText(getApplicationContext(), "密码不一致,请重新输入", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_modifypassword;
    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.oldPassword_edit:
//                break;
//        }
//    }
}
