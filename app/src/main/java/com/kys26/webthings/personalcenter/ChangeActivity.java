package com.kys26.webthings.personalcenter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.httpnetworks.CustomImgRequest;
import com.kys26.webthings.httpnetworks.MyImageRequest;
import com.kys26.webthings.main.BaseActivity;
import com.kys26.webthings.main.MainActivity;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.util.BitmapToRoundUtil;
import com.kys26.webthings.util.StringUtil;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.kys26.webthings.method.MethodTools.sPreFerCookie;

/**
 * 更改信息的活动
 * Created by kys_8 on 2017/9/23.
 */
public class ChangeActivity extends BaseActivity implements View.OnTouchListener {

    private EditText edit_name, edit_phone, edit_email;
    private FloatingActionButton fab;
    private final static int CAMERA_RESULT = 0;//声明一个常量，代表结果码
    private final static int ALBUM = 1;
    private Bitmap mBitmap;
    private Handler handler = new Handler();
    private String filePath, fileName;

    //    private final int UPDATA_CLIENT = 29;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnableRefresh(false, null);
        setTitle("更改信息");
        initView();
        edit_name.setText(MethodTools.mClientInforData.get(0).getTrueName());
        edit_phone.setText(MethodTools.mClientInforData.get(0).getContactPhone());
        edit_email.setText(MethodTools.mClientInforData.get(0).getEmail());
    }

    /**
     * 初始化控件
     */
    private void initView() {
        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_phone = (EditText) findViewById(R.id.edit_phone);
        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_name.setOnTouchListener(this);
        edit_phone.setOnTouchListener(this);
        edit_email.setOnTouchListener(this);
        fab = (FloatingActionButton) findViewById(R.id.float_btn);
        new MyImageRequest().getHeadImage(fab, MethodTools.mClientInforData.get(0).getAvatarUrl());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose();
            }
        });
    }

    /**
     * 设置布局
     *
     * @return 布局的引用
     */
    @Override
    protected int getContentView() {
        return R.layout.activity_change;
    }

    /**
     * 左上角的返回键的设置
     */
    @Override
    protected void onClickLeft() {
        super.onClickLeft();
        sendWebupData();
//        Log.e("CLIENT=-**", MethodTools.mClientInforData.get(0).getTrueName() + "**" + edit_name.getText().toString());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                finish();
//            }
//        },1000);
        finish();
//        Log.v("edit",edit_name.getText().toString()+"*"+edit_phone.getText().toString()+"*"+
//                edit_email.getText().toString());
    }

    /**
     * 弹出底部对话框
     */
    private void choose() {
        final Dialog dialog = new Dialog(ChangeActivity.this);
        //去掉title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        Window window = dialog.getWindow();
        // 设置布局
        window.setContentView(R.layout.change_window);
        // 设置宽高
        window.setLayout(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        // 设置弹出的动画效果
        window.setWindowAnimations(R.style.AnimBottom);
        window.setGravity(Gravity.BOTTOM);
        LinearLayout photo = (LinearLayout) window.findViewById(R.id.photo);
        LinearLayout album = (LinearLayout) window.findViewById(R.id.album);
        LinearLayout cancel = (LinearLayout) window.findViewById(R.id.the_cancel);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ChangeActivity.this,"1",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_RESULT);
                dialog.cancel();
            }
        });

        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ChangeActivity.this,"2",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, ALBUM);
                dialog.cancel();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ChangeActivity.this,"3",Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        // 因为用的是windows的方法，所以不管ok活cancel都要加上“dialog.cancel()”这句话，
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_RESULT:
                photo(resultCode, data);
                break;
            case ALBUM:
                album(resultCode, data);
                break;
        }

    }


    /**
     * 发送web请求,更改信息
     */
    public void sendWebupData() {
        if (MethodTools.mClientInforData.size() > 0) {
            JSONObject jb = new JSONObject();
            try {
                jb.put("trueName", edit_name.getText().toString());
                jb.put("sex", MethodTools.mClientInforData.get(0).getSex());
                jb.put("qq", MethodTools.mClientInforData.get(0).getQq());
                jb.put("workingAddress", MethodTools.mClientInforData.get(0).getWorkingAddress());
                jb.put("contactPhone", edit_phone.getText().toString());
                jb.put("nickName", MethodTools.mClientInforData.get(0).getTrueName());
                //jb.put("avatarUrl",);
                //jb.put("avatarUrl",getBitmapByte(mBitmap).toString());
                //jb.put("avatarUrl","http://192.168.87.59:8080/avatar/18131371669.png");
                //b.put("avatarUrl","https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E5%B0%8F%E8%88%AA%E4%BA%BA&step_word=&hs=0&pn=2&spn=0&di=43450302501&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=2&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=-1&cs=245313896%2C594439050&os=2948934429%2C813497859&simid=3193926042%2C3700139904&adpicid=0&lpn=0&ln=1982&fr=&fmq=1506430445974_R&fm=&ic=0&s=undefined&se=&sme=&tab=0&width=&height=&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=http%3A%2F%2Fwww.xiazaiba.com%2Fuploadfiles%2Fsub_ico%2F2015%2F0311%2F2015031115293257602.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3F4_z%26e3Bxtwzwtkw_z%26e3Bv54AzdH3FtAzdH3F2w4jAzdH3Fnllb_z%26e3Bip4s&gsm=0&rpstart=0&rpnum=0");
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            if (StringUtil.isEmpty(filePath) || StringUtil.isEmpty(fileName))
            doRegist(Path.host + Path.URL_UPDATA_CLIENT, jb, MainActivity.UPDATA_CLIENT);
//            else
            if (!StringUtil.isEmpty(filePath))
                RequestWebData(Path.host + Path.URL_UPDATA_CLIENTIMG);

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (v.getId()) {
            case R.id.edit_name:
                edit_name.setCursorVisible(true);// 再次点击显示光标
                break;
            case R.id.edit_phone:
                edit_phone.setCursorVisible(true);// 再次点击显示光标
                break;
            case R.id.edit_email:
                edit_email.setCursorVisible(true);// 再次点击显示光标
                break;
        }
        return false;
    }


    /**
     * 调用相机进行拍照，设置为头像
     *
     * @param resultCode
     * @param data
     */
    private void photo(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                Log.i("TestFile", "SD card is not avaiable/writeable right now.");
                return;
            }
            // 获取内置SD卡路径
            String sdCardPath = Environment.getExternalStorageDirectory().getPath();
            String cutTime = new SimpleDateFormat("MM-dd-HH-mm-ss").format(new Date());
            filePath = sdCardPath + File.separator + "相机" + File.separator + cutTime + "avater.jpg";
            fileName = "avater.jpg";
            Bundle bundle = data.getExtras();
            mBitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
            FileOutputStream os = null;
            File file = new File(filePath);
            try {
                os = new FileOutputStream(file);
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);// 把数据写入文件
                /**将截图保存至相册并广播通知系统刷新*/
                MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), "title", "description");
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file));
                sendBroadcast(intent);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            BitmapToRoundUtil toRound = new BitmapToRoundUtil();
            mBitmap = toRound.toRoundBitmap(mBitmap);
            fab.setImageBitmap(mBitmap);
        }
    }

    /**
     * 从相册选择照片，设置为头像
     *
     * @param resultCode
     * @param data
     */
    private void album(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {//从相册选择照片
            try {
                Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePath = cursor.getString(columnIndex);  //获取照片路径
                fileName = new File(filePath).getName();//获取照片名字
                cursor.close();
                mBitmap = BitmapFactory.decodeFile(filePath);
                BitmapToRoundUtil toRound = new BitmapToRoundUtil();
                mBitmap = toRound.toRoundBitmap(mBitmap);
                fab.setImageBitmap(mBitmap);
                getBitmapByte(mBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] getBitmapByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //参数1转换类型，参数2压缩质量，参数3字节流资源
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Log.e("out", out.toByteArray().toString());
        return out.toByteArray();
    }

    //    public void load(Bitmap photodata){
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//            //将bitmap一字节流输出 Bitmap.CompressFormat.PNG 压缩格式，100：压缩率，baos：字节流
//            photodata.compress(Bitmap.CompressFormat.PNG, 100, baos);
//            baos.close();
//            byte[] buffer = baos.toByteArray();
//            Log.e("图片的大小：",buffer.length+"");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    private void RequestWebData(final String url) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                sPreFerCookie = getSharedPreferences("Cookies",
                        Context.MODE_PRIVATE);
                CustomImgRequest.uploadFile(new File(filePath), url, null, fileName, sPreFerCookie.getString("cookie", "null"));
            }
        }.start();
