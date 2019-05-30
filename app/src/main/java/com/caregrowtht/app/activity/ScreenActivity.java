package com.caregrowtht.app.activity;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.ScreenAdapter;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-5-4 14:11:33
 * 学员请假筛选
 */
public class ScreenActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_my_course)
    TextView tvMyCourse;
    @BindView(R.id.tv_org_course)
    TextView tvOrgCourse;
    @BindView(R.id.rv_time_frame)
    RecyclerView rvTimeFrame;
    @BindView(R.id.rl_time_frame)
    RelativeLayout rlTimeFrame;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.tv_month)
    TextView tvMonth;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private String orgId;

    private String[] timeFrame = {"今天", "7天", "30天", "90天"};
    private ScreenAdapter screenAdapter;

    private long todayTime;// 今天的时间戳
    private String isMy = "1";// 1 我的课程 2机构课程
    private String today = DateUtil.getNetTime("yyyy-MM-dd");
    private String selectDay = DateUtil.getNetTime("yyyy-MM-dd");
    private long startTime, endTime;
    private String showType, status;

    @Override
    public int getLayoutId() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏显示需要添加的语句
        return R.layout.activity_view_menu;
    }

    @Override
    public void initView() {
        initRecyclerGrid(rvTimeFrame, 4);
        screenAdapter = new ScreenAdapter(Arrays.asList(timeFrame), this, this);
        rvTimeFrame.setAdapter(screenAdapter);
        tvMyCourse.setSelected(true);
        screenAdapter.getSelect(0);
    }

    @Override
    public void initData() {
        showType = getIntent().getStringExtra("type");
        status = getIntent().getStringExtra("status");
        if (showType.equals("9")) {// 9：有学员请假汇总动态
            rlTimeFrame.setVisibility(View.GONE);
        } else {
            tvMonth.setVisibility(View.GONE);
        }
        orgId = UserManager.getInstance().getOrgId();
        todayTime = TimeUtils.getCurTimeLong() / 1000;
        getTodayTime();
    }

    @OnClick({R.id.iv_back, R.id.btn_cancel, R.id.tv_my_course, R.id.tv_org_course,
            R.id.tv_start_time, R.id.tv_end_time, R.id.tv_month, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.tv_my_course:
                isMy = "1";
                tvMyCourse.setSelected(true);
                tvOrgCourse.setSelected(false);
                break;
            case R.id.tv_org_course:
                isMy = "2";
                tvMyCourse.setSelected(false);
                tvOrgCourse.setSelected(true);
                break;
            case R.id.tv_start_time:
                screenAdapter.getSelect(-1);
                selectDate(1);
                break;
            case R.id.tv_end_time:
                screenAdapter.getSelect(-1);
                selectDate(2);
                break;
            case R.id.tv_month:
                screenAdapter.getSelect(-1);
                monthDate();
                break;
            case R.id.btn_submit:
                EventBus.getDefault().post(new ToUIEvent(ToUIEvent.SET_SCREEN_LES, isMy, startTime, endTime));
                finish();
                break;
        }
    }

    private void selectDate(int type) {
        String[] split = today.split("-");
        Calendar startDate = Calendar.getInstance();
        startDate.set(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1, Integer.parseInt(split[2]));

        new TimePickerBuilder(this, (date, v) -> {
            long time = date.getTime() / 1000;
            selectDay = DateUtil.getDate(time, "yyyy-MM-dd");
            if (type == 1) {
                if (StrUtils.isEmpty(endTime) && time > endTime || startTime == endTime) {
                    U.showToast("开始时间不能大于结束时间");
                } else {
                    startTime = time;
                    tvStartTime.setText(selectDay);
                }
            } else {
                if (StrUtils.isEmpty(startTime)) {
                    U.showToast("请先选择开始时间");
                } else if (time < startTime || startTime == endTime) {
                    U.showToast("结束时间不能小于开始时间");
                } else {
                    endTime = time;
                    tvEndTime.setText(selectDay);
                }
            }
        })
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("年", "月", "日", "", "", "")
                .build().show();
    }

    private void monthDate() {
        String[] split = today.split("-");
        Calendar startDate = Calendar.getInstance();
        startDate.set(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1, Integer.parseInt(split[2]));

        new TimePickerBuilder(this, (date, v) -> {
            long time = date.getTime() / 1000;
            selectDay = DateUtil.getDate(date.getTime() / 1000, "yyyy-MM");
            startTime = time;
            endTime = time;
            tvMonth.setText(selectDay);
        })
                .setType(new boolean[]{true, true, false, false, false, false})
                .setLabel("年", "月", "", "", "", "")
                .build().show();
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        screenAdapter.getSelect(position);
        if (showType.equals("9")) {// 9：有学员请假汇总动态
            rlTimeFrame.setVisibility(View.GONE);
        } else {
            tvMonth.setVisibility(View.GONE);
        }
        GetStartEndTime getStartEndTime;
        switch (position) {
            case 0:
                getTodayTime();
                break;
            case 1:
                if (showType.equals("9") && status.equals("1")) {// 9：有学员请假汇总动态
                    getStartEndTime = new GetStartEndTime().invoke(1, 7);
                } else {
                    getStartEndTime = new GetStartEndTime().invoke(-7, -1);
                }
                startTime = getStartEndTime.getWithinDay();
                endTime = getStartEndTime.getYesTerday();
                break;
            case 2:
                if (showType.equals("9") && status.equals("1")) {// 9：有学员请假汇总动态
                    getStartEndTime = new GetStartEndTime().invoke(1, 30);
                } else {
                    getStartEndTime = new GetStartEndTime().invoke(-30, -1);
                }
                startTime = getStartEndTime.getWithinDay();
                endTime = getStartEndTime.getYesTerday();
                break;
            case 3:
                if (showType.equals("9") && status.equals("1")) {// 9：有学员请假汇总动态
                    getStartEndTime = new GetStartEndTime().invoke(1, 90);
                } else {
                    getStartEndTime = new GetStartEndTime().invoke(-90, -1);
                }
                startTime = getStartEndTime.getWithinDay();
                endTime = getStartEndTime.getYesTerday();
                break;
        }
    }

    private void getTodayTime() {
        startTime = DateUtil.getStringToDate(TimeUtils.getCurTimeLong("yyyy-MM-dd 00:00"), "yyyy-MM-dd HH:mm");
        endTime = DateUtil.getStringToDate(TimeUtils.getCurTimeLong("yyyy-MM-dd 24:00"), "yyyy-MM-dd HH:mm");
    }

    private class GetStartEndTime {
        private long withinDay;
        private long yesTerday;

        long getWithinDay() {
            return withinDay;
        }

        long getYesTerday() {
            return yesTerday;
        }

        GetStartEndTime invoke(int start, int end) {
            withinDay = DateUtil.getStringToDate(TimeUtils.dateTiem(
                    DateUtil.getDate(todayTime, "yyyy-MM-dd 00:00")
                    , start, "yyyy-MM-dd 00:00"), "yyyy-MM-dd 00:00");
            yesTerday = DateUtil.getStringToDate(TimeUtils.dateTiem(
                    DateUtil.getDate(todayTime, "yyyy-MM-dd 24:00")
                    , end, "yyyy-MM-dd 24:00"), "yyyy-MM-dd 24:00");
            return this;
        }
    }
}
