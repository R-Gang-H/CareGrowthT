package com.caregrowtht.app.view.draglistview;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemLongClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.List;

import butterknife.BindView;

public class ItemAdapter extends XrecyclerAdapter {

    @BindView(R.id.text)
    TextView mText;
    @BindView(R.id.tv_end)
    TextView tvEnd;

    public ItemAdapter(List datas, Context context, ViewOnItemClick onItemClick1, ViewOnItemLongClick onItemLongClick) {
        super(datas, context, onItemClick1, onItemLongClick);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        CourseEntity courseEntity = (CourseEntity) datas.get(position);
        mText.setText(courseEntity.getCourseName());
        tvEnd.setVisibility(TextUtils.equals(courseEntity.getStatus(), "1")
                ? View.INVISIBLE : View.VISIBLE);//课程状态 1：正在进行(没有结束) 2：已结束
        holder.itemView.setTag(courseEntity);
    }

    public void setData(List<CourseEntity> courseList) {
        this.datas.clear();
        this.datas.addAll(courseList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.column_item;
    }

}
