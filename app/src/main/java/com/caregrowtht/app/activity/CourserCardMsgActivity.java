package com.caregrowtht.app.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.NewCardsAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.LoadingFrameView;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-10-26 14:41:36
 * 课时卡管理
 */
public class CourserCardMsgActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_activity_line)
    TextView tvActivityLine;
    @BindView(R.id.tv_no_activity_line)
    TextView tvNoActivityLine;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.iv_emtity)
    ImageView ivEmtity;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    List<CourseEntity> mListCard = new ArrayList<>();
    NewCardsAdapter mCardsAdapter;
    private String status = "1";// 课程卡的状态 1：活跃 2：非活跃
    private String orgId;
    private MessageEntity msgEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_courser_card_msg;
    }

    @Override
    public void initView() {
        tvTitle.setText("课时卡管理");
        //列表
        initRecyclerView(recyclerView, true);
        mCardsAdapter = new NewCardsAdapter(mListCard, this, this, "4", "");//展示页面 1:选择购买新卡 2:新建课时卡种类 3:学员课时卡 4:课时卡管理
        recyclerView.setAdapter(mCardsAdapter);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(20));
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            pageIndex = 1;
            getOrgExistCard(status);//31.获取机构现有的课时卡
        });
        refreshLayout.setEnableLoadmoreWhenContentNotFull(false);
    }

    class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mSpacing;

        ItemOffsetDecoration(int itemOffset) {
            mSpacing = itemOffset;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildLayoutPosition(view) == 0) {//第一个的上外边距
                outRect.set(0, mSpacing, 0, 0);
            }
        }
    }

    @Override
    public void initData() {
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (msgEntity != null) {
            orgId = msgEntity.getOrgId();
        } else {
            orgId = UserManager.getInstance().getOrgId();
        }
        getOrgExistCard(status);//31.获取机构现有的课时卡

    }

    @OnClick({R.id.rl_back_button, R.id.tv_active, R.id.tv_no_active, R.id.iv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_active:
                tvActivityLine.setBackgroundColor(getResources().getColor(R.color.blueLight));
                tvNoActivityLine.setBackgroundColor(getResources().getColor(R.color.white));
                ivAdd.setVisibility(View.VISIBLE);
                status = "1";
                ivEmtity.setVisibility(View.VISIBLE);
                getOrgExistCard(status);//31.获取机构现有的课时卡
                break;
            case R.id.tv_no_active:
                tvActivityLine.setBackgroundColor(getResources().getColor(R.color.white));
                tvNoActivityLine.setBackgroundColor(getResources().getColor(R.color.blueLight));
                ivAdd.setVisibility(View.GONE);
                status = "2";
                ivEmtity.setVisibility(View.GONE);
                getOrgExistCard(status);//31.获取机构现有的课时卡
                break;
            case R.id.iv_add:
                if (!UserManager.getInstance().isTrueRole("ksk_1")) {
                    U.showToast(getString(R.string.text_role));
                    break;
                } else {
                    //新建课时卡
                    startActivity(new Intent(this, TimeCardNewActivity.class)
                            .putExtra("cardOperaType", "3"));//cardOperaType 1:选择购买新卡 2:选择课时卡 3:新建课时卡管理
                }
                break;
        }
    }

    /**
     * 31.获取机构现有的课时卡
     * haoruigang on 2018-8-17 17:53:42
     *
     * @param status 课程卡的状态 1：活跃 2：非活跃
     */
    private void getOrgExistCard(String status) {
        loadView.setProgressShown(true);
        HttpManager.getInstance().doGetOrgCard("CourserCardMsgActivity", orgId, status,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        mListCard.clear();
                        mListCard.addAll(data.getData());
                        mCardsAdapter.update(mListCard, status);
                        if (data.getData().size() > 0) {
                            loadView.delayShowContainer(true);
                        } else {
                            loadView.setNoShown(true);
                        }
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CourserCardMsgActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourserCardMsgActivity.this);
                        } else {
                            U.showToast(errorMsg);
                            loadView.setErrorShown(true, v -> getOrgExistCard(status));
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.tag(" throwable " + throwable);
                        loadView.setErrorShown(true, v -> getOrgExistCard(status));
                    }
                });
    }

    @Override
    public void setOnItemClickListener(View view, int postion) {
        if (status.equals("2")) {// 2：非活跃
            showEditDialog(mListCard.get(postion));
        }
    }

    /**
     * 课时卡管理操作弹窗
     *
     * @param entity isUsed:1：使用过 2：没有使用过
     */
    private void showEditDialog(final CourseEntity entity) {
        String orgId = UserManager.getInstance().getOrgId();
        String orgCardId = entity.getOrgCardId();
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_sign_on, null);
        final TextView tvEditCard = view.findViewById(R.id.tv_org_sign);
        tvEditCard.setVisibility(View.GONE);
        final TextView tvDelActCard = view.findViewById(R.id.tv_stu_sign);
        tvDelActCard.setText("置为活跃");
        tvDelActCard.setOnClickListener(v -> {
            mCardsAdapter.cardSetInactivity(orgId, orgCardId, "1");
            dialog.dismiss();
        });
        final TextView tvCancel = view.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
        //设置弹窗在底部
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        if (event.getWhat() == ToUIEvent.REFERSH_NEWCARD || event.getWhat() == ToUIEvent.REFERSH_STUDENT) {
            getOrgExistCard(status);//31.获取机构现有的课时卡
        }
    }

}
