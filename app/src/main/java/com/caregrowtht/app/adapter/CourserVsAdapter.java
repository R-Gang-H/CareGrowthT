package com.caregrowtht.app.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.cardview.widget.CardView;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 课时卡Vs排课 适配器
 * haoruigang on 2018年10月24日18:52:46
 */
public class CourserVsAdapter extends XrecyclerAdapter {

    private List<CourseEntity> courseAllList = new ArrayList<>();
    private List<CourseEntity> courseVsList = new ArrayList<>();
    private CourseEntity courseCardEntity = new CourseEntity();

    @BindView(R.id.iv_course_icon)
    CardView ivCourseIcon;
    @BindView(R.id.tv_course_type)
    TextView tvCourseType;
    @BindView(R.id.tv_unit)
    TextView tvUnit;
    @BindView(R.id.et_number)
    public EditText etNumber;
    @BindView(R.id.tv_select_course)
    public CheckBox tvSelectCourse;

    private String ISYEARCARD = "ISYEARCARD";// 是年卡

    public CourserVsAdapter(List datas, Context context, ViewOnItemClick onItemClick) {
        super(datas, context, onItemClick);
    }

    @Override
    public void onBindViewHolder(XrecyclerViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        // 强行关闭复用
        viewHolder.setIsRecyclable(false);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        CourseEntity courseEntity = courseAllList.get(position);

        setVisibilityView(false, false, false);

        ivCourseIcon.setCardBackgroundColor(TextUtils.isEmpty(courseEntity.getColor())
                ? ResourcesUtils.getColor(R.color.blue)
                : Color.parseColor(courseEntity.getColor()));
        tvCourseType.setText(courseEntity.getCourseName());
        etNumber.setEnabled(false);

        // 卡的类型 1：次卡 2：储值卡 3：年卡 4：折扣卡
        if (TextUtils.equals(courseCardEntity.getCardType(), "1")) {
            tvUnit.setText("次");
        } else {
            tvUnit.setText("元");
        }
        String Number = getNumber(courseEntity);
        if (!TextUtils.isEmpty(Number) &&
                !Number.equals(ISYEARCARD)) {
            setVisibilityView(true, true, false);
            etNumber.setText(Number);
        } else if (!TextUtils.isEmpty(Number) &&
                Number.equals(ISYEARCARD)) {
            if (TextUtils.equals(courseCardEntity.getCardType(), "3")) {
                setVisibilityView(false, false, true);
                tvSelectCourse.setSelected(Number.equals(ISYEARCARD));
            }
        }
    }

    private String getNumber(CourseEntity courseEntity) {
        for (int i = 0; i < courseVsList.size(); i++) {
            CourseEntity courseVs = courseVsList.get(i);
            if (courseVs != null) {
                if (TextUtils.equals(courseEntity.getCourseId(), courseVs.getId())) {
                    if (TextUtils.equals(courseCardEntity.getCardType(), "1")) {
                        String singleTimes = courseVs.getSingleTimes();
                        if (!TextUtils.equals(singleTimes, "0")) {
                            return singleTimes;
                        } else {
                            return null;
                        }
                    } else if (TextUtils.equals(courseCardEntity.getCardType(), "3")) {
                        if (courseVs != null && courseVs.getCourseId() != null) {
                            return ISYEARCARD;
                        } else {
                            return null;
                        }
                    } else {
                        String singleMoney = courseVs.getSingleMoney();
                        if (!TextUtils.equals(singleMoney, "0")) {
                            return String.valueOf(Integer.parseInt(singleMoney) / 100);
                        } else {
                            return null;
                        }
                    }
                }
            }
        }
        return null;
    }


    private void setVisibilityView(boolean isNubmer, boolean isUnit, boolean isSelect) {
        etNumber.setVisibility(isNubmer ? View.VISIBLE : View.GONE);
        tvUnit.setVisibility(isUnit ? View.VISIBLE : View.GONE);
        tvSelectCourse.setVisibility(isSelect ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return courseAllList.size();
    }

    public void setData(List<CourseEntity> courseAllList, List<CourseEntity> courseVsList, CourseEntity courseCardEntity) {
        this.courseAllList.clear();
        this.courseAllList.addAll(courseAllList);
        this.courseVsList.clear();
        this.courseVsList.addAll(courseVsList);
        this.courseCardEntity = courseCardEntity;
        notifyDataSetChanged();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_course_vs;
    }

    /**
     * 本地刷新
     */
    public void refresh(CourseEntity courseData, boolean isSelected) {

        String courseId = courseData.getCourseId();
        String courseName = courseData.getCourseName();
        String color = courseData.getColor();
        String count = courseData.getSingleTimes();
        String prict = courseData.getSingleMoney();

        CourseEntity courseEntity = null;
        for (int i = 0; i < courseVsList.size(); i++) {
            if (TextUtils.equals(courseVsList.get(i).getId(), courseId)) {
                courseEntity = courseVsList.get(i);
                if (TextUtils.equals(courseCardEntity.getCardType(), "3")) {
                    if (!isSelected) {// 取消关联,课程id为空
                        courseId = null;
                    }
                }
                courseVsList.remove(i);
                break;
            }
        }
        if (courseEntity == null) {
            courseEntity = new CourseEntity();
        }
        courseEntity.setCourseId(courseId);
        courseEntity.setCourseName(courseName);
        courseEntity.setColor(color);
        courseEntity.setSingleTimes(count);
        courseEntity.setSingleMoney(prict);
        courseVsList.add(courseEntity);
        notifyDataSetChanged();
    }

}
