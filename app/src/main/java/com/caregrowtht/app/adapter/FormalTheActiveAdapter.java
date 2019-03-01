package com.caregrowtht.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.android.library.view.MyGridView;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * haoruigang on 2018-10-17 12:12:25
 * 正式非活跃学员适配器
 */
public class FormalTheActiveAdapter extends XrecyclerAdapter {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.gv_tch)
    MyGridView gvTch;

    List<String> indexs = new ArrayList<>();// 3：非活跃待确认 4：非活跃历史
    HashMap<String, List<StudentEntity>> formalList = new HashMap<>();// 学员信息

    public FormalTheActiveAdapter(List datas, Context context) {
        super(datas, context);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        String statu = indexs.get(position);
        if (TextUtils.equals(statu, "3")) {
            tvTitle.setText("待确认");
        } else if (TextUtils.equals(statu, "4")) {
            tvTitle.setText("历史学员");
        }
        tvTitle.setBackgroundResource(R.mipmap.ic_blue_tag);
        tvTitle.setTextColor(mContext.getResources().getColor(R.color.white));
        gvTch.setAdapter(new FormalCardAdapter((Activity) mContext, R.layout.xrecy_formal_item, formalList.get(statu), statu));
    }

    public void update(HashMap<String, List<StudentEntity>> studentList, List<String> indexs) {
        this.formalList.clear();
        this.indexs.clear();
        this.formalList.putAll(studentList);
        this.indexs.addAll(indexs);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return formalList.size();
    }//默认循环一次

    @Override
    public int getLayoutResId() {
        return R.layout.item_formal_search;
    }
}
