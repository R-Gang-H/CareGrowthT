package com.caregrowtht.app.adapter;

import android.app.Activity;
import android.widget.TextView;

import com.android.library.view.MyGridView;
import com.caregrowtht.app.R;
import com.caregrowtht.app.user.MultipleItem;
import com.caregrowtht.app.model.StudentEntity;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by haoruigang on 2018/5/4 14:35.
 * 筛选条件适配器
 */

public class ScreenAdapter extends BaseMultiItemQuickAdapter<MultipleItem, BaseViewHolder> {

    private Activity activity;

    private StudentEntity studentList;

    public ScreenAdapter(Activity mActivity, List<MultipleItem> data, StudentEntity studentList) {
        super(data);
        this.activity = mActivity;
        this.studentList = studentList;
        addItemType(MultipleItem.TYPE_ORIGIN, R.layout.item_screen);
        addItemType(MultipleItem.TYPE_STATUS, R.layout.item_screen);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultipleItem item) {
        if (studentList == null || studentList.getOriginList().size() == 0 || studentList.getStatusList().size() == 0) {
            return;
        }
        TextView tvTitle = helper.getView(R.id.tv_title_name);
        MyGridView myGridView = helper.getView(R.id.gv_tch);
        switch (helper.getItemViewType()) {
            case MultipleItem.TYPE_ORIGIN:
                tvTitle.setText("来源");
                myGridView.setAdapter(new OriginAdapter(activity, R.layout.item_screen_son, studentList.getOriginList()));
                break;
            case MultipleItem.TYPE_STATUS:
                tvTitle.setText("当前状态");
                myGridView.setAdapter(new StatusAdapter(activity, R.layout.item_screen_son, studentList.getStatusList()));
                break;
        }
    }

    public void setData(StudentEntity studentList) {
        this.studentList = studentList;
        notifyDataSetChanged();
    }
}
