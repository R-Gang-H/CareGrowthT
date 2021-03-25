package com.caregrowtht.app.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.view.View;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * haoruigang on 2018-10-25 17:45:41
 * 课程分类颜色
 */
public class CourseColorAdapter extends XrecyclerAdapter {

    ArrayList<CourseEntity> couTypeArrayList = new ArrayList<>();//课程类型集合
    private int selectItem = -1;
    private CourseEntity courseEntity;

    @BindView(R.id.iv_xerox)
    CardView ivXerox;
    @BindView(R.id.iv_course_icon)
    CardView ivCourseIcon;

    public CourseColorAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
        couTypeArrayList.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        CourseEntity couTypeList = couTypeArrayList.get(position);
        if (selectItem == position) {
            ivXerox.setVisibility(View.VISIBLE);
        } else {
            ivXerox.setVisibility(View.GONE);
        }
        if (courseEntity != null && TextUtils.equals(courseEntity.getColor(), couTypeList.getColor())) {
            ivXerox.setVisibility(View.VISIBLE);
        }
        ivCourseIcon.setCardBackgroundColor(Color.parseColor(couTypeList.getColor()));
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
        this.courseEntity = null;//设为空,点击后不选择默认项
        notifyDataSetChanged();
    }

    public void setData(ArrayList<CourseEntity> couTypeArrayList, CourseEntity courseEntity) {
        this.couTypeArrayList.clear();
        this.couTypeArrayList.addAll(couTypeArrayList);
        this.courseEntity = courseEntity;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return couTypeArrayList.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_cou_type_color;
    }
}
