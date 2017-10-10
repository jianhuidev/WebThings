package com.kys26.webthings.person;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kys26.webthings.dialog.CustomDialog;
import com.kys26.webthings.main.BaseActivity;
import com.zhangyx.MyGestureLock.R;

/**
 * @author 李赛鹏
 * @class Created by kys_9 on 2017/6/6.
 */

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private TextView edit_phone, edit_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setTitle("设置界面");
        setEnableRefresh(false, null);
    }

    @Override
    protected void onClickLeft() {
        super.onClickLeft();
        this.finish();
    }

    /**
     * 初始化view
     */
    private void initView() {
        edit_email = (TextView) findViewById(R.id.edit_email);
        edit_email.setOnClickListener(this);
        edit_phone = (TextView) findViewById(R.id.edit_phone);
        edit_phone.setOnClickListener(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_setting;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_email:
                showDialog("修改邮箱", "");
                break;
            case R.id.edit_phone:
                showDialog("修改手机号", "");
                break;
        }
    }

    public void showDialog(String Title, String editString) {
        CustomDialog dialog = new CustomDialog();
        dialog.setTitle(Title);
        dialog.setTitleSize(20);
        dialog.setTitleColor(R.color.blue);
        EditText edit = new EditText(this);
        edit.setWidth(-1);
        edit.setText(editString);
        dialog.setView(edit);
        dialog.setOnDialogListener(new CustomDialog.OnDialogListener() {
            @Override
            public void sureListener() {

            }

            @Override
            public void cancelListener() {

            }
        });
        dialog.show(getFragmentManager(), "edit");
    }
}
