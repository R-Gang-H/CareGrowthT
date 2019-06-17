package com.caregrowtht.app.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.OrgNotifyEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.ImgLabelUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-12-5 17:43:57 动态详情
 */
public class NotifityInfoActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_orgName)
    TextView tvOrgName;
    @BindView(R.id.tv_publishTime)
    TextView tvPublishTime;
    @BindView(R.id.tv_receipt)
    TextView tvReceipt;
    @BindView(R.id.tv_notifity_detail)
    TextView tvNotifityDetail;
    @BindView(R.id.btn_send)
    Button btnSend;


    private MessageEntity msgEntity;
    private String orgId;
    private OrgNotifyEntity notifyEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_notifity_info;
    }

    @Override
    public void initView() {
        tvTitle.setText("动态详情");
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        notifyEntity = (OrgNotifyEntity) getIntent().getSerializableExtra("notifyEntity");
        if (msgEntity != null) {
            orgId = msgEntity.getOrgId();
            UserManager.getInstance().setOrgId(orgId);
            boolean isReceipt, isShowOrg, isShowNotify = false, isReceipt1;
            String orgName = msgEntity.getOrgName(), time, content;
            if (notifyEntity != null) {
                //15：我的机构通知/是否需要回执 1：不需要回执 2：待回执 3：已经回执
                isReceipt = TextUtils.equals(notifyEntity.getReceipt(), "2");
                isShowOrg = msgEntity.getType().equals("15") && isReceipt;
                isReceipt1 = TextUtils.equals(notifyEntity.getReceipt(), "3");

                time = notifyEntity.getUpdateAt();
                content = notifyEntity.getContent();

            } else {
                //4,5：机构和系统发出的通知/是否需要回执 1：不需要回执 2：待回执 3：已经回执
                isReceipt = TextUtils.equals(msgEntity.getReceipt(), "2");
                isShowOrg = msgEntity.getType().equals("4") && isReceipt;
                isShowNotify = msgEntity.getType().equals("5") && isReceipt;
                isReceipt1 = TextUtils.equals(msgEntity.getReceipt(), "3");

                time = msgEntity.getTime();
                content = msgEntity.getContent();
            }
            tvReceipt.setVisibility(isReceipt1 ? View.VISIBLE : View.GONE);// 已经回执
            btnSend.setVisibility((isShowOrg || isShowNotify) ? View.VISIBLE : View.GONE);

            tvOrgName.setText(orgName);
            tvPublishTime.setText(TimeUtils.GetFriendlyTime(time));
            tvNotifityDetail.setText(content);
        } else {
            orgId = UserManager.getInstance().getOrgId();
        }
    }

    @Override
    public void initData() {
    }

    @OnClick({R.id.rl_back_button, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.btn_send:
                setReceipt();
                break;
        }
    }

    private void setReceipt() {
        // 回执动态
        HttpManager.getInstance().doSetReceipt("NotifityInfoActivity", msgEntity.getTargetId(),
                orgId, new HttpCallBack<BaseDataModel>() {
                    @Override
                    public void onSuccess(BaseDataModel data) {
                        U.showToast("成功");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_TEACHER));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NotifityInfoActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(NotifityInfoActivity.this);
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

}
