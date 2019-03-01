package com.caregrowtht.app.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.bumptech.glide.Glide;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.OrgEntity;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemLongClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 机构活动Adapter适配器
 * Created by haoruigang on 2018/4/4 17:12.
 */

public class OrgActionAdapter extends XrecyclerAdapter {

    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_actName)
    TextView tvActName;
    @BindView(R.id.tv_actTime)
    TextView tvActTime;
    @BindView(R.id.tv_enrolment_num)
    TextView tvEnrolmentNum;

    private Context mContext;
    private ArrayList<OrgEntity> listDatas = new ArrayList<>();

    public OrgActionAdapter(List datas, Context context, ViewOnItemClick onItemClick1, ViewOnItemLongClick onItemLongClick) {
        super(datas, context, onItemClick1, onItemLongClick);
        this.mContext = context;
        this.listDatas.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        Glide.with(context).load(listDatas.get(position).getActivityImage()).into(ivLogo);
        tvActName.setText(listDatas.get(position).getActivityName());
        tvActTime.setText(DateUtil.getDate(Long.parseLong(listDatas.get(position).getActivityTime()), "yyyy-MM-dd HH:mm"));
        tvEnrolmentNum.setText(listDatas.get(position).getJoinerCount() + "已报名");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_org_action;
    }

    @Override
    public int getItemCount() {
        return listDatas.size();
    }

    public void setData(ArrayList<OrgEntity> argList, Boolean isClear) {
        if (isClear) {
            this.listDatas.clear();
        }
        this.listDatas.addAll(argList);
        notifyDataSetChanged();
    }
}
