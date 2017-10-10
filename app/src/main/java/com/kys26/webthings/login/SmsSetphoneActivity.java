//package com.kys26.webthings.login;
//
//import android.os.Bundle;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//
//import com.ab.activity.AbActivity;
//import com.kys26.webthings.dialog.IntentDialog;
//import com.kys26.webthings.main.BaseActivity;
//import com.kys26.webthings.util.MyToast;
//import com.lidroid.xutils.ViewUtils;
//import com.lidroid.xutils.view.annotation.ViewInject;
//import com.zhangyx.MyGestureLock.R;
//
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//
///**
// * @author:Created by 徐建强 .
// * @function:手机账号验证之获取验证码
// * @time:2016/3/8.
// */
//public class SmsSetphoneActivity extends AbActivity{
//
//    //手机号码控件
//    private EditText phoneNumber;
//    //按钮控件
//    @ViewInject(R.id.next)
//    private Button nextBtn;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setAbContentView(R.layout.activity_sms);
//
//        // 初始化注解
//        ViewUtils.inject(this);
//        //初始化UI组件
//        initUI();
//        //初始化监听
//        initListener();
//    }
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
//        //手机号码
//        phoneNumber=(EditText)findViewById(R.id.phoneNumber);
//    }
//    /**
//     * @author:Created by 徐建强 .
//     * @function:初始化监听
//     * @time:2016/3/8.
//     */
//    private void initListener(){
//
//        nextBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //判断电话长度格式
//                Pattern p = Pattern
//                        .compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
//                Matcher m = p.matcher(phoneNumber.getText().toString());
//                if (!m.matches()) {
//                    MyToast.makeTextToast(SmsSetphoneActivity.this,
//                            "错误的电话号码", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
//                }else {
//                    //设置进度条显示等待
//                    IntentDialog.createDialog(SmsSetphoneActivity.this, "正在获取数据......").show();
//                    //执行请求
////                    getSMS();
//                }
//            }
//        });
//    }
//    /**
//     * @author:Created by 徐建强 .
//     * @function:验证码获取请求
//     * @time:2016/3/8.
//     */
////    private void getSMS(){
////        //随机生成数字验证码
////        final int mobile_code = (int)((Math.random()*9+1)*100000);
////        Log.i("mobile_code",Integer.toString(mobile_code));
////        //短信内容，这个短信内容互亿无线公司做了规格，只需缴纳1500元成为会员，你就能改变内容了，官方网址：ihuyi.com。我答应他们，广告打到我的源码中，他们赠我30条免费测试短信。
////        String content = new String("您的验证码是：" + mobile_code + "。请不要把验证码泄露给其他人。");
////        // 设置HTTP POST请求参数必须用NameValuePair对象
////        final   List<NameValuePair> params = new ArrayList<NameValuePair>();
////        params.add(new BasicNameValuePair("account", "cf_xu123"));
////        //采用MD5加密方式
////        params.add(new BasicNameValuePair("password", EncryptString.MD5Encode("xujianqiang")));
////        params.add(new BasicNameValuePair("mobile",phoneNumber.getText().toString().toString()));
////        params.add(new BasicNameValuePair("content", content));
////
////        //开始网络请求
////        Thread thread=new Thread(new Runnable() {
////            @Override
////            public void run() {
////                HttpPost httpPost = new HttpPost(Path.urlSMS);
////                HttpResponse httpResponse = null;
////                try {
////                    // 设置httpPost请求参数
////                    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
////                    //添加cookies,互亿无线技术文档中的开发要求
////                    httpPost.setHeader("ContentType","application/x-www-form-urlencoded;charset=UTF-8");
////                    httpResponse = new DefaultHttpClient().execute(httpPost);
////                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
////                        // 第三步，使用getEntity方法活得返回结果
////                        String result = EntityUtils.toString(httpResponse.getEntity());
////                        Log.i("mobile_code2",result);
////                        //开始跳转下一步验证
////                        Intent intent=new Intent(SmsSetphoneActivity.this,SmsSetcodeActivity.class);
////                        intent.putExtra("mobileCode",Integer.toString(mobile_code));
////                        startActivity(intent);
////                        SmsSetphoneActivity.this.finish();
////                        //等待进度弹出框消失
////                        IntentDialog.dialog.cancel();
////                    }else{
////                        MyToast.makeTextToast(SmsSetphoneActivity.this,
////                                "连接超时", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
////                    }
////                } catch (ClientProtocolException e) {
////                    e.printStackTrace();
////                    MyToast.makeTextToast(SmsSetphoneActivity.this,
////                            "获取失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
////
////                } catch (IOException e) {
////                    e.printStackTrace();
////                    MyToast.makeTextToast(SmsSetphoneActivity.this,
////                            "获取失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
////                }
////            }
////        });
////        thread.start();
////    }
//}
