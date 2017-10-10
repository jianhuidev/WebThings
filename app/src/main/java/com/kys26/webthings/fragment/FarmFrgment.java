package com.kys26.webthings.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kys26.webthings.adapter.FarmAdapter;
import com.kys26.webthings.httpconstant.Path;
import com.kys26.webthings.interfac.FragInterForUI;
import com.kys26.webthings.main.MainActivity;
import com.zhangyx.MyGestureLock.R;

import butterknife.ButterKnife;

/**
 * Created by kys-36 on 2017/5/6.
 *
 * @param
 * @author
 * @function
 */

public class FarmFrgment extends Fragment implements FragInterForUI {
    /**
     * 标识
     */
    private String TAG;
    private View mView;
    public RecyclerView mGridView;
    /**
     * RecyclerView的构造器
     */
    private FarmAdapter mFarmAdapter;

    /**
     * 下拉刷新监听
     */
    public SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            Log.e(TAG, "下拉刷新");
            ((MainActivity) getActivity()).getGwAndDeviceDate();
            ((MainActivity) getActivity()).doRegist(Path.host + Path.URL_MANAGE_FARM, null, MainActivity.GET_FARM);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        TAG = this.getClass().getName();
        mView = inflater.inflate(R.layout.fragment_farm_new, container, false);
        ((MainActivity) getActivity()).doRegist(Path.host + Path.URL_MANAGE_FARM, null, MainActivity.GET_FARM);
        initView();
        ButterKnife.inject(this, super.onCreateView(inflater, container, savedInstanceState));
        return mView;
    }

    private void initView() {
        mGridView = (RecyclerView) mView.findViewById(R.id.farm_list);
        GridLayoutManager gridManager = new GridLayoutManager(getActivity(), 2);
        mGridView.setLayoutManager(gridManager);
//        mFarmAdapter = new FarmAdapter((MainActivity) getActivity());
//        mGridView.setAdapter(mFarmAdapter);
    }

    @Override
    public void changeUI(MainActivity activity) {
//        mFarmAdapter.notifyDataSetChanged();
        mFarmAdapter = new FarmAdapter((MainActivity) getActivity());
        mGridView.setAdapter(mFarmAdapter);
        ((MainActivity) getActivity()).dismissDialog();
    }
}
