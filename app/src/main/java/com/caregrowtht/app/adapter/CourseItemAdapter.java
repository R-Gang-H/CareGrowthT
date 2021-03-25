package com.caregrowtht.app.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.CourserTableActivity;
import com.caregrowtht.app.activity.OrderTableActivity;
import com.caregrowtht.app.activity.SchoolWorkLeagueActivity;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.TimeUtils;

import java.util.ArrayList;

/**
 * Created by haoruigang on 2018/4/23 17:53.
 * 课程表卡片
 */

public class CourseItemAdapter extends BaseAdapter {

    private Activity mActivity;
    private final ArrayList<CourseEntity> listData;
    private int cardType = 1;//判断课程卡点击跳转 1:点击课表放大 2:选择排课班级里的成员 3:预约课提醒
    private int type = 1;//1：我的课表 2：机构课表 3：跨机构课表

    CourseItemAdapter(Activity mActivity, ArrayList<CourseEntity> listData, int cardType, int type) {
        this.mActivity = mActivity;
        this.listData = listData;
        this.cardType = cardType;
        this.type = type;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int i) {
        return listData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint({"ViewHolder", "ResourceType"})
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_course, parent, false);
        final CourseEntity courseModel = listData.get(position);
        RelativeLayout rlCardFront = convertView.findViewById(R.id.rl_card_front);
        ImageView ivStatus = convertView.findViewById(R.id.check_course);
        TextView tvCourseName = convertView.findViewById(R.id.tv_course_name);
        ImageView ivBlueG = convertView.findViewById(R.id.iv_blue_g);
        TextView tvStartTime = convertView.findViewById(R.id.tv_start_time);
        TextView tvEndTime = convertView.findViewById(R.id.tv_end_time);
        TextView tvOrgName = convertView.findViewById(R.id.tv_org_name);

        rlCardFront.setBackgroundColor(Color.parseColor(courseModel.getColor3()));
        ivStatus.setBackgroundColor(Color.parseColor(courseModel.getColor()));

        convertView.setOnClickListener(v -> {
            //点击课表放大
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation
                    (mActivity, v, mActivity.getString(R.string.transition_name));
            if (cardType == 1) {
                //点击课表放大
                mActivity.startActivity(new Intent(mActivity, CourserTableActivity.class)
                        .putExtra("courseModel", courseModel), options.toBundle());
            } else if (cardType == 2) {
                //选择排课班级成员
                mActivity.startActivity(new Intent(mActivity, SchoolWorkLeagueActivity.class)
                        .putExtra("courseModel", courseModel), options.toBundle());
            } else if (cardType == 3) {
                //预约课提醒
                mActivity.startActivity(new Intent(mActivity, OrderTableActivity.class)
                        .putExtra("courseModel", courseModel), options.toBundle());
            }

        });
        tvCourseName.setText(courseModel.getCourseName());
        long startTime = Long.parseLong(courseModel.getStartAt());
        long endTime = Long.parseLong(courseModel.getEndAt());
        tvStartTime.setText(TimeUtils.getDateToString(startTime, "HH:mm"));
        tvEndTime.setText(TimeUtils.getDateToString(endTime, "HH:mm"));

        String isOrder = courseModel.getIsOrder();//1：该节课可以预约 2：该节课不可以预约

        if (type == 1) {// 我的课程
            tvOrgName.setText(!TextUtils.isEmpty(courseModel.getStudent()) ?
                    String.format("%s..等", courseModel.getStudent()) : "");
        } else if (type == 2) {// 机构课程
            tvOrgName.setText(!TextUtils.isEmpty(courseModel.getTeacher()) ?
                    String.format("%s", courseModel.getTeacher()) : "");
        } else if (type == 3) {// 跨机构课表
            tvOrgName.setText(courseModel.getOrgName());
        } else if (type == 0) {// 预约课提醒
            tvOrgName.setText(courseModel.getTeacher());
            ivBlueG.setVisibility(isOrder.equals("1") ? View.VISIBLE : View.GONE);
        }
        if (StrUtils.isNotEmpty(isOrder)) {
            rlCardFront.setBackgroundColor(Color.parseColor(isOrder.equals("1") ? courseModel.getColor3() : courseModel.getColor4()));
            ivStatus.setBackgroundColor(Color.parseColor(isOrder.equals("1") ? courseModel.getTintColor() : courseModel.getColor()));
        }
        return convertView;
    }
}
