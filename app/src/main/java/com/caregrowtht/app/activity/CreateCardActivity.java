package com.caregrowtht.app.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.android.library.view.CircleImageView;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.WorkClassAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.NewCardEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-8-17 11:07:47
 * 新建课时卡
 */
public class CreateCardActivity extends BaseActivity {

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_card_bg)
    RelativeLayout rlCardBg;
    @BindView(R.id.ll_head)
    LinearLayout llHead;
    @BindView(R.id.tv_card_name)
    TextView tvCardName;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_card_type)
    TextView tvCardType;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.rl_card_time)
    RelativeLayout rlCardTime;
    @BindView(R.id.line)
    ImageView Line;
    @BindView(R.id.tv_card_time_name)
    TextView tvCardTimeName;
    @BindView(R.id.et_card_name)
    EditText etCardName;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.et_price)
    EditText etPrice;
    @BindView(R.id.tv_price_unit)
    TextView tvPriceUnit;
    @BindView(R.id.iv_line1)
    ImageView ivLine1;
    @BindView(R.id.tv_reality)
    TextView tvReality;
    @BindView(R.id.et_reality)
    EditText etReality;
    @BindView(R.id.tv_reality_unit)
    TextView tvRealityUnit;
    @BindView(R.id.rl_card_count)
    RelativeLayout rlCardCount;
    @BindView(R.id.tv_surplus)
    TextView tvSurplus;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.rb_month)
    CheckBox rbMonth;
    @BindView(R.id.tv_month)
    EditText etMonth;
    @BindView(R.id.rb_valid)
    CheckBox rbValid;
    @BindView(R.id.rv_work)
    RecyclerView rvWork;
    @BindView(R.id.cl_work_class)
    ConstraintLayout clWorkClass;

    private CourseEntity cardsEntity;

    //选中课程的信息
    private List<CourseEntity> mCourseModels = new ArrayList<>();
    //上次选中课时卡的信息
    private List<CourseEntity> mCourses = new ArrayList<>();
    //选中课程的消课次数
    private List<CourseEntity> mCount = new ArrayList<>();

    private String cardPrice;
    private String realityPrice;
    private String addType;

    private String orgId;
    private String cardType;
    private String cardName;
    private Boolean validTerm = false;//默认非长期有效
    private String price;
    private String countPriceDiscount;
    private StudentEntity stuDetails;
    private String courses = "";

    private WorkClassAdapter mAdapter;
    private String cardOperaType;

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_card;
    }

    @Override
    public void initView() {
        ivLeft.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_close_1));

        cardsEntity = (CourseEntity) getIntent().getSerializableExtra("CourseCardsEntity");
        stuDetails = (StudentEntity) getIntent().getSerializableExtra("StudentEntity");
        addType = getIntent().getStringExtra("addType");// 学员添加新卡的类型 4:新建课时卡 5:编辑课时卡管理
        cardOperaType = getIntent().getStringExtra("cardOperaType");//cardOperaType 1:选择购买新卡 2:选择课时卡 3:新建课时卡管理
        MessageEntity msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (msgEntity != null) {
            orgId = msgEntity.getOrgId();
        } else {
            orgId = UserManager.getInstance().getOrgId();
        }

        String validMonth = cardsEntity.getValidMonth();
        if (TextUtils.equals(addType, "4")) {// 4：新建课时卡
            tvTitle.setText("新建课时卡");
        } else if (TextUtils.equals(addType, "5")) {//5:编辑课时卡管理
            tvTitle.setText("编辑课时卡");
            getCardResource();//17.获取课时卡所关联的排课列表
        }
        tvTime.setText(TextUtils.equals(validMonth, "0") ? "长期有效" : String.format("有效期:\t%s", cardsEntity.getValidMonth() + "个月"));//卡的有效月数 长期有效返回 字符串 "0"

        final TextView tvCardPrice = findViewById(R.id.tv_card_price);
        final TextView tvRealityPrice = findViewById(R.id.tv_reality_price);

        int textColor = 0;//机构名称色值
        int bgRes = 0;//背景id
        int headRes = 0;//头像背景
        int typeRes = 0;
        int timeRes = 0;
        // 卡的类型 1：次卡 2：储值卡 3：年卡 4：折扣卡
        if (!TextUtils.isEmpty(cardsEntity.getCardType())) {
            switch (cardsEntity.getCardType()) {
                case "1":
//                    tvTitle.setText("新建次数卡");
                    String totalCount = cardsEntity.getTotalCount();
                    realityPrice = totalCount + "次";

                    tvCardType.setText("次数卡");
                    cardPrice = String.valueOf(Integer.parseInt(cardsEntity.getCardPrice()) / 100);
                    etCardName.setHint("例如 某某课100次卡");

                    if (TextUtils.equals(addType, "5")) {//编辑课时卡管理
                        etPrice.setText(cardPrice);
                        etReality.setText(totalCount);
                        tvSurplus.setText(String.format("单价：售卡金额%s元/次数%s次",
                                cardPrice, totalCount));
                        tvTotal.setText(String.format("%s元", Integer.parseInt(cardPrice)
                                / Integer.parseInt(TextUtils.isEmpty(totalCount) ? "1"
                                : totalCount)));
                    }

                    textColor = R.color.color_caa0;
                    bgRes = R.mipmap.ic_card_num_bg;
                    headRes = R.mipmap.ic_card_num_head;
                    typeRes = R.mipmap.ic_card_num_type;
                    timeRes = R.mipmap.ic_card_num_time;
                    break;
                case "2":
//                    tvTitle.setText("新建储值卡");
                    String price2 = String.valueOf(Integer.parseInt(cardsEntity.getRealityPrice()) / 100);
                    if (!TextUtils.equals(addType, "5")) {//编辑课时卡管理
                        String balance = TextUtils.isEmpty(cardsEntity.getBalance()) ? "0" : cardsEntity.getBalance();
                        realityPrice = "¥" + String.valueOf(Integer.parseInt(balance) / 100)
                                + "/¥" + price2 + "元";
                        tvRealityPrice.setText("余额/实得金额");
                    }

                    tvCardType.setText("储值卡");
                    cardPrice = String.valueOf(Integer.parseInt(cardsEntity.getCardPrice()) / 100);
                    etCardName.setHint("例如 某某课5000元卡");

                    tvPrice.setText("售卡金额");
                    tvReality.setText("实得金额");
                    tvRealityUnit.setText("元");
                    rlCardCount.setVisibility(View.GONE);

                    if (TextUtils.equals(addType, "5")) {//编辑课时卡管理
                        realityPrice = "¥" + price2 + "元";
                        tvRealityPrice.setText("实得金额");
                        etPrice.setText(cardPrice);
                        etReality.setText(price2);
                    }

                    tvCardPrice.setVisibility(View.VISIBLE);
                    tvRealityPrice.setVisibility(View.VISIBLE);
                    textColor = R.color.color_93ca;
                    bgRes = R.mipmap.ic_card_save_bg;
                    headRes = R.mipmap.ic_card_save_head;
                    typeRes = R.mipmap.ic_card_save_type;
                    timeRes = R.mipmap.ic_card_save_time;
                    break;
                case "3":
                    if (!TextUtils.isEmpty(cardsEntity.getEndTime())) {
                        //年卡类型的卡片：
                        //如果是<=3月的卡，月卡，
                        //>=4, <=9的卡， 算为学期卡
                        //>=10月的卡， 算为年卡。
                        long yy = Long.valueOf(DateUtil.getDate(Long.valueOf(cardsEntity.getEndTime()), "yyyy")) - Long.valueOf(DateUtil.getSysTimeType("yyyy")) - 1;//年 -1减去今年
                        long m = 0;
                        if (yy > 0) {
                            m = yy * 12;//月
                        }
                        long mm = m + (12 - Long.valueOf(DateUtil.getSysTimeType("MM"))) + Long.valueOf(DateUtil.getDate(Long.valueOf(cardsEntity.getEndTime()), "MM"));//月
                        if (mm <= 3) {
                            tvCardType.setText("月卡");
                        } else if (mm <= 9) {
                            tvCardType.setText("学期卡");
                        } else {
                            tvCardType.setText("年卡");
                        }
                    } else {
                        tvCardType.setText("年卡");
                    }
//                    tvTitle.setText("新建年卡");
                    realityPrice = String.format("%s", TextUtils.equals(validMonth, "0") ?
                            "长期有效" : cardsEntity.getValidMonth() + "个月");

                    cardPrice = String.valueOf(Integer.parseInt(cardsEntity.getCardPrice()) / 100);
                    etCardName.setHint("例如 某某课年卡");

                    tvPrice.setText("课时卡总价");
                    ivLine1.setVisibility(View.GONE);
                    tvReality.setVisibility(View.GONE);
                    etReality.setVisibility(View.GONE);
                    tvRealityUnit.setVisibility(View.GONE);
                    rlCardCount.setVisibility(View.GONE);

                    if (TextUtils.equals(addType, "5")) {//编辑课时卡管理
                        etPrice.setText(cardPrice);
                    }


                    textColor = R.color.color_57a1;
                    bgRes = R.mipmap.ic_card_year_bg;
                    headRes = R.mipmap.ic_card_year_head;
                    typeRes = R.mipmap.ic_card_year_type;
                    timeRes = R.mipmap.ic_card_year_time;
                    break;
                case "4":
//                    tvTitle.setText("新建折扣卡");
                    realityPrice = "¥" + String.valueOf(Integer.parseInt(cardsEntity.getRealityPrice()) / 100) + "元";

                    tvCardType.setText("折扣卡");
                    String price4 = String.valueOf(Integer.parseInt(cardsEntity.getCardPrice()) / 100);
                    String disCount = cardsEntity.getDiscount();
                    cardPrice = String.format("%s\t\t%s折", price4, disCount);
                    etCardName.setHint("例如 某某课7折卡");

                    tvPrice.setText("售卡金额");
                    tvReality.setText("折扣");
                    tvRealityUnit.setText("折");
                    rlCardCount.setVisibility(View.GONE);

                    if (TextUtils.equals(addType, "5")) {//编辑课时卡管理
                        etPrice.setText(price4);
                        etReality.setText(disCount);
                    }


                    tvCardPrice.setVisibility(View.VISIBLE);
                    tvRealityPrice.setVisibility(View.VISIBLE);
                    textColor = R.color.color_e38f;
                    bgRes = R.mipmap.ic_card_dis_bg;
                    headRes = R.mipmap.ic_card_dis_head;
                    typeRes = R.mipmap.ic_card_dis_type;
                    timeRes = R.mipmap.ic_card_dis_time;
                    break;
            }
        }
        tvCardPrice.setTextColor(textColor);
        tvRealityPrice.setTextColor(textColor);
        String cardName = cardsEntity.getCardName();
        tvCardName.setText(cardName);
        tvMoney.setText(String.format("¥%s", cardPrice));
        tvNum.setText(realityPrice);
        if (!TextUtils.equals(addType, "4")) {// 4：新建课时卡
            etCardName.setText(cardName);
        }

        final CircleImageView ivHeadIcon = findViewById(R.id.iv_head_icon);

        if (!TextUtils.isEmpty(cardsEntity.getOrgImage())) {
            GlideUtils.setGlideImg(this, cardsEntity.getOrgImage(), R.mipmap.ic_logo_default, ivHeadIcon);
            tvName.setVisibility(View.GONE);
            ivHeadIcon.setVisibility(View.VISIBLE);
        } else {
            ivHeadIcon.setVisibility(View.GONE);
            tvName.setVisibility(View.VISIBLE);
            String orgShortName = cardsEntity.getOrgShortName();
            UserManager.getInstance().getOrgShortName(tvName, orgShortName);// 设置机构简称
            tvName.setTextColor(getResources().getColor(textColor));
        }
        rlCardBg.setBackgroundResource(bgRes);
        llHead.setBackgroundResource(headRes);
        tvCardType.setBackgroundResource(typeRes);
        rlCardTime.setBackgroundResource(timeRes);
        View.OnClickListener clickMonth = v -> validMoth();
        etMonth.setOnFocusChangeListener((v, hasFocus) -> {
            validMoth();
        });
        etMonth.setOnClickListener(clickMonth);
        rbMonth.setOnClickListener(clickMonth);
        rbValid.setOnClickListener(v -> validTime());
        if (TextUtils.equals(validMonth, "0")) {
            validTime();
        } else {
            etMonth.setText(validMonth);
            validMoth();
        }
        etMonth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(StrUtils.getReplaceTrim(etMonth.getText().toString()))) {
                    tvTime.setText(String.format("有效期:\t%s",
                            StrUtils.getReplaceTrim(etMonth.getText().toString()) + "个月"));//卡的有效月数
                }
            }
        });
        etCardName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(StrUtils.getReplaceTrim(etCardName.getText().toString()))) {
                    tvCardName.setText(String.format("%s",
                            StrUtils.getReplaceTrim(etCardName.getText().toString())));
                }
            }
        });
        etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(StrUtils.getReplaceTrim(s.toString()))) {
                    tvSurplus.setText(String.format("单价：售卡金额%s元/次数%s次",
                            StrUtils.getReplaceTrim(s.toString()),
                            StrUtils.getReplaceTrim(etReality.getText().toString())));
                    tvTotal.setText(String.format("%s元", Integer.parseInt(StrUtils.getReplaceTrim(s.toString()))
                            / Integer.parseInt(TextUtils.isEmpty(
                            StrUtils.getReplaceTrim(etReality.getText().toString())) ? "1"
                            : StrUtils.getReplaceTrim(etReality.getText().toString()))));
                    // 卡的类型 1：次卡 2：储值卡 3：年卡 4：折扣卡
                    if (cardsEntity.getCardType().equals("4")) {
                        cardPrice = String.format("%s\t\t%s折", StrUtils.getReplaceTrim(s.toString()),
                                etReality.getText());
                        tvMoney.setText(String.format("¥%s", cardPrice));
                        tvNum.setText(String.format("¥%s", cardPrice));
                    } else {
                        tvMoney.setText(String.format("¥%s", StrUtils.getReplaceTrim(s.toString())));
                    }
                }
            }
        });
        etReality.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(StrUtils.getReplaceTrim(s.toString()))) {
                    tvSurplus.setText(String.format("单价：售卡金额%s元/次数%s次",
                            etPrice.getText(), StrUtils.getReplaceTrim(s.toString())));
                    tvTotal.setText(String.format("%s元", Double.parseDouble(TextUtils.isEmpty(
                            StrUtils.getReplaceTrim(etPrice.getText().toString()))
                            ? "1" : StrUtils.getReplaceTrim(etPrice.getText().toString()))
                            / Double.parseDouble(StrUtils.getReplaceTrim(s.toString()))));
                    // 卡的类型 1：次卡 2：储值卡 3：年卡 4：折扣卡
                    if (cardsEntity.getCardType().equals("1")) {
                        tvNum.setText(String.format("%s次", StrUtils.getReplaceTrim(s.toString())));
                    } else if (cardsEntity.getCardType().equals("4")) {
                        cardPrice = String.format("%s\t\t%s折", etPrice.getText(),
                                StrUtils.getReplaceTrim(s.toString()));
                        tvMoney.setText(String.format("¥%s", cardPrice));
                    } else {
                        tvNum.setText(String.format("¥%s", StrUtils.getReplaceTrim(s.toString())));
                    }
                }
            }
        });
        initRecyclerGrid(rvWork, 2);
        mAdapter = new WorkClassAdapter(mCourseModels, mCount, this);
        rvWork.setAdapter(mAdapter);
    }

    private void validTime() {
        etMonth.setFocusable(true);
        etMonth.setFocusableInTouchMode(true);
        rbMonth.setSelected(false);
        rbValid.setSelected(true);
        validTerm = true;
        tvTime.setText("长期有效");//长期有效
    }

    private void validMoth() {
//        etMonth.setFocusable(false);
//        etMonth.setFocusableInTouchMode(false);
        rbMonth.setSelected(true);
        rbValid.setSelected(false);
        validTerm = false;
        tvTime.setText(String.format("有效期:\t%s", etMonth.getText() + "个月"));//卡的有效月数
    }

    @Override
    public void initData() {
        getCourses();
    }

    @OnClick({R.id.rl_back_button, R.id.rl_work_class, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
                break;
            case R.id.rl_work_class:
                startActivityForResult(new Intent(this, WorkClassActivity.class)
                        .putExtra("cardType", cardsEntity.getCardType())
                        .putExtra("mCourses", (Serializable) mCourses)
                        .putExtra("mCount", (Serializable) mCount)
                        .putExtra("addType", addType), 3315);
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
                break;
            case R.id.btn_submit:
                if (TextUtils.equals(addType, "4")) {// 4：新建课时卡
                    addChildCard();
                } else if (TextUtils.equals(addType, "5")) {//5:编辑课时卡管理
                    buyNewCard();
                }
                break;
        }
    }

    private void getCourses() {
        //37.获取机构的所有排课或班级 haoruigang on 2018-8-24 12:43:20
        HttpManager.getInstance().doGetCourses("CreateCardActivity",
                UserManager.getInstance().getOrgId(),
                "", new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        clWorkClass.setVisibility(data.getData().size() > 0 ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CreateCardActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CreateCardActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CreateCardActivity onFail", throwable.getMessage());
                    }
                });
    }

    private void addChildCard() {
        cardType = cardsEntity.getCardType();
        cardName = StrUtils.getReplaceTrim(etCardName.getText().toString());
        if (TextUtils.isEmpty(cardName)) {
            U.showToast("请输入课时卡名字");
            return;
        }
        String validMonth;
        if (validTerm) {//长期有效
            validMonth = "0";
        } else {
            validMonth = StrUtils.getReplaceTrim(etMonth.getText().toString());
            if (TextUtils.isEmpty(validMonth)) {
                U.showToast("请输入有效期");
                return;
            }
        }
        price = StrUtils.getReplaceTrim(etPrice.getText().toString());
        if (TextUtils.isEmpty(price)) {
            U.showToast("请输入售卡金额");
            return;
        }
        countPriceDiscount = StrUtils.getReplaceTrim(etReality.getText().toString());

        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("cardType", cardType);
        map.put("cardName", cardName);
        map.put("validMonth", validMonth);
        map.put("cardPrice", String.valueOf((Integer.valueOf(price) * 100)));
        if (TextUtils.equals(cardsEntity.getCardType(), "1")) {
            if (TextUtils.isEmpty(countPriceDiscount)) {
                U.showToast("请输入次数");
                return;
            }
            map.put("count", countPriceDiscount);
        }
        if (TextUtils.equals(cardsEntity.getCardType(), "2")) {
            if (TextUtils.isEmpty(countPriceDiscount)) {
                U.showToast("请输入金额");
                return;
            }
            map.put("validPrice", String.valueOf((Integer.valueOf(countPriceDiscount) * 100)));
        }
        if (TextUtils.equals(cardsEntity.getCardType(), "4")) {
            if (TextUtils.isEmpty(countPriceDiscount)) {
                U.showToast("请输入折扣");
                return;
            }
            map.put("discount", countPriceDiscount);
        }
        map.put("courses", courses);

        HttpManager.getInstance().doAddChildCard("CreateCardActivity", map,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        U.showToast("操作成功");
                        if (TextUtils.equals(cardOperaType, "1")) {//1:选择购买新卡
                            //学员添加新卡的类型 1：充值缴费 2：购买新卡
                            startActivity(new Intent(CreateCardActivity.this, NewCardBuyActivity.class)
                                    .putExtra("stuDetails", stuDetails)
                                    .putExtra("addType", ""));
                        } else if (TextUtils.equals(cardOperaType, "2")) {
                            startActivity(new Intent(CreateCardActivity.this, TimeCardSelectActivity.class));
                        } else if (TextUtils.equals(cardOperaType, "3")) {
                            startActivity(new Intent(CreateCardActivity.this, CourserCardMsgActivity.class));
                        }
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_NEWCARD));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CreateCardActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CreateCardActivity.this);
                        } else if (statusCode == 1055) {
                            U.showToast("课时卡名字重复!");
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CreateCardActivity onFail", throwable.getMessage());
                    }
                });
    }

    /**
     * 32.购买新的课时卡
     */
    private void buyNewCard() {
        String price = String.valueOf((Integer.valueOf(etPrice.getText().toString()) * 100));//单位分  100元传10000
        String validMonth;
        if (validTerm) {//长期有效
            validMonth = "0";
        } else {
            validMonth = StrUtils.getReplaceTrim(etMonth.getText().toString());
            if (TextUtils.isEmpty(validMonth)) {
                U.showToast("请输入有效期");
                return;
            }
        }
        String realityCount = StrUtils.getReplaceTrim(etReality.getText().toString());

        HashMap<String, String> map = new HashMap<>();
        map.put("orgCardId", cardsEntity.getOrgCardId());
        map.put("price", price);
        map.put("validMonth", validMonth);
        map.put("cardName", etCardName.getText().toString());
        map.put("orgId", orgId);
        if (TextUtils.equals(cardsEntity.getCardType(), "1")
                || TextUtils.equals(cardsEntity.getCardType(), "4")) {
            map.put("count", realityCount);
        }
        if (TextUtils.equals(cardsEntity.getCardType(), "2")) {
            realityCount = String.valueOf((Integer.valueOf(realityCount) * 100));
            map.put("realityPrice", realityCount);
        }
        map.put("course", courses);
        HttpManager.getInstance().doBuyNewCard("CreateCardActivity", map, new HttpCallBack<CourseEntity>() {
            @Override
            public void onSuccess(CourseEntity data) {
                EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_STUDENT));
                U.showToast("编辑成功");
                finish();
            }

            @Override
            public void onFail(int statusCode, String errorMsg) {
                LogUtils.d("TimeCardBuyActivity onFail", statusCode + ":" + errorMsg);
                if (statusCode == 1002 || statusCode == 1011) {//异地登录
                    U.showToast("该账户在异地登录!");
                    HttpManager.getInstance().dologout(CreateCardActivity.this);
                } else if (statusCode == 1055) {
                    U.showToast("课时卡名字重复!");
                } else {
                    U.showToast(errorMsg);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtils.d("TimeCardBuyActivity Throwable", throwable.getMessage());
            }
        });
    }

    /**
     * 17.获取课时卡所关联的排课列表
     */
    private void getCardResource() {
        HttpManager.getInstance().doGetCardResource("CreateCardActivity", cardsEntity.getOrgCardId()
                , orgId, new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        mCourseModels.clear();
                        mCourseModels.addAll(data.getData());
                        //课时卡
                        //---遍历传递的参数数据--start--
                        ArrayList<NewCardEntity> newCardList = new ArrayList<>();
                        for (int i = 0; i < mCourseModels.size(); i++) {
                            CourseEntity countData = new CourseEntity();
                            String courseCount = "";
                            boolean isAddCour = false;
                            NewCardEntity newCardEntity = new NewCardEntity();
                            newCardEntity.setCourseId(mCourseModels.get(i).getId());
                            if (TextUtils.equals(cardsEntity.getCardType(), "1")) {
                                String singleTimes = mCourseModels.get(i).getSingleTimes();
                                if (!TextUtils.isEmpty(singleTimes) && Double.valueOf(singleTimes) > 0) {
                                    courseCount = singleTimes;
                                    newCardEntity.setCount(courseCount);
                                    newCardEntity.setPrice("");
                                    isAddCour = true;
                                }
                            } else if (TextUtils.equals(cardsEntity.getCardType(), "3")) {//年卡
                                String courseId = mCourseModels.get(i).getCourseId();
                                if (!TextUtils.isEmpty(courseId)) {//课时卡id有值是年卡
                                    newCardEntity.setCount("");
                                    newCardEntity.setPrice(courseCount);
                                    isAddCour = true;
                                }
                            } else {
                                String singleMoney = mCourseModels.get(i).getSingleMoney();
                                if (!TextUtils.isEmpty(singleMoney) && Double.valueOf(singleMoney) > 0) {
                                    courseCount = String.valueOf((Double.valueOf(singleMoney) / 100));
                                    newCardEntity.setCount("");
                                    newCardEntity.setPrice(courseCount);
                                    isAddCour = true;
                                }
                            }
                            if (isAddCour) {
                                //编辑前已选中的数据
                                newCardList.add(newCardEntity);
                                //显示 start
                                countData.setCourseId(mCourseModels.get(i).getId());
                                countData.setCourseCount(courseCount);
                                mCount.add(countData);
                                mCourses.add(mCourseModels.get(i));
                                //显示 end
                            }
                        }
                        //---遍历传递的参数数据--end--
                        courses = newCardList.size() > 0 ? new Gson().toJson(newCardList) : "";
                        mAdapter.setData(mCourses, mCount, cardsEntity.getCardType(), null);
                        //课时卡
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CreateCardActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CreateCardActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.tag("CreateCardActivity throwable " + throwable);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 3315:
                    this.mCount.clear();
                    this.mCount.addAll((List<CourseEntity>) data.getSerializableExtra("Count"));// 消课次数
                    this.mCourseModels.clear();
                    this.mCourseModels.addAll((List<CourseEntity>) data.getSerializableExtra("CourseEntity"));// 课程信息
                    this.mCourses.clear();
                    this.mCourses.addAll(mCourseModels);//上页选择的内容
                    //---遍历传递的参数数据--start--
                    ArrayList<NewCardEntity> newCardList = new ArrayList<>();
                    for (int i = 0; i < mCourseModels.size(); i++) {
                        NewCardEntity newCardEntity = new NewCardEntity();
                        newCardEntity.setCourseId(mCourseModels.get(i).getCourseId());
                        if (TextUtils.equals(cardsEntity.getCardType(), "1")) {
                            if (!TextUtils.isEmpty(mCount.get(i).getCourseCount())) {
                                newCardEntity.setCount(mCount.get(i).getCourseCount());
                            }
                            newCardEntity.setPrice("");
                        } else {
                            newCardEntity.setCount("");
                            if (!TextUtils.isEmpty(mCount.get(i).getCourseCount())) {
                                newCardEntity.setPrice(String.valueOf((Double.valueOf(mCount.get(i).getCourseCount()) * 100)));
                            }
                        }
                        newCardList.add(newCardEntity);
                    }
                    //---遍历传递的参数数据--end--
                    courses = new Gson().toJson(newCardList);
                    //显示 start
                    //显示 end
                    mAdapter.setData(mCourseModels, mCount, cardsEntity.getCardType(), null);
                    break;
            }
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

}
