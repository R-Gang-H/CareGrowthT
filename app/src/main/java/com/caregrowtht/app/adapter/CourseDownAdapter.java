package com.caregrowtht.app.adapter;

import android.content.Context;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * haoruigang on 课表下拉适配器
 */
public class CourseDownAdapter extends CommonAdapter {

    List<String> datas = new ArrayList<>();
    // 用来控制CheckBox的选中状况
    private HashMap<Integer, Boolean> isSelected = new HashMap<>();
    private int type;

    private TextView ivSelect;

    public CourseDownAdapter(Context context, int layoutId, List datas, int type) {
        super(context, layoutId, datas);
        this.datas.addAll(datas);
        this.type = type;
        // 初始化数据
        initDate();
    }

    @Override
    protected void convert(ViewHolder viewHolder, Object item, int position) {
        ivSelect = viewHolder.getView(R.id.tv_course_type);
        ivSelect.setText(datas.get(position));
        // 根据isSelected来设置checkbox的选中状况
        ivSelect.setSelected(getIsSelected().get(position));
    }

    // 初始化isSelected的数据
    private void initDate() {
        if (datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
                if (i == (type - 1)) {
                    getIsSelected().put(i, true);// 同时修改map的值保存状态
                } else {
                    getIsSelected().put(i, false);// 同时修改map的值保存状态
                }
            }
        }
    }

    public void getSelect(int position) {
        ivSelect.setSelected(ivSelect.isSelected() ? false : true);
        if (datas.size() > 0) {
            for (int i = 0; i < datas.size(); i++) {
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
