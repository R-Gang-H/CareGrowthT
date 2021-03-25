package com.caregrowtht.app.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.user.ToUIEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by haoruigang on 2018-4-28 14:05:06.
 * 教师帮学生签到/请假
 */

public class LeaveActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_reason)
    EditText etReason;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    private String lessonId;
    private String stuId;
    private String type;

    @Override
    public int getLayoutId() {
        return R.layout.activity_leave;
    }

    @Override
    public void initView() {
        type = getIntent().getStringExtra("type");
        tvTitle.setText(TextUtils.equals(type, "1") ? "签到" : "请假");//1:签到;2:请假
        etReason.setHint(TextUtils.equals(type, "1") ? "请输入签到备注" : "请输入请假备注");
        tvStatus.setText(TextUtils.equals(type, "1") ? "添加签到备注" : "添加请假备注");
    }

    @Override
    public void initData() {
        lessonId = getIntent().getStringExtra("lessonId");
        stuId = getIntent().getStringExtra("stuId");
    }

    @OnClick({R.id.rl_back_button, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.btn_submit:
                ask4Leave();
                break;
        }
    }

    private void ask4Leave() {
        String reason = etReason.getText().toString().trim();
        if (TextUtils.isEmpty(reason)) {
            U.showToast(TextUtils.equals(type, "1") ? "请输入签到备注" : "请输入请假原因");
            return;
        }

        HttpManager.getInstance().doSignByTeacher("LeaveActivity",
                lessonId, stuId, type, reason,
                new HttpCallBack<BaseDataModel<StudentEntity>>(LeaveActivity.this) {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.COURSE));
                        startActivity(new Intent(LeaveActivity.this, CourserActivity.class));
                        U.showToast("成功");
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(LeaveActivity.this);
                        } else if (statusCode == 1038) {
                            U.showToast("开课前半小时才可以签到!");
                            finish();
                        } else if (statusCode == 1049) {
                            U.showToast("已过请假时间!");
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });

    }

}
