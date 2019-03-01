package com.caregrowtht.app.activity;

import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.TeacherAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.UserEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-8-23 14:38:33
 * 选择教师
 */
public class TeacherSelectActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    private String orgId;
    private String identity = "1";//身份 1：老师 2：学员

    private List<UserEntity> mContactModels;
    private TeacherAdapter mAdapter;
    private String teacherType;//教师类型 1：主讲教师 2：助教
    private List<UserEntity> mainTeachers = new ArrayList<>();//选择的教师信息
    private List<UserEntity> checkmainTeach = new ArrayList<>();//已选中的主讲教师
    private List<UserEntity> assistantTeachers = new ArrayList<>();//已选中的助教

    @Override
    public int getLayoutId() {
        return R.layout.activity_teacher_select;
    }

    @Override
    public void initView() {
        final TextView tvTitleRight = findViewById(R.id.tv_title_right);
        tvTitleRight.setText("全选");//教师的全选
        tvTitle.setText("选择教师");
        ivLeft.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_close_1));
        initRecyclerView(recyclerView, true);
        mContactModels = new ArrayList<>();
        mAdapter = new TeacherAdapter(mContactModels, this, this, null);
        recyclerView.setAdapter(mAdapter);
        tvTitleRight.setOnClickListener(v -> {
            tvTitleRight.setSelected(!tvTitleRight.isSelected());//改变状态
            tvTitleRight.setText(tvTitleRight.isSelected() ? "取消" : "全选");//教师的取消
            mAdapter.isAll = tvTitleRight.isSelected();
            mAdapter.initDate();
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void initData() {
        teacherType = getIntent().getStringExtra("teacherType");
        checkmainTeach.clear();
        checkmainTeach = (List<UserEntity>) getIntent().getSerializableExtra("mainTeachers");
        assistantTeachers = (List<UserEntity>) getIntent().getSerializableExtra("assistantTeachers");
        orgId = UserManager.getInstance().getOrgId();
        if (TextUtils.equals(teacherType, "2")) {
            rlNextButton.setVisibility(View.VISIBLE);
        }
        getNoticeHuman();
    }

    /**
     * 26.选择通知对象
     */
    private void getNoticeHuman() {
        HttpManager.getInstance().doGetNoticeHuman("TeacherSelectActivity",
                orgId, identity,
                new HttpCallBack<BaseDataModel<UserEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        mContactModels.clear();
                        mContactModels.addAll(data.getData());
                        mAdapter.setData(mContactModels, teacherType, checkmainTeach, assistantTeachers);//刷新数据
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("TeacherSelectActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(TeacherSelectActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
                break;
            case R.id.btn_confirm:
                mainTeachers.clear();
                Intent intent = new Intent();
                for (int i = 0; i < mAdapter.getIsUserEntity().size(); i++) {
                    if (mAdapter.getIsUserEntity().get(i) != null) {
                        mainTeachers.add(mAdapter.getIsUserEntity().get(i));
                    }
                }
//                if (mainTeachers.size() == 0) {
//                    U.showToast("请选择教师");
//                    break;
//                }
                intent.putExtra("UserEntity", (Serializable) mainTeachers);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
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
        final CheckBox tvSelectStu = view.findViewById(R.id.tv_select_stu);
        mAdapter.getSelect(postion, tvSelectStu);
    }
}
