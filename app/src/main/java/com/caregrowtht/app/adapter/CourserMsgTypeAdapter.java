package com.caregrowtht.app.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
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
 * 课程管理分类 适配器
 * haoruigang on 2018年10月24日18:52:46
 */
public class CourserMsgTypeAdapter extends XrecyclerAdapter {

    List<CourseEntity> courseTypeList = new ArrayList<>();
    private int selectItem = 0;// 当前选中那项
    private int selItem = -1;// 当前悬浮在那项之上

    @BindView(R.id.iv_course_icon)
    CardView ivCourseIcon;
    @BindView(R.id.tv_course_type)
    TextView tvCourseType;
    @BindView(R.id.tv_course_num)
    TextView tvCourseNum;
    @BindView(R.id.rl_course)
    RelativeLayout rlCourse;
    @BindView(R.id.rl_course_type)
    RelativeLayout rlCourseType;
    @BindView(R.id.rl_add_course)
    RelativeLayout rlAddCourse;

    public CourserMsgTypeAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
        this.courseTypeList.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        CourseEntity courseEntity = courseTypeList.get(position);
        ivCourseIcon.setCardBackgroundColor(TextUtils.isEmpty(courseEntity.getColor())
                || courseEntity.getColor().equals("null") ?
                ResourcesUtils.getColor(R.color.blue) :
                Color.parseColor(courseEntity.getColor()));
        tvCourseType.setText(courseEntity.getClassifyName());
        tvCourseNum.setText(String.format("排课 %s个", courseEntity.getCourseCount()));

        if (selectItem == position) {
            rlCourseType.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        } else {
            rlCourseType.setBackgroundColor(mContext.getResources().getColor(R.color.col_f0));
        }
        if (selItem == position) {
            rlCourse.setBackgroundResource(R.drawable.shape_round_corner_notify_obj);
        } else {
            rlCourse.setBackground(null);
        }
        if (courseTypeList.size() - 1 == position) {
            rlCourseType.setVisibility(View.GONE);
            rlAddCourse.setVisibility(View.VISIBLE);
        } else {
            rlCourseType.setVisibility(View.VISIBLE);
            rlAddCourse.setVisibility(View.GONE);
        }
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
        notifyDataSetChanged();
    }

    public void setSelItem(int selItem) {
        this.selItem = selItem;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return courseTypeList.size();
    }

    public void setData(List<CourseEntity> courseTypeList) {
        this.courseTypeList.clear();
        this.courseTypeList.addAll(courseTypeList);
        notifyDataSetChanged();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_course_msg_type;
    }
}
