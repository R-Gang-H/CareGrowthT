package com.caregrowtht.app.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.library.utils.U;
import com.caregrowtht.app.AppManager;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.fragment.HomeFragment;
import com.caregrowtht.app.fragment.StateFragment;
import com.caregrowtht.app.uitil.GradientUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.NotificationUtil;
import com.caregrowtht.app.uitil.permissions.PermissionCallBackM;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.version.UpdateAppHttpUtil;
import com.caregrowtht.app.view.version.UpdateCallback;
import com.vector.update_app.UpdateAppManager;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindViews;
import butterknife.Setter;

import static com.caregrowtht.app.Constant.VERSION_PATH;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends BaseActivity implements View.OnClickListener {


    @BindViews({R.id.ll_state_btn, R.id.ll_home_btn})
    List<TextView> radioButtons;

    private int position;

    private StateFragment stateFragment;
    private HomeFragment homeFragment;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        //动态改变“colorPrimaryDark”来实现沉浸式状态栏
        GradientUtils.setColor(this, R.drawable.mine_title_bg, true);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        UserManager.getInstance().xgPush(U.MD5(UserManager.getInstance().userData.getUid()));//信鸽
        //动态权限申请
        requestPermission(
                Constant.REQUEST_CODE_WRITE,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE},
                getString(R.string.rationale_file),
                new PermissionCallBackM() {
                    @Override
                    public void onPermissionGrantedM(int requestCode, String... perms) {
                        //版本更新
                        new UpdateAppManager
                                .Builder()
                                //当前Activity
                                .setActivity(MainActivity.this)
                                //更新地址
                                .setUpdateUrl(VERSION_PATH)
                                .handleException(Throwable::printStackTrace)
                                //实现httpManager接口的对象
                                .setHttpManager(new UpdateAppHttpUtil())
                                .build()
                                .checkNewApp(new UpdateCallback(MainActivity.this));
                    }

                    @Override
                    public void onPermissionDeniedM(int requestCode, String... perms) {
                        LogUtils.e("MainActivity", "TODO: WRITE_EXTERNAL_STORAGE Denied");
                    }
                });
    }

    @Override
    public void initData() {
        CheckisNew();
        radioButtons.get(0).setOnClickListener(this);
        radioButtons.get(1).setOnClickListener(this);
        setChioceItem(position);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //判断是否需要开启通知栏功能
            NotificationUtil.OpenNotificationSetting(this, null);
        }
    }

    private void CheckisNew() {
        String name = UserManager.getInstance().userData.getName();// 空是新用户,不为空是旧用户
//        String isNew = UserManager.getInstance().userData.getIsNew();//是否是新用户 0：不是 1：是
        if (TextUtils.isEmpty(name)) {
//            if (!TextUtils.isEmpty(isNew) && isNew.equals("1")) {
            Intent pIntent = new Intent(this, SetInfoActivity.class);
            pIntent.putExtra("type", "register");
            startActivity(pIntent);
//            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckisNew();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        position = savedInstanceState.getInt("position");
        setChioceItem(position);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        //记录当前的position
        super.onSaveInstanceState(outState);
        outState.putInt("position", position);
    }

    public void setChioceItem(int index) {
        this.position = index;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        hideFragments(ft);
        UserManager.apply(radioButtons, TABSPEC, radioButtons.get(index));
        switch (index) {
            case 0:
                //动态
                if (null == stateFragment) {
                    stateFragment = new StateFragment();
                    ft.add(R.id.ll_content, stateFragment);
                } else {
                    EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_TEACHER));
                    ft.show(stateFragment);
                }
                break;
            case 1:
                //首页
                if (null == homeFragment) {
                    homeFragment = new HomeFragment();
                    ft.add(R.id.ll_content, homeFragment);
                } else {
                    ft.show(homeFragment);
                    EventBus.getDefault().post(new ToUIEvent(ToUIEvent.TEACHER_HOME_EVENT));
                }
                break;
        }
        ft.commitAllowingStateLoss();
    }

    private void hideFragments(FragmentTransaction ft) {
        if (null != stateFragment)
            ft.hide(stateFragment);
        if (null != homeFragment)
            ft.hide(homeFragment);
    }

    //控制normal 状态的当前View 隐藏，其它空间仍然为显示
    final Setter<TextView, TextView> TABSPEC = (view, value, index) -> {
        assert value != null;
        if (view.getId() == value.getId()) {
            view.setSelected(true);
            view.setBackgroundColor(getResources().getColor(R.color.greenLight));
        } else {
            view.setSelected(false);
            view.setBackgroundColor(getResources().getColor(R.color.white));
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_state_btn:
                setChioceItem(0);
                break;
            case R.id.ll_home_btn:
                setChioceItem(1);
                break;
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) {
        if ((paramKeyEvent.getAction() == 0)
                && (paramKeyEvent.getKeyCode() == 4)) {

            AppManager.getAppManager().finishAllActivity();
        }
        return super.dispatchKeyEvent(paramKeyEvent);
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        switch (event.getWhat()) {
            case ToUIEvent.STATE_EVENT:
                int type = (int) event.getObj();
                setChioceItem(type);
                break;
        }
    }

}
