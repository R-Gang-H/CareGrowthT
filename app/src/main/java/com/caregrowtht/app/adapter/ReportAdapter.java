package com.caregrowtht.app.adapter;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.FeedbackEntity;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemLongClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by haoruigang on 2018/4/18 18:05.
 * 举报列表
 */

public class ReportAdapter extends XrecyclerAdapter {

    @BindView(R.id.iv_select)
    ImageView ivSelect;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.et_other)
    public EditText etOther;
    @BindView(R.id.item_org)
    ConstraintLayout mOrgLayout;

    private Context activity;
    private ArrayList<FeedbackEntity> listModel = new ArrayList<>();

    // 用来控制CheckBox的选中状况
    private HashMap<Integer, Boolean> isSelected = new HashMap<>();

    public ReportAdapter(List datas, Context context, ViewOnItemClick onItemClick1, ViewOnItemLongClick onItemLongClick) {
        super(datas, context, onItemClick1, onItemLongClick);
        this.activity = context;
        listModel.addAll(datas);
        // 初始化数据
        initDate();
    }

    // 初始化isSelected的数据
    private void initDate() {
        if (listModel.size() > 0) {
            for (int i = 0; i < listModel.size(); i++) {
                getIsSelected().put(i, false);
            }
        }
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        FeedbackEntity item = listModel.get(position);
        tvName.setText(item.getReasonName());
        // 根据isSelected来设置checkbox的选中状况
        ivSelect.setSelected(getIsSelected().get(position));
        if (TextUtils.equals(item.getReasonId(), "7")) {
            etOther.setVisibility(View.VISIBLE);
        } else {
            etOther.setVisibility(View.GONE);
        }
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_report;
    }

    @Override
    public int getItemCount() {
        return listModel.size();
    }

    public void setData(ArrayList<FeedbackEntity> argList, Boolean isClear) {
        if (isClear) {
            listModel.clear();
        }
        listModel.addAll(argList);
        // 初始化数据
        initDate();
        notifyDataSetChanged();
    }

    public void getSelect(int position) {
        ivSelect.setSelected(ivSelect.isSelected() ? false : true);
        if (listModel.size() > 0) {
            for (int i = 0; i < listModel.size(); i++) {
                if (i == position) {
                    getIsSelected().put(i, true);// 同时修改map的值保存状态
                } else {
                    getIsSelected().put(i, false);// 同时修改map的值保存状态
                }
            }
        }
    }

    public HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }
}
