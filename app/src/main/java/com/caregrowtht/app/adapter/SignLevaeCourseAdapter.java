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
    private String showType, status;

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
                    typeContent = "??????";
                    break;
                case "2":
                    typeContent = "??????7???";
                    break;
                case "3":
                    typeContent = "7?????????";
                    break;
                case "4":
                    typeContent = "??????7???";
                    break;
                case "5":
                    typeContent = "7?????????";
                    break;
            }
            tvDay.setText(typeContent);
        } else {
            rlDay.setVisibility(View.GONE);
        }
        switch (showType) {
            case "9":// 9??????????????????????????????
                tvStuDetail.setVisibility(View.GONE);
                tvStuNum.setVisibility(View.GONE);
                ((BaseActivity) mContext).initRecyclerView(recyclerView, false);
                recyclerView.setAdapter(new LeaveStuAdapter((List) courseEntity.getStudents(), mContext));
                break;
            case "10":// 10???????????????????????????
                rlSign.setVisibility(View.GONE);
                tvStuNum.setVisibility(View.GONE);
                break;
            case "12":// 12??????????????????????????????????????????
                tvStuDetail.setVisibility(View.GONE);
                rlSign.setVisibility(View.GONE);
                break;
        }
        long courseStartAt = Long.valueOf(courseEntity.getCourseBeginAt());
        String Month = TimeUtils.getDateToString(courseStartAt, "MM???dd???");
        String data = DateUtil.getDate(courseStartAt, "yyyy???MM???dd???");
        String week = TimeUtils.getWeekByDateStr(data);//????????????
        String time = TimeUtils.getDateToString(courseStartAt, "HH:mm");
        String teacherName = courseEntity.getTeacherName();
        String courseName = courseEntity.getCourseName();
        tvContent.setText(String.format("%s\t\t%s\t\t%s\n%s\t\t%s",
                Month, week, time, teacherName, courseName));
        String childNum = courseEntity.getStuCount();
        String sign = courseEntity.getSignCount();
        String leav = courseEntity.getLeaveCount();
        String noTake = courseEntity.getWaitCount();
        tvStuDetail.setText(Html.fromHtml(String.format("??????\t%s???\t\t|\t\t??????\t%s???\t\t|\t\t??????\t%s???" +
                        "\t\t|\t\t?????????\t<font color='#F54949'>%s</font>???",
                childNum, sign, leav, noTake)));
        tvStuNum.setText(String.format("??????%s???", courseEntity.getStuCount()));
        tvLevaeNum.setText(String.format("%s?????????", courseEntity.getLeaveCount()));
    }

    public void setData(List<CourseEntity> courseData, String showType, String status) {
        this.courseData.clear();
        this.courseData.addAll(courseData);
        this.showType = showType;
        this.status = status;
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
