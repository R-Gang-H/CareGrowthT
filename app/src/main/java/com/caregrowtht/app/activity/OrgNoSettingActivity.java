package com.caregrowtht.app.activity;

import android.view.View;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.OrgEntity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 机构信息未设置
 */
public class OrgNoSettingActivity extends BaseActivity {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    private OrgEntity orgEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_org_no_setting;
    }

    @Override
    public void initView() {
        orgEntity = (OrgEntity) getIntent().getSerializableExtra("orgEntity");
        tvTitle.setText(orgEntity.getOrgName());
    }

    @Override
    public void initData() {

    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }
}
