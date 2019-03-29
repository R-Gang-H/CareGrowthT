package com.caregrowtht.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.view.MyGridView;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by haoruigang on 2018/4/26 18:02.
 * 学员情况
 */

public class StudentSituatAdapter extends XrecyclerAdapter {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.gv_tch)
    MyGridView gvTch;
    @BindView(R.id.rl_exist)
    RelativeLayout rlExist;
    @BindView(R.id.v_line)
    View vLine;

    private Activity activity;
    private CourseEntity courseEntity;

    public boolean isExt = true;

    List<StudentEntity> stuStatus = new ArrayList<>();
    StudentCardAdapter stuCardAdapter = null;
    ArrayList<StudentEntity> studentEntities = new ArrayList<>();//全部学员

    public StudentSituatAdapter(List datas, Context context, CourseEntity courseEntity
            , OnStuCardClick onStuCardClick) {
        super(datas, context);
        this.activity = (Activity) context;
        studentEntities.addAll(datas);
        this.courseEntity = courseEntity;
        this.onStuCardClick = onStuCardClick;
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        final CheckBox tvSelectCourse = (CheckBox) holder.getView(R.id.tv_select_course);
        StudentEntity studentEntity = stuStatus.get(position);
        String status = studentEntity.getStatus();
        if (onStuCardClick == null) {
            if (TextUtils.equals(status, "1")) {
                tvTitle.setText("应到学员");
                tvTitle.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_blue_tag));
            } else if (TextUtils.equals(status, "2")) {
                tvTitle.setText("签到学员");
                tvTitle.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_submit_ssion));
            } else if (TextUtils.equals(status, "3")) {
                tvTitle.setText("请假学员");
                tvTitle.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_leave_stu));
            }
            tvTitle.setVisibility(View.VISIBLE);
            tvSelectCourse.setVisibility(View.GONE);
            vLine.setVisibility(View.GONE);
        } else {
            if (TextUtils.equals(status, "1")) {
                tvSelectCourse.setText("待处理");
            } else if (TextUtils.equals(status, "2")) {
                tvSelectCourse.setText("已签到");
            } else if (TextUtils.equals(status, "3")) {
                tvSelectCourse.setText("请假学员");
            }
            tvTitle.setVisibility(View.GONE);
            tvSelectCourse.setVisibility(View.VISIBLE);
            vLine.setVisibility(View.VISIBLE);
        }
        stuCardAdapter = new StudentCardAdapter(activity, R.layout.item_stu_situat, getData(status), courseEntity);
        gvTch.setAdapter(stuCardAdapter);

        if (onStuCardClick != null) {
            if (isExt && TextUtils.equals(status, "2")) {// 默认选中已签到
                stuCardAdapter.isAll = true;
                stuCardAdapter.setData(tvSelectCourse);
                stuCardAdapter.checkAll();
            }
            onStuCardClick.stuCard(stuCardAdapter, tvSelectCourse);//监听mAdapter
        }

    }

    //筛选当前分类下的课程名称
    private ArrayList<StudentEntity> getData(String status) {
        ArrayList<StudentEntity> datas = new ArrayList<>();
        for (StudentEntity feedbackEntity : studentEntities) {
            if (feedbackEntity.getStatus().equals(status)) {//状态 1未签到 2已签到 3请假
                datas.add(feedbackEntity);
            }
        }
        return datas;
    }

    public void setData(List<StudentEntity> stuStatus, ArrayList<StudentEntity> mArrDatas) {
        this.stuStatus.clear();
        this.stuStatus.addAll(stuStatus);
        this.studentEntities.clear();
        this.studentEntities.addAll(mArrDatas);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return stuStatus.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_student_card;
    }


    private OnStuCardClick onStuCardClick;

    public interface OnStuCardClick {
        void stuCard(final StudentCardAdapter stuCardAdapter, final CheckBox tvSelectCourse);
    }
}
