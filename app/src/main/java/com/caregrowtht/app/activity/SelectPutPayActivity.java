package com.caregrowtht.app.activity;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.PutPayAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.BaseModel;
import com.caregrowtht.app.model.PutPayEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.chart.LineChartView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import org.xclcharts.chart.LineData;
import org.xclcharts.renderer.XEnum;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import butterknife.Setter;

/**
 * 查看收与支的统计
 */
public class SelectPutPayActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.line_chart)
    LineChartView lineChart;
    @BindView(R.id.tv_put)
    TextView tvPut;
    @BindView(R.id.tv_pay)
    TextView tvPay;
    @BindView(R.id.tv_put_title)
    TextView tvPutTitle;
    @BindView(R.id.tv_pay_title)
    TextView tvPayTitle;

    @BindViews({R.id.tv_7_day, R.id.tv_30_day, R.id.tv_90_day, R.id.tv_select})
    List<TextView> radioTextViews;

    PutPayAdapter adapter;
    private PutPayEntity putPayDatas = new PutPayEntity();
    private List<PutPayEntity.TableData> listDatas = new ArrayList<>();
    private String orgId;
    private String timeType1 = "1";// 筛选时间 1：7天前 2：30天内 3：本月 4：自定义时间段格式：2014/04/23 - 2014/04/25
    private String timeType2 = "2";// 筛选时间 1：今日 2：7天内 3：30天内 4：本月 或 2019/07/12~2019/08/20（自定义时间段之间传）
    private String showType = "1";// 显示方式 1：全部 2：收入 3：支出;
    private int index = 0;
    private boolean isEdit = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_select_put_pay;
    }

    @Override
    public void initView() {
        tvTitle.setText("查看统计");
        initRecyclerView(recyclerView, true);
        adapter = new PutPayAdapter(listDatas, this);
        recyclerView.setAdapter(adapter);
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            pageIndex = 1;
            getBillRecordInfo(true);
        });
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnLoadmoreListener(refreshlayout -> {
            pageIndex++;
            getBillRecordInfo(false);
        });
        refreshLayout.setEnableLoadmoreWhenContentNotFull(false);

        radioTextViews.get(0).setOnClickListener(this);
        radioTextViews.get(1).setOnClickListener(this);
        radioTextViews.get(2).setOnClickListener(this);
        radioTextViews.get(3).setOnClickListener(this);
    }

    @Override
    public void initData() {
        orgId = UserManager.getInstance().getOrgId();
        setChioceItem(index);
    }

    private void inOutRecords() {
        HttpManager.getInstance().doInOutRecords("SelectPutPayActivity", orgId, timeType1
                , new HttpCallBack<BaseDataModel<PutPayEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<PutPayEntity> data) {
                        getBillRecordInfo(true);
                        ArrayList<PutPayEntity> putPay = data.getData();
                        if (putPay.size() > 0) {// 避免空指针，数组越界
                            LinkedList<String> labels = new LinkedList<String>();
                            LinkedList<LineData> chartData = new LinkedList<>();
                            LinkedList<Double> dataPut = new LinkedList<Double>();
                            LinkedList<Double> dataPay = new LinkedList<Double>();
                            double axisMax = 0;
                            for (int i = 0; i < putPay.size(); i++) {
                                labels.add(DateUtil.getDate(Long.valueOf(putPay.get(i).getxDate()), "MM/dd"));
                                Double inPrice = Double.valueOf(putPay.get(i).getInPrice()) / 1000;// 千元
                                Double OutPrice = Double.valueOf(putPay.get(i).getOutPrice()) / 1000;// 千元
                                if (inPrice > axisMax) {
                                    axisMax = inPrice;
                                }
                                if (OutPrice > axisMax) {
                                    axisMax = OutPrice;
                                }
                                dataPut.add(inPrice);
                                dataPay.add(OutPrice);
                            }
                            LineData lineData1 = new LineData("收入", dataPut, Color.rgb(238, 129, 130));
                            lineData1.setDotStyle(XEnum.DotStyle.HIDE);
                            LineData lineData2 = new LineData("支出", dataPay, Color.rgb(163, 214, 92));
                            lineData2.setDotStyle(XEnum.DotStyle.HIDE);
                            chartData.add(lineData1);
                            chartData.add(lineData2);
                            lineChart.chartLabels(labels);
                            lineChart.chartDataSet(chartData);
                            lineChart.chartRender(axisMax);
                            lineChart.invalidate();
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(SelectPutPayActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    private void getBillRecordInfo(boolean isClear) {
        HttpManager.getInstance().doGetBillRecordInfo("SelectPutPayActivity", orgId, timeType2, showType,
                pageIndex, new HttpCallBack<BaseModel<PutPayEntity>>() {
                    @Override
                    public void onSuccess(BaseModel<PutPayEntity> data) {
                        putPayDatas = data.getData();
                        if (isClear) {
                            listDatas.clear();
                            if (StrUtils.isNotEmpty(putPayDatas)) {
                                tvPutTitle.setText("收入合计");
                                tvPayTitle.setText("支出合计");
                                DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.00");// 金额格式化
                                tvPut.setText(String.format("¥%s", decimalFormat.format(Double.valueOf(putPayDatas.getIncomePrice()))));
                                tvPay.setText(String.format("¥%s", decimalFormat.format(Double.valueOf(putPayDatas.getOutcomePrice()))));
                            }
                        }
                        listDatas.addAll(putPayDatas.getTableData());
                        adapter.setData(listDatas);
                        if (isClear) {
                            refreshLayout.finishRefresh();
                        } else {
                            refreshLayout.finishLoadmore();
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(SelectPutPayActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    @OnClick({R.id.rl_back_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_7_day:
                setChioceItem(0);
                break;
            case R.id.tv_30_day:
                setChioceItem(1);
                break;
            case R.id.tv_90_day:
                setChioceItem(2);
                break;
            case R.id.tv_select:
                setChioceItem(3);
                break;
        }
    }

    private void setChioceItem(int index) {
        this.index = index;
        this.pageIndex = 1;
        if (index < 3) {
            this.timeType1 = index + 1 + "";
            this.timeType2 = index + 2 + "";
            inOutRecords();
        } else {
            if (isEdit) {// 是编辑用已选中的日期
                inOutRecords();
            } else {
                selectDate();
            }
        }
        UserManager.apply(radioTextViews, TABSPEC, radioTextViews.get(index));
    }

    //控制normal 状态的当前View 隐藏，其它空间仍然为显示
    private final Setter<TextView, TextView> TABSPEC = (view, value, index) -> {
        if (view.getId() == value.getId()) {
            view.setSelected(true);
            view.setBackgroundResource(R.mipmap.ic_blue_levae);
        } else {
            view.setSelected(false);
            view.setBackgroundResource(0);
        }
    };

    /**
     * select time to update course table.
     */
    private void selectDate() {
        Calendar startDate = Calendar.getInstance();
        new TimePickerBuilder(this, (date, v) -> {

            String type = "yyyy/MM/dd";//"yyyy-MM-dd HH:mm:ss"
            SimpleDateFormat sdf = new SimpleDateFormat(type);
            String time = TimeUtils.getDateToString(date.getTime(), type);
            Date sdate = TimeUtils.getStartDate(time, type);
            Date edate = TimeUtils.getEndDate(time, type);

            String startEndTime = String.format("%s~%s", sdf.format(sdate), sdf.format(edate));
            this.timeType1 = startEndTime;
            this.timeType2 = startEndTime;
            inOutRecords();
        })
                .setType(new boolean[]{true, true, false, false, false, false})
                .setDate(startDate)
                .setRangDate(null, startDate)
                .setLabel("年", "月", "", "", "", "")
                .build().show();
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        switch (event.getWhat()) {
            case ToUIEvent.REFERSH_PUTPAY:
                this.isEdit = false;
                if (StrUtils.isNotEmpty(event.getObj())) {
                    isEdit = (boolean) event.getObj();
                }
                initData();
                break;
        }
    }

}
