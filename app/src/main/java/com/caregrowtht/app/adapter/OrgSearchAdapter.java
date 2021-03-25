package com.caregrowtht.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.OrgEntity;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.List;

import butterknife.BindView;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

public class OrgSearchAdapter extends XrecyclerAdapter {

    Context mContext;

    @BindView(R.id.iv_logo)
    AvatarImageView ivLogo;
    @BindView(R.id.tv_org_name)
    TextView tvOrgName;

    public OrgSearchAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
        this.mContext = context;
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        OrgEntity orgEntity = (OrgEntity) datas.get(position);
        if (orgEntity.getOrgShortName() != null) {
            ivLogo.setTextAndColor(orgEntity.getOrgShortName(), mContext.getResources().getColor(R.color.b0b2b6));
        }
        GlideUtils.setGlideImg(mContext, orgEntity.getHeadImage(), 0, ivLogo);
        tvOrgName.setText(String.format("%s", TextUtils.isEmpty(orgEntity.getOrgChainName()) ?
                orgEntity.getTitle() : orgEntity.getTitle() + orgEntity.getOrgChainName()));
    }

    public void setData(List<OrgEntity> orgDatas) {
        this.datas.clear();
        this.datas.addAll(orgDatas);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_org_search;
    }
}
