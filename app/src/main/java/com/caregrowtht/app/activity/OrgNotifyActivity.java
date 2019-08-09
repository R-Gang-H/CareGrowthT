package com.caregrowtht.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.OrgNotifyAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.OrgNotifyEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-7-27 14:30:27
 * 机构通知
 */
public class OrgNotifyActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ll_tob)
    LinearLayout llTob;
    @BindView(R.id.tv_my_relase)
    TextView tvMyRelase;
    @BindView(R.id.tv_all)
    TextView tvAll;
    @BindView(R.id.iv_add)
    ImageView ivAll;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    //全部
    List<OrgNotifyEntity> orgNotifyList = new ArrayList<>();
    @BindView(R.id.iv_empty)
    TextView ivEmpty;
    @BindView(R.id.rl_yes_data)
    RelativeLayout rlYesData;

    private OrgNotifyAdapter notifyAdapter;
    private String OrgId;
    private int position;
    private MessageEntity msgEntity;
    private String type;// 1 我发布的通知 2我收到的通知 3全部

    @Override
    public int getLayoutId() {
        return R.layout.activity_org_notify;
    }

    @Override
    public void initView() {
        String str = "<font color='#333333'>这是能够查看回执的通知</font><br/><br/><font color='#666666'><small>赶快发一条试试吧</small></font>";
        ivEmpty.setText(Html.fromHtml(str));
        initRecyclerView(recyclerView, true);
        notifyAdapter = new OrgNotifyAdapter(orgNotifyList, this, this);//@TODO
        recyclerView.setAdapter(notifyAdapter);

        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            pageIndex = 1;
            OrgNotify(true);
        });
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnLoadmoreListener(refreshlayout -> {
            pageIndex++;
            OrgNotify(false);
        });
        refreshLayout.setEnableLoadmoreWhenContentNotFull(false);
    }

    @Override
    public void initData() {
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (StrUtils.isNotEmpty(msgEntity)) {
            OrgId = msgEntity.getOrgId();
            UserManager.getInstance().setOrgId(OrgId);
            if (msgEntity.getType().equals("16")) {// 16：|84：通知管理
                tvTitle.setText("通知管理");
                llTob.setVisibility(View.VISIBLE);
                type = "1"; //1 我发布的通知
            } else {// 15：|83：我的机构通知
                tvTitle.setText("机构通知");
                ivAll.setVisibility(View.GONE);
                type = "2"; //2我收到的通知
            }
        } else {
            tvTitle.setText("通知管理");
            llTob.setVisibility(View.VISIBLE);
            type = "1"; //1 我发布的通知
            position = getIntent().getIntExtra("position", 0);
            OrgId = UserManager.getInstance().getOrgId();
        }
        OrgNotify(true);
    }

    @OnClick({R.id.rl_back_button, R.id.iv_add, R.id.tv_my_relase, R.id.tv_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                setResult(RESULT_OK, getIntent().putExtras(bundle));
                finish();
                break;
            case R.id.iv_add:
                if (!UserManager.getInstance().isTrueRole("tz_1")) {
                    UserManager.getInstance().showSuccessDialog(this
                            , getString(R.string.text_role));
                } else {
                    startActivity(new Intent(this, NewNotifyActivity.class)
                            .putExtra("OrgId", OrgId));
                    overridePendingTransition(R.anim.window_out, R.anim.window_back);//底部弹出动画
                }
                break;
            case R.id.tv_my_relase:
                pageIndex = 1;
                type = "1";
                tvMyRelase.setBackgroundResource(R.mipmap.ic_blue_levae);
                tvMyRelase.setTextColor(ResourcesUtils.getColor(R.color.white));
                tvAll.setBackground(null);
                tvAll.setTextColor(ResourcesUtils.getColor(R.color.blueLight));
                OrgNotify(true);
                break;
            case R.id.tv_all:
                pageIndex = 1;
                type = "3";
                tvMyRelase.setBackground(null);
                tvMyRelase.setTextColor(ResourcesUtils.getColor(R.color.blueLight));
                tvAll.setBackgroundResource(R.mipmap.ic_blue_levae);
                tvAll.setTextColor(ResourcesUtils.getColor(R.color.white));
                OrgNotify(true);
                break;
        }
    }

    private void OrgNotify(boolean isClear) {
        HttpManager.getInstance().doGetNoticeList("OrgNotifyActivity",
                OrgId, type, pageIndex, new HttpCallBack<BaseDataModel<OrgNotifyEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<OrgNotifyEntity> data) {
                        if (isClear) {
                            if (data.getData().size() == 0) {
                                rlYesData.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                recyclerView.setVisibility(View.VISIBLE);
                                rlYesData.setVisibility(View.GONE);
                            }
                            orgNotifyList.clear();
                        }
                        orgNotifyList.addAll(data.getData());
                        notifyAdapter.setData(orgNotifyList, StrUtils.isNotEmpty(msgEntity) ? msgEntity.getType() : "");
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("OrgNotifyActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(OrgNotifyActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("OrgNotifyActivity onError", throwable.getMessage());
                    }
                });
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadmore();
    }

    @Override
    public void setOnItemClickListener(View view, int postion) {
        if (StrUtils.isNotEmpty(msgEntity) && msgEntity.getType().equals("15")) {// 15：|83：我的机构通知
            startActivity(new Intent(this, NotifityInfoActivity.class)
                    .putExtra("msgEntity", msgEntity)
                    .putExtra("notifyEntity", orgNotifyList.get(postion)));
        } else {
            startActivity(new Intent(this, NotifyObjActivity.class)
                    .putExtra("notifyEntity", orgNotifyList.get(postion)));
        }
        overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        if (event.getWhat() == ToUIEvent.REFERSH_NOTIFY) {
            pageIndex = 1;
            OrgNotify(true);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) {
        if ((paramKeyEvent.getAction() == 0)
                && (paramKeyEvent.getKeyCode() == 4)) {

            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            setResult(RESULT_OK, getIntent().putExtras(bundle));
            finish();
        }
        return super.dispatchKeyEvent(paramKeyEvent);
    }
}
