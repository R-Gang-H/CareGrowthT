package com.caregrowtht.app.activity;

import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.user.ToUIEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 课程补位
 */
public class CourserCoverActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_course_name)
    TextView tvCourseName;
    @BindView(R.id.tv_course_time)
    TextView tvCourseTime;
    @BindView(R.id.tv_main_teacher)
    TextView tvMainTeacher;
    @BindView(R.id.tv_assis_teacher)
    TextView tvAssisTeacher;
    @BindView(R.id.tv_student_count)
    TextView tvStudentCount;
    @BindView(R.id.tv_org_name)
    TextView tvOrgName;
    @BindView(R.id.tv_opening)
    TextView tvOpening;
    @BindView(R.id.tv_equi)
    TextView tvEqui;
    @BindView(R.id.ll_handle)
    LinearLayout llHandle;
    @BindView(R.id.btn_allot)
    Button btnAllot;
    @BindView(R.id.btn_stu_take)
    Button btnStuTake;

    private String courseId;
    private CourseEntity courInfoData = new CourseEntity();
    private CourseEntity courseEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_courser_cover;
    }

    @Override
    public void initView() {
        tvTitle.setText("课程补位");
    }

    @Override
    public void initData() {
        MessageEntity msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        courseEntity = (CourseEntity) getIntent().getSerializableExtra("courseEntity");
        if (StrUtils.isNotEmpty(msgEntity)) {
            courseId = msgEntity.getTargetId();
        } else if (StrUtils.isNotEmpty(courseEntity)) {
            courseId = courseEntity.getCourseId();
        }
        teacherLessonDetail();
    }

    private void teacherLessonDetail() {
        // 46. 获取课程的详细信息
        HttpManager.getInstance().doTeacherLessonDetail("CourserCoverActivity", courseId,
                new HttpCallBack<BaseDataModel<CourseEntity<String, String, String>>>() {

                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity<String, String, String>> data) {
                        courInfoData = data.getData().get(0);
                        getCourseData();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CourserCoverActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourserCoverActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CourserCoverActivity onError", throwable.getMessage());
                    }
                });
    }

    private void getCourseData() {
        CourseEntity<String, String, String> courseData = courInfoData;
        tvCourseName.setText(courseData.getCourseName());
        long startTime = Long.parseLong(courseData.getStartAt());
        long endTime = Long.parseLong(courseData.getEndAt());
        tvCourseTime.setText(String.format("%s-%s", TimeUtils.getDateToString(startTime, "MM月dd HH:mm"),
                TimeUtils.getDateToString(endTime, "HH:mm")));
        tvMainTeacher.setText(courseData.getMainTeacher());
        //助教
        String assistant = courseData.getAssistant();
        tvAssisTeacher.setText(String.format("助教:%s", TextUtils.isEmpty(assistant) ? "无" : assistant));
        tvStudentCount.setText(Html.fromHtml(String.format("学员\t%s...等%s人\t<font color='#999999'>签到%s人\t请假\t%s人</font>"
                , courseData.getStuName(), courseData.getStudentCount(), courseData.getSign(), courseData.getLeave())));
        tvOrgName.setText(courseData.getOrgName());
        tvOpening.setText(Html.fromHtml(String.format("空位:\t<font color='#69ace5'>%s</font>\t个", courseData.getKongwei())));
        tvEqui.setText(Html.fromHtml(String.format("等位学员:\t<font color='#69ace5'>%s</font>\t个", courseData.getWait())));
        ArrayList<CourseEntity<String, String, String>.Handle> handle = courseData.getHandle();
        if (handle.size() > 0) {// 有操作记录
            if (Integer.valueOf(courseData.getKongwei()) == 0) {// 没有空位按钮隐藏（覆盖）
                llHandle.removeAllViews();
            }
            setHandle(handle);
        }
    }

    private void setHandle(ArrayList<CourseEntity<String, String, String>.Handle> handle) {
        llHandle.setOrientation(LinearLayout.VERTICAL);
        llHandle.setGravity(Gravity.CENTER_HORIZONTAL);
        for (int i = 0; i < handle.size(); i++) {
            String handleName = handle.get(i).getHandleName();
            String handleType = handle.get(i).getHandleType();
            String handleTime = handle.get(i).getHandleTime();
            TextView textView = new TextView(this);
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setTextSize(15);
            textView.setTextColor(getResources().getColor(R.color.color_9));
            textView.setText(String.format("%s\t\t\t%s\t\t\t%s", handleName,
                    TimeUtils.getDateToString(Long.parseLong(handleTime), "MM-dd HH:mm")
                    , handleType));
            llHandle.addView(textView);
        }
    }

    @OnClick({R.id.rl_back_button, R.id.btn_allot, R.id.btn_stu_take})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.btn_allot:
                startActivity(new Intent(CourserCoverActivity.this,
                        AllotActivity.class).putExtra("courseId", courseId));
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
                break;
            case R.id.btn_stu_take:
                borCourse();
                break;
        }
    }

    private void borCourse() {
        // 71. 学员抢位
        HttpManager.getInstance().doBorCourse("CourserCoverActivity", courseId,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        U.showToast("抢位成功");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_TEACHER));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CourserCoverActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourserCoverActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CourserCoverActivity onError", throwable.getMessage());
                    }
                });
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        switch (event.getWhat()) {
            case ToUIEvent.REFERSH_ALLOT:
                initData();
                break;
        }
    }

}
