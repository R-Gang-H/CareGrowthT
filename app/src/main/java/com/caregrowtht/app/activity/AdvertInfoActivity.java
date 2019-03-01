package com.caregrowtht.app.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.MomentMessageEntity;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.ImgLabelUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-4-8 10:55:04 广告详情
 */
public class AdvertInfoActivity extends BaseActivity {

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
    private MomentMessageEntity pData;

    @Override
    public int getLayoutId() {
        return R.layout.activity_study_course_info;
    }

    @Override
    public void initView() {
        tvTitle.setText("广告详情");
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            refreshLayout.finishLoadmore();
            refreshLayout.finishRefresh();
        });
    }

    @Override
    public void initData() {
        //解决NetworkOnMainThreadException异常
        ImgLabelUtils.getInstance().struct();

        pData = (MomentMessageEntity) getIntent().getSerializableExtra("pData");
        setData(pData);
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
     * 广告详情 数据处理
     * haoruigang on 2018-4-8 11:57:37
     *
     * @param data
     */
    public void setData(MomentMessageEntity data) {
        GlideUtils.setGlideImg(AdvertInfoActivity.this, pData.getPngOravis(), R.mipmap.ic_org_action, ivLogo);
        tvCourseName.setText(data.getTitle());
        tvCourseTag.setText(TimeUtils.GetFriendlyTime(pData.getTime()));
        tvAge.setText("");
        ImgLabelUtils.getInstance().htmlThree(tvCourseDetail, data.getContent());//加载Html富文本及图片
    }
}
