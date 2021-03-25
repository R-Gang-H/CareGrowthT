package com.caregrowtht.app.activity;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.TimeUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * 人工消课详情
 */
public class EliminateDetailActivity extends BaseActivity {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_day)
    RelativeLayout rlDay;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_levae_author_avatar)
    AvatarImageView ivLevaeAuthorAvatar;
    @BindView(R.id.tv_leave_author_name)
    TextView tvLeaveAuthorName;
    @BindView(R.id.tv_eliminate_failed)
    TextView tvEliminateFailed;
    @BindView(R.id.tv_eliminate)
    TextView tvEliminate;
    @BindView(R.id.tv_check_detial)
    TextView tvCheckDetial;

    private MessageEntity msgEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_eliminate_detail;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (StrUtils.isNotEmpty(msgEntity)) {
            tvTitle.setText("人工消课");
            rlDay.setVisibility(View.GONE);
            tvEliminate.setVisibility(View.GONE);
            long courseStartAt = Long.valueOf(msgEntity.getCourseStartAt());
            String Month = TimeUtils.getDateToString(courseStartAt, "MM月dd日");
            String data = DateUtil.getDate(courseStartAt, "yyyy年MM月dd日");
            String week = TimeUtils.getWeekByDateStr(data);//获取周几
            String time = TimeUtils.getDateToString(courseStartAt, "HH:mm");
            String teacherName = msgEntity.getTeacherName();
            String courseName = msgEntity.getCourseName();
            tvContent.setText(String.format("%s\t\t%s\t\t%s\n%s\t\t%s",
                    Month, week, time, teacherName, courseName));
            ivLevaeAuthorAvatar.setTextAndColor(TextUtils.isEmpty(msgEntity.getShowName()) ? ""
                    : msgEntity.getShowName().substring(0, 1), getResources().getColor(R.color.b0b2b6));
            GlideUtils.setGlideImg(this, msgEntity.getShowHeadImage(), 0, ivLevaeAuthorAvatar);
            tvLeaveAuthorName.setText(String.format("%s", msgEntity.getShowName()));
            // status 1：已签到 2：取消签到
            tvEliminateFailed.setText(Html.fromHtml(String.format("<font color='#F34B4B'>%s</font>",
                    msgEntity.getStatus().equals("2") ? "学员取消签到" : "自动消课失败")));
            if (Integer.valueOf(msgEntity.getCount()) <= 0) {
                tvEliminateFailed.setText(Html.fromHtml(String.format(
                        "<font color='#F34B4B'>%s</font>", "无可用课时卡")));
            }
            if (StrUtils.isNotEmpty(msgEntity.getCourseList()) && msgEntity.getCourseList().size() > 0) {
                StringBuilder sb = new StringBuilder();
                boolean isPage = false;
                for (CourseEntity entity : msgEntity.getCourseList()) {
                    if (isPage) {
                        sb.append("\n");
                    }
                    sb.append(entity.getContent());
                    isPage = true;
                }
                tvCheckDetial.setVisibility(View.VISIBLE);
                tvCheckDetial.setText(sb.toString());
                tvEliminateFailed.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }
}
