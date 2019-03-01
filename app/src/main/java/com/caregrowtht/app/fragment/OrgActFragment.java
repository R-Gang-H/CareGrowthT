package com.caregrowtht.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.ActionInfoActivity;
import com.caregrowtht.app.adapter.OrgActionAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.OrgEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 机构活动
 * Created by haoruigang on 2018/3/14.
 */

public class OrgActFragment extends BaseFragment implements ViewOnItemClick {

    @BindView(R.id.x_recycler_view)
    XRecyclerView xRecyclerView;

    private static OrgActionAdapter orgActionAdapter;
    //添加机构活动数据
    private static ArrayList<OrgEntity> listDatas = new ArrayList<>();

    private static String OrgId;
    private static String type = "3";//1:未结束的活动 2:历史活动 3:全部;
    public static String pageIndex = "1";
    public static String pageSize = "15";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_org_activity;
    }

    @Override
    public void initView(View view, Bundle savedInstanceState) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xRecyclerView.setLoadingMoreEnabled(false);
        xRecyclerView.setPullRefreshEnabled(false);
        xRecyclerView.setLayoutManager(layoutManager);
        orgActionAdapter = new OrgActionAdapter(listDatas, getContext(), OrgActFragment.this, null);
        xRecyclerView.setAdapter(orgActionAdapter);
    }

    @Override
    public void initData() {
        OrgId = (String) getActivity().getIntent().getExtras().get("orgId");
        getOrgAction(getActivity());//机构活动
    }

    //haoruigang on 2018-4-4 17:26:53 机构活动
    public static void getOrgAction(Activity activity) {
        HttpManager.getInstance().doOrgAction("OrgActFragment",
                OrgId, type, pageIndex, pageSize,
                new HttpCallBack<BaseDataModel<OrgEntity>>() {

                    @Override
                    public void onSuccess(BaseDataModel<OrgEntity> data) {
                        LogUtils.d("OrgActFragment onSuccess", data.getData().size() + "");
                        listDatas.clear();
                        listDatas.addAll(data.getData());
                        orgActionAdapter.setData(data.getData(), true);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("OrgActFragment onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(activity);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("OrgActFragment onError", throwable.getMessage());
                    }
                });
    }

    @Override
    public void setOnItemClickListener(View view, int postion) {
        String actId = listDatas.get(postion - 1).getActivityId();
        startActivity(new Intent(getActivity(), ActionInfoActivity.class).putExtra("actId", actId));
    }
}
