package com.kys26.webthings.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kys26.webthings.adapter.NodeAdapter;
import com.kys26.webthings.adapter.RecyclerViewManage.HorizontalPageLayoutManager;
import com.kys26.webthings.adapter.RecyclerViewManage.PagingItemDecoration;
import com.kys26.webthings.adapter.RecyclerViewManage.PagingScrollHelper;
import com.kys26.webthings.command.CommandActivity;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.interfac.FragInterForUI;
import com.kys26.webthings.main.MainActivity;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.model.NodeDeviceData;
import com.kys26.webthings.query.QueryDetailActivity;
import com.kys26.webthings.util.DataUtil;
import com.zhangyx.MyGestureLock.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.zhangyx.MyGestureLock.R.id.hum;

/**
 * Created by kys-36 on 2017/5/7.
 *
 * @param
 * @author
 * @function
 */

public class GWFragment extends Fragment implements PagingScrollHelper.onPageChangeListener, FragInterForUI {
    @InjectView(R.id.pm)
    TextView mPm;
    @InjectView(hum)
    TextView mHum;
    @InjectView(R.id.temp)
    TextView mTemp;
    @InjectView(R.id.sunpower)
    TextView mSunpower;
    @InjectView(R.id.date)
    TextView mDate;
    @InjectView(R.id.week)
    TextView mWeek;
    private View mView, dot1, dot2;
    private ViewPager mViewPager;
    private List<View> views = new ArrayList<>();
    private LinearLayout mDotLl;
    private View mRedDot;
    private Button mStartBtn;
    private int mDotSpacing; //2个指示点间的距离
    public int parentPosition = 0;
    public static int[] image = new int[]{R.drawable.yzc_icon_video, R.drawable.yzc_icon_fj, R.drawable.cooling, R.drawable.yzc_icon_ws, R.drawable.yzc_icon_cf,
            R.drawable.yzc_icon_dg, R.drawable.yzc_jw, R.drawable.yzc_icon_sl,
            R.drawable.yzc_icon_dg, R.drawable.yzc_icon_sj};
    public static String[] names = new String[]{"监控", "通风", "降温", "喂食", "除粪",
            "照明", "加温", "加湿", "补光", "杀菌"};
    public static int[] gifView = new int[]{R.drawable.yzc_icon_video, R.drawable.yzc_icon_fj, R.drawable.cooling,
            R.drawable.yzc_icon_feed, R.drawable.yzc_icon_clear, R.drawable.yzc_icon_light,
            R.drawable.yzc_jw, R.drawable.yzc_icon_sl, R.drawable.yzc_icon_dg, R.drawable.yzc_icon_sterilization};
    /**
     * 下拉刷新监听
     */
    public SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            Log.e(TAG, "下拉刷新");
            JSONObject jb = new JSONObject();
            try {
                jb.put("farmid", MethodTools.farmDataList.get(((MainActivity) getActivity()).position).getFarm_id());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ((MainActivity) getActivity()).doRegist(Path.host + Path.URL_GET_DEVICE, jb, MainActivity.GET_DEVICE);
        }
    };
    /**
     * 保存节点类型的List
     */
    private List<HashMap<String, Object>> list = new ArrayList<>();

    /**
     * 实现RecyclerView分页滚动的工具类
     */
    private PagingScrollHelper scrollHelper = new PagingScrollHelper();
    private String TAG = "GWFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_gw_new, container, false);
        ButterKnife.inject(this, mView);
        initRecycler();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            changeUI(((MainActivity) getActivity()));
        } catch (Exception e) {

        }
    }

    /**
     * 初始化列表
     */
    private void initList() {
        for (int i = 0; i < names.length; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", names[i]);
            map.put("image", image[i]);
            list.add(map);
        }
    }

    /**
     * @param i 页数
     * @return 返回第一页和第二页的LIST
     */
    private List<HashMap<String, Object>> getList(int i) {
        List<HashMap<String, Object>> mlist = new ArrayList<>();
        switch (i) {
            case 1:
                for (int j = 0; j < 6; j++) {
                    mlist.add(list.get(j));
                }
                break;
            case 2:
                for (int j = 6; j < list.size(); j++) {
                    mlist.add(list.get(j));
                }
                break;
            default:
                return null;
        }
        return mlist;
    }

    /**
     * 返回Viewpager的一页
     *
     * @return
     */
    private void initRecycler() {
        dot1 = mView.findViewById(R.id.view_dot1);
        dot2 = mView.findViewById(R.id.view_dot2);
        dot1.setSelected(true);
        initList();
        initTime();
        RecyclerView rv = (RecyclerView) mView.findViewById(R.id.node_list);
        final NodeAdapter na = new NodeAdapter(((MainActivity) getActivity()), list);
        rv.setAdapter(na);
        scrollHelper.setUpRecycleView(rv);
        scrollHelper.setOnPageChangeListener(this);
        HorizontalPageLayoutManager horizontalPageLayoutManager = new HorizontalPageLayoutManager(2, 3);
        PagingItemDecoration pagingItemDecoration = new PagingItemDecoration(getActivity(), horizontalPageLayoutManager);
        rv.setLayoutManager(horizontalPageLayoutManager);
        rv.addItemDecoration(pagingItemDecoration);
        scrollHelper.updateLayoutManger();//更新RecyclerView
        na.setOnClickListener(new NodeAdapter.onClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getActivity(), CommandActivity.class);
                intent.putExtra("type", position);
                intent.putExtra("position", ((MainActivity) getActivity()).position);
                switch (position) {
                    case 0:
                        if (((MainActivity) getActivity()).video) {
                            startActivity(intent);
                        } else {
                            getToast();
                        }
                        break;
                    case 1:
                        if (((MainActivity) getActivity()).wind) {
                            startActivity(intent);
                        } else {
                            getToast();
                        }
                        break;
                    case 2:
                        if (((MainActivity) getActivity()).cool) {
                            startActivity(intent);
                        } else {
                            getToast();
                        }
                        break;
                    case 3:
                        if (((MainActivity) getActivity()).feed) {
                            startActivity(intent);
                        } else {
                            getToast();
                        }
                        break;
                    case 4:
                        if (((MainActivity) getActivity()).clear) {
                            startActivity(intent);
                        } else {
                            getToast();
                        }
                        break;
                    case 5:
                        if (((MainActivity) getActivity()).light) {
                            startActivity(intent);
                        } else {
                            getToast();
                        }
                        break;
                    case 6:
                        if (((MainActivity) getActivity()).heat) {
                            startActivity(intent);
                        } else {
                            getToast();
                        }
                        break;
                    case 7:
                        if (((MainActivity) getActivity()).hum) {
                            startActivity(intent);
                        } else {
                            getToast();
                        }
                        break;
                    case 8:
                        if (((MainActivity) getActivity()).fillLight) {
                            startActivity(intent);
                        } else {
                            getToast();
                        }
                        break;
                    case 9:
                        if (((MainActivity) getActivity()).sterilization) {
                            startActivity(intent);
                        } else {
                            getToast();
                        }
                        break;
                }
            }
        });
    }

    private void getToast() {
        Toast.makeText(getActivity(), "该类节点您需要购买", Toast.LENGTH_SHORT).show();
    }

    /**
     * 初始化时间
     */
    private void initTime() {
        mDate.setText(DataUtil.getCurrentDate());
        mWeek.setText(DataUtil.getWeek(DataUtil.getCurrentDate()));
    }


    @Override
    public void onPageChange(int index) {
        switch (index) {
            case 0:
                dot1.setSelected(true);
                dot2.setSelected(false);
                break;
            case 1:
                dot1.setSelected(false);
                dot2.setSelected(true);
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            try {
                changeUI(((MainActivity) getActivity()));
            } catch (Exception e) {
                Log.w(TAG, "初始化界面异常" + e.toString());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick({R.id.pm, R.id.hum_btn, R.id.temp_btn, R.id.sunpower_btn})
    public void onClick(View view) {
        List<NodeDeviceData> nodeDeviceDatas = MethodTools.farmDataList
                .get(parentPosition).getNodeDeviceList();
        switch (view.getId()) {
            case R.id.pm:
                ArrayList<Integer> NH3 = new ArrayList<>();
                for (int i = 0; i < nodeDeviceDatas.size(); i++) {
                    if (null != nodeDeviceDatas.get(i).getNH3()) {
                        Log.i(TAG, "PM:" + nodeDeviceDatas.get(i).getNH3());
                        NH3.add(Math.round(Integer.decode(nodeDeviceDatas.get(i).getNH3()) / 100f));
                    }
                }
                if (NH3.size() > 0) {
                    startIntentToQueryNode(0, NH3);
                } else {
                    getToast();
                }
                break;
            case R.id.hum_btn:
                ArrayList<Integer> hum = new ArrayList<>();
                for (int i = 0; i < nodeDeviceDatas.size(); i++) {
                    if (null != nodeDeviceDatas.get(i).getShidu()) {
                        Log.i(TAG, "PM:" + nodeDeviceDatas.get(i).getShidu());
                        hum.add(Math.round(Integer.decode(nodeDeviceDatas.get(i).getShidu()) / 100f));
                    }
                }
                if (hum.size() > 0) {
                    startIntentToQueryNode(2, hum);
                } else {
                    getToast();
                }
                break;
            case R.id.temp_btn:
                ArrayList<Integer> temp = new ArrayList<>();
                for (int i = 0; i < nodeDeviceDatas.size(); i++) {
                    if (null != nodeDeviceDatas.get(i).getWendu()) {
                        Log.i(TAG, "PM:" + nodeDeviceDatas.get(i).getWendu());
                        temp.add(Math.round(Integer.decode(nodeDeviceDatas.get(i).getWendu()) / 100f));
                    }
                }
                if (temp.size() > 0) {
                    startIntentToQueryNode(1, temp);
                } else {
                    getToast();
                }
                break;
            case R.id.sunpower_btn:
//                ArrayList<Integer> sunPower = new ArrayList<>();
//                for (int i = 0; i < nodeDeviceDatas.size(); i++) {
//                    if (null != nodeDeviceDatas.get(i).get()) {
//                        Log.i(TAG, "PM:" + nodeDeviceDatas.get(i).getWendu());
//                        sunPower.add(Math.round(Integer.decode(nodeDeviceDatas.get(i).getWendu()) / 100f));
//                    }
//                }
//                if (sunPower.size() > 0) {
//                    startIntentToQueryNode(3, sunPower);
//                } else {
                getToast();
//                }
                break;
        }
    }

    /**
     * 跳转到查询节点
     */
    private void startIntentToQueryNode(int type, ArrayList<Integer> list) {
        Intent intent = new Intent(getActivity(), QueryDetailActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("farmPosition", ((MainActivity) getActivity()).position);
        intent.putIntegerArrayListExtra("list", list);
        startActivity(intent);
    }

    public void changeUI(MainActivity activity) {
        if (!activity.mGwfragment.isHidden()) {
            activity.setTitle(MethodTools.farmDataList.get(activity.position).getFarm_name());
            List<NodeDeviceData> nodeDeviceDatas = MethodTools.farmDataList
                    .get(activity.position).getNodeDeviceList();
            Log.i(TAG, "position:" + nodeDeviceDatas.size());
            List<Integer> temp = new ArrayList<>();
            List<Integer> hum = new ArrayList<>();
            List<Integer> NH3 = new ArrayList<>();
            List<Integer> sunPower = new ArrayList<>();
            if (mTemp != null) {
                for (int i = 0; i < nodeDeviceDatas.size(); i++) {
                    Log.i(TAG, "position:" + mTemp);
                    if (null != nodeDeviceDatas.get(i).getWendu()) {//控件判断一个就行
                        temp.add(Math.round(Integer.decode(nodeDeviceDatas.get(i).getWendu()) / 100f));
                        Log.i(TAG, "TEMP:" + nodeDeviceDatas.get(i).getWendu());
                    }
                    if (null != nodeDeviceDatas.get(i).getShidu()) {
                        Log.i(TAG, "Hum:" + nodeDeviceDatas.get(i).getShidu());
                        hum.add(Math.round(Integer.decode(nodeDeviceDatas.get(i).getShidu()) / 100f));
                    }
                    if (null != nodeDeviceDatas.get(i).getNH3()) {
                        Log.i(TAG, "PM:" + nodeDeviceDatas.get(i).getNH3());
                        NH3.add(Math.round(Integer.decode(nodeDeviceDatas.get(i).getNH3()) / 100f));
                    }
                }
                mTemp.setText(getAverage(temp));
                mHum.setText(getAverage(hum));
                mPm.setText(getAverage(NH3));
                if (Integer.decode(getAverage(NH3)) <= 15) {
                    activity.changeBackground(0);
                } else if (Integer.decode(getAverage(NH3)) <= 20) {
                    activity.changeBackground(1);
                } else if (Integer.decode(getAverage(NH3)) <= 30) {
                    activity.changeBackground(2);
                } else if (Integer.decode(getAverage(NH3)) > 30) {
                    activity.changeBackground(3);
                }
            }
        }
    }

    private String getAverage(List<Integer> temp) {
        int average = 0;
        Log.i(TAG, "size:" + temp.size());
        for (int i = 0; i < temp.size(); i++) {
            average += temp.get(i);
        }
        return temp.size() != 0 ? "" + Math.round(average / temp.size()) : "0";
    }

}
