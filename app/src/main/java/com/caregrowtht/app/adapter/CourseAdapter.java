package com.caregrowtht.app.adapter;

import android.app.Activity;
import android.text.TextUtils;

import com.android.library.view.ExpandListView;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.uitil.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by haoruigang on 2018/4/23 17:22.
 * 课程表
 */

public class CourseAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private Activity mActivity;
    private String selectDay;
    private ArrayList<CourseEntity> listData = new ArrayList<>();
    private String today;
    private int cardType = 1;//判断课程卡点击跳转 1:点击课表放大 2:选择排课班级里的成员 3:预约课提醒
    private int type = 1;//1：我的课表 2：机构课表 3：跨机构课表 0:预约课提醒

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
        helper.setText(R.id.tv_week, TextUtils.equals(today, TimeUtils.getDayOfWeek("yyyy-MM-dd", helper.getPosition() + 1, selectDay)) ? "今天" : Constant.weekArr[helper.getPosition()]);
        helper.setText(R.id.tv_date, TimeUtils.getDayOfWeek("dd", helper.getPosition() + 1, selectDay));
        ExpandListView listView = helper.getView(R.id.lv_course);
        listView.setAdapter(new CourseItemAdapter(mActivity, getEveryDayData(helper.getPosition()), cardType, type));
        helper.setBackgroundColor(R.id.ll_header, TextUtils.equals(today, TimeUtils.getDayOfWeek("yyyy-MM-dd", helper.getPosition() + 1, selectDay))
                ? mActivity.getResources().getColor(R.color.blueMaskDark) : mActivity.getResources().getColor(R.color.transparent));
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
            int dayOfWeek = TimeUtils.getDayOfWeek(Long.valueOf(entity.getStartAt()));
            //  Logger.d("getDayOfWeek  " + (dayOfWeek - 1) + "  " + dayIndex);
            if (dayOfWeek - 1 == dayIndex) {
                list.add(entity);
            }
        }
        return list;
    }

    public void update(String theDay, ArrayList<CourseEntity> listData, int type) {
        this.listData = listData;
        selectDay = theDay;
        this.type = type;
        notifyDataSetChanged();
    }

}
