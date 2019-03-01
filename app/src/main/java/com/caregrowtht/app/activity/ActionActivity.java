package com.caregrowtht.app.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.ActionListAdapter;
import com.caregrowtht.app.adapter.OrgActionAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.OrgEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by haoruigang on 2018-5-18 14:02:57.
 * 活动
 */
public class ActionActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tab_layout)
    TabPageIndicator tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    /**
     * Tab标题
     */
    private static final String[] TITLE = new String[]{"未结束", "历史"};

    private ActionListAdapter adapter;
    private List<Fragment> fragments = new ArrayList<>();
    private Fragment actionFragment;
    private Fragment recordFragment;


    @BindView(R.id.x_recycler_view)
    RecyclerView xRecyclerView;

    private OrgActionAdapter orgActionAdapter;
    //添加机构活动数据
    private ArrayList<OrgEntity> listDatas = new ArrayList<>();

    private String OrgId;
    private String type = "1";//1:未结束的活动 2:历史活动 3:全部;
    public String pageIndex = "1";
    public String pageSize = "15";
    private int position;

    @Override
    public int getLayoutId() {
        return R.layout.activity_action;
    }

    @Override
    public void initView() {
        tvTitle.setText("活动");
//        fragments.clear();//清空重新添加
        actionFragment = new Fragment();//未结束
        fragments.add(actionFragment);
        recordFragment = new Fragment();//历史
        fragments.add(recordFragment);
        //实例化ViewPager， 然后给ViewPager设置Adapter
        adapter = new ActionListAdapter(getSupportFragmentManager(), TITLE, fragments);
        viewPager.setAdapter(adapter);
        //实例化TabPageIndicator，然后与ViewPager绑在一起（核心步骤）
        tabLayout.setViewPager(viewPager);
        //设置关联的ViewPager，默认显示第一个
        tabLayout.setViewPager(viewPager, 0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switchTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initRecyclerView(xRecyclerView, true);
        orgActionAdapter = new OrgActionAdapter(listDatas, this, this, null);
        xRecyclerView.setAdapter(orgActionAdapter);
    }

    @Override
    public void initData() {
        OrgId = UserManager.getInstance().getOrgId();
        position = getIntent().getIntExtra("position", 0);
        getOrgAction();//机构活动
    }

    public void switchTab(int position) {
        switch (position) {
            case 0:
                type = "1";
                initData();
                break;
            case 1:
                type = "2";
                initData();
                break;
        }
        viewPager.setCurrentItem(position, false);
    }

    //haoruigang on 2018-4-4 17:26:53 机构活动
    public void getOrgAction() {
        HttpManager.getInstance().doOrgAction("OrgActFragment", OrgId, type, pageIndex,
                pageSize, new HttpCallBack<BaseDataModel<OrgEntity>>() {

                    @Override
                    public void onSuccess(BaseDataModel<OrgEntity> data) {
                        LogUtils.d("OrgActFragment onSuccess", data.getData().size() + "");
                        listDatas.clear();
                        listDatas.addAll(data.getData());
                        orgActionAdapter.setData(data.getData(), true);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("OrgActFragment onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ActionActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("OrgActFragment onError", throwable.getMessage());
                    }
                });
    }

    @Override
    public void setOnItemClickListener(View view, int postion) {
        String actId = listDatas.get(postion).getActivityId();
        startActivity(new Intent(ActionActivity.this, ActionInfoActivity.class).putExtra("actId", actId));
    }

    @OnClick({R.id.rl_back_button, R.id.rl_next_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                setResult(RESULT_OK, getIntent().putExtras(bundle));
                finish();
                break;
            case R.id.rl_next_button:

                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) {
        if ((paramKeyEvent.getAction() == 0)
                && (paramKeyEvent.getKeyCode() == 4)) {

            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            setResult(RESULT_OK, getIntent().putExtras(bundle));
            finish();
        }
        return super.dispatchKeyEvent(paramKeyEvent);
    }
}
