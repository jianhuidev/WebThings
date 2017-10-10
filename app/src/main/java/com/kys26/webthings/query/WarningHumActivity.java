package com.kys26.webthings.query;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kys26.webthings.dialog.IntentDialog;
import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.httpnetworks.MyJsonRequestWithCookie;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.util.ToastHelper;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 窦文 on 2017/1/17.
 */
public class WarningHumActivity extends Activity {
    @InjectView(R.id.title)
    TextView mTitle;
    @InjectView(R.id.suplow)
    EditText mSuplow;
    @InjectView(R.id.low)
    EditText mLow;
    @InjectView(R.id.suphigh)
    EditText mSuphigh;
    @InjectView(R.id.high)
    EditText mHigh;
    @InjectView(R.id.active_phone)
    Spinner mActivePhone;
    @InjectView(R.id.active_low)
    Spinner mActiveLow;
    @InjectView(R.id.active_high)
    Spinner mActiveHigh;
    @InjectView(R.id.degree)
    Spinner mDegree;
    @InjectView(R.id.time)
    EditText mTime;
    @InjectView(R.id.auto_true)
    RadioButton mAutoTrue;
    @InjectView(R.id.auto_false)
    RadioButton mAutoFalse;
    @InjectView(R.id.active_rg)
    RadioGroup mActiveRg;

    /**
     * 标识
     */
    private String TAG;


    private String[] activePhone = {"响铃","震动","响铃及震动"};
    private String[] activeControl;
    private String[] degree = {"档位一","档位二","档位三"};
    /**
     * 所设置网关ID
     */
    private String gwid;
    private String type = "4102";//NH3 4098  湿度 4102

    /**
     * dialog
     */
    private Dialog dialog;

