package com.caregrowtht.app.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.MomentMessageEntity;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemLongClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * haoruigang on 2018-7-13 16:06:05
 * 兴趣圈评论
 */
public class CommonsAdapter extends XrecyclerAdapter {

    @BindView(R.id.tv_comm_content)
    TextView tvCommContent;
    @BindView(R.id.tv_reply)
    TextView tvReply;

    public ArrayList<MomentMessageEntity.Comment> comment = new ArrayList<>();

    public CommonsAdapter(List datas, Context context, ViewOnItemClick onItemClick1, ViewOnItemLongClick onItemLongClick) {
        super(datas, context, onItemClick1, onItemLongClick);
        this.comment.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        tvCommContent.setVisibility(TextUtils.isEmpty(comment.get(position).getReplyName()) ? View.VISIBLE : View.GONE);
        tvCommContent.setText(Html.fromHtml(String.format("<font color='#69ace5'>%s:</font>\t%s", comment.get(position).getCommenterName(), comment.get(position).getContent())));
        tvReply.setVisibility(TextUtils.isEmpty(comment.get(position).getReplyName()) ? View.GONE : View.VISIBLE);
        tvReply.setText(Html.fromHtml(String.format("<font color='#69ace5'>%s</font>\t回复\t<font color='#69ace5'>%s</font>:\t%s", comment.get(position).getCommenterName(), comment.get(position).getReplyName(), comment.get(position).getContent())));
    }

    @Override
    public int getItemCount() {
        return comment.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_commons;
    }
}
