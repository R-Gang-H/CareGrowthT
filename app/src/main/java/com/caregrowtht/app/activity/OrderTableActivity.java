package com.caregrowtht.app.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.library.utils.U;
import com.caregrowtht.app.Constant;
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

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 预约课提醒弹窗
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class OrderTableActivity extends BaseActivity {

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
    @BindView(R.id.tv_select_course)
    CheckBox tvSelectCourse;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_complete)
    Button btnComplete;

    private CourseEntity courseModel;//上页值
    private String isOrder, orgId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_order_table;
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
        courseModel = (CourseEntity) getIntent().getSerializableExtra("courseModel");

        int statusBg = Color.parseColor(courseModel.getColor());
        int cardFront = Color.parseColor(courseModel.getColor3());

        rlCardFront.setBackgroundColor(cardFront);
        ivStatus.setBackgroundColor(statusBg);
        btnCancel.setBackgroundColor(statusBg);
        btnComplete.setBackgroundColor(statusBg);

        tvCourseName.setText(courseModel.getCourseName());
        long startTime = Long.parseLong(courseModel.getStartAt());
        long endTime = Long.parseLong(courseModel.getEndAt());

        String Month = TimeUtils.getDateToString(startTime, "MM月dd日");
        int dayOfWeek = TimeUtils.getDayOfWeek(startTime);// 获取周几
        String time = TimeUtils.getDateToString(startTime, "HH:mm");

        tvCourseTime.setText(String.format("%s\t%s\t%s-%s", Month, Constant.weekArr[dayOfWeek - 1], time,
                TimeUtils.getDateToString(endTime, "HH:mm")));
        tvMainTeacher.setText(courseModel.getTeacher());
        isOrder = courseModel.getIsOrder();//1：该节课可以预约 2：该节课不可以预约
        tvSelectCourse.setSelected(isOrder.equals("1"));
        tvSelectCourse.setChecked(isOrder.equals("1"));
        tvSelectCourse.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                isOrder = "2";// 2：该节课不可以预约
            } else {
                isOrder = "1";// 1：该节课可以预约
            }
            tvSelectCourse.setSelected(isChecked);
        });

        orgId = UserManager.getInstance().getOrgId();
    }

    @OnClick({R.id.btn_cancel, R.id.btn_complete})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                supportFinishAfterTransition();
                break;
            case R.id.btn_complete:
                setCourseConfirmed();
                break;
        }
    }

    private void setCourseConfirmed() {
        String courseId = courseModel.getCourseId();
        HttpManager.getInstance().doSetCourseConfirmed("OrderTableActivity", courseId, orgId, isOrder,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        supportFinishAfterTransition();
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_ORDERS));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("OrderTableActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(OrderTableActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("OrderTableActivity onError", throwable.getMessage());
                    }
                });
    }

}
