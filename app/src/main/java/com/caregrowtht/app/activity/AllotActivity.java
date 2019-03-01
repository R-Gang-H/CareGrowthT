package com.caregrowtht.app.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.AllotAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.permissions.PermissionCallBackM;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 选择补位学员
 */
public class AllotActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.x_recycler_view)
    XRecyclerView xRecyclerView;
    @BindView(R.id.tv_select_stu)
    TextView tvSelectStu;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private String courseId;
    private List<StudentEntity> stuData = new ArrayList<>();
    private AllotAdapter allotAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_allot;
    }

    @Override
    public void initView() {
        tvTitle.setText("选择补位学员");
        courseId = getIntent().getStringExtra("courseId");
        ivLeft.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_close_1));
        iniXrecyclerView(xRecyclerView);
        allotAdapter = new AllotAdapter(stuData, this, this);
        xRecyclerView.setAdapter(allotAdapter);
        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageIndex = 0;
                lesWaitingStuV2(true);
            }

            @Override
            public void onLoadMore() {
                pageIndex++;
                lesWaitingStuV2(false);
            }
        });
    }

    @Override
    public void initData() {
        lesWaitingStuV2(true);
    }

    // 69. 获取某节课的等位学员
    private void lesWaitingStuV2(boolean isClear) {
        HttpManager.getInstance().doLesWaitingStuV2("AllotActivity",
                courseId, String.valueOf(pageIndex), pageSize,
                new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        if (isClear) {
                            stuData.clear();
                        }
                        stuData.addAll(data.getData());
                        allotAdapter.update(stuData, isClear);
                        xRecyclerView.loadMoreComplete();
                        xRecyclerView.refreshComplete();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("AllotActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(AllotActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("AllotActivity onError", throwable.getMessage());
                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
                break;
            case R.id.btn_submit:
                if (TextUtils.isEmpty(valadate())) {
                    U.showToast("请选择补位学员");
                    return;
                }
                allotWaitingLesV2();
                break;
        }
    }

    private String valadate() {
        StringBuilder stuIds = new StringBuilder();
        for (int i = 0; i < allotAdapter.getStu().size(); i++) {
            if (i > 0) {
                stuIds.append(",");
            }
            stuIds.append(allotAdapter.getStu().get(i));
        }
        return stuIds.toString();
    }

    //70. 给等位学员分配课程
    private void allotWaitingLesV2() {
        HttpManager.getInstance().doAllotWaitingLesV2("AllotActivity", courseId, valadate(),
                new HttpCallBack<CourseEntity>() {
                    @Override
                    public void onSuccess(CourseEntity data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_ALLOT));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("AllotActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(AllotActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("AllotActivity onError", throwable.getMessage());
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

    @Override
    public void setOnItemClickListener(View view, int postion) {
        int position = postion - 1;
        allotAdapter.getSelect(position,
                view.findViewById(R.id.cb_stu), tvSelectStu);
        view.findViewById(R.id.iv_call_phone).setOnClickListener(v -> {
            String stuPhone = stuData.get(position).getPhoneNumber();
            if (!StrUtils.isEmpty(stuPhone)) {
                getCallPhone(stuPhone);
            }
        });
    }

    //获取CALL_PHONE权限 haoruigang on 2018-4-3 15:29:46
    public void getCallPhone(String stuPhone) {
        requestPermission(
                Constant.RC_CALL_PHONE,
                new String[]{Manifest.permission.CALL_PHONE},
                getString(R.string.rationale_call_phone),
                new PermissionCallBackM() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionGrantedM(int requestCode, String... perms) {
                        LogUtils.e(AllotActivity.this, "TODO: CALL_PHONE Granted");
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + stuPhone)));
                    }

                    @Override
                    public void onPermissionDeniedM(int requestCode, String... perms) {
                        LogUtils.e(AllotActivity.this, "TODO: CALL_PHONE Denied");
                    }
                });
    }

}
