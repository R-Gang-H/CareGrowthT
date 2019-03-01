package com.caregrowtht.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.UserEntity;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemLongClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * Created by haoruigang on 2018-8-23 15:06:53.
 * 选择教师/助教
 */

public class TeacherAdapter extends XrecyclerAdapter {

    @BindView(R.id.iv_avatar)
    AvatarImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;

    private Context activity;
    private ArrayList<UserEntity> listModel = new ArrayList<>();//选择的教师信息

    // 用来控制CheckBox的选中状况
    private HashMap<Integer, Boolean> isSelected = new HashMap<>();
    //选中教师的信息
    private HashMap<Integer, UserEntity> mContactModels = new HashMap<>();
    public Boolean isAll = false;
    private String teacherType;//教师类型 1：主讲教师 2：助教

    public TeacherAdapter(List datas, Context context, ViewOnItemClick onItemClick1, ViewOnItemLongClick onItemLongClick) {
        super(datas, context, onItemClick1, onItemLongClick);
        this.activity = context;
        listModel.addAll(datas);
    }

    // 初始化isSelected的数据
    public void initDate() {
        if (listModel.size() > 0) {
            for (int i = 0; i < listModel.size(); i++) {
                getIsSelected().put(i, isAll);
                getIsUserEntity().put(i, isAll ? listModel.get(i) : null);
            }
        }
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        UserEntity item = listModel.get(position);
        ivAvatar.setTextAndColor(TextUtils.isEmpty(item.getUserName()) ? "" :
                        item.getUserName().substring(0, 1),
                mContext.getResources().getColor(R.color.b0b2b6));
        GlideUtils.setGlideImg(activity, item.getUserImage(), 0, ivAvatar);
        tvName.setText(item.getUserName());

        final CheckBox tvSelectStu = (CheckBox) holder.getView(R.id.tv_select_stu);
        // 根据isSelected来设置checkbox的选中状况
        tvSelectStu.setSelected(getIsSelected().get(position));
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_teacher_select;
    }

    @Override
    public int getItemCount() {
        return listModel.size();
    }

    public void setData(List<UserEntity> argList, String teacherType, List<UserEntity> mainTeach, List<UserEntity> assisTeach) {
        this.teacherType = teacherType;
        listModel.clear();
        listModel.addAll(argList);
        // 初始化数据
        initDate();
        if (mainTeach != null && mainTeach.size() > 0) {
            for (UserEntity teach : mainTeach) {
                for (int i = 0; i < listModel.size(); i++) {
                    if (TextUtils.equals(teach.getUserId(), listModel.get(i).getUserId())) {//过滤已选中的主讲教师
                        if (TextUtils.equals(teacherType, "1")) {// 主讲教师
                            getIsSelected().put(i, true);// 同时修改map的值保存状态
                            getIsUserEntity().put(i, listModel.get(i));
                        } else if (TextUtils.equals(teacherType, "2")) {// 助教
                            //助教，已经是主讲教师不显示在助教列表
                            listModel.remove(i);
                        }
                    }
                }
            }
        }
        if (assisTeach != null && assisTeach.size() > 0) {
            for (UserEntity teach : assisTeach) {
                for (int i = 0; i < listModel.size(); i++) {
                    if (TextUtils.equals(teach.getUserId(), listModel.get(i).getUserId())) {//过滤已选中的助教
                        if (TextUtils.equals(teacherType, "2")) {// 助教
                            getIsSelected().put(i, true);// 同时修改map的值保存状态
                            getIsUserEntity().put(i, listModel.get(i));
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void getSelect(final int position, final CheckBox tvSelectStu) {
        if (listModel.size() > 0) {
            if (TextUtils.equals(teacherType, "1")) {
                for (int i = 0; i < listModel.size(); i++) {
                    if (i == position) {
                        getIsSelected().put(i, true);// 同时修改map的值保存状态
                        getIsUserEntity().put(i, listModel.get(i));
                    } else {
                        getIsSelected().put(i, false);// 同时修改map的值保存状态
                        getIsUserEntity().put(i, null);
                    }
                }
            } else {
                tvSelectStu.setSelected(!tvSelectStu.isSelected());
                getIsSelected().put(position, tvSelectStu.isSelected());// 同时修改map的值保存状态
                getIsUserEntity().put(position, tvSelectStu.isSelected() ? listModel.get(position) : null);
            }
            notifyDataSetChanged();
        }
    }

    public HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public HashMap<Integer, UserEntity> getIsUserEntity() {
        return mContactModels;
    }
}
