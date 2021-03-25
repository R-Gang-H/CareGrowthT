package com.caregrowtht.app.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GradientUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.user.ToUIEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-8-2 16:50:19
 * 选择排课班级成员
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SchoolWorkLeagueActivity extends BaseActivity {

    @BindView(R.id.tv_course_name)
    TextView tvCourseName;
    @BindView(R.id.tv_course_time)
    TextView tvCourseTime;
    @BindView(R.id.tv_mainTeacher)
    TextView tvMainTeacher;
    @BindView(R.id.tv_assistant)
    TextView tvAssistant;
    @BindView(R.id.tv_studentCount)
    TextView tvStudentCount;
    @BindView(R.id.tv_sign_leave)
    TextView tvSignLeave;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    private CourseEntity userModel;

    @Override
    public int getLayoutId() {
        return R.layout.activity_school_work_league;
    }

    @Override
    public void initView() {
        GradientUtils.setColor(this, R.drawable.mine_title_bg, true);
        //实现震动
        Vibrator vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(60);
    }

    @Override
    public void initData() {
        userModel = (CourseEntity) getIntent().getSerializableExtra("courseModel");
        getLessonHuman();
    }

    /**
     * 28.获取课程的全部老师和学员
     */
    private void getLessonHuman() {
        HttpManager.getInstance().doGetLessonHuman("SchoolWorkLeagueActivity", userModel.getCourseId(),
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        CourseEntity courseData = data.getData().get(0);
                        tvCourseName.setText(courseData.getCourseName());
                        long startTime = Long.parseLong(courseData.getStartAt());
                        long endTime = Long.parseLong(courseData.getEndAt());
                        tvCourseTime.setText(String.format("%s-%s",
                                TimeUtils.getDateToString(startTime, "MM月dd日 HH:mm"),
                                TimeUtils.getDateToString(endTime, "HH:mm")));
                        String[] teacherCount = courseData.getTeacherNames().split("#");
                        tvMainTeacher.setText(String.format("教师%s人", teacherCount.length > 0 &&
                                !TextUtils.isEmpty(teacherCount[0]) ? teacherCount.length : "0"));
                        String teacherNames = courseData.getTeacherNames().replace("#", "\t");
                        tvAssistant.setText(teacherNames);
                        String[] studentCount = courseData.getStudentNames().split("#");
                        tvStudentCount.setText(String.format("学员%s人", studentCount.length > 0 &&
                                !TextUtils.isEmpty(studentCount[0]) ? studentCount.length : "0"));
                        String studentNames = courseData.getStudentNames().replace("#", "\t");
                        tvSignLeave.setText(studentNames);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("SchoolWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(SchoolWorkLeagueActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("SchoolWorkActivity onFail", throwable.getMessage());
                    }
                });
    }


    @OnClick({R.id.btn_cancel, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                finishAfterTransition();
                break;
            case R.id.btn_confirm:
                EventBus.getDefault().post(new ToUIEvent(ToUIEvent.CLASS_NOTIFY, userModel));
                startActivity(new Intent(this, CustomActivity.class));
                finishAfterTransition();
                break;
        }
    }

}
