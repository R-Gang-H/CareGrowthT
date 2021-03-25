package com.caregrowtht.app.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.NullRemindAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.LoadingFrameView;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 空位提醒
 */
public class NullRemindActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycler_view)
    XRecyclerView recyclerView;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    NullRemindAdapter adapter;
    List<CourseEntity> listDatas = new ArrayList<>();
    private MessageEntity msgEntity;
    private String orgId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_null_remind;
    }

    @Override
    public void initView() {
        tvTitle.setText("空位提醒");
        iniXrecyclerView(recyclerView);
        recyclerView.setPullRefreshEnabled(true);
        adapter = new NullRemindAdapter(listDatas, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                getVacancy();
            }

            @Override
            public void onLoadMore() {
            }
        });
    }

    @Override
    public void initData() {
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (StrUtils.isNotEmpty(msgEntity)) {
            orgId = msgEntity.getOrgId();
            UserManager.getInstance().setOrgId(orgId);
        }
        getVacancy();

    }

    private void getVacancy() {
        HttpManager.getInstance().doGetVacancy("NullRemindActivity", orgId,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        listDatas.clear();
                        listDatas.addAll(data.getData());
                        adapter.setData(listDatas);
                        if (listDatas.size() > 0) {
                            loadView.delayShowContainer(true);
                        } else {
                            loadView.setNoShown(true);
                        }
                        recyclerView.refreshComplete();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NullRemindActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(NullRemindActivity.this);
                        } else {
                            loadView.setErrorShown(true, v -> getVacancy());
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        loadView.setErrorShown(true, v -> getVacancy());
                        LogUtils.d("NullRemindActivity onError", throwable.getMessage());
                    }
                });
    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        startActivity(new Intent(this, CourserCoverActivity.class)
                .putExtra("courseEntity", listDatas.get(position - 1)));
    }

}
