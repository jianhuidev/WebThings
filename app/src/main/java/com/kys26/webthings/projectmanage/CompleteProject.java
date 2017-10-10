package com.kys26.webthings.projectmanage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kys26.webthings.adapter.ComProjectListAdapter;
import com.kys26.webthings.dialog.IntentDialog;
import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.httpnetworks.MyJsonRequestWithCookie;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.util.MyToast;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.kys26.webthings.dialog.IntentDialog.dialog;

/**
 * @author 李赛鹏
 * @class 完成项目
 * Created by Administrator on 2016/12/19.
 */

public class CompleteProject extends Activity {
    @InjectView(R.id.completeproject_left_iv)
    ImageView mCompleteprojectLeftIv;
    @InjectView(R.id.completeproject_list)
    ListView mCompleteprojectList;
    @InjectView(R.id.confplan_NextStep)
    TextView mConfplanNextStep;
    private static String TAG = "CompleteProject";
    private List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
    private static int GET_NODE = 0, NEXT_STEP = 1,UPDATE_STEP=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_completeproject);
        ButterKnife.inject(this);

    }

    /***
     * @fuction 初始化视图
     */
    private void initView() {
        ComProjectListAdapter adapter = new ComProjectListAdapter(this, list);
        mCompleteprojectList.setAdapter(adapter);
//        mCompleteprojectList.getItemAtPosition();
        JSONObject object = new JSONObject();
        try {
            object.put("step", "5");
            object.put("projectid", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getWebData(Path.URL_GET_NODE, object, GET_NODE);
    }

    public void initList() {
        ComProjectListAdapter adapter = new ComProjectListAdapter(this, list);
        mCompleteprojectList.setAdapter(adapter);
    }

    /***
     * @param URL     获取网页数据的URL
     * @param send_jb 需要发送的json数据
     * @fuction 获取网页数据
     */
    public void getWebData(String URL, JSONObject send_jb, final int Type) {
        MethodTools.sPreFerCookie = getSharedPreferences("Cookies",
                Context.MODE_PRIVATE);
        MyJsonRequestWithCookie.newhttpPost(Path.host + URL, send_jb, MethodTools.sPreFerCookie.getString("cookie", "null"), 7);
        MethodTools.handlerJson = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == Code.LUCKYSEVEN) {
                    //获取数据后进行处理
                    if (Type == GET_NODE)
                        analysis(msg.obj.toString(), GET_NODE);
                    //  else if(Type==)
                    Log.e(TAG, msg.obj.toString());
                    IntentDialog.dialog.cancel();
                    initList();
                } else if (msg.what == Code.FAILURE) {
                    //消除等待进度弹出框
                    dialog.cancel();
                    MyToast.makeImgAndTextToast(CompleteProject.this, getResources().getDrawable(R.drawable.tips_sry),
                            "请求失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
                }
            }
        };
    }

    /***
     * @param data//获取后台的农场列表信息
     * @return null
     * @author Admin-李赛鹏 create at 2016-9-17 下午8:56:18
     * @function:对获取的数据进行分析
     */
    private void analysis(String data, int Type) {
        //打印获取到的数据，用于开发人员进行数据分析
        Log.i(TAG, data.toString());
        //将获取的数据保存在静态存储区里
        MethodTools.framData = data;
        //声明一个map将所有fram数据装载起来
        try {
            Log.i(TAG, data.toString());
            //数据是一个json数组，开始进行筛选得到所有用户
            JSONArray jsonArray = new JSONArray(data.toString());
            //利用for对数据进行剥离
            Log.i(TAG, Integer.toString(jsonArray.length()));
            for (int i = 0; i <= jsonArray.length(); i++) {
                //实例化list的对象
                //json数组中第一个数据是农场信息列表
                HashMap<String, Object> map = new HashMap<String, Object>();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //网关名称
                map.put("gwname", jsonObject.get("gwname"));
                //兔场名称And布置位置
                map.put("farm_name", jsonObject.get("farm_name"));
                //设备类型
                map.put("type", jsonObject.get("type"));
                //金额
                map.put("price",jsonObject.get("price"));
                JSONArray array = jsonObject.getJSONArray("listdata");
                for (int a = 0; a < array.length(); a++) {
                    JSONObject object = array.getJSONObject(a);
                    //设备编号
                    map.put("idhex", object.get("id2"));
                    //网关编号
                    map.put("gwid", object.get("gwid"));
                    //备注信息
                    map.put("describe", object.get("describe"));
                    //购置设备名称
                    map.put("name",object.get("name"));
                    list.add(i, map);
                 //   map.put("farm_name",object.get("farm_name"));
                }
            }
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        SwipeListView lv = (SwipeListView) findViewById(R.id.permission_list);
//        PermissionActivity.SwipeAdapter adapter = new PermissionActivity.SwipeAdapter(this);
//        lv.setAdapter(adapter);
    }

    @OnClick({R.id.completeproject_left_iv, R.id.confplan_NextStep})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.completeproject_left_iv:
                this.finish();
                break;
            case R.id.confplan_NextStep:
                //this.finish();
                break;
        }
    }
}
