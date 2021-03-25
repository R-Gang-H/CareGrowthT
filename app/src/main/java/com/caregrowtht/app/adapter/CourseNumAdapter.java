package com.caregrowtht.app.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class CourseNumAdapter extends XrecyclerAdapter {

    List<CourseEntity> courseData = new ArrayList<>();
    @BindView(R.id.tv_day)
    TextView tvDay;
    @BindView(R.id.cv_radius)
    ImageView cvRadius;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.rl_day)
    RelativeLayout rlDay;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_stu_detail)
    TextView tvStuDetail;
    @BindView(R.id.tv_stu_num)
    TextView tvStuNum;

    public CourseNumAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        CourseEntity courseEntity = courseData.get(position);
        if (position == 0 || !courseEntity.getType()
                .equals(courseData.get(position - 1).getType())) {
            rlDay.setVisibility(View.VISIBLE);
            String typeContent = "";
            switch (courseEntity.getType()) {
                case "1":
                    typeContent = "今天";
                    break;
                case "2":
                    typeContent = "过去7天";
                    break;
                case "3":
                    typeContent = "7天之前";
                    break;
                case "4":
                    typeContent = "未来7天";
                    break;
                case "5":
                    typeContent = "未来7—14天";
                    break;
            }
            tvDay.setText(typeContent);
        } else {
            rlDay.setVisibility(View.GONE);
        }

        long courseStartAt = Long.valueOf(courseEntity.getCourseBeginAt());
        String Month = TimeUtils.getDateToString(courseStartAt, "MM月dd日");
        String data = DateUtil.getDate(courseStartAt, "yyyy年MM月dd日");
        String week = TimeUtils.getWeekByDateStr(data);//获取周几
        String time = TimeUtils.getDateToString(courseStartAt, "HH:mm");
        String teacherName = courseEntity.getTeacherName();
        String courseName = courseEntity.getCourseName();
        tvContent.setText(String.format("%s\t\t%s\t\t%s\n%s\t\t%s",
                Month, week, time, teacherName, courseName));
        String childNum = courseEntity.getStuCount();
        String MinStuCount = courseEntity.getMinStuCount();
        tvStuDetail.setText(String.format("学员\t%s人", childNum));
        tvStuNum.setText(String.format("最小开班人数：%s人", MinStuCount));
    }

    public void setData(List<CourseEntity> courseData) {
        this.courseData.clear();
        this.courseData.addAll(courseData);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return courseData.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_course_num;
    }
}
