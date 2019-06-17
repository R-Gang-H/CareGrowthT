package com.caregrowtht.app.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.CourseAdapter;
import com.caregrowtht.app.adapter.StuOrderAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.UserEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.LoadingFrameView;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 预约课提醒
 */
public class StuOrdersActivity extends BaseActivity implements ViewOnItemClick {

    private MessageEntity msgEntity;
    private String orgId, showType;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_last_week)
    ImageView ivLastWeek;
    @BindView(R.id.tv_tswk)
    TextView tvTswk;
    @BindView(R.id.iv_next_week)
    ImageView ivNextWeek;
    @BindView(R.id.rv_course)
    RecyclerView rvCourse;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    private CourseAdapter mAdapter;
    private String today = DateUtil.getNetTime("yyyy-MM-dd");
    private String selectDay = DateUtil.getNetTime("yyyy-MM-dd");

    private ArrayList<CourseEntity> listData = new ArrayList<>();// 全部(包括可约 不可约)
    private ArrayList<CourseEntity> yesList = new ArrayList<>();// 课程可约
    private ArrayList<CourseEntity> yesClassList = new ArrayList<>();// 课程可约(分类之后的课)
    private int cardType = 3;//1:点击课表放大 2:选择排课班级里的成员 3:预约课提醒
    private String isOrder = "1";// 1：全部(包括可约 不可约) 2:课程可约 3课程不可约

    private StuOrderAdapter stuAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_stu_order_activitys;
    }

    @Override
    public void initView() {
        tvTitle.setText("预约课提醒");
        String mondayOfWeek = TimeUtils.getDayOfWeek("MM月dd日", 1, selectDay);
        String sundayOfWeek = TimeUtils.getDayOfWeek("MM月dd日", 7, selectDay);
        tvTswk.setText(String.format("%s-%s", mondayOfWeek, sundayOfWeek));

        initRecyclerView(rvCourse, false);
        //禁用滑动事件
        rvCourse.setNestedScrollingEnabled(false);
        //添加Android自带的分割线
        rvCourse.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        rvCourse.setHasFixedSize(true);

        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            strings.add(i + "");
        }
        mAdapter = new CourseAdapter(this, R.layout.item_course_list, strings, today, cardType);
        rvCourse.setAdapter(mAdapter);

        initRecyclerView(recyclerView, true);
        stuAdapter = new StuOrderAdapter(yesClassList, today, this, this);
        recyclerView.setAdapter(stuAdapter);
    }

    @Override
    public void initData() {
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (StrUtils.isNotEmpty(msgEntity)) {
            orgId = msgEntity.getOrgId();
            showType = msgEntity.getType();
            UserManager.getInstance().setOrgId(orgId);
        }
        getCourseTimetable();
    }

    @OnClick({R.id.rl_back_button, R.id.tv_tswk, R.id.iv_last_week, R.id.iv_next_week})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.tv_tswk:
                selectDate();
                break;
            case R.id.iv_last_week:
                selectDay = TimeUtils.getDayOfWeek("yyyy-MM-dd", 0, selectDay);
                String mondayOfWeek = TimeUtils.getDayOfWeek("MM月dd日", 1, selectDay);
                String sundayOfWeek = TimeUtils.getDayOfWeek("MM月dd日", 7, selectDay);
                tvTswk.setText(String.format("%s-%s", mondayOfWeek, sundayOfWeek));
                getCourseTimetable();
                break;
            case R.id.iv_next_week:
                selectDay = TimeUtils.getDayOfWeek("yyyy-MM-dd", 8, selectDay);
                String monday = TimeUtils.getDayOfWeek("MM月dd日", 1, selectDay);
                String sunday = TimeUtils.getDayOfWeek("MM月dd日", 7, selectDay);
                tvTswk.setText(String.format("%s-%s", monday, sunday));
                getCourseTimetable();
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

        new TimePickerBuilder(this, (date, v) -> {
            selectDay = DateUtil.getDate(date.getTime() / 1000, "yyyy-MM-dd");

            String mondayOfWeek = TimeUtils.getDayOfWeek("MM月dd日", 1, selectDay);
            String sundayOfWeek = TimeUtils.getDayOfWeek("MM月dd日", 7, selectDay);
            tvTswk.setText(String.format("%s-%s", mondayOfWeek, sundayOfWeek));
            getCourseTimetable();
        })
                .setType(new boolean[]{true, true, true, false, false, false})
                .setRangDate(startDate, null)
                .setLabel("年", "月", "日", "", "", "")
                .build().show();
    }

    private void getCourseTimetable() {
        loadView.setProgressShown(true);
        String startDate = TimeUtils.getDayOfWeek("yyyy-MM-dd 00:00", 1, selectDay);
        String endDate = TimeUtils.getDayOfWeek("yyyy-MM-dd 24:00", 7, selectDay);
        //haoruigang on 2018-4-23 17:40:26 获取首页课程表
        HttpManager.getInstance().doGetCourseTimetable("StuOrdersActivity", startDate, endDate, orgId, isOrder,
                new HttpCallBack<BaseDataModel<CourseEntity<Object, Object, List<UserEntity>>>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity<Object, Object, List<UserEntity>>> data) {
                        listData.clear();
                        listData.addAll(data.getData());
                        mAdapter.update(selectDay, listData, 0);

                        // 筛选可预约的课程
                        yesList.clear();
                        for (CourseEntity entity : listData) {
                            if (entity.getIsOrder().equals("1")) {// 可预约课
                                yesList.add(entity);
                            }
                        }

                        //筛选分类
                        if (yesList.size() > 0) {
                            yesClassList.clear();
                            for (int i = 1; i < 8; i++) {
                                ArrayList<CourseEntity> yesClass = new ArrayList<>();
                                for (CourseEntity course : yesList) {
                                    int dayOfWeek = TimeUtils.getDayOfWeek(Long.valueOf(course.getStartAt()));// 某一周中的周几
                                    if (dayOfWeek == i) {
                                        yesClass.add(course);
                                    }
                                }
                                yesClassList.addAll(yesClass);
                            }
                            stuAdapter.setData(yesClassList);
                        }

                        if (listData.size() > 0) {
                            loadView.delayShowContainer(true);
                        } else {
                            loadView.setNoShown(true);
                        }

                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(StuOrdersActivity.this);
                        } else {
                            loadView.setErrorShown(true, v -> getCourseTimetable());
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        loadView.setErrorShown(true, v -> getCourseTimetable());
                        LogUtils.d("StuOrdersActivity onFail", throwable.getMessage());
                    }
                });
    }

    @Override
    public void onEvent(ToUIEvent event) {
        switch (event.getWhat()) {
            case ToUIEvent.REFERSH_ORDERS:
                getCourseTimetable();
                break;
        }
    }

    @Override
    public void setOnItemClickListener(View view, int position) {

    }

}
