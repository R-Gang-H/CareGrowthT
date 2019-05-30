package com.caregrowtht.app.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.List;

import butterknife.BindView;

/**
 * Date: 2019/5/5
 * Author: haoruigang
 * Description: com.caregrowtht.app.adapter
 */
public class CreateOrgHintAdapter extends XrecyclerAdapter {

    @BindView(R.id.rl_line)
    RelativeLayout rlLine;
    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.rl_text)
    RelativeLayout rlText;

    public CreateOrgHintAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        if (position == 0) {// 灰边间隔
            rlLine.setVisibility(View.VISIBLE);
            rlText.setVisibility(View.GONE);
        } else {// 文字
            rlLine.setVisibility(View.GONE);
            rlText.setVisibility(View.VISIBLE);
            tvText.setText(datas.get(position).toString());
        }
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_hint_text;
    }
}
