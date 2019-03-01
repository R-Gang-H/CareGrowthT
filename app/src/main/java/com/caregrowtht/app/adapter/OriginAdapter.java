package com.caregrowtht.app.adapter;


import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.ScreenActivity;
import com.caregrowtht.app.model.StudentEntity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by haoruigang on 2018/5/4 14:52.
 * 来源
 */

public class OriginAdapter extends CommonAdapter {

    private List<StudentEntity.OriginList> originList = new ArrayList<>();

    private int selectItem = -1;

    public OriginAdapter(Context context, int layoutId, List originList) {
        super(context, layoutId, originList);
        StudentEntity.OriginList statusEntity = new StudentEntity.OriginList();
        statusEntity.setOriginList("");
        statusEntity.setOriginName("全部");
        originList.add(0, statusEntity);
        this.originList.addAll(originList);
    }

    @Override
    protected void convert(ViewHolder viewHolder, Object item, int position) {
        TextView textView = viewHolder.getView(R.id.tv_statu_name);
        if (originList != null && originList.size() > 0) {
            textView.setText(originList.get(position).getOriginName());
            if (selectItem == -1 && TextUtils.equals(originList.get(position).getOriginName(), "全部")) {
                textView.setSelected(true);
            } else {
                if (selectItem == position) {
                    textView.setSelected(true);
                } else {
                    textView.setSelected(false);
                }
            }
            textView.setOnClickListener(view -> {
                setSelectItem(position);
                notifyDataSetInvalidated();
//                U.showToast(originList.get(position).getOriginList() + ":" + originList.get(position).getOriginName());
                ScreenActivity.originId = originList.get(position).getOriginList();//来源筛选id
            });
        }
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }
}
