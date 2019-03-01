package com.caregrowtht.app.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.TimeEntity;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * haoruigang on 2018-7-11 10:41:47
 * 上课时段 适配器
 */
public class ClassTimeAdapter extends XrecyclerAdapter {

    @BindView(R.id.iv_del)
    ImageView ivDel;
    @BindView(R.id.tv_class_times)
    TextView tvClassTimes;

    //上课时段
    public ArrayList<TimeEntity> timeList = new ArrayList<>();
    public List<String> classTimes = new ArrayList<>();
    public String isTimeEdit = "0";

    public ClassTimeAdapter(List datas, Context context, ViewOnItemClick onItemClick) {
        super(datas, context, onItemClick);
        this.mContext = context;
        this.classTimes.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        String times = classTimes.get(position);
        tvClassTimes.setText(times);
        ivDel.setOnClickListener(v -> {
            isTimeEdit = "1";// 编辑了课程
            timeList.remove(position);
            classTimes.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, classTimes.size());
        });
    }

    @Override
    public int getItemCount() {
        return classTimes.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_class_time;
    }

}
