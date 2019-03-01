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
 * haoruigang on 2018-12-5 17:43:57 通知详情
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

    @Override
    public int getLayoutId() {
        return R.layout.activity_notifity_info;
    }

    @Override
    public void initView() {
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        String titleName = "";
        switch (msgEntity.getType()) {
            case "2"://2：课后反馈（我的学员发布的课后反馈）
            case "7"://7：课后反馈（我发布的课后反馈收到了评论或者点赞）
                titleName = "课后反馈";
                break;
            case "3"://3：个人类型
                titleName = "个人类型";
                break;
            case "4"://4：机构信息
            case "5"://5：机构发出的通知
                titleName = "机构信息";
                break;
            case "6"://6：系统发出的通知
                titleName = "系统信息";
                break;
        }
        tvTitle.setText(String.format("%s动态通知", titleName));
        //4,5：机构和系统发出的通知/是否需要回执 1：不需要回执 2：待回执 3：已经回执
        boolean isReceipt = TextUtils.equals(msgEntity.getReceipt(), "2");
        boolean isShowOrg = msgEntity.getType().equals("4") && isReceipt;
        boolean isShowNotify = msgEntity.getType().equals("5") && isReceipt;
        boolean isReceipt1 = TextUtils.equals(msgEntity.getReceipt(), "3");
        tvReceipt.setVisibility(isReceipt1 ? View.VISIBLE : View.GONE);// 已经回执
        btnSend.setVisibility((isShowOrg || isShowNotify) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void initData() {
        //解决NetworkOnMainThreadException异常
        ImgLabelUtils.getInstance().struct();

        setData(msgEntity);
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
                UserManager.getInstance().getOrgId(), new HttpCallBack<BaseDataModel>() {
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

    /**
     * 通知详情 数据处理
     * haoruigang on 2018-12-5 17:46:42
     *
     * @param data
     */
    public void setData(MessageEntity data) {
        tvOrgName.setText(data.getOrgName());
        tvPublishTime.setText(TimeUtils.GetFriendlyTime(data.getTime()));
        tvNotifityDetail.setText(data.getContent());
    }
}
