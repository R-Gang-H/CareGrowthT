package com.caregrowtht.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
 * haoruigang on 2018-8-7 18:44:44
 * 正式学员搜索适配器
 */
public class FormalSearchAdapter extends XrecyclerAdapter {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.gv_tch)
    MyGridView gvTch;

    List<Integer> indexs = new ArrayList<>();
    HashMap<Integer, List<StudentEntity>> formalList = new HashMap<>();// 学员信息

    public FormalSearchAdapter(List datas, Context context) {
        super(datas, context);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        int index = indexs.get(position);
        if (index == 0) {
            tvTitle.setText("标星学员");
        }
        if (index == 1) {
            tvTitle.setText("活跃学员");
        }
        if (index == 2) {
            tvTitle.setText("非活跃学员");
        }
        ViewGroup.MarginLayoutParams source = (ViewGroup.MarginLayoutParams) tvTitle.getLayoutParams();
        source.leftMargin = 20;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(source);
        tvTitle.setLayoutParams(params);
        gvTch.setAdapter(new FormalCardAdapter((Activity) mContext, R.layout.xrecy_formal_item, formalList.get(index), ""));
    }

    /**
     * @param studentList 显示数据
     * @param indexs      当前坐标
     * @param isClear
     */
    public void update(HashMap<Integer, List<StudentEntity>> studentList, List<Integer> indexs, Boolean isClear) {
        if (isClear) {
            this.formalList.clear();
            this.indexs.clear();
        }
        this.formalList.putAll(studentList);
        this.indexs.addAll(indexs);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return formalList.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_formal_search;
    }

}
