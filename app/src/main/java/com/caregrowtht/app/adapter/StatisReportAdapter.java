package com.caregrowtht.app.adapter;

import android.content.Context;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

public class StatisReportAdapter extends XrecyclerAdapter {

    List<MessageEntity> reportData = new ArrayList<>();

    public StatisReportAdapter(List datas, Context context) {
        super(datas, context);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {

    }

    public void setData(List<MessageEntity> reportData) {
        this.reportData.clear();
        this.reportData.addAll(reportData);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return reportData.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_statis_report;
    }
}
