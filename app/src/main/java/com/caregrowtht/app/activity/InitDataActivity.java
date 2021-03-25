package com.caregrowtht.app.activity;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.InitDataAdapter;
import com.caregrowtht.app.adapter.NotifyCardAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.NotifyCardEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.ItemOffsetDecoration;
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
    @BindView(R.id.rv_add_all)
    RecyclerView rvAddAll;

    private int[] initRes = {R.mipmap.ic_add_teacher, R.mipmap.ic_send_stu};
    private String[] initName = {"添加教师", "添加学员"};
    private List<String> initSize = new ArrayList<>();
    private InitDataAdapter initAdapter;
    private String orgId;
    private String teacherSize;//教师数
    private String stuSize;//学员数
    private MessageEntity msgEntity;

    ArrayList<NotifyCardEntity> moreCards = new ArrayList<>();
    //更多管理的类型 1：添加课时卡 2：添加教室 3：添加课程分类
    private int[] moreImage = new int[]{R.mipmap.ic_add_course_card, R.mipmap.ic_add_room, R.mipmap.ic_add_course_type};
    private String[] moreName = new String[]{"添加课时卡", "添加教室", "添加课程分类"};

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

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        final int spacing = getResources().getDimensionPixelOffset(R.dimen.margin_size_20);
        initRecyclerGrid(rvAddAll, 3);
        moreCards.clear();
        for (int i = 0; i < moreImage.length; i++) {
            moreCards.add(new NotifyCardEntity(moreImage[i], moreName[i]));
        }
        rvAddAll.setAdapter(new NotifyCardAdapter(moreCards, this, this));
        rvAddAll.addItemDecoration(new ItemOffsetDecoration(spacing, spacing, 0, 0));
    }

    @Override
    public void initData() {
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (msgEntity != null) {
            orgId = msgEntity.getOrgId();
            UserManager.getInstance().setOrgId(orgId);
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
                        LogUtils.d("InitDataActivity onFail", throwable.getMessage());
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
                        initSize.add(1, stuSize);
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
        switch (view.getId()) {
            case R.id.rl_init_data:
                switch (postion) {
                    case 0:
                        //教师管理
                        startActivity(new Intent(this, TeacherMsgActivity.class)
                                .putExtra("msgEntity", msgEntity));
                        break;
                    case 1:
                        // 学员
                        startActivity(new Intent(this, FormalActivity.class)
                                .putExtra("msgEntity", msgEntity));
                        break;
                }
                break;
            case R.id.rl_notify:
                switch (postion) {
                    case 0:
                        //添加课时卡
                        startActivity(new Intent(this, CourserCardMsgActivity.class));
                        break;
                    case 1:
                        // 添加教室
                        startActivity(new Intent(this, ClassMsgActivity.class));
                        break;
                    case 2:
                        // 添加课程分类
                        startActivity(new Intent(this, CourserMsgActivity.class));
                        break;
                }
                break;
        }
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        if (event.getWhat() == ToUIEvent.REFERSH_NEWCARD || event.getWhat() == ToUIEvent.REFERSH_ACTIVE_STU) {
            getOrgTeachers("1");// 48.获取机构的教师
        }
    }
}
