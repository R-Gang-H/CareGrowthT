package com.caregrowtht.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.AddTeacherActivity;
import com.caregrowtht.app.activity.TeacherMsgActivity;
import com.caregrowtht.app.activity.TeacherPermisActivity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * haoruigang on 2018-10-22 12:21:35
 * 教师管理适配器
 */
public class OfficialAdapter extends XrecyclerAdapter {

    @BindView(R.id.iv_head_icon)
    AvatarImageView ivHeadIcon;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.iv_mobile)
    ImageView ivMobile;
    @BindView(R.id.iv_wechat)
    ImageView ivWechat;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.tv_edit_teacher)
    TextView tvEditTeacher;

    private ArrayList<StudentEntity> auditData = new ArrayList<>();

    public OfficialAdapter(List datas, Context context) {
        super(datas, context);
        this.auditData.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        final StudentEntity auditEntity = auditData.get(position);
        ivHeadIcon.setTextAndColor(TextUtils.isEmpty(auditEntity.getUserName()) ? "" : auditEntity.getUserName().substring(0, 1),
                mContext.getResources().getColor(R.color.b0b2b6));
        GlideUtils.setGlideImg(mContext, auditEntity.getUserImage(), 0, ivHeadIcon);
        tvName.setText(auditEntity.getUserName());
        if (!TextUtils.isEmpty(auditEntity.getAppLogin()) && auditEntity.getAppLogin().equals("1")) {//1:激活 2:没激活
            ivMobile.setImageResource(R.mipmap.ic_formal_mobile_true);
        } else {
            ivMobile.setImageResource(R.mipmap.ic_formal_mobile_false);
        }
        if (!TextUtils.isEmpty(auditEntity.getWechat()) && auditEntity.getWechat().equals("1")) {//激活 1：已激活 2：未激活
            ivWechat.setImageResource(R.mipmap.ic_formal_wechat_true);
        } else {
            ivWechat.setImageResource(R.mipmap.ic_formal_wechat_false);
        }
        tvPhone.setText(auditEntity.getMobile());
        tvEditTeacher.setText(auditEntity.getPower());
        tvEditTeacher.setOnClickListener(v -> {
            if (auditEntity.getAppLogin().equals("1") || auditEntity.getWechat().equals("1")) {
                if (!UserManager.getInstance().isTrueRole("js_1")) {
                    U.showToast(mContext.getString(R.string.text_role));
                } else {
                    mContext.startActivity(new Intent(mContext, TeacherPermisActivity.class)
                            .putExtra("auditEntity", auditEntity));
                }
            } else {
                mContext.startActivity(new Intent(mContext, AddTeacherActivity.class)
                        .putExtra("auditEntity", auditEntity));
            }
            ((TeacherMsgActivity) mContext).overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
        });
    }

    public void setData(ArrayList<StudentEntity> auditData) {
        this.auditData.clear();
        this.auditData.addAll(auditData);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return auditData.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_teacher;
    }
}
