package com.caregrowtht.app.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemLongClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 机构所学课程Adapter适配器
 * Created by haoruigang on 2018/4/4 15:11.
 */

public class StudyCourseAdapter extends XrecyclerAdapter {

    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_course_name)
    TextView tvCourseName;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.tv_course_tag)
    TextView tvCourseTag;
    private Context mContext;
    private ArrayList<CourseEntity> listDatas = new ArrayList<>();

    public StudyCourseAdapter(List datas, Context context, ViewOnItemClick onItemClick1, ViewOnItemLongClick onItemLongClick) {
        super(datas, context, onItemClick1, onItemLongClick);
        this.mContext = context;
        this.listDatas.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        Glide.with(context).load(listDatas.get(position).getCourseImage()).into(ivLogo);
        tvCourseName.setText(listDatas.get(position).getCourseName());
        tvAge.setText(listDatas.get(position).getAge());
        tvCourseTag.setText(listDatas.get(position).getCourseTag());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_study_course;
    }

    @Override
    public int getItemCount() {
        return listDatas.size();
    }

    public void setData(ArrayList<CourseEntity> argList) {
        this.listDatas.clear();
        this.listDatas.addAll(argList);
        notifyDataSetChanged();
    }
}
