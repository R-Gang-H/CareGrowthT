package com.caregrowtht.app.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.OrgEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.sina.weibo.sdk.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * haoruigang on by 2018-4-23 11:13:40
 * 确定添加机构
 */
public class AddOrgActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_avatar)
    AvatarImageView ivAvatar;
    @BindView(R.id.tv_org_name)
    TextView tvOrgName;
    @BindView(R.id.tv_excut)
    TextView tvExcut;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private OrgEntity orgEntity;
    private String orgId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_org;
    }

    @Override
    public void initView() {
        tvTitle.setText("加入机构");
    }

    @Override
    public void initData() {
        orgEntity = (OrgEntity) getIntent().getSerializableExtra("OrgEntity");
        if (orgEntity != null) {
            ivAvatar.setTextAndColor(TextUtils.isEmpty(orgEntity.getOrgShortName()) ? "" :
                    orgEntity.getOrgShortName(), getResources().getColor(R.color.b0b2b6));
            GlideUtils.setGlideImg(this, orgEntity.getOrgImage(), 0, ivAvatar);
            tvOrgName.setText(String.format("%s", TextUtils.isEmpty(orgEntity.getOrgChainName()) ?
                    orgEntity.getOrgName() : orgEntity.getOrgName() + orgEntity.getOrgChainName()));
            orgId = orgEntity.getOrgId();
            getOrgTeachers("1", "1");
        }
    }

    @OnClick({R.id.rl_back_button, R.id.btn_cancel, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_submit:
                getOrgTeachers("2", "2");
                if (checkStatus()) return;
                bandOrg();
                break;
        }
    }

    private boolean checkStatus() {
        String status = orgEntity.getStatus();//审核状态 1审核通过 2待审核 3审核不通过
        switch (status) {
            case "1":
                tvExcut.setText("已加入该机构");
                isVisible(false, false, true);
                return true;
            case "2":
                tvExcut.setText("该机构审核中");
                isVisible(false, false, true);
                return true;
            default:
                UserManager.getInstance().isNoPass(orgId);
                isVisible(true, true, false);
                return false;
        }
    }

    private void isVisible(boolean cancel, boolean submit, boolean excut) {
        btnCancel.setVisibility(cancel ? View.VISIBLE : View.GONE);
        btnSubmit.setVisibility(submit ? View.VISIBLE : View.GONE);
        tvExcut.setVisibility(excut ? View.VISIBLE : View.GONE);
    }

    private void bandOrg() {
        if (StrUtils.isEmpty(orgId)) {
            U.showToast("请输入机构代码");
            return;
        }
        HttpManager.getInstance().doBindOrg("AddInstActivity", orgId,
                new HttpCallBack<BaseDataModel<OrgEntity>>(AddOrgActivity.this) {
                    @Override
                    public void onSuccess(BaseDataModel<OrgEntity> data) {
                        UserManager.getInstance().orgAddTeacher(orgId);
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.TEACHER_HOME_EVENT));
                        startActivity(new Intent(AddOrgActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(AddOrgActivity.this);
                        } else if (statusCode == 1025) {
                            U.showToast("不能重复绑定!");
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.d("AddOrgActivity", throwable.getMessage());
                    }
                });
    }

    /**
     * 48.获取机构的教师
     *
     * @param status       教师的状态 1：正式 2：待审核
     * @param leave_status 离职状态 1：在职 2：离职
     */
    private void getOrgTeachers(String status, String leave_status) {
        HttpManager.getInstance().doGetOrgTeachers("TeacherMsgActivity",
                orgId, status, leave_status, new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        boolean isExcut = false, isStatus = false;
                        UserManager instance = UserManager.getInstance();
                        String UserId = instance.userData.getMobile();// 当前用户手机
                        ArrayList<StudentEntity> tercherData = data.getData();
                        if (TextUtils.equals("1", status) && TextUtils.equals("1", leave_status)) {//正式,在职
                            String[] orgIds = instance.userData.getOrgIds().split(",");
                            for (String orgid : orgIds) {
                                if (orgid.equals(orgId)) {// 已添加
                                    isExcut = true;
                                    break;
                                }
                            }
                            isStatus = instance.isStatus(isStatus, UserId, tercherData);// 在职教师
                            if (isExcut || isStatus) {
                                checkStatus();
                            }
                        } else {
                            isStatus = instance.isStatus(isStatus, UserId, tercherData);// 离职教师
                            if (isStatus) {
                                bandOrg();
                            }
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("TeacherMsgActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(AddOrgActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("TeacherMsgActivity throwable", throwable.getMessage());
                    }
                });
    }

}
