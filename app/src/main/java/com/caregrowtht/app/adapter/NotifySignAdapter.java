package com.caregrowtht.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.OrgNotifyEntity;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * Created by haoruigang on 2018/4/26 16:31.
 */

public class NotifySignAdapter extends XrecyclerAdapter {

    @BindView(R.id.iv_head_icon)
    AvatarImageView ivHeadIcon;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_receipt)
    TextView tvReceipt;
    private Context activity;
    ArrayList<OrgNotifyEntity> mArrDatas = new ArrayList<>();//通知人

    private String isReceipt;// 1：需要回执 2：不需要回执

    public NotifySignAdapter(List datas, Context context, String isReceipt) {
        super(datas, context);
        this.activity = context;
        this.mArrDatas.addAll(datas);
        this.isReceipt = isReceipt;
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        Boolean isEmtity = TextUtils.equals(mArrDatas.get(position).getName(), "");
        ivHeadIcon.setTextAndColor(isEmtity ? "" : mArrDatas.get(position).getName().substring(0, 1),
                mContext.getResources().getColor(R.color.b0b2b6));
        GlideUtils.setGlideImg(activity, mArrDatas.get(position).getIcon(), 0, ivHeadIcon);
        tvName.setText(mArrDatas.get(position).getName());
        tvReceipt.setVisibility(View.GONE);
        if (isReceipt.equals("1")) {// 1：需要回执
            tvReceipt.setVisibility(View.VISIBLE);
            if (TextUtils.equals(mArrDatas.get(position).getIsReceipt(), "2")) {//是否已经回执 1：未回执 2：已回执
                tvReceipt.setText(String.format("已回执\t\t%s", DateUtil.getDate(Long.parseLong(mArrDatas.get(position).getTime()), "yyyy-MM-dd HH:mm")));
            } else if (TextUtils.equals(mArrDatas.get(position).getIsReceipt(), "1")) {
                tvReceipt.setText("未回执");
                tvReceipt.setTextColor(ResourcesUtils.getColor(R.color.blueLight));
            } else {
                tvReceipt.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mArrDatas.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_notify_card;
    }
}
