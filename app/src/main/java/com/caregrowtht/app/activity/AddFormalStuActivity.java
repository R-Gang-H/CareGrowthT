package com.caregrowtht.app.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.android.library.view.WheelPopup;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.NewCardsAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CardBindEntity;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018年10月16日11:54:49
 * 添加正式学员
 */
public class AddFormalStuActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_stu_name)
    EditText etStuName;
    @BindView(R.id.et_common_name)
    EditText etCommonName;
    @BindView(R.id.btn_women)
    Button btnWomen;
    @BindView(R.id.btn_man)
    Button btnMan;
    @BindView(R.id.tv_birthday_date)
    TextView tvBirthdayDate;
    @BindView(R.id.et_phone_num)
    EditText etPhoneNum;
    @BindView(R.id.et_again_phone_num)
    EditText etAgainPhoneNum;
    @BindView(R.id.tv_stu_relation)
    TextView tvStuRelation;
    @BindView(R.id.rv_stu_card)
    RecyclerView rvStuCard;
    @BindView(R.id.btn_add_card)
    Button btnAddCard;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    String sex = "2";//1 男 2 女
    String birthday = "";
    String relativeId = "1";
    String relativeName = "妈妈";

    private NewCardsAdapter mCardsAdapter;
    private int courseStuSize = 0;
    private String OrgId;
    private MessageEntity msgEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_stu;
    }

    @Override
    public void initView() {
        tvTitle.setText("添加学员");

        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (msgEntity != null) {
            OrgId = msgEntity.getOrgId();
        } else {
            OrgId = UserManager.getInstance().getOrgId();
        }

        btnWomen.setSelected(true);
        initRecyclerView(rvStuCard, true);
        mCardsAdapter = new NewCardsAdapter(UserManager.getInstance().getCardStuList(), this,
                null, "5", "");//展示页面 1:选择购买新卡 2:新建课时卡种类 3:学员课时卡 5:为学员添加课时卡
        rvStuCard.setAdapter(mCardsAdapter);
    }

    @Override
    public void initData() {
        etPhoneNum.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // 此处为失去焦点时的处理内容
                getCardByPhone();
                etPhoneNum.setFocusable(true);
                etPhoneNum.setFocusableInTouchMode(true);
            }
        });
    }

    @OnClick({R.id.rl_back_button, R.id.btn_women, R.id.btn_man, R.id.tv_birthday_date, R.id.tv_stu_relation,
            R.id.btn_add_card, R.id.btn_cancel, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_women:
                sex = "2";
                btnWomen.setSelected(true);
                btnMan.setSelected(false);
                break;
            case R.id.btn_man:
                sex = "1";
                btnWomen.setSelected(false);
                btnMan.setSelected(true);
                break;
            case R.id.tv_birthday_date:
                hideKeyboard();
                getBirthday();
                break;
            case R.id.tv_stu_relation:
                hideKeyboard();
                selectRelation();
                break;
            case R.id.btn_add_card:
                showCardDialog();
                break;
            case R.id.btn_confirm:
                btnConfirm.setFocusable(false);
                addStudent();
                break;
        }
    }

    private void getBirthday() {
        new TimePickerBuilder(AddFormalStuActivity.this, (date, v) -> {
            birthday = String.valueOf(date.getTime() / 1000);
            tvBirthdayDate.setText(DateUtil.getDate(date.getTime() / 1000, "yyyy-MM"));
        })
                .setType(new boolean[]{true, true, false, false, false, false})
                .setRangDate(null, Calendar.getInstance())
                .setLabel("年", "月", "", "",
                        "", "")
                .build().show();
    }

    /**
     * 选择亲属关系
     */
    private void selectRelation() {
        WheelPopup pop = new WheelPopup(AddFormalStuActivity.this, Constant.relationArray);
        pop.showAtLocation(View.inflate(AddFormalStuActivity.this,
                R.layout.popup_wheel_select, null),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        pop.setSelectListener((argValue, position) -> {
            relativeId = String.valueOf(position + 1);
            relativeName = argValue;
            tvStuRelation.setText(argValue);
            return null;
        });
    }

    /**
     * 学员卡片操作弹框
     */
    private void showCardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_card_oper, null);
        final TextView tvUnbind = view.findViewById(R.id.tv_unbind);
        tvUnbind.setVisibility(View.GONE);
        final TextView tvFamiliar = view.findViewById(R.id.tv_familiar);
        if (courseStuSize == 0) {// 共用课时卡的学员数量大于0是显示
            tvFamiliar.setVisibility(View.GONE);
        }
        tvFamiliar.setText("与家人共用课时卡");
        tvFamiliar.setOnClickListener(v -> {
            //与家人共用课时卡
            startActivity(new Intent(this, FamilyShareActivity.class)
                    .putExtra("phoneNumber", etPhoneNum.getText().toString().trim())
                    .putExtra("toShare", "1"));// toShare： 1:从添加学员进入 2:从学员详情进入 3:从审核学员进入
            overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
            dialog.dismiss();
        });
        final TextView tvCourseCard = view.findViewById(R.id.tv_course_card);
        tvCourseCard.setText("绑定已有课时卡");
        tvCourseCard.setOnClickListener(v -> {
            //绑定已有课时卡
            startActivity(new Intent(this, NewCardBuyActivity.class)
                    .putExtra("addType", "5"));// 5、选择 绑定已有课时卡
            dialog.dismiss();
        });
        final TextView tvEditCard = view.findViewById(R.id.tv_edit_card);
        tvEditCard.setText("添加新课时卡");
        tvEditCard.setOnClickListener(v -> {
            //添加新课时卡
            startActivity(new Intent(this, NewCardBuyActivity.class)
                    .putExtra("addType", "4"));// 4、选择 添加新课时卡
            dialog.dismiss();
        });
        final TextView tvCancel = view.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
        //设置弹窗在底部
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
    }

    private void addStudent() {
        // 62.添加学员
        String stuName = etStuName.getText().toString().trim();
        if (TextUtils.isEmpty(stuName)) {
            U.showToast("请输入学员姓名");
            return;
        }
        String stuNickName = etCommonName.getText().toString().trim();
        String phoneNumber = etPhoneNum.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber)) {
            U.showToast("请输入手机号");
            return;
        }
        String againPhoneNumber = etAgainPhoneNum.getText().toString().trim();
        if (!TextUtils.equals(phoneNumber, againPhoneNumber)) {
            U.showToast("两次输入的手机号不相同");
            return;
        }
        List<CardBindEntity> cardBindList = new ArrayList<>();
        for (CourseEntity cardStu : UserManager.getInstance().getCardStuList()) {
            CardBindEntity cardBind = new CardBindEntity();
            String cardType = cardStu.getCardType();// 1：次卡 2：储值卡 3：年卡 4：折扣卡
            cardBind.setCardType(cardType);
            cardBind.setCardId(cardStu.getOrgCardId());
            cardBind.setCardName(cardStu.getCardName());
            String cardPrice = String.valueOf(Integer.parseInt(cardStu.getCardPrice()) / 100);
            if (TextUtils.equals(cardType, "2")) {
                String realPrice = TextUtils.isEmpty(cardStu.getRealityPrice()) ? "" :
                        String.valueOf(Integer.parseInt(cardStu.getRealityPrice()) / 100);
                if (!TextUtils.isEmpty(cardStu.getAddType()) && cardStu.getAddType().equals("5")) {// 4、选择 添加新课时卡 5、选择 绑定已有课时卡
                    String leftPrice = TextUtils.isEmpty(cardStu.getLeftPrice()) ? "" :
                            String.valueOf(Integer.parseInt(cardStu.getLeftPrice()) / 100);
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
                            String.valueOf(Integer.parseInt(cardStu.getLeftPrice()) / 100);
                    cardBind.setLeftPrice(leftPrice);
                }
                cardBind.setDiscount(cardStu.getDiscount());// 折扣 （用于折扣卡，其他卡传0）
            } else {
                cardBind.setDiscount("10");
            }
            if (TextUtils.equals(cardType, "2") && !TextUtils.isEmpty(cardStu.getAddType()) && cardStu.getAddType().equals("5")) {// 4、选择 添加新课时卡 5、选择 绑定已有课时卡
                String leftPrice = TextUtils.isEmpty(cardStu.getLeftPrice()) ? "" :
                        String.valueOf(Integer.parseInt(cardStu.getLeftPrice()) / 100);
                String realPrice = TextUtils.isEmpty(cardStu.getRealityPrice()) ? "" :
                        String.valueOf(Integer.parseInt(cardStu.getRealityPrice()) / 100);
                float prices = Float.parseFloat(realPrice) / Float.parseFloat(cardPrice);
                cardBind.setLeftTruePrice(String.valueOf(
                        Integer.valueOf(leftPrice) / prices));
            } else if (TextUtils.equals(cardType, "4") && !TextUtils.isEmpty(cardStu.getAddType()) && cardStu.getAddType().equals("5")) {
                String leftPrice = TextUtils.isEmpty(cardStu.getLeftPrice()) ? "" :
                        String.valueOf(Integer.parseInt(cardStu.getLeftPrice()) / 100);
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
            if (TextUtils.isEmpty(cardStu.getCourseCardId())) {// 等于空添加 cards
                cardBindList.add(cardBind);
            }
        }
        String courseCard = TextUtils.isEmpty(courseCards) ? "" :
                courseCards.toString().substring(0, courseCards.toString().lastIndexOf(","));
        String cards = cardBindList.size() > 0 ? new Gson().toJson(cardBindList) : "";
        HttpManager.getInstance().doAddStudent("AddFormalStuActivity", OrgId, stuName, stuNickName,
                sex, birthday, phoneNumber, relativeId, relativeName, courseCard, cards,
                new HttpCallBack<BaseDataModel<StudentEntity>>(this, true) {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        btnConfirm.setFocusable(true);
                        U.showToast("添加学员成功");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_ACTIVE_STU));
                        startActivity(new Intent(AddFormalStuActivity.this, InviteStudentActivity.class)
                                .putExtra("msgEntity", msgEntity)
                                .putExtra("stuName", stuName));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        btnConfirm.setFocusable(true);
                        LogUtils.d("AddFormalStuActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(AddFormalStuActivity.this);
                        } else if (statusCode == 1062) {
                            U.showToast("用户已存在!");
                        } else if (statusCode == 1070) {// 超出机构允许的最大学员限制!
                            UserManager.getInstance().showSuccessDialog(AddFormalStuActivity.this
                                    , getString(R.string.version_limit));
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        btnConfirm.setFocusable(true);
                        LogUtils.d("AddFormalStuActivity onFail", throwable.getMessage());
                    }
                });
    }

    // 共用学员课时卡
    private void getCardByPhone() {
        HttpManager.getInstance().doGetCardByPhone("FamilyShareActivity",
                UserManager.getInstance().getOrgId(), "",
                etPhoneNum.getText().toString().trim(),
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        courseStuSize = data.getData().size();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("WorkClassActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(AddFormalStuActivity.this);
                        } else if (statusCode == 1004) {// 用户不存在
                            courseStuSize = 0;
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("WorkClassActivity onError", throwable.getMessage());
                    }
                });
    }

    StringBuffer courseCards = new StringBuffer();

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        if (event.getWhat() == ToUIEvent.REFERSH_STU_CARDS) {
            CourseEntity entity = (CourseEntity) event.getObj();
            UserManager.getInstance().getCardStuList().add(entity);
            mCardsAdapter.update(UserManager.getInstance().getCardStuList(), null);
        } else if (event.getWhat() == ToUIEvent.REFERSH_SHARE_CARDS) {
            CourseEntity entity = (CourseEntity) event.getObj();
            UserManager.getInstance().getCardStuList().add(entity);
            courseCards.append(entity.getCourseCardId()).append(",");
            mCardsAdapter.update(UserManager.getInstance().getCardStuList(), null);
        }
    }
}
