package com.caregrowtht.app.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.CourserVsAdapter;
import com.caregrowtht.app.adapter.NewCardsAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.LoadingFrameView;
import com.caregrowtht.app.view.gallery.CardScaleHelper;
import com.caregrowtht.app.view.gallery.SpeedRecyclerView;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

import static com.caregrowtht.app.user.ToUIEvent.REFERSH_COURSE_WORK;

/**
 * 课时卡VS排课
 */
public class CourseVsActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;
    @BindView(R.id.rv_gallery)
    SpeedRecyclerView rvGallery;
    @BindView(R.id.rv_course_list)
    RecyclerView rvCourseList;

    List<CourseEntity> mListCard = new ArrayList<>();
    private CardScaleHelper mCardScaleHelper = null;
    private NewCardsAdapter mCardsAdapter;
    private String orgId;
    private int mListPost = 0;

    List<CourseEntity> courseAllList = new ArrayList<>();
    List<CourseEntity> courseVsList = new ArrayList<>();
    private CourserVsAdapter courseVsAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_course_vs;
    }

    @Override
    public void initView() {
        tvTitle.setText(String.format("课时卡%s排课", "VS"));
        initRecyclerView(rvGallery, false);
        mCardsAdapter = new NewCardsAdapter(mListCard, this, null, "4", "45");//展示页面 1:选择购买新卡 2:新建课时卡种类 3:学员课时卡 4:课时卡管理(45:VS)
        rvGallery.setAdapter(mCardsAdapter);
        // rvGallery绑定scale效果
        mCardScaleHelper = new CardScaleHelper();
        mCardScaleHelper.attachToRecyclerView(rvGallery);
        rvGallery.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mListPost == mCardScaleHelper.getCurrentItemPos()) return;
                    mListPost = mCardScaleHelper.getCurrentItemPos();
                    LogUtils.d("CourseVsActivity", mListPost + "" + newState);
                    getCardResource(mListPost);
                }
            }
        });
        initRecyclerView(rvCourseList, true);
        courseVsAdapter = new CourserVsAdapter(courseAllList, this, this);
        rvCourseList.setAdapter(courseVsAdapter);
        rvCourseList.addItemDecoration(new DefaultItemDecoration(
                ContextCompat.getColor(this, R.color.col_e8), 0,
                2, -1));
    }

    @Override
    public void initData() {
        orgId = UserManager.getInstance().getOrgId();
        String status = "1";// 课程卡的状态 0：不显示 1：活跃 2：非活跃
        getOrgExistCard(status);//31.获取机构现有的课时卡
        getCourses();//37.获取机构的所有排课或班级
    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }

    /**
     * 31.获取机构现有的课时卡
     * haoruigang on 2018-8-17 17:53:42
     *
     * @param status 课程卡的状态 1：活跃 2：非活跃
     */
    private void getOrgExistCard(String status) {
        HttpManager.getInstance().doGetOrgCard("CourseVsActivity", orgId, status,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        mListCard.clear();
                        mListCard.addAll(data.getData());
                        mCardsAdapter.updateStatus(mListCard, "0");
                        if (mListCard.size() > 0) {
                            getCardResource(mListPost);//17.获取课时卡所关联的排课列表
                            loadView.delayShowContainer(true);
                        } else {
                            loadView.setNoShown(true);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CourseVsActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourseVsActivity.this);
                        } else {
                            U.showToast(errorMsg);
                            loadView.setErrorShown(true, v -> getOrgExistCard(status));
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.tag(" throwable " + throwable.getMessage());
                        loadView.setErrorShown(true, v -> getOrgExistCard(status));
                    }
                });
    }

    /**
     * 37.获取机构的所有排课或班级
     * haoruigang on 2018-11-28 11:46:15
     */
    private void getCourses() {
        String searchText = "";
        HttpManager.getInstance().doGetCourses("CourseVsActivity",
                orgId, searchText, new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        courseAllList.clear();
                        courseAllList.addAll(data.getData());
                        getContacts(courseAllList);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CourseVsActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourseVsActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.tag(" throwable " + throwable);
                    }
                });
    }

    /**
     * 从服务器获取数据并排序
     */
    public static void getContacts(List<CourseEntity> data) {
        //按孩子id排序
        Collections.sort(data, (o1, o2) -> o1.getClassifyId().compareToIgnoreCase(o2.getClassifyId()));
    }

    /**
     * 17.获取课时卡所关联的排课列表
     *
     * @param mListPost 当前课时卡坐标
     */
    private void getCardResource(int mListPost) {
        String cardId = mListCard.get(mListPost).getOrgCardId();
        HttpManager.getInstance().doGetCardResource("CourseVsActivity", cardId, orgId,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        courseVsList.clear();
                        courseVsList.addAll(data.getData());
                        courseVsAdapter.setData(courseAllList, courseVsList, mListCard.get(mListPost));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CourseVsActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourseVsActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.tag(" throwable " + throwable);
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setOnItemClickListener(View view, int postion) {
        //点击让这个Item突出显示
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation
                (this, view, getString(R.string.transition_name));
        UserManager.getInstance().setCourVsview(view);
        CourseEntity courseEntity = courseAllList.get(postion);
        EditText etNumber = view.findViewById(R.id.et_number);
        CheckBox tvSelectCourse = view.findViewById(R.id.tv_select_course);
        startActivity(new Intent(this, CourseVsEditActivity.class)
                        .putExtra("courseCardEntity", mListCard.get(mListPost))// 卡片类型
                        .putExtra("courseEntity", courseEntity)// 卡片信息
                        .putExtra("etNumber", etNumber.getText().toString().trim())// 卡片值
                        .putExtra("tvSelectCourse", tvSelectCourse.isSelected())// 年卡是否选中
                , options.toBundle());
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        if (event.getWhat() == REFERSH_COURSE_WORK) {
            courseVsAdapter.refresh((CourseEntity) event.getObj(), (Boolean) event.getObj1());
        }
    }
}
