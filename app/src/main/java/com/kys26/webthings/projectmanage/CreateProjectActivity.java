package com.kys26.webthings.projectmanage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kys26.webthings.adapter.ClientListAdapter;
import com.kys26.webthings.dialog.IntentDialog;
import com.kys26.webthings.global.Constant;
import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.httpnetworks.MyJsonRequestWithCookie;
import com.kys26.webthings.httpnetworks.VolleyJsonArrayRequest;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.util.MyToast;
import com.kys26.webthings.util.StringUtil;
import com.kys26.webthings.util.ToastHelper;
import com.kys26.webthings.view.CustomProjectList;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.kys26.webthings.dialog.IntentDialog.dialog;
import static com.kys26.webthings.method.MethodTools.state;

/**
 * @author 李赛鹏
 * @class 新建项目
 * Created by kys_9 on 2016/11/24.
 */

public class CreateProjectActivity extends Activity {
    public static final String TAG = "CreateProjectActivity";
    @InjectView(R.id.createProject_left_iv)
    ImageView mCreateProjectLeftIv;
    @InjectView(R.id.createProject_tittle)
    TextView mCreateProjectTittle;
    @InjectView(R.id.title_Rl)
    RelativeLayout mTitleRl;
    @InjectView(R.id.createProject_step0)
    TextView mCreateProjectStep0;
    @InjectView(R.id.createProject_ApplicationType)
    Spinner mCreateProjectApplicationType;
    @InjectView(R.id.createProject_VarieTytype)
    Spinner mCreateProjectVarieTytype;
    @InjectView(R.id.createProject_Manager)
    EditText mCreateProjectManager;
    @InjectView(R.id.createProject_ManagerPhone)
    EditText mCreateProjectManagerPhone;
    @InjectView(R.id.createProject_Client)
    EditText mCreateProjectClient;
    @InjectView(R.id.createProject_list)
    CustomProjectList mCreateProjectList;
    @InjectView(R.id.createProject_ClientPhone)
    EditText mCreateProjectClientPhone;
    @InjectView(R.id.createProject_ClientMailBox)
    EditText mCreateProjectClientMailBox;
    @InjectView(R.id.createProject_ClientAccount)
    EditText mCreateProjectClientAccount;
    @InjectView(R.id.createProject_SelectFile)
    Button mCreateProjectSelectFile;
    @InjectView(R.id.createProject_FileTxt)
    TextView mCreateProjectFileTxt;
    @InjectView(R.id.createProject_NextStep)
    LinearLayout mCreateProjectNextStep;
    @InjectView(R.id.createProject_scrollview)
    ScrollView mCreateProjectScrollview;
    //URL地址需要辅助一个值去判断 0获取农场类型 1查询客户信息接口2添加客户3获取所有客户类型,4更新步骤
    private static int FARMTYPE = 0, FEEDTYPE = 1, ADDCLIENT = 2, ALLCLIENT = 3, NEXTSTEP = 4;
    @InjectView(R.id.createProject_FarmName)
    EditText mCreateProjectFarmName;
    @InjectView(R.id.createProject_FarmAddress)
    EditText mCreateProjectFarmAddress;
    //获取到的客户列表
    private List<HashMap<String, Object>> ClientList;
    private List<HashMap<String, Object>> ClientMsg;
    //显示用户信息的popuwindow
    PopupWindow mPopupWindow;
    //客户EditText改变时出现ListView,出现popuwindow不好看,先试试ListView
    private boolean addusers_dialog = false;
    //如果没有用户则显示这个dialog
    AlertDialog addusersdialog;
    //判断是否有这个用户
    private boolean bool;
    //  public static String ProjectId;
    public static String farmId;
    public static String farmName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_createproject);
        ButterKnife.inject(this);
        initView();
    }

    /**
     * @return state
     * @author Admin-李赛鹏 create at 2016-9-17 下午8:56:18
     * @function:获取农场列表
     */
    public boolean getWebData(final String URL) {
        //调用静态SharedPreferences获取cookie值
        MethodTools.sPreFerCookie = getSharedPreferences("Cookies",
                Context.MODE_PRIVATE);
        //开始进行发送数据
        VolleyJsonArrayRequest.JsonArrayRequest(CreateProjectActivity.this,
                Path.host + URL);
        //重写Handler中的message方法，获取数据后进行异步加载
        MethodTools.HnadlerVOlleyJson = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == Code.SUCCESS) {
                    //获取数据后进行处理
                    analysis(msg.obj.toString(), FARMTYPE);
                    state = true;
                    //  IntentDialog.dialog.cancel();
                } else if (msg.what == Code.FAILURE) {
                    //消除等待进度弹出框
                    dialog.cancel();
                    MyToast.makeImgAndTextToast(CreateProjectActivity.this, getResources().getDrawable(R.drawable.tips_sry),
                            "请求失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
                    state = false;
                }
            }
        };
        return state;
    }

    public boolean HttpGetData(final String URL, final JSONObject send_jb) {
        //调用静态SharedPreferences获取cookie值
        MethodTools.sPreFerCookie = getSharedPreferences("Cookies",
                Context.MODE_PRIVATE);
//        MyJsonRequestWithCookie.httpPostJson(Path.host + URL, send_jb, MethodTools.sPreFerCookie.getString("cookie", "null"));
        //重写Handler中的message方法，获取数据后进行异步加载
        MethodTools.NewJsonHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == Code.SUCCESS) {
                    //获取数据后进行处理
//                    if (URL.equals(Path.URL_GET_FEEDTYPE)) {
//                        analysis(msg.obj.toString(), FEEDTYPE);
//                    } else
                    if (URL.equals(Path.URL_GET_ALLCLIENT)) {
                        analysis(msg.obj.toString(), ALLCLIENT);
                    } else if (URL.equals(Path.URL_ADD_CLIENT)) {
                        analysis(msg.obj.toString(), ADDCLIENT);
                    } else if (URL.equals(Path.URL_NEXT_STEP)) {
                        analysis(msg.obj.toString(), NEXTSTEP);
                    }
                    state = true;
                    //  IntentDialog.dialog.cancel();
                } else if (msg.what == Code.FAILURE) {
                    //消除等待进度弹出框
                    dialog.cancel();
                    MyToast.makeImgAndTextToast(CreateProjectActivity.this, getResources().getDrawable(R.drawable.tips_sry),
                            "请求失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
                    state = false;
                }
            }
        };
        return state;
    }

    public void getFeedData(String URL, JSONObject send_jb) {
        MethodTools.sPreFerCookie = getSharedPreferences("Cookies",
                Context.MODE_PRIVATE);
        MyJsonRequestWithCookie.newhttpPost(Path.host + URL, send_jb, MethodTools.sPreFerCookie.getString("cookie", "null"), 7);
        MethodTools.handlerJson = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == Code.LUCKYSEVEN) {
                    //获取数据后进行处理
                    analysis(msg.obj.toString(), FEEDTYPE);
                    state = true;
                    //  IntentDialog.dialog.cancel();
                } else if (msg.what == Code.FAILURE) {
                    //消除等待进度弹出框
                    dialog.cancel();
                    MyToast.makeImgAndTextToast(CreateProjectActivity.this, getResources().getDrawable(R.drawable.tips_sry),
                            "请求失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
                    state = false;
                }
            }
        };
    }

    /***
     * @param data//获取后台的农场列表信息
     * @author Admin-李赛鹏 create at 2016-9-17 下午8:56:18
     * @function:对获取的数据进行分析
     */
    private void analysis(String data, int type) {
        //打印获取到的数据，用于开发人员进行数据分析
        Log.e(TAG, "data:" + data + "type:" + type);
        //声明一个map将所有fram数据装载起来
        try {
            if (type == FARMTYPE) {
                JSONArray jsonArray = new JSONArray(data.toString());
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    //json数组中第一个数据是农场信息列表
                    JSONObject ob = jsonArray.getJSONObject(i);
                    list.add(ob.get("applicationTypeName").toString());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list);
                mCreateProjectApplicationType.setAdapter(adapter);
            } else if (type == FEEDTYPE) {
                Log.e(TAG, "feeding:" + data.toString());
                JSONArray jsonArray = new JSONArray(data.toString());
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    //json数组中第一个数据是农场信息列表
                    JSONObject ob = jsonArray.getJSONObject(i);
                    //JSONObject ob=array.getJSONObject(1);
                    Log.e("feed", ob.toString());
                    list.add(ob.get("feedingTypeName").toString());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list);
                mCreateProjectVarieTytype.setAdapter(adapter);
            } else if (type == ALLCLIENT) {
                JSONObject jsonob = new JSONObject(data.toString());
                JSONArray array = jsonob.getJSONArray("data");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject personob = array.getJSONObject(i);
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("phone", personob.get("phone"));
                    map.put("trueName", personob.get("trueName"));
                    map.put("email", personob.get("email"));
                    map.put("nickName", personob.get("nickName"));
                    ClientList.add(map);
                }
            } else if (type == ADDCLIENT) {
                JSONArray array = new JSONArray(data.toString());
                JSONObject ob = array.getJSONObject(0);
                if (ob.getString("Status").equals("Success")) {
                    ToastHelper.show(getApplicationContext(), "添加成功");
                } else {
                    ToastHelper.show(getApplicationContext(), data.toString());
                }
            } else if (type == NEXTSTEP) {
                JSONArray array = new JSONArray(data.toString());
                JSONObject ob = array.getJSONObject(0);
                if (ob.getString("Status").equals("Success")) {
                    Log.e(TAG, "新建项目成功");
                    farmId = ob.getString("farmId");
                    farmName = ob.getString("farmName");
                    Constant.ProjectId = ob.getString("projectId");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * @author 李赛鹏
     * @fuction 初始化控件
     */
    private void initView() {
        ClientList = new ArrayList<HashMap<String, Object>>();
        ClientMsg = new ArrayList<HashMap<String, Object>>();
        //设置进度条显示等待
        IntentDialog.createDialog(CreateProjectActivity.this, "正在获取数据......").show();
        getWebData(Path.URL_GET_FARMTYPE);
        JSONObject feedob = new JSONObject();
        JSONObject clientob = new JSONObject();
        try {
            feedob.put("applicationTypeId", "1");
            clientob.put("trueName", "孙涵");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getFeedData(Path.URL_GET_FEEDTYPE, feedob);
        HttpGetData(Path.URL_GET_ALLCLIENT, clientob);
        //获取完数据让进度条消失
        dialog.dismiss();
        ActionSelectFile();
        // initPopuWindow();
        EditTextListener(mCreateProjectClient);
        initList();
    }

    /***
     * @author李赛鹏
     * @fuction 初始化listView
     */
    private void initList() {
        mCreateProjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setEditTextValue("姓名:" + ClientMsg.get(i).get("trueName").toString(), "手机:" + ClientMsg.get(i).get("phone").toString(),
                        "邮箱:" + ClientMsg.get(i).get("email").toString(), "账号:" + ClientMsg.get(i).get("nickName").toString());
                mCreateProjectList.setVisibility(View.GONE);
            }
        });
    }

    /***
     * @author 李赛鹏
     * @fuction 用来新建popuwindow
     */
    private void initPopuWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.popuwindow_list, null);
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
        ListView listView = (ListView) view.findViewById(R.id.popuwindw_list);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, ClientMsg);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mPopupWindow.dismiss();
                mCreateProjectClientPhone.setVisibility(View.VISIBLE);
                mCreateProjectClientAccount.setVisibility(View.VISIBLE);
                mCreateProjectClientMailBox.setVisibility(View.VISIBLE);
                mCreateProjectClient.setText(ClientMsg.get(i).get("trueName").toString());
                mCreateProjectClientPhone.setText(ClientMsg.get(i).get("phone").toString());
                mCreateProjectClientAccount.setText(ClientMsg.get(i).get("nickName").toString());
                mCreateProjectClientMailBox.setText(ClientMsg.get(i).get("email").toString());
            }
        });
    }

    /***
     * @author 李赛鹏
     * @fuction 用来调用系统自带的文件管理器选择文件
     */
    private void ActionSelectFile() {
        final int FILE_SELECT_CODE = 0;
        mCreateProjectSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                try {
                    startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"),
                            FILE_SELECT_CODE);
                } catch (ActivityNotFoundException ex) {
                    // Potentially direct the user to the Market with a Dialog
                    Toast.makeText(getApplicationContext(), "请安装文件管理器", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    /***
     * @author 李赛鹏
     * @fuction 用来监听
     */
    private void EditTextListener(EditText edit) {
        //暂时存储匹配到的用户信息
//        mList = new ArrayList<String>();
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // mPopupWindow.showAsDropDown(Client);
                ClientMsg.clear();
                //判断解析到的全部客户信息中是否有正在输入的信息,有就把数据添加到ClienMsg中
                for (int a = 0; a < ClientList.size(); a++) {
                    if (ClientList.get(a).get("nickName").toString().contains(charSequence)) {
                        ClientMsg.add(ClientList.get(a));
                    }
                }
                if (charSequence.equals("") && charSequence.length() == 0) {
                    mCreateProjectList.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.e(TAG, "ClientMsg::" + String.valueOf(ClientMsg.size()));
                //如果ClientMsg不为空就显示popuWindow,为空就不显示
                if (ClientMsg.size() == 0) {
                    mCreateProjectList.setVisibility(View.GONE);
                } else if (mCreateProjectList.getVisibility() == View.VISIBLE) {
                    ClientListAdapter adapter = new ClientListAdapter(CreateProjectActivity.this, ClientMsg);
                    mCreateProjectScrollview.smoothScrollTo(0, 0);
                    mCreateProjectList.setAdapter(adapter);
                } else {
                    ClientListAdapter adapter = new ClientListAdapter(CreateProjectActivity.this, ClientMsg);
                    mCreateProjectList.setAdapter(adapter);
                    mCreateProjectScrollview.smoothScrollTo(0, 0);
                    mCreateProjectList.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            String img_path = actualimagecursor.getString(actual_image_column_index);
            File file = new File(img_path);
            mCreateProjectFileTxt.setText("您选择了文件" + img_path.substring(img_path.lastIndexOf('/') + 1));
            Toast.makeText(this, file.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick({R.id.createProject_left_iv, R.id.createProject_NextStep})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.createProject_left_iv:
                this.finish();
                break;
            case R.id.createProject_NextStep:
                bool = false;
                for (int a = 0; a < ClientList.size(); a++) {
                    if (mCreateProjectClient.getText().toString().length() > 3) {
                        if (ClientList.get(a).get("nickName").toString().equals(mCreateProjectClientAccount.getText().toString().substring(3))) {
                            bool = true;
                            Log.e(TAG, "bool:" + bool);
                        }
                    }
                }
                if (bool) {
                    //如果是真,则判断用户信息是否填写完整
                    judgeDialog();
                } else {
                    //如果为假,则去添加用户
                    CreateDialog();
                }
                break;
        }

    }

    /***
     * @fuction 判断用户信息填写是否正确
     */
    public void judgeDialog() {
        if (!StringUtil.isEmpty(mCreateProjectClient.getText().toString()) &&
                !StringUtil.isEmpty(mCreateProjectClientPhone.getText().toString()) &&
                !StringUtil.isEmpty(mCreateProjectClientAccount.getText().toString()) &&
                !StringUtil.isEmpty(mCreateProjectClientMailBox.getText().toString()) &&
                !StringUtil.isEmpty(mCreateProjectFarmName.getText().toString()) &&
                !StringUtil.isEmpty(mCreateProjectFarmAddress.getText().toString())) {
            if (StringUtil.isMobileNO(mCreateProjectClientPhone.getText().toString().substring(3)) &&
                    StringUtil.isEmail(mCreateProjectClientMailBox.getText().toString().substring(3))) {
                Intent intent = new Intent();
                intent.setClass(CreateProjectActivity.this, AddGateWayActivity.class);
                JSONObject Clientmsgob = new JSONObject();
//
//                try {
//                    Clientmsgob.put("applicationType", mCreateProjectApplicationType.getSelectedItem().toString());
//                    Clientmsgob.put("feedingType", mCreateProjectVarieTytype.getSelectedItem().toString());
//                    Clientmsgob.put("farmname", mCreateProjectFarmName.getText().toString());
//                    Clientmsgob.put("farmaddress", mCreateProjectFarmAddress.getText().toString());
//                    Clientmsgob.put("clientNickName", mCreateProjectClientAccount.getText().toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                HttpGetData(Path.URL_NEXT_STEP, Clientmsgob);
                intent.putExtra("farmname", mCreateProjectFarmName.getText().toString());
                startActivity(intent);
            } else {
                Log.e(TAG, "phone:" + mCreateProjectClientPhone.getText().toString().substring(3)
                        + "email:" + mCreateProjectClientMailBox.getText().toString().substring(3));
                ToastHelper.show(CreateProjectActivity.this, "手机或邮箱格式不正确");
            }
        } else {
            ToastHelper.show(this, "请填写完整信息以便进行下一步");
        }

    }

    /***
     * @param NameText  客户姓名
     * @param PhoneText 客户电话
     * @param EmailText 客户邮箱
     * @fuction 填写三个EditText的值
     */
    public void setEditTextValue(String NameText, String PhoneText, String EmailText, String AccountText) {
        mCreateProjectClientPhone.setVisibility(View.VISIBLE);
        mCreateProjectClientAccount.setVisibility(View.VISIBLE);
        mCreateProjectClientMailBox.setVisibility(View.VISIBLE);
        mCreateProjectClientPhone.setText(PhoneText);
        mCreateProjectClientAccount.setText(AccountText);
        mCreateProjectClientMailBox.setText(EmailText);
        mCreateProjectClient.setText(NameText);
    }

    /***
     * @fuction 添加用户dialog
     */
    public void CreateDialog() {
        View notFoundUsers = getLayoutInflater().from(this).inflate(R.layout.dialog_notfoundusers, null);
        final View addUsers = getLayoutInflater().from(this).inflate(R.layout.dialog_addusers, null);
        final EditText nameev, emailev, phoneev;
        nameev = (EditText) addUsers.findViewById(R.id.dialog_addusers_trueNameev);
        emailev = (EditText) addUsers.findViewById(R.id.dialog_addusers_emailev);
        phoneev = (EditText) addUsers.findViewById(R.id.dialog_addusers_phoneev);
        addusersdialog = new AlertDialog.
                Builder(this).setTitle("添加用户")
                .setCancelable(false)
                .setView(notFoundUsers)
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new AlertDialog.Builder(CreateProjectActivity.this).
                                setTitle("添加用户")
                                .setCancelable(false)
                                .setView(addUsers)
                                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        canCloseDialog(dialogInterface, true);
                                    }
                                })
                                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface Interface, int i) {
                                        if (!StringUtil.isEmpty(nameev.getText().toString()) &&
                                                !StringUtil.isEmpty(emailev.getText().toString()) &&
                                                !StringUtil.isEmpty(phoneev.getText().toString())) {

                                            if (StringUtil.isMobileNO(phoneev.getText().toString()) &&
                                                    StringUtil.isEmail(emailev.getText().toString())) {
                                                canCloseDialog(Interface, true);
                                                JSONObject addClient = new JSONObject();
                                                try {
                                                    addClient.put("trueName", nameev.getText().toString());
                                                    addClient.put("email", emailev.getText().toString());
                                                    addClient.put("phone", phoneev.getText().toString());
                                                    addClient.put("nickName", phoneev.getText().toString());
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                HttpGetData(Path.URL_ADD_CLIENT, addClient);
                                                setEditTextValue("姓名:" + nameev.getText().toString(), "手机:" + phoneev.getText().toString(),
                                                        "邮箱:" + emailev.getText().toString(), "账号:" + phoneev.getText().toString());
                                            } else {
                                                canCloseDialog(Interface, false);
                                                ToastHelper.show(CreateProjectActivity.this, "手机或邮箱格式不正确");
                                            }
                                        } else {
                                            canCloseDialog(Interface, false);
                                            ToastHelper.show(CreateProjectActivity.this, "请填写完整信息以便添加用户");

                                        }
                                    }
                                }).show();
                    }
                }).create();
//          addusersdialog.setCancelable(false);
        addusersdialog.show();
    }

    /***
     * @param dialogInterface dialog的接口
     * @param close           是否可以关闭
     * @fuction 点击dialog消失或者不消失
     */
    private void canCloseDialog(DialogInterface dialogInterface, boolean close) {
        try {
            Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialogInterface, close);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
