package com.caregrowtht.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.JoinOrgActivity;
import com.caregrowtht.app.adapter.OrgFragmentAdapter;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.viewpagerindicator.CirclePageIndicator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 首页
 * Update by haoruigang on 2018-4-23 10:43:03
 */

public class HomeFragment extends BaseFragment {

    @BindView(R.id.rl_exist_org)
    RelativeLayout rlExistOrg;
    @BindView(R.id.rl_no_org)
    RelativeLayout rlNoOrg;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.indicator)
    CirclePageIndicator mIndicator;

    private ArrayList<TeacherHomeFragment> fragmentList = new ArrayList<>();
    private OrgFragmentAdapter fragmentAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void initView(View view, Bundle savedInstanceState) {
    }

    @Override
    public void initData() {
        UserManager.getInstance().getRoleEntityList(null).clear();
        UserManager.getInstance().getOrgEntityList(null).clear();
        if (!StrUtils.isEmpty(UserManager.getInstance().userData.getOrgIds())) {
            String[] teacherOrgs = UserManager.getInstance().userData.getOrgIds().split(",");//分隔教师机构id
            switchOrgLayout(0 < teacherOrgs.length, teacherOrgs);
        } else {
            switchOrgLayout(false, null);
        }
    }

    private void switchOrgLayout(boolean isExist, String[] teacherOrgs) {
        rlExistOrg.setVisibility(isExist ? View.VISIBLE : View.GONE);
        rlNoOrg.setVisibility(isExist ? View.GONE : View.VISIBLE);
        if (isExist) {
            fragmentList.clear();
            mIndicator.setVisibility(teacherOrgs.length > 1 ? View.VISIBLE : View.GONE);
            for (String teacherOrd : teacherOrgs) {
                fragmentList.add(createFragments(teacherOrd));
            }
            UserManager.getInstance().setOrgId(teacherOrgs[0]);//默认第一个
            if (fragmentAdapter == null) {
                fragmentAdapter = new OrgFragmentAdapter(getFragmentManager(), fragmentList);
            } else {
                // 刷新fragment
                fragmentAdapter.setFragments(getFragmentManager(), fragmentList);
            }
            viewPager.setAdapter(fragmentAdapter);
            mIndicator.setViewPager(viewPager);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    //重新同步获取Data
                    UserManager instance = UserManager.getInstance();
                    String[] teacherOrgs = instance.userData.getOrgIds().split(",");//分隔教师机构id
                    String orgId = teacherOrgs[position];
                    UserManager.getInstance().setOrgId(orgId);
                    U.savePreferences("orgId", orgId);
                    // 教务老师和管理者，进去教师端默认是机构课表，教学老师默认是我的课表 setOrgid 必须在前面
                    if (instance.getOrgEntity() != null) {
                        String identity = instance.getOrgEntity().getIdentity();
                        LogUtils.e("HomeFragment", identity + "orgEntity:" + instance.getOrgEntity());
                        if (!TextUtils.isEmpty(identity)) {
                            int type = 1;
                            if (identity.contains("教务") || identity.contains("管理")) {
                                type = 2;
                            }
                            EventBus.getDefault().post(new ToUIEvent(ToUIEvent.COURSE_TYPE, type));
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

    /**
     * Create fragment dynamically.
     * <p>
     * //     * @param teacherOrd
     *
     * @return
     */
    private TeacherHomeFragment createFragments(String teacherOrd) {
        //已添加孩子主页
        TeacherHomeFragment fragment = new TeacherHomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("orgId", teacherOrd);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        switch (event.getWhat()) {
            case ToUIEvent.TEACHER_HOME_EVENT:
                initData();
                break;
        }
    }

    @OnClick(R.id.btn_add_org)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_add_org:
                //haoruigang on 2018-3-30 18:12:31 添加机构
                startActivity(new Intent(getActivity(), JoinOrgActivity.class));
                break;
        }
    }
}