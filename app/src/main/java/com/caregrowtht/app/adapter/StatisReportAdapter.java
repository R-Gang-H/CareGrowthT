package com.caregrowtht.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.chart.DountChartView;
import com.caregrowtht.app.view.chart.HorizontalBarChartView;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xclcharts.chart.BarData;
import org.xclcharts.chart.PieData;
import org.xclcharts.common.DensityUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;

public class StatisReportAdapter extends XrecyclerAdapter {

    @BindView(R.id.tv_in_time)
    TextView tvInTime;
    @BindView(R.id.tv_day_xk_monery)
    TextView tvDayXkMonery;
    @BindView(R.id.tv_month_xk_monery)
    TextView tvMonthXkMonery;
    @BindView(R.id.tv_day_xz_monery)
    TextView tvDayXzMonery;
    @BindView(R.id.tv_month_xz_monery)
    TextView tvMonthXzMonery;
    @BindView(R.id.tv_day_tf_monery)
    TextView tvDayTfMonery;
    @BindView(R.id.tv_month_tf_monery)
    TextView tvMonthTfMonery;
    @BindView(R.id.tv_day_xz_num)
    TextView tvDayXzNum;
    @BindView(R.id.tv_month_xz_num)
    TextView tvMonthXzNum;
    @BindView(R.id.tv_go_hour)
    TextView tvGoHour;
    @BindView(R.id.tv_teacher_work)
    TextView tvTeacherWork;
    @BindView(R.id.rl_work_situa)
    RelativeLayout rlWorkSitua;
    @BindView(R.id.rl_class_time)
    RelativeLayout rlClassTime;

    List<MessageEntity> reportData = new ArrayList<>();
    private Typeface tfLight, tfRegular;
    private final List<String> parties = new ArrayList<>();
    private final List<String> partiesVal = new ArrayList<>();
    public static final int[] COLORS = {
            Color.rgb(163, 214, 96),
            Color.rgb(200, 162, 206),
            Color.rgb(105, 172, 229)
    };
    List<Double> values1 = new LinkedList<Double>();
    List<Double> values2 = new LinkedList<Double>();
    List<Double> values3 = new LinkedList<Double>();
    List<BarData> chartData = new LinkedList<BarData>();
    ArrayList<String> teas = new ArrayList<>();

    ExecutorService executorService;
    private String dpTodayCourseCount, dpTodayCourseHourMinutes, dpTodayCourseHour, dpYingChuQin, dpShiJiChuQin, dpQingjia, dpWeiChuLi;

