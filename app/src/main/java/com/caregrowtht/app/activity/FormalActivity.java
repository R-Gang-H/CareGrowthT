package com.caregrowtht.app.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.FormalAdapter;
import com.caregrowtht.app.adapter.FormalTheActiveAdapter;
import com.caregrowtht.app.model.BaseBeanModel;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.LoadingFrameView;
import com.caregrowtht.app.view.xrecyclerview.SpaceItemDecoration;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-8-7 16:18:53
 * 正式学员
 */
public class FormalActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.rl_back_button)
    ImageView rlBackButton;
    @BindView(R.id.iv_add)
    ImageView ivAdd;
    @BindView(R.id.tv_audit)
    TextView tvAudit;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.tv_ongoing)
    TextView tvOngoing;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    @BindView(R.id.tv_no_active)
    TextView tvNoActive;
    @BindView(R.id.tv_ongoing_line)
    TextView tvOngoingLine;
    @BindView(R.id.tv_complete_line)
    TextView tvCompleteLine;
    @BindView(R.id.tv_no_active_line)
    TextView tvNoActiveLine;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.xrv_student)
    RecyclerView xrvStudent;
    @BindView(R.id.rv_student)
    RecyclerView rvStudent;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    FormalAdapter mFormalAdapter;
    FormalTheActiveAdapter mFormalsAdapter;

    private int position;
    String text = "当前活跃学员总数:", status = "2";//1:标星学员 2：活跃学员 3：非活跃待确认 4：非活跃历史 5待审核
    private String OrgId;
    private MessageEntity msgEntity;
    private Boolean isClear = true;

    @Override
    public int getLayoutId() {
        return R.layout.activity_formal;
    }

    List<StudentEntity> mFormalList = new ArrayList<>();
    List<StudentEntity> mFormalList1 = new ArrayList<>();
    List<StudentEntity> mFormalList2 = new ArrayList<>();
    HashMap<String, List<StudentEntity>> studentList = new HashMap<>();// 学员信息
    List<String> positions = new ArrayList<>();// 当前坐标状态
    private ArrayList<StudentEntity> auditData = new ArrayList<>();

    @Override
    public void initView() {
        position = getIntent().getIntExtra("position", 0);

        GridLayoutManager manager = new GridLayoutManager(this, 4);
        manager.setOrientation(RecyclerView.VERTICAL);
        xrvStudent.setLayoutManager(manager);
        xrvStudent.addItemDecoration(new SpaceItemDecoration(5, SpaceItemDecoration.GRIDLAYOUT));

        mFormalAdapter = new FormalAdapter(mFormalList, this, this, null);
        xrvStudent.setAdapter(mFormalAdapter);
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            pageIndex = 1;
            isClear = true;
            initData();
        });
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnLoadmoreListener(refreshlayout -> {
            pageIndex++;
            isClear = false;
            initData();
        });
        refreshLayout.setEnableLoadmoreWhenContentNotFull(false);
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (msgEntity != null) {
            OrgId = msgEntity.getOrgId();
            UserManager.getInstance().setOrgId(OrgId);
        } else {
            OrgId = UserManager.getInstance().getOrgId(); //getIntent().getStringExtra("orgId");
        }
        initRecyclerView(rvStudent, true);
        mFormalsAdapter = new FormalTheActiveAdapter(mFormalList1, this);
        rvStudent.setAdapter(mFormalsAdapter);
        status = getIntent().getStringExtra("status");// 非活跃学员
        if (!TextUtils.isEmpty(status)) {
            if (status.equals("3") || status.equals("4")) {
                tvOngoingLine.setBackgroundColor(getResources().getColor(R.color.white));
                tvCompleteLine.setBackgroundColor(getResources().getColor(R.color.white));
                tvNoActiveLine.setBackgroundColor(getResources().getColor(R.color.blueLight));
                ivAdd.setVisibility(View.GONE);
                xrvStudent.setVisibility(View.GONE);
                rvStudent.setVisibility(View.VISIBLE);
                text = "当前非活跃学员总数:";
                pageIndex = 1;
            }
        } else {
            status = "2";
        }
    }

    @Override
    public void initData() {
        getAudit();//21.获取机构的待审核学员
        getStudent(isClear, status);//10.获取机构的正式学员
        getOrgChildNum();//29.获取机构的正式学员数量
    }

    private void getStudent(Boolean isClear, final String status) {
        boolean isRefresh = true, loadMore = true;// 默认可以刷新
        if (!TextUtils.isEmpty(status)) {
            if (status.equals("3") || status.equals("4")) { // 3：非活跃待确认 4：非活跃历史 不能刷新
                loadMore = false;
            }
        }
        refreshLayout.setEnableRefresh(isRefresh);
        refreshLayout.setEnableLoadmore(loadMore);

        loadView.setProgressShown(true);
        //10.获取机构的正式学员 haoruigang on 2018-8-7 15:50:55
        HttpManager.getInstance().doGetOrgChild("FormalActivity",
                OrgId, status, pageIndex + "", "",
                new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        mFormalAdapter.update(data.getData(), status);//每次重置数据
                        if (TextUtils.equals(status, "1") || TextUtils.equals(status, "2")) {
                            if (isClear) {
                                mFormalList.clear();
                            }
                            if (data.getData().size() > 0) {
                                mFormalList.addAll(data.getData());
                                loadView.delayShowContainer(true);
                            } else {
                                if (isClear) {
                                    loadView.setNoShown(true);
                                } else {
                                    loadView.delayShowContainer(true);
                                }
                            }
                            mFormalAdapter.update(mFormalList, status);
                        }
                        if (TextUtils.equals(status, "3") || TextUtils.equals(status, "4")) {
                            if (TextUtils.equals(status, "3")) {
//                                    if (isClear) {
                                mFormalList1.clear();
//                                    }
                                studentList.clear();// 清空数据
                                positions.clear();// 清空坐标
                                mFormalList1.addAll(data.getData());
                                if (mFormalList1.size() > 0) {
                                    studentList.put(status, mFormalList1);
                                    positions.add(status);
                                }
                                getStudent(true, "4");//10.获取机构的正式学员@TODO
                            } else if (TextUtils.equals(status, "4")) {
//                                    if (isClear) {
                                mFormalList2.clear();
//                                    }
                                mFormalList2.addAll(data.getData());
                                if (mFormalList2.size() > 0) {
                                    studentList.put(status, mFormalList2);
                                    positions.add(status);
                                }
                                mFormalsAdapter.update(studentList, positions);
                            }
                            if (mFormalList1.size() > 0 || mFormalList2.size() > 0) {
                                loadView.delayShowContainer(true);
                            } else {
                                if (isClear) {
                                    loadView.setNoShown(true);
                                } else {
                                    loadView.delayShowContainer(true);
                                }
                            }
                        }

                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("FormalActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(FormalActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.tag(" throwable " + throwable);
                    }
                });
    }

    private void getAudit() {
        //21.获取机构的待审核学员
        HttpManager.getInstance().doGetVerifyChild("FormalActivity",
                OrgId, new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        if (data != null && data.getData().size() > 0) {
                            auditData.clear();
                            auditData.addAll(data.getData());
                            tvAudit.setVisibility(View.VISIBLE);
                            tvAudit.setText(String.format("%s学员待审核", auditData.size()));
                        } else {
                            tvAudit.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("StudentDetailsActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(FormalActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("StudentDetailsActivity onFail", throwable.getMessage());
                    }
                });
    }

    public void getOrgChildNum() {
        //haoruigang on 2018-8-7 17:11:12 29.获取机构的正式学员数量
        HttpManager.getInstance().doGetOrgChildNum("FormalActivity",
                OrgId, status, new HttpCallBack<BaseDataModel<StudentEntity>>() {

                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        tvNumber.setText(
                                Html.fromHtml(text + "\t\t<font color='#69ACE5'>"
                                        + data.getData().get(0).getCount() + "人</font>"));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("StudentDetailsActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(FormalActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.tag(" throwable " + throwable);
                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.et_search, R.id.iv_add, R.id.tv_audit, R.id.tv_ongoing,
            R.id.tv_complete, R.id.tv_no_active})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                setResult(RESULT_OK, getIntent().putExtras(bundle));
                finish();
                break;
            case R.id.et_search:
                startActivity(new Intent(this, FormalSearchActivity.class));
                break;
            case R.id.iv_add:
                if (!UserManager.getInstance().isTrueRole("xy_1")) {
                    UserManager.getInstance().showSuccessDialog(this
                            , getString(R.string.text_role));
                    break;
                } else {
                    checkStudentNum();
                }
                break;
            case R.id.tv_audit:
                startActivity(new Intent(this, AuditActivity.class)
                        .putExtra("auditData", auditData)
                        .putExtra("type", "1"));//type 1: 添加学员 2: 添加教师
                break;
            case R.id.tv_ongoing:
                tvOngoingLine.setBackgroundColor(getResources().getColor(R.color.blueLight));
                tvCompleteLine.setBackgroundColor(getResources().getColor(R.color.white));
                tvNoActiveLine.setBackgroundColor(getResources().getColor(R.color.white));
                ivAdd.setVisibility(View.GONE);
                xrvStudent.setVisibility(View.VISIBLE);
                rvStudent.setVisibility(View.GONE);
                status = "1";
                text = "当前标星学员总数:";
                pageIndex = 1;
                getOrgChildNum();//29.获取机构的正式学员数量
                getStudent(true, status);//10.获取机构的正式学员
                break;
            case R.id.tv_complete:
                tvOngoingLine.setBackgroundColor(getResources().getColor(R.color.white));
                tvCompleteLine.setBackgroundColor(getResources().getColor(R.color.blueLight));
                tvNoActiveLine.setBackgroundColor(getResources().getColor(R.color.white));
                ivAdd.setVisibility(View.VISIBLE);
                xrvStudent.setVisibility(View.VISIBLE);
                rvStudent.setVisibility(View.GONE);
                status = "2";
                text = "当前活跃学员总数:";
                pageIndex = 1;
                getOrgChildNum();//29.获取机构的正式学员数量
                getStudent(true, status);//10.获取机构的正式学员
                break;
            case R.id.tv_no_active:
                tvOngoingLine.setBackgroundColor(getResources().getColor(R.color.white));
                tvCompleteLine.setBackgroundColor(getResources().getColor(R.color.white));
                tvNoActiveLine.setBackgroundColor(getResources().getColor(R.color.blueLight));
                ivAdd.setVisibility(View.GONE);
                xrvStudent.setVisibility(View.GONE);
                rvStudent.setVisibility(View.VISIBLE);
                status = "3";
                text = "当前非活跃学员总数:";
                pageIndex = 1;
                getOrgChildNum();//29.获取机构的正式学员数量
                getStudent(true, status);//10.获取机构的正式学员
                break;
        }
    }

    /**
     * 检查学员是否已达上限
     */
    private void checkStudentNum() {
        HttpManager.getInstance().doCheckStudentNum("FormalActivity", OrgId, new HttpCallBack<BaseBeanModel>() {
            @Override
            public void onSuccess(BaseBeanModel data) {
                //@TODO 暂时隐藏弹出
//                startActivity(new Intent(this, AddStuActivity.class)
//                        .putExtra("type", "1"));//type 1: 添加学员 2: 添加教师
//                overridePendingTransition(R.anim.window_out, R.anim.window_back);//底部弹出动画
                UserManager.getInstance().getCardStuList().clear();// 清空上次数据
                startActivity(new Intent(FormalActivity.this, AddFormalStuActivity.class)
                        .putExtra("msgEntity", msgEntity));
            }

            @Override
            public void onFail(int statusCode, String errorMsg) {
                if (statusCode == 1070) {
                    UserManager.getInstance().showSuccessDialog(FormalActivity.this
                            , getString(R.string.version_limit));
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }
        });
    }

    @Override
    public void setOnItemClickListener(View view, int postion) {
        if (TextUtils.equals(status, "1") || TextUtils.equals(status, "2")) {
            startActivity(new Intent(this, StudentDetailsActivity.class)
                    .putExtra("StudentEntity", mFormalList.get(postion))
                    .putExtra("status", status));
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

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        if (event.getWhat() == ToUIEvent.REFERSH_ACTIVE_STU) {
            initData();
        }
    }

}
