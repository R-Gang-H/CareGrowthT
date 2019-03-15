package com.caregrowtht.app.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.TeacherPermisAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-10-22 17:41:50
 * 添加教师
 */
public class AddTeacherActivity extends BaseActivity {

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_tea_name)
    EditText etTeaName;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.et_phone_num)
    EditText etPhoneNum;
    @BindView(R.id.et_again_phone_num)
    EditText etAgainPhoneNum;
    @BindView(R.id.cl_basic_info)
    ConstraintLayout clBasicInfo;
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
    private String teacherId;
    private MessageEntity msgEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_teacher_permis;
    }

    @Override
    public void initView() {
        clBasicInfo.setVisibility(View.VISIBLE);
        ivLeft.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_close_1));
        initRecyclerGrid(recyclerView, 3);
        permisAdapter = new TeacherPermisAdapter(identityEntity, this);
        recyclerView.setAdapter(permisAdapter);
    }

    @Override
    public void initData() {
        auditEntity = (StudentEntity) getIntent().getSerializableExtra("auditEntity");
        if (auditEntity != null) {
            tvTitle.setText("编辑教师信息");
            teacherId = auditEntity.getUserId();
            permisAdapter.powerId = auditEntity.getPowerId();
            etTeaName.setText(auditEntity.getUserName());
            tvNickname.setText(String.format("%s老师", auditEntity.getUserName().substring(0, 1)));
            etPhoneNum.setText(auditEntity.getMobile());
            etAgainPhoneNum.setText(auditEntity.getMobile());
        } else {
            tvTitle.setText("新增教师");
        }
        etTeaName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("beforeTextChanged", s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("onTextChanged", s.toString());
                String nickName = "";
                if (!TextUtils.isEmpty(s.toString())) {
                    nickName = String.format("%s老师", s.toString().substring(0, 1));
                }
                tvNickname.setText(nickName);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("afterTextChanged", s.toString());
            }
        });
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (msgEntity != null) {
            orgId = msgEntity.getOrgId();
        } else {
            orgId = UserManager.getInstance().getOrgId();
        }
        getIdentity();
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
                orgAddTeacher();
                break;
        }
    }

    private void orgAddTeacher() {
        // 50. 新增教师 / 编辑教师信息
        String teacherName = etTeaName.getText().toString().trim();
        if (TextUtils.isEmpty(teacherName)) {
            U.showToast("请输入教师姓名");
            return;
        }
        String phoneNumber = etPhoneNum.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber)) {
            U.showToast("请输入手机号");
            return;
        }
        String againPhoneNumber = etAgainPhoneNum.getText().toString().trim();
        if (!TextUtils.equals(phoneNumber, againPhoneNumber)) {
            U.showToast("两次输入的手机号不相同");
            return;
        }
        String powerId = permisAdapter.powerId;
        if (TextUtils.isEmpty(powerId)) {
            U.showToast("请选择教师权限");
            return;
        }
        String teacherNickname = tvNickname.getText().toString().trim();
        HttpManager.getInstance().doOrgAddTeacher("AddTeacherActivity", orgId, teacherId, teacherName,
                teacherNickname, phoneNumber, powerId,
                new HttpCallBack<BaseDataModel<StudentEntity>>(this, true) {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        U.showToast("成功");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_ACTIVE_TEACH));
                        startActivity(new Intent(AddTeacherActivity.this, InviteTeacherActivity.class)
                                .putExtra("msgEntity", msgEntity));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("AddTeacherActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(AddTeacherActivity.this);
                        } else if (statusCode == 1054) {
                            U.showToast("手机号码已经存在");
                        } else if (statusCode == 1060) {
                            U.showToast("电话号码和名字不匹配");
                        } else if (statusCode == 1061) {
                            U.showToast("用户已存在");
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("AddTeacherActivity throwable", throwable.getMessage());
                    }
                });
    }

    private void getIdentity() {
        // 49.获取机构的所有身份
        HttpManager.getInstance().doGetIdentity("AddTeacherActivity",
                orgId, new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        identityEntity.clear();
                        identityEntity.addAll(data.getData());
                        permisAdapter.setData(identityEntity, auditEntity);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("AddTeacherActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(AddTeacherActivity.this);
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
