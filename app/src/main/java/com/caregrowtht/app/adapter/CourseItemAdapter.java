package com.caregrowtht.app.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.CourserTableActivity;
import com.caregrowtht.app.activity.SchoolWorkLeagueActivity;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.uitil.TimeUtils;

import java.util.ArrayList;

/**
 * Created by haoruigang on 2018/4/23 17:53.
 * 课程表卡片
 */

public class CourseItemAdapter extends BaseAdapter {

    private Activity mActivity;
    private final ArrayList<CourseEntity> listData;
    private int cardType = 1;//判断课程卡点击跳转 1:点击课表放大 2:选择排课班级里的成员
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
        final CourseEntity userModel = listData.get(position);
        RelativeLayout rlCardFront = convertView.findViewById(R.id.rl_card_front);
        ImageView ivStatus = convertView.findViewById(R.id.check_course);
        TextView tvCourseName = convertView.findViewById(R.id.tv_course_name);
        TextView tvStartTime = convertView.findViewById(R.id.tv_start_time);
        TextView tvEndTime = convertView.findViewById(R.id.tv_end_time);
        TextView tvOrgName = convertView.findViewById(R.id.tv_org_name);

        rlCardFront.setBackgroundColor(Color.parseColor(userModel.getColor3()));
        ivStatus.setBackgroundColor(Color.parseColor(userModel.getColor()));

        convertView.setOnClickListener(v -> {
            //点击课表放大
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation
                    (mActivity, v, mActivity.getString(R.string.transition_name));
            if (cardType == 1) {
                //点击课表放大
                mActivity.startActivity(new Intent(mActivity, CourserTableActivity.class)
                        .putExtra("userModel", userModel), options.toBundle());
            } else if (cardType == 2) {
                //选择排课班级成员
                mActivity.startActivity(new Intent(mActivity, SchoolWorkLeagueActivity.class)
                        .putExtra("userModel", userModel), options.toBundle());
            }

        });
        tvCourseName.setText(userModel.getCourseName());
        long startTime = Long.parseLong(userModel.getStartAt());
        long endTime = Long.parseLong(userModel.getEndAt());
        tvStartTime.setText(TimeUtils.getDateToString(startTime, "HH:mm"));
        tvEndTime.setText(TimeUtils.getDateToString(endTime, "HH:mm"));
        if (type == 1 || type == 2) {
            tvOrgName.setText(!TextUtils.isEmpty(userModel.getStudent()) ?
                    String.format("%s..等", userModel.getStudent()) : "");
        } else {
            tvOrgName.setText(userModel.getOrgName());
        }
        return convertView;
    }
}
