package com.caregrowtht.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.TextView;

import com.android.library.view.CircleImageView;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.StudentDetailsActivity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by haoruigang on 2018/4/9 11:26.
 * 获取用户所有孩子的Adapter适配器
 */

public class MyChildAdapter extends XrecyclerAdapter {

    private Context mContext;
    @BindView(R.id.iv_logo)
    CircleImageView ivLogo;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.iv_select)
    TextView ivSelect;
    private ArrayList<StudentEntity> mArrDatas = new ArrayList<>();

    public MyChildAdapter(List datas, Context context) {
        super(datas, context);
        this.mContext = context;
        mArrDatas.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        GlideUtils.setGlideImg(mContext, mArrDatas.get(position).getStuAvatar(), R.mipmap.ic_avatar_default, ivLogo);
        tvName.setText(mArrDatas.get(position).getStuName());
        tvPhone.setText(mArrDatas.get(position).getPhone());
        ivSelect.setText(TextUtils.equals(mArrDatas.get(position).getIsMember(), "0") ? "非会员" : "会员");
        ivSelect.setBackgroundResource(TextUtils.equals(mArrDatas.get(position).getIsMember(), "0") ? R.color.blueLight : R.color.greenLight);//是否为会员 0:不是 1:是
        if (TextUtils.equals(mArrDatas.get(position).getIsMember(), "1")) {
            ivLogo.setOnClickListener(view -> {
                mContext.startActivity(new Intent(mContext, StudentDetailsActivity.class)
                        .putExtra("StudentEntity", mArrDatas.get(position)));
            });
        }
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_my_child;
    }

    @Override
    public int getItemCount() {
        return mArrDatas.size();
    }

    public void setData(ArrayList<StudentEntity> mArrDatas, Boolean isClear) {
        if (isClear) {
            this.mArrDatas.clear();
        }
        this.mArrDatas.addAll(mArrDatas);
        notifyDataSetChanged();
    }

}
