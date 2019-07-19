package com.caregrowtht.app.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.ArtificialAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CardInfoEntity;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.LoadingFrameView;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 人工消课
 */
public class ArtificialActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.x_recycler_view)
    RecyclerView xRecyclerView;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    ArtificialAdapter mCardsAdapter;
    List<CourseEntity> mListCard = new ArrayList<>();
    private CourseEntity courseEntity;
    private String orgId, courseId, stuId;
    ArrayList<CardInfoEntity> cardInfoList = new ArrayList<>();
    private MessageEntity msgEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_artificial;
    }

    @Override
    public void initView() {
        tvTitle.setText("人工消课");
        initRecyclerView(xRecyclerView, true);
        mCardsAdapter = new ArtificialAdapter(mListCard, this, this);
        xRecyclerView.setAdapter(mCardsAdapter);
    }

    @Override
    public void initData() {
        courseEntity = (CourseEntity) getIntent().getSerializableExtra("courseEntity");
        if (StrUtils.isNotEmpty(courseEntity)) {
            courseId = courseEntity.getCourseId();
            stuId = courseEntity.getStudentId();
            orgId = UserManager.getInstance().getOrgId();
        }
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (msgEntity != null) {
            courseId = msgEntity.getTargetId();
            stuId = msgEntity.getChildId();
            orgId = msgEntity.getOrgId();
            UserManager.getInstance().setOrgId(orgId);
        }
        getStudentCourseCard();
    }

    private void getStudentCourseCard() {
        HttpManager.getInstance().doGetStuCard("ArtificialActivity",
                orgId, courseId, stuId, new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        mListCard.clear();
                        mListCard.addAll(data.getData());
                        mCardsAdapter.update(mListCard);
                        if (mListCard.size() > 0) {
                            loadView.delayShowContainer(true);
                        } else {
                            loadView.setNoShown(true);
                            loadView.setNoInfo("无可用课时卡");
                            btnCancel.setVisibility(View.GONE);
                            btnSubmit.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("ArtificialActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ArtificialActivity.this);
                        } else {
                            loadView.setErrorShown(true, v -> getStudentCourseCard());
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("ArtificialActivity onError", throwable.getMessage());
                        loadView.setErrorShown(true, v -> getStudentCourseCard());
                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.btn_cancel, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_submit:
                peopleCostLes();
                break;
        }
    }

    private void peopleCostLes() {
        if (getCardInfo()) {
            U.showToast("所选课时卡填写信息不完整");
            return;
        }
        String cardInfo = new Gson().toJson(cardInfoList);
        HttpManager.getInstance().doPeopleCostLes("ArtificialActivity", orgId, courseId,
                cardInfo, new HttpCallBack<BaseDataModel<CourseEntity>>(this) {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_ELIMINATE));
                        U.showToast("消课成功");
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("ArtificialActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ArtificialActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("ArtificialActivity onError", throwable.getMessage());
                    }
                });
    }

    /**
     * 获取消课上传参数 json
     *
     * @return true:课时卡不完整 false
     */
    private boolean getCardInfo() {
        cardInfoList.clear();
        HashMap<Integer, CourseEntity> mCourseModels = mCardsAdapter.getIsCourseCardEntity();
        HashMap<Integer, String> mCount = mCardsAdapter.getCount();
        for (int j = 0; j < mCourseModels.size(); j++) {
            CourseEntity courseEntity = mCourseModels.get(j);
            String count = mCount.get(j);
            if (courseEntity != null) {
                if (!TextUtils.isEmpty(count) || courseEntity.getCardType().equals("3")) {// 是年卡
                    CardInfoEntity cardInfo = new CardInfoEntity();
                    cardInfo.setCardType(courseEntity.getCardType());
                    cardInfo.setChildId(stuId);
                    cardInfo.setChildCardId(courseEntity.getCourseCardId());
                    // 卡的类型 1：次卡 2：储值卡 3：年卡 4：折扣卡
                    if (courseEntity.getCardType().equals("1")) {
                        cardInfo.setUseNum(count);
                        cardInfo.setUsePrice("");
                        cardInfo.setDiscount("");
                    } else {// 不会出现年卡（出现全传空）
                        cardInfo.setUseNum("");
                        cardInfo.setUsePrice(count);
                        if (courseEntity.getCardType().equals("4")) {
                            cardInfo.setDiscount(courseEntity.getDiscount());
                        } else {
                            cardInfo.setDiscount("");
                        }
                    }
                    cardInfoList.add(cardInfo);
                } else {
                    return true;
                }
            }
        }
        return cardInfoList.size() <= 0;
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        mCardsAdapter.getSelect(position,
                view.findViewById(R.id.tv_select_card),
                view.findViewById(R.id.rl_select));
    }
}
