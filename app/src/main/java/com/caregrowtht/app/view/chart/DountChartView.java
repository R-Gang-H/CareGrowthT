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
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import org.xclcharts.chart.DountChart;
import org.xclcharts.chart.PieData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.event.click.ArcPosition;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.plot.PlotLegend;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author XiongChuanLiang<br />(xcl_168@aliyun.com)
 * @ClassName DountChart01View
 * @Description 环形图例子
 */
public class DountChartView extends DemoView implements Runnable {

    private String TAG = "DountChartView";
    private DountChart chart = new DountChart();

    LinkedList<PieData> lPieData = new LinkedList<PieData>();

    public DountChartView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        initView();
    }

    public DountChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DountChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        new Thread(this).start();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //图所占范围大小
        chart.setChartRange(w, h);
    }

    public void chartRender() {
        try {
            //设置绘图区默认缩进px值
            int[] ltrb = getDountDefaultSpadding();
            chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);

            //设置起始偏移角度(即第一个扇区从哪个角度开始绘制)
            //chart.setInitialAngle(90);

            //标签显示(隐藏，显示在中间，显示在扇区外面)
            chart.setLabelStyle(XEnum.SliceLabelStyle.INSIDE);
            chart.getLabelPaint().setColor(Color.WHITE);
            chart.getLabelPaint().setTextSize(30);

//            chart.setDataSource(lPieData);

            //激活点击监听
            chart.ActiveListenItemClick();
            chart.showClikedFocus();

            //设置允许的平移模式
            //chart.enablePanMode();
            //chart.setPlotPanMode(XEnum.PanMode.HORIZONTAL);

            //显示图例
            PlotLegend legend = chart.getPlotLegend();
            legend.show();
            legend.getPaint().setTextSize(DensityUtil.dip2px(getContext(), 12));
            legend.setType(XEnum.LegendType.COLUMN);
            legend.setHorizontalAlign(XEnum.HorizontalAlign.RIGHT);
            legend.setOffsetX(DensityUtil.dip2px(getContext(), 20));
            legend.setVerticalAlign(XEnum.VerticalAlign.MIDDLE);
            legend.hideBorder();
            legend.hideBackground();

            //可用这个修改环所占比例
            chart.setInnerRadius(0.4f);
            chart.setInitialAngle(90.f);

            //保存标签位置
            chart.saveLabelsPosition(XEnum.LabelSaveType.ALL);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e(TAG, e.toString());
        }
    }

    public void chartDataSet(ArrayList<PieData> chartData) {
        //设置图表数据源
        this.lPieData.clear();
        this.lPieData.addAll(chartData);
    }

    @Override
    public void render(Canvas canvas) {
        try {
            chart.render(canvas);
            
            /*
             * 
             * 在显示标签的位置显示图片:
             * 
             1.chart.saveLabelsPosition(XEnum.LabelSaveType.ONLYPOSITION);
             2. 返回各标签位置
	          
    		*/
            
           
            /*
             * 贴图的例子代码：
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.pieaa);  
            
            ArrayList<PlotArcLabelInfo> mLstLabels = chart.getLabelsPosition();	    
            for(PlotArcLabelInfo info: mLstLabels)
    		{
            	PointF pos = info.getLabelPointF();
            	if(null == pos)continue;
            	//String posXY = " x="+Float.toString(pos.x)+" y="+Float.toString(pos.y);
            	//Log.e("Pie","label="+lPieData.get(info.getID())+" "+posXY);	   
            	
            	canvas.drawBitmap(bmp, pos.x, pos.y, null); 
    		}
            */

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
        if (!chart.getListenItemClickStatus()) return;
        ArcPosition record = chart.getPositionRecord(x, y);
        if (null == record) return;

        PieData pData = lPieData.get(record.getDataID());

        boolean isInvaldate = true;
        for (int i = 0; i < lPieData.size(); i++) {
            PieData cData = lPieData.get(i);
            if (i == record.getDataID()) {
                if (cData.getSelected()) {
                    isInvaldate = false;
                    break;
                } else {
                    cData.setSelected(true);
                }
            } else
                cData.setSelected(false);
        }

        if (isInvaldate) this.invalidate();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            chartAnimation();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    private void chartAnimation() {
        try {

            chart.setDataSource(lPieData);
            int count = 360 / 10;

            for (int i = 1; i < count; i++) {
                Thread.sleep(40);

                chart.setTotalAngle(10 * i);

                //激活点击监听
                if (count - 1 == i) {
                    chart.setTotalAngle(360);

                    chart.ActiveListenItemClick();
                    //显示边框线，并设置其颜色
//                    chart.getArcBorderPaint().setColor(Color.YELLOW);
//                    chart.getArcBorderPaint().setStrokeWidth(3);
                }

                postInvalidate();
            }

        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }

    }

}
