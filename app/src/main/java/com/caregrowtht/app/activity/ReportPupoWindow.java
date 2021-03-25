package com.caregrowtht.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.caregrowtht.app.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * haoruigang on 2018年7月18日10:48:40
 * 举报弹框
 */
public class ReportPupoWindow extends BaseActivity {

    private String mCircleId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_report_pupo;
    }

    @Override
    public void initView() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        mCircleId = getIntent().getExtras().getString("mCircleId");
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.tv_repeat, R.id.tv_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_repeat:
                startActivity(new Intent(this, ReportActivity.class).putExtra("circleId", mCircleId));
                finish();
                break;
            case R.id.tv_cancel:
                finish();
                overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) {
        if ((paramKeyEvent.getAction() == 0)
                && (paramKeyEvent.getKeyCode() == 4)) {
            finish();
            overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
        }
        return super.dispatchKeyEvent(paramKeyEvent);
    }
}
