package com.caregrowtht.app.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.library.MyApplication;
import com.android.library.utils.DateUtil;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.BaseActivity;
import com.caregrowtht.app.activity.FileDisplayActivity;
import com.caregrowtht.app.activity.SpaceImageDetailActivity;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.permissions.PermissionCallBackM;
import com.caregrowtht.app.view.ninegrid.NineGridLayout;
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
                    DateUtil.getDate(Long.valueOf(entity.getStart_at()), "yyyy-MM-dd HH:mm"),
                    DateUtil.getDate(Long.valueOf(entity.getEnd_at()), "HH-mm")));
            tvCourseName.setVisibility(View.VISIBLE);
            tvCourseName.setText(entity.getLessonName());
            tvSign.setVisibility(View.VISIBLE);
            tvSign.setText(entity.getOperateType().equals("1") ? "学员签到" : "机构代签到");
        } else {
            tvSign.setVisibility(View.GONE);
            String content = TextUtils.isEmpty(entity.getContent()) ? entity.getCreate_at() : entity.getContent();
            if (entity.getIs_remark().equals("1")) {// 1：备注 2：非
                tvCourseName.setVisibility(View.VISIBLE);
                tvTime.setText("备注");
                tvCourseName.setTextColor(mContext.getResources().getColor(R.color.color_9));
                tvCourseName.setText(content);
            } else {
                tvCourseName.setVisibility(View.GONE);
                tvTime.setText(content);
            }
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
            tvMoneyOrNum.setText(entity.getName());
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
                showImage(circlePicture);
            });
        }

        final LinearLayout llAtter = holder.itemView.findViewById(R.id.ll_atter);
        llAtter.setVisibility(TextUtils.isEmpty(entity.getCard_file()) ? View.GONE : View.VISIBLE);
        if (llAtter.getVisibility() == View.VISIBLE) {
            //必须先清空上次add的View
            ViewParent parent = llAtter.getParent();
            if (parent != null) {
                llAtter.removeAllViews();
            }
            String accessoryPath = entity.getCard_file();
            String[] accessory = accessoryPath.split(",");
            for (String filePath : accessory) {
                final View view = LayoutInflater.from(context).inflate(R.layout.item_atter_text, null);
                final TextView tvAtter = view.findViewById(R.id.tv_atter);
                tvAtter.setGravity(Gravity.START);
                tvAtter.setText(filePath.substring(filePath.lastIndexOf("/") + 1));
                tvAtter.setOnClickListener(v -> {
                    if (NineGridLayout.CheckIsImage(filePath)) {
                        String[] circlePicture = {filePath};
                        showImage(circlePicture);
                    } else {
                        //动态权限申请
                        ((BaseActivity) mContext).requestPermission(Constant.REQUEST_CODE_WRITE,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                mContext.getString(R.string.rationale_file),
                                new PermissionCallBackM() {
                                    @SuppressLint("MissingPermission")
                                    @Override
                                    public void onPermissionGrantedM(int requestCode, String... perms) {
                                        String fileName = filePath.toString().substring(filePath.lastIndexOf("/") + 1);
                                        FileDisplayActivity.actionStart(mContext, filePath, fileName);
                                    }

                                    @Override
                                    public void onPermissionDeniedM(int requestCode, String... perms) {
                                        LogUtils.e(mContext, "TODO: WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT);
                                    }
                                });
                    }
                });
                llAtter.addView(tvAtter);
            }
        }
    }

    private void showImage(String[] circlePicture) {
        ArrayList<String> arrImageList = new ArrayList<>();
        Collections.addAll(arrImageList, circlePicture);//转化为数组
        Intent intent = new Intent(MyApplication.getAppContext(), SpaceImageDetailActivity.class);
        intent.putExtra("images", arrImageList);//非必须
        intent.putExtra("position", 0);
        mContext.startActivity(intent);
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
