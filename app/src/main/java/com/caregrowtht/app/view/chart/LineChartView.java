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
import android.util.AttributeSet;
import android.util.Log;

import org.xclcharts.chart.LineChart;
import org.xclcharts.chart.LineData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.plot.PlotLegend;

import java.text.DecimalFormat;
import java.util.LinkedList;

/**
 * @author XiongChuanLiang<br />(xcl_168@aliyun.com)
 * @ClassName LineChart01View
 * @Description 折线图的例子 <br/>
 * * 	~_~
 */
public class LineChartView extends DemoView {

    private String TAG = "LineChartView";
    private LineChart chart = new LineChart();

    //标签集合
    private LinkedList<String> labels = new LinkedList<>();
    private LinkedList<LineData> chartData = new LinkedList<>();

    public LineChartView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        initView();
    }

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LineChartView(Context context, AttributeSet attrs, int defStyle) {
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
        chart.setChartRange(w, h);
    }

    public void chartRender(double axisMax) {
        try {

            //设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
            int[] ltrb = getBarLnDefaultSpadding();
            chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);

            //设定数据源
            chart.setCategories(labels);
            chart.getCategoryAxis().getTickLabelPaint()
                    .setTextSize(DensityUtil.dip2px(getContext(), 10));
            chart.setDataSource(chartData);
            if (axisMax == 0) {
                axisMax = 1;
            }

            String max = String.valueOf(Math.round(axisMax));
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < max.length(); i++) {
                sb.append("0");
            }
            int num = Integer.valueOf(max.substring(0, 1)) + 1;
            axisMax = Integer.valueOf(String.format("%s%s", num, sb.toString()));

            //数据轴最大值
            chart.getDataAxis().setAxisMax(axisMax);
            //数据轴刻度间隔
            chart.getDataAxis().setAxisSteps(axisMax / (num * 10) * 10);
            //指隔多少个轴刻度(即细刻度)后为主刻度
            chart.getDataAxis().setDetailModeSteps(1);

            chart.getDataAxis().getTickLabelPaint()
                    .setTextSize(DensityUtil.dip2px(getContext(), 10));

            //激活点击监听
            chart.ActiveListenItemClick();
            //为了让触发更灵敏，可以扩大5px的点击监听范围
            chart.extPointClickRange(5);
            chart.showClikedFocus();

            //显示图例
            PlotLegend legend = chart.getPlotLegend();
            legend.show();
            legend.getPaint().setTextSize(DensityUtil.dip2px(getContext(), 10));
            legend.setOffsetY(DensityUtil.dip2px(getContext(), 10));
            legend.setHorizontalAlign(XEnum.HorizontalAlign.CENTER);

            //定义数据轴标签显示格式
            chart.getDataAxis().setLabelFormatter(value -> {
                // TODO Auto-generated method stub
                Double tmp = Double.parseDouble(value);
                DecimalFormat df = new DecimalFormat("#0");
                String label = df.format(tmp);
                return (label);
            });

            //绘制十字交叉线
            chart.showDyLine();
            chart.getDyLine().setDyLineStyle(XEnum.DyLineStyle.Cross);
            //收缩绘图区右边分割的范围，让绘图区的线不显示出来
            chart.getClipExt().setExtLeft(200.f);
            chart.getClipExt().setExtRight(150.f);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e(TAG, e.toString());
        }
    }

    public void chartLabels(LinkedList<String> labels) {
        this.labels.clear();
        if (labels.size() > 8) {
            for (int i = 0; i < labels.size(); i++) {
                if (i % 5 == 0) {
                    this.labels.add(labels.get(i));
                } else {
                    this.labels.add("");
                }
            }
        } else {
            this.labels.addAll(labels);
        }
    }

    public void chartDataSet(LinkedList<LineData> chartData) {
        this.chartData.clear();
        this.chartData.addAll(chartData);
    }

    @Override
    public void render(Canvas canvas) {
        try {
            chart.render(canvas);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

}
