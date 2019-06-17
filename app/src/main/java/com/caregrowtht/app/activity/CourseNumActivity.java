package com.caregrowtht.app.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.CourseNumAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.LoadingFrameView;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 开课人数提醒
 */
public class CourseNumActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    private List<CourseEntity> courseData = new ArrayList<>();
    private CourseNumAdapter adapter;
    private MessageEntity msgEntity;
    private String orgId;

    private long todayTime;// 今天的时间戳

    @Override
    public int getLayoutId() {
        return R.layout.activity_course_num;
    }

    @Override
    public void initView() {
        tvTitle.setText("学员人数提醒");
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (StrUtils.isNotEmpty(msgEntity)) {
            orgId = msgEntity.getOrgId();
            UserManager.getInstance().setOrgId(orgId);
        }
        initRecyclerView(recyclerView, true);
        adapter = new CourseNumAdapter(courseData, this, this);
        recyclerView.setAdapter(adapter);
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            getInsufficient();
        });
    }

    @Override
    public void initData() {
        todayTime = TimeUtils.getCurTimeLong() / 1000;
        getInsufficient();
    }

    private void getInsufficient() {
        HttpManager.getInstance().doGetInsufficient("CourseNumActivity"
                , orgId, new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        courseData.clear();
                        List<CourseEntity> list = data.getData();
                        if (list.size() > 0) {
                            // 筛选 条件 start
                            List<CourseEntity> todayData = new ArrayList<>();// 今天的
                            List<CourseEntity> withinData = new ArrayList<>();// 7天内
                            List<CourseEntity> beforeData = new ArrayList<>();// 7天之前
                            for (int i = 0; i < list.size(); i++) {// 筛选今天的
                                CourseEntity entity = list.get(i);
                                long begingAt = Long.valueOf(entity.getCourseBeginAt());
                                boolean isToday = TimeUtils.IsToday(DateUtil.getDate(begingAt
                                        , "yyyy-MM-dd"));// 是否为今天
                                long yesTerday = DateUtil.getStringToDate(TimeUtils.dateTiem(
                                        DateUtil.getDate(todayTime, "yyyy-MM-dd")
                                        , -1, "yyyy-MM-dd"), "yyyy-MM-dd");// 昨天
                                long withinDay = DateUtil.getStringToDate(TimeUtils.dateTiem(
                                        DateUtil.getDate(todayTime, "yyyy-MM-dd")
                                        , -7, "yyyy-MM-dd"), "yyyy-MM-dd");// 7天内的时间
                                if (isToday) {
                                    entity.setType("1");
                                    todayData.add(entity);
                                } else if (begingAt <= yesTerday && begingAt >= withinDay) {
                                    entity.setType("2");
                                    withinData.add(entity);
                                } else {
                                    entity.setType("3");
                                    beforeData.add(entity);
                                }
                            }
                            // 筛选 条件 end
                            courseData.addAll(todayData);
                            courseData.addAll(withinData);
                            courseData.addAll(beforeData);
                        }
                        adapter.setData(courseData);
                        if (courseData.size() > 0) {
                            loadView.delayShowContainer(true);
                        } else {
                            loadView.setNoShown(true);
                        }
                        if (refreshLayout.isRefreshing()) {
                            refreshLayout.finishRefresh();
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CourseNumActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourseNumActivity.this);
                        } else {
                            loadView.setErrorShown(true, v -> getInsufficient());
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CourseNumActivity onError", throwable.getMessage());
                        loadView.setErrorShown(true, v -> getInsufficient());
                    }
                });
    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        startActivity(new Intent(this, CourserActivity.class)
                .putExtra("msgEntity", msgEntity)
                .putExtra("courseId", courseData.get(position).getCourseId()));
    }
}
