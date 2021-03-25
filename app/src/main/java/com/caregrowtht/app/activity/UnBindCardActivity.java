package com.caregrowtht.app.activity;

import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.library.utils.U;
import com.android.library.view.WheelPopup;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.user.ToUIEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 解除绑定课时卡
 */
public class UnBindCardActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_monery)
    EditText etMonery;
    @BindView(R.id.tv_reason_content)
    TextView tvReasonContent;
    @BindView(R.id.et_remark)
    EditText etRemark;

    StudentEntity stuDetails;
    CourseEntity courseCardsEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_un_bind_card;
    }

    @Override
    public void initView() {
        tvTitle.setText("解绑课时卡");
        tvReasonContent.setText("更换课时卡种类");
    }

    @Override
    public void initData() {
        courseCardsEntity = (CourseEntity) getIntent().getSerializableExtra("CourseCardsEntity");
        stuDetails = (StudentEntity) getIntent().getSerializableExtra("StudentEntity");
        etMonery.setText(String.valueOf(Float.valueOf(courseCardsEntity.getCardLeftPrice()) / 100));
    }

    /**
     * 36.解除绑定课时卡
     */
    private void dropChildCard() {
        String monery = etMonery.getText().toString().trim();
        String reason = tvReasonContent.getText().toString().trim();
        String remark = etRemark.getText().toString().trim();
        if (StrUtils.isEmpty(monery)) {
            U.showToast("请输入退费金额");
            return;
        }
        if (StrUtils.isEmpty(reason)) {
            U.showToast("请输入退费原因");
            return;
        }
        if (StrUtils.isEmpty(remark)) {
            U.showToast("请输入备注");
            return;
        }
        HttpManager.getInstance().doDropChildCard("UnBindCardActivity", courseCardsEntity.getCardId(),
                stuDetails.getStuId(), String.valueOf(Float.valueOf(monery) * 100), reason, remark,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_STUDENT));
                        U.showToast("解绑成功");
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("UnBindCardActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(UnBindCardActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("UnBindCardActivity onError", throwable.getMessage());
                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.tv_reason_content, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_reason_content:
                hideKeyboard();
                selectReason();
                break;
            case R.id.btn_submit:
                if (StrUtils.isNotEmpty(courseCardsEntity) && StrUtils.isNotEmpty(stuDetails)) {
                    dropChildCard();
                }
                break;
        }
    }

    /**
     * 选择退费原因
     */
    private void selectReason() {
        WheelPopup pop = new WheelPopup(UnBindCardActivity.this, Constant.reasonContent);
        pop.showAtLocation(View.inflate(UnBindCardActivity.this,
                R.layout.popup_wheel_select, null),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        pop.setSelectListener((argValue, position) -> {
            tvReasonContent.setText(argValue);
            return null;
        });
    }
}
