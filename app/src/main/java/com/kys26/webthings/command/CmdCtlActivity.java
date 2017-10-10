package com.kys26.webthings.command;

//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ListView;
//import android.widget.SimpleAdapter;
//import android.widget.Toast;
//
//import com.ab.activity.AbActivity;
//import com.kys26.webthings.main.BaseActivity;
//import com.kys26.webthings.method.MethodTools;
//import com.kys26.webthings.pullableview.PullToRefreshLayout;
//import com.kys26.webthings.util.MyToast;
//import com.zhangyx.MyGestureLock.R;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
///**
// * Created by kys_9 on 2016/9/15.
// */
//public class CmdCtlActivity extends AbActivity {
//    /**
//     * 农场列表信息添加List
//     */
//    private ListView framListView;
//    /**
//     * 农场列表信息添加适配器
//     */
//    private SimpleAdapter listItemAdapter;
//    /**
//     * 农场列表装载ArrayList
//     */
//    private List<HashMap<String, Object>> listItems;
//    /**
//     * 输出标识
//     */
//    private String TAG = "com.kys26.webthings.command.CmdCtlActivity";
//    /**
//     * 设置一个返回值，用于下拉刷新判断
//     */
//    boolean state = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setAbContentView(R.layout.activity_cmdctl);
//        //初始化界面
//        initView();
//    }
//
//    /**
//     * @return null
//     * @author Admin-李赛鹏 create at 2016-9-15 上午11:51:18
//     * @function:初始化UI组件
//     */
//    private void initView() {
//        this.setTitleText("命令控制");
//        this.setTitleTextSize(20);
//        this.titleTextBtn.setTextColor(Color.BLACK);
//        this.setTitleTextMargin(BaseActivity.marginToLeftTitleNormal, 0, 0, 0);
//        this.setLogo(R.drawable.button_selector_back);
//        this.setTitleLayoutBackground(R.drawable.bg_top);
//        this.setLogoLine(R.drawable.line);
//        //下拉刷新自定义控件
//        ((PullToRefreshLayout) findViewById(R.id.refresh_view))
//                .setOnRefreshListener(new MyListenerQuare());
//        //农场listview控件
//        framListView = (ListView) findViewById(R.id.node_view);
//        //初始化农场列表
//        setFramList();
//    }
//
//
//    /***
//     *
//     * @return null
//     * @author Admin-李赛鹏 create at 2016-9-15 下午12:01:18
//     * @Editor kys_36窦文 create at 2016-9-16 下午16:22:18
//     * @function:初始化农场列表
//     */
//    private void setFramList() {
//        //List适配器
//        if (MethodTools.listdata_farm!=null) {
//            listItems = MethodTools.listdata_farm;
//        }else {
//            listItems = new ArrayList<HashMap<String, Object>>();
//            MyToast.makeTextToast(getApplicationContext(),"请求超时", Toast.LENGTH_SHORT, Gravity.BOTTOM,120).show();
//        }
//
//        // listItems数据源
//        listItemAdapter = new SimpleAdapter(this, (ArrayList<? extends Map<String, ?>>) listItems,
//                //ListItem的XML布局实现
//                R.layout.list_item_layout,
//                //动态数组与ImageItem对应的子项
//                new String[]{"farm_name", "ItemImage"},
//                new int[]{R.id.tv, R.id.listImage});
//        //设置设配器
//        framListView.setAdapter(listItemAdapter);
//        //为列表设置监听
//        framListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                Intent it = new Intent();
//                it.setClass(CmdCtlActivity.this,
//                        CmdCtlDetailActivity.class);
//                it.putExtra("position",position);
//                startActivity(it);
//            }
//        });
//    }
//
//    /***
//     * @author Admin-李赛鹏create at 2016-9-15 上午11:55:18
//     * @function:匿名内部类监听下拉手势进行刷新
//     * @return null
//     */
//    public class MyListenerQuare implements PullToRefreshLayout.OnRefreshListener {
//        public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
//            // 下拉刷新操作
//            new Handler() {
//                @Override
//                public void handleMessage(Message msg) {
//                    if (MethodTools.state) {
//                        //告诉控件刷新完毕
//                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
//                    } else {
//                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
//                    }
//                }
//            }.sendEmptyMessageDelayed(0, 1000);
//        }
//
//        @Override
//        public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
//            // 加载操作
//            new Handler() {
//                @Override
//                public void handleMessage(Message msg) {
//                    if (MethodTools.state) {
//                        //告诉控件刷新完毕
//                        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
//                    } else {
//                        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
//                    }
//                }
//            }.sendEmptyMessageDelayed(0, 1000);
//        }
//    }
//}
