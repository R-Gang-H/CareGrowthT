package com.caregrowtht.app.activity;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.OrgAdapter;
import com.caregrowtht.app.model.BaseBeanModel;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.OrgEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.LoadingFrameView;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

import static com.caregrowtht.app.Constant.BASE_ORG_URL;

/**
 * 我关注的机构
 */
public class MyFollowActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
    @BindView(R.id.rv_org)
    XRecyclerView mRecyclerView;
    String orgId;
    OrgAdapter mAdapter;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_follow;
    }

    @Override
    public void initView() {
        tvTitle.setText("我的机构");
        rlNextButton.setVisibility(View.VISIBLE);
        tvTitleRight.setText("添加机构");
    }

    @Override
    public void initData() {
        iniXrecyclerView(mRecyclerView);
        orgList = new ArrayList<>();
        mAdapter = new OrgAdapter(orgList, MyFollowActivity.this, this, null);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                getBindOrg(true);
            }

            @Override
            public void onLoadMore() {
                pageIndex++;
                getBindOrg(false);
            }
        });
        getBindOrg(true);
    }


    ArrayList<OrgEntity> orgList;

    private void getBindOrg(boolean isClear) {
        loadView.setProgressShown(true);
        //haoruigang on 2018-4-23 14:05:58 获取教师添加过的机构列表
        HttpManager.getInstance().doGetBindOrg("MyFollowActivity",
                new HttpCallBack<BaseDataModel<OrgEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<OrgEntity> data) {
                        orgList = data.getData();
                        if (orgList.size() > 0) {
                            setData(data, isClear);
                            loadView.delayShowContainer(true);
                        } else {
                            loadView.setNoShown(true);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MyFollowActivity.this);
                        } else {
                            U.showToast(errorMsg);
                            loadView.setErrorShown(true, v -> getBindOrg(true));
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        loadView.setErrorShown(true, v -> getBindOrg(true));
                    }
                });
    }

    private void setData(BaseDataModel<OrgEntity> model, boolean isClear) {
        if (isClear) {
            mAdapter.setData(model.getData(), true);
        } else {
            mAdapter.setData(model.getData(), false);
        }
        mRecyclerView.refreshComplete();
        mRecyclerView.loadMoreComplete();
    }


    @OnClick({R.id.rl_back_button, R.id.rl_next_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                //返回按钮
                finish();
                break;
            case R.id.rl_next_button:
                //haoruigang on 2018-3-30 18:12:31 添加机构
                startActivity(new Intent(this, JoinOrgActivity.class));
                break;
        }
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        LogUtils.d("OrgFragment", position + ":" + orgList.get(position - 1).getOrgId());
        Intent intent = new Intent(this, UserTermActivity.class);
        intent.setData(Uri.parse(BASE_ORG_URL + orgList.get(position - 1).getOrgId()));
        intent.putExtra("openType", "3");// 用户协议
        startActivity(intent);
    }

}
