package com.caregrowtht.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CourseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 二级目录的子目录的数据适配器
 *
 * @author Administrator
 */
public class SubListViewAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;

    private List<CourseEntity.TopicsBean> items = new ArrayList<>();

    private int selectedPosition = -1;

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public SubListViewAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);

    }

    public void setItems(List<CourseEntity.TopicsBean> courseRightMenu) {
        this.items.clear();
        this.items.addAll(courseRightMenu);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
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
            holder.ivLine = convertView.findViewById(R.id.iv_line);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        /**
         * 该项被选中时改变背景色
         */
        if (selectedPosition == position) {
            holder.item_text.setSelected(true);
            holder.ivLine.setVisibility(View.VISIBLE);
        } else {
            holder.item_text.setSelected(false);
            holder.ivLine.setVisibility(View.GONE);
        }
        holder.item_text.setText(items.get(position).getTopicName());
        return convertView;
    }

    class ViewHolder {
        TextView item_text;
        ImageView ivLine;
    }

}

