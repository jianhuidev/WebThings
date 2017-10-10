package com.kys26.webthings.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kys26.webthings.util.SharedPreferencesUtils;
import com.zhangyx.MyGestureLock.R;


/**
 * @author 李赛鹏
 * @class Created by kys_9 on 2017/4/26.
 */

public class SelectActivity extends Activity implements View.OnClickListener{
    private Button login_btn,register_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        initFirst();
//        if (Build.VERSION.SDK_INT >= 19) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
        initView();
    }

    /**
     * 初始化
     */
    private void initFirst() {
        if (SharedPreferencesUtils.readObject(getApplicationContext(),"farmList")!=null){

        }
    }

//    @Override
//    protected int getContentView() {
//        return R.layout.activity_select;
//    }
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        }
//    }
    /**
     * @function 初始化view
     */
    private void initView() {
        login_btn=(Button)findViewById(R.id.login_btn);
        login_btn.setOnClickListener(this);
        register_btn=(Button)findViewById(R.id.register_btn);
        register_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_btn:
                startActivity(new Intent(SelectActivity.this,LoginActivity.class));
                break;
            case R.id.register_btn:
                startActivity(new Intent(SelectActivity.this,RegisterActivity.class));

                break;
        }
    }
}
