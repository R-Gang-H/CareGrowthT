package com.caregrowtht.app.activity;

import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.NewCardsAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-8-8 11:00:28
 * 购买新卡
 */
public class NewCardBuyActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_card_type)
    TextView tvCardType;
    @BindView(R.id.v_line)
    View vLine;
    @BindView(R.id.x_recycler_view)
    RecyclerView xRecyclerView;

    List<CourseEntity> mListCard = new ArrayList<>();
    NewCardsAdapter mCardsAdapter;
    private String addType;
    private StudentEntity stuDetails;
    private String audit;// 学员审核通过为学员添加课时卡

    @Override
    public int getLayoutId() {
        return R.layout.activity_new_card;
    }

    @Override
    public void initView() {
        stuDetails = (StudentEntity) getIntent().getSerializableExtra("stuDetails");
        addType = getIntent().getStringExtra("addType");
        audit = getIntent().getStringExtra("audit");
        if (TextUtils.equals(addType, "1")) {// 学员添加新卡的类型 1：充值缴费
            tvTitle.setText("选择充值缴费的卡");
            tvCardType.setText(String.format("%s的课时卡", stuDetails.getStuName()));
            tvCardType.setVisibility(View.VISIBLE);
            vLine.setVisibility(View.VISIBLE);
        } else if (TextUtils.equals(addType, "4")) {// 4、选择 添加新课时卡
            tvTitle.setText("添加新课时卡");
        } else if (TextUtils.equals(addType, "5")) {// 5、选择 添加新课时卡
            tvTitle.setText("绑定已有课时卡");
        } else { // 2：购买新卡
            tvTitle.setText("选择购买新卡");
        }

        initRecyclerView(xRecyclerView, true);
        mCardsAdapter = new NewCardsAdapter(mListCard, this, this, "1", addType);//展示页面 1:选择购买新卡 2:新建课时卡种类 3:学员课时卡
        xRecyclerView.setAdapter(mCardsAdapter);
    }

    @Override
    public void initData() {
        if (TextUtils.equals(addType, "1")) {// 学员添加新卡的类型 1：充值缴费
            mListCard.clear();
            for (int i = 0; i < stuDetails.getCourseCards().size(); i++) {//过滤 已解绑/失效的卡片
                if (TextUtils.equals(stuDetails.getCourseCards().get(i).getStatus(), "2")) {//状态 1正常 2解除绑定
                    continue;// 已解绑/失效的卡片 不显示
                }
                mListCard.add(stuDetails.getCourseCards().get(i));
            }
            mCardsAdapter.update(mListCard);
        } else { // 2：购买新卡 4,5
            getOrgExistCard();//31.获取机构现有的课时卡
        }
    }

    /**
     * 31.获取机构现有的课时卡
     * haoruigang on 2018-8-17 17:53:42
     */
    private void getOrgExistCard() {

        HttpManager.getInstance().doGetOrgCard("NewCardBuyActivity",
                UserManager.getInstance().getOrgId(), "1",
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        mListCard.clear();
                        mListCard.addAll(data.getData());
                        setData();//@TODO 制造测试数据
                        mCardsAdapter.update(mListCard);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("StudentDetailsActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(NewCardBuyActivity.this);
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
        if (TextUtils.equals(addType, "2")) {// 2：购买新卡
            mListCard.add(c4);
        }
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
        mCardsAdapter.getSelect(postion, view.findViewById(R.id.tv_select_card));
        mCardsAdapter.notifyDataSetChanged();
        if (postion == (mListCard.size() - 1) && TextUtils.equals(addType, "2")) {// 学员添加新卡的类型 1：充值缴费 2：购买新卡
            //新建课时卡
            startActivity(new Intent(this, TimeCardNewActivity.class)
                    .putExtra("StudentEntity", stuDetails)// 学员信息
                    .putExtra("cardOperaType", "1"));//cardOperaType 1:选择购买新卡 2:选择课时卡
        } else {
            //购买课时卡
            startActivity(new Intent(this, TimeCardBuyActivity.class)
                    .putExtra("addType", addType)// 学员添加新卡的类型 1：充值缴费 2：购买新卡 4、选择 添加新课时卡 5、选择 绑定已有课时卡
                    .putExtra("StudentEntity", stuDetails)// 学员信息
                    .putExtra("audit", audit)// 学员审核通过为学员添加课时卡
                    .putExtra("CourseCardsEntity", mListCard.get(postion)));
            overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
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
