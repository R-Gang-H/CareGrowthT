package com.caregrowtht.app.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.library.utils.DateUtil;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.BaseActivity;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SignLevaeCourseAdapter extends XrecyclerAdapter {

    @BindView(R.id.tv_day)
    TextView tvDay;
    @BindView(R.id.rl_day)
    RelativeLayout rlDay;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_stu_detail)
    TextView tvStuDetail;
    @BindView(R.id.tv_stu_num)
    TextView tvStuNum;
    @BindView(R.id.tv_levae_num)
    TextView tvLevaeNum;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.rl_sign)
    RelativeLayout rlSign;

    List<CourseEntity> courseData = new ArrayList<>();
    private String showType;

    public SignLevaeCourseAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
        this.courseData.addAll(datas);
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
                    typeContent = "7天内";
                    break;
                case "3":
                    typeContent = "7天之前";
                    break;
            }
            tvDay.setText(typeContent);
        } else {
            rlDay.setVisibility(View.GONE);
        }
        switch (showType) {
            case "9":// 9：有学员请假汇总动态
                tvStuDetail.setVisibility(View.GONE);
                tvStuNum.setVisibility(View.GONE);
                ((BaseActivity) mContext).initRecyclerView(recyclerView, false);
                recyclerView.setAdapter(new LeaveStuAdapter((List) courseEntity.getStudents(), mContext));
                break;
            case "10":// 10：签到提醒汇总动态
                rlSign.setVisibility(View.GONE);
                tvStuNum.setVisibility(View.GONE);
                break;
            case "12":// 12：未发布课程反馈提醒汇总动态
                tvStuDetail.setVisibility(View.GONE);
                rlSign.setVisibility(View.GONE);
                break;
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
        String sign = courseEntity.getSignCount();
        String leav = courseEntity.getLeaveCount();
        String noTake = courseEntity.getWaitCount();
        tvStuDetail.setText(Html.fromHtml(String.format("学员\t%s人\t\t|\t\t签到\t%s人\t\t|\t\t请假\t%s人" +
                        "\t\t|\t\t待处理\t<font color='#F54949'>%s</font>人",
                childNum, sign, leav, noTake)));
        tvStuNum.setText(String.format("学员%s人", courseEntity.getStuCount()));
        tvLevaeNum.setText(String.format("%s人请假", courseEntity.getLeaveCount()));
    }

    public void setData(List<CourseEntity> courseData, String showType) {
        this.courseData.clear();
        this.courseData.addAll(courseData);
        this.showType = showType;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return courseData.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_stu_leave;
    }
}
