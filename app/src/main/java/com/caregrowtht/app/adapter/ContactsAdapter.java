package com.caregrowtht.app.adapter;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.UserEntity;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * Contact联系人适配器
 *
 * @author haoruigang
 * @date 2018-7-30 18:45:02
 */
public class ContactsAdapter extends XrecyclerAdapter {

    private static final String TAG = "ContactsAdapter";

    @BindView(R.id.tv_index)
    TextView tvIndex;
    @BindView(R.id.iv_avatar)
    AvatarImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_select_stu)
    CheckBox tvSelect;

    // 用来控制CheckBox的选中状况
    private HashMap<Integer, Boolean> isSeleThe = new HashMap<>();
    private HashMap<Integer, Boolean> isSelStu = new HashMap<>();

    public Boolean isTheAll = false;//教师全选
    public Boolean isStuAll = false;//学员全选

    private int isType;
    private String notifyType = "1";

    private List<UserEntity> mContactModels = new ArrayList<>();
    private List<UserEntity> editList = new ArrayList<>();//编辑已选择的人

    public ContactsAdapter(List datas, Context context, ViewOnItemClick itemClick) {
        super(datas, context, itemClick);
        this.mContactModels.addAll(datas);
    }

    // 初始化isSelected的数据
    public void initDate(int isType) {//身份 1：老师 2：学员
        if (mContactModels.size() > 0) {
            for (int i = 0; i < mContactModels.size(); i++) {
                if (isType == 1) {
                    getIsSeleThe().put(i, isTheAll);
                }
                if (isType == 2) {
                    getIsSelStu().put(i, isStuAll);
                }
            }
        }
    }

    public void update(List<UserEntity> mContactModels, int isType, List<UserEntity> editList,
                       int isinitDate, String notifyType) {
        this.mContactModels.clear();
        this.mContactModels.addAll(mContactModels);
        if (isinitDate == 1) {//首次加载执行
            // 初始化数据
            initDate(isType);//默认教师
        }
        this.isType = isType;
        this.editList.clear();
        if (editList != null && editList.size() > 0) {
            this.editList.addAll(editList);
        }
        this.notifyType = notifyType;
        notifyDataSetChanged();
    }

    @Override
    public void convert(XrecyclerViewHolder holder, final int position, Context context) {
        UserEntity contact = mContactModels.get(position);
        Log.e(TAG, "convert: index:" + contact.getIndex());
        if (position == 0 || !mContactModels.get(position - 1).getIndex().equals(contact.getIndex())) {
            tvIndex.setVisibility(View.VISIBLE);
            tvIndex.setText(contact.getIndex());
        } else {
            tvIndex.setVisibility(View.GONE);
        }
        String userName, userImage, userId;
        if (notifyType.equals("2")) {//2：标星学员通知
            userId = contact.getStuId();
            userName = contact.getStuName();
            userImage = contact.getStuIcon();
        } else {
            userId = contact.getUserId();
            userName = contact.getUserName();
            userImage = contact.getUserImage();
        }
        tvName.setText(userName);
        ivAvatar.setTextAndColor(TextUtils.isEmpty(userName) ? "" : userName.substring(0, 1),
                mContext.getResources().getColor(R.color.b0b2b6));
        GlideUtils.setGlideImg(context, userImage, 0, ivAvatar);

        // 编辑已选的初始化数据
        if (editList.size() > 0) {
            for (int i = 0; i < editList.size(); i++) {
                String stuId;
                if (notifyType.equals("2")) {//2：标星学员通知
                    stuId = editList.get(i).getStuId();
                } else {
                    stuId = editList.get(i).getUserId();
                }
                if (TextUtils.equals(stuId, userId)) {//已选
                    if (isType == 1) {
                        getIsSeleThe().put(position, true);
                    }
                    if (isType == 2) {
                        getIsSelStu().put(position, true);
                    }
                    break;
                }
            }
        }
        // 根据isSelected来设置checkbox的选中状况
        if (isType == 1) {
            tvSelect.setSelected(getIsSeleThe().get(position));
        }
        if (isType == 2) {
            tvSelect.setSelected(getIsSelStu().get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mContactModels.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_contacts;
    }

    public void getSelect(int position, final CheckBox tvSelect) {
        if (isType == 1) {
            tvSelect.setSelected(!getIsSeleThe().get(position));
            getIsSeleThe().put(position, tvSelect.isSelected());// 同时修改map的值保存状态
        }
        if (isType == 2) {
            tvSelect.setSelected(!getIsSelStu().get(position));
            getIsSelStu().put(position, tvSelect.isSelected());// 同时修改map的值保存状态
        }
    }

    public HashMap<Integer, Boolean> getIsSeleThe() {
        return isSeleThe;
    }

    public HashMap<Integer, Boolean> getIsSelStu() {
        return isSelStu;
    }

    public interface OnContactsListenar {
        void showCheckImage(final UserEntity userEntity, final int position);

        void deleteImage(final UserEntity userEntity, final int position);
    }

    OnContactsListenar onContactsListenar;

    public void setOnContactsListenar(OnContactsListenar onContactsListenar) {
        this.onContactsListenar = onContactsListenar;
    }

}
