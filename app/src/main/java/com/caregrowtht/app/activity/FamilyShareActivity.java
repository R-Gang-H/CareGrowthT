package com.caregrowtht.app.activity;

import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.FamilyCardAdapter;
import com.caregrowtht.app.adapter.FamilyShareAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.LoadingFrameView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018年10月18日15:06:52
 * 与家人共用
 */
public class FamilyShareActivity extends BaseActivity {

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    private FamilyShareAdapter mAdapter;
    private List<CourseEntity> userIds = new ArrayList<>();
    private List<FamilyCardAdapter> courserNameAdapter = new ArrayList<>();
    private StudentEntity stuDetails;
    private String phoneNumber;
    private String toShare = "0";
    private String orgId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_family_share;
    }

    @Override
    public void initView() {
        ivLeft.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_close_1));
        tvTitle.setText("与家人共用课时卡");
        initRecyclerView(recyclerView, true);
        mAdapter = new FamilyShareAdapter(userIds, this, onCourserWorkClick);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
        toShare = getIntent().getStringExtra("toShare");// toShare： 1:从添加学员进入 2:从学员详情进入 3:从审核学员进入
        stuDetails = (StudentEntity) getIntent().getSerializableExtra("stuDetails");// 通过学员id查询
        phoneNumber = getIntent().getStringExtra("phoneNumber");// 通过手机查询
        orgId = UserManager.getInstance().getOrgId();
        getCardByPhone();
    }

    private void getCardByPhone() {
        loadView.setProgressShown(true);
        String stuId = null;
        if (stuDetails != null) {
            stuId = stuDetails.getStuId();
        }
        HttpManager.getInstance().doGetCardByPhone("FamilyShareActivity",
                orgId, stuId, phoneNumber,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        if (data.getData().size() > 0) {
                            userIds = removeDuplicateOrder(data.getData());//筛选有几个学员
                            mAdapter.setData(userIds, data.getData());
                            loadView.delayShowContainer(true);
                        } else {
                            loadView.setNoShown(true);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("WorkClassActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(FamilyShareActivity.this);
                        } else {
                            U.showToast(errorMsg);
                            loadView.setErrorShown(true, v -> getCardByPhone());
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        loadView.setErrorShown(true, v -> getCardByPhone());
                    }
                });
    }

    /**
     * 根据userId去重
     *
     * @param orderList
     * @return
     * @author haoruigang
     */
    private static List<CourseEntity> removeDuplicateOrder(List<CourseEntity> orderList) {
        Set<CourseEntity> set = new TreeSet<>((a, b) -> {
            // 字符串则按照asicc码升序排列
            return a.getUserId().compareTo(b.getUserId());
        });
        set.addAll(orderList);
        return new ArrayList<>(set);
    }

    public FamilyShareAdapter.OnCourserWorkClick onCourserWorkClick = courserNameAdapter -> {
        this.courserNameAdapter.add(courserNameAdapter);
    };

    @OnClick({R.id.rl_back_button, R.id.btn_cancel, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
            case R.id.btn_cancel:
                finish();
                overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
                break;
            case R.id.btn_submit:
                List<CourseEntity> courseEntityList = new ArrayList<>();
                List<Boolean> isCheckAll = new ArrayList<>();
                StringBuilder cardId = new StringBuilder();
                int i = 0;
                for (FamilyCardAdapter coursesAdapter : courserNameAdapter) {
                    HashMap<Integer, CourseEntity> mCourseModels = coursesAdapter.getIsCourseEntity();
                    isCheckAll.add(coursesAdapter.isAll);
                    for (int j = 0; j < mCourseModels.size(); j++) {
                        CourseEntity courseEntity = mCourseModels.get(j);
                        if (courseEntity != null) {
                            courseEntityList.add(courseEntity);
                            switch (toShare) {
                                case "1": // 从添加学员进入
                                    EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_SHARE_CARDS, courseEntity));
                                    break;
                                case "2": // 从学员详情进入
                                case "3": // 从审核学员进入
                                    i++;
                                    if (i > 1) {
                                        cardId.append(",");
                                    }
                                    cardId.append(courseEntity.getCourseCardId());
                                    break;
                            }
                        }
                    }
                }

                switch (toShare) {
                    case "1": // 从添加学员进入
                        startActivity(new Intent(this, AddFormalStuActivity.class));
                        finish();
                        break;
                    case "2": // 从学员详情进入
                    case "3": // 从审核学员进入
                        String stuId = null;
                        if (stuDetails != null) {
                            stuId = stuDetails.getStuId();
                        }
                        courseCardShating(stuId, cardId.toString());
                        break;
                }
                break;
        }
    }

    private void courseCardShating(String stuId, String cardId) {
        HttpManager.getInstance().doChildShareCard("FamilyShareActivity", stuId, cardId, orgId,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        if (toShare.equals("2")) {
                            EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_STUDENT));
                        } else {
                            EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_ACTIVE_STU));
                        }
                        U.showToast("共用成功");
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("FamilyShareActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(FamilyShareActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("FamilyShareActivity onError", throwable.getMessage());
                    }
                });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) {
        if ((paramKeyEvent.getAction() == 0)
                && (paramKeyEvent.getKeyCode() == 4)) {
            finish();
            overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
        }
        return super.dispatchKeyEvent(paramKeyEvent);
    }
}
