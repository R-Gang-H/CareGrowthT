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
import com.caregrowtht.app.view.chart.DayAxisValueFormatter;
import com.caregrowtht.app.view.chart.MyValueFormatter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    List<MessageEntity> reportData = new ArrayList<>();
    private Typeface tfLight, tfRegular;
    private final List<String> parties = new ArrayList<>();
    private final List<String> partiesVal = new ArrayList<>();
    public static final int[] COLORS = {
            Color.rgb(163, 214, 96),
            Color.rgb(200, 162, 206),
            Color.rgb(105, 172, 229)
    };
    ArrayList<BarEntry> values1 = new ArrayList<>();
    ArrayList<BarEntry> values2 = new ArrayList<>();
    ArrayList<BarEntry> values3 = new ArrayList<>();
    ArrayList<String> teas = new ArrayList<>();

    private String dpTodayCourseCount, dpTodayCourseHourMinutes, dpTodayCourseHour, dpYingChuQin, dpShiJiChuQin, dpQingjia, dpWeiChuLi;

    public StatisReportAdapter(List datas, Context context) {
        super(datas, context);
        tfLight = Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");
        tfRegular = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        // 强行关闭复用
        holder.setIsRecyclable(false);
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
            tvTeacherWork.setText(String.format("教师工作完成情况\t\t\t教师：%s人", teachers.length()));
            teas.clear();
            values1.clear();
            values2.clear();
            values3.clear();
            for (int i = 0; i < teachers.length(); i++) {
                JSONObject teacher = teachers.getJSONObject(i);
                instance.getJsonString(teacher, "teacherId");
                String teacherName = instance.getJsonString(teacher, "teacherName");
                teas.add(teacherName);
                String courseCouont = instance.getJsonString(teacher, "courseCount");
                String endCount = instance.getJsonString(teacher, "courseEndCount");
                String feedCount = instance.getJsonString(teacher, "courseFeedbackCount");
                values1.add(new BarEntry(i, Integer.valueOf(courseCouont)));
                values2.add(new BarEntry(i, Integer.valueOf(endCount)));
                values3.add(new BarEntry(i, Integer.valueOf(feedCount)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (StrUtils.isNotEmpty(dpTodayCourseHourMinutes)) {
            dpTodayCourseHour = dpTodayCourseHourMinutes;
        } else {
            dpTodayCourseHour += "小时";
        }
        tvGoHour.setText(Html.fromHtml(String.format("今日上课时间\t\t<font color='#69ACE5'>%s节/%s</font>"
                , dpTodayCourseCount, dpTodayCourseHour)));
        if (StrUtils.isNotEmpty(messageData)) {
            final PieChart pieChart = (PieChart) holder.getView(R.id.pieChart);
            setPieChartData(pieChart, 3, dpYingChuQin, dpShiJiChuQin, dpQingjia, dpWeiChuLi);
            final HorizontalBarChart barChart = (HorizontalBarChart) holder.getView(R.id.barChart);
            setHorizontalBarChart(barChart);
        }
    }

    private void setPieChartData(final PieChart pieChart, int count,
                                 String dpYingChuQin, String dpShiJiChuQin, String dpQingjia, String dpWeiChuLi) {

        // (true)以百分比绘制，(false)以原始值绘制
        pieChart.setUsePercentValues(true);
        // 返回图表的描述对象，该对象负责保存与图表右下角显示的描述文本相关的所有信息*(默认情况下)。
        pieChart.getDescription().setEnabled(false);
        // 设置附加到自动计算偏移量的额外偏移量(围绕图表视图)。
        pieChart.setExtraOffsets(5, 10, 5, 5);
        // 减速摩擦系数[0;1]区间越大，表示速度下降越慢，如设为0，则速度立即停止。1是无效值，将自动转换为0.999f。
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        // 设置在PieChart中心绘制的洞的颜色(如果启用)。
        pieChart.setHoleColor(Color.WHITE);
        // 设置透明圆圈应有的颜色。
        pieChart.setTransparentCircleColor(Color.WHITE);
        // 设置透明圆的透明度为0 =完全透明，255 =完全不透明。默认值为100。
        pieChart.setTransparentCircleAlpha(110);
        // 将PieChart中心的孔半径设置为最大半径(max=整个图表的半径)的百分比，默认值为50
        pieChart.setHoleRadius(40f);
        // 设置在Piechart中孔旁边绘制的透明圆的半径，默认为最大半径的百分比(max=整个图表的半径)，默认为55% ->表示比中心孔大5%
        pieChart.setTransparentCircleRadius(40f);
        // 将此设置为true以绘制显示在饼图中心的文本
        pieChart.setDrawCenterText(false);
        // 设置以度为单位的RadarChart旋转的偏移量。默认值270f——>顶部(北部)
        pieChart.setRotationAngle(0);
        // 通过触摸使图表旋转
        pieChart.setRotationEnabled(true);
        // 可以通过拖动或编程方式突出显示。默认值:true
        pieChart.setHighlightPerTapEnabled(true);

        pieChart.animateY(1400, Easing.EaseInOutQuad);
        // pieChart.spin(2000, 0, 360);

        // 返回图表的图例对象。此方法可用于获取图例的实例，以便自定义自动生成的图例。
        Legend pieL = pieChart.getLegend();
        // 设置图例的垂直对齐
        pieL.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        // 设置图例的水平对齐
        pieL.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        // 设置图例的方向
        pieL.setOrientation(Legend.LegendOrientation.VERTICAL);
        // 设置图例将在图表内绘制还是在图表外绘制
        pieL.setDrawInside(false);
        // 设置水平轴上图例条目之间的空间(以像素为单位)，并在内部转换为dp
        pieL.setXEntrySpace(10f);
        pieL.setYEntrySpace(10f);
        pieL.setYOffset(0f);

        // 将条目标签绘制到饼图切片中
        pieChart.setDrawEntryLabels(false);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTypeface(tfRegular);
        pieChart.setEntryLabelTextSize(12f);


        parties.clear();
        parties.add(String.format("签到:\t\t%s人", dpShiJiChuQin));
        parties.add(String.format("请假:\t\t%s人", dpQingjia));
        parties.add(String.format("待处理:\t\t%s人", dpWeiChuLi));
        partiesVal.clear();
        partiesVal.add(dpShiJiChuQin);
        partiesVal.add(dpQingjia);
        partiesVal.add(dpWeiChuLi);

        if (parties.size() > 0 && partiesVal.size() > 0) {// 避免空指针，数组越界
            ArrayList<PieEntry> entries = new ArrayList<>();
            // NOTE: The order of the entries when being added to the entries array determines their position around the center of
            // the chart.
            for (int i = 0; i < count; i++) {
                entries.add(new PieEntry((float) Integer.valueOf(partiesVal.get(i)),
                        parties.get(i % parties.size())));
            }

            PieDataSet dataSet = new PieDataSet(entries, String.format("学员:\t\t%s人", dpYingChuQin));
            dataSet.setDrawIcons(false);// 不显示图标
            // 设置dp中的piechart-slice之间剩余的空间。默认值:0——>没有空间，最大20f
            dataSet.setSliceSpace(0f);
            dataSet.setIconsOffset(new MPPointF(0, 40));
            // 设置此数据集的突出显示的片段从图表中心“移动”的距离，默认为12f
            dataSet.setSelectionShift(5f);

            // add a lot of colors
            ArrayList<Integer> colors = new ArrayList<>();
            for (int c : COLORS)
                colors.add(c);
            colors.add(ColorTemplate.getHoloBlue());
            dataSet.setColors(colors);
            //dataSet.setSelectionShift(0f);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter(pieChart));
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.WHITE);
            data.setValueTypeface(tfLight);
            pieChart.setData(data);

            PieData datas = pieChart.getData();
            if (StrUtils.isNotEmpty(datas) && datas.getDataSets().size() > 0) {
                // 将此设置为true以在图表上绘制y值。
                for (IDataSet<?> set : pieChart.getData().getDataSets())
                    set.setDrawValues(false);
            }

            // undo all highlights
            pieChart.highlightValues(null);

            pieChart.invalidate();
        }
    }

    private void setHorizontalBarChart(final HorizontalBarChart barChart) {
        if (teas.size() > 0 && values1.size() > 0 && values2.size() > 0 && values3.size() > 0) {// 避免空指针，数组越界
            barChart.setVisibility(View.VISIBLE);
            // 在每个条后面绘制一个表示最大值的灰色区域。开启他将使性能降低约50%。
            barChart.setDrawBarShadow(false);
            // 如果设置为true，则所有值都绘制在其条形图上方，而不是顶部下方。
            barChart.setDrawValueAboveBar(true);
            // 返回图表的描述对象，该对象负责保存与图表右下角显示的描述文本相关的所有信息*(默认情况下)。
            barChart.getDescription().setEnabled(false);
            // if more than 60 entries are displayed in the chart, no values will be drawn
            // 设置图表上最大可见绘制值的数量
            barChart.setMaxVisibleValueCount(60);
            // 如果设置为true，x轴和y轴可以用2根手指同时缩放，如果设置为false，x轴和y轴可以分别缩放。默认值:false
            barChart.setPinchZoom(true);
            // 绘制网格背景
            barChart.setDrawGridBackground(false);

            XAxis xl = barChart.getXAxis();
            ValueFormatter xAxisFormatter = new DayAxisValueFormatter(teas, xl);
            xl.setPosition(XAxis.XAxisPosition.BOTTOM);
            xl.setTypeface(tfLight);
            xl.setDrawAxisLine(true);
            // 在缩放时为轴设置一个最小间隔。坐标轴不能低于这个极限。这可以用来避免标签复制时，放大。
            xl.setGranularity(3f);
            xl.setLabelCount(25);
            xl.setCenterAxisLabels(true);
            xl.setValueFormatter(xAxisFormatter);

            ValueFormatter custom = new MyValueFormatter();

            YAxis leftAxis = barChart.getAxisLeft();
            leftAxis.setTypeface(tfLight);
            leftAxis.setDrawGridLines(false);
            leftAxis.setValueFormatter(custom);
            leftAxis.setSpaceTop(5f);
            leftAxis.setAxisMinimum(0f);

            YAxis rightAxis = barChart.getAxisRight();
            rightAxis.setTypeface(tfLight);
            rightAxis.setDrawGridLines(false);
            rightAxis.setValueFormatter(custom);
            rightAxis.setSpaceTop(5f);
            rightAxis.setAxisMinimum(0f);

            barChart.setFitBars(true);
            barChart.animateY(2500);

            Legend l = barChart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            l.setDrawInside(false);


            float groupSpace = 0.07f;
            float barSpace = 0.03f; // x4 DataSet
            float barWidth = 0.94f; // x4 DataSet

            BarDataSet set1, set2, set3;

            if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {

                set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
                set2 = (BarDataSet) barChart.getData().getDataSetByIndex(1);
                set3 = (BarDataSet) barChart.getData().getDataSetByIndex(2);
                set1.setValues(values1);
                set2.setValues(values2);
                set3.setValues(values3);
                barChart.getData().notifyDataChanged();
                barChart.notifyDataSetChanged();
            } else {
                // create 4 DataSets
                set1 = new BarDataSet(values1, "今日课");
                set1.setColor(Color.rgb(105, 172, 229));
                set2 = new BarDataSet(values2, "出勤完成");
                set2.setColor(Color.rgb(163, 214, 92));
                set3 = new BarDataSet(values3, "发布课程反馈");
                set3.setColor(Color.rgb(230, 230, 230));

                BarData data = new BarData(set1, set2, set3);
                barChart.setData(data);
            }
            // specify the width each bar should have
            barChart.getBarData().setBarWidth(barWidth);

            // restrict the x-axis range
            barChart.getXAxis().setAxisMinimum(0);

            // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
            int group = 3;// 同时显示多少组
            if (teas.size() > 3) {
                group = teas.size();
            }
            barChart.getXAxis().setAxisMaximum(0 + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * group);
            barChart.groupBars(0, groupSpace, barSpace);
            barChart.invalidate();
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
