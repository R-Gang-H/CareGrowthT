package com.caregrowtht.app.activity;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.ReportAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.FeedbackEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-5-17 17:18:18
 * 举报兴趣圈
 */
public class ReportActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.x_recycler_view)
    XRecyclerView xRecyclerView;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private ArrayList<FeedbackEntity> orgList;
    private ReportAdapter mAdapter;
    private String circleId;
    private String reasonId;
    private String content;

    @Override
    public int getLayoutId() {
        return R.layout.activity_report;
    }

    @Override
    public void initView() {
        circleId = getIntent().getStringExtra("circleId");

        iniXrecyclerView(xRecyclerView);
        tvTitle.setText("举报");
        orgList = new ArrayList<>();
        mAdapter = new ReportAdapter(orgList, this, this, null);
        xRecyclerView.setAdapter(mAdapter);
        xRecyclerView.setPullRefreshEnabled(false);
    }

    @Override
    public void initData() {
        getReportType();
    }

    private void getReportType() {
        //haoruigang on 2018-5-18 14:54:38 60. 获取举报原因
        HttpManager.getInstance().doGetReportType("ReportActivity",
                new HttpCallBack<BaseDataModel<FeedbackEntity>>(this) {
                    @Override
                    public void onSuccess(BaseDataModel<FeedbackEntity> data) {
                        orgList = data.getData();
                        mAdapter.setData(orgList, true);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ReportActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.btn_submit:
                content = mAdapter.etOther.getText().toString().trim();
                addCircleReport();
                break;
        }
    }

    @Override
    public void setOnItemClickListener(View view, int postion) {
        reasonId = orgList.get(postion - 1).getReasonId();
        mAdapter.getSelect(postion - 1);
        mAdapter.notifyDataSetChanged();
    }

    private void addCircleReport() {
        //haoruigang on 2018-5-18 15:57:56 58. 举报兴趣圈
        HttpManager.getInstance().doAddCircleReport("ReportActivity", circleId, reasonId, content,
                new HttpCallBack<BaseDataModel<FeedbackEntity>>(this) {
                    @Override
                    public void onSuccess(BaseDataModel<FeedbackEntity> data) {
                        U.showToast("举报成功!");
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ReportActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

}
