package com.caregrowtht.app.adapter;

import android.app.Activity;
import android.text.TextUtils;

import com.android.library.utils.DateUtil;
import com.android.library.view.ExpandListView;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CourseEntity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by haoruigang on 2018/4/23 17:22.
 * 课程表
 */

public class CourseAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private Activity mActivity;
    private String selectDay;
    private ArrayList<CourseEntity> listData = new ArrayList<>();
    private String today;
    private int cardType = 1;//判断课程卡点击跳转 1:点击课表放大 2:选择排课班级里的成员
    private int type = 1;//1：我的课表 2：机构课表 3：跨机构课表

    public CourseAdapter(Activity mActivity, int layoutResId, ArrayList<String> data, String today, int cardType) {
        super(layoutResId, data);
        this.mActivity = mActivity;
        this.selectDay = today;
        this.today = today;
        this.cardType = cardType;
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        bubbleSort(listData);
        helper.setText(R.id.tv_week, TextUtils.equals(today, getDateOfMonth("yyyy-MM-dd", helper.getPosition())) ? "今天" : Constant.weekArr[helper.getPosition()]);
        helper.setText(R.id.tv_date, getDateOfMonth("dd", helper.getPosition()));
        ExpandListView listView = helper.getView(R.id.lv_course);
        listView.setAdapter(new CourseItemAdapter(mActivity, getEveryDayData(helper.getPosition()), cardType, type));
        helper.setBackgroundColor(R.id.ll_header, TextUtils.equals(today, getDateOfMonth("yyyy-MM-dd", helper.getPosition())) ? mActivity.getResources().getColor(R.color.blueMaskDark) : mActivity.getResources().getColor(R.color.transparent));
    }

    /**
     * Bubble sort.
     *
     * @param listData
     */
    private void bubbleSort(ArrayList<CourseEntity> listData) {
        CourseEntity temp;
        for (int i = 0; i < listData.size() - 1; i++) {
            for (int j = 1; j < listData.size() - i; j++) {
                if (Long.parseLong(listData.get(j - 1).getStartAt()) > Long.parseLong(listData.get(j).getStartAt())) {
                    // Switch
                    temp = listData.get(j - 1);
                    listData.set((j - 1), listData.get(j));
                    listData.set(j, temp);
                }
            }
        }
    }

    /**
     * Handle the data.
     *
     * @param dayIndex
     */
    private ArrayList<CourseEntity> getEveryDayData(int dayIndex) {
        ArrayList<CourseEntity> list = new ArrayList<>();
        for (CourseEntity entity : listData) {
            int dayOfWeek = getDayOfWeek(entity.getStartAt());
            //  Logger.d("getDayOfWeek  " + (dayOfWeek - 1) + "  " + dayIndex);
            if (dayOfWeek - 1 == dayIndex) {
                list.add(entity);
            }
        }
        return list;
    }

    /**
     * Get what day of week.
     *
     * @param timeStamp
     * @return
     */
    private int getDayOfWeek(String timeStamp) {

        String date = DateUtil.getDate(Long.valueOf(timeStamp), "yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int week = cal.get(Calendar.DAY_OF_WEEK) - 1 == 0 ? 7 : cal.get(Calendar.DAY_OF_WEEK) - 1;
        return week;
    }

    /**
     * get the day is which day in this week.
     *
     * @param type
     * @param position
     * @return
     */
    public String getDateOfMonth(String type, int position) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(selectDay));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int d;
        if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
            d = -6;
        } else {
            d = 2 - cal.get(Calendar.DAY_OF_WEEK);
        }
        cal.add(Calendar.DAY_OF_WEEK, d);
        cal.add(Calendar.DAY_OF_WEEK, position);
        String day = new SimpleDateFormat(type).format(cal.getTime());
        return day;
    }

    /**
     * 是否是这周中的这一天
     *
     * @param position
     * @return
     */
    private boolean isToday(int position) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int week = cal.get(Calendar.DAY_OF_WEEK) == 1 ? 6 : cal.get(Calendar.DAY_OF_WEEK) - 2;
        Logger.d(today + "  " + week + "  " + position);
        return week == position;
    }

    public void update(String theDay, ArrayList<CourseEntity> listData, int type) {
        this.listData = listData;
        selectDay = theDay;
        this.type = type;
        notifyDataSetChanged();
    }

}