//        RequestQueue queue = Volley.newRequestQueue(this);
//        MultipartRequest multipartRequest = new MultipartRequest(
//                url, new Response.Listener<String>() {
//
//            @Override
//            public void onResponse(String response) {
//                Log.e("", "### response : " + response);
//            }
//
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                 Log.e("", "### ErrorResponse : " + volleyError);
//            }
//        });
//        sPreFerCookie = getSharedPreferences("Cookies",
//                Context.MODE_PRIVATE);
//        // 添加header
//        multipartRequest.addHeader("Cookie", sPreFerCookie.getString("cookie", "null"));
//
//        MultipartEntity multi = new MultipartEntity();
//        // 文本参数
//        try {
//            multi.addPart("trueName", new StringBody(edit_name.getText().toString(), Charset.forName("UTF-8")));
//            multi.addPart("sex", new StringBody(MethodTools.mClientInforData.get(0).getSex(), Charset.forName("UTF-8")));
//            multi.addPart("qq", new StringBody(MethodTools.mClientInforData.get(0).getQq(), Charset.forName("UTF-8")));
//            multi.addPart("workingAddress", new StringBody(MethodTools.mClientInforData.get(0).toString(), Charset.forName("UTF-8")));
//            multi.addPart("contactPhone", new StringBody(edit_phone.getText().toString(), Charset.forName("UTF-8")));
//            multi.addPart("nickName", new StringBody(MethodTools.mClientInforData.get(0).toString(), Charset.forName("UTF-8")));
//            multi.addPart("avatarUrl", new FileBody(new File(filePath)));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        queue.add(multipartRequest);
        //发起请求
    }

    /**
     * 保存文件
     *
     * @param bm
     * @param fileName
     * @throws IOException
     */
    public String saveFile(Bitmap bm, String fileName) throws IOException {
        String path = getSDPath() + "/webThings/";
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
//        if (myCaptureFile.exists()) {
//            myCaptureFile.delete();
//        }
        File myCaptureFile = new File(path + fileName);
        if (!myCaptureFile.exists())
            myCaptureFile.createNewFile();
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        return myCaptureFile.getPath();
    }

    /**
     * @return
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

}
