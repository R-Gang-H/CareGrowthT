package com.caregrowtht.app.activity;

import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.OfficialAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-10-22 11:24:56
 * 教师管理
 */
public class TeacherMsgActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.tv_title_audit)
    TextView tvTitleAudit;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private MessageEntity msgEntity;
    private String orgId;

    ArrayList<StudentEntity> officialData = new ArrayList<>();//正式教师
    ArrayList<StudentEntity> auditData = new ArrayList<>();//待审核教师
    private OfficialAdapter officialAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_teacher_msg;
    }

    @Override
    public void initView() {
        tvTitle.setText("教师管理");

        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            initData();
        });

        initRecyclerView(recyclerView, true);
        officialAdapter = new OfficialAdapter(officialData, this);
        recyclerView.setAdapter(officialAdapter);
    }

    @Override
    public void initData() {
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (msgEntity != null) {
            orgId = msgEntity.getOrgId();
        } else {
            orgId = UserManager.getInstance().getOrgId();
        }
        getOrgTeachers("1");// 1：正式 2：待审核
    }

    /**
     * 48.获取机构的教师
     *
     * @param status 教师的状态 1：正式 2：待审核
     */
    private void getOrgTeachers(String status) {
        HttpManager.getInstance().doGetOrgTeachers("TeacherMsgActivity",
                orgId, status, "1", new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        if (TextUtils.equals("1", status)) {//正式
                            officialData.clear();
                            officialData.addAll(data.getData());
                            officialAdapter.setData(officialData);
                            //成功后加载待审核教师
                            getOrgTeachers("2");// 1：正式 2：待审核
                        } else if (TextUtils.equals("2", status)) {//待审核
                            auditData.clear();
                            auditData.addAll(data.getData());
                            if (data.getData().size() > 0) {
                                tvTitleAudit.setVisibility(View.VISIBLE);
                                tvTitleAudit.setText(String.format("%s位教师待审核", data.getData().size()));
                            } else {
                                tvTitleAudit.setVisibility(View.GONE);
                            }
                        }
                        refreshLayout.finishRefresh();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("TeacherMsgActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(TeacherMsgActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("TeacherMsgActivity throwable", throwable.getMessage());
                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.tv_title_audit, R.id.iv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_title_audit:
                startActivity(new Intent(this, AuditActivity.class)
                        .putExtra("auditData", auditData)
                        .putExtra("type", "2"));//type 1: 添加学员 2: 添加教师
                break;
            case R.id.iv_add:
                if (!UserManager.getInstance().isTrueRole("js_1")) {
                    UserManager.getInstance().showSuccessDialog(this
                            , getString(R.string.text_role));
                    break;
                } else {
                    //@TODO 暂时隐藏弹出
//                startActivity(new Intent(this, AddStuActivity.class)
//                        .putExtra("type", "2"));//type 1: 添加学员 2: 添加教师
//                overridePendingTransition(R.anim.window_out, R.anim.window_back);//底部弹出动画
                    startActivity(new Intent(this, AddTeacherActivity.class)
                            .putExtra("msgEntity", msgEntity)
                            .putExtra("type", "2"));
                }
                break;
        }
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        if (event.getWhat() == ToUIEvent.REFERSH_ACTIVE_TEACH) {
            initData();
        }
    }

}
