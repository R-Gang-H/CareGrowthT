package com.caregrowtht.app.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.uitil.X5WebView;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.utils.TbsLog;

import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-8-30 17:26:22
 * 用户协议
 */
public class UserTermActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    /**
     * 作为一个浏览器的示例展示出来，采用android+web的模式
     */
    @BindView(R.id.webView1)
    ViewGroup mViewParent;

    private ProgressBar mProgressBar;
    X5WebView mWebView;

    private URL mIntentUrl;
    private String openType;

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_term;
    }

    @Override
    public void initView() {
        //避免闪烁
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        openType = getIntent().getStringExtra("openType");
        Uri data = getIntent().getData();
        if (data != null) {
            try {
                mIntentUrl = new URL(data.toString());
                if (Integer.parseInt(Build.VERSION.SDK) >= 11) {
                    getWindow()
                            .setFlags(
                                    android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                                    android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void initData() {
        if (!TextUtils.isEmpty(openType) && mIntentUrl != null) {
            if (openType.equals("1")) {
                tvTitle.setText("爱成长用户协议");
            } else if (openType.equals("2")) {
                tvTitle.setText("调查问卷");
            } else if (openType.equals("3")) {
                tvTitle.setText("机构主页");
            } else if (openType.equals("4")) {
                tvTitle.setText("来自创始人的信");
            } else {// 5
                tvTitle.setText("3分钟快速了解爱成长");
            }
            init();
        }
    }

    @OnClick(R.id.rl_back_button)
    public void onClick() {
        finish();
    }

    private void init() {
        mWebView = new X5WebView(this, null);
        mViewParent.addView(mWebView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // webSetting.setPreFectch(true);
        long time = System.currentTimeMillis();
        mWebView.loadUrl(mIntentUrl.toString());
        TbsLog.d("time-cost", "cost time: "
                + (System.currentTimeMillis() - time));
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent == null || mWebView == null || intent.getData() == null)
            return;
        mWebView.loadUrl(intent.getData().toString());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            } else
                return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        if (mWebView != null)
            mWebView.destroy();
        super.onDestroy();
    }

}