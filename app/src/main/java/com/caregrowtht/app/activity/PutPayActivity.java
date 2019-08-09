package com.caregrowtht.app.activity;

import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.PutPayAdapter;
import com.caregrowtht.app.model.BaseModel;
import com.caregrowtht.app.model.PutPayEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 收与支
 */
public class PutPayActivity extends BaseActivity {

    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.x_recycler_view)
    XRecyclerView xRecyclerView;
    @BindView(R.id.iv_empty)
    TextView ivEmpty;
    @BindView(R.id.rl_yes_data)
    RelativeLayout rlYesData;

    TextView tvPut;
    TextView tvPay;

    PutPayAdapter adapter;
    private PutPayEntity putPayDatas = new PutPayEntity();
    private List<PutPayEntity.TableData> listDatas = new ArrayList<>();
    private String orgId;
    private String timeType = "1";// 筛选时间 1：今日 2：7天内 3：30天内 4：本月 或 2019/07/12~2019/08/20（自定义时间段之间传）
    private String showType = "1";// 显示方式 1：全部 2：收入 3：支出;

    @Override
    public int getLayoutId() {
        return R.layout.activity_put_pay;
    }

    @Override
    public void initView() {
        String str = "<font color='#333333'>今日暂无收与支数据</font>";
        ivEmpty.setText(Html.fromHtml(str));
        boolean isRecord = getIntent().getBooleanExtra("isRecord", false);
        boolean isInfo = getIntent().getBooleanExtra("isInfo", false);
        if (isRecord && isInfo) {// 收与支/记录  收与支/查看统计
            rlNextButton.setVisibility(View.VISIBLE);
        }
        tvTitle.setText("收与支");
        tvTitleRight.setText("查看统计");
        tvTitleRight.setPadding(0, 0, 20, 0);

        iniXrecyclerView(xRecyclerView);
        xRecyclerView.setPullRefreshEnabled(true);
        xRecyclerView.setLoadingMoreEnabled(true);
        adapter = new PutPayAdapter(listDatas, this);
        xRecyclerView.setAdapter(adapter);
        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                getBillRecordInfo(true);
            }

            @Override
            public void onLoadMore() {
                pageIndex++;
                getBillRecordInfo(false);
            }
        });
        RelativeLayout putpayTitle = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.item_put_pay_title, xRecyclerView, false);
        tvPut = putpayTitle.findViewById(R.id.tv_put);
        tvPay = putpayTitle.findViewById(R.id.tv_pay);
        xRecyclerView.addHeaderView(putpayTitle);
    }

    @Override
    public void initData() {
        orgId = UserManager.getInstance().getOrgId();
        getBillRecordInfo(true);
    }

    private void getBillRecordInfo(boolean isClear) {
        HttpManager.getInstance().doGetBillRecordInfo("PutPayActivity", orgId, timeType, showType,
                pageIndex, new HttpCallBack<BaseModel<PutPayEntity>>() {
                    @Override
                    public void onSuccess(BaseModel<PutPayEntity> data) {
                        putPayDatas = data.getData();
                        if (isClear) {
                            if (putPayDatas.getTableData().size() == 0) {
                                rlYesData.setVisibility(View.VISIBLE);
                            } else {
                                rlYesData.setVisibility(View.GONE);
                            }
                            listDatas.clear();
                            if (StrUtils.isNotEmpty(putPayDatas)) {
                                DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.00");// 金额格式化
                                tvPut.setText(String.format("¥%s", decimalFormat.format(Double.valueOf(putPayDatas.getIncomePrice()))));
                                tvPay.setText(String.format("¥%s", decimalFormat.format(Double.valueOf(putPayDatas.getOutcomePrice()))));
                            }
                        }
                        listDatas.addAll(putPayDatas.getTableData());
                        adapter.setData(listDatas);
                        if (isClear) {
                            xRecyclerView.refreshComplete();
                        } else {
                            xRecyclerView.loadMoreComplete();
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(PutPayActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.rl_next_button, R.id.iv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.rl_next_button:
                startActivity(new Intent(this, SelectPutPayActivity.class));// 查看统计
                break;
            case R.id.iv_add:
                startActivity(new Intent(this, AddPutPayActivity.class));
                overridePendingTransition(R.anim.window_out, R.anim.window_back);//底部弹出动画
                break;
        }
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        switch (event.getWhat()) {
            case ToUIEvent.REFERSH_PUTPAY:
                pageIndex = 1;
                getBillRecordInfo(true);
                break;
        }
    }

}
