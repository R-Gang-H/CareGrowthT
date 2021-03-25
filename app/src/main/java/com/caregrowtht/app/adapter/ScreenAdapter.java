package com.caregrowtht.app.adapter;

import android.content.Context;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by haoruigang on 2018/5/4 14:35.
 * 筛选条件适配器
 */

public class ScreenAdapter extends XrecyclerAdapter {

    @BindView(R.id.tv_time)
    TextView tvTime;

    // 用来控制CheckBox的选中状况
    private HashMap<Integer, Boolean> isSelected = new HashMap<>();

    public ScreenAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
        // 初始化数据
        initDate();
        notifyDataSetChanged();
    }

    public void setData(List datas) {
        this.datas.clear();
        this.datas.addAll(datas);
        initDate();
        notifyDataSetChanged();
    }

    // 初始化isSelected的数据
    private void initDate() {
        if (datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                getIsSelected().put(i, false);
            }
        }
    }


    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        tvTime.setText(datas.get(position).toString());
        // 根据isSelected来设置checkbox的选中状况
        tvTime.setSelected(getIsSelected().get(position));
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
            notifyDataSetChanged();
        }
    }

    public HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_time_frame;
    }
}
