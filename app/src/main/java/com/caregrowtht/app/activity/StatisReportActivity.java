package com.caregrowtht.app.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.StatisReportAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.LoadingFrameView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 报告
 */
public class StatisReportActivity extends BaseActivity {

    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycler_view)
    XRecyclerView recyclerView;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    private MessageEntity msgEntity;
    private String orgId;
    private String beginAt = "0", endAt = "0";
    private int index = 0;

    private String showType;

    private StatisReportAdapter adapter;
    private List<MessageEntity> courseData = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_statis_report;
    }

    @Override
    public void initView() {
        tvTitle.setText("报告");
        rlNextButton.setVisibility(View.VISIBLE);
        ivTitleRight.setImageResource(R.mipmap.ic_screen_right);
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (StrUtils.isNotEmpty(msgEntity)) {
            showType = msgEntity.getType();
            orgId = msgEntity.getOrgId();
            UserManager.getInstance().setOrgId(orgId);
        }
        iniXrecyclerView(recyclerView);
        recyclerView.setPullRefreshEnabled(true);
        recyclerView.setLoadingMoreEnabled(!showType.equals("9"));
        adapter = new StatisReportAdapter(courseData, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                getDaily(true);
            }

            @Override
            public void onLoadMore() {
                pageIndex++;
                getDaily(false);
            }
        });
    }

    @Override
    public void initData() {
        getDaily(true);
    }

    private void getDaily(boolean isClear) {
        HttpManager.getInstance().doGetDaily("StatisReportActivity",
                orgId, beginAt, endAt, pageIndex + "", new HttpCallBack<BaseDataModel<MessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        if (isClear) {
                            courseData.clear();
                        }
                        courseData.addAll(data.getData());
                        adapter.setData(courseData);
                        if (courseData.size() > 0) {
                            loadView.delayShowContainer(true);
                        } else {
                            if (isClear) {
                                loadView.setNoShown(true);
                            } else {
                                loadView.delayShowContainer(true);
                            }
                        }
                        if (isClear) {
                            recyclerView.refreshComplete();
                        } else {
                            recyclerView.loadMoreComplete();
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("StatisReportActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(StatisReportActivity.this);
                        } else {
                            loadView.setErrorShown(true, v -> getDaily(true));
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("StatisReportActivity onError", throwable.getMessage());
                        loadView.setErrorShown(true, v -> getDaily(true));
                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.rl_next_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.rl_next_button:
                startActivity(new Intent(this, ScreenActivity.class)
                        .putExtra("type", showType)
                        .putExtra("status", 0)
                        .putExtra("isMy", "1")
                        .putExtra("index", index));
                break;
        }
    }

    @Override
    public void onEvent(ToUIEvent event) {
        switch (event.getWhat()) {
            case ToUIEvent.SET_SCREEN_LES:
                beginAt = String.valueOf((long) event.getObj1());
                endAt = String.valueOf((long) event.getObj2());
                index = (int) event.getObj3();
                pageIndex = 1;
                getDaily(true);
                break;
        }
    }

}
