package com.caregrowtht.app.activity;

import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.InitDataAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 数据初始化
 */
public class InitDataActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private int[] initRes = {R.mipmap.ic_add_teacher, R.mipmap.ic_card_blue, R.mipmap.ic_send_stu};
    private String[] initName = {"添加教师", "设置课时卡", "添加学员"};
    private List<String> initSize = new ArrayList<>();
    private InitDataAdapter initAdapter;
    private String orgId;
    private String teacherSize;//教师数
    private String cardsSize;//课时卡数
    private String stuSize;//学员数
    private MessageEntity msgEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_init_data;
    }

    @Override
    public void initView() {
        tvTitle.setText("数据初始化");
        initRecyclerView(recyclerView, true);
        initAdapter = new InitDataAdapter(Collections.singletonList(initRes), this, this);
        recyclerView.setAdapter(initAdapter);
    }

    @Override
    public void initData() {
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (msgEntity != null) {
            orgId = msgEntity.getOrgId();
        } else {
            orgId = UserManager.getInstance().getOrgId();
        }
        getOrgTeachers("1");// 48.获取机构的教师
    }


    /**
     * 48.获取机构的教师
     *
     * @param status 教师的状态 1：正式 2：待审核
     */
    private void getOrgTeachers(String status) {
        HttpManager.getInstance().doGetOrgTeachers("InitDataActivity",
                orgId, status, "1", new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        teacherSize = String.valueOf(data.getData().size());
                        getOrgExistCard("1");//31.获取机构现有的课时卡
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("InitDataActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(InitDataActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("InitDataActivity onFail", throwable.getMessage());
                    }
                });
    }

    /**
     * 31.获取机构现有的课时卡
     * haoruigang on 2018-8-17 17:53:42
     *
     * @param status 课程卡的状态 1：活跃 2：非活跃
     */
    private void getOrgExistCard(String status) {
        HttpManager.getInstance().doGetOrgCard("InitDataActivity", orgId, status,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        cardsSize = String.valueOf(data.getData().size());
                        getOrgChildNum();//29.获取机构的正式学员数量
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("InitDataActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(InitDataActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.tag("InitDataActivity throwable " + throwable);
                    }
                });
    }

    public void getOrgChildNum() {
        //haoruigang on 2018-8-7 17:11:12 29.获取机构的正式学员数量
        HttpManager.getInstance().doGetOrgChildNum("InitDataActivity",
                orgId, "2", new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        stuSize = data.getData().get(0).getCount();
                        initSize.clear();
                        initSize.add(0, teacherSize);
                        initSize.add(1, cardsSize);
                        initSize.add(2, stuSize);
                        initAdapter.setData(initRes, initName, initSize);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("InitDataActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(InitDataActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.tag("InitDataActivity throwable " + throwable);
                    }
                });
    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }

    @Override
    public void setOnItemClickListener(View view, int postion) {
        switch (postion) {
            case 0:
                //教师管理
                startActivity(new Intent(this, TeacherMsgActivity.class)
                        .putExtra("msgEntity", msgEntity));
                break;
            case 1:
                //课时卡管理
                startActivity(new Intent(this, CourserCardMsgActivity.class)
                        .putExtra("msgEntity", msgEntity));
                break;
            case 2:
                // 学员
                startActivity(new Intent(this, FormalActivity.class)
                        .putExtra("msgEntity", msgEntity));
                break;
        }
    }
}
