package com.caregrowtht.app.fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.library.utils.U;
import com.caregrowtht.app.MyApplication;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.BaseActivity;
import com.caregrowtht.app.uitil.permissions.BasePermissionFragment;
import com.caregrowtht.app.user.ToUIEvent;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ZMM on 2017/10/17.
 * 基类Fragment
 * Update by haoruigang on 2018-4-3 09:51:22 继承权限父类
 *
 * @author 张明明
 */
public abstract class BaseFragment extends BasePermissionFragment {

    protected BaseActivity mActivity;
    public Context mContext;
    public int pageIndex = 1;
    public String pageSize = "15";
    Handler handler = new Handler();
    HashMap<String, String> map = new HashMap<>();
    private Unbinder unbinder;

    /**
     * 获得全局的，防止使用getActivity()为空
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (BaseActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, view);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);//注册EventBus
        }
        initView(view, savedInstanceState);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /**
     * 该抽象方法就是 onCreateView中需要的layoutId
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 初始化View
     *
     * @param view
     * @param savedInstanceState
     */
    public abstract void initView(View view, Bundle savedInstanceState);

    /**
     * 初始化数据
     */
    public abstract void initData();

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);//反注册EventBus
        }
    }

    //Eventbus
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(ToUIEvent event) {

    }

}
