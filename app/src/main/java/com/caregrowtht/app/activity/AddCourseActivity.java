package com.caregrowtht.app.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.android.library.view.WheelPopup;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.ClassTimeAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.TimeEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.picker.builder.TimePickerBuilder;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;

import static com.caregrowtht.app.Constant.sexWeekly;

/**
 * haoruigang on 2018-9-4 12:02:33
 * 加课
 */
public class AddCourseActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_online_priview)
    TextView tvOnlinePriview;
    @BindView(R.id.rv_class_times)
    RecyclerView rvClassTimes;
    @BindView(R.id.iv_class_time)
    ImageView ivClassTime;
    @BindView(R.id.tv_weekly)
    TextView tvWeekly;
    @BindView(R.id.et_end)
    TextView etEnd;
    @BindView(R.id.tv_of)
    TextView tvOf;
    @BindView(R.id.et_after)
    EditText etAfter;
    @BindView(R.id.tv_after_end)
    TextView tvAfterEnd;
    @BindView(R.id.btn_check_conflict)
    Button btnCheckConflict;
    @BindView(R.id.btn_dereact_course)
    Button btnDereactCourse;

    private CourseEntity courseData;
    private String repeat = "1";//表示重复周期 1：无 2：每天 3：每周 4：每月
    private String repeatEver = "1";//1：永远重复 2：不是永远重复
    private String courseId;
    private String courseName;
    private ClassTimeAdapter classTimeAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_course;
    }

    @Override
    public void initView() {
        tvTitle.setText("加课");
        initRecyclerView(rvClassTimes, true);
        classTimeAdapter = new ClassTimeAdapter(new ArrayList(), this, this);
        rvClassTimes.setAdapter(classTimeAdapter);
    }

    @Override
    public void initData() {
        courseData = (CourseEntity) getIntent().getSerializableExtra("courseData");
        courseId = courseData.getCourseId();
        courseName = courseData.getCourseName();
        tvOnlinePriview.setText(String.format("所属排课:%s", courseName));

        Long startAt = Long.valueOf(courseData.getStartAt());
        Long endAt = Long.valueOf(courseData.getEndAt());
        String startDate = DateUtil.getDate(startAt, "yyyy年MM月dd日 HH:mm");
        String endDate = DateUtil.getDate(endAt, "HH:mm");
        Logger.d(startDate + " ~ " + endDate);
        TimeEntity timeEntity = new TimeEntity();
        timeEntity.setStartTime(String.valueOf(startAt));
        timeEntity.setEndTime(String.valueOf(endAt));
        classTimeAdapter.timeList.add(timeEntity);
        String selectStartDate1 = DateUtil.getDate(Long.valueOf(startAt), "yyyy年MM月dd日 HH:mm");
        String selectEndTime = DateUtil.getDate(Long.valueOf(endAt), "HH:mm");
        classTimeAdapter.classTimes.add(String.format("%s~%s", selectStartDate1, selectEndTime));
        classTimeAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.rl_back_button, R.id.iv_class_time, R.id.tv_weekly, R.id.tv_of, R.id.et_after,
            R.id.btn_check_conflict, R.id.btn_dereact_course})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.iv_class_time:
                selectStartEndTime(-1);
                break;
            case R.id.tv_weekly:
                selectRepeat();
                break;
            case R.id.tv_of:
                selectOf();
                break;
            case R.id.et_after:
                if (isShowOfTime)
                    selectOfTime();
                break;
            case R.id.btn_check_conflict:
                btnCheckConflict.setEnabled(false);
                addLessonV2("1");
                break;
            case R.id.btn_dereact_course:
                btnDereactCourse.setEnabled(false);
                addLessonV2("2");
                break;
        }
    }

    /**
     * Select start and end time of CourseTimeAdapter's item.
     */
    private void selectStartEndTime(int position) {
        Calendar startDate = Calendar.getInstance();
        new TimePickerBuilder(this, (startTime, v) -> {
            String selectStartDate1 = DateUtil.getDate(startTime.getTime() / 1000, "yyyy年MM月dd日 HH:mm");
            String selectStartDate = DateUtil.getDate(startTime.getTime() / 1000, "yyyy-MM-dd-HH-mm");
            String[] split = selectStartDate.split("-");
            startDate.set(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1, Integer.parseInt(split[2]),//month从0开始
                    Integer.parseInt(split[3]), Integer.parseInt(split[4]));
            new TimePickerBuilder(this, (endTime, view) -> {
                String selectEndTime = DateUtil.getDate(endTime.getTime() / 1000, "HH:mm");
                Logger.d(selectStartDate + " ~ " + selectEndTime);

                // Traversal the timeList to find the duplicate element and delete it.
                String[] endArr = selectEndTime.split(":");
                int startHour = Integer.parseInt(split[3]);
                int startMinute = Integer.parseInt(split[4]);
                int endHour = Integer.parseInt(endArr[0]);
                int endMinute = Integer.parseInt(endArr[1]);
                if (startHour > endHour || (startHour == endHour && startMinute >= endMinute)) {
                    U.showToast("开始时间不能小于结束时间");
                    return;
                }

                TimeEntity timeEntity = new TimeEntity();
                timeEntity.setStartTime(String.valueOf(startTime.getTime() / 1000));
                timeEntity.setEndTime(String.valueOf(endTime.getTime() / 1000));
                if (position >= 0) {
                    classTimeAdapter.timeList.remove(position);
                    classTimeAdapter.timeList.add(position, timeEntity);
                } else {
                    classTimeAdapter.timeList.add(timeEntity);
                }

                if (position >= 0) {
                    classTimeAdapter.classTimes.remove(position);
                    classTimeAdapter.classTimes.add(position, String.format("%s~%s", selectStartDate1, selectEndTime));
                } else {
                    classTimeAdapter.classTimes.add(String.format("%s~%s", selectStartDate1, selectEndTime));
                }
                classTimeAdapter.notifyDataSetChanged();
            }).setType(new boolean[]{false, false, false, true, true, false})
                    .setRangDate(startDate, startDate)
                    .setTitleText("选择结束时间")
                    .setLabel("", "", "", "时", "分", "")
                    .build().show();
        }).setType(new boolean[]{true, true, true, true, true, false})
                .setTitleText("选择开始时间")
                .setRangDate(startDate, null)
                .setLabel("年", "月", "日", "时", "分", "")
                .build().show();
    }

    /**
     * 重复 "天", "周", "月"
     */
    boolean isShowAfter = true;//首次是否显示几次后结束

    private void selectRepeat() {
        WheelPopup pop = new WheelPopup(this, Constant.sexWeekly);
        pop.showAtLocation(View.inflate(this, R.layout.item_color_course, null), Gravity
                .BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        pop.setSelectListener((argValue, position) -> {
            switch (position) {
                case 0:
                    etEnd.setVisibility(View.GONE);
                    tvOf.setVisibility(View.GONE);
                    etAfter.setVisibility(View.GONE);
                    tvAfterEnd.setVisibility(View.GONE);
                    break;
                case 1:
                case 2:
                case 3:
                    etEnd.setVisibility(View.VISIBLE);
                    tvOf.setVisibility(View.VISIBLE);
                    if (isShowAfter) {//首次默认是于几次
                        repeatEver = "1";
                        etAfter.setVisibility(View.VISIBLE);
                        tvAfterEnd.setVisibility(View.VISIBLE);
                        break;
                    }
            }
            repeat = position + 1 + "";
            tvWeekly.setText(argValue);
            return null;
        });
    }

    /**
     * 重复 "于", "于日前", "永不"
     */
    boolean isShowOfTime = false;// 是否显示选择时间弹框

    private void selectOf() {
        String[] sexWeekly = {"于", "于日前"};
        WheelPopup pop = new WheelPopup(this, sexWeekly);
        pop.showAtLocation(View.inflate(this, R.layout.item_color_course, null), Gravity
                .BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        pop.setSelectListener((argValue, position) -> {
            isShowAfter = false;//选择之后不显示
            switch (position) {
                case 0:
                    isShowOfTime = false;
                    repeatEver = "1";
                    etAfter.setVisibility(View.VISIBLE);
                    etAfter.setFocusable(true);
                    etAfter.setText("1");
                    tvAfterEnd.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    isShowOfTime = true;
                    etAfter.setVisibility(View.VISIBLE);
                    etAfter.setFocusable(false);
                    etAfter.setText(DateUtil.getSysTimeType("yyyy/MM/dd"));
                    tvAfterEnd.setVisibility(View.GONE);
                    repeatEver = "1";
                    break;
            }
            tvOf.setText(argValue);
            return null;
        });
    }

    /**
     * 结束于时间
     */
    private void selectOfTime() {
        new TimePickerBuilder(this, (startTime, v) -> {
            String selectStartDate = DateUtil.getDate(startTime.getTime() / 1000, "yyyy/MM/dd");
            etAfter.setText(selectStartDate);
        }).setType(new boolean[]{true, true, true, false, false, false})
                .setTitleText("选择时间")
                .setLabel("年", "月", "日", "", "", "")
                .build().show();
    }

    /**
     * 76.加课
     */
    private void addLessonV2(String check) {//1:检查排课冲突 2：直接排入课表
        String orgId = UserManager.getInstance().getOrgId();
        if (TextUtils.isEmpty(check) || TextUtils.isEmpty(orgId)) {
            LogUtils.d("AddCourseActivity", "check、orgId");
            return;
        }
        StringBuilder courseTime = new StringBuilder();
        for (int i = 0; i < classTimeAdapter.timeList.size(); i++) {
            TimeEntity timeEntity = classTimeAdapter.timeList.get(i);
            courseTime.append(String.format("%s,%s", timeEntity.getStartTime(), timeEntity.getEndTime()));
            if (i < classTimeAdapter.timeList.size() - 1) {
                courseTime.append("#");
            }
        }
        if (TextUtils.isEmpty(courseTime)) {
            U.showToast("请选择排课时间");
            return;
        }
        HttpManager.getInstance().doAddLessonV2("AddCourseActivity", check, orgId, courseTime.toString(),
                repeat, etAfter.getText().toString().trim(), false,
                repeatEver, courseId, new HttpCallBack<BaseDataModel<CourseEntity>>(this) {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        if (TextUtils.equals(check, "1")) {
                            btnCheckConflict.setEnabled(true);
                            if (data != null && data.getData().size() > 0) {
                                showConflictDialog(data.getData().get(0));
                            } else {
                                showConflictDialog(null);
                            }
                        } else {
                            btnDereactCourse.setEnabled(true);
                            U.showToast("加课成功");
                            EventBus.getDefault().post(new ToUIEvent(ToUIEvent.TEACHER_REFERSH));
                            finish();
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        btnCheckConflict.setEnabled(true);
                        btnDereactCourse.setEnabled(true);
                        LogUtils.d("NewWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(AddCourseActivity.this);
                        } else if (statusCode == 1057 || statusCode == 1055) {//排课名称相同
                            U.showToast("排课名称相同!");
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    /**
     * 排课冲突提示
     *
     * @param data
     */
    private void showConflictDialog(CourseEntity data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_prompt_org, null);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText("排课冲突");
        TextView tvDesc = view.findViewById(R.id.tv_desc);
        TextView tv_ok = view.findViewById(R.id.tv_ok);
        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        if (data != null) {
            String startAt = data.getStartAt();
            String startDate = "";
            if (!TextUtils.isEmpty(startAt)) {
                startDate = DateUtil.getDate(Long.valueOf(startAt), "yyyy年MM月dd日 HH:mm") + "-";
            }
            String endAt = data.getEndAt();
            String endDate = "";
            if (!TextUtils.isEmpty(endAt)) {
                endDate = DateUtil.getDate(Long.valueOf(endAt), "HH:mm");
            }
            tvDesc.setText(Html.fromHtml(String.format("系统发现排课%s与本机构%s的%s有冲突。" +
                            "<br/>冲突原因：%s<br/><font color='#AAAAAA'>（冲突课程可能不限于此）</font>",
                    courseName, startDate + endDate, data.getCourseName(), data.getConflict())));
            tv_cancel.setText("修改排课");
            tv_ok.setText("忽略冲突排入课表");
        } else {
            tvDesc.setText("未发现排课冲突");
            tv_ok.setText("排入课表");
        }
        tv_ok.setOnClickListener(v -> {
            dialog.dismiss();
            addLessonV2("2");
        });
        tv_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
    }

    @Override
    public void setOnItemClickListener(View view, int postion) {
        selectStartEndTime(postion);
    }
}
