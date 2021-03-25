package com.caregrowtht.app.activity;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.DensityUtil;
import com.android.library.utils.U;
import com.android.library.view.CircleImageView;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.OrgEntity;
import com.caregrowtht.app.uitil.FilePickerUtils;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.ImgLabelUtils;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.RecoDialog;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.File;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-9-6 12:39:53
 * 添加成功-邀请教师
 */
public class InviteTeacherActivity extends BaseActivity {


    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.cl_teacher)
    CardView clTeacher;
    @BindView(R.id.tv_theacher_invite)
    TextView tvTheacherInvite;
    @BindView(R.id.iv_org_img)
    CircleImageView ivOrgImg;
    @BindView(R.id.tv_org_name)
    TextView tvOrgName;

    String retentImg;
    boolean isRetent;

    @Override
    public int getLayoutId() {
        return R.layout.activity_invite_teacher;
    }

    @Override
    public void initView() {
        tvTitle.setText("邀请教师");
    }

    @Override
    public void initData() {
        UserManager instance = UserManager.getInstance();
        MessageEntity msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        OrgEntity orgEntity = instance.getOrgEntity();
        String orgName;
        if (msgEntity != null) {
            orgName = String.format("%s", TextUtils.isEmpty(msgEntity.getOrgChainName()) ?
                    msgEntity.getOrgName() : msgEntity.getOrgName() + msgEntity.getOrgChainName());
        } else {
            orgName = String.format("%s", TextUtils.isEmpty(orgEntity.getOrgChainName()) ?
                    orgEntity.getOrgName() : orgEntity.getOrgName() + orgEntity.getOrgChainName());
        }
        GlideUtils.setGlideImg(this, orgEntity.getOrgImage(),
                R.mipmap.ic_avatar_default, ivOrgImg);
        tvOrgName.setText(orgName);
        tvTheacherInvite.setText(String.format("%s邀请您下载最方便快捷的教务管理工具爱成长教师端",
                UserManager.getInstance().userData.getName()));
        retentImg = "sdcard/" + System.currentTimeMillis() + ".png";
        clTeacher.post(() -> {
            // 固定到相对位置
            int[] location = new int[2];
            clTeacher.getLocationOnScreen(location);
            int left = location[0];
            int top = location[1];
            ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(clTeacher.getLayoutParams());
            marginParams.leftMargin = left;
            marginParams.topMargin = top - DensityUtil.getStatusBarHeight(this);//减去状态栏的高度
            marginParams.width = clTeacher.getWidth();
            marginParams.height = clTeacher.getHeight();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(marginParams);
            clTeacher.setLayoutParams(params);
            isRetent = ImgLabelUtils.shotBitmap(clTeacher, clTeacher.getWidth(), clTeacher.getHeight(), retentImg);
        });
    }


    @OnClick({R.id.rl_back_button, R.id.tv_wechat, R.id.tv_message})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                if (isRetent) {
                    finish();
                }
                break;
            case R.id.tv_wechat:
                if (isRetent) {
                    UMImage pImg = new UMImage(this, new File(retentImg));
                    pImg.setThumb(pImg);// 设置缩略图
                    new RecoDialog(this, pImg, view1 -> {
                        FilePickerUtils.getInstance().deleteSingleFile(retentImg);//删除
                    }).share(SHARE_MEDIA.WEIXIN);
                } else {
                    U.showToast("分享失败!");
                }
                break;
            case R.id.tv_message:
                if (isRetent) {
                    // 将bitmap转换成drawable
                    UMImage pImg = new UMImage(this, new File(retentImg));
                    pImg.setThumb(pImg);// 设置缩略图
                    new RecoDialog(this, pImg, new File(retentImg), view1 -> {
                        FilePickerUtils.getInstance().deleteSingleFile(retentImg);//删除
                    }).shareMessage();
                } else {
                    U.showToast("分享失败!");
                }
                break;
        }
    }
}
