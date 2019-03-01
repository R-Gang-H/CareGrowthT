package com.caregrowtht.app.activity;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.fragment.MyCourseFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-8-2 15:15:43
 * 选择排课班级
 */
public class SchoolWorkActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private MyCourseFragment myCouFrag;

    @Override
    public int getLayoutId() {
        return R.layout.activity_school_work;
    }

    @Override
    public void initView() {
        tvTitle.setText("选择一节课/班级");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        //我的课程
        myCouFrag = new MyCourseFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", 2);//课程类型
        bundle.putInt("cardType", 2);//判断课程卡点击跳转 1:点击课表放大 2:选择排课班级里的成员
        myCouFrag.setArguments(bundle);
        ft.add(R.id.ll_content, myCouFrag);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void initData() {

    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }
}
