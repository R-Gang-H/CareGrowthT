package com.caregrowtht.app.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.CreateOrgHintAdapter;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;

import java.util.Arrays;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 创建机构提示
 */
public class CreateOrgHintActivity extends BaseActivity implements ViewOnItemClick {

    String[] hintArray = {"", "查看功能列表", "查看资费说明", "关于试用"};

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    CreateOrgHintAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_org_hint;
    }

    @Override
    public void initView() {
        tvTitle.setText("提示");
        initRecyclerView(recyclerView, true);
        adapter = new CreateOrgHintAdapter(Arrays.asList(hintArray), this, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.rl_back_button, R.id.btn_go_create})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.btn_go_create:
                startActivity(new Intent(this, CreateOrgActivity.class));
                break;
        }
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        if (position == 1) {// 查看功能列表
            startActivity(new Intent(this, FCTListActivity.class));
        } else if (position == 2 || position == 3) {// "查看资费说明", "关于试用"
            startActivity(new Intent(this, TariffExplainActivity.class)
                    .putExtra("position", position + ""));
        }
    }
}
