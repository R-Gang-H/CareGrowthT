package com.caregrowtht.app.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.library.utils.U;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.permissions.PermissionCallBackM;
import com.caregrowtht.app.user.UserManager;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-10-18 10:52:39
 * 加入机构
 */
public class JoinOrgActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;

    private String orgId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_serve_org;
    }

    @Override
    public void initView() {
        tvTitle.setText("选择供职机构");
        rlBackButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.rl_back_button, R.id.iv_avatar_add, R.id.btn_add_org, R.id.iv_avatar_create, R.id.btn_create_org})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.iv_avatar_add:
            case R.id.btn_add_org://加入机构
                //动态获取 Camera 权限 haoruigang on 2018-4-4 19:13:48
                getCamera();
                break;
            case R.id.iv_avatar_create:
            case R.id.btn_create_org://创建新机构
                startActivity(new Intent(this, CreateOrgHintActivity.class));
                break;
        }
    }

    //获取CAMERA权限 haoruigang on 2018-4-9 17:07:32
    public void getCamera() {
        requestPermission(
                Constant.RC_CALL_PHONE,
                new String[]{Manifest.permission.CAMERA},
                getString(R.string.rationale_camera),
                new PermissionCallBackM() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionGrantedM(int requestCode, String... perms) {
                        LogUtils.e(JoinOrgActivity.this, "TODO: CAMERA Granted", Toast.LENGTH_SHORT);
                        startActivityForResult(new Intent(JoinOrgActivity.this, QRCodeActivity.class), 6980);
                    }

                    @Override
                    public void onPermissionDeniedM(int requestCode, String... perms) {
                        LogUtils.e(JoinOrgActivity.this, "TODO: CAMERA Denied", Toast.LENGTH_SHORT);
                    }
                });
    }

    //    String temp;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 6980) {
            if (null != data) {
                Bundle bundle = data.getExtras();
//                if (bundle == null) {
//                    return;
//                }
                String type = bundle.getString("type");// type: 1: org=000H(机构Id) 2: org=00CC&lesson=2265(课程Id)
                String invitationCode = bundle.getString("invitationCode");
                if (type.equals("1")) {
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS
                            && !StrUtils.isEmpty(invitationCode)) {
                        orgId = invitationCode;
                        UserManager.getInstance().getOrgInfo(orgId, this, "1", null);
                    } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                        U.showToast("未识别二维码!");
                    }
                } else {
                    startActivity(new Intent(this, CourserActivity.class)
                            .putExtra("courseId", invitationCode));
                }
            }
        }
    }

}
