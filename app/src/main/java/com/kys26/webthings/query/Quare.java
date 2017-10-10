package com.kys26.webthings.query;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.util.MyToast;
import com.zhangyx.MyGestureLock.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 *
  *com.kys26.webthings.query.query
 * @author Admin-徐建强 create at 2015-7-16 下午1:56:18
 * @function:实时查询农场信息
 */
public class Quare extends AbActivity {

    /**农场列表信息添加List*/
    private ListView framListView;
    /**农场列表信息添加适配器*/
    private SimpleAdapter listItemAdapter;
    /**农场列表装载ArrayList*/
    private List<HashMap<String, Object>> listItems;
    /**输出标识*/
    private String TAG="com.kys26.webthings.query.query";
    /**设置一个返回值，用于下拉刷新判断*/
     boolean state=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_quare);

        //初始化界面
        initView();
        setFramList();
    }
    /**
     * @author Admin-徐建强 create at 2015-7-16 下午1:56:18
     * @function:初始化UI组件
     * @return null
     */
    private  void initView(){
        this.setTitleText("农场管理");
        this.setTitleTextSize(20);
        this.titleTextBtn.setTextColor(Color.BLACK);
//        this.setTitleTextMargin(BaseActivity.marginToLeftTitleNormal, 0, 0, 0);
        this.setLogo(R.drawable.button_selector_back);
        this.setTitleLayoutBackground(R.drawable.bg_top);
        this.setLogoLine(R.drawable.line);
        //下拉刷新自定义控件
//        ((PullToRefreshLayout) findViewById(R.id.refresh_view))
//                .setOnRefreshListener(new MyListenerQuare());

        //农场listview控件
        framListView=(ListView)findViewById(R.id.node_view);
    }

    /***
     * @function:初始化农场列表
     * @return null
     */
    private void setFramList() {
        //List适配器
        if (MethodTools.listdata_farm!=null) {
            listItems = MethodTools.listdata_farm;
        }else {
            listItems = new ArrayList<HashMap<String, Object>>();
            MyToast.makeTextToast(getApplicationContext(),"请求超时", Toast.LENGTH_SHORT, Gravity.BOTTOM,120).show();
        }

        // listItems数据源
        listItemAdapter = new SimpleAdapter(Quare.this, (ArrayList<? extends Map<String, ?>>) listItems,
                //ListItem的XML布局实现
                R.layout.list_item_layout,
                //动态数组与ImageItem对应的子项
                new String[]{"farm_name", "ItemImage"},
                new int[]{R.id.tv, R.id.listImage});
        //设置设配器
        framListView.setAdapter(listItemAdapter);
        //为列表设置监听
        framListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent it = new Intent();
                it.setClass(Quare.this,
                        QueryDetailActivity.class);
                it.putExtra("position",position);
                startActivity(it);
            }
        });
        }
}

