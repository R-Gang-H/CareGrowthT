package com.caregrowtht.app.adapter;


import android.content.Context;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by haoruigang on 2018-10-22 15:13:21
 * 教师分配权限
 */

public class TeacherPermisAdapter extends XrecyclerAdapter {


    @BindView(R.id.tv_statu_name)
    TextView tvStatuName;
    private ArrayList<StudentEntity> identityList = new ArrayList<>();
    private StudentEntity auditEntity = new StudentEntity();

    private int selectItem = -1;
    public String powerId;

    public TeacherPermisAdapter(List datas, Context context) {
        super(datas, context);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        if (identityList != null && identityList.size() > 0) {
            StudentEntity studentEntity = identityList.get(position);
            tvStatuName.setText(studentEntity.getPower());
            if (selectItem == position) {
                tvStatuName.setSelected(true);
            } else {
                tvStatuName.setSelected(false);
            }
            if (auditEntity != null && auditEntity.getPowerId().equals(studentEntity.getPowerId())) {
                tvStatuName.setSelected(true);
            }
            tvStatuName.setOnClickListener(view -> {
                setSelectItem(position);
                notifyDataSetChanged();
                powerId = studentEntity.getPowerId();//身份id
            });
        }
    }

    public void setData(ArrayList<StudentEntity> auditData, StudentEntity auditEntity) {
        this.identityList.clear();
        this.identityList.addAll(auditData);
        this.auditEntity = auditEntity;
        notifyDataSetChanged();
    }

    private void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
        this.auditEntity = null;//设为空,点击后不选择默认项
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return identityList.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_teacher_permis;
    }

}
