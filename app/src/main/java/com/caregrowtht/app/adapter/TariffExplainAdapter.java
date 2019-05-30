package com.caregrowtht.app.adapter;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Date: 2019/5/6
 * Author: haoruigang
 * Description: com.caregrowtht.app.adapter
 */
public class TariffExplainAdapter extends XrecyclerAdapter {

    List<String> datas1 = new ArrayList<>();

    @BindView(R.id.ll_bg)
    LinearLayout llBg;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.total)
    TextView total;

    public TariffExplainAdapter(List datas, List datas1, Context context) {
        super(datas, context);
        this.datas1.addAll(datas1);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        llBg.setBackgroundColor(context.getResources().getColor(position % 2 == 0 ?
                R.color.color_f1f : R.color.color_e3e));
        Integer num = Integer.valueOf(datas.get(position).toString());
        Double price = Double.valueOf(datas1.get(position));
        tvNum.setText(String.format("%s人", num));
        tvPrice.setText(String.format("%s元", price));
        total.setText(String.format("%s元", num * price));
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_tariff;
    }
}
