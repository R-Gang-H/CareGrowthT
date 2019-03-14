package com.caregrowtht.app.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.view.CircleImageView;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.OrgEntity;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.user.UserManager;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-9-6 12:39:53
 * 添加成功-邀请学员
 */
public class InviteStudentActivity extends BaseActivity {


    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_qrcode)
    ImageView ivQrcode;
    @BindView(R.id.tv_theacher_invite)
    TextView tvTheacherInvite;
    @BindView(R.id.iv_org_img)
    CircleImageView ivOrgImg;
    @BindView(R.id.tv_org_name)
    TextView tvOrgName;
    @BindView(R.id.tv_wechat)
    TextView tvWechat;
    @BindView(R.id.tv_message)
    TextView tvMessage;

    @Override
    public int getLayoutId() {
        return R.layout.activity_invite_stu_reg;
    }

    @Override
    public void initView() {
        tvTitle.setText("邀请学员");
    }

    @Override
    public void initData() {
        UserManager instance = UserManager.getInstance();
        OrgEntity orgEntity = instance.getOrgEntity();
        GlideUtils.setGlideImg(this, orgEntity.getOrgImage(),
                R.mipmap.ic_avatar_default, ivOrgImg);
        tvOrgName.setText(orgEntity.getOrgName());
        tvTheacherInvite.setText(String.format("%s邀请您关注爱成长公众号",
                UserManager.getInstance().userData.getName()));
    }


    @OnClick({R.id.rl_back_button, R.id.tv_wechat, R.id.tv_message})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_wechat:
                break;
            case R.id.tv_message:
                break;
        }
    }
}
