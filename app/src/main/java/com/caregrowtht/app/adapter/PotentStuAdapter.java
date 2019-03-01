package com.caregrowtht.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.PotentStuInfoActivity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by haoruigang on 2018/5/4 17:03.
 * 潜在学员
 */

public class PotentStuAdapter extends XrecyclerAdapter {

    @BindView(R.id.tv_stu_nickname)
    TextView tvStuNickname;
    @BindView(R.id.tv_author_phone)
    TextView tvAuthorPhone;
    @BindView(R.id.tv_add_time_v)
    TextView tvAddTimeV;
    @BindView(R.id.tv_comm_number_v)
    TextView tvCommNumberV;
    @BindView(R.id.tv_mark_v)
    TextView tvMarkV;
    @BindView(R.id.rl_potent_stu)
    RelativeLayout rlPotentStu;

    private ArrayList<StudentEntity> studentDatas = new ArrayList<>();

    public PotentStuAdapter(List datas, Context context) {
        super(datas, context);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        tvStuNickname.setText(studentDatas.get(position).getStuName());
        tvAuthorPhone.setText(String.format("(%s)", studentDatas.get(position).getPhone()));
        tvAddTimeV.setText(DateUtil.getDate(Long.valueOf(studentDatas.get(position).getAddDate()), "yyyy-MM-dd"));
        tvCommNumberV.setText(studentDatas.get(position).getCommCount() + "次");
        tvMarkV.setText(StrUtils.isEmpty(studentDatas.get(position).getMark()) ? "无" : studentDatas.get(position).getMark());
        rlPotentStu.setOnClickListener(view -> {
            context.startActivity(new Intent(context, PotentStuInfoActivity.class).putExtra("stuId", studentDatas.get(position).getStuId()));
        });
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_potent_stu;
    }

    @Override
    public int getItemCount() {
        return studentDatas.size();
    }

    public void setData(ArrayList<StudentEntity> studentDatas) {
        this.studentDatas = studentDatas;
        notifyDataSetChanged();
    }
}
