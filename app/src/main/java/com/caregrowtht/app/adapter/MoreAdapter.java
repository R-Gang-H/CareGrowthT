package com.caregrowtht.app.adapter;

import android.content.Context;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MoreAdapter extends XrecyclerAdapter {

    private List<String> moreFunct = new ArrayList<>();

    public MoreAdapter(List datas, Context context) {
        super(datas, context);
        moreFunct.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        TextView textView = (TextView) holder.getView(R.id.text1);
        textView.setText(moreFunct.get(position));
    }

    @Override
    public int getItemCount() {
        return moreFunct.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_more;
    }
}
