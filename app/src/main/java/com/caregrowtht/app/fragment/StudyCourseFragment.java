package com.caregrowtht.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.StudyCourseInfoActivity;
import com.caregrowtht.app.adapter.StudyCourseAdapter;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 所学课程
 * Created by haoruigang on 2018-4-4 14:33:45.
 */

public class StudyCourseFragment extends BaseFragment implements ViewOnItemClick {

    @BindView(R.id.x_recycler_view)
    XRecyclerView xRecyclerView;

    private static String OrgId;
    //所学课程
    private static StudyCourseAdapter orgCourseAdapter;
    //添加所学课程数据
    private static ArrayList<CourseEntity> listDatas = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_study_course;
    }

    @Override
    public void initView(View view, Bundle savedInstanceState) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xRecyclerView.setLoadingMoreEnabled(false);
        xRecyclerView.setPullRefreshEnabled(false);
        xRecyclerView.setLayoutManager(layoutManager);
        orgCourseAdapter = new StudyCourseAdapter(listDatas, getContext(), StudyCourseFragment.this, null);
        xRecyclerView.setAdapter(orgCourseAdapter);
    }

    @Override
    public void initData() {
        OrgId = (String) getActivity().getIntent().getExtras().get("orgId");
        getOrgCourse(getActivity());//机构所学课程
    }

    //haoruigang on 2018-4-4 15:00:15 获取机构课程
    public static void getOrgCourse(Activity activity) {
        HttpManager.getInstance().doOrgCourse("OrgCourseFragment", OrgId,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {

                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        LogUtils.d("OrgCourseFragment onSuccess", data.getData().size() + "");
                        listDatas.clear();
                        listDatas.addAll(data.getData());
                        orgCourseAdapter.setData(data.getData());
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("OrgCourseFragment onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(activity);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("OrgCourseFragment onError", throwable.getMessage());
                    }
                });
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
//        ToastUtils.showToast("OrgCourseFragment 点击子项" + position);
        String courseId = listDatas.get(position - 1).getCourseId();
        startActivity(new Intent(getActivity(), StudyCourseInfoActivity.class).putExtra("courseId", courseId));
    }
}
