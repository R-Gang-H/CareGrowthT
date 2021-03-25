package com.caregrowtht.app.activity;

import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by haoruigang on 2017/12/1.
 */

public class AboutUsActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_version)
    TextView tvVersion;

    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }


    @Override
    public void initView() {
        tvTitle.setText("关于爱成长");
        tvVersion.setText(String.format("(V%s)", U.getVersionName()));
    }

    @Override
    public void initData() {

    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked() {
        finish();
    }
}
