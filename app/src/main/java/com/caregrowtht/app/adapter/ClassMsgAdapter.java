package com.caregrowtht.app.adapter;

import android.content.Context;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * haoruigang on 2018-10-29 15:03:55
 * 教室管理 适配器
 */
public class ClassMsgAdapter extends XrecyclerAdapter {

    List<CourseEntity> classRoomList = new ArrayList<>();

    @BindView(R.id.tv_room_name)
    TextView tvRoomName;

    public ClassMsgAdapter(List datas, Context context) {
        super(datas, context);
        this.classRoomList.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        tvRoomName.setText(classRoomList.get(position).getClassroomName());
    }

    public void setData(List<CourseEntity> classRoomList) {
        this.classRoomList.clear();
        this.classRoomList.addAll(classRoomList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return classRoomList.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_class_romm;
    }
}
