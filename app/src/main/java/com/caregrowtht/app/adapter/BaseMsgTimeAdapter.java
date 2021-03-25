package com.caregrowtht.app.adapter;

import android.content.Context;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.PriceMsgEntity;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Date: 2019/5/7
 * Author: haoruigang
 * Description: com.caregrowtht.app.adapter
 */
public class BaseMsgTimeAdapter extends XrecyclerAdapter {

    @BindView(R.id.rb_year)
    Button rbYear;

    // 用来控制CheckBox的选中状况
    private HashMap<Integer, Boolean> isSelected = new HashMap<>();

    public BaseMsgTimeAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
    }

    // 初始化isSelected的数据
    public void initDate() {
        if (datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                getIsSelected().put(i, false);
            }
        }
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        PriceMsgEntity entity = (PriceMsgEntity) datas.get(position);
        rbYear.setSelected(getIsSelected().get(position));
        rbYear.setText(String.format("%s\t月", entity.getBuy_month()));
    }

    public void setData(List datas) {
        this.datas.clear();
        this.datas.addAll(datas);
        // 初始化数据
        initDate();
        notifyDataSetChanged();
    }

    public void getSelect(int position) {
        if (datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                if (i == position) {
                    getIsSelected().put(i, true);// 同时修改map的值保存状态
                } else {
                    getIsSelected().put(i, false);// 同时修改map的值保存状态
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_base_msg_time;
    }

    public HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

}
