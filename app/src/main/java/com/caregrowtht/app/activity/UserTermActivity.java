package com.caregrowtht.app.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.okhttp.callback.OkHttpUtils;
import com.caregrowtht.app.uitil.X5WebView;
import com.caregrowtht.app.uitil.alicloud.oss.AliYunOss;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.TbsReaderView;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.utils.TbsLog;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import zlc.season.rxdownload3.core.Status;
import zlc.season.rxdownload3.core.Succeed;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
    TbsReaderView mTbsReaderView;

    private String tbsReaderTemp = Environment.getExternalStorageDirectory() + "/TbsReaderTemp/";
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
        assert data != null;
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
                init2();
            } else if (openType.equals("2")) {
                tvTitle.setText("文件");

                /*添加进度条*/
                mProgressBar = new ProgressBar(this, null,
                        android.R.attr.progressBarStyleHorizontal);
                LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 10);
                mProgressBar.setLayoutParams(barParams);
                mProgressBar.setProgress(0);
                mViewParent.addView(mProgressBar);

                //创建文件夹 MyDownLoad，在存储卡下
                String fileName = tbsReaderTemp + mIntentUrl.toString().substring(mIntentUrl.toString().lastIndexOf("/") + 1);
                //判断是否在本地/[下载/直接打开]
                if (AliYunOss.getInstance(this).fileIsExists(fileName)) {
                    init(fileName);
                } else {
                    //下载的文件名
                    OkHttpUtils.download(mIntentUrl.toString(), status -> {
                        //当进度走到100的时候做自己的操作，我这边是弹出dialog
                        int i = (int) status.getDownloadSize();
                        if (i == 100) {
                            mProgressBar.setVisibility(GONE);
                        } else {
                            if (mProgressBar.getVisibility() == GONE)
                                mProgressBar.setVisibility(VISIBLE);
                            mProgressBar.setProgress(i);
                        }
                        Log.d("进度 ", i + "");
                        if (status instanceof Succeed) {
                            init(fileName);
                        }
                    });
                }
            } else {
                tvTitle.setText("机构主页");
                init2();
            }
        }
    }

    @OnClick(R.id.rl_back_button)
    public void onClick() {
        finish();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init(String fileName) {
        mTbsReaderView = new TbsReaderView(this, (integer, o, o1) ->
                Log.d("lm", "onCallBackAction: " + integer));
        mViewParent.addView(mTbsReaderView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        //防止布局参数设置失败
        mTbsReaderView.post(() -> {
            //添加title
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.topMargin = 0;//actionBar高度+状态栏高度
            mTbsReaderView.setLayoutParams(layoutParams);
        });

        //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
        String bsReaderTemp = tbsReaderTemp;
        File bsReaderTempFile = new File(bsReaderTemp);
        if (!bsReaderTempFile.exists()) {
            Log.d("print", "准备创建/TbsReaderTemp！！");
            boolean mkdir = bsReaderTempFile.mkdir();
            if (!mkdir) {
                Log.d("print", "创建/TbsReaderTemp失败！！！！！");
            }
        }
        Bundle bundle = new Bundle();
        bundle.putString("filePath", fileName);
        bundle.putString("tempPath", tbsReaderTemp);
        boolean result = mTbsReaderView.preOpen(getFileType(fileName), false);
        Log.d("print", "查看文档---" + result);
        if (result) {
            mTbsReaderView.openFile(bundle);
        }
    }

    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            Log.d("print", "paramString---->null");
            return str;
        }
        Log.d("print", "paramString:" + paramString);
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            Log.d("print", "i <= -1");
            return str;
        }

        str = paramString.substring(i + 1);
        Log.d("print", "paramString.substring(i + 1)------>" + str);
        return str;
    }


    private void init2() {
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
        if (intent == null || mWebView == null || intent.getData() == null)
            return;
        mWebView.loadUrl(intent.getData().toString());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mTbsReaderView != null)
                mTbsReaderView.onStop();
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
        if (mTbsReaderView != null)
            mTbsReaderView.onStop();
        if (mWebView != null)
            mWebView.destroy();
        super.onDestroy();
    }

}