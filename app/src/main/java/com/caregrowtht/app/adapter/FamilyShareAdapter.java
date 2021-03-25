package com.caregrowtht.app.adapter;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * haruigang on 2018-8-24 15:16:05
 * 与家人共用适配器
 */
public class FamilyShareAdapter extends XrecyclerAdapter {

    private List<CourseEntity> courseIds = new ArrayList<>();//学员分类
    private List<CourseEntity> courseDatas = new ArrayList<>();

    @BindView(R.id.iv_child_avatar)
    AvatarImageView ivCourseIcon;
    @BindView(R.id.tv_child_name)
    TextView tvChildName;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public Boolean isAll = false;
    public Boolean isExeCute = false;

    public FamilyShareAdapter(List datas, Context context, OnCourserWorkClick onCourserWorkClick) {
        super(datas, context);
        this.onCourserWorkClick = onCourserWorkClick;
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        CourseEntity courseEntity = courseIds.get(position);
        if (!TextUtils.isEmpty(courseEntity.getUserName())) {
            ivCourseIcon.setTextAndColor(courseEntity.getUserName().substring(0, 1),
                    mContext.getResources().getColor(R.color.b0b2b6));
        }
        GlideUtils.setGlideImg(mContext, courseEntity.getUserImage(), 0, ivCourseIcon);
        tvChildName.setText(courseEntity.getUserName());
        initRecyclerView(recyclerView, true);
        FamilyCardAdapter mAdapter = new FamilyCardAdapter(getData(courseEntity.getUserId()), mContext);
        recyclerView.setAdapter(mAdapter);

        if (onCourserWorkClick != null) {
            onCourserWorkClick.courseWork(mAdapter);//监听mAdapter
        }

        final CheckBox tvSelectCourse = (CheckBox) holder.getView(R.id.tv_select_course);

        if (isExeCute) {//全局全选
            // 根据isSelected来设置checkbox的选中状况
            tvSelectCourse.setSelected(isAll);
            mAdapter.isAll = isAll;
            mAdapter.initDate();
            mAdapter.notifyDataSetChanged();
        }

        tvSelectCourse.setOnClickListener(v -> {
            isExeCute = false;
            tvSelectCourse.setSelected(!tvSelectCourse.isSelected());
            mAdapter.isAll = tvSelectCourse.isSelected();
            mAdapter.initDate();
            mAdapter.notifyDataSetChanged();
        });
    }

    //筛选当前学员的课程卡
    private List<CourseEntity> getData(String userId) {
        List<CourseEntity> datas = new ArrayList<>();
        for (int i = 0; i < courseDatas.size(); i++) {
            if (TextUtils.equals(userId, courseDatas.get(i).getUserId())) {
                datas.add(courseDatas.get(i));
            }
        }
        return datas;
    }

    public void setData(List<CourseEntity> courseIds, List<CourseEntity> courseDatas) {
        this.courseIds.clear();
        this.courseDatas.clear();
        this.courseIds.addAll(courseIds);
        this.courseDatas.addAll(courseDatas);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return courseIds.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_share_card;
    }

    public void initRecyclerView(RecyclerView recyclerView, boolean isVertical) {
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(isVertical ? RecyclerView.VERTICAL : LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
    }


    private OnCourserWorkClick onCourserWorkClick;

    public interface OnCourserWorkClick {
        void courseWork(FamilyCardAdapter courserNameAdapter);
    }
}
