package com.caregrowtht.app.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.OrgSearchAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.OrgEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.LoadingFrameView;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-8-7 15:10:20
 * 机构搜索
 */
public class OrgSearchActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_title)
    EditText tvTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    OrgSearchAdapter mAdapter;
    List<OrgEntity> orgDatas = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_org_search;
    }

    @Override
    public void initView() {
        initRecyclerView(recyclerView, true);
        mAdapter = new OrgSearchAdapter(orgDatas, this, this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
        tvTitle.setOnEditorActionListener((textView, i, keyEvent) -> {
            searchOrg();
            LogUtils.tag("sousuo");
            return false;
        });
    }

    /**
     * 搜索机构
     */
    private void searchOrg() {
        loadView.setVisibility(View.VISIBLE);
        loadView.setProgressShown(true);
        String orgName = tvTitle.getText().toString().trim();
        orgDatas.clear();
        if (TextUtils.isEmpty(U.stringFilter(orgName))) {
            mAdapter.setData(orgDatas);
            loadView.setNoShown(true);
            return;
        }
        HttpManager.getInstance().doSearchOrg("OrgSearchActivity", orgName,
                new HttpCallBack<BaseDataModel<OrgEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<OrgEntity> data) {
                        orgDatas.clear();
                        orgDatas.addAll(data.getData());
                        mAdapter.setData(orgDatas);
                        if (orgDatas.size() > 0) {
                            loadView.delayShowContainer(true);
                        } else {
                            loadView.setNoShown(true);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("OrgSearchActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(OrgSearchActivity.this);
                        } else {
                            U.showToast(errorMsg);
                            loadView.setErrorShown(true, v -> searchOrg());
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("OrgSearchActivity throwable", throwable.getMessage());
                        loadView.setErrorShown(true, v -> searchOrg());
                    }
                });
    }

    @OnClick(R.id.btn_cancel)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    @Override
    public void setOnItemClickListener(View view, int postion) {
        UserManager.getInstance().getOrgInfo(orgDatas.get(postion).getOrgId(),
                OrgSearchActivity.this, "1");
    }
}
