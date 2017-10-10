package com.kys26.webthings.command;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.kys26.webthings.adapter.WindlistAdapter;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.interfac.ChangeList;
import com.kys26.webthings.main.BaseActivity;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.model.NodeControlData;
import com.kys26.webthings.model.WarningData;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.kys26.webthings.method.MethodTools.mCoolingDataList;

/**
 * @author 李赛鹏
 * @class Created by kys_9 on 2017/5/6.
 */

public class WarningActionActivity extends BaseActivity implements View.OnClickListener, ChangeList {
    FrameLayout left_btn, right_btn;
    Button sure;
    ListView wind_list;
    List<NodeControlData> mNodeControlDataList;
    int parentPosition;
    ArrayList<String> mList = new ArrayList<>();
    WarningData warningData;
    JSONObject jsonObject;
    Intent intent;
    private int typePosition;
    Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setTitle("告警动作");
        setRightBtnVisible(false);
        addRightWidget(1);
        setEnableRefresh(false, null);
        handler = new Handler(){
            public void handleMessage(final Message msg) {
                final ListView listView = (ListView) msg.obj;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < listView.getChildCount() ; i++) {
                            listView.getChildAt(i).findViewById(R.id.switch_btn).setClickable(false);
                        }}},500);
            }
        };
        initHandler();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 添加handler
     */
    private void initHandler() {
        MethodTools.handlerJson = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case UPDATE_WARNING://更改告警信息
                        try {
                            JSONObject jb = new JSONObject(msg.obj.toString());
                            if (jb.getString("Status").equals("Success")) {
                                WarningActionActivity.this.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case GET_WARNING://获取告警设备
                        try {
                            JSONObject jsonObject = new JSONObject(msg.obj.toString());
                            warningData = WarningData.analysis(new JSONObject(msg.obj.toString()));
                            WindlistAdapter mAdapter = new WindlistAdapter(WarningActionActivity.this, mNodeControlDataList, WarningActionActivity.this, warningData.getWarningDevice());
                            wind_list.setAdapter(mAdapter);
                            //如果自动控制打开,那么不允许打开自动控制
                            if (intent.getBooleanExtra("IsSwitchOpen", false)) {
                                sure.setClickable(false);
                                sure.setEnabled(false);
                                Message message = new Message();
                                message.obj = wind_list;
                                handler.sendMessage(message);
                                Toast.makeText(getApplicationContext(), "请先关闭自动控制", Toast.LENGTH_SHORT).show();
                            }
                            dismissProgress();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_warnaction;
    }

    @Override
    protected void onClickLeft() {
        super.onClickLeft();
        this.finish();
    }

    /**
     * @function 初始化view
     */
    private void initView() {
        left_btn = (FrameLayout) findViewById(R.id.left_btn);
        //right_btn = (FrameLayout) findViewById(R.id.right_btn);
        left_btn.setOnClickListener(this);
        sure = (Button) findViewById(R.id.button_sure);
        sure.setOnClickListener(this);
        right_btn = (FrameLayout) findViewById(R.id.right_btn);
        right_btn.setOnClickListener(this);
        wind_list = (ListView) findViewById(R.id.wind_list);
        intent = getIntent();
        parentPosition = intent.getIntExtra("parentPosition", parentPosition);
        typePosition = intent.getIntExtra("typePosition", -1);
        switch (typePosition) {
            case -1:
                mNodeControlDataList = MethodTools.farmDataList.get(parentPosition).getNodeSideWind();
                mNodeControlDataList.clear();
                break;
            case 1:
                mNodeControlDataList = MethodTools.farmDataList.get(parentPosition).getNodeSideWind();
                break;
            case 2:
                mNodeControlDataList = mCoolingDataList;
                break;
        }
        try {
            jsonObject = new JSONObject(intent.getStringExtra("jsonObject"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        doRegist(Path.host + Path.URL_GET_WARNING, jsonObject, GET_WARNING);
        mList = intent.getStringArrayListExtra("list");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                this.finish();
                break;
            case R.id.right_btn:
                break;
            case R.id.button_sure:
                Intent intent = new Intent();
                intent.putStringArrayListExtra("list", mList);
                setResult(0, intent);
                JSONObject object = new JSONObject();
                try {
                    object.put("gwid", warningData.getGwid());
                    object.put("ndname", warningData.getNdname());
                    object.put("autoControl", warningData.getautoControl());
                    object.put("warningValue", warningData.getWarningValue());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray array = new JSONArray();
                List<String> windnodeList = new ArrayList<>();
                for (int i = 0; i < wind_list.getChildCount(); i++) {
                    CheckBox s = (CheckBox) wind_list.getChildAt(i).findViewById(R.id.switch_btn);
                    if (s.isChecked()) {
                        array.put(mNodeControlDataList.get(i).getNodeid());
                        windnodeList.add(mNodeControlDataList.get(i).getNodeid());
                    }
                }
                warningData.setWarningDevice(windnodeList);
                try {
                    object.put("warningDevice", array);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                doRegist(Path.host + Path.URL_UPDATE_WARNING, object, UPDATE_WARNING);
        }
    }

    @Override
    public void setList(int position) {
        mList.add(mNodeControlDataList.get(position).getNodeid());
    }

    @Override
    public void resetList(int position) {
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).equals(mNodeControlDataList.get(position).getNodeid())) {
                mList.remove(i);
            }
        }
    }
}
