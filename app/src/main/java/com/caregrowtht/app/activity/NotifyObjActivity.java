package com.caregrowtht.app.activity;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.NotifyObjAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.OrgNotifyEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.user.MultipleItem;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.LoadingFrameView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-7-27 17:20:54
 * 通知对象
 */
public class NotifyObjActivity extends BaseActivity {

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_receipt_no)

    TextView tvReceiptNo;
    @BindView(R.id.ll_tob)
    LinearLayout llTob;
    @BindView(R.id.tv_receipt_all)
    TextView tvReceiptAll;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    private ArrayList<OrgNotifyEntity> orgNotifyList = new ArrayList<>();
    private OrgNotifyEntity notifyEntity;
    private String type = "2";
    private NotifyObjAdapter notifyObjAdapter;
    private ArrayList<MultipleItem> list = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_notify_obj;
    }

    @Override
    public void initView() {
        ivLeft.setBackgroundResource(R.mipmap.ic_close_1);
        tvTitle.setText("通知对象");
        notifyEntity = (OrgNotifyEntity) getIntent().getSerializableExtra("notifyEntity");
        String isReceipt = notifyEntity.getIsReceipt();// 是否需要回执 1：需要回执 2：不需要回执
        if (TextUtils.equals(isReceipt, "2")) {
            llTob.setVisibility(View.GONE);
            type = "1";
            btnSend.setVisibility(View.GONE);
        }
        initRecyclerView(recyclerView, true);
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setOnRefreshListener(refreshlayout -> getNotifyObj(type));
        list.clear();
        list.add(new MultipleItem(MultipleItem.TYPE_RECEIPT_NO));
        list.add(new MultipleItem(MultipleItem.TYPE_RECEIPT_ALL));
        notifyObjAdapter = new NotifyObjAdapter(this, list, orgNotifyList, isReceipt);
        recyclerView.setAdapter(notifyObjAdapter);
    }

    @Override
    public void initData() {
        getNotifyObj(type);//1:全部 2：未回执
    }

    private void getNotifyObj(String type) {
        HttpManager.getInstance().doGetNoticePeople("NotifyObjActivity",
                notifyEntity.getNotifiId(), type, new HttpCallBack<BaseDataModel<OrgNotifyEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<OrgNotifyEntity> data) {
                        orgNotifyList.clear();
                        orgNotifyList.addAll(data.getData());
                        notifyObjAdapter.setData(data.getData(), type);

                        if (orgNotifyList.size() > 0) {
                            loadView.delayShowContainer(true);
                        } else {
                            loadView.setNoShown(true);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NotifyObjActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(NotifyObjActivity.this);
                        } else {
                            loadView.setErrorShown(true, v -> getNotifyObj(type));
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("NotifyObjActivity onError", throwable.getMessage());
                        loadView.setErrorShown(true, v -> getNotifyObj(type));
                    }
                });
        refreshLayout.finishLoadmore();
        refreshLayout.finishRefresh();
    }

    @OnClick({R.id.rl_back_button, R.id.tv_receipt_no, R.id.tv_receipt_all, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
                break;
            case R.id.tv_receipt_no:
                type = "2";
                tvReceiptNo.setBackgroundResource(R.drawable.notify_obj_round_left);
                tvReceiptNo.setTextColor(ResourcesUtils.getColor(R.color.white));
                tvReceiptAll.setBackground(null);
                tvReceiptAll.setTextColor(ResourcesUtils.getColor(R.color.blueLight));
                btnSend.setVisibility(View.VISIBLE);
                getNotifyObj(type);// 2:未回执
                break;
            case R.id.tv_receipt_all:
                type = "1";
                tvReceiptNo.setBackground(null);
                tvReceiptNo.setTextColor(ResourcesUtils.getColor(R.color.blueLight));
                tvReceiptAll.setBackgroundResource(R.drawable.notify_obj_round_right);
                tvReceiptAll.setTextColor(ResourcesUtils.getColor(R.color.white));
                btnSend.setVisibility(View.GONE);
                getNotifyObj(type);// 1:全部
                break;
            case R.id.btn_send:
                if (!UserManager.getInstance().isTrueRole("tz_1")) {
                    UserManager.getInstance().showSuccessDialog(this
                            , getString(R.string.text_role));
                } else {
                    sendNoticeAgain();
                }
                break;
        }
    }

    /**
     * 27.机构通知 再次发送
     * haoruigang on 2018-08-06 23:31:28
     */
    private void sendNoticeAgain() {
        HttpManager.getInstance().doSendNoticeAgain("OrgNotifyAdapter",
                notifyEntity.getNotifiId(),
                new HttpCallBack<BaseDataModel<OrgNotifyEntity>>(NotifyObjActivity.this, true) {
                    @Override
                    public void onSuccess(BaseDataModel<OrgNotifyEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_NOTIFY));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("SchoolWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(NotifyObjActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) {
        if ((paramKeyEvent.getAction() == 0)
                && (paramKeyEvent.getKeyCode() == 4)) {
            finish();
            overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
        }
        return super.dispatchKeyEvent(paramKeyEvent);
    }
}