    public StatisReportAdapter(List datas, Context context) {
        super(datas, context);
        tfLight = Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");
        tfRegular = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
        executorService = Executors.newSingleThreadExecutor();//唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行(同步)
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        MessageEntity messageData = reportData.get(position);
        String data = DateUtil.getDate(Long.valueOf(messageData.getCreateAt()), "yyyy年MM月dd日");
        String week = TimeUtils.getWeekByDateStr(data);
        tvInTime.setText(String.format("%s\t\t%s", data, week));
        UserManager instance = UserManager.getInstance();
        try {
            JSONObject jsonObject = new JSONObject(messageData.getContent());
            String todayIncome = instance.getJsonString(jsonObject, "dpTodayCourseIncome");
            tvDayXkMonery.setText(String.format("%s元", String.valueOf(Float.parseFloat(todayIncome) / 100)));
            String todayCardIncome = instance.getJsonString(jsonObject, "dpTodayCourseCardIncome");
            tvDayXzMonery.setText(String.format("%s元", String.valueOf(Float.parseFloat(todayCardIncome) / 100)));
            String todayRefund = instance.getJsonString(jsonObject, "dpTodayRefund");
            tvDayTfMonery.setText(String.format("%s元", String.valueOf(Float.parseFloat(todayRefund) / 100)));
            String monthIncome = instance.getJsonString(jsonObject, "dpMonthCourseIncome");
            tvMonthXkMonery.setText(String.format("%s元", String.valueOf(Float.parseFloat(monthIncome) / 100)));
            String monthCardIncome = instance.getJsonString(jsonObject, "dpMonthCourseCardIncome");
            tvMonthXzMonery.setText(String.format("%s元", String.valueOf(Float.parseFloat(monthCardIncome) / 100)));
            String monthRefund = instance.getJsonString(jsonObject, "dpMonthRefund");
            tvMonthTfMonery.setText(String.format("%s元", String.valueOf(Float.parseFloat(monthRefund) / 100)));
            String todayNewStu = instance.getJsonString(jsonObject, "dpTodayNewStudent");
            tvDayXzNum.setText(String.format("%s人", todayNewStu));
            String monthNewStu = instance.getJsonString(jsonObject, "dpMonthNewStudent");
            tvMonthXzNum.setText(String.format("%s人", monthNewStu));
            dpTodayCourseCount = instance.getJsonString(jsonObject, "dpTodayCourseCount");// 上了几节课
            dpTodayCourseHourMinutes = instance.getJsonString(jsonObject, "dpTodayCourseHourMinutes");// 分钟
            dpTodayCourseHour = instance.getJsonString(jsonObject, "dpTodayCourseHour");// 小时
            dpYingChuQin = instance.getJsonString(jsonObject, "dpYingChuQin");// 学员：*人
            dpShiJiChuQin = instance.getJsonString(jsonObject, "dpShiJiChuQin");// 签到
            dpQingjia = instance.getJsonString(jsonObject, "dpQingjia");// 请假
            dpWeiChuLi = instance.getJsonString(jsonObject, "dpWeiChuLi");// 待处理

            JSONArray teachers = jsonObject.getJSONArray("teacher");
            tvTeacherWork.setText(Html.fromHtml(String.format("教师工作完成情况\t\t" +
                    "<font color='#69ACE5'>教师\t%s人</font>", teachers.length())));
            teas.clear();
            values1.clear();
            values2.clear();
            values3.clear();
            chartData.clear();
            for (int i = 0; i < teachers.length(); i++) {
                JSONObject teacher = teachers.getJSONObject(i);
                instance.getJsonString(teacher, "teacherId");
                String teacherName = instance.getJsonString(teacher, "teacherName");
                teas.add(teacherName);
                String courseCouont = instance.getJsonString(teacher, "courseCount");
                String endCount = instance.getJsonString(teacher, "courseEndCount");
                String feedCount = instance.getJsonString(teacher, "courseFeedbackCount");
                values1.add(Double.parseDouble(courseCouont) > 0 ? Double.parseDouble(courseCouont) : 0.01);
                values2.add(Double.parseDouble(endCount) > 0 ? Double.parseDouble(endCount) : 0.01);
                values3.add(Double.parseDouble(feedCount) > 0 ? Double.parseDouble(feedCount) : 0.01);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (StrUtils.isNotEmpty(dpTodayCourseHourMinutes)) {
            dpTodayCourseHour = dpTodayCourseHourMinutes;
        } else {
            dpTodayCourseHour += "小时";
        }
        tvGoHour.setText(Html.fromHtml(String.format("今日上课时间\t\t<font color='#69ACE5'>%s节/%s,学员\t%s人</font>"
                , dpTodayCourseCount, dpTodayCourseHour, dpYingChuQin)));
        rlWorkSitua.setVisibility(teas.size() > 0 ? View.VISIBLE : View.GONE);
        if (StrUtils.isNotEmpty(messageData)) {
            final DountChartView pieChart = (DountChartView) holder.getView(R.id.pieChart);
            pieChart.setVisibility(Integer.valueOf(dpYingChuQin) > 0 ? View.VISIBLE : View.GONE);
            final HorizontalBarChartView barChart = (HorizontalBarChartView) holder.getView(R.id.barChart);
            barChart.setVisibility(teas.size() > 0 ? View.VISIBLE : View.GONE);
            if (Integer.valueOf(dpYingChuQin) > 0) {
                setPieChartData(pieChart, 3, dpYingChuQin, dpShiJiChuQin, dpQingjia, dpWeiChuLi);
            }
            if (teas.size() > 0) {
                setHorizontalBarChart(barChart, teas, chartData);
            }
        }
    }

    private void setPieChartData(final DountChartView pieChart, int count,
                                 String dpYingChuQin, String dpShiJiChuQin, String dpQingjia, String dpWeiChuLi) {
        parties.clear();
        parties.add(String.format("签到:\t\t%s人", dpShiJiChuQin));
        parties.add(String.format("请假:\t\t%s人", dpQingjia));
        parties.add(String.format("待处理:\t\t%s人", dpWeiChuLi));
        partiesVal.clear();
        partiesVal.add(dpShiJiChuQin);
        partiesVal.add(dpQingjia);
        partiesVal.add(dpWeiChuLi);

        if (parties.size() > 0 && partiesVal.size() > 0) {// 避免空指针，数组越界
            ArrayList<PieData> entries = new ArrayList<>();
            double total = Double.valueOf(dpShiJiChuQin) + Double.valueOf(dpQingjia) + Double.valueOf(dpWeiChuLi);
            for (int i = 0; i < count; i++) {
                double val = (Double.valueOf(partiesVal.get(i)) / total) * 100;
                entries.add(new PieData(parties.get(i % parties.size()),
                        partiesVal.get(i) + "人", val, COLORS[i]));
            }
            pieChart.chartDataSet(entries);
            pieChart.chartRender();
        }
    }

    private void setHorizontalBarChart(final HorizontalBarChartView barChart, List<String> teas, List<BarData> chartData) {
        if (this.teas.size() > 0 && values1.size() > 0 && values2.size() > 0 && values3.size() > 0) {// 避免空指针，数组越界

            BarData set1 = new BarData("今日课", values1, Color.rgb(105, 172, 229));
            BarData set2 = new BarData("出勤完成", values2, Color.rgb(163, 214, 92));
            BarData set3 = new BarData("发布课程反馈", values3, Color.rgb(200, 162, 206));
            chartData.add(set1);
            chartData.add(set2);
            chartData.add(set3);

            int gruop = values3.size() + 1;

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(barChart.getLayoutParams());
            params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.height = DensityUtil.dip2px(mContext, 20) * 3 * gruop;// 根据树状图的宽定义总高度
            params.addRule(RelativeLayout.BELOW, tvTeacherWork.getId());
            //居中显示
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            barChart.setLayoutParams(params);

            barChart.setVisibility(View.VISIBLE);
            barChart.chartLabels(teas);
            barChart.chartDataSet(chartData);
            barChart.chartRender();

        } else {
            barChart.setVisibility(View.GONE);
        }
    }

    public void setData(List<MessageEntity> reportData) {
        this.reportData.clear();
        this.reportData.addAll(reportData);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return reportData.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_statis_report;
    }

}
