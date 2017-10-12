package com.kys26.webthings.personalcenter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kys26.webthings.adapter.NoticeListAdapter;
import com.kys26.webthings.main.BaseActivity;
import com.zhangyx.MyGestureLock.R;

/**
 * Created by kys_8 on 2017/9/23.
 * 推送消息活动
 */
public class NoticeActivity extends BaseActivity {

    /**存储推送来的消息*/
    private SharedPreferences push_pref;
    private SharedPreferences.Editor push_editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Data.position = Data.noticeList.size();
        initView();
    }

    /**
     * 初始化控件
     * 通过多个判断（就是用户点击进入的各种情况，如推送信息是否为空，是否相同）
     */
    private void initView(){
        setTitle("推送信息");
        push_pref = getSharedPreferences("pushMsg",MODE_PRIVATE);
        Log.e("Data.pushMsg",Data.pushMsg);
        //Log.e("push_pref",push_pref.getString("0","0")+"+++++"+ Data.pushMsg);
        if (Data.position ==0 && push_pref.getString("0","0").equals("0")
                && TextUtils.isEmpty(Data.pushMsg)){

        }else if ( Data.position ==0 && !(TextUtils.isEmpty(Data.pushMsg)) ){

            Data.noticeList.add(Data.pushMsg);
            Data.position  = Data.noticeList.size();
        }else{
            Data.noticeList.clear();

            /**如果存储的消息，则先将存储的内容取出来*/
            if (!push_pref.getString("0","0").equals("0")){
                for (int a=0;a<push_pref.getInt("a",0) ;a++){
                    Data.noticeList.add(push_pref.getString(""+a,""));
                }
                Data.position = Data.noticeList.size();
            }

            if (Data.noticeList.size()>0)
            {
                /**如果最后一条推送和刚来的推送消息相同，则不添加*/
                if ( !(Data.noticeList.get(Data.noticeList.size() -1).equals(Data.pushMsg))
                        &&!TextUtils.isEmpty(Data.pushMsg)){
                    Data.noticeList.add(Data.pushMsg);
                }
                /**刷新通知的个数*/
                Data.position  = Data.noticeList.size();
            }
            /**防止If  语句没执行，刷新通知的个数*/
            Data.position  = Data.noticeList.size();
        }
//        addLeftWidget(Data.noticeList.size());
        //Log.e("LeftNum", Data.noticeList.size()+"");
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        final NoticeListAdapter adapter = new NoticeListAdapter(this, Data.noticeList);
        //Log.e("noticeList", Data.position+"");
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new NoticeListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int p = position + 1;
                Toast.makeText(NoticeActivity.this,"点击第"+p+" 个",Toast.LENGTH_SHORT).show();
            }
        });


    }

    /**
     * @return 设置的布局
     */
    @Override
    protected int getContentView() {
        return R.layout.notice_layout;
    }

    /**
     * 左上角的图标（返回）的点击
     * 点击销毁当前活动
     */
    @Override
    protected void onClickLeft() {
        super.onClickLeft();
        //addLeftWidget(Data.noticeList.size());
        finish();

    }

    /**
     * 复写销毁活动的生命周期函数
     * 1.先清除SharedPreferences 里存储的数据
     * 再把活动销毁前的list 里的数据通过for 循环存储
     */
    @Override
    protected void onDestroy() {
        push_pref.edit().clear().apply();
        if (Data.position  != 0){
            push_editor = push_pref.edit();
            int a;
            for (a = 0; a < Data.position; a++){
                if (Data.position !=0){
                    push_editor.putString(""+a, (String) Data.noticeList.get(a));
                    //Data.x = a+1;
                }
            }
            /**存储通知的个数*/
            push_editor.putInt("a",a);
            push_editor.apply();
        }
//        Log.e("ondestrory","zxl");
        super.onDestroy();
    }
}
