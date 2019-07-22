/**
 * Copyright 2014  XCL-Charts
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @Project XCL-Charts
 * @Description Android图表基类库
 * @author XiongChuanLiang<br />(xcl_168@aliyun.com)
 * @Copyright Copyright (c) 2014 XCL-Charts (www.xclcharts.com)
 * @license http://www.apache.org/licenses/  Apache v2 License
 * @version 1.0
 */
package com.caregrowtht.app.view.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.caregrowtht.app.R;
import com.caregrowtht.app.uitil.ResourcesUtils;

import org.xclcharts.chart.BarChart;
import org.xclcharts.chart.BarData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.event.click.BarPosition;
import org.xclcharts.renderer.XEnum;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * @author XiongChuanLiang<br />(xcl_168@aliyun.com)
 * @ClassName BarChart02View
 * @Description 柱形图例子(横向)
 */
public class HorizontalBarChartView extends DemoView {

    private static final String TAG = "HorizontalBarChartView";
    private BarChart chart = new BarChart();

    //标签轴
    private List<String> chartLabels = new LinkedList<String>();
    private List<BarData> chartData = new LinkedList<BarData>();

    public HorizontalBarChartView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        initView();
    }

    public HorizontalBarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public HorizontalBarChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        //綁定手势滑动事件
//        this.bindTouch(this, chart);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //图所占范围大小
        if (null != chart) chart.setChartRange(w, h);
    }


    public void chartRender() {
        try {
            //设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
            int[] ltrb = getBarDefaultSpadding();
            chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);

            //数据源
            chart.setDataSource(chartData);
            chart.setCategories(chartLabels);

            //数据轴
            chart.getDataAxis().setAxisMax(10);
            chart.getDataAxis().setAxisMin(0);
            chart.getDataAxis().setAxisSteps(1);

            // 图例
            chart.getPlotLegend().getPaint().setTextSize(DensityUtil.dip2px(getContext(), 12));
            chart.getPlotLegend().setOffsetY(DensityUtil.dip2px(getContext(), 10));
            chart.getPlotLegend().setHorizontalAlign(XEnum.HorizontalAlign.CENTER);

            chart.getDataAxis().getTickLabelPaint().setTextSize(DensityUtil.dip2px(getContext(), 12));
            chart.getDataAxis().getTickLabelPaint().setColor(ResourcesUtils.getColor(R.color.color_84));
            chart.getDataAxis().getAxisPaint().setColor(ResourcesUtils.getColor(R.color.col_e0));
            chart.getDataAxis().getTickMarksPaint().setColor(ResourcesUtils.getColor(R.color.col_e0));

            chart.getDataAxis().setLabelFormatter(value -> {
                // TODO Auto-generated method stub
                Double tmp = Double.parseDouble(value);
                DecimalFormat df = new DecimalFormat("#0");
                String label = df.format(tmp);
                return (label);
            });

            //网格
            chart.getPlotGrid().showHorizontalLines();
            chart.getPlotGrid().showVerticalLines();
            chart.getPlotGrid().getHorizontalLinePaint().setColor(ResourcesUtils.getColor(R.color.col_e0));
            chart.getPlotGrid().getVerticalLinePaint().setColor(ResourcesUtils.getColor(R.color.col_e0));
            chart.showBarEqualAxisMin();

            //标签轴文字旋转-45度
            chart.getCategoryAxis().setTickLabelRotateAngle(-45f);
            chart.getCategoryAxis().getTickLabelPaint().setTextSize(DensityUtil.dip2px(getContext(), 12));
            chart.getCategoryAxis().getTickLabelPaint().setColor(ResourcesUtils.getColor(R.color.color_84));
            chart.getCategoryAxis().getAxisPaint().setColor(ResourcesUtils.getColor(R.color.col_e0));
            chart.getCategoryAxis().getTickMarksPaint().setColor(ResourcesUtils.getColor(R.color.col_e0));
            //横向显示柱形
            chart.setChartDirection(XEnum.Direction.HORIZONTAL);
            //在柱形顶部显示值
            chart.getBar().setItemLabelVisible(true);
            chart.getBar().getItemLabelPaint().setTextSize(DensityUtil.dip2px(getContext(), 8));
            chart.getBar().setBarInnerMargin(0.03f);
            chart.getBar().setBarTickSpacePercent(0.8f);

            //激活点击监听
            chart.ActiveListenItemClick();
            chart.showClikedFocus();

            chart.setItemLabelFormatter(value -> {
                // TODO Auto-generated method stub
                DecimalFormat df = new DecimalFormat("#0");
                String label = df.format(value);
                return label;
            });

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e(TAG, e.toString());
        }


    }

    public void chartDataSet(List<BarData> barData) {
        //标签对应的柱形数据集
        this.chartData.clear();
        this.chartData.addAll(barData);
    }

    public void chartLabels(List<String> chartLabels) {
        this.chartLabels.clear();
        this.chartLabels.addAll(chartLabels);
    }


    @Override
    public void render(Canvas canvas) {
        try {
            chart.render(canvas);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            triggerClick(event.getX(), event.getY());
        }
        return true;
    }


    //触发监听
    private void triggerClick(float x, float y) {
        BarPosition record = chart.getPositionRecord(x, y);
        if (null == record) return;

        BarData bData = chartData.get(record.getDataID());
        Double bValue = bData.getDataSet().get(record.getDataChildID());

//        Toast.makeText(this.getContext(),
//                "info:" + record.getRectInfo() +
//                        " Key:" + bData.getKey() +
//                        " Current Value:" + Double.toString(bValue),
//                Toast.LENGTH_SHORT).show();

        chart.showFocusRectF(record.getRectF());
        chart.getFocusPaint().setStyle(Style.STROKE);
        chart.getFocusPaint().setStrokeWidth(3);
        chart.getFocusPaint().setColor(Color.GREEN);
        this.invalidate();
    }

}
