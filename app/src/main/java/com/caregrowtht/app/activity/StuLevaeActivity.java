package com.caregrowtht.app.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.SignLevaeCourseAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.UserEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.LoadingFrameView;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 学员请假列表
 */
public class StuLevaeActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_no_start)
    TextView tvNoStart;
    @BindView(R.id.tv_finish)
    TextView tvFinish;
    @BindView(R.id.recycler_view)
    XRecyclerView recyclerView;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    MessageEntity msgEntity;
    private String orgId;
    private String isMy = "1";// 1 我的课程 2机构课程
    private String beginAt = "0", endAt = "0";
    private long startTime, endTime;// 今天开始和结束的时间戳
    private String Url;

    private SignLevaeCourseAdapter adapter;
    private List<CourseEntity> courseData = new ArrayList<>();
    private String status = "1";// 1未开始的课程 2已结束的课程
    private String showType;
    private boolean isLoading = false, isFirstIn = true;// 是否刷新了 | 请假首次进入
    private boolean roleType = false;// true 有 ,false 没有
    private int index = 0;


    @Override
    public int getLayoutId() {
        return R.layout.activity_stu_levae;
    }

    @Override
    public void initView() {
        rlNextButton.setVisibility(View.VISIBLE);
        ivTitleRight.setImageResource(R.mipmap.ic_screen_right);
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (StrUtils.isNotEmpty(msgEntity)) {
            showType = msgEntity.getType();
            orgId = msgEntity.getOrgId();
            UserManager.getInstance().setOrgId(orgId);
        }
        iniXrecyclerView(recyclerView);
        recyclerView.setPullRefreshEnabled(true);
        recyclerView.setLoadingMoreEnabled(!showType.equals("9"));
        adapter = new SignLevaeCourseAdapter(courseData, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                isLoading = true;
                getCourseList(true);
            }

            @Override
            public void onLoadMore() {
                pageIndex++;
                isLoading = true;
                getCourseList(false);
            }
        });
    }

    @Override
    public void initData() {
        startTime = DateUtil.getStringToDate(TimeUtils.getCurTimeLong("yyyy-MM-dd 00:00"), "yyyy-MM-dd HH:mm");
        endTime = DateUtil.getStringToDate(TimeUtils.getCurTimeLong("yyyy-MM-dd 24:00"), "yyyy-MM-dd HH:mm");
        String isRole = "";
        if (showType.equals("9")) {// 9：有学员请假汇总动态
            tvTitle.setText("学员请假列表");
            tvNoStart.setText("未开始课程");
            tvFinish.setText("已结束课程");
            Url = Constant.GETLEAVELIST;
            isRole = "tx_rc_3";
        } else if (showType.equals("10")) {// 10：签到提醒汇总动态
            tvTitle.setText("出勤处理提醒");
            tvNoStart.setText("待处理");
            tvFinish.setText("已完成");
            Url = Constant.GETATTENDANCE;
            isRole = "tx_rc_2";
        } else if (showType.equals("12")) {// 12：未发布课程反馈提醒汇总动态
            tvTitle.setText("未发布课程反馈");
            tvNoStart.setText("未发布");
            tvFinish.setText("已完成");
            Url = Constant.GETUNPUBLISHED;
            isRole = "tx_rc_6";
        }
        UserManager.getInstance().isTxRole(isRole, isRole1 -> {
            roleType = isRole1;
            if (isRole1) {// 有机构权限
                isMy = "2";// 1 我的课程 2机构课程
            }
            getCourseList(true);
        });
    }

    private void getCourseList(boolean isClear) {
        HttpManager.getInstance().doGetUnpublished("StuLevaeActivity",
                orgId, isMy, beginAt, endAt, status, pageIndex + "", Url,
                new HttpCallBack<BaseDataModel<CourseEntity<Object, Object, List<UserEntity>>>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity<Object, Object, List<UserEntity>>> data) {
                        if (isClear) {
                            courseData.clear();
                        }
                        List<CourseEntity<Object, Object, List<UserEntity>>> list = data.getData();
                        if (list.size() > 0) {
                            // 筛选 条件 start
                            List<CourseEntity> todayData = new ArrayList<>();// 今天的
                            List<CourseEntity> withinData = new ArrayList<>();// 过去7天
                            List<CourseEntity> beforeData = new ArrayList<>();// 7天之前
                            List<CourseEntity> toMorrowData = new ArrayList<>();// 未来7天
                            List<CourseEntity> futureData = new ArrayList<>();// 7天之后
                            for (int i = 0; i < list.size(); i++) {// 筛选今天的
                                CourseEntity entity = list.get(i);
                                long begingAt = DateUtil.getStringToDate(DateUtil.getDate(Long.valueOf(
                                        entity.getCourseBeginAt()), "yyyy-MM-dd"), "yyyy-MM-dd");
                                boolean isToday = TimeUtils.IsToday(DateUtil.getDate(begingAt
                                        , "yyyy-MM-dd"));// 是否为今天
                                long yesTerday = DateUtil.getStringToDate(TimeUtils.dateTiem(
                                        DateUtil.getDate(endTime, "yyyy-MM-dd")
                                        , -1, "yyyy-MM-dd"), "yyyy-MM-dd");// 昨天
                                long withinDay = DateUtil.getStringToDate(TimeUtils.dateTiem(
                                        DateUtil.getDate(endTime, "yyyy-MM-dd")
                                        , -7, "yyyy-MM-dd"), "yyyy-MM-dd");// 过去7天的时间
                                long toMorrow = DateUtil.getStringToDate(TimeUtils.dateTiem(
                                        DateUtil.getDate(startTime, "yyyy-MM-dd")
                                        , 1, "yyyy-MM-dd"), "yyyy-MM-dd");// 明天
                                long future = DateUtil.getStringToDate(TimeUtils.dateTiem(
                                        DateUtil.getDate(startTime, "yyyy-MM-dd")
                                        , 7, "yyyy-MM-dd"), "yyyy-MM-dd");// 未来7天的时间
                                if (isToday) {// 今天
                                    entity.setType("1");
                                    todayData.add(entity);
                                } else if (begingAt <= yesTerday && begingAt >= withinDay) {// 过去7天
                                    entity.setType("2");
                                    withinData.add(entity);
                                } else if (begingAt < withinDay) {// 7天之前
                                    entity.setType("3");
                                    beforeData.add(entity);
                                } else if (begingAt >= toMorrow && begingAt <= future) {// 未来7天
                                    entity.setType("4");
                                    toMorrowData.add(entity);
                                } else if (begingAt > future) {// 7天之后
                                    entity.setType("5");
                                    futureData.add(entity);
                                }
                            }
                            // 筛选 条件 end
                            courseData.addAll(todayData);
                            courseData.addAll(futureData);
                            courseData.addAll(toMorrowData);
                            courseData.addAll(withinData);
                            courseData.addAll(beforeData);
                        }
                        adapter.setData(courseData, showType, status);
                        if (courseData.size() > 0) {
                            loadView.delayShowContainer(true);
                        } else {
                            if (isClear) {
                                loadView.setNoShown(true);
                            } else {
                                loadView.delayShowContainer(true);
                            }
                        }
                        if (isLoading) {
                            if (isClear) {
                                recyclerView.refreshComplete();
                            } else {
                                recyclerView.loadMoreComplete();
                            }
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("StuLevaeActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(StuLevaeActivity.this);
                        } else {
                            loadView.setErrorShown(true, v -> getCourseList(true));
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("StuLevaeActivity onError", throwable.getMessage());
                        loadView.setErrorShown(true, v -> getCourseList(true));
                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.rl_next_button, R.id.tv_no_start, R.id.tv_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.rl_next_button:
                startActivity(new Intent(this, ScreenActivity.class)
                        .putExtra("role_type", roleType)
                        .putExtra("type", showType)
                        .putExtra("status", status)
                        .putExtra("isMy", isMy)
                        .putExtra("index", index));
                break;
            case R.id.tv_no_start:
                pageIndex = 1;
                status = "1";
                tvNoStart.setBackgroundResource(R.mipmap.ic_blue_levae);
                tvNoStart.setTextColor(ResourcesUtils.getColor(R.color.white));
                tvFinish.setBackground(null);
                tvFinish.setTextColor(ResourcesUtils.getColor(R.color.blueLight));
                if (!isFirstIn && showType.equals("9")) {// 9：有学员请假汇总动态 筛选后的时间
                    positonScreen(index);
                }
                getCourseList(true);
                break;
            case R.id.tv_finish:
                pageIndex = 1;
                status = "2";
                tvNoStart.setBackground(null);
                tvNoStart.setTextColor(ResourcesUtils.getColor(R.color.blueLight));
                tvFinish.setBackgroundResource(R.mipmap.ic_blue_levae);
                tvFinish.setTextColor(ResourcesUtils.getColor(R.color.white));
                if (!isFirstIn && showType.equals("9")) {// 9：有学员请假汇总动态 筛选后的时间
                    positonScreen(index);
                }
                getCourseList(true);
                break;
        }
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        startActivity(new Intent(this, CourserActivity.class)
                .putExtra("msgEntity", msgEntity)
                .putExtra("courseId", courseData.get(position - 1).getCourseId()));
    }

    @Override
    public void onEvent(ToUIEvent event) {
        switch (event.getWhat()) {
            case ToUIEvent.SET_SCREEN_LES:
                if (isFirstIn && showType.equals("9")) {// 9：有学员请假汇总动态
                    isFirstIn = false;
                }
                isMy = (String) event.getObj();
                beginAt = String.valueOf((long) event.getObj1());
                endAt = String.valueOf((long) event.getObj2());
                index = (int) event.getObj3();
                pageIndex = 1;
                getCourseList(true);
                break;
        }
    }

    private void positonScreen(int position) {
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
                    getStartEndTime = new TimeUtils.GetStartEndTime().invoke(0, 6);
                } else {
                    getStartEndTime = new TimeUtils.GetStartEndTime().invoke(-6, 0);
                }
                startTime = getStartEndTime.getWithinDay();
                endTime = getStartEndTime.getYesTerday();
                break;
            case 2:
                if (showType.equals("9") && status.equals("1")) {// 9：有学员请假汇总动态
                    getStartEndTime = new TimeUtils.GetStartEndTime().invoke(0, 13);
                } else {
                    getStartEndTime = new TimeUtils.GetStartEndTime().invoke(-13, 0);
                }
                startTime = getStartEndTime.getWithinDay();
                endTime = getStartEndTime.getYesTerday();
                break;
            case 3:
                if (showType.equals("9") && status.equals("1")) {// 9：有学员请假汇总动态
                    getStartEndTime = new TimeUtils.GetStartEndTime().invoke(0, 29);
                } else {
                    getStartEndTime = new TimeUtils.GetStartEndTime().invoke(-29, 0);
                }
                startTime = getStartEndTime.getWithinDay();
                endTime = getStartEndTime.getYesTerday();
                break;
        }
        beginAt = String.valueOf(startTime);
        endAt = String.valueOf(endTime);
    }

    private void getTodayTime() {
        startTime = DateUtil.getStringToDate(TimeUtils.getCurTimeLong("yyyy-MM-dd 00:00"), "yyyy-MM-dd HH:mm");
        endTime = DateUtil.getStringToDate(TimeUtils.getCurTimeLong("yyyy-MM-dd 24:00"), "yyyy-MM-dd HH:mm");
        beginAt = String.valueOf(startTime);
        endAt = String.valueOf(endTime);
    }

}
