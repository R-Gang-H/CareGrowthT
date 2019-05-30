package com.caregrowtht.app.activity;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.TariffExplainAdapter;

import java.util.Arrays;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 资费说明
 */
public class TariffExplainActivity extends BaseActivity {

    String[] num = new String[]{"50", "100", "150", "200", "250", "300", "400", "500", "1000"};// 学员数量
    String[] price = new String[]{"30", "21", "18", "16", "14", "12.5", "10", "9", "5"};// 人均单价

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_base)
    RelativeLayout rlBase;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    TariffExplainAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_tariff_explain;
    }

    @Override
    public void initView() {
        initRecyclerView(recyclerView, true);
        adapter = new TariffExplainAdapter(Arrays.asList(num), Arrays.asList(price), this);
        recyclerView.setAdapter(adapter);
        String posType = getIntent().getStringExtra("position");
        tvTitle.setText(posType.equals("3") ? "关于使用" : "资费说明");
        rlBase.setVisibility(posType.equals("3") ? View.GONE : View.VISIBLE);
    }

    @Override
    public void initData() {

    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }
}
