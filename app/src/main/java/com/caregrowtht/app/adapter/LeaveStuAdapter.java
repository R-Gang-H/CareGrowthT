package com.caregrowtht.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.UserEntity;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.List;

import butterknife.BindView;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

public class LeaveStuAdapter extends XrecyclerAdapter {

    @BindView(R.id.iv_author_avatar)
    AvatarImageView ivAuthorAvatar;
    @BindView(R.id.tv_author_name)
    TextView tvAuthorName;

    public LeaveStuAdapter(List datas, Context context) {
        super(datas, context);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        UserEntity entity = (UserEntity) datas.get(position);
        ivAuthorAvatar.setTextAndColor(TextUtils.isEmpty(entity.getName()) ? ""
                : entity.getName().substring(0, 1), mContext.getResources().getColor(R.color.b0b2b6));
        GlideUtils.setGlideImg(mContext, entity.getHeadImage(), 0, ivAuthorAvatar);
        tvAuthorName.setText(entity.getName());
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_leave_stu;
    }
}
