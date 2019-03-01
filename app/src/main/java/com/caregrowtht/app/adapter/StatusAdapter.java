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
 * 当前状态
 */

public class StatusAdapter extends CommonAdapter {

    private List<StudentEntity.StatusList> statusList = new ArrayList<>();

    private TextView textView;
    private int selectItem = -1;

    public StatusAdapter(Context context, int layoutId, List statusList) {
        super(context, layoutId, statusList);
        StudentEntity.StatusList statusEntity = new StudentEntity.StatusList();
        statusEntity.setStatusId("");
        statusEntity.setStatusName("全部");
        statusList.add(0, statusEntity);
        this.statusList.addAll(statusList);
    }

    @Override
    protected void convert(ViewHolder viewHolder, Object item, int position) {
        textView = viewHolder.getView(R.id.tv_statu_name);
        if (statusList != null && statusList.size() > 0) {
            textView.setText(statusList.get(position).getStatusName());
            if (selectItem == -1 && TextUtils.equals(statusList.get(position).getStatusName(), "全部")) {
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
//                U.showToast(statusList.get(position).getStatusId() + ":" + statusList.get(position).getStatusName());
                ScreenActivity.statusId = statusList.get(position).getStatusId();//状态筛选
            });
        }
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }
}
