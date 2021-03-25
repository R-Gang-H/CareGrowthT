package com.caregrowtht.app.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.NotifyCardEntity;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * haoruigang on 2018-7-30 12:36:17
 * 新建通知卡片
 */
public class NotifyCardAdapter extends XrecyclerAdapter {

    ArrayList<NotifyCardEntity> notifyCards = new ArrayList<>();
    @BindView(R.id.img_notify)
    ImageView imgNotify;
    @BindView(R.id.tv_card_name)
    TextView tvCardName;

    public NotifyCardAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
        this.notifyCards.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        imgNotify.setImageResource(notifyCards.get(position).getNotifyImage());
        tvCardName.setText(notifyCards.get(position).getNotifyName());
    }

    @Override
    public int getItemCount() {
        return notifyCards.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_new_notify;
    }
}
