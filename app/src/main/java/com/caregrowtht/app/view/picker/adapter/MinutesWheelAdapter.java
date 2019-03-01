package com.caregrowtht.app.view.picker.adapter;


import com.contrarywind.adapter.WheelAdapter;

/**
 * Numeric Wheel adapter.
 * 以5分钟间隔显示
 */
public class MinutesWheelAdapter implements WheelAdapter {

    private int minValue;
    private int maxValue;

    /**
     * Constructor
     *
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     */
    public MinutesWheelAdapter(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public Object getItem(int index) {
        if (index >= 0 && index < getItemsCount()) {
            return (minValue + index) * 5;
        }
        return 0;
    }

    @Override
    public int getItemsCount() { //0 5 10 15 20 25 30 35 40 45 50 55
        return (maxValue + 1) / 5;
    }

    @Override
    public int indexOf(Object o) {
        try {
            return (int) o - minValue;
        } catch (Exception e) {
            return -1;
        }

    }
}
