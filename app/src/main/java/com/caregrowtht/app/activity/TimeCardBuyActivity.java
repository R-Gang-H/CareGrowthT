package com.caregrowtht.app.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CardBindEntity;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.NewCardEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import androidx.constraintlayout.widget.ConstraintLayout;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-8-8 17:50:01
 * 购买课时卡
 * 1：充值缴费 2：购买新卡 3：编辑课时卡
 */
public class TimeCardBuyActivity extends BaseActivity {

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
    @BindView(R.id.tv_reality_price)
    TextView tvRealityPrice;
    @BindView(R.id.iv_line)
    ImageView ivLine;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.et_price)
    EditText etPrice;
    @BindView(R.id.tv_price_unit)
    TextView tvPriceUnit;
    @BindView(R.id.tv_reality)
    TextView tvReality;
    @BindView(R.id.et_reality)
    EditText etReality;
    @BindView(R.id.tv_reality_unit)
    TextView tvRealityUnit;
    @BindView(R.id.iv_line2)
    ImageView ivLine2;
    @BindView(R.id.rl_card_count)
    RelativeLayout rlCardCount;
    @BindView(R.id.tv_surplus)
    TextView tvSurplus;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.rl_validity)
    RelativeLayout rlValidity;
    @BindView(R.id.tv_validity_term)
    TextView tvValidityTerm;
    @BindView(R.id.et_validity_term)
    EditText etValidityTerm;
    @BindView(R.id.tv_validity_unit)
    TextView tvValidityTermUnit;
    @BindView(R.id.ll_bind_card)
    LinearLayout llBindCard;
    @BindView(R.id.rb_month)
    CheckBox rbMonth;
    @BindView(R.id.tv_month)
    EditText etMonth;
    @BindView(R.id.rb_valid)
    CheckBox rbValid;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private Boolean validTerm = false;//默认非长期有效

    private StudentEntity stuDetails;
    private CourseEntity cardsEntity;

    private String cardPrice;
    private String realityPrice;
    private String addType;
    private String audit;
    private String dataMonth;
    private String courses;
    private String validMonth;

    @Override
    public int getLayoutId() {
        return R.layout.activity_time_card_buy;
    }

    @Override
    public void initView() {
        ivLeft.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_close_1));

        cardsEntity = (CourseEntity) getIntent().getSerializableExtra("CourseCardsEntity");
        stuDetails = (StudentEntity) getIntent().getSerializableExtra("StudentEntity");
        // @param cardType 卡类型
        // @param addType  操作方式
        addType = getIntent().getStringExtra("addType");// 学员添加新卡的类型 1：充值缴费 2：购买新卡 3：编辑课时卡 4、选择 绑定已有课时卡

        audit = getIntent().getStringExtra("audit");// 学员审核通过为学员添加课时卡

        validMonth = cardsEntity.getValidMonth();
        if (TextUtils.equals(addType, "1") || TextUtils.equals(addType, "3")) {// 1：充值缴费 3：编辑课时卡
            String date = DateUtil.getDate(Long.parseLong(cardsEntity.getEndTime()), "yyyy-MM-dd");
            if (TextUtils.equals(validMonth, "0") || Integer.valueOf(validMonth) < 0) {
                dataMonth = "长期有效";
                rlValidity.setVisibility(View.GONE);
            } else {
                dataMonth = String.format("有效期:\t%s", date);
            }
            etValidityTerm.setFocusable(false);
            etValidityTerm.setOnClickListener(v -> selectDate());
        } else {// 2：购买新卡 4,5
            dataMonth = String.format("有效期:\t%s", TextUtils.equals(validMonth, "0") ? "长期有效" : validMonth + "个月");//卡的有效月数 长期有效返回 字符串 "0"

            // 4,5 绑定学员已有课时卡,添加课时卡
            etValidityTerm.setOnFocusChangeListener((v, hasFocus) -> {

                if (hasFocus) {// 获取焦点
                    if (TextUtils.equals(addType, "4") || TextUtils.equals(addType, "5")) {// 4、选择 添加新课时卡 5、选择 绑定已有课时卡
                        llBindCard.setVisibility(View.VISIBLE);
                        etValidityTerm.setVisibility(View.GONE);
                        tvValidityTermUnit.setVisibility(View.GONE);
                    }
                }
            });
            View.OnClickListener clickMonth = v -> validMoth();
            etMonth.setOnFocusChangeListener((v, hasFocus) -> {
                validMoth();
            });
            etMonth.setOnClickListener(clickMonth);
            rbMonth.setOnClickListener(clickMonth);
            rbValid.setOnClickListener(v -> validTime());
            if (TextUtils.equals(validMonth, "0")) {
                validTime();
                etMonth.setText(validMonth);
                etValidityTerm.setFocusable(false);
            } else {
                etMonth.setText(validMonth);
                validMoth();
                etValidityTerm.setFocusable(true);
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
                    if (!TextUtils.isEmpty(s)) {
                        if (s.toString().trim().equals("0")) {
                            U.showToast("请输入大于0的有效期");
                        }
                        if (cardsEntity.getCardType().equals("3")) {// 年卡
                            String validity = String.format("%s", TextUtils.equals(s, "0") ? "长期有效" : s + "个月");
                            tvNum.setText(validity);
                        }
                        tvTime.setText(String.format("有效期:\t%s", etMonth.getText() + "个月"));//卡的有效月数
                    }
                }
            });
        }
        tvTime.setText(dataMonth);

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
                    if (TextUtils.equals(addType, "1")) {// 1：充值缴费
                        tvTitle.setText("次数卡充值续费");
                        realityPrice = String.format("%s/%s次", cardsEntity.getLeftCount(), cardsEntity.getTotalCount());
                        tvPrice.setText("新购买金额");
                        tvReality.setText("新实得次数");
                        rlCardCount.setVisibility(View.VISIBLE);
                        tvValidityTerm.setText("新有效期至");
                        tvValidityTermUnit.setVisibility(View.GONE);
                    } else if (TextUtils.equals(addType, "2") || TextUtils.equals(addType, "4") || TextUtils.equals(addType, "5")) {// 2：购买新卡 4,5
                        String reality = "实得次数";
                        if (TextUtils.equals(addType, "2")) {
                            tvTitle.setText("购买新次数卡");
                        } else if (TextUtils.equals(addType, "4")) {
                            tvTitle.setText("添加新次数卡");
                        } else if (TextUtils.equals(addType, "5")) {
                            tvTitle.setText("绑定已有次数卡");
                            tvPrice.setVisibility(View.GONE);
                            etPrice.setVisibility(View.GONE);
                            tvPriceUnit.setVisibility(View.GONE);
                            ivLine.setVisibility(View.GONE);
                            reality = "剩余次数";
                        }
                        realityPrice = cardsEntity.getTotalCount() + "次";
                        tvReality.setText(reality);
                    } else if (TextUtils.equals(addType, "3")) {// 3：编辑课时卡
                        tvTitle.setText("编辑次数卡");
                        realityPrice = String.format("%s/%s次", cardsEntity.getLeftCount(), cardsEntity.getTotalCount());
                        tvPrice.setVisibility(View.GONE);
                        etPrice.setVisibility(View.GONE);
                        tvPriceUnit.setVisibility(View.GONE);
                        ivLine.setVisibility(View.GONE);
                        tvReality.setText("修改剩余次数");
                        tvValidityTerm.setText("修改有效期至");
                        tvValidityTermUnit.setVisibility(View.GONE);
                    }
                    tvCardType.setText("次数卡");
                    cardPrice = String.valueOf(Integer.parseInt(cardsEntity.getCardPrice()) / 100);
                    tvReality.setVisibility(View.VISIBLE);
                    etReality.setVisibility(View.VISIBLE);
                    tvRealityUnit.setVisibility(View.VISIBLE);
                    ivLine2.setVisibility(View.VISIBLE);
                    tvRealityUnit.setText("次");
                    textColor = R.color.color_caa0;
                    bgRes = R.mipmap.ic_card_num_bg;
                    headRes = R.mipmap.ic_card_num_head;
                    typeRes = R.mipmap.ic_card_num_type;
                    timeRes = R.mipmap.ic_card_num_time;
                    break;
                case "2":
                    tvReality.setVisibility(View.VISIBLE);
                    etReality.setVisibility(View.VISIBLE);
                    tvRealityUnit.setVisibility(View.VISIBLE);
                    ivLine2.setVisibility(View.VISIBLE);
                    if (TextUtils.equals(addType, "1")) {// 1：充值缴费
                        tvTitle.setText("储值卡充值续费");
                        realityPrice = "¥" + String.valueOf(Integer.parseInt(cardsEntity.getBalance()) / 100)
                                + "/¥" + String.valueOf(Integer.parseInt(cardsEntity.getRealityPrice()) / 100) + "元";
                        tvRealityPrice.setText("余额/实得金额");
                        tvPrice.setText("新购买金额");
                        tvReality.setText("新实得金额");
                        rlCardCount.setVisibility(View.VISIBLE);
                        tvSurplus.setText("原卡余额:200元");
                        tvTotal.setText("总金额:6000元");
                        tvValidityTerm.setText("新有效期至");
                        tvValidityTermUnit.setVisibility(View.GONE);
                    } else if (TextUtils.equals(addType, "2") || TextUtils.equals(addType, "4") || TextUtils.equals(addType, "5")) {// 2：购买新卡 4,5
                        String reality = "实得金额";
                        if (TextUtils.equals(addType, "2")) {
                            tvTitle.setText("购买新储值卡");
                        } else if (TextUtils.equals(addType, "4")) {
                            tvTitle.setText("添加新储值卡");
                        } else if (TextUtils.equals(addType, "5")) {
                            tvTitle.setText("绑定已有储值卡");
                            tvPrice.setVisibility(View.GONE);
                            etPrice.setVisibility(View.GONE);
                            tvPriceUnit.setVisibility(View.GONE);
                            ivLine.setVisibility(View.GONE);
                            reality = "剩余金额";
                        }
                        realityPrice = "¥" + String.valueOf(Integer.parseInt(cardsEntity.getRealityPrice()) / 100) + "元";
                        tvReality.setText(reality);
                    } else if (TextUtils.equals(addType, "3")) {// 3：编辑课时卡
                        tvTitle.setText("编辑储值卡");
                        realityPrice = "¥" + String.valueOf(Integer.parseInt(cardsEntity.getBalance()) / 100)
                                + "/¥" + String.valueOf(Integer.parseInt(cardsEntity.getRealityPrice()) / 100) + "元";
                        tvRealityPrice.setText("余额/实得金额");
                        tvPrice.setText("修改剩余金额");
                        tvReality.setVisibility(View.GONE);
                        etReality.setVisibility(View.GONE);
                        tvRealityUnit.setVisibility(View.GONE);
                        ivLine2.setVisibility(View.GONE);
                        tvValidityTerm.setText("修改有效期至");
                        tvValidityTermUnit.setVisibility(View.GONE);
                    }
                    tvCardType.setText("储值卡");
                    cardPrice = String.valueOf(Integer.parseInt(cardsEntity.getCardPrice()) / 100);
                    tvRealityUnit.setText("元");
                    tvCardPrice.setVisibility(View.VISIBLE);
                    tvRealityPrice.setVisibility(View.VISIBLE);
                    textColor = R.color.color_93ca;
                    bgRes = R.mipmap.ic_card_save_bg;
                    headRes = R.mipmap.ic_card_save_head;
                    typeRes = R.mipmap.ic_card_save_type;
                    timeRes = R.mipmap.ic_card_save_time;
                    break;
                case "3":
                    tvReality.setVisibility(View.GONE);
                    etReality.setVisibility(View.GONE);
                    tvRealityUnit.setVisibility(View.GONE);
                    ivLine2.setVisibility(View.GONE);
                    long mm;
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
                        mm = m + (12 - Long.valueOf(DateUtil.getSysTimeType("MM"))) + Long.valueOf(DateUtil.getDate(Long.valueOf(cardsEntity.getEndTime()), "MM"));//月
                    } else {
                        mm = Long.valueOf(cardsEntity.getValidMonth());
                    }
                    if (mm <= 3) {
                        tvCardType.setText("月卡");
                    } else if (mm >= 4 && mm <= 9) {
                        tvCardType.setText("学期卡");
                    } else if (mm >= 10) {
                        tvCardType.setText("年卡");
                    } else {
                        tvCardType.setText("折扣卡");
                    }
                    tvCardType.setText(TextUtils.equals(validMonth, "0") ? "年卡" : tvCardType.getText());
                    if (TextUtils.equals(addType, "1")) {// 1：充值缴费
                        tvTitle.setText("月/学期/年卡充值续费");
                        realityPrice = String.format("%s", TextUtils.equals(validMonth, "0") ?
                                "长期有效" : cardsEntity.getValidMonth() + "个月");
                        tvPrice.setText("新购买金额");
                        rlCardCount.setVisibility(TextUtils.equals(validMonth, "0") ? View.GONE : View.VISIBLE);
                        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.topToBottom = R.id.rl_validity;
                        rlCardCount.setLayoutParams(params);
                        tvSurplus.setText("原有效期至:2018-12-31");
                        tvTotal.setText("新有效期至:2020-12-31");
                        ConstraintLayout.LayoutParams params2 = new ConstraintLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params2.leftMargin = 10;
                        params2.topMargin = 32;
                        params2.height = tvPrice.getLayoutParams().height;
                        params2.topToBottom = R.id.tv_reality;
                        rlValidity.setLayoutParams(params2);
                        tvValidityTerm.setText("新卡有效期");
                        tvValidityTermUnit.setVisibility(View.GONE);
                    } else if (TextUtils.equals(addType, "2") || TextUtils.equals(addType, "4") || TextUtils.equals(addType, "5")) {// 2：购买新卡 4,5
                        if (TextUtils.equals(addType, "2")) {
                            tvTitle.setText("购买新年卡");
                        } else if (TextUtils.equals(addType, "4")) {
                            tvTitle.setText("添加新年卡");
                        } else if (TextUtils.equals(addType, "5")) {
                            tvTitle.setText("绑定已有年卡");
                            tvPrice.setVisibility(View.GONE);
                            etPrice.setVisibility(View.GONE);
                            tvPriceUnit.setVisibility(View.GONE);
                            ivLine.setVisibility(View.GONE);
                        }
                        realityPrice = String.format("%s", TextUtils.equals(validMonth, "0") ?
                                "长期有效" : cardsEntity.getValidMonth() + "个月");
                    } else if (TextUtils.equals(addType, "3")) {// 3：编辑课时卡
                        tvTitle.setText(String.format("编辑%s", tvCardType.getText()));
                        realityPrice = String.format("%s", TextUtils.equals(validMonth, "0") ?
                                "长期有效" : cardsEntity.getPassMonth() + "/" + cardsEntity.getValidMonth() + "个月");
                        tvPrice.setVisibility(View.GONE);
                        etPrice.setVisibility(View.GONE);
                        tvPriceUnit.setVisibility(View.GONE);
                        ivLine.setVisibility(View.GONE);
                        tvReality.setVisibility(View.GONE);
                        etReality.setVisibility(View.GONE);
                        tvRealityUnit.setVisibility(View.GONE);
                        ivLine2.setVisibility(View.GONE);
                        tvValidityTerm.setText("修改有效期至");
                        tvValidityTermUnit.setVisibility(View.GONE);
                    }
                    cardPrice = String.valueOf(Integer.parseInt(cardsEntity.getCardPrice()) / 100);
                    textColor = R.color.color_57a1;
                    bgRes = R.mipmap.ic_card_year_bg;
                    headRes = R.mipmap.ic_card_year_head;
                    typeRes = R.mipmap.ic_card_year_type;
                    timeRes = R.mipmap.ic_card_year_time;
                    break;
                case "4":
                    if (TextUtils.equals(addType, "1")) {// 1：充值缴费
                        tvTitle.setText("折扣卡充值续费");
                        realityPrice = "¥" + String.valueOf(Integer.parseInt(cardsEntity.getBalance()) / 100) + "元";
                        tvRealityPrice.setText("余额");
                        rlCardCount.setVisibility(View.VISIBLE);
                        tvSurplus.setText("原卡余额:1000元");
                        tvTotal.setText("总金额:6000元");
                        tvValidityTerm.setText("新有效期至");
                        tvValidityTermUnit.setVisibility(View.GONE);
                    } else if (TextUtils.equals(addType, "2") || TextUtils.equals(addType, "4") || TextUtils.equals(addType, "5")) {
                        if (TextUtils.equals(addType, "2")) {
                            tvTitle.setText("购买新折扣卡");
                        } else if (TextUtils.equals(addType, "4")) {
                            tvTitle.setText("添加新折扣卡");
                        } else if (TextUtils.equals(addType, "5")) {
                            tvTitle.setText("绑定已有折扣卡");
                            tvPrice.setText("剩余金额");
                            tvRealityPrice.setText("余额");
                        }
                        realityPrice = "¥" + String.valueOf(Integer.parseInt(cardsEntity.getRealityPrice()) / 100) + "元";
                    } else if (TextUtils.equals(addType, "3")) {// 3：编辑课时卡
                        tvTitle.setText("编辑折扣卡");
                        String realPrice = String.valueOf(Integer.parseInt(cardsEntity.getBalance()) / 100);
                        realityPrice = "¥" + realPrice + "元";
                        tvRealityPrice.setText("余额");
                        tvPrice.setText("修改剩余金额");
                        tvReality.setVisibility(View.GONE);
                        etReality.setVisibility(View.GONE);
                        tvRealityUnit.setVisibility(View.GONE);
                        ivLine2.setVisibility(View.GONE);
                        tvValidityTerm.setText("修改有效期至");
                        tvValidityTermUnit.setVisibility(View.GONE);
                    }
                    tvCardType.setText("折扣卡");
                    cardPrice = (Integer.parseInt(cardsEntity.getCardPrice()) / 100) + "\t\t" + cardsEntity.getDiscount() + "折";
                    tvCardPrice.setVisibility(View.VISIBLE);
                    tvRealityPrice.setVisibility(View.VISIBLE);
                    tvReality.setVisibility(View.GONE);
                    etReality.setVisibility(View.GONE);
                    tvRealityUnit.setVisibility(View.GONE);
                    ivLine2.setVisibility(View.GONE);
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
        tvCardName.setText(cardsEntity.getCardName());
        tvMoney.setText(String.format("¥%s", cardPrice));
        tvNum.setText(realityPrice);

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
    }

    private void setValidityTerm() {
        if (TextUtils.equals(validMonth, "0") || Integer.valueOf(validMonth) < 0) {
            dataMonth = "长期有效";
        } else {
            dataMonth = DateUtil.getDate(Long.parseLong(cardsEntity.getEndTime()), "yyyy-MM-dd");
        }
        etValidityTerm.setText(dataMonth);
    }

    /**
     * 设置初始值
     */
    private void setInitData() {
        if (TextUtils.equals(cardsEntity.getCardType(), "2")    //储值卡
                || TextUtils.equals(cardsEntity.getCardType(), "4")) { // 折扣卡
            if (TextUtils.equals(addType, "3")) {
                String balance = cardsEntity.getBalance();
                etPrice.setText(String.valueOf(Integer.parseInt(!TextUtils.isEmpty(balance) ? balance : "0") / 100));
            } else {
                etPrice.setText(String.valueOf(Integer.parseInt(cardsEntity.getCardPrice()) / 100));
            }
        } else {
            etPrice.setText(String.valueOf(Integer.parseInt(cardsEntity.getCardPrice()) / 100));
        }
        if (TextUtils.equals(cardsEntity.getCardType(), "1")) { //次数卡
            if (addType.equals("3")) {// 3.编辑课时卡
                etReality.setText(cardsEntity.getLeftCount());
            } else {
                etReality.setText(cardsEntity.getTotalCount());
            }
        } else {
            etReality.setText(String.valueOf(Integer.parseInt(cardsEntity.getRealityPrice()) / 100));
        }
        if (TextUtils.equals(addType, "1") || TextUtils.equals(addType, "3")) {// 1：充值缴费 3：编辑课时卡
            setValidityTerm();
        } else if (TextUtils.equals(addType, "2") || TextUtils.equals(addType, "4") || TextUtils.equals(addType, "5")) {// 2：购买新卡 4,5
            String validMonth;
            if (TextUtils.equals(cardsEntity.getValidMonth(), "0")) {//卡的有效月数 长期有效返回 字符串 "0"
                validMonth = "长期有效";
                tvValidityTermUnit.setVisibility(View.GONE);
            } else {
                validMonth = cardsEntity.getValidMonth();
            }
            etValidityTerm.setText(validMonth);
        }
        if (TextUtils.equals(addType, "1")) { // 1：充值缴费
            if (TextUtils.equals(cardsEntity.getCardType(), "1")) {
                tvSurplus.setText(String.format("原卡剩余次数: %s次", cardsEntity.getLeftCount()));
                tvTotal.setText(String.format("总次数: %s次", cardsEntity.getTotalCount()));
            }
            if (TextUtils.equals(cardsEntity.getCardType(), "2")) {
                tvSurplus.setText(String.format("原卡余额:%s元", Integer.valueOf(cardsEntity.getBalance()) / 100));
                tvTotal.setText(String.format("总金额:%s元", Integer.valueOf(cardsEntity.getRealityPrice()) / 100));
            }
            if (TextUtils.equals(cardsEntity.getCardType(), "3")) {
                tvSurplus.setText(String.format("原有效期至:%s", DateUtil.getDate(Long.parseLong(cardsEntity.getEndTime()), "yyyy-MM-dd")));
                tvTotal.setText(String.format("新有效期至:%s", DateUtil.getDate(Long.parseLong(cardsEntity.getEndTime()), "yyyy-MM-dd")));
            }
            if (TextUtils.equals(cardsEntity.getCardType(), "4")) {
                tvSurplus.setText(String.format("原卡余额:%s元", String.valueOf(Integer.parseInt(cardsEntity.getBalance()) / 100)));
                tvTotal.setText(String.format("总金额:%s元", String.valueOf(Integer.parseInt(cardsEntity.getBalance()) / 100)));
            }
        }
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
//        etMonth.setFocusable(true);
//        etMonth.setFocusableInTouchMode(true);
        rbMonth.setSelected(true);
        rbValid.setSelected(false);
        validTerm = false;
        tvTime.setText(String.format("有效期:\t%s", etMonth.getText() + "个月"));//卡的有效月数
    }

    @Override
    public void initData() {
        setInitData();//设置初始值
        etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    String price = s.toString().trim();
                    if (TextUtils.equals(addType, "1")) {// 1：充值缴费
                        cardPrice = (Integer.parseInt(cardsEntity.getCardPrice()) / 100 + Integer.valueOf(s.toString().trim())) +
                                "\t\t" + cardsEntity.getDiscount() + "折";
                        tvMoney.setText(String.format("¥%s", cardPrice));
                        Integer realityPrice = Integer.valueOf(cardsEntity.getBalance()) / 100 + Integer.valueOf(s.toString().trim());
                        if (!TextUtils.equals(cardsEntity.getCardType(), "3")) { //不是年卡
                            tvTotal.setText(String.format("总金额:%s元", realityPrice));
                        }
                        tvNum.setText(String.format("¥%s元", realityPrice));
                    } else if (TextUtils.equals(addType, "3")) {// 3：编辑课时卡
                        if (TextUtils.equals(cardsEntity.getCardType(), "2")) { //储值卡
                            price += "/¥" + Integer.valueOf(cardsEntity.getRealityPrice()) / 100 + "元";
                        }
                        tvNum.setText(String.format("¥%s", price));
                    } else if (TextUtils.equals(addType, "4") || TextUtils.equals(addType, "5")) {
                        if (TextUtils.equals(addType, "4")) {
                            tvMoney.setText(String.format("¥%s", price));
                        }
                        if (cardsEntity.getCardType().equals("2") || cardsEntity.getCardType().equals("4")) {
                            tvNum.setText(String.format("¥%s", price));
                        }
                    } else {
                        tvMoney.setText(String.format("¥%s", price));
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
                if (!TextUtils.isEmpty(s)) {
                    String reality = "";
                    if (TextUtils.equals(cardsEntity.getCardType(), "1")) {
                        if (TextUtils.equals(addType, "1")) { //1：充值缴费
                            Integer totalCount = Integer.valueOf(cardsEntity.getLeftCount()) + Integer.valueOf(s.toString().trim());
                            tvTotal.setText(String.format("总次数: %s次", totalCount));
                            reality = String.format("%s/%s次", totalCount, totalCount);
                        }
                        if (TextUtils.equals(addType, "2") || TextUtils.equals(addType, "4") || TextUtils.equals(addType, "5")) { // 2：购买新卡 4,5
                            reality = s.toString().trim() + "次";
                        }
                        if (TextUtils.equals(addType, "3")) { // 3：编辑课时卡
                            reality = String.format("%s/%s次", s.toString().trim(), cardsEntity.getTotalCount());
                        }
                    }
                    if (TextUtils.equals(cardsEntity.getCardType(), "2")) {
                        if (TextUtils.equals(addType, "1")) {
                            Integer realityPrice = Integer.valueOf(cardsEntity.getBalance()) / 100 + Integer.valueOf(s.toString().trim());
                            tvTotal.setText(String.format("总金额:%s元", realityPrice));
                            reality = "¥" + realityPrice + "/¥" + realityPrice + "元";
                        }
                        if (TextUtils.equals(addType, "2") || TextUtils.equals(addType, "4") || TextUtils.equals(addType, "5")) { //2,4,5
                            if (TextUtils.equals(addType, "5")) {
                                reality = "¥" + s.toString().trim() + "/¥" + String.valueOf(Integer.parseInt(cardsEntity.getRealityPrice()) / 100) + "元";
                            } else {
                                reality = "¥" + s.toString().trim() + "元";
                            }
                        }
                        if (TextUtils.equals(addType, "3")) {
                            reality = "¥" + s.toString().trim() + "/¥" + String.valueOf(Integer.parseInt(cardsEntity.getRealityPrice()) / 100) + "元";
                        }
                    }
                    if (TextUtils.equals(cardsEntity.getCardType(), "3")) {
                        if (TextUtils.equals(addType, "1")) {
                            int Month = Integer.parseInt(s.toString().trim());//月
                            tvTotal.setText(String.format("新有效期至:%s",
                                    DateUtil.getDate(Long.parseLong(TimeUtils.getMonthAgo(new Date(cardsEntity.getEndTime()), Month, "yyyy-MM-dd")), "yyyy-MM-dd")));
                            reality = String.format("%s", TextUtils.equals(s.toString().trim(), "0") ? "长期有效" : s.toString().trim() + "/" + cardsEntity.getValidMonth() + "个月");
                        }
                        if (TextUtils.equals(addType, "2") || TextUtils.equals(addType, "4") || TextUtils.equals(addType, "5")) {// 2,4,5
                            reality = String.format("%s", TextUtils.equals(s.toString().trim(), "0") ? "长期有效" : s.toString().trim() + "个月");
                        }
                        if (TextUtils.equals(addType, "3")) {
                            reality = String.format("%s", TextUtils.equals(s.toString().trim(), "0") ? "长期有效" : s.toString().trim() + "/" + cardsEntity.getValidMonth() + "个月");
                        }
                    }
                    if (TextUtils.equals(cardsEntity.getCardType(), "4")) {
                        if (TextUtils.equals(addType, "2") || TextUtils.equals(addType, "3") || TextUtils.equals(addType, "4")
                                || TextUtils.equals(addType, "5")) {
                            reality = "¥" + s.toString().trim() + "元";
                        }
                    }
                    tvNum.setText(reality);
                }
            }
        });
        etValidityTerm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (s.toString().trim().equals("0")) {
                        U.showToast("请输入大于0的有效期");
                    }
                    String valid = "有效期:" + s.toString().trim();
                    if (TextUtils.equals(addType, "2") || TextUtils.equals(addType, "4")
                            || TextUtils.equals(addType, "5")) {// 2：购买新卡 4,5
                        valid += "个月";
                    }
                    tvTime.setText(valid);
                    if (TextUtils.equals(cardsEntity.getCardType(), "3")) {
                        if (TextUtils.equals(addType, "1")) {
                            tvTotal.setText(String.format("新有效期至:%s", s.toString().trim()));
                        }
                    }

                }
            }
        });
    }

    /**
     * select time to update course table.
     */
    private void selectDate() {
        Calendar startDate = Calendar.getInstance();
        new TimePickerBuilder(this, (date, v) -> {
            String selectDay = DateUtil.getDate(date.getTime() / 1000, "yyyy-MM-dd");
            tvTime.setText(String.format("有效期:\t%s", selectDay));//卡的有效期
            etValidityTerm.setText(selectDay);//有效期
        }).setType(new boolean[]{true, true, true, false, false, false})
                .setRangDate(startDate, null)
                .setLabel("年", "月", "日", "", "", "")
                .build().show();
    }

    @OnClick({R.id.rl_back_button, R.id.btn_cancel, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
            case R.id.btn_cancel:
                finish();
                overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
                break;
            case R.id.btn_submit:
                btnSubmit.setClickable(false);
                if (TextUtils.equals(addType, "1")) {// 1：充值缴费
                    rechargeCard();
                } else if (TextUtils.equals(addType, "2")) {// 2：购买新卡
                    buyNewCard();
                } else if (TextUtils.equals(addType, "3")) {// 3：编辑课时卡
                    editChildCard();
                } else if (TextUtils.equals(addType, "4") || TextUtils.equals(addType, "5")) {// 4、选择 添加新课时卡 5、选择 绑定已有课时卡
                    if (addType != null && addType.equals("4") &&
                            audit != null && audit.equals("1")) {// audit不为空且等于1 ,为审核通过学员添加课时卡
                        //课时卡
                        //---遍历传递的参数数据--start--
                        ArrayList<NewCardEntity> newCardList = new ArrayList<>();
                        String courseCount = "";
                        boolean isAddCour = false;
                        NewCardEntity newCardEntity = new NewCardEntity();
                        newCardEntity.setCourseId(cardsEntity.getOrgCardId());
                        if (TextUtils.equals(cardsEntity.getCardType(), "1")) {
                            String singleTimes = etReality.getText().toString();
                            if (!TextUtils.isEmpty(singleTimes) && Double.valueOf(singleTimes) > 0) {
                                courseCount = singleTimes;
                                newCardEntity.setCount(courseCount);
                                newCardEntity.setPrice("");
                                isAddCour = true;
                            }
                        } else if (TextUtils.equals(cardsEntity.getCardType(), "3")) {//年卡
                            String courseId = cardsEntity.getOrgCardId();
                            if (!TextUtils.isEmpty(courseId)) {//课时卡id有值是年卡
                                newCardEntity.setCount("");
                                newCardEntity.setPrice(courseCount);
                                isAddCour = true;
                            }
                        } else {
                            String singleMoney = String.valueOf(Integer.parseInt(etReality.getText().toString()) * 100);
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
                        }

                        //---遍历传递的参数数据--end--
                        courses = new Gson().toJson(newCardList);

                        buyNewCard();// 学员审核通过为学员添加课时卡
                    } else {// 添加学员同时为学员添加课时卡
                        String price = String.valueOf(Integer.parseInt(etPrice.getText().toString()) * 100);
                        // 卡的类型 1：次卡 2：储值卡 3：年卡 4：折扣卡
                        if (TextUtils.equals(cardsEntity.getCardType(), "1")) {
                            if (TextUtils.equals(addType, "5")) {
                                cardsEntity.setLeftCount(etReality.getText().toString());
                            } else {
                                cardsEntity.setTotalCount(etReality.getText().toString());
                            }
                        } else if (TextUtils.equals(cardsEntity.getCardType(), "2")) {
                            String reality = String.valueOf(Integer.parseInt(etReality.getText().toString()) * 100);
                            if (TextUtils.equals(addType, "5")) {
                                cardsEntity.setLeftPrice(reality);
                            } else {
                                cardsEntity.setRealityPrice(reality);
                            }
                        } else if (TextUtils.equals(cardsEntity.getCardType(), "4")) {
                            if (TextUtils.equals(addType, "5")) {// 5、选择 绑定已有课时卡
                                cardsEntity.setLeftPrice(price);// 余额
                                price = cardsEntity.getCardPrice();// 售卡金额
                                cardsEntity.setRealityPrice(cardsEntity.getCardPrice());
                            } else {// 4、选择 添加新课时卡
                                cardsEntity.setRealityPrice(price);
                            }
                        } else {
                            cardsEntity.setLeftPrice(cardsEntity.getRealityPrice());
                        }
                        cardsEntity.setCardPrice(price);
                        cardsEntity.setAddType(addType);// 识别当前是 添加课时卡 还是 绑定已有课时卡
                        String validMonth;
                        if (validTerm) {//长期有效
                            validMonth = "0";
                        } else {
                            validMonth = etMonth.getText().toString().trim();
                            if (TextUtils.isEmpty(validMonth)) {
                                U.showToast("请输入有效期");
                                return;
                            }
                            if (validMonth.equals("0")) {// 新卡有效期必须大于0
                                U.showToast("有效期不可以为0");
                                return;
                            }
                        }
                        if (TextUtils.equals(addType, "5")) {// 5、选择 绑定已有课时卡
                            cardsEntity.setPassMonth(validMonth);//有效期
                            cardsEntity.setValidMonth(cardsEntity.getValidMonth());
                        } else {
                            cardsEntity.setValidMonth(validMonth);
                        }
                        if (addType != null && addType.equals("5") &&
                                audit != null && audit.equals("2")) {// audit不为空且等于2 ,为审核通过学员绑定课时卡
                            bindNewCard(cardsEntity);
                        } else {
                            EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_STU_CARDS, cardsEntity));
                            startActivity(new Intent(this, AddFormalStuActivity.class));
                        }
                    }
                }
                break;
        }
    }

    /**
     * 34.编辑学员已有的课时卡
     */
    private void editChildCard() {
        String price = String.valueOf((Integer.valueOf(etPrice.getText().toString()) * 100));//单位分  100元传10000
        String cardId = cardsEntity.getCardId();
        String validDate = DateUtil.getStringToDate(etValidityTerm.getText().toString().trim(), "yyyy-MM-dd") + "";//传时间戳
        String realityCount = etReality.getText().toString().trim();

        HashMap<String, String> map = new HashMap<>();
        map.put("cardId", cardId);
        map.put("validDate", validDate);
        if (TextUtils.equals(cardsEntity.getCardType(), "1")) {
            map.put("count", realityCount);
        }
        if (TextUtils.equals(cardsEntity.getCardType(), "2") || TextUtils.equals(cardsEntity.getCardType(), "3")
                || TextUtils.equals(cardsEntity.getCardType(), "4")) {
            map.put("leftPrice", price);
        }
        HttpManager.getInstance().doEditChildCard("TimeCardBuyActivity", map,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        U.showToast("编辑成功");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_STUDENT));
                        if (TextUtils.equals(cardsEntity.getCardType(), "3")) {
                            startActivity(new Intent(TimeCardBuyActivity.this, StudentDetailsActivity.class)
                                    .putExtra("StudentEntity", stuDetails));
                        }
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("TimeCardBuyActivity onFail", statusCode + ":" + errorMsg);
                        btnSubmit.setClickable(true);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(TimeCardBuyActivity.this);
                        } else if (statusCode == 1001) {
                            U.showToast("请填入大于0的数!");
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("TimeCardBuyActivity onFail", throwable.getMessage());
                    }
                });
    }

    /**
     * 33.为学员已有的课时卡充值续费
     */
    private void rechargeCard() {
        String price = String.valueOf((Integer.valueOf(etPrice.getText().toString()) * 100));//单位分  100元传10000
        String etValidity = etValidityTerm.getText().toString().trim();
        String validDate = etValidity.equals("长期有效") ? "0" : DateUtil.getStringToDate(etValidity, "yyyy-MM-dd") + "";//传时间戳
        String realityCount = etReality.getText().toString().trim();

        HashMap<String, String> map = new HashMap<>();
        map.put("cardId", cardsEntity.getCardId());
        map.put("price", price);
        map.put("validDate", validDate);
        if (TextUtils.equals(cardsEntity.getCardType(), "1")) {
            map.put("count", realityCount);
        }
        if (TextUtils.equals(cardsEntity.getCardType(), "2")
                || TextUtils.equals(cardsEntity.getCardType(), "4")) {
            realityCount = String.valueOf((Integer.valueOf(realityCount) * 100));
            map.put("realityPrice", realityCount);
        }
        HttpManager.getInstance().doRechargeCard("TimeCardBuyActivity", map,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {

                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        U.showToast("充值续费成功");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_STUDENT));
                        startActivity(new Intent(TimeCardBuyActivity.this, StudentDetailsActivity.class)
                                .putExtra("StudentEntity", stuDetails));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("TimeCardBuyActivity onFail", statusCode + ":" + errorMsg);
                        btnSubmit.setClickable(true);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(TimeCardBuyActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("TimeCardBuyActivity onFail", throwable.getMessage());
                    }
                });
    }

    /**
     * 32.购买新的课时卡
     */
    private void buyNewCard() {
        String price = String.valueOf((Integer.valueOf(etPrice.getText().toString()) * 100));//单位分  100元传10000
        String validMonth = etValidityTerm.getText().toString().trim();//单位月 有效期1年传12 长期有效传0
        if (validMonth.equals("0")) {// 新卡有效期必须大于0
            U.showToast("有效期不可以为0");
            return;
        }
        String realityCount = etReality.getText().toString().trim();

        HashMap<String, String> map = new HashMap<>();
        map.put("orgCardId", cardsEntity.getOrgCardId());
        String stuId = null;
        if (stuDetails != null) {
            stuId = stuDetails.getStuId();
        }
        map.put("stuId", stuId);
        map.put("price", price);
        map.put("validMonth", validMonth);
        if (TextUtils.equals(cardsEntity.getCardType(), "1")) {
            map.put("count", realityCount);
        }
        if (TextUtils.equals(cardsEntity.getCardType(), "2")) {
            realityCount = String.valueOf((Integer.valueOf(realityCount) * 100));
            map.put("realityPrice", realityCount);
        }
        if (addType != null && addType.equals("4") &&
                audit != null && audit.equals("1")) {// audit不为空且等于1 ,为审核通过学员添加课时卡{
            map.put("cardName", tvCardName.getText().toString());
            map.put("orgId", UserManager.getInstance().getOrgId());
            map.put("course", courses);
        }
        HttpManager.getInstance().doBuyNewCard("TimeCardBuyActivity", map, new HttpCallBack<BaseDataModel<CourseEntity>>() {
            @Override
            public void onSuccess(BaseDataModel<CourseEntity> data) {
                EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_STUDENT));
                U.showToast("购买成功");
                startActivity(new Intent(TimeCardBuyActivity.this, StudentDetailsActivity.class)
                        .putExtra("StudentEntity", stuDetails));
            }

            @Override
            public void onFail(int statusCode, String errorMsg) {
                LogUtils.d("TimeCardBuyActivity onFail", statusCode + ":" + errorMsg);
                btnSubmit.setClickable(true);
                if (statusCode == 1002 || statusCode == 1011) {//异地登录
                    U.showToast("该账户在异地登录!");
                    HttpManager.getInstance().dologout(TimeCardBuyActivity.this);
                } else if (statusCode == 1001) {
                    U.showToast("请填入大于0的参数!");
                } else if (statusCode == 1055) {
                    U.showToast("课时卡名字重复!");
                } else {
                    U.showToast(errorMsg);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtils.d("TimeCardBuyActivity onFail", throwable.getMessage());
            }
        });
    }

    // 学员审核通过为学员绑定课时卡
    public void bindNewCard(CourseEntity cardStu) {
        HashMap<String, String> map = new HashMap<>();
        if (addType != null && addType.equals("5") &&
                audit != null && audit.equals("2")) {// audit不为空且等于2 ,为审核通过学员绑定课时卡
            String stuId = null;
            if (stuDetails != null) {
                stuId = stuDetails.getStuId();
            }
            CardBindEntity cardBind = new CardBindEntity();
            String cardType = cardStu.getCardType();// 1：次卡 2：储值卡 3：年卡 4：折扣卡
            cardBind.setCardType(cardType);
            cardBind.setCardId(cardStu.getOrgCardId());
            cardBind.setCardName(cardStu.getCardName());
            String cardPrice = String.valueOf(Integer.parseInt(cardStu.getCardPrice()));
            if (TextUtils.equals(cardType, "2")) {
                String realPrice = TextUtils.isEmpty(cardStu.getRealityPrice()) ? "" :
                        String.valueOf(Integer.parseInt(cardStu.getRealityPrice()));
                if (!TextUtils.isEmpty(cardStu.getAddType()) && cardStu.getAddType().equals("5")) {// 4、选择 添加新课时卡 5、选择 绑定已有课时卡
                    String leftPrice = TextUtils.isEmpty(cardStu.getLeftPrice()) ? "" :
                            String.valueOf(Integer.parseInt(cardStu.getLeftPrice()));
                    cardBind.setLeftPrice(leftPrice);
                } else {
                    cardBind.setLeftPrice(realPrice);// 剩余金额等于实得金额
                }
                cardBind.setRealPrice(realPrice);// 储值卡的实得金额 单位元，其他卡该值与price值相同
            } else {
                cardBind.setRealPrice(cardPrice);
                cardBind.setLeftPrice(cardPrice);// 剩余金额等于购买金额
            }
            cardBind.setPrice(cardPrice);
            if (TextUtils.equals(cardType, "1")) {
                cardBind.setPurchCount(cardStu.getTotalCount());// 购买次数（用于次数卡，其他卡传0）
                if (!TextUtils.isEmpty(cardStu.getAddType()) && cardStu.getAddType().equals("5")) {// 4、选择 添加新课时卡 5、选择 绑定已有课时卡
                    cardBind.setLeftCount(cardStu.getLeftCount());
                    // 绑定已有课时卡传
                    cardBind.setLeftPrice(String.valueOf(
                            Integer.valueOf(cardPrice)
                                    / Integer.valueOf(cardStu.getTotalCount())
                                    * Integer.valueOf(cardStu.getLeftCount())));// 绑定课时卡传
                } else {
                    cardBind.setLeftCount(cardStu.getTotalCount());// 次数卡剩余次数和购买次数一样 ,剩余次数（用于次数卡，其他卡传0）
                }
            } else {
                cardBind.setPurchCount("0");
                cardBind.setLeftCount("0");
            }
            if (TextUtils.equals(cardType, "4")) {
                if (!TextUtils.isEmpty(cardStu.getAddType()) && cardStu.getAddType().equals("5")) {// 4、选择 添加新课时卡 5、选择 绑定已有课时卡
                    String leftPrice = TextUtils.isEmpty(cardStu.getLeftPrice()) ? "" :
                            String.valueOf(Integer.parseInt(cardStu.getLeftPrice()));
                    cardBind.setLeftPrice(leftPrice);
                }
                cardBind.setDiscount(cardStu.getDiscount());// 折扣 （用于折扣卡，其他卡传0）
            } else {
                cardBind.setDiscount("10");
            }
            if (TextUtils.equals(cardType, "2") && !TextUtils.isEmpty(cardStu.getAddType()) && cardStu.getAddType().equals("5")) {// 4、选择 添加新课时卡 5、选择 绑定已有课时卡
                String leftPrice = TextUtils.isEmpty(cardStu.getLeftPrice()) ? "" :
                        String.valueOf(Integer.parseInt(cardStu.getLeftPrice()));
                String realPrice = TextUtils.isEmpty(cardStu.getRealityPrice()) ? "" :
                        String.valueOf(Integer.parseInt(cardStu.getRealityPrice()));
                float prices = Float.parseFloat(realPrice) / Float.parseFloat(cardPrice);
                cardBind.setLeftTruePrice(String.valueOf(
                        Integer.valueOf(leftPrice) / prices));
            } else if (TextUtils.equals(cardType, "4") && !TextUtils.isEmpty(cardStu.getAddType()) && cardStu.getAddType().equals("5")) {
                String leftPrice = TextUtils.isEmpty(cardStu.getLeftPrice()) ? "" :
                        String.valueOf(Integer.parseInt(cardStu.getLeftPrice()));
                cardBind.setLeftTruePrice(leftPrice);
            } else {
                cardBind.setLeftTruePrice(cardPrice);
            }
            String endAt;
            if (cardStu.getValidMonth().equals("0")) {
                endAt = cardStu.getValidMonth();
            } else {
                String validData = TimeUtils.getMonthAgo(new Date(Calendar.getInstance().getTimeInMillis()),
                        Integer.parseInt(cardStu.getValidMonth()), "yyyy/MM/dd");// 有效期转化为日期
                cardBind.setEnd_at(String.valueOf(DateUtil.getStringToDate(validData, "yyyy/MM/dd")));
                endAt = String.valueOf(DateUtil.getStringToDate(validData, "yyyy/MM/dd"));
            }
            cardBind.setEnd_at(endAt);
            map.put("orgId", UserManager.getInstance().getOrgId());
            map.put("childId", stuId);
            map.put("cardType", cardType);
            map.put("cardId", cardStu.getOrgCardId());
            map.put("leftPrice", cardBind.getLeftPrice());
            map.put("leftCount", cardBind.getLeftCount());
            map.put("leftTruePrice", cardBind.getLeftTruePrice());
            map.put("discount", cardBind.getDiscount());
            map.put("cardName", cardBind.getCardName());
            map.put("price", cardPrice);
            map.put("end_at", cardBind.getEnd_at());
            map.put("purchCount", cardBind.getPurchCount());
            map.put("realPrice", cardBind.getRealPrice());
//            map.put("cards", cards);
        }
        HttpManager.getInstance().doBindNewCard("TimeCardBuyActivity", map,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_STUDENT));
                        U.showToast("绑定成功");
                        startActivity(new Intent(TimeCardBuyActivity.this, StudentDetailsActivity.class)
                                .putExtra("StudentEntity", stuDetails));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        btnSubmit.setClickable(true);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(TimeCardBuyActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("TimeCardBuyActivity onFail", throwable.getMessage());
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
