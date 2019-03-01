package com.caregrowtht.app.adapter;

import android.content.Context;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by haoruigang on 2018/5/4 19:00.
 * 备注历史
 */

public class MemoHistoryAdapter extends XrecyclerAdapter {

    @BindView(R.id.tv_memo_name)
    TextView tvMemoName;
    @BindView(R.id.tv_call_comm)
    TextView tvCallComm;
    @BindView(R.id.tv_memo_data)
    TextView tvMemoData;
    @BindView(R.id.tv_memo_text)
    TextView tvMemoText;
    private ArrayList<StudentEntity.MarkList> markList = new ArrayList<>();

    public MemoHistoryAdapter(List datas, Context context) {
        super(datas, context);
        this.markList.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        tvMemoName.setText(markList.get(position).getRelativeName());
        tvCallComm.setText("(" + Constant.callArray[Integer.valueOf(markList.get(position).getCommWay())] + "沟通)");
        tvMemoData.setText(DateUtil.getDate(Long.parseLong(markList.get(position).getMarkDate()), "yyyy-MM-dd HH:mm:ss"));
        tvMemoText.setText(markList.get(position).getMark());
    }

    @Override
    public int getItemCount() {
        return markList.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_memo_history;
    }
}
