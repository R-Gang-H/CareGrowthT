package com.caregrowtht.app.activity;

import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.model.UserEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 学员预约情况
 */
public class StuOrderActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_course_task)
    TextView tvCourseTask;
    @BindView(R.id.tv_students)
    TextView tvStudents;

    private CourseEntity courseData;
    private String courseId;
    List<StudentEntity> studentList = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_stu_order;
    }

    @Override
    public void initView() {
        tvTitle.setText("学员预约详情");
    }

    @Override
    public void initData() {
        MessageEntity msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        courseId = msgEntity.getTargetId();
        teacherLessonDetail();
    }

    private void teacherLessonDetail() {
        //haoruigang on 2018-7-6 17:23:39 4. 获取课程的详细信息
        HttpManager.getInstance().doCourseLessonDetails("StuOrderActivity", courseId,
                new HttpCallBack<BaseModel<CourseEntity<List<UserEntity>, List<UserEntity>, List<UserEntity>>>>() {

                    @Override
                    public void onSuccess(BaseModel<CourseEntity<List<UserEntity>, List<UserEntity>, List<UserEntity>>> data) {
                        courseData = data.getData();
                        setLessonDetail();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("StuOrderActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(StuOrderActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("StuOrderActivity Throwable", throwable.getMessage());
                    }
                });
    }

    private void setLessonDetail() {
        long startTime = Long.parseLong(courseData.getStartAt());
        long endTime = Long.parseLong(courseData.getEndAt());
        String data = TimeUtils.getDateToString(startTime, "yyyy年MM月dd日");
        String week = TimeUtils.getWeekByDateStr(data);//获取周几
        List<UserEntity> studentList = (List<UserEntity>) courseData.getStudents();
        tvCourseTask.setText(String.format("%s\t%s\n%s\n上课学员%s名", data, week,
                courseData.getCourseName(), studentList.size()));
        StringBuilder sbChild = new StringBuilder();
        for (int i = 1; i < studentList.size() + 1; i++) {
            if ((i % 3) == 0) {
                sbChild.append("\n");
            }
            UserEntity userEntity = studentList.get(i - 1);
            sbChild.append(Html.fromHtml(userEntity.getUserName() +
                    "<font color='#999999'>(" + userEntity.getNickname() + ");</font>\t"));
        }
        tvStudents.setText(sbChild.toString());
    }


    @OnClick(R.id.rl_back_button)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }
}
