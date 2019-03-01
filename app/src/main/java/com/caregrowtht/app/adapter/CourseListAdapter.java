package com.caregrowtht.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.library.MyApplication;
import com.android.library.utils.DateUtil;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.SpaceImageDetailActivity;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

public class CourseListAdapter extends XrecyclerAdapter {


    private List<CourseEntity> messageAllList = new ArrayList<>();
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_course_name)
    TextView tvCourseName;
    @BindView(R.id.tv_do_time)
    TextView tvDoTime;
    @BindView(R.id.tv_money_or_num)
    TextView tvMoneyOrNum;
    @BindView(R.id.tv_sign)
    TextView tvSign;

    public CourseListAdapter(List datas, Context context) {
        super(datas, context);
        this.messageAllList.addAll(datas);
    }

    //    类型 1：次卡 2：储值卡 3：年卡 4：折扣卡   |
    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        final CourseEntity entity = messageAllList.get(position);
        if (!TextUtils.isEmpty(entity.getOperateType())) {
            tvTime.setText(String.format("%s-%s",
                    DateUtil.getDate(Long.valueOf(entity.getStart_at()), "MM月dd日 HH-mm"),
                    DateUtil.getDate(Long.valueOf(entity.getEnd_at()), "HH-mm")));
            tvCourseName.setVisibility(View.VISIBLE);
            tvCourseName.setText(entity.getLessonName());
            tvSign.setVisibility(View.VISIBLE);
        } else {
            tvSign.setVisibility(View.GONE);
            tvCourseName.setVisibility(View.GONE);
            tvTime.setText(entity.getCreate_at());
        }
        if (TextUtils.isEmpty(entity.getUseNum()) && TextUtils.isEmpty(entity.getUsePrice())) {
            tvMoneyOrNum.setVisibility(View.GONE);
        } else {
            tvMoneyOrNum.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(entity.getUseNum()) && Float.parseFloat(entity.getUseNum()) > 0) {
            tvMoneyOrNum.setText(String.format("%s\t-%s次", entity.getName(), entity.getUseNum()));
        } else if (!TextUtils.isEmpty(entity.getUserBackPrice()) && Float.parseFloat(entity.getUserBackPrice()) > 0) {
            tvMoneyOrNum.setText(String.format("%s\t-%s元", entity.getName(),
                    String.valueOf(Integer.valueOf(entity.getUserBackPrice()) / 100)));
        } else {
            tvMoneyOrNum.setText("-0");
        }
        tvDoTime.setText(String.format("%s\t%s", entity.getOperateName(),
                DateUtil.getDate(Long.valueOf(entity.getCreate_at()), "yyyy-MM-dd HH:mm")));
        String signSheet = entity.getSignSheet();
        if (!TextUtils.isEmpty(signSheet)) {// 不为空可点击
            holder.itemView.setOnClickListener(v -> {
                String[] circlePicture = {};
                if (!TextUtils.isEmpty(signSheet)) {
                    circlePicture = signSheet.split("#");
                }
                ArrayList<String> arrImageList = new ArrayList<>();
                Collections.addAll(arrImageList, circlePicture);//转化为数组
                Intent intent = new Intent(MyApplication.getAppContext(), SpaceImageDetailActivity.class);
                intent.putExtra("images", arrImageList);//非必须
                intent.putExtra("position", 0);
                mContext.startActivity(intent);
            });
        }
    }

    @Override
    public int getLayoutResId() {
        return R.layout.recycle_course_item;
    }

    public void update(List<CourseEntity> pdata, Boolean isClear) {
        if (isClear) {
            messageAllList.clear();
        }
        messageAllList.addAll(pdata);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return messageAllList.size();
    }
}
