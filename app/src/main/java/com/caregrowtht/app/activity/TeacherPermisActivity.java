package com.caregrowtht.app.activity;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.TeacherPermisAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-10-22 14:26:13
 * 分配教师权限
 */
public class TeacherPermisActivity extends BaseActivity {

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private ArrayList<StudentEntity> identityEntity = new ArrayList<>();
    private StudentEntity auditEntity;
    private TeacherPermisAdapter permisAdapter;
    private String orgId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_teacher_permis;
    }

    @Override
    public void initView() {
        tvTitle.setText("分配权限");
        ivLeft.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_close_1));
        initRecyclerGrid(recyclerView, 3);
        permisAdapter = new TeacherPermisAdapter(identityEntity, this);
        recyclerView.setAdapter(permisAdapter);
    }

    @Override
    public void initData() {
        auditEntity = (StudentEntity) getIntent().getSerializableExtra("auditEntity");
        orgId = UserManager.getInstance().getOrgId();
        permisAdapter.powerId = auditEntity.getPowerId();// 上次选中的教师身份
        getIdentity();
    }

    private void getIdentity() {
        // 49.获取机构的所有身份
        HttpManager.getInstance().doGetIdentity("TeacherPermisActivity",
                orgId, new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        identityEntity.clear();
                        identityEntity.addAll(data.getData());
                        permisAdapter.setData(identityEntity, auditEntity);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("TeacherPermisActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(TeacherPermisActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("TeacherPermisActivity onError", throwable.getMessage());
                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.btn_cancel, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
            case R.id.btn_cancel:
                finish();
                overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
                break;
            case R.id.btn_submit:
                editRole();
                break;
        }
    }

    private void editRole() {
        // 51.编辑教师权限
        HttpManager.getInstance().doEditRole("TeacherPermisActivity",
                orgId, auditEntity.getUserId(), permisAdapter.powerId,
                new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_ACTIVE_TEACH));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("TeacherPermisActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(TeacherPermisActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("TeacherPermisActivity onError", throwable.getMessage());
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
