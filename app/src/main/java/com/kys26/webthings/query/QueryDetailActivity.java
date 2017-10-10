package com.kys26.webthings.query;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kys26.webthings.adapter.NodeDetailAdapter;
import com.kys26.webthings.main.BaseActivity;
import com.zhangyx.MyGestureLock.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author 窦文
 * @function:实时查询明细
 */
public class QueryDetailActivity extends BaseActivity {


    @InjectView(R.id.node_detail_list)
    RecyclerView mNodeDetailList;
    /**
     * 显示的节点类型  0：空气质量 1：温度 2：湿度 3：光照强度
     */
    private int type;
    /**
     * 保存节点信息
     */
    private List<HashMap<String, Object>> nodeList = new ArrayList<>();
    /**
     * 农场的position
     */
    private int farmPosition;
    private ArrayList<Integer>nodeDataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.inject(this);
        setEnableRefresh(false, null);
        Intent in = getIntent();
        type = in.getIntExtra("type", 0);
        farmPosition = in.getIntExtra("farmPosition", 0);
        nodeDataList=in.getIntegerArrayListExtra("list");
        initRecyclerView();
    }

    @Override
    protected void onClickLeft() {
        super.onClickLeft();
        this.finish();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_node_detail;
    }

    private void initRecyclerView() {
        LinearLayoutManager lm = new LinearLayoutManager(this);
        mNodeDetailList.setLayoutManager(lm);
//        mNodeDetailList.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL));
        NodeDetailAdapter nda = new NodeDetailAdapter(farmPosition, type,nodeDataList);
        mNodeDetailList.setAdapter(nda);
        switch (type) {
            case 0:
                break;
            case 1:
                setTitle("温度明细");
                break;
            case 2:
                setTitle("湿度明细");
                break;
            case 3:
                setTitle("光照明细");
                break;
        }
    }
}
