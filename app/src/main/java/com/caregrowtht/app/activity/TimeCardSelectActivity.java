package com.caregrowtht.app.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.TimeCardsAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-8-28 18:04:16
 * 选择课时卡
 */
public class TimeCardSelectActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.x_recycler_view)
    RecyclerView xRecyclerView;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    List<CourseEntity> mListCard = new ArrayList<>();
    private TimeCardsAdapter mCardsAdapter;

    //编辑上页数据
    List<CourseEntity> mCourseCards;
    List<CourseEntity> countList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_time_card_select;
    }

    @Override
    public void initView() {
        tvTitle.setText("选择课时卡");
        ivLeft.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_close_1));
        initRecyclerView(xRecyclerView, true);
        mCardsAdapter = new TimeCardsAdapter(mListCard, this, this);
        xRecyclerView.setAdapter(mCardsAdapter);
    }

    @Override
    public void initData() {
        mCourseCards = (List<CourseEntity>) getIntent().getSerializableExtra("mCourseModels");
        countList = (List<CourseEntity>) getIntent().getSerializableExtra("CourseEntity");

        getOrgExistCard();//31.获取机构现有的课时卡
    }

    /**
     * 31.获取机构现有的课时卡
     * haoruigang on 2018-8-17 17:53:42
     */
    private void getOrgExistCard() {

        HttpManager.getInstance().doGetOrgCard("TimeCardSelectActivity",
                UserManager.getInstance().getOrgId(), null,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        mListCard.clear();
                        mListCard.addAll(data.getData());
                        setData();//@TODO 制造测试数据
                        mCardsAdapter.update(mListCard, mCourseCards, countList);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("TimeCardSelectActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(TimeCardSelectActivity.this);
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

    private void setData() {
        CourseEntity c4 = new CourseEntity();
        c4.setCardType("5");//卡的类型 1：次卡 2：储值卡 3：年卡 4：折扣卡 5：新增课时卡(手动添加)
        mListCard.add(c4);
    }

    @OnClick({R.id.rl_back_button, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
                break;
            case R.id.btn_submit:
                mCourseCards.clear();
                countList.clear();
                HashMap<Integer, CourseEntity> mCourseModels = mCardsAdapter.getIsCourseCardEntity();
                HashMap<Integer, String> mCount = mCardsAdapter.getCount();
                for (int j = 0; j < mCourseModels.size() - 1; j++) {// 减去创建课时卡按钮+
                    CourseEntity courseEntity = mCourseModels.get(j);
                    String count = mCount.get(j);
                    if (courseEntity != null) {
                        if (!TextUtils.isEmpty(count) || courseEntity.getCardType().equals("3")) {// 是年卡
                            mCourseCards.add(courseEntity);
                            CourseEntity course = new CourseEntity();
                            course.setCourseCount(count);
                            countList.add(course);
                        } else {
                            U.showToast("所选课时卡填写信息不完整");
                            return;
                        }
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("CourseCardsEntity", (Serializable) mCourseCards);
                intent.putExtra("Count", (Serializable) countList);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
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

    @Override
    public void setOnItemClickListener(View view, int position) {
        if (position == (mListCard.size() - 1)) {
            //新建课时卡
            startActivity(new Intent(this, TimeCardNewActivity.class)
                    .putExtra("cardOperaType", "2"));//cardOperaType 1:选择购买新卡 2:选择课时卡
        } else {
            mCardsAdapter.getSelect(position,
                    view.findViewById(R.id.tv_select_card),
                    view.findViewById(R.id.rl_select));
        }
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        if (event.getWhat() == ToUIEvent.REFERSH_NEWCARD) {
            getOrgExistCard();//31.获取机构现有的课时卡
        }
    }
}
