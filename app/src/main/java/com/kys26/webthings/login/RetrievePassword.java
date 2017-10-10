//package com.kys26.webthings.login;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.ab.activity.AbActivity;
//import com.kys26.webthings.bean.RetrievePasswordBean;
//import com.kys26.webthings.dialog.IntentDialog;
//import com.kys26.webthings.httpconstant.Code;
//import com.kys26.webthings.httpconstant.Path;
//import com.kys26.webthings.httpnetworks.VolleyJsonRequest;
//import com.kys26.webthings.main.BaseActivity;
//import com.kys26.webthings.method.MethodTools;
//import com.kys26.webthings.util.MyToast;
//import com.kys26.webthings.util.StringUtil;
//import com.lidroid.xutils.ViewUtils;
//import com.lidroid.xutils.view.annotation.ViewInject;
//import com.zhangyx.MyGestureLock.R;
//
//import org.json.JSONObject;
//
///**
// * Created by kys26 徐建强 on 2015/7/20.
// * function:RetrievePassword
// */
//public class RetrievePassword extends AbActivity {
//    /**密码输入控件*/
//    @ViewInject(R.id.ret_userPwd)
//    private TextView textPassword;
//    /**新密码输入控件*/
//    @ViewInject(R.id.ret_userPwdNew)
//    private TextView textPasswordNew;
//    /**密码确认输入*/
//    @ViewInject(R.id.ret_userPwdAg)
//    private TextView textPasswordAg;
//    /**确认修改密码按钮控件*/
//    @ViewInject(R.id.ret_ensure)
//    private Button btnEnsure;
//    /**输出标识*/
//    private String TAG="com.kys26.webthings.login.RetrievePassword";
//    /**用户名输入框*/
//    private EditText userName;
//    /**确认修改密码按钮控件*/
//    @ViewInject(R.id.re_loginImage)
//    private ImageView img;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        // TODO Auto-generated method stub
//        super.onCreate(savedInstanceState);
//        setAbContentView(R.layout.activity_retrieepassword);
//        // 初始化IOC注解
//        ViewUtils.inject(this);
//        //初始化UI组件
//        initView();
//    }
//    /**
//     * @function:加载UI
//     * @author:Created by 徐建强 on 2015/11/23.
//     * @return:null
//     * @param: null
//     */
//    private void initView(){
//
//        //设置标题字体
//        this.setTitleText("密码修改");
//        this.setTitleTextSize(20);
//        this.setTitleTextMargin(BaseActivity.marginToLeftTitleNormal, 0, 0, 0);
//        this.setLogo(R.drawable.button_selector_back);
//        this.setTitleLayoutBackground(R.drawable.top_bg);
//        this.setLogoLine(R.drawable.line);
//
//
//        userName=(EditText)findViewById(R.id.changePas_username);
//       /* ret_topname_text.setTextColor(Color.BLACK);
//        ret_topname_text.setTextSize(26.0f);
//        ret_topname_text.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
//        // Typeface.BOLD_ITALIC
//        TextPaint tp = ret_topname_text.getPaint();
//        tp.setFakeBoldText(true);*/
//
//        //确认控件设置监听
//        btnEnsure.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //确认修改信息
//                examineMessage();
//            }
//        });
//    }
//    /**
//     * @function:执行信息填写确认
//     * @author:Created by 徐建强 on 2015/11/23.
//     * @return:null
//     * @param: null
//     */
//    private void examineMessage(){
//     //对填写信息做确认，防止漏填信息
//        if(TextUtils.isEmpty(textPassword.getText().toString())){
//            MyToast.makeTextToast(RetrievePassword.this,
//                    "漏填旧密码", MyToast.Long_TIME, Gravity.BOTTOM,120).show();
//        }else if(TextUtils.isEmpty(textPasswordNew.getText().toString())){
//            MyToast.makeTextToast(RetrievePassword.this,
//                    "漏填新密码", MyToast.Long_TIME, Gravity.BOTTOM,120).show();
//        }else if(TextUtils.isEmpty(textPasswordAg.getText().toString())){
//            MyToast.makeTextToast(RetrievePassword.this,
//                    "请再次输入新密码", MyToast.Long_TIME, Gravity.BOTTOM,120).show();
//        }else if(StringUtil.isEquality(textPasswordNew.getText().toString(),textPasswordAg.getText().toString())==false){
//            MyToast.makeTextToast(RetrievePassword.this,
//                    "两次输入的密码不一致", MyToast.Long_TIME, Gravity.BOTTOM,120).show();
//        }else if(StringUtil.isEmpty(userName.getText().toString())){
//            MyToast.makeTextToast(RetrievePassword.this,
//                    "漏填账号", MyToast.Long_TIME, Gravity.BOTTOM,120).show();
//        } else {
//            //提交修改信息
//            doRetrieve();
//        }
//    }
//    /**
//     * @function:执行提交信息任务
//     * @author:Created by 徐建强 on 2015/11/23.
//     * @return:null
//     * @param: null
//     */
//    private void doRetrieve(){
//        //封装json数据
//        RetrievePasswordBean retrievePasswordBean  =new RetrievePasswordBean();
//        retrievePasswordBean.setOldPsw(textPassword.getText().toString());
//        retrievePasswordBean.setNewPsw(textPasswordNew.getText().toString());
//        retrievePasswordBean.setUserName(userName.getText().toString());
//        //等待进度条显示
//        IntentDialog.createDialog(RetrievePassword.this,"正在提交...").show();
//        //调用静态SharedPreferences获取cookie值
//        MethodTools.sPreFerCookie = getSharedPreferences("Cookies",
//                Context.MODE_PRIVATE);
//        //调用volley框架进行请求
//        VolleyJsonRequest.JsonRequestWithCookie(RetrievePassword.this,
//                Path.host + Path.retrievepassword,
//                MethodTools.gson.toJson(retrievePasswordBean),
//                MethodTools.sPreFerCookie .getString("cookie", "null"));
//        //等待服务端响应
//        //重写Handler中的message方法，获取数据后进行异步加载
//           MethodTools.HnadlerVOlleyJson= new Handler() {
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (msg.what== Code.SUCCESS) {
//                    //获取数据后进行处理
//                    analysis(msg.obj.toString());
//                }else if(msg.what==Code.FAILURE){
//                    //弹出进度条消失
//                    IntentDialog.dialog.cancel();
//                    MyToast.makeImgAndTextToast(RetrievePassword.this, getResources().getDrawable(R.drawable.tips_sry),
//                            "请求失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
//                }
//            }
//        };
//    }
//    /**
//     * @function:得到服务端数据回返进行处理
//     * @author:Created by 徐建强 on 2015/11/23.
//     * @return:null
//     * @param: str
//     */
//    /**服务端json数据返回暂存*/
//    private String jsonData;
//    private void analysis(String str){
//        //打印输出返回数据
//        Log.i(TAG,str);
//        //服务端返回数据是一个json数据
//        try{
//            JSONObject jsonObject=new JSONObject(str);
//            jsonData=jsonObject.getString("Status");
//            if(jsonData.equals("OldPasswordError")){
//                MyToast.makeImgAndTextToast(RetrievePassword.this, getResources().getDrawable(R.drawable.tips_sry),
//                        "旧密码验证错误", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
//            }else if(jsonData.equals("Success")){
//                MyToast.makeImgAndTextToast(RetrievePassword.this, getResources().getDrawable(R.drawable.tips_smile),
//                        "密码更改成功", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
//                RetrievePassword.this.finish();
//            }
//            else if(jsonData.equals("Fail")){
//                MyToast.makeImgAndTextToast(RetrievePassword.this, getResources().getDrawable(R.drawable.tips_sry),
//                        "密码更改失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
//            }
//            //等待进度条消失
//            IntentDialog.dialog.cancel();
//        }catch (Exception e){
//           Log.i(TAG,"json数据解析异常");
//        }
//    }
//}
