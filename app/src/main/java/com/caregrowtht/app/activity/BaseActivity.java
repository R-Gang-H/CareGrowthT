package com.caregrowtht.app.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.android.library.utils.U;
import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.caregrowtht.app.AppManager;
import com.caregrowtht.app.CrashHandler;
import com.caregrowtht.app.R;
import com.caregrowtht.app.uitil.permissions.BasePermissionActivity;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by haoruigang on 2018-4-3 09:51:22 继承权限父类
 */

public abstract class BaseActivity extends BasePermissionActivity {

    private Unbinder unbinder;
    public int pageIndex = 1;
    public String pageSize = "15";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        unbinder = ButterKnife.bind(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);//注册EventBus
        }
        AppManager.getAppManager().addActivity(this);
        CrashHandler.getInstance().init(this);//初始化全局异常管理
//        int screenWidth = getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px）

        initView();
        initData();
    }


    /********************* 子类实现 *****************************/
    // 获取布局文件
    public abstract int getLayoutId();

    // 初始化view
    public abstract void initView();

    // 数据加载
    public abstract void initData();

    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);//反注册EventBus
        }
        if (Util.isOnMainThread() && !this.isFinishing()) {
            Glide.with(this).pauseRequests();
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && this.isDestroyed()) {
//            throw new IllegalArgumentException("You cannot start a load for a destroyed activity");
//        }
    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        XGPushClickedResult click = XGPushManager.onActivityStarted(this);
        // click.getCustomContent()
        if (click != null) { // 判断是否来自信鸽的打开方式
            String uid = (String) U.getPreferences("uid", "");
            String token = (String) U.getPreferences("token", "");
            UserManager.getInstance().autoLogin(this, uid, token);
            Log.d("TPush", "onResumeXGPushClickedResult:" + click.toString());
            EventBus.getDefault().post(new ToUIEvent(ToUIEvent.STATE_EVENT, 0));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        MobclickAgent.onPageStart("Activity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        MobclickAgent.onPageEnd("Activity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        XGPushManager.onActivityStoped(this);
    }

    //Eventbus
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(ToUIEvent event) {

    }

    public LinearLayoutManager initRecyclerView(RecyclerView recyclerView, boolean isVertical) {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(isVertical ? RecyclerView.VERTICAL : LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);
        return manager;
    }

    public void initRecyclerGrid(RecyclerView recyclerView, int span) {
        GridLayoutManager manager = new GridLayoutManager(this, span);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
    }

    //    初始化 RecyclerView的配置
    public LinearLayoutManager iniXrecyclerView(XRecyclerView xRecyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        xRecyclerView.setLayoutManager(layoutManager);
        // xRecyclerView.setRefreshHeader(new CustomArrowHeader(this));
        xRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        xRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        xRecyclerView.setArrowImageView(R.mipmap.ic_recycler_view_arrow);
        xRecyclerView.setPullRefreshEnabled(false);
        xRecyclerView.setLoadingMoreEnabled(false);
        xRecyclerView.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
        xRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
        return layoutManager;
    }

    public void iniXrecyclerGrid(XRecyclerView xRecyclerView, int span) {
        GridLayoutManager manager = new GridLayoutManager(this, span);
        manager.setOrientation(RecyclerView.VERTICAL);
        xRecyclerView.setLayoutManager(manager);
        xRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        xRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        xRecyclerView.setArrowImageView(R.mipmap.ic_recycler_view_arrow);
        xRecyclerView.setPullRefreshEnabled(false);
        xRecyclerView.setLoadingMoreEnabled(false);
        xRecyclerView.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
        xRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
    }

    /***
     * 隐藏软键盘
     */
    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view != null) {
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 权限检测
     *
     * @param permission 权限
     * @return
     */
    public boolean selfPermissionGranted(String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            int targetSdkVersion = this.getApplicationInfo().targetSdkVersion;

            Log.e("lt", "targetSdkVersion=" + targetSdkVersion);

            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = this.checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(this, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }

        Log.e("lt", "result" + result);
        return result;
    }

    public void dismissLoadingDialog() {
        U.dismissLoadingDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }


    public void CheckSharePermision() {
        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{Manifest.permission.READ_PHONE_STATE};
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }
    }

}
