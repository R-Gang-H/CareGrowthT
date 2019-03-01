package com.caregrowtht.app.adapter;

import android.content.Context;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by haoruigang on 2018/4/24 18:58.
 * 课程记录适配器
 */

public class CourseRecordAdapter extends XrecyclerAdapter {

    @BindView(R.id.tv_course_name)
    TextView tvCourseName;
    @BindView(R.id.tv_attendance)
    TextView tvAttendance;
    @BindView(R.id.tv_course_count)
    TextView tvCourseCount;
    @BindView(R.id.tv_course_time)
    TextView tvCourseTime;
    private ArrayList<CourseEntity> listData = new ArrayList<>();
    private Context mContext;

    public CourseRecordAdapter(List datas, Context context) {
        super(datas, context);
        listData.addAll(datas);
        this.mContext = context;
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        tvCourseName.setText(listData.get(position).getCourseName());
        tvAttendance.setText("出勤人数:\t" + (StrUtils.isEmpty(listData.get(position).getAttendance()) ? "0" : listData.get(position).getAttendance()));
        tvCourseCount.setText(String.format("消课总数:\t%s节", (String) (StrUtils.isEmpty(listData.get(position).getCourseCount()) ? "0" : listData.get(position).getCourseCount())));
        tvCourseTime.setText(DateUtil.getDate(Long.valueOf(listData.get(position).getStartDate()), "yyyy-MM-dd HH:mm") + "-" + DateUtil.getDate(Long.valueOf(listData.get(position).getEndDate()), "HH:mm"));
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_course_record;
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void setData(ArrayList<CourseEntity> courseEntities, Boolean isClear) {
        if (isClear) {
            listData.clear();
        }
        listData.addAll(courseEntities);
        notifyDataSetChanged();
    }
}
