package com.caregrowtht.app.activity;

import androidx.recyclerview.widget.RecyclerView;

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
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;

import java.util.ArrayList;
import java.util.List;

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
        String orgName = tvTitle.getText().toString().trim();
        HttpManager.getInstance().doSearchOrg("OrgSearchActivity", orgName,
                new HttpCallBack<BaseDataModel<OrgEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<OrgEntity> data) {
                        orgDatas.clear();
                        orgDatas.addAll(data.getData());
                        mAdapter.setData(orgDatas);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("OrgSearchActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(OrgSearchActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("OrgSearchActivity throwable", throwable.getMessage());
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
