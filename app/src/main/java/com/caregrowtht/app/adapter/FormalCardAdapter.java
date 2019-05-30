package com.caregrowtht.app.adapter;

import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.StudentDetailsActivity;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * Created by haoruigang on 2018/4/26 16:31.
 */

public class FormalCardAdapter extends CommonAdapter {

    private final String statu;//(3：非活跃待确认 4：非活跃历史(非活跃))|""搜索学员
    private Activity activity;
    List<StudentEntity> mArrDatas = new ArrayList<>();

    FormalCardAdapter(Activity activity, int layoutId, List datas, String statu) {
        super(activity, layoutId, datas);
        this.activity = activity;
        this.mArrDatas.addAll(datas);
        this.statu = statu;
    }

    @Override
    protected void convert(ViewHolder viewHolder, Object item, int position) {
        final StudentEntity entity = mArrDatas.get(position);
        final RelativeLayout rlItem = viewHolder.getView(R.id.rl_item);
        final AvatarImageView ivHeadIcon = viewHolder.getView(R.id.iv_head_icon);
        ivHeadIcon.setTextAndColor(TextUtils.isEmpty(entity.getStuName()) ? "" :
                        entity.getStuName().substring(0, 1),
                mContext.getResources().getColor(R.color.b0b2b6));
        GlideUtils.setGlideImg(activity, entity.getStuIcon(), 0, ivHeadIcon);
        TextView tvName = viewHolder.getView(R.id.tv_name);
        TextView tvStatus = viewHolder.getView(R.id.tv_status);
        View vLine = viewHolder.getView(R.id.v_line);
        tvName.setText(entity.getStuName());
        ImageView ivCllon = viewHolder.getView(R.id.iv_cllon);
        if (!TextUtils.isEmpty(entity.getIsStar()) && entity.getIsStar().equals("1")) {
            ivCllon.setImageResource(R.mipmap.ic_cllo_true);
        } else {
            ivCllon.setImageResource(R.mipmap.ic_cllo_false);
        }
        ImageView ivMobile = viewHolder.getView(R.id.iv_mobile);
        if (!TextUtils.isEmpty(entity.getAppIn()) && entity.getAppIn().equals("1")) {
            ivMobile.setImageResource(R.mipmap.ic_formal_mobile_true);
        } else {
            ivMobile.setImageResource(R.mipmap.ic_formal_mobile_false);
        }
        ImageView ivWechat = viewHolder.getView(R.id.iv_wechat);
        if (!TextUtils.isEmpty(entity.getWechatIn())) {
            ivWechat.setImageResource(R.mipmap.ic_formal_wechat_true);
        } else {
            ivWechat.setImageResource(R.mipmap.ic_formal_wechat_false);
        }
        if (TextUtils.equals(statu, "3")) {
            ivMobile.setVisibility(View.GONE);
            tvStatus.setVisibility(View.VISIBLE);
            switch (entity.getStatus()) {//状态 0待审核 1活跃 2卡用完了 3卡过期了 4未办卡 5非活跃 6未审核通过
                case "0":
                    tvStatus.setText("待审核");
                    break;
                case "1":
                    tvStatus.setText("活跃");
                    break;
                case "2":
                    tvStatus.setText("卡用完了");
                    break;
                case "3":
                    tvStatus.setText("过期");
                    break;
                case "4":
                    tvStatus.setText("无卡");
                    break;
                case "5":
                    tvStatus.setText("非活跃");
                    break;
                case "6":
                    tvStatus.setText("未审核通过");
                    break;
            }
            vLine.setVisibility(View.GONE);
        } else if (TextUtils.equals(statu, "4")) {
            ivMobile.setVisibility(View.GONE);
            tvStatus.setVisibility(View.GONE);
            vLine.setVisibility(View.GONE);
        }
        ivCllon.setOnClickListener(view -> {
            signStar(entity);
        });
        rlItem.setOnClickListener(view -> {
            if (TextUtils.equals(statu, "3")) {// || TextUtils.equals(statu, "4")
                showCardDialog(entity);
            } else {//""搜索学员
                activity.startActivity(new Intent(activity, StudentDetailsActivity.class)
                        .putExtra("StudentEntity", entity));
            }
        });
    }

    private void signStar(StudentEntity entity) {
        //20.标星/取消标星学员
        HttpManager.getInstance().doSignStar("FormalCardAdapter", entity.getStuId(),
                UserManager.getInstance().getOrgId(),
                new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        if (!TextUtils.isEmpty(entity.getIsStar()) && entity.getIsStar().equals("1")) {
                            entity.setIsStar("2");
                        } else {
                            entity.setIsStar("1");
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("FormalCardAdapter onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout((Activity) mContext);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("FormalCardAdapter throwable", throwable.getMessage());
                    }
                });
    }

    /**
     * 非活跃学员卡片操作弹框
     */
    private void showCardDialog(final StudentEntity entity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(activity, R.layout.dialog_card_oper, null);
        final TextView tvUnbind = view.findViewById(R.id.tv_unbind);
        tvUnbind.setVisibility(View.GONE);
        final TextView tvFamiliar = view.findViewById(R.id.tv_familiar);
        tvFamiliar.setText("激活学员");
        tvFamiliar.setOnClickListener(v -> {
            //激活学员
            activity.startActivity(new Intent(activity, StudentDetailsActivity.class)
                    .putExtra("StudentEntity", entity));
            dialog.dismiss();
        });
        final TextView tvCourseCard = view.findViewById(R.id.tv_course_card);
        tvCourseCard.setText("确认非活跃");
        tvCourseCard.setOnClickListener(v -> {
            //确认非活跃 1激活 5确认非活跃 6审核不通过
            changeStudentStatus("5", entity.getStuId());
            dialog.dismiss();
        });
        final TextView tvEditCard = view.findViewById(R.id.tv_edit_card);
        tvEditCard.setText("查看详情");
        tvEditCard.setOnClickListener(v -> {
            //查看详情
            activity.startActivity(new Intent(activity, StudentDetailsActivity.class)
                    .putExtra("StudentEntity", entity));
            dialog.dismiss();
        });
        final TextView tvCancel = view.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
        //设置弹窗在底部
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
    }

    /**
     * 25. 激活/确认非活跃/审核不通过
     */
    private void changeStudentStatus(String status, String stuId) {
        String orgId = UserManager.getInstance().getOrgId();
        HttpManager.getInstance().doChangeStudentStatus("FormalCardAdapter", orgId,
                stuId, status, new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_ACTIVE_STU));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("FormalCardAdapter onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout((Activity) mContext);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("FormalCardAdapter throwable", throwable.getMessage());
                    }
                });
    }
}
