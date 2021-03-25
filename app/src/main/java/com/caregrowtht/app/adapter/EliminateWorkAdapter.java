package com.caregrowtht.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.ArtificialActivity;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * 人工消课适配器
 */
public class EliminateWorkAdapter extends XrecyclerAdapter {

    @BindView(R.id.rl_day)
    RelativeLayout rlDay;
    @BindView(R.id.tv_day)
    TextView tvDay;
    @BindView(R.id.cv_radius)
    ImageView cvRadius;
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

    List<CourseEntity> courseData = new ArrayList<>();
    private String status;// 1：待处理 2：已完成
    private String orgId;

    public EliminateWorkAdapter(List datas, Context context) {
        super(datas, context);
        orgId = UserManager.getInstance().getOrgId();
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        final CourseEntity courseEntity = courseData.get(position);
        if (position == 0 || !courseEntity.getType()
                .equals(courseData.get(position - 1).getType())) {
            rlDay.setVisibility(View.VISIBLE);
            String typeContent = "";
            switch (courseEntity.getType()) {
                case "1":
                    typeContent = "今天";
                    break;
                case "2":
                    typeContent = "过去7天";
                    break;
                case "3":
                    typeContent = "7天之前";
                    break;
                case "4":
                    typeContent = "未来7天";
                    break;
                case "5":
                    typeContent = "7天之后";
                    break;
            }
            tvDay.setText(typeContent);
        } else {
            rlDay.setVisibility(View.GONE);
        }
        long courseStartAt = Long.valueOf(courseEntity.getCourseBeginAt());
        String Month = TimeUtils.getDateToString(courseStartAt, "MM月dd日");
        String data = DateUtil.getDate(courseStartAt, "yyyy年MM月dd日");
        String week = TimeUtils.getWeekByDateStr(data);//获取周几
        String time = TimeUtils.getDateToString(courseStartAt, "HH:mm");
        String teacherName = courseEntity.getTeacherName();
        String courseName = courseEntity.getCourseName();
        tvContent.setText(String.format("%s\t\t%s\t\t%s\n%s\t\t%s",
                Month, week, time, teacherName, courseName));
        ivLevaeAuthorAvatar.setTextAndColor(TextUtils.isEmpty(courseEntity.getStudentName()) ? ""
                : courseEntity.getStudentName().substring(0, 1), mContext.getResources().getColor(R.color.b0b2b6));
        GlideUtils.setGlideImg(mContext, courseEntity.getStudentIcon(), 0, ivLevaeAuthorAvatar);
        tvLeaveAuthorName.setText(courseEntity.getStudentName());

        final TextView tvCheckDetial = holder.itemView.findViewById(R.id.tv_check_detial);
        tvEliminate.setVisibility(status.equals("1") ? View.VISIBLE : View.GONE);
        tvEliminateFailed.setVisibility(status.equals("1") ? View.VISIBLE : View.GONE);
        tvCheckDetial.setVisibility(status.equals("1") ? View.GONE : View.VISIBLE);
        tvEliminate.setOnClickListener(v -> {
            context.startActivity(new Intent(context, ArtificialActivity.class)
                    .putExtra("courseEntity", courseEntity));// 人工消课
        });
        tvCheckDetial.setOnClickListener(v -> {
            if (tvCheckDetial.getText().equals("查看消课情况")) {// 查看消课情况
                getLessonCardLog(tvCheckDetial, courseEntity);
            }
        });
    }

    public void setData(List<CourseEntity> courseData, String status) {
        this.courseData.clear();
        this.courseData.addAll(courseData);
        this.status = status;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return courseData.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_eliminate;
    }

    private void getLessonCardLog(TextView tvCheckDetial, CourseEntity courseEntity) {
        HttpManager.getInstance().doGetLessonCardLog("EliminateWorkAdapter", orgId,
                courseEntity.getCourseId(), courseEntity.getStudentId(),
                new HttpCallBack<BaseDataModel<CourseEntity>>((Activity) mContext) {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        StringBuilder sb = new StringBuilder();
                        boolean isPage = false;
                        for (CourseEntity entity : data.getData()) {
                            if (isPage) {
                                sb.append("\n");
                            }
                            sb.append(entity.getContent());
                            isPage = true;
                        }
                        tvCheckDetial.setText(sb.toString());
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("EliminateWorkAdapter onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout((Activity) mContext);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("EliminateWorkAdapter onError", throwable.getMessage());
                    }
                });
    }
}
