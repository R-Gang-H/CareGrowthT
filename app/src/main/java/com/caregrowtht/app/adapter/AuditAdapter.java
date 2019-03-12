package com.caregrowtht.app.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.AuditActivity;
import com.caregrowtht.app.activity.FamilyShareActivity;
import com.caregrowtht.app.activity.NewCardBuyActivity;
import com.caregrowtht.app.activity.TeacherPermisActivity;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * haoruigang on 2018-10-17 18:02:17
 * 审核学员适配器
 */
public class AuditAdapter extends XrecyclerAdapter {

    @BindView(R.id.iv_head_icon)
    AvatarImageView ivHeadIcon;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_org_name)
    TextView tvOrgName;
    @BindView(R.id.iv_gender)
    ImageView ivGender;
    @BindView(R.id.tv_contact_details)
    TextView tvContactDetails;
    @BindView(R.id.btn_no_stu)
    Button btnNoStu;
    @BindView(R.id.btn_my_stu)
    Button btnMyStu;

    private String type;
    private ArrayList<StudentEntity> auditData = new ArrayList<>();
    private String hintYes;
    private String hintNo;
    private int strNum = 0;

    private ArrayList<StudentEntity> identityEntity = new ArrayList<>();

    public AuditAdapter(List datas, Context context, String type) {
        super(datas, context);
        this.auditData.addAll(datas);
        this.type = type;
        getIdentity();
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        final StudentEntity auditEntity = auditData.get(position);
        if (TextUtils.equals(type, "1")) {//type 1: 添加学员 2: 添加教师
            ivHeadIcon.setTextAndColor(TextUtils.isEmpty(auditEntity.getStuName()) ? "" : auditEntity.getStuName().substring(0, 1),
                    mContext.getResources().getColor(R.color.b0b2b6));
            GlideUtils.setGlideImg(mContext, auditEntity.getStuIcon(), 0, ivHeadIcon);
            tvName.setText(auditEntity.getStuName());
            tvOrgName.setText(auditEntity.getStuNickname());
            ivGender.setImageResource(auditEntity.getSex().equals("1") ? R.mipmap.ic_sex_man : R.mipmap.ic_sex_women);
            tvContactDetails.setText(String.format("%s：%s",
                    auditEntity.getRelatives(), auditEntity.getPhoneNumber()));
            hintYes = "是您的学员";
            hintNo = "不是您的学员";
        } else {
            ivHeadIcon.setTextAndColor(TextUtils.isEmpty(auditEntity.getUserName()) ? "" : auditEntity.getUserName().substring(0, 1),
                    mContext.getResources().getColor(R.color.b0b2b6));
            GlideUtils.setGlideImg(mContext, auditEntity.getUserImage(), 0, ivHeadIcon);
            tvName.setText(auditEntity.getUserName());
            tvOrgName.setVisibility(View.GONE);
            ivGender.setVisibility(View.GONE);
            tvContactDetails.setText(String.format("%s", auditEntity.getMobile()));
            btnMyStu.setText("是我机构的教师");
            btnNoStu.setText("不是我机构的教师");
            hintYes = "是您机构的教师";
            hintNo = "不是您机构的教师";
        }

        btnMyStu.setOnClickListener(v -> {
            if (isYesRole()) {// 没权限
                return;
            } else {
                String name = "";
                if (TextUtils.equals(type, "1")) {//type 1: 添加学员 2: 添加教师
                    name = auditEntity.getStuName();
                } else {
                    name = auditEntity.getUserName();
                }
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage(String.format("您确定%s%s", name, hintYes))
                        .setPositiveButton("确定", (d, which) -> {
                            if (TextUtils.equals(type, "1")) {//type 1: 添加学员 2: 添加教师
                                getCardByPhone(auditEntity.getPhoneNumber());// 获取共用学员数量
                                auditsStudent(auditEntity, "1");
                            } else if (TextUtils.equals(type, "2")) {
                                auditedTeacher(auditEntity, "1");
                            }
                        })
                        .setNegativeButton("取消", (d, which) -> d.dismiss())
                        .create().show();
            }
        });
        btnNoStu.setOnClickListener(v -> {
            if (isYesRole()) {// 没权限
                return;
            } else {
                String name = "";
                if (TextUtils.equals(type, "1")) {//type 1: 添加学员 2: 添加教师
                    name = auditEntity.getStuName();
                } else {
                    name = auditEntity.getUserName();
                }
                new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage(String.format("您确定%s%s", name, hintNo))
                        .setPositiveButton("确定", (d, which) -> {
                            if (TextUtils.equals(type, "1")) {//type 1: 添加学员 2: 添加教师
                                auditsStudent(auditEntity, "2");
                            } else if (TextUtils.equals(type, "2")) {
                                auditedTeacher(auditEntity, "2");
                            }
                        })
                        .setNegativeButton("取消", (d, which) -> d.dismiss())
                        .create().show();
            }
        });
    }

    private void getIdentity() {
        // 49.获取机构的所有身份
        HttpManager.getInstance().doGetIdentity("TeacherPermisActivity",
                UserManager.getInstance().getOrgId(), new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        identityEntity.clear();
                        identityEntity.addAll(data.getData());
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("TeacherPermisActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout((Activity) mContext);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("TeacherPermisActivity onError", throwable.getMessage());
                    }
                });
    }

    private void auditsStudent(StudentEntity auditEntity, String option) {
        //61.审核学员
        //操作 1：是我的学员(审核通过) 2：不是我的学员(审核不通过)
        HttpManager.getInstance().doAuditsStudent("AuditAdapter", UserManager.getInstance().getOrgId(),
                auditEntity.getStuId(), option,
                new HttpCallBack<BaseDataModel<StudentEntity>>((Activity) mContext, true) {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_ACTIVE_STU));
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_TEACHER));
                        if (option.equals("1")) {
                            U.showToast("成功加入活跃学员,请继续为学员添加课时卡");
                            showCardDialog(auditEntity);
                        } else {
                            U.showToast("不是我的学员");
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NewWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout((Activity) mContext);
                        } else if (statusCode == 1070) {// 超出机构允许的最大学员限制!
                            U.showToast("机构学员人数已到达上限!");
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("NewWorkActivity onError", throwable.getMessage());
                    }
                });
    }

    private void auditedTeacher(StudentEntity auditEntity, String option) {
        String roleId = "1", roleTitle = "教务老师";
        if (identityEntity != null && identityEntity.size() > 1) {
            roleId = identityEntity.get(1).getPowerId();
            roleTitle = identityEntity.get(1).getPower();
        }
        //52.审核教师
        //操作 1：是我机构的教师(审核通过) 2：不是我机构的老师(审核不通过)
        HttpManager.getInstance().doAuditedTeacher("AuditAdapter", UserManager.getInstance().getOrgId(),
                auditEntity.getUserId(), option, roleId, roleTitle,
                new HttpCallBack<BaseDataModel<StudentEntity>>((Activity) mContext, true) {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_ACTIVE_TEACH));
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_TEACHER));
                        if (option.equals("1")) {
                            U.showToast("教师已成功加入该机构,请分配权限");
                            mContext.startActivity(new Intent(mContext, TeacherPermisActivity.class)
                                    .putExtra("auditEntity", auditEntity));
                        } else {
                            U.showToast("教师不属于该机构");
                        }
                        ((Activity) mContext).finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NewWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout((Activity) mContext);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.tag(" throwable " + throwable.getMessage());
                    }
                });
    }

    /**
     * 学员卡片操作弹框
     */
    private void showCardDialog(StudentEntity auditEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.dialog);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(mContext, R.layout.dialog_card_oper, null);
        final TextView tvUnbind = view.findViewById(R.id.tv_unbind);
        tvUnbind.setVisibility(View.GONE);
        final TextView tvFamiliar = view.findViewById(R.id.tv_familiar);
        tvFamiliar.setVisibility(strNum > 0 ? View.VISIBLE : View.GONE);
        tvFamiliar.setText("与家人共用课时卡");
        tvFamiliar.setOnClickListener(v -> {
            //与家人共用课时卡
            mContext.startActivity(new Intent(mContext, FamilyShareActivity.class)
                    .putExtra("stuDetails", auditEntity)
                    .putExtra("toShare", "3"));// toShare： 1:从添加学员进入 2:从学员详情进入 3:从审核学员进入
            ((AuditActivity) mContext).overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
            dialog.dismiss();

            ((Activity) mContext).finish();
        });
        final TextView tvCourseCard = view.findViewById(R.id.tv_course_card);
        tvCourseCard.setText("绑定已有课时卡");
        tvCourseCard.setOnClickListener(v -> {
            //绑定已有课时卡
            mContext.startActivity(new Intent(mContext, NewCardBuyActivity.class)
                    .putExtra("addType", "5")// // 5、选择 绑定已有课时卡
                    .putExtra("audit", "2")// 学员审核通过为学员绑定课时卡
                    .putExtra("stuDetails", auditEntity)
            );
            dialog.dismiss();

            ((Activity) mContext).finish();
        });
        final TextView tvEditCard = view.findViewById(R.id.tv_edit_card);
        tvEditCard.setText("添加新课时卡");
        tvEditCard.setOnClickListener(v -> {
            //添加新课时卡
            mContext.startActivity(new Intent(mContext, NewCardBuyActivity.class)
                    .putExtra("addType", "4")// 4、选择 添加新课时卡
                    .putExtra("audit", "1")// 学员审核通过为学员添加课时卡
                    .putExtra("stuDetails", auditEntity)
            );
            dialog.dismiss();

            ((Activity) mContext).finish();
        });
        final TextView tvCancel = view.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
        //设置弹窗在底部
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
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
        return R.layout.item_audit;
    }


    // true:没权限，fals:有权限
    public boolean isYesRole() {
        if (TextUtils.equals(type, "1")) {//type 1: 添加学员 2: 添加教师
            if (!UserManager.getInstance().isTrueRole("xy_1")) {
                UserManager.getInstance().showSuccessDialog((Activity) mContext
                        , mContext.getString(R.string.text_role));
                return true;
            }

        } else if (TextUtils.equals(type, "2")) {
            if (!UserManager.getInstance().isTrueRole("js_1")) {
                UserManager.getInstance().showSuccessDialog((Activity) mContext
                        , mContext.getString(R.string.text_role));
                return true;
            }
        }
        return false;
    }


    // 共用学员课时卡
    private void getCardByPhone(String phoneNum) {
        HttpManager.getInstance().doGetCardByPhone("FamilyShareActivity",
                UserManager.getInstance().getOrgId(), "", phoneNum,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        strNum = data.getData().size();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("WorkClassActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout((Activity) mContext);
                        } else if (statusCode == 1004) {// 用户不存在
                            strNum = 0;
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("WorkClassActivity onError", throwable.getMessage());
                    }
                });
    }
}
