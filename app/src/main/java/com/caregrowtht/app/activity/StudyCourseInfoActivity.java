package com.caregrowtht.app.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.bumptech.glide.Glide;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.ImgLabelUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-4-8 10:55:04 课程详情
 */
public class StudyCourseInfoActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_courseName)
    TextView tvCourseName;
    @BindView(R.id.tv_courseTag)
    TextView tvCourseTag;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.tv_course_detail)
    TextView tvCourseDetail;
    private String courseId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_study_course_info;
    }

    @Override
    public void initView() {
        tvTitle.setText("课程详情");
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            getCourseInfo();//课程详情
        });
    }

    @Override
    public void initData() {
        courseId = getIntent().getStringExtra("courseId");
        getCourseInfo();
    }

    public void getCourseInfo() {
        /**
         * haoruigang on 2018-4-8 11:21:48 课程详情
         */
        HttpManager.getInstance().doCourseInfo("CourseInfoActivity", courseId,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        LogUtils.d("CourseInfoActivity onSuccess", data.getData().toString());
                        setData(data.getData().get(0));
                        refreshLayout.finishLoadmore();
                        refreshLayout.finishRefresh();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CourseInfoActivity onFail", statusCode + ":" + errorMsg);
                        refreshLayout.finishLoadmore();
                        refreshLayout.finishRefresh();
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(StudyCourseInfoActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CourseInfoActivity onError", throwable.getMessage());
                        refreshLayout.finishLoadmore();
                        refreshLayout.finishRefresh();
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

    /**
     * 课程详情 数据处理
     * haoruigang on 2018-4-8 11:57:37
     *
     * @param data
     */
    public void setData(CourseEntity data) {
        Glide.with(StudyCourseInfoActivity.this).load(data.getCourseImage()).into(ivLogo);
        tvCourseName.setText(data.getCourseName());
        tvCourseTag.setText(data.getCourseTag());
        tvAge.setText("适合" + data.getAge() + "孩子");
        ImgLabelUtils.getInstance().htmlThree(tvCourseDetail, data.getDetail());//加载Html富文本及图片
    }
}
