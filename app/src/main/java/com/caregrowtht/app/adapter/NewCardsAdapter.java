package com.caregrowtht.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.android.library.view.CircleImageView;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.CreateCardActivity;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.gallery.CardAdapterHelper;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;

/**
 * 学员课时卡/选择购买新卡/缴费充值
 */
public class NewCardsAdapter extends XrecyclerAdapter {

    @BindView(R.id.rl_card_bg)
    RelativeLayout rlCardBg;
    @BindView(R.id.rl_add_card)
    RelativeLayout rlAddCard;
    @BindView(R.id.ll_head)
    LinearLayout llHead;
    @BindView(R.id.iv_head_icon)
    CircleImageView ivHeadIcon;
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
    @BindView(R.id.iv_edit_card)
    ImageView ivEditCard;
    @BindView(R.id.iv_del_card)
    ImageView ivDelCard;
    @BindView(R.id.tv_no_active_card)
    TextView tvNoActiveCard;
    @BindView(R.id.rl_card_time)
    RelativeLayout rlCardTime;
    @BindView(R.id.tv_card_price)
    TextView tvCardPrice;
    @BindView(R.id.tv_reality_price)
    TextView tvRealityPrice;

    private List<CourseEntity> cardsList = new ArrayList<>();
    // 用来控制CheckBox的选中状况
    private HashMap<Integer, Boolean> isSelected = new HashMap<>();

    public String cardType;
    public String addType;
    private String cardPrice;
    private String realityPrice;

    private int textColor = 0;//机构名称色值
    private int bgRes = 0;//背景id
    private int headRes = 0;//头像背景
    private int typeRes = 0;
    private int timeRes = 0;
    private String status;// 课程卡的状态 1：活跃 2：非活跃
    private String dataMonth;

    private CardAdapterHelper mCardAdapterHelper = new CardAdapterHelper();

    public NewCardsAdapter(List datas, Context context, ViewOnItemClick onItemClick1, String cardType, String addType) {//展示页面 1:选择购买新卡(addType 1：充值缴费 2：购买新卡) 2:新建课时卡种类 3:学员课时卡（addType 3：编辑课时卡）
        super(datas, context, onItemClick1);
        this.cardsList.addAll(datas);
        this.cardType = cardType;
        this.addType = addType;
    }

    @NonNull
    @Override
    public XrecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (TextUtils.equals(addType, "45")) {//Gallery画廊需要
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(getLayoutResId(), viewGroup, false);
            mCardAdapterHelper.onCreateViewHolder(viewGroup, view);
            return new XrecyclerViewHolder(view, onItemClick, longClick);
        } else {
            return super.onCreateViewHolder(viewGroup, viewType);
        }
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        final TextView tvName = holder.itemView.findViewById(R.id.tv_name);

        final CourseEntity entity = cardsList.get(position);

        final CheckBox tvSelectCard = (CheckBox) holder.getView(R.id.tv_select_card);
        if (!TextUtils.equals(cardType, "3") && !TextUtils.equals(cardType, "4")
                && !TextUtils.equals(cardType, "5")) {// 3:学员课时卡 /4:课时卡管理/5:为学员添加课时卡
            tvSelectCard.setVisibility(View.VISIBLE);
            // 根据isSelected来设置checkbox的选中状况
            tvSelectCard.setSelected(getIsSelected().get(position));
        }
        if (TextUtils.equals(addType, "45")) {//Gallery画廊需要
            mCardAdapterHelper.onBindViewHolder(holder.itemView, position, getItemCount());
        }

