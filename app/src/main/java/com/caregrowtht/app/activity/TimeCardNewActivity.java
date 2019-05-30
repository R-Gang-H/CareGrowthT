package com.caregrowtht.app.activity;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.NewCardsAdapter;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-8-9 10:59:43
 * 新建课时卡
 */
public class TimeCardNewActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.x_recycler_view)
    RecyclerView xRecyclerView;

    List<CourseEntity> mListCard = new ArrayList<>();
    NewCardsAdapter mCardsAdapter;

    private StudentEntity stuDetails;
    private String cardOperaType;
    private MessageEntity msgEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_time_card_new;
    }

    @Override
    public void initView() {
        tvTitle.setText("新建课时卡");

        initRecyclerView(xRecyclerView, true);
        mCardsAdapter = new NewCardsAdapter(mListCard, this, this, "2", "");//展示页面 1:选择购买新卡 2:新建课时卡种类 3:学员课时卡
        xRecyclerView.setAdapter(mCardsAdapter);
    }

    @Override
    public void initData() {
        stuDetails = (StudentEntity) getIntent().getSerializableExtra("StudentEntity");
        cardOperaType = getIntent().getStringExtra("cardOperaType");//cardOperaType 1:选择购买新卡 2:选择课时卡 3:新建课时卡管理
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        getOrgExistCard();//31.获取机构现有的课时卡
    }

    private void getOrgExistCard() {

        mListCard.clear();
        setData();//@TODO 制造测试数据
//        mListCard.addAll(data.getCourseCards());
        mCardsAdapter.update(mListCard);

    }

    private void setData() {

        String orgImage = UserManager.getInstance().getOrgEntity().getOrgImage();
        CourseEntity c = new CourseEntity();
        c.setOrgCardId("1");
        c.setCardType("1");//卡的类型 1：次卡 2：储值卡 3：年卡 4：折扣卡
        c.setValidMonth("12");
        c.setDiscount("");//折扣类型的卡片 几折？
        c.setCardPrice("500000");
        c.setRealityPrice("");
        c.setBalance("");//卡内的余额 单位是分
        c.setOrgImage(orgImage);
        c.setOrgShortName("机构简称");
        c.setCardName("英语课50次卡");
        c.setEndTime("");
        c.setTotalCount("50");

        CourseEntity c1 = new CourseEntity();
        c1.setOrgCardId("2");
        c1.setCardType("3");//卡的类型 1：次卡 2：储值卡 3：年卡 4：折扣卡
        c1.setValidMonth("0");//卡的有效月数   长期有效返回 字符串 "0"
        c1.setDiscount("");//折扣类型的卡片 几折？
        c1.setCardPrice("500000");
        c1.setRealityPrice("");
        c1.setBalance("");//卡内的余额 单位是分
        c1.setOrgImage(orgImage);
        c1.setOrgShortName("机构简称");
        c1.setCardName("英语课年卡");
        c1.setEndTime("1560960000");
        c1.setTotalCount("");

        CourseEntity c2 = new CourseEntity();
        c2.setOrgCardId("3");
        c2.setCardType("2");//卡的类型 1：次卡 2：储值卡 3：年卡 4：折扣卡
        c2.setValidMonth("12");//卡的有效月数   长期有效返回 字符串 "0"
        c2.setDiscount("");//折扣类型的卡片 几折？
        c2.setCardPrice("500000");
        c2.setRealityPrice("580000");
        c2.setBalance("42000");//卡内的余额 单位是分
        c2.setOrgImage(orgImage);
        c2.setOrgShortName("机构简称");
        c2.setCardName("英语课储值卡");
        c2.setEndTime("");
        c2.setTotalCount("");

        CourseEntity c3 = new CourseEntity();
        c3.setOrgCardId("4");
        c3.setCardType("4");//卡的类型 1：次卡 2：储值卡 3：年卡 4：折扣卡
        c3.setValidMonth("12");//卡的有效月数   长期有效返回 字符串 "0"
        c3.setDiscount("7.0");//折扣类型的卡片 几折？
        c3.setCardPrice("500000");
        c3.setRealityPrice("500000");
        c3.setBalance("48000");//卡内的余额 单位是分
        c3.setOrgImage(orgImage);
        c3.setOrgShortName("机构简称");
        c3.setCardName("英语课折扣卡");
        c3.setEndTime("");
        c3.setTotalCount("");

        mListCard.add(c);
        mListCard.add(c1);
        mListCard.add(c2);
        mListCard.add(c3);
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
        mCardsAdapter.getSelect(postion, view.findViewById(R.id.tv_card_type_name));
        mCardsAdapter.notifyDataSetChanged();

        //新建课时卡
        startActivity(new Intent(this, CreateCardActivity.class)
                .putExtra("addType", "4")// 学员添加新卡的类型 4：新建课时卡
                .putExtra("msgEntity", msgEntity)
                .putExtra("StudentEntity", stuDetails)// 学员信息
                .putExtra("CourseCardsEntity", mListCard.get(postion))
                .putExtra("cardOperaType", cardOperaType));//cardOperaType 1:选择购买新卡 2:选择课时卡 3:新建课时卡管理
        overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
    }
}
