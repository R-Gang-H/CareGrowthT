package com.caregrowtht.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CourseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 二级目录的根目录的数据适配器
 *
 * @author Administrator
 */
public class RootListViewAdapter extends BaseAdapter {


    private Context context;

    private LayoutInflater inflater;

    private List<CourseEntity> items = new ArrayList<>();

    private int selectedPosition = -1;

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public RootListViewAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setItems(List<CourseEntity> data) {
        this.items.clear();
        this.items.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_course_resource, parent, false);
            holder.item_text = convertView.findViewById(R.id.tv_libName);
            holder.item_layout = convertView.findViewById(R.id.rl_course_resource);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        /**
         * 该项被选中时改变背景色
         */
        if (selectedPosition == position) {
            holder.item_text.setSelected(true);
            holder.item_layout.setBackgroundColor(Color.WHITE);
        } else {
            holder.item_text.setSelected(false);
            holder.item_layout.setBackgroundColor(Color.TRANSPARENT);
        }
        holder.item_text.setText(items.get(position).getThemeName());
        return convertView;
    }

    class ViewHolder {
        TextView item_text;
        RelativeLayout item_layout;
    }

}
