package com.caregrowtht.app.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.android.library.utils.U;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.AddTeacherActivity;
import com.caregrowtht.app.activity.BaseActivity;
import com.caregrowtht.app.activity.TeacherMsgActivity;
import com.caregrowtht.app.activity.TeacherPermisActivity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.permissions.PermissionCallBackM;
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

    BaseActivity mContext;

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
    @BindView(R.id.btn_call)
    ImageView mCallBtn;

    private ArrayList<StudentEntity> auditData = new ArrayList<>();

    public OfficialAdapter(List datas, BaseActivity context) {
        super(datas, context);
        mContext = context;
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
            String powerId = auditEntity.getPowerId();
            if (!TextUtils.isEmpty(powerId) && powerId.equals("99999")) {// 超级管理员
                U.showToast("超级管理员，不允许修改权限。");
            } else {
                if (auditEntity.getAppLogin().equals("1") || auditEntity.getWechat().equals("1")) {
                    if (!UserManager.getInstance().isTrueRole("js_1")) {
                        UserManager.getInstance().showSuccessDialog((Activity) mContext
                                , mContext.getString(R.string.text_role));
                    } else {
                        mContext.startActivity(new Intent(mContext, TeacherPermisActivity.class)
                                .putExtra("auditEntity", auditEntity));
                    }
                } else {
                    mContext.startActivity(new Intent(mContext, AddTeacherActivity.class)
                            .putExtra("auditEntity", auditEntity));
                }
                mContext.overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
            }
        });
        mCallBtn.setOnClickListener(view -> {
            //获取CALL_PHONE权限
            mContext.requestPermission(
                    Constant.RC_CALL_PHONE,
                    new String[]{Manifest.permission.CALL_PHONE},
                    mContext.getString(R.string.rationale_call_phone),
                    new PermissionCallBackM() {
                        @Override
                        public void onPermissionGrantedM(int requestCode, String... perms) {
                            LogUtils.e(mContext, "TODO: CALL_PHONE Granted", Toast.LENGTH_SHORT);
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + auditEntity.getMobile()));
                            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            mContext.startActivity(intent);
                        }

                        @Override
                        public void onPermissionDeniedM(int requestCode, String... perms) {
                            LogUtils.e(mContext, "TODO: CALL_PHONE Denied", Toast.LENGTH_SHORT);
                        }
                    });
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
