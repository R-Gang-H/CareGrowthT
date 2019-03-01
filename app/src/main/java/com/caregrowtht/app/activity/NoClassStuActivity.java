package com.caregrowtht.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.FormalAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.LoadingFrameView;
import com.caregrowtht.app.view.xrecyclerview.SpaceItemDecoration;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 6:未出勤超过1个月(最近30天都没出勤) 的学员
 */
public class NoClassStuActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.xrv_student)
    XRecyclerView xrvStudent;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    private Boolean isClear = true;
    FormalAdapter mFormalAdapter;
    List<StudentEntity> mFormalList = new ArrayList<>();
    private String OrgId, status = "2";// 2：活跃学员
    private String key = "6";//  4:需要续费的学员 6:未出勤超过1个月(最近30天都没出勤) 的学员

    @Override
    public int getLayoutId() {
        return R.layout.activity_no_class_stu;
    }

    @Override
    public void initView() {
        tvTitle.setText("学员动态");

        GridLayoutManager manager = new GridLayoutManager(this, 4);
        manager.setOrientation(RecyclerView.VERTICAL);
        xrvStudent.setLayoutManager(manager);
        xrvStudent.addItemDecoration(new SpaceItemDecoration(5, SpaceItemDecoration.GRIDLAYOUT));

        mFormalAdapter = new FormalAdapter(mFormalList, this, this, null);
        xrvStudent.setAdapter(mFormalAdapter);
        xrvStudent.setPullRefreshEnabled(true);
        xrvStudent.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                isClear = true;
                initData();
            }

            @Override
            public void onLoadMore() {
                pageIndex++;
                isClear = false;
                initData();
            }
        });
    }

    @Override
    public void initData() {
        OrgId = UserManager.getInstance().getOrgId();
        key = getIntent().getStringExtra("key");
        getStudent(isClear, status);//10.获取机构的正式学员
    }

    private void getStudent(Boolean isClear, final String status) {
        loadView.setProgressShown(true);
        //10.获取机构的正式学员 haoruigang on 2018-8-7 15:50:55
        HttpManager.getInstance().doGetOrgChild("FormalActivity",
                OrgId, status, pageIndex + "", key,
                new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        mFormalAdapter.update(data.getData(), status);//每次重置数据
                        if (isClear) {
                            mFormalList.clear();
                        }
                        mFormalList.addAll(data.getData());
                        if (data.getData().size() > 0) {
                            loadView.delayShowContainer(true);
                        } else {
                            if (isClear) {
                                loadView.setNoShown(true);
                            } else {
                                loadView.delayShowContainer(true);
                            }
                        }
                        mFormalAdapter.update(mFormalList, status);
                        xrvStudent.refreshComplete();
                        xrvStudent.loadMoreComplete();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("FormalActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(NoClassStuActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.tag(" throwable " + throwable);
                    }
                });
    }

    @Override
    public void setOnItemClickListener(View view, int postion) {
        startActivity(new Intent(this, StudentDetailsActivity.class)
                .putExtra("StudentEntity", mFormalList.get(postion - 1))
                .putExtra("status", status));
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        if (event.getWhat() == ToUIEvent.REFERSH_ACTIVE_STU) {
            initData();
        }
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
