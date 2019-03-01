package com.caregrowtht.app.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.user.UserManager;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Update by haoruigang on 2018-4-20 14:31:20
 */

public class SplashActivity extends BaseActivity {

    @BindView(R.id.iv_splash)
    ImageView ivSplash;
//    @BindView(R.id.btn_go)
//    Button btnGo;

    private Timer timer;
    //设定倒计时时长 n 单位 s
    private int time = 1;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        btnGo.setText("跳过(" + time + ")");
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (time > 0) {
                    time--;
                }
                handler.sendEmptyMessage((time == 0 ? 1 : 0));
            }
        }, 1000, 1000);
    }

    @Override
    public void initData() {
//        showLoadingDialog("加载中...");
    }


    // 跳转主页
    private void go2MainActivity() {
        String uid = (String) U.getPreferences("uid", "");
        String token = (String) U.getPreferences("token", "");
        if (!StrUtils.isEmpty(uid) && !StrUtils.isEmpty(token)) {
            Log.e("----------", "autologin called");
            UserManager.getInstance().autoLogin(SplashActivity.this, uid, token);
        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }


    //初始化 Handler
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
//                    btnGo.setText("跳过(" + time + ")");
                    break;
                case 1:
//                    btnGo.setText("跳过(" + time + ")");
                    go2MainActivity();
            }
            super.handleMessage(msg);
        }
    };

    @OnClick(R.id.btn_go)
    public void onViewClicked() {
        handler.sendEmptyMessage(1);
    }


    @Override
    protected void onPause() {
        timer.cancel();
        super.onPause();
    }
}
