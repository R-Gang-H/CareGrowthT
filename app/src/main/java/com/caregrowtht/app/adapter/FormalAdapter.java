package com.caregrowtht.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.FormalActivity;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemLongClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * haoruigang on 2018-8-7 18:44:44
 * 正式学员适配器
 */
public class FormalAdapter extends XrecyclerAdapter {

    @BindView(R.id.iv_cllon)
    ImageView ivCllon;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.iv_mobile)
    ImageView ivMobile;
    @BindView(R.id.iv_wechat)
    ImageView ivWechat;

    List<StudentEntity> messageAllList = new ArrayList<>();
    private String status;//1:标星学员

    public FormalAdapter(List datas, Context context, ViewOnItemClick onItemClick1, ViewOnItemLongClick onItemLongClick) {
        super(datas, context, onItemClick1, onItemLongClick);
        this.messageAllList.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        final StudentEntity entity = messageAllList.get(position);
        final AvatarImageView ivHeadIcon = (AvatarImageView) holder.getView(R.id.iv_head_icon);
        ivHeadIcon.setTextAndColor(TextUtils.isEmpty(entity.getStuName()) ? "" : entity.getStuName().substring(0, 1),
                mContext.getResources().getColor(R.color.b0b2b6));
        GlideUtils.setGlideImg(context, entity.getStuIcon(), 0, ivHeadIcon);
        tvName.setText(entity.getStuName());
        if (!TextUtils.isEmpty(entity.getIsStar()) && entity.getIsStar().equals("1")) {
            ivCllon.setImageResource(R.mipmap.ic_cllo_true);
        } else {
            ivCllon.setImageResource(R.mipmap.ic_cllo_false);
        }
        if (!TextUtils.isEmpty(entity.getAppIn()) && entity.getAppIn().equals("1")) {
            ivMobile.setImageResource(R.mipmap.ic_formal_mobile_true);
        } else {
            ivMobile.setImageResource(R.mipmap.ic_formal_mobile_false);
        }
        if (!TextUtils.isEmpty(entity.getWechatIn())) {
            ivWechat.setImageResource(R.mipmap.ic_formal_wechat_true);
        } else {
            ivWechat.setImageResource(R.mipmap.ic_formal_wechat_false);
        }
        ivCllon.setOnClickListener(view -> {
            signStar(entity, position);
        });
    }

    private void signStar(final StudentEntity entity, final int position) {
        //20.标星/取消标星学员
        HttpManager.getInstance().doSignStar("FormalCardAdapter", entity.getStuId(),
                UserManager.getInstance().getOrgId(), new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        if (!TextUtils.isEmpty(entity.getIsStar()) && entity.getIsStar().equals("1")) {
                            if (TextUtils.equals(status, "1")) {//1:标星学员
                                ((FormalActivity) mContext).getOrgChildNum();//刷新学员总数
                                //移除
                                messageAllList.remove(position);
                                notifyItemRemoved(position + 1);
                                notifyItemRangeChanged(position + 1, messageAllList.size() - position);
                            } else {
                                entity.setIsStar("2");
                                notifyDataSetChanged();
                            }
                        } else {
                            entity.setIsStar("1");
                            notifyDataSetChanged();
                        }
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
                        LogUtils.d("FormalCardAdapter onFail", throwable.getMessage());
                    }
                });
    }

    @Override
    public int getLayoutResId() {
        return R.layout.xrecy_formal_item;
    }

    public void update(List<StudentEntity> pdata, String status) {
        messageAllList.clear();
        messageAllList.addAll(pdata);
        this.status = status;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return messageAllList.size();
    }
}
