package com.caregrowtht.app.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.UserEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.user.UserManager;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-4-20 16:26:35
 * 注册
 */
public class RegActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.tv_get_code)
    TextView tvGetCode;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.ll_protocol)
    LinearLayout llProtocol;
    @BindView(R.id.btn_reg)
    Button btnReg;


    private CountDownTimer countDownTimer;
    private String phoneNum, code, pwd;
    private String type;

    @Override
    public int getLayoutId() {
        return R.layout.activity_reg;
    }

    @Override
    public void initView() {
        type = getIntent().getStringExtra("type");
        tvTitle.setText(TextUtils.equals(type, "1") ? "注册" : "重置密码");//1注册 2找回密码
        btnReg.setText(TextUtils.equals(type, "1") ? "注册" : "确定");//1注册 2找回密码
        llProtocol.setVisibility(TextUtils.equals(type, "1") ? View.VISIBLE : View.GONE);//1注册 2找回密码
    }

    @Override
    public void initData() {

    }


    @OnClick({R.id.tv_get_code, R.id.ll_protocol, R.id.btn_reg, R.id.rl_back_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_get_code:
                getRandomCode();
                break;
            case R.id.ll_protocol:
                Intent intent = new Intent(this, UserTermActivity.class);
                intent.setData(Uri.parse(Constant.USER_AGREEMENT));
                intent.putExtra("openType", "1");// 用户协议
                startActivity(intent);
                break;
            case R.id.btn_reg:
                String phoneNum = etPhone.getText().toString();
                if (StrUtils.isEmpty(phoneNum)) {
                    U.showToast(" 请输入您的手机号");
                    return;
                }
//                if (!phoneNum.matches("0?(13|14|15|17|18)[0-9]{9}")) {
                if (phoneNum.length() != 11) {
                    U.showToast("请输入正确的手机号码");
                    return;
                }
                code = etCode.getText().toString();
                if (StrUtils.isEmpty(code)) {
                    U.showToast("验证码不能为空");
                    return;
                }
                pwd = etPwd.getText().toString();
                if (StrUtils.isEmpty(pwd)) {
                    U.showToast("密码不能为空");
                    return;
                }
                if (pwd.length() < 6 || pwd.length() > 16) {
                    U.showToast("请输入6-16位的密码");
                    return;
                }
                reg();
                break;
        }
    }

    // 获取验证码
    private void getRandomCode() {
        String phoneNum = etPhone.getText().toString();
//        if (!phoneNum.matches("0?(13|14|15|17|18)[0-9]{9}")) {
        if (phoneNum.length() != 11) {
            U.showToast("请输入正确的手机号码");
            return;
        }
        etCode.setFocusable(true);
        etCode.setFocusableInTouchMode(true);
        etCode.requestFocus();

        /**
         * haoruigang 2018-3-30 10:31:11   获取验证码接口
         */
        HttpManager.getInstance().doRandomCode("RegActivity",
                phoneNum,
                new HttpCallBack<BaseDataModel<UserEntity>>(
                        RegActivity.this, true) {
                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> date) {
                        countDown();
//                        String code = date.getData().get(0).getCode();
//                        etCode.setText(code);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        U.showToast(errorMsg);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    // 倒计时
    private void countDown() {
        phoneNum = etPhone.getText().toString();
//        if (!phoneNum.matches("0?(13|14|15|17|18)[0-9]{9}")) {
        if (phoneNum.length() != 11) {
            U.showToast("请输入正确的手机号码");
            return;
        }
        tvGetCode.setClickable(false);
        if (countDownTimer == null)
            countDownTimer = new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long l) {
                    tvGetCode.setText((l / 1000) + "S");
                }

                @Override
                public void onFinish() {
                    tvGetCode.setClickable(true);
                    tvGetCode.setText("获取验证码");
                    tvGetCode.setOnClickListener(view -> getRandomCode());
                }
            };
        countDownTimer.start();
    }


    private void reg() {
        //haoruigang on 2018-4-20 16:50:01 注册/忘记密码
        HttpManager.getInstance().doReginster("RegActivity",
                phoneNum, pwd, code, type,
                new HttpCallBack<BaseDataModel<UserEntity>>(RegActivity.this) {

                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        U.showToast(TextUtils.equals(type, "1") ? "注册成功!" : "密码修改成功!");
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            countDownTimer = null;
                        }

                        UserManager.getInstance().save(RegActivity.this, data.getData().get(0));
                        String isNew = UserManager.getInstance().userData.getIsNew();
                        startActivity(new Intent(RegActivity.this, LoginActivity.class));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("RegActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1005) {
                            errorMsg = "重复注册!";
                            U.showToast(errorMsg);
                        } else if (statusCode == 1006) {
                            errorMsg = "验证码错误!";
                            U.showToast(errorMsg);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("RegActivity onError", throwable.getMessage());
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
