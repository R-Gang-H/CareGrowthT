package com.caregrowtht.app.view.chart;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

/**
 *
 */
public class DayAxisValueFormatter extends ValueFormatter {

    ArrayList<String> teachers = new ArrayList<>();
    XAxis xl;

    public DayAxisValueFormatter(ArrayList<String> teachers, XAxis xl) {
        this.teachers.clear();
        this.teachers.addAll(teachers);
        this.xl = xl;
    }

    @Override
    public String getFormattedValue(float value) {
        try {
            int position = (int) value / 3;
            if (position > -1 && (int) value % 3 == 0 && position < teachers.size()) {
                return teachers.get(position);
            }
        } catch (IndexOutOfBoundsException e) {
            xl.setGranularityEnabled(false);
        }
        return "";
    }

}