        if (TextUtils.equals(entity.getCardType(), "5")) {//5：新增课时卡(手动添加)
            rlCardBg.setVisibility(View.GONE);
            rlAddCard.setVisibility(View.VISIBLE);
        } else {
            rlCardBg.setVisibility(View.VISIBLE);
            rlAddCard.setVisibility(View.GONE);

            final CheckBox tvCardTypeName = (CheckBox) holder.getView(R.id.tv_card_type_name);
            final TextView tvCardStyle = (TextView) holder.getView(R.id.tv_card_style);
            tvCardTypeName.setSelected(getIsSelected().get(position));

            if (TextUtils.equals(cardType, "2")) {//2:新建课时卡种类
                tvSelectCard.setVisibility(View.GONE);
                tvCardTypeName.setVisibility(View.VISIBLE);
                tvCardStyle.setVisibility(View.VISIBLE);
            }
            // 4:课时卡管理
            if (TextUtils.equals(cardType, "4")) {
                if (TextUtils.equals(status, "1")) {
                    ivEditCard.setVisibility(View.VISIBLE);
                    tvNoActiveCard.setVisibility(View.GONE);
                } else if (TextUtils.equals(status, "2")) {
                    ivEditCard.setVisibility(View.GONE);
                    tvNoActiveCard.setVisibility(View.VISIBLE);
                }
            }
            // 5:为学员添加课时卡
            if (TextUtils.equals(cardType, "5")) {
                ivDelCard.setVisibility(View.VISIBLE);
            }

            String validMonth = entity.getValidMonth();
            if (TextUtils.equals(cardType, "3") || TextUtils.equals(addType, "1")) {// 3:学员课时卡 1：充值缴费
                if (TextUtils.equals(addType, "3") || TextUtils.equals(addType, "1")) {
                    if (TextUtils.equals(validMonth, "0") || Integer.valueOf(validMonth) < 0) {//卡的有效月数 长期有效返回 字符串 "0"
                        dataMonth = "长期有效";
                    } else {
                        if (!TextUtils.isEmpty(entity.getEndTime())) {
                            dataMonth = String.format("有效期:\t%s", DateUtil.getDate(Long.parseLong(entity.getEndTime()), "yyyy-MM-dd"));//卡的有效期
                        }
                    }
                } else {
                    if (TextUtils.equals(validMonth, "0")) {//卡的有效月数 长期有效返回 字符串 "0"
                        dataMonth = "长期有效";
                    } else {
                        if (!TextUtils.isEmpty(entity.getEndTime())) {
                            dataMonth = String.format("有效期:\t%s", DateUtil.getDate(Long.parseLong(entity.getEndTime()), "yyyy-MM-dd"));//卡的有效期
                        }
                    }
                }
            } else {
                if (TextUtils.equals(cardType, "5")) {// 5:为学员添加课时卡
                    if (TextUtils.equals(entity.getAddType(), "4")) {//
                        dataMonth = setDataMonth(validMonth);
                    } else if (TextUtils.equals(entity.getAddType(), "5")) {// 5、选择 绑定已有课时卡
                        dataMonth = setDataMonth(entity.getPassMonth());
                    }
                } else {
                    dataMonth = TextUtils.equals(validMonth, "0") ? "长期有效" : String.format("有效期:\t%s", entity.getValidMonth() + "个月");
                }
            }
            tvTime.setText(dataMonth);

            boolean isExeit = true;// 解决卡片颜色覆盖
            if (!TextUtils.isEmpty(entity.getCardType())) {
                // 卡的类型 1：次卡 2：储值卡 3：年卡 4：折扣卡
                switch (entity.getCardType()) {
                    case "1":
                        tvCardType.setText("次数卡");
                        tvCardTypeName.setText("次数卡");
                        cardPrice = String.valueOf(Integer.parseInt(entity.getCardPrice()) / 100);
                        if (TextUtils.equals(cardType, "3") || TextUtils.equals(addType, "1")
                                || TextUtils.equals(cardType, "5")) {// 3:学员课时卡 1：充值缴费 5:为学员添加课时卡
                            String leftCount = entity.getLeftCount();
                            if (TextUtils.equals(leftCount, "0")) {
                                isExeit = false;
                                // 次数卡如果剩余次数是0次，次数不足变成灰色，显示“次数不足”
                                unBindCard();
                                tvNoActiveCard.setVisibility(View.VISIBLE);
                                tvNoActiveCard.setText("次数不足");
                            }
                            if (TextUtils.equals(cardType, "5")) {
                                if (TextUtils.equals(entity.getAddType(), "4")) {
                                    realityPrice = String.format("%s/%s次", entity.getTotalCount(), entity.getTotalCount());
                                } else if (TextUtils.equals(entity.getAddType(), "5")) {
                                    realityPrice = String.format("%s/%s次", leftCount, entity.getTotalCount());
                                }
                            } else if (TextUtils.equals(cardType, "3") || TextUtils.equals(addType, "1")) {
                                realityPrice = String.format("%s/%s次", leftCount, entity.getTotalCount());
                            }
                        } else {
                            // 绑定已有课时判断
                            if (TextUtils.equals(entity.getAddType(), "5")) {
                                realityPrice = entity.getLeftCount() + "次";
                            } else {
                                realityPrice = entity.getTotalCount() + "次";
                            }
                        }
                        textColor = R.color.color_caa0;
                        tvCardPrice.setVisibility(View.GONE);
                        tvRealityPrice.setVisibility(View.GONE);
                        if (TextUtils.equals(entity.getStatus(), "2") && !TextUtils.equals(cardType, "1")
                                && !TextUtils.equals(cardType, "5")) {//1:选择购买新卡
                            checkCardStatus(entity);
                        } else {//状态 1正常 2解除绑定
                            if (isExeit) {
                                bgRes = R.mipmap.ic_card_num_bg;
                                headRes = R.mipmap.ic_card_num_head;
                                typeRes = R.mipmap.ic_card_num_type;
                                timeRes = R.mipmap.ic_card_num_time;
                                setSaturation();// 设置头像为彩色
                            }
                        }
                        break;
                    case "2":
                        tvCardType.setText("储值卡");
                        tvCardTypeName.setText("储值卡");
                        if (TextUtils.equals(addType, "1") || TextUtils.equals(cardType, "5")) {// 1：充值缴费 5:为学员添加课时卡
                            cardPrice = String.valueOf(Integer.parseInt(entity.getCardPrice()) / 100);
                            if (TextUtils.equals(addType, "1")) {// 1：充值缴费
                                realityPrice = "¥" + String.valueOf(Integer.parseInt(entity.getBalance()) / 100)
                                        + "/¥" + String.valueOf(Integer.parseInt(entity.getRealityPrice()) / 100) + "元";
                            } else if (TextUtils.equals(cardType, "5")) {// 5:为学员添加课时卡
                                if (TextUtils.equals(entity.getAddType(), "4")) {
                                    realityPrice = "¥" + String.valueOf(Integer.parseInt(entity.getRealityPrice()) / 100)
                                            + "/¥" + String.valueOf(Integer.parseInt(entity.getRealityPrice()) / 100) + "元";
                                } else if (TextUtils.equals(entity.getAddType(), "5")) {
                                    if (!TextUtils.isEmpty(entity.getLeftPrice()) && !TextUtils.isEmpty(entity.getRealityPrice())) {
                                        realityPrice = "¥" + String.valueOf(Integer.parseInt(entity.getLeftPrice()) / 100)
                                                + "/¥" + String.valueOf(Integer.parseInt(entity.getRealityPrice()) / 100) + "元";
                                    }
                                }
                            }
                            tvRealityPrice.setText("余额/实得金额");
                        } else if (TextUtils.equals(cardType, "3")) {// 3:学员课时卡
                            cardPrice = String.format("%s/¥%s", String.valueOf(Integer.parseInt(entity.getCardPrice()) / 100)
                                    , String.valueOf(Integer.parseInt(entity.getRealityPrice()) / 100));
                            tvCardPrice.setText("售卡金额/实得金额");
                            String realPrice = String.valueOf(TextUtils.isEmpty(entity.getBalance()) ? 0 :
                                    Integer.parseInt(entity.getBalance()) / 100);
                            realityPrice = "¥" + realPrice;
                            if (TextUtils.equals(realPrice, "0")) {
                                isExeit = false;
                                // 储值或者折扣余额是0的时候，显示“余额不足”，变灰色
                                unBindCard();
                                tvNoActiveCard.setVisibility(View.VISIBLE);
                                tvNoActiveCard.setText("余额不足");
                            }
                            tvRealityPrice.setText("余额");
                        } else {
                            cardPrice = String.valueOf(Integer.parseInt(entity.getCardPrice()) / 100);
                            // 绑定已有课时判断
                            if (TextUtils.equals(entity.getAddType(), "5")) {
                                realityPrice = "¥" + String.valueOf(Integer.parseInt(entity.getLeftPrice()) / 100) + "元";
                            } else {
                                realityPrice = "¥" + String.valueOf(Integer.parseInt(entity.getRealityPrice()) / 100) + "元";
                            }
                        }
                        tvCardPrice.setVisibility(View.VISIBLE);
                        tvRealityPrice.setVisibility(View.VISIBLE);
                        textColor = R.color.color_93ca;
                        if (TextUtils.equals(entity.getStatus(), "2") && !TextUtils.equals(cardType, "1")) {//1:选择购买新卡
                            checkCardStatus(entity);
                        } else {//状态 1正常 2解除绑定
                            if (isExeit) {
                                bgRes = R.mipmap.ic_card_save_bg;
                                headRes = R.mipmap.ic_card_save_head;
                                typeRes = R.mipmap.ic_card_save_type;
                                timeRes = R.mipmap.ic_card_save_time;
                                setSaturation();// 设置头像为彩色
                            }
                        }
                        break;
                    case "3":
                        long mm;
                        if (!TextUtils.isEmpty(entity.getEndTime())) {
                            //年卡类型的卡片：
                            //如果是<=3月的卡，月卡，
                            //>=4, <=9的卡， 算为学期卡
                            //>=10月的卡， 算为年卡。
                            long yy = Long.valueOf(DateUtil.getDate(Long.valueOf(entity.getEndTime()), "yyyy")) - Long.valueOf(DateUtil.getSysTimeType("yyyy")) - 1;//年 -1减去今年
                            long m = 0;
                            if (yy > 0) {
                                m = yy * 12;//月
                            }
                            mm = m + (12 - Long.valueOf(DateUtil.getSysTimeType("MM"))) + Long.valueOf(DateUtil.getDate(Long.valueOf(entity.getEndTime()), "MM"));//月
                        } else {
                            mm = Long.valueOf(entity.getValidMonth());
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
                        tvCardTypeName.setText("月/学期/年卡");
                        cardPrice = String.valueOf(Integer.parseInt(entity.getCardPrice()) / 100);
                        if (TextUtils.equals(cardType, "3") || TextUtils.equals(addType, "1")) {// 3:学员课时卡 1：充值缴费
                            if (TextUtils.equals(addType, "1")) {
                                if (TextUtils.equals(cardType, "1")) {
                                    realityPrice = String.format("%s", TextUtils.equals(validMonth, "0") ?
                                            "长期有效" : entity.getValidMonth() + "个月");
                                } else {
                                    String passMonth = entity.getPassMonth();
                                    realityPrice = String.format("%s", TextUtils.equals(validMonth, "0") ?
                                            "长期有效" : passMonth + "/" + entity.getValidMonth() + "个月");
                                }
                                boolean isExpire = !TextUtils.equals(validMonth, "0");
                                if (isExpire && (System.currentTimeMillis() / 1000 > Integer.valueOf(entity.getEndTime()))) {
                                    isExeit = false;
                                    // 年卡的时候，过期了显示“过期”，变灰色
                                    unBindCard();
                                    tvNoActiveCard.setVisibility(View.VISIBLE);
                                    tvNoActiveCard.setText("过期");
                                }
                            } else if (TextUtils.equals(addType, "3")) {
                                realityPrice = String.format("%s", TextUtils.equals(validMonth, "0") ?
                                        "长期有效" : entity.getValidMonth() + "个月");
                            }
                        } else if (TextUtils.equals(cardType, "5")) {// 5:为学员添加课时卡
                            if (TextUtils.equals(entity.getAddType(), "4")) {
                                realityPrice = String.format("%s", TextUtils.equals(validMonth, "0") ?
                                        "长期有效" : entity.getValidMonth() + "/" + entity.getValidMonth() + "个月");
                            } else if (TextUtils.equals(entity.getAddType(), "5")) {
                                realityPrice = String.format("%s", TextUtils.equals(entity.getPassMonth(), "0") ?
                                        "长期有效" : entity.getPassMonth() +
                                        "/" + (TextUtils.equals(entity.getValidMonth(), "0")
                                        ? entity.getPassMonth() : entity.getValidMonth()) + "个月");
                            }
                        } else {
                            realityPrice = String.format("%s", TextUtils.equals(validMonth, "0") ?
                                    "长期有效" : entity.getValidMonth() + "个月");
                        }
                        tvCardPrice.setVisibility(View.GONE);
                        tvRealityPrice.setVisibility(View.GONE);
                        textColor = R.color.color_57a1;
                        if (TextUtils.equals(entity.getStatus(), "2") && !TextUtils.equals(cardType, "1")) {//1:选择购买新卡
                            checkCardStatus(entity);
                        } else {//状态 1正常 2解除绑定
                            if (isExeit) {
                                bgRes = R.mipmap.ic_card_year_bg;
                                headRes = R.mipmap.ic_card_year_head;
                                typeRes = R.mipmap.ic_card_year_type;
                                timeRes = R.mipmap.ic_card_year_time;
                                setSaturation();// 设置头像为彩色
                            }
                        }
                        break;
                    case "4":
                        tvCardType.setText("折扣卡");
                        tvCardTypeName.setText("折扣卡");
                        if (TextUtils.equals(addType, "1")) {// 1：充值缴费
                            String realPrice;
                            if (addType.equals("1")) {
                                realPrice = String.valueOf(Integer.parseInt(entity.getBalance()) / 100);
                            } else {
                                realPrice = String.valueOf(Integer.parseInt(entity.getRealityPrice()) / 100);
                            }
                            realityPrice = "¥" + realPrice + "元";
                            tvRealityPrice.setText("余额");
                            tvCardPrice.setVisibility(View.VISIBLE);
                        } else if (TextUtils.equals(cardType, "3")) {// 3:学员课时卡
                            String realPrice;
                            if (addType.equals("3")) {
                                realPrice = String.valueOf(Integer.parseInt(entity.getBalance()) / 100);
                            } else {
                                realPrice = String.valueOf(Integer.parseInt(entity.getRealityPrice()) / 100);
                            }
                            realityPrice = "¥" + realPrice + "元";
                            if (TextUtils.equals(realPrice, "0")) {
                                isExeit = false;
                                // 储值或者折扣余额是0的时候，显示“余额不足”，变灰色
                                unBindCard();
                                tvNoActiveCard.setVisibility(View.VISIBLE);
                                tvNoActiveCard.setText("余额不足");
                            }
                            tvRealityPrice.setText("余额");
                            tvCardPrice.setVisibility(View.GONE);
                        } else if (TextUtils.equals(cardType, "5")) {
                            if (TextUtils.equals(entity.getAddType(), "4")) {
                                realityPrice = "¥" + String.valueOf(Integer.parseInt(entity.getCardPrice()) / 100) + "元";
                            } else if (TextUtils.equals(entity.getAddType(), "5")) {
                                tvRealityPrice.setText("余额");
                                realityPrice = "¥" + String.valueOf(Integer.parseInt(entity.getLeftPrice()) / 100) + "元";
                            }
                        } else {
                            // 绑定已有课时判断
                            if (TextUtils.equals(entity.getAddType(), "5")) {
                                realityPrice = "¥" + String.valueOf(Integer.parseInt(entity.getLeftPrice()) / 100) + "元";
                            } else {
                                realityPrice = "¥" + String.valueOf(Integer.parseInt(entity.getRealityPrice()) / 100) + "元";
                            }
                            tvCardPrice.setVisibility(View.VISIBLE);
                        }
                        cardPrice = (Integer.parseInt(entity.getCardPrice()) / 100) + "\t\t" + entity.getDiscount() + "折";
                        tvRealityPrice.setVisibility(View.VISIBLE);
                        textColor = R.color.color_e38f;
                        if (TextUtils.equals(entity.getStatus(), "2") && !TextUtils.equals(cardType, "1")) {//1:选择购买新卡
                            checkCardStatus(entity);
                        } else { //状态 1正常 2解除绑定
                            if (isExeit) {
                                bgRes = R.mipmap.ic_card_dis_bg;
                                headRes = R.mipmap.ic_card_dis_head;
                                typeRes = R.mipmap.ic_card_dis_type;
                                timeRes = R.mipmap.ic_card_dis_time;
                                setSaturation();// 设置头像为彩色
                            }
                        }
                        break;
                }
            }
            tvCardPrice.setTextColor(ResourcesUtils.getColor(textColor));
            tvRealityPrice.setTextColor(ResourcesUtils.getColor(textColor));
            tvCardName.setText(entity.getCardName());
            tvMoney.setText(String.format("¥%s", cardPrice));
            tvNum.setText(realityPrice);

            if (!TextUtils.isEmpty(entity.getOrgImage())) {
                GlideUtils.setGlideImg(context, entity.getOrgImage(), R.mipmap.ic_logo_default, ivHeadIcon);
                tvName.setVisibility(View.GONE);
                ivHeadIcon.setVisibility(View.VISIBLE);
            } else {
                ivHeadIcon.setVisibility(View.GONE);
                tvName.setVisibility(View.VISIBLE);
                String orgShortName = entity.getOrgShortName();
                UserManager.getInstance().getOrgShortName(tvName, orgShortName);// 设置机构简称
                tvName.setTextColor(ResourcesUtils.getColor(textColor));
            }
            rlCardBg.setBackgroundResource(bgRes);
            llHead.setBackgroundResource(headRes);
            tvCardType.setBackgroundResource(typeRes);
            rlCardTime.setBackgroundResource(timeRes);
            ivEditCard.setOnClickListener(v -> {
                // 4:课时卡管理
                showEditDialog(entity);
            });
            ivDelCard.setOnClickListener(v -> {
                // 5:为学员添加课时卡  移除
                cardsList.remove(position);
                UserManager.getInstance().getCardStuList().remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,
                        UserManager.getInstance().getCardStuList().size());
            });
        }

    }

    private void checkCardStatus(CourseEntity entity) {
        unBindCard();
        tvNoActiveCard.setVisibility(View.VISIBLE);
        if (TextUtils.equals(cardType, "4")) {
            tvNoActiveCard.setText("非活跃课时卡");
        } else if (TextUtils.equals(cardType, "3")) {
            if (System.currentTimeMillis() / 1000 > DateUtil.getStringToDate(entity.getYxq(), "yyyy-MM-dd")) {
                // 年卡的时候，过期了显示“过期”，变灰色
                unBindCard();
                tvNoActiveCard.setVisibility(View.VISIBLE);
                tvNoActiveCard.setText("过期");
                tvTime.setText(entity.getYxq());
            }
        } else {
            tvNoActiveCard.setText("已解除绑定");
        }
    }

    private String setDataMonth(String validMonth) {
        if (TextUtils.equals(validMonth, "0")) {
            dataMonth = "长期有效";
        } else {
            String validData = "";
            if (!TextUtils.isEmpty(validMonth) && validMonth != null) {
                validData = TimeUtils.getMonthAgo(new Date(Calendar.getInstance().getTimeInMillis()),
                        Integer.parseInt(validMonth), "yyyy/MM/dd");// 有效期转化为日期
            }
            dataMonth = String.format("有效期:\t%s", validData);
        }
        return dataMonth;
    }

    /**
     * 课时卡管理操作弹窗
     *
     * @param entity isUsed:1：使用过 2：没有使用过
     */
    private void showEditDialog(final CourseEntity entity) {
        String isUsed = entity.getIsUsed();
        String orgId = UserManager.getInstance().getOrgId();
        String orgCardId = entity.getOrgCardId();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(mContext, R.layout.dialog_sign_on, null);
        final TextView tvEditCard = view.findViewById(R.id.tv_org_sign);
        tvEditCard.setText("编辑");
        tvEditCard.setOnClickListener(v -> {
            if (!UserManager.getInstance().isTrueRole("ksk_1")) {
                UserManager.getInstance().showSuccessDialog((Activity) mContext
                        , mContext.getString(R.string.text_role));
            } else {
                //编辑课时卡
                mContext.startActivity(new Intent(mContext, CreateCardActivity.class)
                        .putExtra("addType", "5")// 学员添加新卡的类型 5 编辑课时卡
                        .putExtra("CourseCardsEntity", entity)
                        .putExtra("cardOperaType", "3"));//cardOperaType 1:选择购买新卡 2:选择课时卡 3:新建课时卡管理
                ((Activity) mContext).overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
            }
            dialog.dismiss();
        });
        final TextView tvDelActCard = view.findViewById(R.id.tv_stu_sign);
        if (TextUtils.equals(isUsed, "1")) {
            tvDelActCard.setText("置为非活跃");
        } else if (TextUtils.equals(isUsed, "2")) {
            tvDelActCard.setText("删除");
        }
        tvDelActCard.setOnClickListener(v -> {
            if (TextUtils.equals(isUsed, "1")) {
                showNoActDialog(orgId, orgCardId);
            } else if (TextUtils.equals(isUsed, "2")) {
                showDelDialog(orgId, orgCardId);
            }
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

    // 置为非活跃课时卡弹框
    private void showNoActDialog(String orgId, String orgCardId) {
        new AlertDialog.Builder(mContext)
                .setTitle("提示")
                .setMessage(Html.fromHtml("确定将这张课时卡置为非活跃状态？<br><br>" +
                        "<font color='#b5b5b5' size='10'><small>课时卡置为非活跃后，" +
                        "不影响拥有这张卡的学员的正常使用，但您将无法为学员再新添加这张卡" +
                        "</small></font>"))
                .setPositiveButton("确定", (d, which) -> cardSetInactivity(orgId, orgCardId, "2"))
                .setNegativeButton("取消", (d, which) -> d.dismiss())
                .create().show();
    }

    // 删除课时卡弹框
    private void showDelDialog(String orgId, String orgCardId) {
        new AlertDialog.Builder(mContext)
                .setTitle("提示")
                .setMessage("确定删除这张课时卡？")
                .setPositiveButton("确定", (d, which) -> deleteCard(orgId, orgCardId))
                .setNegativeButton("取消", (d, which) -> d.dismiss())
                .create().show();
    }

    /**
     * 58.课时卡置为非活跃或活跃
     *
     * @param orgId
     * @param orgCardId
     * @paramstatus 设置状态 1：设置为活跃 2：设置为非活跃
     */
    public void cardSetInactivity(String orgId, String orgCardId, String status) {
        HttpManager.getInstance().doCardSetInactivity("NewCardsAdapter", orgId, orgCardId, status,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_NEWCARD));
                        U.showToast("成功");
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NewCardsAdapter onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout((Activity) mContext);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("NewCardsAdapter onError", throwable.getMessage());
                    }
                });
    }

    /**
     * 59.删除机构课时卡
     *
     * @param orgId
     * @param orgCardId
     */
    private void deleteCard(String orgId, String orgCardId) {
        HttpManager.getInstance().doDeleteCard("NewCardsAdapter", orgId, orgCardId,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_NEWCARD));
                        U.showToast("成功");
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NewCardsAdapter onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout((Activity) mContext);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    private void setSaturation() {
        //设置头像为彩色
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(1f); // 设置饱和度:0为纯黑白，饱和度为0；1为饱和度为100，即原图；
        ColorMatrixColorFilter grayColorFilter = new ColorMatrixColorFilter(cm);
        ivHeadIcon.setColorFilter(grayColorFilter);
    }

    /**
     * 解除绑定、余额不足、过期
     */
    private void unBindCard() {
        bgRes = R.mipmap.ic_card_ash_bg;
        headRes = R.mipmap.ic_card_ash_head;
        typeRes = R.mipmap.ic_card_ash_type;
        timeRes = R.mipmap.ic_card_ash_time;
        //设置头像为黑白
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0f); // 设置饱和度:0为纯黑白，饱和度为0；1为饱和度为100，即原图；
        ColorMatrixColorFilter grayColorFilter = new ColorMatrixColorFilter(cm);
        ivHeadIcon.setColorFilter(grayColorFilter);
        textColor = R.color.b0b2b6;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.recycle_stu_card_item;
    }

    public void update(List<CourseEntity> pdata, String status) {
        this.cardsList.clear();
        this.cardsList.addAll(pdata);
        this.status = status;
        // 初始化数据
        initDate();
        notifyDataSetChanged();
    }

    // 初始化isSelected的数据
    private void initDate() {
        if (cardsList.size() > 0) {
            for (int i = 0; i < cardsList.size(); i++) {
                getIsSelected().put(i, false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return cardsList.size();
    }

    public void getSelect(int position, final CheckBox tvSelectCard) {
        tvSelectCard.setSelected(!tvSelectCard.isSelected());
        if (cardsList.size() > 0) {
            for (int i = 0; i < cardsList.size(); i++) {
                if (i == position) {
                    getIsSelected().put(i, true);// 同时修改map的值保存状态
                } else {
                    getIsSelected().put(i, false);// 同时修改map的值保存状态
                }
            }
        }
    }

    public HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }
}
