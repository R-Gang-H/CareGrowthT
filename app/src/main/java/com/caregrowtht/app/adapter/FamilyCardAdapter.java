package com.caregrowtht.app.adapter;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 家庭共享课程卡
 */
public class FamilyCardAdapter extends XrecyclerAdapter {

    //课程卡信息
    private List<CourseEntity> courseTypeDatas = new ArrayList<>();

    // 用来控制CheckBox的选中状况
    private HashMap<Integer, Boolean> isSelected = new HashMap<>();
    //选中课程卡的信息
    private HashMap<Integer, CourseEntity> mCourseModels = new HashMap<>();
    //选中课程卡的消课次数
    private HashMap<Integer, CourseEntity> mCount = new HashMap<>();
    public Boolean isAll = false;

    public FamilyCardAdapter(List datas, Context context) {
        super(datas, context);
        this.courseTypeDatas.addAll(datas);
        // 初始化数据
        initDate();
    }

    // 初始化isSelected的数据
    public void initDate() {
        if (courseTypeDatas.size() > 0) {
            for (int i = 0; i < courseTypeDatas.size(); i++) {
                getIsSelected().put(i, isAll);
                CourseEntity courseEntity = new CourseEntity();
                courseEntity.setCourseCardId(courseTypeDatas.get(i).getCourseCardId());
                getIsCourseEntity().put(i, isAll ? courseTypeDatas.get(i) : null);
            }
        }
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        CourseEntity courseEntity = courseTypeDatas.get(position);
        final CheckBox tvSelectCourse = (CheckBox) holder.getView(R.id.tv_select_course);
        String cardInfo = null;
        //cardType 卡的类型 1：次卡 2：储值卡 3：年卡 4：折扣卡
        switch (courseEntity.getCardType()) {
            case "1":
                cardInfo = String.format("%s/%s元/%s次", courseEntity.getCardName(),
                        Long.parseLong(courseEntity.getRealityPrice()) / 100,
                        Float.parseFloat(courseEntity.getLeftCount()));
                break;
            case "2":
                cardInfo = String.format("%s/%s元/%s元", courseEntity.getCardName(),
                        Long.parseLong(courseEntity.getRealityPrice()) / 100,
                        Long.parseLong(courseEntity.getLeftPrice()) / 100);
                break;
            case "3":
                cardInfo = String.format("%s", courseEntity.getCardName());
                break;
            case "4":
                cardInfo = String.format("%s/%s折/%s元", courseEntity.getCardName(),
                        Long.parseLong(courseEntity.getDiscount()),
                        Long.parseLong(courseEntity.getLeftPrice()) / 100);
                break;
        }
        tvSelectCourse.setText(cardInfo);
        // 根据isSelected来设置checkbox的选中状况
        tvSelectCourse.setSelected(getIsSelected().get(position));
        tvSelectCourse.setOnClickListener(v -> {
            getSelect(position, tvSelectCourse);
        });
    }

    @Override
    public int getItemCount() {
        return courseTypeDatas.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_work_course;
    }

    public void getSelect(final int position, final TextView tvSelectCourse) {
        if (courseTypeDatas.size() > 0) {
            tvSelectCourse.setSelected(!tvSelectCourse.isSelected());
            getIsSelected().put(position, tvSelectCourse.isSelected());// 同时修改map的值保存状态
            CourseEntity courseEntity = new CourseEntity();
            courseEntity.setCourseCardId(courseTypeDatas.get(position).getCourseCardId());
            getIsCourseEntity().put(position, tvSelectCourse.isSelected() ? courseTypeDatas.get(position) : null);
        }
    }

    public HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public HashMap<Integer, CourseEntity> getIsCourseEntity() {
        return mCourseModels;
    }

}
