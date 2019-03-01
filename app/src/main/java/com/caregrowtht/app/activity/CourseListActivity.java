package com.caregrowtht.app.activity;

import android.view.View;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.CourseListAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.view.LoadingFrameView;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-8-10 17:59:49
 * 课时卡记录
 */
public class CourseListActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.xrv_course)
    XRecyclerView xrvCourse;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    private String cardId = "0", sid;

    List<CourseEntity> mListCourse = new ArrayList<>();
    CourseListAdapter mCourseListAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_cours_list;
    }

    @Override
    public void initView() {
        tvTitle.setText("课时卡记录");
        cardId = getIntent().getStringExtra("cid");
        sid = getIntent().getStringExtra("sid");
    }

    @Override
    public void initData() {
        iniXrecyclerView(xrvCourse);
        mCourseListAdapter = new CourseListAdapter(mListCourse, this);
        xrvCourse.setAdapter(mCourseListAdapter);
        getGetCardLog(true);
    }

    private void getGetCardLog(Boolean isClear) {
        loadView.setProgressShown(true);
        HttpManager.getInstance().doGetCardLog("CourseListActivity", cardId,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel data) {
                        if (isClear) {
                            mListCourse.clear();
                        }
                        mListCourse.addAll(data.getData());
                        mCourseListAdapter.update(data.getData(), isClear);
                        if (data != null && data.getData().size() > 0) {
                            loadView.delayShowContainer(true);
                        } else {
                            if (isClear) {
                                loadView.setNoShown(true);
                            } else {
                                loadView.delayShowContainer(true);
                            }
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.tag(" errorMsg " + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourseListActivity.this);
                        } else {
                            U.showToast(errorMsg);
                            loadView.setErrorShown(true, v -> getGetCardLog(true));
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.tag(" throwable " + throwable);
                        loadView.setErrorShown(true, v -> getGetCardLog(true));
                    }
                });
    }


    @OnClick({R.id.rl_back_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }

}
