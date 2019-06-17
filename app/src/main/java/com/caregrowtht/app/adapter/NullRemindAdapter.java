package com.caregrowtht.app.adapter;

import android.content.Context;
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

/**
 * 预约课提醒适配器
 */
public class NullRemindAdapter extends XrecyclerAdapter {

    List<CourseEntity> listDatas = new ArrayList<>();

    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_stu_detail)
    TextView tvStuDetail;

    public NullRemindAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        CourseEntity entity = listDatas.get(position);
        long courseStartAt = Long.valueOf(entity.getCourseBeginAt());
        String Month = TimeUtils.getDateToString(courseStartAt, "MM月dd日");
        String data = DateUtil.getDate(courseStartAt, "yyyy年MM月dd日");
        String week = TimeUtils.getWeekByDateStr(data);//获取周几
        String time = TimeUtils.getDateToString(courseStartAt, "HH:mm");
        String teacherName = entity.getTeacherName();
        String courseName = entity.getCourseName();
        tvContent.setText(String.format("%s\t\t%s\t\t%s\n%s\t\t%s",
                Month, week, time, teacherName, courseName));
        tvStuDetail.setText(String.format("空位：%s\t人", entity.getKongwei()));
    }

    public void setData(List<CourseEntity> listDatas) {
        this.listDatas.clear();
        this.listDatas.addAll(listDatas);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listDatas.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_null_remid;
    }
}