    private final int SAVE_WARNING = 21;
    private final int GETWARNING = 22;
    /**
     * 保存告警
     */
    private String save = Path.host+Path.URL_SAVE_WARNING;
    /**
     * 更新报警
     */
    private String update = Path.host+Path.URL_UPDATE_WARNING;
    /**
     * 最后用的接口
     */
    private String url;
    /**
     * 猜猜看这是啥
     */
    private Map<String ,Object> map = new Hashtable<String, Object>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning_hum);
        ButterKnife.inject(this);
        TAG = getClass().getName();
        Intent intent = getIntent();
        gwid = intent.getStringExtra("gwid");
        initHandler();
        initView();
    }

    /**
     * 初始化Handler
     */
    private void initHandler() {
        MethodTools.handlerJson = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case GETWARNING:
                        analysis(msg.obj.toString());
                        break;
                    case Code.FAILURE:
                        ToastHelper.show(getApplication(),"请求失败，请重新尝试");
                        Log.e(TAG,"连接错误："+msg.obj.toString());
                        break;
                    case SAVE_WARNING:
                        analysisBack(msg.obj.toString());
                        break;
                }
                dialog.dismiss();
            }
        };
    }

    /**
     * @function 判断是否保存成功
     * @param s
     */
    private void analysisBack(String s) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(s);
            if ("Success".equals(jsonObject.get("Status"))){
                Toast.makeText(getApplicationContext(),"设置成功",Toast.LENGTH_LONG).show();
                finish();
            }else {
                Toast.makeText(getApplicationContext(),"设置失败，该告警已存在",Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @function 解析数据并处理
     * @param s
     */
    private void analysis(String s) {
        try {
            JSONObject jb = new JSONArray(s).getJSONObject(0);
            if ("Success".equals(jb.get("status"))) {
                map.put("controlTime", jb.get("controlTime"));//控制时长
                map.put("lNdname", jb.get("lNdname"));//低警告动作
                map.put("uHighWarning", jb.get("uHighWarning"));//超高警告值
                map.put("uLowWarning", jb.get("uLowWarning"));//超低警告值
                map.put("openRange", jb.get("openRange"));//开启幅度
                map.put("hNdname", jb.get("hNdname"));//高警告动作
                map.put("highWarning", jb.get("highWarning"));//高警告值
                map.put("phoneAction", jb.get("phoneAction"));//手机动作
                map.put("lowWarning", jb.get("lowWarning"));//低警告值
                map.put("autoControl", jb.get("autoControl"));//自动控制

                url = update;
                contView();
            }else {
                url = save;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialog.dismiss();
    }

    /**
     * 继续更改界面
     */
    private void contView() {
        mSuplow.setText(map.get("uLowWarning").toString());
        mLow.setText(map.get("lowWarning").toString());
        mSuphigh.setText(map.get("uHighWarning").toString());
        mHigh.setText(map.get("highWarning").toString());
        mActivePhone.setSelection((Integer.valueOf(map.get("phoneAction").toString())-1),true);
        for (int i = 0;i<MethodTools.controlList.size();i++){
            if(MethodTools.controlList.get(i).get("nodename").toString().equals(map.get("lNdname").toString())){
                mActiveLow.setSelection(i,true);
            }
            if(MethodTools.controlList.get(i).get("nodename").toString().equals(map.get("hNdname").toString())){
                mActiveHigh.setSelection(i,true);

            }
        }

//        mDegree.setSelection((Integer.valueOf(map.get("openRange").toString())-1),true);
        mTime.setText(map.get("controlTime").toString());
        if (Integer.valueOf(map.get("autoControl").toString())==1){
            mAutoTrue.setChecked(true);
        }else{
            mAutoFalse.setChecked(false);
        }
    }

    /**
     * 初始化界面
     */
    private void initView() {
        dialog = IntentDialog.createDialog(WarningHumActivity.this, "正在获取数据......");
        dialog.setCanceledOnTouchOutside(false);
        JSONObject jb = new JSONObject();
        try {
            jb.put("gwid",gwid);
            jb.put("ndname",type);
            MyJsonRequestWithCookie.newhttpPost(Path.host + Path.URL_GET_ONEWARNING, jb, MethodTools.sPreFerCookie.getString("cookie", "null"),GETWARNING);
            dialog.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        activeControl = new String[MethodTools.controlList.size()];
        Log.e(TAG,"+++++"+MethodTools.controlList.size());
        for (int i=0;i<MethodTools.controlList.size();i++){
            activeControl[i] = MethodTools.controlList.get(i).get("devicetypename").toString();
            Log.e(TAG,"+++++"+activeControl[i]);
        }
        ArrayAdapter<String> phoneAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_white,R.id.spinner_itemText,activePhone);
        mActivePhone.setAdapter(phoneAdapter);
        ArrayAdapter<String> controAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_white,R.id.spinner_itemText,activeControl);
        mActiveHigh.setAdapter(controAdapter);
        mActiveLow.setAdapter(controAdapter);
        ArrayAdapter<String> degreeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_white,R.id.spinner_itemText,degree);
        mDegree.setAdapter(degreeAdapter);
//        VolleyStringRequest.stringRequestWithCookie(WarningTempActivity.this,Path.host+Path.URL_GET_DEVICE,MethodTools.sPreFerCookie.getString("cookie", "null"));
//        dialog.show();
    }

    @OnClick({R.id.left_iv, R.id.sure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_iv:
                finish();
                break;
            case R.id.sure:
                JSONObject jb = new JSONObject();
                try {
                    jb.put("gwid",gwid);
                    jb.put("ndname",type);
                    jb.put("lowWarning",mLow.getText().toString());
                    jb.put("uLowWarning",mSuplow.getText().toString());
                    jb.put("highWarning",mHigh.getText().toString());
                    jb.put("uHighWarning",mSuphigh.getText().toString());
                    if ("响铃".equals(mActivePhone.getSelectedItem().toString())){
                        jb.put("openRange","1");
                    }else if ("震动".equals(mActivePhone.getSelectedItem().toString())){
                        jb.put("openRange","2");
                    }else if ("响铃及震动".equals(mActivePhone.getSelectedItem().toString())){
                        jb.put("openRange","3");
                    }
                    for (int i = 0;i<MethodTools.controlList.size();i++){
                        if(MethodTools.controlList.get(i).get("devicetypename").toString().equals(mActiveLow.getSelectedItem().toString())){
                            jb.put("lNdname",MethodTools.controlList.get(i).get("nodename"));
                        }
                        if(MethodTools.controlList.get(i).get("devicetypename").toString().equals(mActiveHigh.getSelectedItem().toString())){
                            jb.put("hNdname",MethodTools.controlList.get(i).get("nodename"));
                        }
                    }
//                    if ("换气风机".equals(mActiveLow.getSelectedItem().toString())){
//                        jb.put("lNdname",MethodTools.controlList.get(0).get("nodename"));
//                    }else if ("对流风机".equals(mActiveLow.getSelectedItem().toString())){
//                        jb.put("lNdname",MethodTools.controlList.get(1).get("nodename"));
//                    }
//                    if ("换气风机".equals(mActiveHigh.getSelectedItem().toString())){
//                        jb.put("hNdname",MethodTools.controlList.get(0).get("nodename"));
//                    }else if ("对流风机".equals(mActiveHigh.getSelectedItem().toString())){
//                        jb.put("hNdname",MethodTools.controlList.get(1).get("nodename"));
//                    }
                    if ("档位一".equals(mDegree.getSelectedItem().toString())){
                        jb.put("phoneAction","1");
                    }else if ("档位二".equals(mDegree.getSelectedItem().toString())){
                        jb.put("phoneAction","2");
                    }else if ("档位三".equals(mDegree.getSelectedItem().toString())){
                        jb.put("phoneAction","3");
                    }
                    jb.put("controlTime",mTime.getText().toString());
                    RadioButton radioBtn = (RadioButton) findViewById(mActiveRg.getCheckedRadioButtonId());
                    if ("是".equals(radioBtn.getText().toString())){
                        jb.put("autoControl","1");
                    }else if ("否".equals(radioBtn.getText().toString())){
                        jb.put("autoControl","0");
                    }
                    Log.e(TAG,jb.toString());
                    MyJsonRequestWithCookie.newhttpPost(url, jb, MethodTools.sPreFerCookie.getString("cookie", "null"),SAVE_WARNING);
                    dialog.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
