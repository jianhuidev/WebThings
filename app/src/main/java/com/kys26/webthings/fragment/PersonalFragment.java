package com.kys26.webthings.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kys26.webthings.dialog.CustomDialog;
import com.kys26.webthings.httpnetworks.MyImageRequest;
import com.kys26.webthings.method.MethodTools;
import com.kys26.webthings.person.SettingActivity;
import com.kys26.webthings.personalcenter.ChangeActivity;
import com.zhangyx.MyGestureLock.R;

import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imkit.RongIM;

/**
 * Created by kys-36 on 2017/5/7.
 *
 * @param
 * @author
 * @function
 */

public class PersonalFragment extends Fragment implements View.OnClickListener {
    private View mView;
    private ConstraintLayout head_layout, setting, about_us, suggestion, exit, customerService;
    public TextView name;
    public CircleImageView head_portrait;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_pesonal_new, container, false);
        initView();
        //new MyImageRequest().getHeadImage(head_portrait, MethodTools.mClientInforData.get(0).getAvatarUrl());
        name.setText(MethodTools.mClientInforData.get(0).getTrueName());
        return mView;
    }

    @Override
    public void onResume() {
        if (MethodTools.mClientInforData.size()>0)
            new MyImageRequest().getHeadImage(head_portrait, MethodTools.mClientInforData.get(0).getAvatarUrl());
        super.onResume();
    }

    /**
     * 初始化view
     */
    private void initView() {
        head_portrait = (CircleImageView) mView.findViewById(R.id.head_portrait);
        name = (TextView) mView.findViewById(R.id.name);
        customerService = (ConstraintLayout) mView.findViewById(R.id.customer_service);
        customerService.setOnClickListener(this);
        head_layout = (ConstraintLayout) mView.findViewById(R.id.head_layout);
        head_layout.setOnClickListener(this);
        setting = (ConstraintLayout) mView.findViewById(R.id.setting);
        setting.setOnClickListener(this);
        about_us = (ConstraintLayout) mView.findViewById(R.id.about_us);
        about_us.setOnClickListener(this);
        suggestion = (ConstraintLayout) mView.findViewById(R.id.suggestion);
        suggestion.setOnClickListener(this);
        exit = (ConstraintLayout) mView.findViewById(R.id.exit);
        exit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_layout:
                Intent intent = new Intent(getActivity(), ChangeActivity.class);
                startActivity(intent);
                break;
            case R.id.customer_service:
                break;
            case R.id.setting:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.about_us:
                break;
            case R.id.suggestion:
                break;
            case R.id.exit:
                ShowExitDialog();
                break;
        }
    }

    /**
     * 显示对话框
     */
    public void ShowExitDialog() {
        CustomDialog dialog = new CustomDialog();
        dialog.setTitle("退出");
        dialog.setTitleSize(18);
        dialog.setTitleColor(Color.WHITE);
        TextView txt = new TextView(getActivity());
        DisplayMetrics dm = getResources().getDisplayMetrics();//获取屏幕宽度
        int txtHeight = (dm.widthPixels) / 4;
        //txt.setHeight(DensityUtil.px2dip(getActivity(), txtHeight));
        txt.setHeight(txtHeight);
        txt.setText("确定要退出应用么?");
        txt.setTextSize(16);
        txt.setGravity(Gravity.CENTER);
        dialog.setView(txt);
        dialog.setOnDialogListener(new CustomDialog.OnDialogListener() {
            @Override
            public void sureListener() {
                RongIM.getInstance().disconnect();
                getActivity().finish();
            }

            @Override
            public void cancelListener() {

            }
        });
        dialog.show(getFragmentManager(), "exit");
    }
}
