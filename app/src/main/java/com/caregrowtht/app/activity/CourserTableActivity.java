package com.caregrowtht.app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
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
import com.caregrowtht.app.user.UserManager;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-7-6 11:18:40
 * 点击课表放大
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CourserTableActivity extends BaseActivity {

    @BindView(R.id.rl_card_front)
    ConstraintLayout rlCardFront;
    @BindView(R.id.check_course)
    ImageView ivStatus;
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
    @BindView(R.id.tv_org_name)
    TextView tvOrgName;
    @BindView(R.id.btn_update)
    Button btnUpdate;
    @BindView(R.id.btn_add_course)
    Button btnAddCourse;
    @BindView(R.id.btn_delete)
    Button btnDelete;
    @BindView(R.id.cv_bg)
    CardView cvBg;

    private CourseEntity userModel;//上页值
    private CourseEntity<String, String, String> courseData;//


    @Override
    public int getLayoutId() {
        return R.layout.activity_courser_table;
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
        userModel = (CourseEntity) getIntent().getSerializableExtra("userModel");

        int statusBg = Color.parseColor(userModel.getColor());
        int opertBg = Color.parseColor(userModel.getTintColor());
        int cardFront = Color.parseColor(userModel.getColor3());

        rlCardFront.setBackgroundColor(cardFront);
        ivStatus.setBackgroundColor(statusBg);
        tvOrgName.setTextColor(statusBg);
        cvBg.setCardBackgroundColor(statusBg);
        btnUpdate.setBackgroundColor(statusBg);
//        btnUpdate.setTextColor(opertBg);
        btnAddCourse.setBackgroundColor(statusBg);
//        btnAddCourse.setTextColor(opertBg);
        btnDelete.setBackgroundColor(statusBg);
//        btnDelete.setTextColor(opertBg);

        teacherLessonDetail();

    }

    private void teacherLessonDetail() {
        //haoruigang on 2018-7-6 17:23:39 4. 获取课程的基本信息
        HttpManager.getInstance().doTeacherLessonDetail("CourserTableActivity"
                , userModel.getCourseId(), new HttpCallBack<BaseDataModel<CourseEntity<String, String, String>>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity<String, String, String>> data) {
                        courseData = data.getData().get(0);
                        tvCourseName.setText(courseData.getCourseName());
                        long startTime = Long.parseLong(courseData.getStartAt());
                        long endTime = Long.parseLong(courseData.getEndAt());
                        tvCourseTime.setText(String.format("%s-%s",
                                TimeUtils.getDateToString(startTime, "HH:mm"),
                                TimeUtils.getDateToString(endTime, "HH:mm")));
                        tvMainTeacher.setText(courseData.getMainTeacher());
                        String[] assistant = courseData.getAssistant().split(",");
                        tvAssistant.setText(String.format("助教:\t%s", assistant.length > 1 ? assistant[0] + "..." : assistant[0]));
                        String stuName = courseData.getStuName();
                        tvStudentCount.setText(String.format("学员%s%s人",
                                !TextUtils.isEmpty(stuName) ? stuName + "..等" : ""
                                , courseData.getStudentCount()));
                        tvSignLeave.setText(String.format("签到%s人\t请假%s人", courseData.getSign(), courseData.getLeave()));
                        tvOrgName.setText(courseData.getOrgName());
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("AddOrgActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourserTableActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("AddOrgActivity Throwable", throwable.getMessage());
                    }
                });
    }

    @OnClick({R.id.ll_course_table, R.id.tv_out_in, R.id.btn_update, R.id.btn_add_course, R.id.btn_delete})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.ll_course_table:
                supportFinishAfterTransition();
                break;
            case R.id.tv_out_in:
                if (courseData != null) {
                    startActivity(new Intent(this, CourserActivity.class)
                            .putExtra("courseId", courseData.getCourseId()));
                    finishAfterTransition();
                }
                break;
            case R.id.btn_update:
                if (!UserManager.getInstance().isTrueRole("zy_2")) {
                    UserManager.getInstance().showSuccessDialog(this
                            , getString(R.string.text_role));
                    break;
                } else {
                    if (getCourseEnd()) break;
                    showUpdateDialog(1);
                }

                break;
            case R.id.btn_add_course:
                if (!UserManager.getInstance().isTrueRole("zy_2")) {
                    UserManager.getInstance().showSuccessDialog(this
                            , getString(R.string.text_role));
                    break;
                } else {
                    startActivity(new Intent(this, AddCourseActivity.class)
                            .putExtra("courseData", courseData));
                    finishAfterTransition();
                }
                break;
            case R.id.btn_delete:
                if (!UserManager.getInstance().isTrueRole("zy_2")) {
                    UserManager.getInstance().showSuccessDialog(this
                            , getString(R.string.text_role));
                    break;
                } else {
                    if (getCourseEnd()) break;
                    showUpdateDialog(2);
                }
                break;
        }
    }

    private boolean getCourseEnd() {
        long endTime = Long.parseLong(userModel.getEndAt());
        long nowTime = System.currentTimeMillis() / 1000;
        boolean isToday = TimeUtils.IsToday(DateUtil.getDate(endTime, "yyyy-MM-dd"));// 是否为今天
        if (!isToday) {
            if (nowTime > endTime) {
                //Course end.
                U.showToast("抱歉,这节课早已结束,您不能再改了");
                return true;
            }
        }
        return false;
    }

    /**
     * 修改/删除 弹窗
     *
     * @param i 1:修改 2:删除
     */
    private void showUpdateDialog(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_sign_on, null);
        final TextView tvOrgSign = view.findViewById(R.id.tv_org_sign);
        if (i == 1) {
            tvOrgSign.setText("修改这节课");
        } else {
            tvOrgSign.setText("删除这节课");
        }
        tvOrgSign.setOnClickListener(v -> {
            if (i == 1) {
                startActivity(new Intent(this, MadifyCourseActivity.class)
                        .putExtra("courseData", courseData));
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
                finishAfterTransition();
            } else {
                showDeleteDialog("1");
            }
            dialog.dismiss();
        });
        final TextView tvStuSign = view.findViewById(R.id.tv_stu_sign);
        if (i == 1) {
            tvStuSign.setText("修改今后所有排课");
        } else {
            tvStuSign.setText("删除今后所有排课");
        }
        tvStuSign.setOnClickListener(v -> {
            if (i == 1) {
                startActivity(new Intent(this, NewWorkActivity.class)
                        .putExtra("courseData", courseData)
                        .putExtra("updateAll", true));
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
                finishAfterTransition();
            } else {
                showDeleteDialog("2");
            }
            dialog.dismiss();
        });
        final TextView tvCancel = view.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
        //设置弹窗在底部
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
    }

    // 退出弹框
    private void showDeleteDialog(String type) {
        new AlertDialog.Builder(this)
                .setTitle("是否删除？")
                .setPositiveButton("确定", (d, which) -> {
                    deleteCourseLesson(type);
                })
                .setNegativeButton("取消", (d, which) -> d.dismiss())
                .create().show();
    }

    private void deleteCourseLesson(String type) {//1:删除一节课 2：删除今后所有同名字的排课
        String orgId = UserManager.getInstance().getOrgId();
        //45.删除排课
        HttpManager.getInstance().doDeleteCourseLesson("CourserTableActivity", type,
                courseData.getCourseId(), orgId,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.TEACHER_REFERSH, false));
                        U.showToast("已删除");
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NewWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourserTableActivity.this);
                        } else if (statusCode == 1067) {// 课程已结束， 不可以删除
                            U.showToast("课程已结束， 不可以删除!");
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("NewWorkActivity onFail", throwable.getMessage());
                    }
                });
    }


}
