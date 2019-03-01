package com.caregrowtht.app.activity;

import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.UserEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GradientUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.user.UserManager;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-4-20 16:26:01
 * 登录
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class LoginActivity extends BaseActivity {

    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.tv_forget_pwd)
    TextView tvForgetPwd;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_rig)
    Button btnRig;

    String phoneNum, pwd;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        //动态改变“colorPrimaryDark”来实现沉浸式状态栏
        GradientUtils.setColor(this, R.drawable.mine_title_bg, true);
    }

    @Override
    public void initData() {

    }

    private void login() {
        phoneNum = etPhone.getText().toString();
        if (StrUtils.isEmpty(phoneNum)) {
            U.showToast("账号不能为空");
            return;
        }
        pwd = etCode.getText().toString();
        if (StrUtils.isEmpty(pwd)) {
            U.showToast("密码不能为空");
            return;
        }
        /**
         * haoruigang 2018-3-30 10:20:01 登录接口
         */
        HttpManager.getInstance().doLoginRequest("LoginActivity",
                phoneNum, pwd, "2",
                new HttpCallBack<BaseDataModel<UserEntity>>(LoginActivity.this) {

                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        UserManager.getInstance().save(LoginActivity.this, data.getData().get(0));
                        String isNew = UserManager.getInstance().userData.getIsNew();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        Log.e("onFail", " " + statusCode);
                        String msg = errorMsg;
                        switch (statusCode) {
                            case 1039:
                                msg = "密码错误";
                                break;
                            case 1004:
                                msg = "该手机号尚未注册";
                                break;
                        }
                        U.showToast(msg);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("onError", " " + throwable.getMessage());
                    }
                });
    }

    @OnClick({R.id.tv_forget_pwd, R.id.btn_login, R.id.btn_rig})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_forget_pwd:
                startActivity(new Intent(this, RegActivity.class).putExtra("type", "2"));
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_rig:
                startActivity(new Intent(this, RegActivity.class).putExtra("type", "1"));
                break;
        }
    }
}
