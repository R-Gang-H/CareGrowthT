package com.caregrowtht.app.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 数据初始化 适配器
 */
public class InitDataAdapter extends XrecyclerAdapter {

    @BindView(R.id.iv_type)
    ImageView ivType;
    @BindView(R.id.tv_add_teacher)
    TextView tvAddTeacher;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    private int[] initRes = {};
    private String[] initName = {};
    private List<String> initSize = new ArrayList<>();

    public InitDataAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        ivType.setImageResource(initRes[position]);
        tvAddTeacher.setText(initName[position]);
        if (Integer.valueOf(initSize.get(position)) > 0) {
            tvStatus.setText("已有数据");
            tvStatus.setBackgroundResource(R.mipmap.ic_pending);
        }
    }

    public void setData(int[] initRes, String[] initName, List<String> initSize) {
        this.initRes = initRes;
        this.initName = initName;
        this.initSize.clear();
        this.initSize.addAll(initSize);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return initRes.length;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_init_data;
    }
}
