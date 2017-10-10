package com.kys26.webthings.permission;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kys26.webthings.adapter.SpinnerAdapter;
import com.kys26.webthings.dialog.IntentDialog;
import com.kys26.webthings.httpconstant.Code;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.httpnetworks.MyJsonRequestWithCookie;
import com.kys26.webthings.httpnetworks.VolleyJsonArrayRequest;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.util.MyToast;
import com.kys26.webthings.util.StringUtil;
import com.kys26.webthings.view.SwipeItemLayout;
import com.kys26.webthings.view.SwipeListView;
import com.lidroid.xutils.ViewUtils;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author kys_09使用者：李赛鹏 on 2016-09-17
 * @function: 选择查看历史详情
 * @return null
 */

public class PermissionActivity extends Activity {
    private String TAG = "com.kys26.webthings.permission.PermissionActivity";
    /**
     * 设置一个返回值，用于下拉刷新判断
     */
    boolean state = false;
    //左边返回的ImageView
    private ImageView left_iv;
    //创建一个自定义的ListView

    //定义一个List用来储存从后台获得的信息.(除farms外)
    public List<HashMap<String, Object>> list;
    //创建spinner
    private Spinner mSpinner;
    //创建Spinner的数据源
    List<String> mItems = new ArrayList<String>();
    SharedPreferences mSharedPreferences;
    //添加list的button；
    ImageView bt_add_list;
    //创建工具类StringUtil的变量名
    StringUtil mStringUtil;
    //分类按钮
    private Button Spinnerbtn;
    //创建一个List用来储存farms的信息
    public List<HashMap<String, Object>> farms;
    //全部数据的JsonArray
    JSONArray array;
    // 展示所有下拉选项的ListView
    // PopupWindow对象
    private PopupWindow selectPopupWindow = null;
    private ListView listView = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        // 初始化IOC注解
        ViewUtils.inject(this);
        //初始化UI组件
        initView();
        //获取后台数据
    }

    /**
     * @return null
     * @author kys_09使用者：李赛鹏  on 2016-09-17
     * @function: 初始化UI组件
     */
    private void initView() {
        //实例化StringUtil的对象
        mStringUtil = new StringUtil();
        //左边ledt_iv的点击监听
        left_iv = (ImageView) findViewById(R.id.left_iv);
        left_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionActivity.this.finish();
            }
        });
        //获取SharedPerenes;
        mSharedPreferences = getSharedPreferences("Cookies", Context.MODE_PRIVATE);
        //添加list的button
        bt_add_list = (ImageView) findViewById(R.id.bt_add_list);
        //自定义的Spinner
        Spinnerbtn = (Button) findViewById(R.id.classify_btn);
        View view = getLayoutInflater().inflate(R.layout.list_item_layout, null);
        getWebData();
        SpinnerbtnOnclick();
    }

    private void SpinnerbtnOnclick() {
        mItems.add("全部用户");
        mItems.add("浏览用户");
        mItems.add("控制用户");
        Spinnerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPopuWindow(mItems);
                popupWindwShowing(Spinnerbtn);
            }
        });
    }

    public void initPopuWindow(final List<String> mlist) {
        View loginwindow = (View) this.getLayoutInflater().inflate(
                R.layout.spinner_dropview, null);
        listView = (ListView) loginwindow.findViewById(R.id.list);
        SpinnerAdapter adapter = new SpinnerAdapter(this, mlist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position,
                                    long arg3) {
                //et.setText(str[position]);
                JSONObject object = new JSONObject();
                try {
                    if (position == 0) {
                        getWebData();
                        selectPopupWindow.dismiss();
                        Spinnerbtn.setText("全部用户");
                    } else if (position == 1) {
                        object.put("roleid", "4");
                        sendToWeb(Path.URL_GET_CONTROL, object);
                        selectPopupWindow.dismiss();
                        Spinnerbtn.setText("控制用户");
                    } else {
                        object.put("roleid", "3");
                        sendToWeb(Path.URL_GET_CONTROL, object);
                        selectPopupWindow.dismiss();
                        Spinnerbtn.setText("浏览用户");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        selectPopupWindow = new PopupWindow(loginwindow, 160,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        selectPopupWindow.setOutsideTouchable(true);
        //当点击屏幕其他部分及Back键时PopupWindow消失
        selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    public void popupWindwShowing(View view) {
        selectPopupWindow.showAsDropDown(view, 0, -1);
    }

    /**
     * @return state
     * @author Admin-李赛鹏 create at 2016-9-17 下午8:56:18
     * @function:获取农场列表
     */
    public boolean getWebData() {
        //设置进度条显示等待
        IntentDialog.createDialog(PermissionActivity.this, "正在获取数据......").show();
        //调用静态SharedPreferences获取cookie值
        MethodTools.sPreFerCookie = getSharedPreferences("Cookies",
                Context.MODE_PRIVATE);
        //开始进行发送数据
        VolleyJsonArrayRequest.StringRequest(PermissionActivity.this,
                Path.host + Path.URL_GET_USER,
                MethodTools.sPreFerCookie.getString("cookie", "null"));
        //重写Handler中的message方法，获取数据后进行异步加载
        MethodTools.HnadlerVOlleyJson = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == Code.SUCCESS) {
                    //获取数据后进行处理
                    analysis(JSONTokener(msg.obj.toString()));
                    state = true;
                    IntentDialog.dialog.cancel();
                } else if (msg.what == Code.FAILURE) {
                    //消除等待进度弹出框
                    IntentDialog.dialog.cancel();
                    MyToast.makeImgAndTextToast(PermissionActivity.this, getResources().getDrawable(R.drawable.tips_sry),
                            "请求失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
                    state = false;
                }
            }
        };
        return state;
    }

    /***
     * @param data//获取后台的农场列表信息
     * @return null
     * @author Admin-李赛鹏 create at 2016-9-17 下午8:56:18
     * @function:对获取的数据进行分析
     */
    private void analysis(String data) {
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
            list = new ArrayList<HashMap<String, Object>>();
            for (int i = 0; i <= jsonArray.length(); i++) {
                //实例化list的对象
                //json数组中第一个数据是农场信息列表
                HashMap<String, Object> map = new HashMap<String, Object>();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                map.put("phone", jsonObject.getString("phone"));//获取用户的电话号码
                Log.i(TAG, map.get("phone").toString());
                map.put("truename", jsonObject.getString("truename"));//获取用户的真是名字
                Log.i(TAG, map.get("truename").toString());
                map.put("email", jsonObject.getString("email"));//获取用户的邮箱
                Log.i(TAG, map.get("email").toString());
                map.put("juese", jsonObject.getString("juese"));//获取用户角色
                Log.i(TAG, map.get("juese").toString());
                map.put("nickName", jsonObject.getString("nickname"));
                Log.i(TAG, jsonObject.getString("nickname"));
                map.put("farms", jsonObject.getJSONArray("farms"));
                Log.i(TAG, jsonObject.getString("farms"));
                array = jsonObject.getJSONArray("farms");
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwipeListView lv = (SwipeListView) findViewById(R.id.permission_list);
        SwipeAdapter adapter = new SwipeAdapter(this);
        lv.setAdapter(adapter);
    }

    /**
     * 发送Json数据到web
     *
     * @param json
     * @author 李赛鹏 2016年9月19日15:16:51
     */
    public void sendToWeb(String path, JSONObject json) {
        //开始进行发送数据
        MyJsonRequestWithCookie.httpPost(Path.host + path, json, MethodTools.sPreFerCookie.getString("cookie", "null"));
        //重写Handler中的message方法，获取数据后进行异步加载
        MethodTools.handlerJson = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == Code.SUCCESS) {
                    //获取数据后进行处理
                    analysis(msg.obj.toString());
//                    state=true;
                } else if (msg.what == Code.FAILURE) {
                    //消除等待进度弹出框
                    IntentDialog.dialog.cancel();
                    MyToast.makeImgAndTextToast(PermissionActivity.this, getResources().getDrawable(R.drawable.tips_sry),
                            "请求失败", MyToast.Long_TIME, Gravity.BOTTOM, 120).show();
//                    state=false;
                }
            }
        };
    }

    /***
     * @param in
     * @return
     */
    public static String JSONTokener(String in) {
        // consume an optional byte order mark (BOM) if it exists
        if (in != null && in.startsWith("\ufeff")) {
            Log.i("Permission", "运行");
            in = in.substring(1);
        }
        return in;
    }

    /**
     * @author kys_26使用者：李赛鹏  on 2016-9-17
     * @function:swipeList的适配器
     * @return null
     */
    public class SwipeAdapter extends BaseAdapter {
        //定义名字角色，电话，邮箱
        String name, juese, phone_number, email_number, nickName, farm_id;
        //定义 role_id 浏览用户为3，控制用户为4;
        int role_id;
        String usersName, usersPasswrod;
        //编辑用户的StringBuffer
        StringBuffer mStringBuffer = new StringBuffer();
        //新增用户的StringBuffer
        StringBuffer StringBuffer = new StringBuffer();
        private Context mContext = null;
        HashMap<String, Object> MessageMap = new HashMap<String, Object>();

        public SwipeAdapter(Context context) {
            this.mContext = context;
//            usersName = mSharedPreferences.getString("userName", "账号错误");
//            usersPasswrod = mSharedPreferences.getString("userPassword", "密码错误");
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            Log.i(TAG, "list.size():" + Integer.toString(list.size()));
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(final int position, View contentView, ViewGroup arg2) {
            ViewHolder holder = null;
            if (contentView == null) {
                holder = new ViewHolder();
                //获取ListView的内容的Layout
                View swipe_content = LayoutInflater.from(mContext).inflate(R.layout.swipelist_content, null);
                //获取ListView的Item的Layout
                View swipe_item = LayoutInflater.from(mContext).inflate(R.layout.swipelist_item, null);
                //获取ListView的Item的编辑Button
                holder.edit_btn = (Button) swipe_item.findViewById(R.id.edit_btn);
                //获取ListView的Item的联系方式Button
                holder.contact_btn = (Button) swipe_item.findViewById(R.id.contact_btn);
                //获取ListView的Item的删除Button
                holder.del_btn = (Button) swipe_item.findViewById(R.id.del_btn);
                //获取ListView的内容的名字text
                holder.swipe_list_name = (TextView) swipe_content.findViewById(R.id.swipe_list_name);
                //获取ListView的内容的用户角色text
                holder.swipe_list_juese = (TextView) swipe_content.findViewById(R.id.swipe_list_role);
                //编辑按钮的点击监听事件
                holder.swipe_list_name.setText(list.get(position).get("truename").toString());
                holder.swipe_list_juese.setText(list.get(position).get("juese").toString());
                //这个是添加按钮
                contentView = new SwipeItemLayout(swipe_content, swipe_item, null, null);
                contentView.setTag(holder);
            } else {
                holder = new ViewHolder();
                holder = (ViewHolder) contentView.getTag();
            }
            holder.edit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getWebData();
                    name = list.get(position).get("truename").toString();
                    juese = list.get(position).get("juese").toString();
                    phone_number = list.get(position).get("phone").toString();
                    email_number = list.get(position).get("email").toString();
                    nickName = list.get(position).get("nickName").toString();
                    // sendToWeb();
                    showedit(name, phone_number, email_number, position, juese);
                }
            });
            //联系方式按钮的点击监听事件
            holder.contact_btn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    phone_number = list.get(position).get("phone").toString();
                    email_number = list.get(position).get("email").toString();
                    showcontact(phone_number, email_number);
                }
            });
            //删除按钮的点击监听事件
            holder.del_btn.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    nickName = list.get(position).get("nickName").toString();
                    showdel(nickName, name, position);
                }
            });
            bt_add_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showinsert();
                }
            });
            return contentView;
        }

        public void showedit(String name, final String phone_number, final String email_number, final int position, final String role) {

            //此处直接new一个Dialog对象出来，在实例化的时候传入主题
            final Dialog dialog = new Dialog(PermissionActivity.this, R.style.wheel_alertDialogStyle);
            //设置它的ContentView
            dialog.setContentView(R.layout.permission_dialog);
            TextView tittle = (TextView) dialog.findViewById(R.id.txt_title);
            tittle.setText("编辑信息");
            //姓名
            TextView true_name = (TextView) dialog.findViewById(R.id.true_name);
            true_name.setVisibility(View.VISIBLE);
            true_name.setText("真实姓名:");
            final EditText true_name_edit = (EditText) dialog.findViewById(R.id.true_name_edit);
            true_name_edit.setVisibility(View.VISIBLE);
            true_name_edit.setText(name);
            //用户角色
            TextView juese = (TextView) dialog.findViewById(R.id.juese);
            juese.setVisibility(View.VISIBLE);
            juese.setText("用户角色:");
            final RadioGroup group = (RadioGroup) dialog.findViewById(R.id.juese_group);
            group.setVisibility(View.VISIBLE);
            //浏览用户
            final RadioButton browse_btn = (RadioButton) dialog.findViewById(R.id.browse_btn);
            ;
            final RadioButton control_btn = (RadioButton) dialog.findViewById(R.id.control_btn);
            browse_btn.setVisibility(View.VISIBLE);
            browse_btn.setText("浏览用户");
            //控制用户
            control_btn.setVisibility(View.VISIBLE);
            control_btn.setText("控制用户");
            if (role.toString().equals("浏览用户")) {
                browse_btn.setChecked(true);
            } else if (role.toString().equals("控制用户")) {
                control_btn.setChecked(true);
            }
            //手机号
            final TextView phone = (TextView) dialog.findViewById(R.id.phone);
            phone.setVisibility(View.VISIBLE);
            phone.setText("联系方式:");
            final EditText phone_edit = (EditText) dialog.findViewById(R.id.phone_edit);
            phone_edit.setVisibility(View.VISIBLE);
            phone_edit.setText(phone_number);
            //邮箱
            final TextView email = (TextView) dialog.findViewById(R.id.email);
            email.setVisibility(View.VISIBLE);
            email.setText("电子邮箱:");
            //这是邮箱的EditText
            final EditText email_edit = (EditText) dialog.findViewById(R.id.email_edit);
            email_edit.setVisibility(View.VISIBLE);
            email_edit.setText(email_number);
            //这是管理农场的那句话;
            TextView manager_farm = (TextView) dialog.findViewById(R.id.manage_farm);
            manager_farm.setVisibility(View.VISIBLE);
            manager_farm.setText("管理农场:");
            //这是怀安国强兔场的checkbox;
            final CheckBox huaian = (CheckBox) dialog.findViewById(R.id.huaian_farm);
            huaian.setVisibility(View.VISIBLE);
            //这是怀安国强兔场的TextView;
            TextView huai_text = (TextView) dialog.findViewById(R.id.huaian_text);
            huai_text.setVisibility(View.VISIBLE);
            huai_text.setText("怀安国强兔场");
            //这是宣化永利肉兔养殖场的CheckBox;
            final CheckBox xuanhua = (CheckBox) dialog.findViewById(R.id.xuanhua_farm);
            xuanhua.setVisibility(View.VISIBLE);
            //这是宣化永利肉兔养殖场的TextView
            TextView xuanhua_text = (TextView) dialog.findViewById(R.id.xuanhua_text);
            xuanhua_text.setVisibility(View.VISIBLE);
            xuanhua_text.setText("宣化永利肉兔养殖场");
            //北方学院肉兔养殖场的CheckBox
            final CheckBox beifang = (CheckBox) dialog.findViewById(R.id.beifang_farm);
            beifang.setVisibility(View.VISIBLE);
            //这是北方学院肉兔养殖场的TextView
            TextView beifang_text = (TextView) dialog.findViewById(R.id.beifang_text);
            beifang_text.setVisibility(View.VISIBLE);
            beifang_text.setText("北方学院肉兔养殖场");
            //这是标题下的那条线
            ImageView img_v = (ImageView) dialog.findViewById(R.id.line_v);
            img_v.setVisibility(View.VISIBLE);
            //这是垂直的那条线
            ImageView line_vertical = (ImageView) dialog.findViewById(R.id.dialog_marBottom);
            line_vertical.setVisibility(View.VISIBLE);
            LinearLayout btn_layout = (LinearLayout) dialog.findViewById(R.id.btn_layout);
            btn_layout.setVisibility(View.VISIBLE);
            //取消按钮
            Button pbtn = (Button) dialog.findViewById(R.id.btn_pos);
            pbtn.setVisibility(View.VISIBLE);
            pbtn.setText("取消");
            pbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            //这是水平的那条线
            ImageView line_horizontal = (ImageView) dialog.findViewById(R.id.img_line);
            line_horizontal.setVisibility(View.VISIBLE);
            //确定按钮
            Button nbtn = (Button) dialog.findViewById(R.id.btn_neg);
            nbtn.setVisibility(View.VISIBLE);
            nbtn.setText("保存");
            nbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mStringUtil.isEmpty(true_name_edit.getText().toString()) & !StringUtil.isEmpty(phone_edit.getText().toString()) &
                            !StringUtil.isEmpty(email_edit.getText().toString()) & (browse_btn.isChecked() | control_btn.isChecked())) {
                        if (mStringUtil.isMobileNO(phone_edit.getText().toString()) & mStringUtil.isEmail(email_edit.getText().toString())) {
                            JSONObject send_jb = new JSONObject();
                            try {
                                send_jb.put("contactPhone", phone_number);
                                send_jb.put("email", email_number);
                                send_jb.put("farm", MethodTools.listdata_farm.get(0).get("farm_id").toString() + ",");
                                send_jb.put("nickName", nickName);
                                send_jb.put("qq", Integer.toString(position));
                                send_jb.put("passwrod", usersPasswrod);
                                if (huaian.isChecked()) {
                                    mStringBuffer.append(MethodTools.listdata_farm.get(0).get("farm_id").toString() + ",");
                                }
                                if (xuanhua.isChecked()) {
                                    mStringBuffer.append(MethodTools.listdata_farm.get(1).get("farm_id").toString() + ",");
                                }
                                if (beifang.isChecked()) {
                                    mStringBuffer.append(MethodTools.listdata_farm.get(2).get("farm_id").toString() + ",");
                                }
                                send_jb.put("farm", mStringBuffer.toString());
                                sendToWeb(Path.URL_CHANGE_USER, send_jb);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "邮箱或者手机格式不正确", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "请填写完整信息", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            try {
                ///farms=new ArrayList<HashMap<String, Object>>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject farmsObject = array.getJSONObject(i);
                    farmsObject.getString("farmname");
                    Log.i(TAG, farmsObject.getString("farmname"));
                    farmsObject.getString("farmid");
                    Log.i(TAG, farmsObject.getString("farmid"));
                    if (farmsObject.getString("farmid").equals("10000001")) {
                        Log.i(TAG, "运行2");
                        huaian.setChecked(true);
                    } else if (farmsObject.getString("farmid").equals("10000002")) {
                        Log.i(TAG, farmsObject.getString("farmid"));
                        Log.i(TAG, "运行3");
                        xuanhua.setChecked(true);
                    } else if (farmsObject.getString("farmid").equals("10000003")) {
                        beifang.setChecked(true);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.show();
        }

        public void showcontact(String phone_number, String email_number) {
            //此处直接new一个Dialog对象出来，在实例化的时候传入主题
            final Dialog dialog = new Dialog(PermissionActivity.this, R.style.wheel_alertDialogStyle);
            //设置它的ContentView
            dialog.setContentView(R.layout.permission_dialog);
            TextView tittle = (TextView) dialog.findViewById(R.id.txt_title);
            tittle.setText("联系方式");
            //手机号
            TextView phone = (TextView) dialog.findViewById(R.id.phone);
            phone.setVisibility(View.VISIBLE);
            phone.setText("联系方式:");
            TextView phone_content = (TextView) dialog.findViewById(R.id.phone_content);
            phone_content.setVisibility(View.VISIBLE);
            phone_content.setText(phone_number);
            //邮箱
            TextView email = (TextView) dialog.findViewById(R.id.email);
            email.setVisibility(View.VISIBLE);
            email.setText("电子邮箱:");
            TextView email_content = (TextView) dialog.findViewById(R.id.email_content);
            email_content.setVisibility(View.VISIBLE);
            email_content.setText(email_number);
            //这是标题下的那条线
            ImageView img_v = (ImageView) dialog.findViewById(R.id.line_v);
            img_v.setVisibility(View.VISIBLE);
            dialog.show();
        }

        /**
         * @param nickName 这个是要给Web提交的信息:账号
         * @param name     这个是真实姓名，删除的时候提示的时候要显示
         */
        public void showdel(final String nickName, String name, final int position) {
            //此处直接new一个Dialog对象出来，在实例化的时候传入主题
            final Dialog dialog = new Dialog(PermissionActivity.this, R.style.wheel_alertDialogStyle);
            //设置它的ContentView
            dialog.setContentView(R.layout.permission_dialog);
            TextView tittle = (TextView) dialog.findViewById(R.id.txt_title);
            tittle.setText("删除");
            //显示message和要删除的人的真实姓名
            TextView msg = (TextView) dialog.findViewById(R.id.txt_msg);
            msg.setVisibility(View.VISIBLE);
            name = list.get(position).get("truename").toString();
            msg.setText("您确定要删除" + name + "的信息吗？");
            //垂直那条线
            ImageView line_vertical = (ImageView) dialog.findViewById(R.id.dialog_marBottom);
            line_vertical.setVisibility(View.VISIBLE);
            //水平那条线
            ImageView line_horizontal = (ImageView) dialog.findViewById(R.id.img_line);
            line_horizontal.setVisibility(View.VISIBLE);
            LinearLayout btn_layout = (LinearLayout) dialog.findViewById(R.id.btn_layout);
            btn_layout.setVisibility(View.VISIBLE);
            //这是标题下的那条线
            ImageView img_v = (ImageView) dialog.findViewById(R.id.line_v);
            img_v.setVisibility(View.VISIBLE);
            //取消按钮
            Button pbtn = (Button) dialog.findViewById(R.id.btn_pos);
            pbtn.setVisibility(View.VISIBLE);
            pbtn.setText("取消");
            pbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            //确定按钮
            Button nbtn = (Button) dialog.findViewById(R.id.btn_neg);
            nbtn.setVisibility(View.VISIBLE);
            nbtn.setText("确定");
            nbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JSONObject send_jb = new JSONObject();
                    try {
                        send_jb.put("nickName", nickName);
                        //发送信息到web;
                        sendToWeb(Path.URL_DELET_USER, send_jb);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                    list.remove(position);
                    AdapterDataChanged();
                    dialog.dismiss();

                }
            });
            dialog.show();
        }

        public void showinsert() {
            //此处直接new一个Dialog对象出来，在实例化的时候传入主题
            final Dialog dialog = new Dialog(PermissionActivity.this, R.style.wheel_alertDialogStyle);
            //设置它的ContentView
            dialog.setContentView(R.layout.permission_dialog);
            TextView tittle = (TextView) dialog.findViewById(R.id.txt_title);
            tittle.setText("添加用户");
            //账号
            TextView nickname = (TextView) dialog.findViewById(R.id.nickname);
            nickname.setText("用户账号");
            nickname.setVisibility(View.VISIBLE);
            final EditText nickname_edit = (EditText) dialog.findViewById(R.id.nickname_edit);
            nickname_edit.setVisibility(View.VISIBLE);
            //密码
            final TextView password = (TextView) dialog.findViewById(R.id.password);
            password.setText("用户密码");
            password.setVisibility(View.VISIBLE);
            final EditText password_edit = (EditText) dialog.findViewById(R.id.password_edit);
            password_edit.setVisibility(View.VISIBLE);
            //姓名
            TextView true_name = (TextView) dialog.findViewById(R.id.true_name);
            true_name.setVisibility(View.VISIBLE);
            true_name.setText("真实姓名:");
            final EditText true_name_edit = (EditText) dialog.findViewById(R.id.true_name_edit);
            true_name_edit.setVisibility(View.VISIBLE);
            //用户角色
            TextView juese = (TextView) dialog.findViewById(R.id.juese);
            juese.setVisibility(View.VISIBLE);
            juese.setText("用户角色:");
            //group
            final RadioGroup group = (RadioGroup) dialog.findViewById(R.id.juese_group);
            group.setVisibility(View.VISIBLE);
            //浏览用户
            final RadioButton browsebtn = (RadioButton) dialog.findViewById(R.id.browse_btn);
            browsebtn.setVisibility(View.VISIBLE);
            browsebtn.setText("浏览用户");
            //控制用户
            final RadioButton controlbtn = (RadioButton) dialog.findViewById(R.id.control_btn);
            controlbtn.setVisibility(View.VISIBLE);
            controlbtn.setText("控制用户");
            //手机号
            final TextView phone = (TextView) dialog.findViewById(R.id.phone);
            phone.setVisibility(View.VISIBLE);
            phone.setText("联系方式:");
            final EditText phone_edit = (EditText) dialog.findViewById(R.id.phone_edit);
            phone_edit.setVisibility(View.VISIBLE);
            //邮箱
            final TextView email = (TextView) dialog.findViewById(R.id.email);
            email.setVisibility(View.VISIBLE);
            email.setText("电子邮箱:");
            //这是邮箱的EditText
            final EditText email_edit = (EditText) dialog.findViewById(R.id.email_edit);
            email_edit.setVisibility(View.VISIBLE);
            //这是管理农场的那句话;
            TextView manager_farm = (TextView) dialog.findViewById(R.id.manage_farm);
            manager_farm.setVisibility(View.VISIBLE);
            manager_farm.setText("管理农场:");
            //这是怀安国强兔场的checkbox;
            final CheckBox huaian_box = (CheckBox) dialog.findViewById(R.id.huaian_farm);
            huaian_box.setVisibility(View.VISIBLE);
            //这是怀安国强兔场的TextView;
            TextView huai_text = (TextView) dialog.findViewById(R.id.huaian_text);
            huai_text.setVisibility(View.VISIBLE);
            huai_text.setText("怀安国强兔场");
            //这是宣化永利肉兔养殖场的CheckBox;
            final CheckBox xuanhua_box = (CheckBox) dialog.findViewById(R.id.xuanhua_farm);
            xuanhua_box.setVisibility(View.VISIBLE);
            //这是宣化永利肉兔养殖场的TextView
            TextView xuanhua_text = (TextView) dialog.findViewById(R.id.xuanhua_text);
            xuanhua_text.setVisibility(View.VISIBLE);
            xuanhua_text.setText("宣化永利肉兔养殖场");
            //北方学院肉兔养殖场的CheckBox
            final CheckBox beifang_box = (CheckBox) dialog.findViewById(R.id.beifang_farm);
            beifang_box.setVisibility(View.VISIBLE);
            //这是北方学院肉兔养殖场的TextView
            TextView beifang_text = (TextView) dialog.findViewById(R.id.beifang_text);
            beifang_text.setVisibility(View.VISIBLE);
            beifang_text.setText("北方学院肉兔养殖场");
            //这是标题下的那条线
            ImageView img_v = (ImageView) dialog.findViewById(R.id.line_v);
            img_v.setVisibility(View.VISIBLE);
            //这是垂直的那条线
            ImageView line_vertical = (ImageView) dialog.findViewById(R.id.dialog_marBottom);
            line_vertical.setVisibility(View.VISIBLE);
            LinearLayout btn_layout = (LinearLayout) dialog.findViewById(R.id.btn_layout);
            btn_layout.setVisibility(View.VISIBLE);
            //取消按钮
            Button pbtn = (Button) dialog.findViewById(R.id.btn_pos);
            pbtn.setVisibility(View.VISIBLE);
            pbtn.setText("取消");
            pbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            //这是水平的那条线
            ImageView line_horizontal = (ImageView) dialog.findViewById(R.id.img_line);
            line_horizontal.setVisibility(View.VISIBLE);
            //确定按钮
            Button nbtn = (Button) dialog.findViewById(R.id.btn_neg);
            nbtn.setVisibility(View.VISIBLE);
            nbtn.setText("确定");
            nbtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (!mStringUtil.isEmpty(true_name_edit.getText().toString()) & !StringUtil.isEmpty(phone_edit.getText().toString()) &
                            !StringUtil.isEmpty(email_edit.getText().toString()) & !mStringUtil.isEmpty(nickname_edit.getText().toString()) & !mStringUtil.isEmpty(password_edit.getText().toString())) {
                        if (mStringUtil.isMobileNO(phone_edit.getText().toString()) & mStringUtil.isEmail(email_edit.getText().toString()) & (browsebtn.isChecked() | controlbtn.isChecked())) {
                            JSONObject send_jb = new JSONObject();
                            try {
                                if (browsebtn.isChecked()) {
                                    role_id = 3;
                                } else if (controlbtn.isChecked()) {
                                    role_id = 4;
                                } else {
                                }
                                send_jb.put("nickName", nickname_edit.getText().toString());
                                send_jb.put("passWord", password_edit.getText().toString());
                                send_jb.put("role_id", Integer.toString(role_id));
                                send_jb.put("trueName", true_name_edit.getText().toString());
                                send_jb.put("contactPhone", phone_edit.getText().toString());
                                send_jb.put("email", email_edit.getText().toString());
                                MessageMap.put("nickName", nickname_edit.getText().toString());
                                MessageMap.put("passWord", password_edit.getText().toString());
                                if (role_id == 3) {
                                    MessageMap.put("juese", "浏览用户");
                                } else if (role_id == 4) {
                                    MessageMap.put("juese", "控制用户");
                                }
                                MessageMap.put("truename", true_name_edit.getText().toString());
                                MessageMap.put("contactPhone", phone_edit.getText().toString());
                                MessageMap.put("email", email_edit.getText().toString());
                                if (huaian_box.isChecked()) {
                                    Log.i(TAG, "box被选中1");
                                    StringBuffer.append(MethodTools.listdata_farm.get(0).get("farm_id").toString() + ",");
                                }
                                if (xuanhua_box.isChecked()) {
                                    StringBuffer.append(MethodTools.listdata_farm.get(1).get("farm_id").toString() + ",");
                                }
                                if (beifang_box.isChecked()) {
                                    StringBuffer.append(MethodTools.listdata_farm.get(2).get("farm_id").toString() + ",");
                                }
                                if (mStringUtil.isEmpty(StringBuffer.toString())) {
                                    Toast.makeText(getApplicationContext(), "请至少选择一个农场", Toast.LENGTH_SHORT).show();
                                } else {
                                    send_jb.put("farm", StringBuffer.toString());
                                    MessageMap.put("farm", StringBuffer.toString());
                                    sendToWeb(Path.URL_NEW_USER, send_jb);
                                    Toast.makeText(getApplicationContext(), "增加成功", Toast.LENGTH_SHORT).show();
                                    list.add(MessageMap);
                                    AdapterDataChanged();
                                    dialog.dismiss();

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "邮箱或者手机格式不正确", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "请填写完整信息", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show();
        }

        class ViewHolder {
            Button edit_btn = null;
            Button contact_btn = null;
            Button del_btn = null;
            TextView swipe_list_name;
            TextView swipe_list_juese;
        }

        public void AdapterDataChanged() {
            SwipeAdapter adapter = new SwipeAdapter(mContext);
            adapter.notifyDataSetChanged();
        }

    }

}