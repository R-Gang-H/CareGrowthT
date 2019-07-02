package com.caregrowtht.app.activity;

import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.ScreenAdapter;
import com.caregrowtht.app.uitil.GradientUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.user.ToUIEvent;
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

    @BindView(R.id.rl_course_range)
    RelativeLayout rlCourseRange;
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
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private String[] timeFrame = {"今天", "7天", "14天", "30天"};
    private ScreenAdapter screenAdapter;

    private String isMy = "2";// 1 我的课程 2机构课程
    private String today = DateUtil.getNetTime("yyyy-MM-dd");
    private String selectDay = DateUtil.getNetTime("yyyy-MM-dd");
    private long startTime, endTime;
    private String showType, status;// 9：有学员请假汇总动态 | 11：每日日报汇总动态
    private boolean roleType;// 权限类型
    private int index = -1;// 上次选中的时间段

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int getLayoutId() {
        GradientUtils.setColor(this, R.drawable.mine_title_bg, true);//全屏显示需要添加的语句
        return R.layout.activity_view_menu;
    }

    @Override
    public void initView() {
        initRecyclerGrid(rvTimeFrame, 4);
        screenAdapter = new ScreenAdapter(Arrays.asList(timeFrame), this, this);
        rvTimeFrame.setAdapter(screenAdapter);

        tvOrgCourse.setSelected(true);// 默认选中机构课程

        roleType = getIntent().getBooleanExtra("role_type", false);// true 有 ,false 没有 是否有权限
        if (!roleType) {// 没有机构权限
            rlCourseRange.setVisibility(View.GONE);
            isMy = "1";// 1 我的课程 2机构课程
        }
    }

    @Override
    public void initData() {
        showType = getIntent().getStringExtra("type");
        status = getIntent().getStringExtra("status");
        isMy = getIntent().getStringExtra("isMy");
        index = getIntent().getIntExtra("index", -1);
        if (showType.equals("11")) {// 11：每日日报汇总动态
            timeFrame = new String[]{"7天", "14天", "30天"};
            screenAdapter.setData(Arrays.asList(timeFrame));
            rlCourseRange.setVisibility(View.GONE);
            isMy = "1";// 1 我的课程 2机构课程
        }
        my_org(isMy);
        positonScreen(index);
    }

    @OnClick({R.id.iv_back, R.id.btn_cancel, R.id.tv_my_course, R.id.tv_org_course,
            R.id.tv_start_time, R.id.tv_end_time, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.tv_my_course:
                isMy = "1";
                my_org(isMy);
                break;
            case R.id.tv_org_course:
                isMy = "2";
                my_org(isMy);
                break;
            case R.id.tv_start_time:
                screenAdapter.getSelect(-1);
                selectDate(1);
                break;
            case R.id.tv_end_time:
                screenAdapter.getSelect(-1);
                selectDate(2);
                break;
            case R.id.btn_submit:
                EventBus.getDefault().post(new ToUIEvent(ToUIEvent.SET_SCREEN_LES, isMy, startTime, endTime, index));
                finish();
                break;
        }
    }

    private void my_org(String isMy) {
        tvMyCourse.setSelected(isMy.equals("1"));
        tvOrgCourse.setSelected(isMy.equals("2"));
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
                    startTime = DateUtil.getStringToDate(DateUtil.getDate(time, "yyyy-MM-dd 00:00"), "yyyy-MM-dd HH:mm");
                    tvStartTime.setText(selectDay);
                }
            } else {
                if (StrUtils.isEmpty(startTime)) {
                    U.showToast("请先选择开始时间");
                } else if (time < startTime || startTime == endTime) {
                    U.showToast("结束时间不能小于开始时间");
                } else {
                    endTime = DateUtil.getStringToDate(DateUtil.getDate(time, "yyyy-MM-dd 24:00"), "yyyy-MM-dd HH:mm");
                    tvEndTime.setText(selectDay);
                }
            }
        })
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("年", "月", "日", "", "", "")
                .build().show();
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        index = position;
        positonScreen(position);
    }

    private void positonScreen(int position) {
        screenAdapter.getSelect(position);
        if (showType.equals("11")) {//  11：每日日报汇总动态
            position += 1;
        }
        TimeUtils.GetStartEndTime getStartEndTime;
        switch (position) {
            case 0:
                getTodayTime();
                break;
            case 1:
                if (showType.equals("9") && status.equals("1")) {// 9：有学员请假汇总动态
                    getStartEndTime = new TimeUtils.GetStartEndTime().invoke(0, 7);
                } else {
                    getStartEndTime = new TimeUtils.GetStartEndTime().invoke(-7, 0);
                }
                startTime = getStartEndTime.getWithinDay();
                endTime = getStartEndTime.getYesTerday();
                break;
            case 2:
                if (showType.equals("9") && status.equals("1")) {// 9：有学员请假汇总动态
                    getStartEndTime = new TimeUtils.GetStartEndTime().invoke(0, 14);
                } else {
                    getStartEndTime = new TimeUtils.GetStartEndTime().invoke(-14, 0);
                }
                startTime = getStartEndTime.getWithinDay();
                endTime = getStartEndTime.getYesTerday();
                break;
            case 3:
                if (showType.equals("9") && status.equals("1")) {// 9：有学员请假汇总动态
                    getStartEndTime = new TimeUtils.GetStartEndTime().invoke(0, 30);
                } else {
                    getStartEndTime = new TimeUtils.GetStartEndTime().invoke(-30, 0);
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

}
