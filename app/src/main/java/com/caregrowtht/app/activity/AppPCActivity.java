package com.caregrowtht.app.activity;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.MoreAdapter;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * PC端
 */
public class AppPCActivity extends BaseActivity {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_more_item)
    RecyclerView rvMoreItem;

    private String[] moreFunct = new String[]{
            "课表具备更多视图和功能", "给学员批量请假", "集中处理当日出勤", "设置教学大纲库",
            "设置机构主页", "更全面的数据统计分析", "自动消课/自动消课时费失败时的人工消课",
            "批量导入学员", "批量导入教师", "新增和修改权限", "教师离职", "激活非活跃学员"};

    @Override
    public int getLayoutId() {
        return R.layout.activity_app_pc;
    }

    @Override
    public void initView() {
        tvTitle.setText("更多");

        initRecyclerView(rvMoreItem, true);
        rvMoreItem.setAdapter(new MoreAdapter(Arrays.asList(moreFunct), this));
    }

    @Override
    public void initData() {

    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                return;
        }
    }
}
