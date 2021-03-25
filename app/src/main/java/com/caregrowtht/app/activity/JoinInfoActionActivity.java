package com.caregrowtht.app.activity;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.MyChildAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-4-9 11:05:10 活动报名详情
 */
public class JoinInfoActionActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycler_view)
    RecyclerView xRecyclerView;

    //添加数据
    private ArrayList<StudentEntity> mArrDatas = new ArrayList<>();
    private MyChildAdapter mAdapter;
    private String activityId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_joininfo_action;
    }

    @Override
    public void initView() {
        tvTitle.setText("报名详情");
        initRecyclerView(xRecyclerView, true);
        mAdapter = new MyChildAdapter(mArrDatas, this);
        xRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
        activityId = getIntent().getStringExtra("actId");
        activityJoinInfo();
    }

    @OnClick({R.id.rl_back_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }

    private void activityJoinInfo() {
        //haoruigang on 2018-5-24 16:15:56 23.获取活动报名详情
        HttpManager.getInstance().doJoinInfoAction("JoinInfoActionActivity",
                activityId, new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        mAdapter.setData(data.getData(), true);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(JoinInfoActionActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

}
