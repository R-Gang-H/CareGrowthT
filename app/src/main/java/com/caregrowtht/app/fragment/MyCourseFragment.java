package com.caregrowtht.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.CourseRecordActivity;
import com.caregrowtht.app.adapter.CourseAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-4-23 16:14:13 我的课程
 */
public class MyCourseFragment extends BaseFragment {

    @BindView(R.id.iv_last_week)
    ImageView ivLastWeek;
    @BindView(R.id.tv_tswk)
    TextView tvTswk;
    @BindView(R.id.iv_next_week)
    ImageView ivNextWeek;
    @BindView(R.id.rv_course)
    RecyclerView rvCourse;

    private String orgId;

    private CourseAdapter mAdapter;
    private String today = DateUtil.getNetTime("yyyy-MM-dd");
    private String selectDay = DateUtil.getNetTime("yyyy-MM-dd");

    private ArrayList<CourseEntity> listData = new ArrayList<>();
    private int type = 1;//1：我的课表 2：机构课表 3：跨机构课表
    private int cardType = 1;//默认 点击课表放大

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_course;
    }

    @Override
    public void initView(View view, Bundle savedInstanceState) {

        String mondayOfWeek = getDayOfWeek("MM月dd日", 1);
        String sundayOfWeek = getDayOfWeek("MM月dd日", 7);
        tvTswk.setText(String.format("%s-%s", mondayOfWeek, sundayOfWeek));

        initRecyclerView(rvCourse, false);
        //禁用滑动事件
        rvCourse.setNestedScrollingEnabled(false);
        //添加Android自带的分割线
        rvCourse.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL));
        rvCourse.setHasFixedSize(true);

        if (getArguments() != null) {
            type = getArguments().getInt("type");//机构周课表
            cardType = getArguments().getInt("cardType");//判断课程卡点击跳转 1:点击课表放大 2:选择排课班级里的成员
        }
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            strings.add(i + "");
        }
        mAdapter = new CourseAdapter(getActivity(), R.layout.item_course_list, strings, today, cardType);
        rvCourse.setAdapter(mAdapter);
        scroll2Today();
    }

    @Override
    public void initData() {
        if (UserManager.getInstance().getOrgEntity() != null) {
            String identity = UserManager.getInstance().getOrgEntity().getIdentity();
            // 教务老师和管理者，进去教师端默认是机构课表，教学老师默认是我的课表
            if (identity.contains("教务") || identity.contains("管理")) {
                type = 2;
            }
        }
        getWeekCourseTable();
    }

    /**
     * The horizontal recyclerView should be scroll to today.
     */
    private void scroll2Today() {
        rvCourse.scrollToPosition(getDayOfWeek(selectDay) - 1);
    }

    /**
     * Get what day of week.
     * 得到星期几
     *
     * @param date
     * @return
     */
    private int getDayOfWeek(String date) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal.get(Calendar.DAY_OF_WEEK) - 1 == 0 ? 7 : cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * Get what day of the week is the selected day.
     * 一周中的哪一天是被选中的一天
     *
     * @param type
     * @param which
     * @return
     */
    private String getDayOfWeek(String type, int which) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(selectDay));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int d;
        if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
            d = -6;
        } else {
            d = 2 - cal.get(Calendar.DAY_OF_WEEK);
        }
        cal.add(Calendar.DAY_OF_WEEK, d);
        cal.add(Calendar.DAY_OF_WEEK, which - 1);
        return new SimpleDateFormat(type).format(cal.getTime());
    }

    @OnClick({R.id.tv_tswk, R.id.iv_last_week, R.id.iv_next_week, R.id.iv_magnifier})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_tswk:
                selectDate();
                break;
            case R.id.iv_last_week:
                selectDay = getDayOfWeek("yyyy-MM-dd", 0);
                String mondayOfWeek = getDayOfWeek("MM月dd日", 1);
                String sundayOfWeek = getDayOfWeek("MM月dd日", 7);
                tvTswk.setText(String.format("%s-%s", mondayOfWeek, sundayOfWeek));
                getWeekCourseTable();
                break;
            case R.id.iv_next_week:
                selectDay = getDayOfWeek("yyyy-MM-dd", 8);
                String monday = getDayOfWeek("MM月dd日", 1);
                String sunday = getDayOfWeek("MM月dd日", 7);
                tvTswk.setText(String.format("%s-%s", monday, sunday));
                getWeekCourseTable();
                break;
            case R.id.iv_magnifier:
                startActivity(new Intent(getContext(), CourseRecordActivity.class).putExtra("orgId", orgId));
                break;
        }
    }

    /**
     * select time to update course table.
     */
    private void selectDate() {
        String[] split = today.split("-");
        Calendar startDate = Calendar.getInstance();
        startDate.set(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1, Integer.parseInt(split[2]));

        new TimePickerBuilder(mContext, (date, v) -> {
            selectDay = DateUtil.getDate(date.getTime() / 1000, "yyyy-MM-dd");

            String mondayOfWeek = getDayOfWeek("MM月dd日", 1);
            String sundayOfWeek = getDayOfWeek("MM月dd日", 7);
            tvTswk.setText(String.format("%s-%s", mondayOfWeek, sundayOfWeek));
            getWeekCourseTable();
        })
                .setType(new boolean[]{true, true, true, false, false, false})
                .setRangDate(startDate, null)
                .setLabel("年", "月", "日", "", "", "")
                .build().show();
    }

    public void getWeekCourseTable() {
        orgId = UserManager.getInstance().getOrgId();
        String startDate = getDayOfWeek("yyyy-MM-dd 00:00", 1);
        String endDate = getDayOfWeek("yyyy-MM-dd 24:00", 7);
        //haoruigang on 2018-4-23 17:40:26 获取首页课程表
        HttpManager.getInstance().doTeacherLessonTable("MyCourseFragment", startDate, endDate, type, orgId,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        listData.clear();
                        listData.addAll(data.getData());
                        mAdapter.update(selectDay, listData, type);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(getActivity());
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        switch (event.getWhat()) {
            case ToUIEvent.COURSE_TYPE:
                type = (int) event.getObj();
                getWeekCourseTable();
                break;
            case ToUIEvent.TEACHER_REFERSH:
                if ((Boolean) event.getObj()) {
                    UserManager.getInstance().setOrgId(
                            String.valueOf(U.getPreferences("orgId", "")));
                }
                getWeekCourseTable();
                break;
        }
    }

}
