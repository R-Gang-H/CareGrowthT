package com.caregrowtht.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * haruigang on 2018-8-24 15:16:05
 * 课程分类适配器
 */
public class CourserTypeAdapter extends XrecyclerAdapter {

    private List<CourseEntity> courseDatas = new ArrayList<>();

    @BindView(R.id.iv_course_icon)
    CardView ivCourseIcon;
    @BindView(R.id.tv_course_type)
    TextView tvCourseType;


    // 用来控制CheckBox的选中状况
    private HashMap<Integer, Boolean> isSelected = new HashMap<>();
    //选中课程的信息
    private HashMap<Integer, CourseEntity> mCourseModels = new HashMap<>();
    //选中课程的消课次数
    private HashMap<Integer, CourseEntity> mCount = new HashMap<>();
    // 是否全选
    public boolean isCheckAll = false;
    public boolean isClick = false;// 是否被点击

    public String isCount = "";
    public Boolean isAll = false;
    private String searchText;
    private String cardType;
    private String addType;// 4：新建课时卡 5:编辑课时卡管理

    //编辑上页数据
    private List<CourseEntity> mCourses = new ArrayList<>();
    private List<CourseEntity> countList = new ArrayList<>();
    public int index = 1;

    public CourserTypeAdapter(List datas, Context context, ViewOnItemClick onItemClick,
                              List<CourseEntity> mCourses, List<CourseEntity> countList, String addType) {
        super(datas, context, onItemClick);
        //上页已选中的数据
        this.mCourses.clear();
        this.mCourses.addAll(mCourses);
        this.countList.clear();
        this.countList.addAll(countList);
        this.addType = addType;
    }

    // 初始化isSelected的数据
    public void initDate(boolean isFor) {
        if (courseDatas.size() > 0) {
            for (int i = 0; i < courseDatas.size(); i++) {
                CourseEntity courseEntity = new CourseEntity();
                if (!isCheckAll) {// 非全选才执行
                    isAll = false;
                    isCount = "";
                }
                if (isFor) {
                    //循环获取上页已选中的
                    for (int j = 0; j < mCourses.size(); j++) {
                        if (TextUtils.equals(courseDatas.get(i).getCourseId(),
                                mCourses.get(j).getCourseId())) {
                            isAll = true;
                            courseEntity = mCourses.get(j);
                            isCount = countList.get(j).getCourseCount();
                            break;
                        }
                    }
                }
                getIsSelected().put(i, isAll);
                courseEntity.setCourseId(courseEntity.getCourseId());
                courseEntity.setCourseCount(isCount);
                getCount().put(i, courseEntity);
                getIsCourseEntity().put(i, isAll ? courseDatas.get(i) : null);
            }
        }
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        CourseEntity courseEntity = courseDatas.get(position);

        final CheckBox tvSelectCourse = (CheckBox) holder.getView(R.id.tv_select_course);
        final TextView tvCouont = (TextView) holder.getView(R.id.tv_couont);
        final EditText etCancelCount = (EditText) holder.getView(R.id.et_cancel_count);
        final RelativeLayout rlSelect = (RelativeLayout) holder.getView(R.id.rl_select);
        if (TextUtils.isEmpty(searchText) && (position == 0 || !courseDatas.get(position - 1).getClassifyId().equals(courseEntity.getClassifyId()))) {
            ivCourseIcon.setVisibility(View.VISIBLE);
            tvCourseType.setVisibility(View.VISIBLE);
            ivCourseIcon.setCardBackgroundColor(TextUtils.isEmpty(courseEntity.getColor()) ?
                    ResourcesUtils.getColor(R.color.blue) : Color.parseColor(courseEntity.getColor()));
            tvCourseType.setText(courseEntity.getClassifyName());
            tvSelectCourse.setText("全选");
            if (!isClick) {
                getIsSelected().put(position, false);// 全选默认不选中
            }
        } else {
            ivCourseIcon.setVisibility(View.GONE);
            tvCourseType.setVisibility(View.GONE);
            tvSelectCourse.setText(courseEntity.getCourseName());
        }

        if (TextUtils.equals(cardType, "1")) {// 次数
            tvCouont.setText("次");
        } else {
            tvCouont.setText("元");
        }

        if (etCancelCount.getTag() instanceof TextWatcher) {
            etCancelCount.removeTextChangedListener((TextWatcher) etCancelCount.getTag());
        }
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isCount = s.toString();
                if (tvSelectCourse.getText().equals("全选")) {
                    getData(courseEntity.getClassifyId(), index);
                } else {
                    courseEntity.setCourseCount(isCount);
                    getCount().put(position, courseEntity);
                }
            }
        };
        // 根据isSelected来设置checkbox的选中状况
        tvSelectCourse.setSelected(getIsSelected().get(position));
        rlSelect.setVisibility(getIsSelected().get(position) ? View.VISIBLE : View.GONE);
        if (getIsSelected().get(position)) {
            if (TextUtils.equals(cardType, "3")) {//年卡隐藏
                rlSelect.setVisibility(View.GONE);
            } else {
                rlSelect.setVisibility(View.VISIBLE);
                etCancelCount.setText(getCount().get(position).getCourseCount());
            }
        }
        etCancelCount.addTextChangedListener(watcher);
        etCancelCount.setTag(watcher);
    }

    public void setData(List<CourseEntity> courseDatas, String searchText,
                        String cardType) {
        this.courseDatas.clear();
        this.courseDatas.addAll(courseDatas);
        this.searchText = searchText;
        this.cardType = cardType;
        // 初始化数据
        initDate(true);//初始化才遍历匹配上次已选的
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return courseDatas.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_course_type;
    }

    public void getSelect(final int position, final CheckBox tvSelectCourse,
                          final RelativeLayout rlSelect, final EditText etCancelCount) {
        if (courseDatas.size() > 0) {
            tvSelectCourse.setSelected(!tvSelectCourse.isSelected());
            if (TextUtils.equals(cardType, "3")) {//年卡隐藏
                rlSelect.setVisibility(View.GONE);
            } else {
                rlSelect.setVisibility(tvSelectCourse.isSelected() ? View.VISIBLE : View.GONE);
            }
            getIsSelected().put(position, tvSelectCourse.isSelected());// 同时修改map的值保存状态
            CourseEntity courseEntity = new CourseEntity();
            courseEntity.setCourseId(courseDatas.get(position).getCourseId());
            courseEntity.setCourseCount(etCancelCount.getText().toString().trim());
            getCount().put(position, tvSelectCourse.isSelected() ? courseEntity : null);
            getIsCourseEntity().put(position, tvSelectCourse.isSelected() ? courseDatas.get(position) : null);
        }
    }

    //筛选当前分类下的课程名称
    public void getData(String classifyId, int position) {
        if (courseDatas.size() > 0) {
            for (int i = position; i < courseDatas.size(); i++) {// +1 全选的下个坐标开始
                if (TextUtils.equals(classifyId, courseDatas.get(i).getClassifyId())) {
                    getIsSelected().put(i, isAll);
                    CourseEntity courseEntity = new CourseEntity();
                    courseEntity.setCourseId(courseDatas.get(i).getCourseId());
                    courseEntity.setCourseCount(isCount);
                    getCount().put(i, isAll ? courseEntity : null);
                    getIsCourseEntity().put(i, isAll ? courseDatas.get(i) : null);
                } else {
                    break;
                }
            }
            notifyDataSetChanged();
        }
    }

    public HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public HashMap<Integer, CourseEntity> getIsCourseEntity() {
        return mCourseModels;
    }

    public HashMap<Integer, CourseEntity> getCount() {
        return mCount;
    }

}
