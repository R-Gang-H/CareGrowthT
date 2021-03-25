package com.caregrowtht.app.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseBeanModel;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by haoruigang on 2018-5-25 17:30:04.
 * 意见反馈
 */

public class AppFeedbackActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
    //    @BindView(R.id.rl_title_bg)
//    RelativeLayout rlTitleBg;
//    @BindView(R.id.rl_title)
//    RelativeLayout rlTitle;
    @BindView(R.id.et_feedback)
    EditText etFeedback;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    @Override
    public int getLayoutId() {
        return R.layout.activity_app_feedback;
    }

    @Override
    public void initView() {
        tvTitle.setText("添加反馈");
    }

    @Override
    public void initData() {

    }


    @OnClick({R.id.rl_back_button, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.btn_submit:
                feedback();
                break;
        }
    }

    private void feedback() {
        String feedback = etFeedback.getText().toString().trim();
        if (TextUtils.isEmpty(feedback)) {
            U.showToast("请输入留言反馈");
            return;
        }

        HttpManager.getInstance().doAppFeedback("AppFeedbackActivity", feedback, new HttpCallBack<BaseBeanModel>(this) {
            @Override
            public void onSuccess(BaseBeanModel data) {
                U.showToast("反馈成功");
                finish();
            }

            @Override
            public void onFail(int statusCode, String errorMsg) {
                if (statusCode == 1002 || statusCode == 1011) {//异地登录
                    U.showToast("该账户在异地登录!");
                    HttpManager.getInstance().dologout(AppFeedbackActivity.this);
                } else {
                    U.showToast(errorMsg);
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }
}
