package com.caregrowtht.app.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.BaseActivity;
import com.caregrowtht.app.view.xrecyclerview.ItemOffsetDecoration;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * Date: 2019/5/5
 * Author: haoruigang
 * Description: com.caregrowtht.app.adapter
 */
public class FCTListAdapter extends XrecyclerAdapter {

    @BindView(R.id.tv_teacher_app)
    TextView tvTeacherApp;
    @BindView(R.id.tv_spot)
    TextView tvSpot;
    @BindView(R.id.tv_spot_fct)
    TextView tvSpotFct;
    @BindView(R.id.tv_rule)
    TextView tvRule;
    @BindView(R.id.tv_rule_fct)
    TextView tvRuleFct;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    FCTAdapter adapter;

    public FCTListAdapter(List datas, Context context) {
        super(datas, context);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        int spotColor = 0, spotBg = 0;
        Drawable drawable = null, drawable1 = null;
        ((BaseActivity) context).initRecyclerGrid(recyclerView, 2);
        String[] appFCT = new String[0];
        if (position == 0) {
            spotBg = R.mipmap.ic_teacher_app;
            spotColor = context.getResources().getColor(R.color.blueLight);
            drawable = context.getResources().getDrawable(R.mipmap.ic_app_spot);
            tvSpotFct.setText(R.string.string_app_spot_fct);
            drawable1 = context.getResources().getDrawable(R.mipmap.ic_app_rule);
            appFCT = new String[]{"课程表周视图", "添加教室", "按周/次周期排课", "添加课程种类", "记录学员出勤情况",
                    "排课与课时卡关系", "自动消课", "新购买卡/充值续费", "添加老师", "查看学员消课记录", "添加学员",
                    "编辑课时卡", "添加课时卡", "......"};
        } else if (position == 1) {
            spotBg = R.mipmap.ic_teacher_pc;
            spotColor = context.getResources().getColor(R.color.greenLight);
            drawable = context.getResources().getDrawable(R.mipmap.ic_pc_spot);
            tvSpotFct.setText(R.string.string_app_spot_fct);
            drawable1 = context.getResources().getDrawable(R.mipmap.ic_pc_rule);
            appFCT = new String[]{"排课与课时卡关系", "课程表月/周/天视图", "新购买卡/充值续费", "按周/次周期排课",
                    "查看学员详情", "记录学员出勤情况", "编辑课时卡", "自动消课/取消消课", "多维度数据统计", "添加老师",
                    "数据初始化、添加学员", "添加学员", "批量签到、请假", "添加课时卡", "人工消课", "添加教室",
                    "办理教师离职", "添加课程种类"};
            tvRuleFct.setText("机构主页装修\n信息汇总(课程预约/反馈)\n排课/班级汇总\n机构主页装修\n" +
                    "教学内容管理(大纲编辑增删)\n功能设置(学员请假/课时卡提醒)\n权限管理(跨机构管理/员工管理)\n" +
                    "续费、升级");
        } else if (position == 2) {
            spotBg = R.mipmap.ic_teacher_xcx;
            spotColor = context.getResources().getColor(R.color.color_caa0);
            drawable = context.getResources().getDrawable(R.mipmap.ic_xcx_spot);
            tvSpotFct.setText(R.string.string_xcx_spot_fct);
            drawable1 = context.getResources().getDrawable(R.mipmap.ic_xcx_rule);
            appFCT = new String[]{"签到", "各类提醒：", "请假", "上课前提醒", "约课", "签到提醒", "等位",
                    "续费提醒", "抢课", "机构通知提醒", "课时记录", "查看课程反馈", "提交作业", "......"};
        }
        adapter = new FCTAdapter(Arrays.asList(appFCT), context);
        recyclerView.setAdapter(adapter);
        final int spacing = context.getResources().getDimensionPixelOffset(R.dimen.margin_size_10);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(0, 0, 0, spacing));
        tvTeacherApp.setBackgroundResource(spotBg);
        tvTeacherApp.setText(datas.get(position).toString());
        tvSpot.setTextColor(spotColor);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }
        tvSpot.setCompoundDrawables(drawable, null, null, null);
        tvRule.setTextColor(spotColor);
        if (drawable1 != null) {
            drawable1.setBounds(0, 0, drawable1.getIntrinsicWidth(), drawable1.getIntrinsicHeight());
        }
        tvRule.setCompoundDrawables(drawable1, null, null, null);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_fct_list;
    }
}
