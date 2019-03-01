package com.caregrowtht.app.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.CourseRecordAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.view.LoadingFrameView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-4-24 18:34:50
 * 课程记录
 */
public class CourseRecordActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.xrecycler_view)
    XRecyclerView recyclerView;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    private String today = DateUtil.getNetTime("yyyy-MM-dd");
    private String selectDay = DateUtil.getNetTime("yyyy-MM-dd");
    private String orgId;
    private ArrayList<CourseEntity> listData = new ArrayList<>();
    private CourseRecordAdapter mAdapter;
    private int position;

    @Override
    public int getLayoutId() {
        return R.layout.activity_course_record;
    }

    @Override
    public void initView() {
        tvTitle.setText("课程记录");
        rlNextButton.setVisibility(View.VISIBLE);
        ivTitleRight.setImageResource(R.mipmap.ic_calendar);

        orgId = getIntent().getStringExtra("orgId");
        position = getIntent().getIntExtra("position", 0);

        iniXrecyclerView(recyclerView);
        mAdapter = new CourseRecordAdapter(listData, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setPullRefreshEnabled(true);
        recyclerView.setLoadingMoreEnabled(true);
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                getCourseStat(pageIndex, true);
            }

            @Override
            public void onLoadMore() {
                pageIndex++;
                getCourseStat(pageIndex, false);
            }
        });
    }

    @Override
    public void initData() {
        getCourseStat(pageIndex, true);
    }

    @OnClick({R.id.rl_back_button, R.id.rl_next_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                setResult(RESULT_OK, getIntent().putExtras(bundle));
                finish();
                break;
            case R.id.rl_next_button:
                selectDate();
                break;
        }
    }

    /**
     * select time to update course table.
     */
    private void selectDate() {
        new TimePickerBuilder(CourseRecordActivity.this, (date, v) -> {
            selectDay = DateUtil.getDate(date.getTime() / 1000, "yyyy-MM-dd");
            getCourseStat(pageIndex, true);
        })
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("年", "月", "日", "", "", "")
                .build().show();
    }

    private void getCourseStat(int pageIndex, boolean isClear) {
        loadView.setProgressShown(true);
        //haoruigang on 2018-4-24 18:45:58 获取课程记录
        String startDate = getDayOfWeek("yyyy-MM-dd", 1);
        String endDate = getDayOfWeek("yyyy-MM-dd", 7);
        //haoruigang on 2018-4-24 19:04:15 课程记录
        HttpManager.getInstance().doCourseStat("CourseRecordActivity", orgId, startDate, endDate, pageIndex, 20,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        if (isClear) {
                            listData.clear();
                        }
                        listData.addAll(data.getData());
                        mAdapter.setData(data.getData(), isClear);
                        if (data.getData().size() > 0) {
                            loadView.delayShowContainer(true);
                        } else {
                            if (isClear) {
                                loadView.setNoShown(true);
                            } else {
                                loadView.delayShowContainer(true);
                            }
                        }

                        recyclerView.loadMoreComplete();
                        recyclerView.refreshComplete();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourseRecordActivity.this);
                        } else {
                            U.showToast(errorMsg);
                            loadView.setErrorShown(true, v -> getCourseStat(1, true));
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        loadView.setErrorShown(true, v -> getCourseStat(1, true));
                    }
                });
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
        int d = 0;
        if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
            d = -6;
        } else {
            d = 2 - cal.get(Calendar.DAY_OF_WEEK);
        }
        cal.add(Calendar.DAY_OF_WEEK, d);
        cal.add(Calendar.DAY_OF_WEEK, which - 1);
        String day = new SimpleDateFormat(type).format(cal.getTime());
        return day;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) {
        if ((paramKeyEvent.getAction() == 0)
                && (paramKeyEvent.getKeyCode() == 4)) {

            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            setResult(RESULT_OK, getIntent().putExtras(bundle));
            finish();
        }
        return super.dispatchKeyEvent(paramKeyEvent);
    }
}
