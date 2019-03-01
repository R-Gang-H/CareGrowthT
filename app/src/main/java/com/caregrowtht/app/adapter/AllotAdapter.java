package com.caregrowtht.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * 选择补位学员
 */
public class AllotAdapter extends XrecyclerAdapter {

    @BindView(R.id.iv_head_icon)
    AvatarImageView ivHeadIcon;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_nick_name)
    TextView tvNickName;
    @BindView(R.id.tv_contact_details)
    TextView tvContactDetails;
    private List<StudentEntity> stuData = new ArrayList<>();
    // 用来控制CheckBox的选中状况
    private HashMap<Integer, Boolean> isSelected = new HashMap<>();
    //选中的学员
    private HashMap<Integer, String> mStu = new HashMap<>();


    public AllotAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        final StudentEntity entity = stuData.get(position);
        ivHeadIcon.setTextAndColor(TextUtils.isEmpty(entity.getStuName()) ? "" : entity.getStuName().substring(0, 1),
                mContext.getResources().getColor(R.color.b0b2b6));
        GlideUtils.setGlideImg(context, entity.getStuIcon(), 0, ivHeadIcon);
        tvName.setText(entity.getStuName());
        tvNickName.setText(entity.getNickname());
        tvContactDetails.setText(String.format("%s:%s", entity.getRelatives(), entity.getPhoneNumber()));
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_allot;
    }

    public void update(List<StudentEntity> stuData, boolean isClear) {
        if (isClear) {
            this.stuData.clear();
        }
        this.stuData.addAll(stuData);
        // 初始化数据
        initDate();
        notifyDataSetChanged();
    }

    // 初始化isSelected的数据
    private void initDate() {
        if (stuData.size() > 0) {
            for (int i = 0; i < stuData.size(); i++) {
                getIsSelected().put(i, false);
                getStu().put(i, null);
            }
        }
    }

    @Override
    public int getItemCount() {
        return stuData.size();
    }

    public void getSelect(int position, final CheckBox tvSelectCard, final TextView tvSelectStu) {
        tvSelectCard.setSelected(!tvSelectCard.isSelected());
        if (stuData.size() > 0) {
            getIsSelected().put(position, tvSelectCard.isSelected());// 同时修改map的值保存状态
            getStu().put(position, tvSelectCard.isSelected() ? stuData.get(position).getStuId() : null);
            StringBuffer stuSb = new StringBuffer();
            String stu = "";
            if (!TextUtils.isEmpty(getStu().get(position))) {
                for (int i = 0; i < getStu().size(); i++) {
                    if (i > 0) {
                        stuSb.append(",");
                    }
                    stuSb.append(stuData.get(position).getStuName());
                }
                if (getStu().size() > 0) {
                    stu = String.format("选择\t%s\t补位", stuSb);
                }
            }
            tvSelectStu.setText(stu);
        }
    }

    public HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public HashMap<Integer, String> getStu() {
        return mStu;
    }
}
