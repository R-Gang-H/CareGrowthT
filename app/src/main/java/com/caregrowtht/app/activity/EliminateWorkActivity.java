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
import com.caregrowtht.app.adapter.EliminateWorkAdapter;
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
 * 人工消课提醒
 */
public class EliminateWorkActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
    @BindView(R.id.iv_title_right)
    ImageView ivTitleRight;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_pending)
    TextView tvPending;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    @BindView(R.id.recycler_view)
    XRecyclerView recyclerView;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    private List<CourseEntity> courseData = new ArrayList<>();
    EliminateWorkAdapter adapter;

    MessageEntity msgEntity;
    private String orgId;
    private String isMy = "1";// 1 我的课程 2机构课程
    private String beginAt = "0", endAt = "0";
    private long todayTime;// 今天的时间戳
    private String Url;
    private String status = "1";// 1：待处理 2：已完成
    private String showType;

    @Override
    public int getLayoutId() {
        return R.layout.activity_eliminate_work;
    }

    @Override
    public void initView() {
        tvTitle.setText("人工消课提醒");
        rlNextButton.setVisibility(View.VISIBLE);
        ivTitleRight.setImageResource(R.mipmap.ic_screen_right);
        iniXrecyclerView(recyclerView);
        recyclerView.setLoadingMoreEnabled(true);
        recyclerView.setPullRefreshEnabled(true);
        adapter = new EliminateWorkAdapter(courseData, this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                getOrgManualList(true);
            }

            @Override
            public void onLoadMore() {
                pageIndex++;
                getOrgManualList(false);
            }
        });
    }

    @Override
    public void initData() {
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (StrUtils.isNotEmpty(msgEntity)) {
            orgId = msgEntity.getOrgId();
            showType = msgEntity.getType();
            UserManager.getInstance().setOrgId(orgId);
        }
        todayTime = TimeUtils.getCurTimeLong() / 1000;
        endAt = String.valueOf(todayTime);
        Url = Constant.GETORGMANUALLIST;
        getOrgManualList(true);
    }

    private void getOrgManualList(boolean isClear) {
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
                        adapter.setData(courseData, status);
                        if (courseData.size() > 0) {
                            loadView.delayShowContainer(true);
                        } else {
                            if (isClear) {
                                loadView.setNoShown(true);
                            } else {
                                loadView.delayShowContainer(true);
                            }
                        }
                        if (isClear) {
                            recyclerView.refreshComplete();
                        } else {
                            recyclerView.loadMoreComplete();
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("EliminateWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(EliminateWorkActivity.this);
                        } else {
                            loadView.setErrorShown(true, v -> getOrgManualList(true));
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("EliminateWorkActivity onError", throwable.getMessage());
                        loadView.setErrorShown(true, v -> getOrgManualList(true));
                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.rl_next_button, R.id.tv_pending, R.id.tv_complete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.rl_next_button:
                startActivity(new Intent(this, ScreenActivity.class)
                        .putExtra("type", showType)
                        .putExtra("status", status));
                break;
            case R.id.tv_pending:
                pageIndex = 1;
                status = "1";// 待处理
                tvPending.setBackgroundResource(R.mipmap.ic_blue_levae);
                tvPending.setTextColor(ResourcesUtils.getColor(R.color.white));
                tvComplete.setBackground(null);
                tvComplete.setTextColor(ResourcesUtils.getColor(R.color.blueLight));
                getOrgManualList(true);
                break;
            case R.id.tv_complete:
                pageIndex = 1;
                status = "2";// 已完成
                tvPending.setBackground(null);
                tvPending.setTextColor(ResourcesUtils.getColor(R.color.blueLight));
                tvComplete.setBackgroundResource(R.mipmap.ic_blue_levae);
                tvComplete.setTextColor(ResourcesUtils.getColor(R.color.white));
                getOrgManualList(true);
                break;
        }
    }

    @Override
    public void setOnItemClickListener(View view, int position) {

    }

    @Override
    public void onEvent(ToUIEvent event) {
        pageIndex = 1;
        switch (event.getWhat()) {
            case ToUIEvent.SET_SCREEN_LES:
                isMy = (String) event.getObj();
                beginAt = String.valueOf((long) event.getObj1());
                endAt = String.valueOf((long) event.getObj2());
                getOrgManualList(true);
                break;
            case ToUIEvent.REFERSH_ELIMINATE:
                getOrgManualList(true);
                break;
        }
    }
}
