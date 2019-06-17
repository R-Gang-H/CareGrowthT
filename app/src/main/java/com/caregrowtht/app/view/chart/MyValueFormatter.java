package com.caregrowtht.app.view.chart;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class MyValueFormatter extends ValueFormatter {


    public MyValueFormatter() {
    }

    @Override
    public String getFormattedValue(float value) {
        if (value == (int) value) {
            //是整数
            return (int) value + "";
        } else {
            //不是整数
            return "";
        }
    }

}
