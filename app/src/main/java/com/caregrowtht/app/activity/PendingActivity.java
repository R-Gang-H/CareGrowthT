package com.caregrowtht.app.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.PendingAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.PendingEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * haoruigang on 2018-7-11 14:45:56
 * 学员批量签到
 */
public class PendingActivity extends BaseActivity {

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    ArrayList<StudentEntity> studentList = new ArrayList<>();
    private String courseId;
    private PendingAdapter pendingAdapter;
    private String sign;
    private String leave;
    private String orgId;
    private CourseEntity courseData;

    @Override
    public int getLayoutId() {
        return R.layout.activity_pending;
    }

    @Override
    public void initView() {
        ivLeft.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_close_1));
        tvTitle.setText("确认出勤情况");
        initRecyclerView(recyclerView, true);
    }

    @Override
    public void initData() {
        orgId = UserManager.getInstance().getOrgId();
        courseId = getIntent().getStringExtra("courseId");
        courseData = (CourseEntity) getIntent().getSerializableExtra("courseData");
        studentList = (ArrayList<StudentEntity>) getIntent().getSerializableExtra("studentList");
        pendingAdapter = new PendingAdapter(studentList, this);
        recyclerView.setAdapter(pendingAdapter);
        pendingAdapter.setData(studentList, courseData);
        if (courseData != null) {
            Log.d("PendingAdapter", courseData.getEndAt());
            Long endAt = Long.parseLong(courseData.getEndAt());// 课程时间
            Long today = System.currentTimeMillis() / 1000;//今天的时间戳
            Long moday = DateUtil.getStringToDate(
                    TimeUtils.dateTiem(
                            DateUtil.getDate(today, "yyyy-MM-dd")
                            , 1, "yyyy-MM-dd"), "yyyy-MM-dd");// 明天的时间戳
            if (moday > endAt) {// 只能签今天或者今天以前的课
                rlNextButton.setVisibility(View.VISIBLE);
                tvTitleRight.setBackgroundResource(R.mipmap.ic_cours_fb);
                tvTitleRight.setText("全部签到");
            }
        }
    }

    @OnClick({R.id.rl_back_button, R.id.rl_next_button, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
                break;
            case R.id.rl_next_button:
                pendingAdapter.initBatchSign();
                break;
            case R.id.btn_submit:
                if (!UserManager.getInstance().isTrueRole("zy_3")) {
                    UserManager.getInstance().showSuccessDialog(this
                            , getString(R.string.text_role));
                    break;
                } else {
                    btnSubmit.setFocusable(false);
                    lessonSignLeave();
                }
                break;
        }
    }

    private void lessonSignLeave() {
        ArrayList<PendingEntity> signList = new ArrayList<>();
        ArrayList<PendingEntity> levaeList = new ArrayList<>();
        for (int i = 0; i < studentList.size(); i++) {
            if (pendingAdapter.getIsSign().get(i)) {
                PendingEntity signEntity = new PendingEntity();
                signEntity.setStuId(pendingAdapter.getIsStudent().get(i).getStuId());
                signEntity.setBz(pendingAdapter.getIsSignBz().get(i));
                signEntity.setStudentType(pendingAdapter.getIsStudent().get(i).getStudentType());
                signEntity.setOptionType("1");
                signList.add(signEntity);
            }
            if (pendingAdapter.getIsLevan().get(i)) {
                PendingEntity levaeEntity = new PendingEntity();
                levaeEntity.setStuId(pendingAdapter.getIsStudent().get(i).getStuId());
                levaeEntity.setBz(pendingAdapter.getIsLevaeBz().get(i));
                levaeEntity.setStudentType(pendingAdapter.getIsStudent().get(i).getStudentType());
                levaeEntity.setOptionType("2");
                levaeList.add(levaeEntity);
            }
        }

        sign = new Gson().toJson(signList);
        leave = new Gson().toJson(levaeList);

        //haoruigang on 2018-7-16 17:57:49 6. 批量的为某节课的学员请假或者签到
        HttpManager.getInstance().doLessonSignLeave("PendingActivity",
                courseId, sign, leave, orgId, new HttpCallBack<BaseDataModel<StudentEntity>>(
                        this, true) {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        btnSubmit.setFocusable(true);
                        U.showToast("批量签到成功!");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.SIGN_SUCCESS));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        btnSubmit.setFocusable(true);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(PendingActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("PendingActivity Throwable", throwable.getMessage());
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
