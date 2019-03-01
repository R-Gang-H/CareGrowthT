package com.caregrowtht.app.fragment;

import android.os.Bundle;
import android.view.View;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.OrgActionAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.OrgEntity;
import com.caregrowtht.app.model.OrgNotifyEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.view.LoadingFrameView;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by haoruigang on 2018/5/13 16:35.
 * 活动
 */

public class ActivityFragment extends BaseFragment implements ViewOnItemClick {

    @BindView(R.id.rv_growth_history)
    XRecyclerView rvGrowthHistory;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    OrgActionAdapter mAdapter;
    ArrayList<OrgEntity> mArrDatas;

    @Override
    protected int getLayoutId() {
        return R.layout.view_my_fav_growth_history;
    }

    @Override
    public void initView(View view, Bundle savedInstanceState) {
        mArrDatas = new ArrayList<>();
        //列表
        iniXrecyclerView(rvGrowthHistory);
        mAdapter = new OrgActionAdapter(mArrDatas, getContext(), ActivityFragment.this, null);
        rvGrowthHistory.setAdapter(mAdapter);
        rvGrowthHistory.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageIndex = 1;
                RequestMomentList(pageIndex);
            }

            @Override
            public void onLoadMore() {
                pageIndex++;
                RequestMomentList(pageIndex);
            }
        });
        rvGrowthHistory.setLoadingMoreEnabled(true);
    }

    @Override
    public void initData() {
        RequestMomentList(1);
    }

    private void RequestMomentList(int argPage) {
        loadView.setProgressShown(true);
        pageIndex = argPage;
        if (mAdapter.getItemCount() < 20) {
            pageIndex = 1;
        }
        HttpManager.getInstance().doMyActivity("MyFavActivityView",
                "", "3", pageIndex, "20",
                new HttpCallBack<BaseDataModel<OrgEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<OrgEntity> data) {
                        if (data.getData().size() > 0) {
                            if (pageIndex == 1) {
                                mAdapter.setData(data.getData(), true);
                            } else {
                                mAdapter.setData(data.getData(), false);
                            }
                            loadView.delayShowContainer(true);
                        } else {
                            if (pageIndex == 1) {
                                mAdapter.setData(data.getData(), true);
                                loadView.setNoShown(true);
                            } else {
                                loadView.delayShowContainer(true);
                            }
                        }
                        if (rvGrowthHistory != null) {
                            rvGrowthHistory.refreshComplete();
                            rvGrowthHistory.loadMoreComplete();
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (rvGrowthHistory != null) {
                            rvGrowthHistory.refreshComplete();
                            rvGrowthHistory.loadMoreComplete();
                        }
                        loadView.setErrorShown(true, v -> RequestMomentList(1));
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(getActivity());
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        loadView.setErrorShown(true, v -> RequestMomentList(1));
                    }
                });

    }

    @Override
    public void setOnItemClickListener(View view, int postion) {

    }
}
