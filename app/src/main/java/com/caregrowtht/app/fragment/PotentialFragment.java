package com.caregrowtht.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.AddStudentActivity;
import com.caregrowtht.app.activity.ScreenActivity;
import com.caregrowtht.app.adapter.PotentStuAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.user.EventData;
import com.caregrowtht.app.model.OrgEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.LoadingFrameView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 潜在学员
 */
public class PotentialFragment extends BaseFragment {

    @BindView(R.id.tv_add_time)
    TextView tvAddTime;
    @BindView(R.id.tv_name_order)
    TextView tvNameOrder;
    @BindView(R.id.tv_screen)
    TextView tvScreen;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    private String orgId;
    private OrgEntity orgEntity;
    private String sortWay = "3";
    private String originId;
    private String statusId;

    private ArrayList<StudentEntity> studentDatas = new ArrayList<>();

    private PotentStuAdapter potentAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_student_pote;
    }

    @Override
    public void initView(View view, Bundle savedInstanceState) {
        initRecyclerView(recyclerView, true);
        potentAdapter = new PotentStuAdapter(studentDatas, getActivity());
        recyclerView.setAdapter(potentAdapter);
    }

    @Override
    public void initData() {
        orgId = UserManager.getInstance().getOrgId();
        //25.获取leads管理页学生列表
        if (orgEntity != null)
            getLeadsList();
    }


    public void setData(OrgEntity data) {
        orgEntity = data;
    }

    private void getLeadsList() {
        loadView.setProgressShown(true);
        //haoruigang on 2018-5-4 10:41:52 获取leads管理页学生列表
        HttpManager.getInstance().doLeadsList("StudentActivity",
                orgId, sortWay, originId, statusId,
                new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        studentDatas = data.getData();
                        if (studentDatas.size() > 0) {
                            potentAdapter.setData(studentDatas);
                            loadView.delayShowContainer(true);
                        } else {
                            loadView.setNoShown(true);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(getActivity());
                        } else {
                            U.showToast(errorMsg);
                            loadView.setErrorShown(true, v -> getLeadsList());
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        loadView.setErrorShown(true, v -> getLeadsList());
                    }
                });
    }


    @OnClick({R.id.iv_add, R.id.tv_add_time, R.id.tv_name_order, R.id.tv_screen})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_add:
                startActivity(new Intent(getActivity(), AddStudentActivity.class).putExtra("orgEntity", orgEntity));
                break;
            case R.id.tv_add_time:// 3:添加时间正序 4:添加时间倒叙
                tvAddTime.setSelected(tvAddTime.isSelected() ? false : true);
                tvNameOrder.setSelected(false);
                tvScreen.setSelected(false);
                sortWay = tvAddTime.isSelected() ? "3" : "4";
                getLeadsList();
                break;
            case R.id.tv_name_order:// 1:姓名正序 2:姓名倒序
                tvAddTime.setSelected(false);
                tvNameOrder.setSelected(tvNameOrder.isSelected() ? false : true);
                tvScreen.setSelected(false);
                sortWay = tvNameOrder.isSelected() ? "1" : "2";
                getLeadsList();
                break;
            case R.id.tv_screen:
                tvAddTime.setSelected(false);
                tvNameOrder.setSelected(false);
                tvScreen.setSelected(true);
                startActivity(new Intent(getActivity(), ScreenActivity.class).putExtra("orgId", orgId));
                break;
        }
    }


    @Subscribe
    public void onEventMainThread(EventData data) {
        switch (data.getTag()) {
            case ESERACH:
                if (!TextUtils.isEmpty(data.getContent()) && !TextUtils.isEmpty((CharSequence) data.getPar())) {
                    originId = data.getContent();
                    statusId = (String) data.getPar();
                }
                getLeadsList();
                break;
        }
    }

}
