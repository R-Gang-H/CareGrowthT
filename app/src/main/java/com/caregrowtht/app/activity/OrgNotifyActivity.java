package com.caregrowtht.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.OrgNotifyAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.OrgNotifyEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-7-27 14:30:27
 * 机构通知
 */
public class OrgNotifyActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_title)
    TextView tvTitle;
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

    @Override
    public int getLayoutId() {
        return R.layout.activity_org_notify;
    }

    @Override
    public void initView() {
        tvTitle.setText("机构通知");
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
        position = getIntent().getIntExtra("position", 0);
        OrgId = UserManager.getInstance().getOrgId();
        OrgNotify(true);
    }

    @OnClick({R.id.rl_back_button, R.id.iv_add})
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
        }
    }

    private void OrgNotify(boolean isClear) {
        HttpManager.getInstance().doGetNoticeList("OrgNotifyActivity",
                OrgId, pageIndex, new HttpCallBack<BaseDataModel<OrgNotifyEntity>>() {
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
                        notifyAdapter.setData(orgNotifyList, isClear);
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
        startActivity(new Intent(this, NotifyObjActivity.class)
                .putExtra("notifyEntity", orgNotifyList.get(postion)));
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
