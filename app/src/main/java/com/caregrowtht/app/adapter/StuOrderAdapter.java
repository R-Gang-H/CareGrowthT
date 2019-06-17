package com.caregrowtht.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.library.utils.DateUtil;
import com.caregrowtht.app.Constant;
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

public class StuOrderAdapter extends XrecyclerAdapter {

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
    private String today;

    public StuOrderAdapter(List datas, String today, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
        this.courseData.addAll(datas);
        this.today = today;
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        CourseEntity courseEntity = courseData.get(position);

        tvStuDetail.setVisibility(View.GONE);
        tvStuNum.setVisibility(View.GONE);
        ((BaseActivity) mContext).initRecyclerView(recyclerView, false);
        recyclerView.setAdapter(new LeaveStuAdapter((List) courseEntity.getStudents(), mContext));

        long startAt = Long.valueOf(courseEntity.getStartAt());
        String Month = TimeUtils.getDateToString(startAt, "MM月dd日");
        String data = DateUtil.getDate(startAt, "yyyy年MM月dd日");
        String week = TimeUtils.getWeekByDateStr(data);//获取周几
        String time = TimeUtils.getDateToString(startAt, "HH:mm");
        String teacherName = courseEntity.getTeacher();
        String courseName = courseEntity.getCourseName();
        tvContent.setText(String.format("%s\t\t%s\t\t%s\n%s\t\t%s",
                Month, week, time, teacherName, courseName));

        int dayOfWeek = TimeUtils.getDayOfWeek(Long.valueOf(courseEntity.getStartAt()));// 某一周中的周几
        if (position == 0 || dayOfWeek != TimeUtils.getDayOfWeek(Long.valueOf(
                courseData.get(position - 1).getStartAt()))) {
            rlDay.setVisibility(View.VISIBLE);
            tvDay.setText(String.format("%s\t%s\t\t%s", Month, Constant.weekArr[dayOfWeek - 1],
                    TextUtils.equals(today, TimeUtils.getDayOfWeek("yyyy-MM-dd", dayOfWeek,
                            courseEntity.getStartAt())) ? "今天" : ""));
        } else {
            rlDay.setVisibility(View.GONE);
        }
        tvLevaeNum.setText(String.format("预约学员:\t%s人\n开班人数:\t%s人\t\t容纳人数:%s人",
                ((List) courseEntity.getStudents()).size(), courseEntity.getMinStuCount(), courseEntity.getMaxStuCount()));
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
        return R.layout.item_stu_leave;
    }
}
