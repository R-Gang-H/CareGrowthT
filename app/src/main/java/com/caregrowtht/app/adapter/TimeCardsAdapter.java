package com.caregrowtht.app.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.android.library.view.CircleImageView;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * 选择课时卡
 */
public class TimeCardsAdapter extends XrecyclerAdapter {

    @BindView(R.id.rl_card_bg)
    RelativeLayout rlCardBg;
    @BindView(R.id.rl_add_card)
    RelativeLayout rlAddCard;
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

    List<CourseEntity> mListCard = new ArrayList<>();
    // 用来控制CheckBox的选中状况
    private HashMap<Integer, Boolean> isSelected = new HashMap<>();
    //选中课程的信息
    private HashMap<Integer, CourseEntity> mCourseModels = new HashMap<>();
    //选中课程的消课次数
    private HashMap<Integer, String> mCount = new HashMap<>();

    private String cardPrice;
    private String realityPrice;

    //编辑上页数据
    List<CourseEntity> mCourseCards = new ArrayList<>();
    List<CourseEntity> countList = new ArrayList<>();

    public TimeCardsAdapter(List datas, Context context, ViewOnItemClick onItemClick1) {
        super(datas, context, onItemClick1);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        final CourseEntity entity = mListCard.get(position);

        final CheckBox tvSelectCard = (CheckBox) holder.getView(R.id.tv_select_card);
        if (TextUtils.equals(entity.getCardType(), "5")) {//5：新增课时卡(手动添加)
            rlCardBg.setVisibility(View.GONE);
            rlAddCard.setVisibility(View.VISIBLE);

            tvSelectCard.setVisibility(View.INVISIBLE);
        } else {
            rlCardBg.setVisibility(View.VISIBLE);
            rlAddCard.setVisibility(View.GONE);

            tvSelectCard.setVisibility(View.VISIBLE);
            // 根据isSelected来设置checkbox的选中状况
            tvSelectCard.setSelected(getIsSelected().get(position));

            if (TextUtils.equals(entity.getCardType(), "5")) {//2:新建课时卡种类
                tvSelectCard.setVisibility(View.GONE);
            }

            String validMonth = entity.getValidMonth();
            tvTime.setText(String.format("有效期:\t%s", TextUtils.equals(validMonth, "0") ? "长期有效" : entity.getValidMonth() + "个月"));//卡的有效月数 长期有效返回 字符串 "0"

            final TextView tvCardPrice = (TextView) holder.getView(R.id.tv_card_price);
            final TextView tvRealityPrice = (TextView) holder.getView(R.id.tv_reality_price);
            final TextView tvCouont = (TextView) holder.getView(R.id.tv_couont);

            int textColor = 0;//机构名称色值
            int bgRes = 0;//背景id
            int headRes = 0;//头像背景
            int typeRes = 0;
            int timeRes = 0;
            if (!TextUtils.isEmpty(entity.getCardType())) {
                // 卡的类型 1：次卡 2：储值卡 3：年卡 4：折扣卡
                switch (entity.getCardType()) {
                    case "1":
                        tvCardType.setText("次数卡");
                        cardPrice = String.valueOf(Integer.parseInt(entity.getCardPrice()) / 100);
                        realityPrice = entity.getTotalCount() + "次";
                        textColor = R.color.color_caa0;
                        bgRes = R.mipmap.ic_card_num_bg;
                        headRes = R.mipmap.ic_card_num_head;
                        typeRes = R.mipmap.ic_card_num_type;
                        timeRes = R.mipmap.ic_card_num_time;
                        tvCouont.setText("次");
                        break;
                    case "2":
                        tvCardType.setText("储值卡");
                        cardPrice = String.valueOf(Integer.parseInt(entity.getCardPrice()) / 100);
                        realityPrice = "¥" + String.valueOf(Integer.parseInt(entity.getRealityPrice()) / 100) + "元";
                        tvCardPrice.setVisibility(View.VISIBLE);
                        tvRealityPrice.setVisibility(View.VISIBLE);
                        textColor = R.color.color_93ca;
                        bgRes = R.mipmap.ic_card_save_bg;
                        headRes = R.mipmap.ic_card_save_head;
                        typeRes = R.mipmap.ic_card_save_type;
                        timeRes = R.mipmap.ic_card_save_time;
                        break;
                    case "3":
                        //卡片类型
                        switch (getCardType(entity)) {
                            case 1:
                                tvCardType.setText("月卡");
                                break;
                            case 2:
                                tvCardType.setText("学期卡");
                                break;
                            case 3:
                                tvCardType.setText("年卡");
                                break;
                            case 4:
                                tvCardType.setText("折扣卡");
                                break;
                        }
                        cardPrice = String.valueOf(Integer.parseInt(entity.getCardPrice()) / 100);
                        realityPrice = String.format("%s", TextUtils.equals(validMonth, "0") ?
                                "长期有效" : entity.getValidMonth() + "个月");
                        textColor = R.color.color_57a1;
                        bgRes = R.mipmap.ic_card_year_bg;
                        headRes = R.mipmap.ic_card_year_head;
                        typeRes = R.mipmap.ic_card_year_type;
                        timeRes = R.mipmap.ic_card_year_time;
                        break;
                    case "4":
                        tvCardType.setText("折扣卡");
                        realityPrice = "¥" + String.valueOf(Integer.parseInt(entity.getRealityPrice()) / 100) + "元";
                        tvCardPrice.setVisibility(View.VISIBLE);
                        cardPrice = (Integer.parseInt(entity.getCardPrice()) / 100) + "\t\t" + entity.getDiscount() + "折";
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
            tvCardName.setText(entity.getCardName());
            tvMoney.setText(String.format("¥%s", cardPrice));
            tvNum.setText(realityPrice);

            final CircleImageView ivHeadIcon = (CircleImageView) holder.getView(R.id.iv_head_icon);

            if (!TextUtils.isEmpty(entity.getOrgImage())) {
                GlideUtils.setGlideImg(context, entity.getOrgImage(), R.mipmap.ic_logo_default, ivHeadIcon);
                tvName.setVisibility(View.GONE);
                ivHeadIcon.setVisibility(View.VISIBLE);
            } else {
                ivHeadIcon.setVisibility(View.GONE);
                tvName.setVisibility(View.VISIBLE);
                String orgShortName = entity.getOrgShortName();
                UserManager.getInstance().getOrgShortName(tvName, orgShortName);// 设置机构简称
                tvName.setTextColor(context.getResources().getColor(textColor));
            }
            rlCardBg.setBackgroundResource(bgRes);
            llHead.setBackgroundResource(headRes);
            tvCardType.setBackgroundResource(typeRes);
            rlCardTime.setBackgroundResource(timeRes);

            final EditText etCancelCount = (EditText) holder.getView(R.id.et_cancel_count);
            if (etCancelCount.getTag() instanceof TextWatcher) {
                etCancelCount.removeTextChangedListener((TextWatcher) etCancelCount.getTag());
            }
            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String num = s.toString().trim();
                    if (TextUtils.isEmpty(num)) {
                        return;
                    }
//                    int inputNum = 0;
//                    // 卡的类型 1：次卡 2：储值卡 3：年卡 4：折扣卡
//                    switch (entity.getCardType()) {
//                        case "1":
//                            inputNum = Integer.valueOf(entity.getTotalCount());
//                            break;
//                        case "2":
//                        case "4":
//                            inputNum = Integer.parseInt(entity.getRealityPrice()) / 100;
//                            break;
//                    }
//                    if (Integer.valueOf(num) > inputNum) {
//                        U.showToast("超出消课范围");
//                        getCount().put(position, "");
//                    etCancelCount.setText("");
//                        return;
//                    }
                    getCount().put(position, num);
                }
            };
            //编辑之前是否选中
            RelativeLayout rlSelect = (RelativeLayout) holder.getView(R.id.rl_select);
            //卡片类型
            switch (getCardType(entity)) {
                case 1://月卡
                case 2://学期卡
                case 3://年卡 不显示消课多少元
                    rlSelect.setVisibility(View.GONE);
                    break;
                case 4://折扣卡
                default:
                    rlSelect.setVisibility(getIsSelected().get(position)
                            ? View.VISIBLE : View.GONE);
                    etCancelCount.setText(getCount().get(position));
                    break;
            }
            etCancelCount.addTextChangedListener(watcher);
            etCancelCount.setTag(watcher);
        }
    }

    @Override
    public int getLayoutResId() {
        return R.layout.recycle_stu_card_item;
    }

    public void update(List<CourseEntity> mListCard, List<CourseEntity> mCourseCards, List<CourseEntity> countList) {
        this.mListCard.clear();
        this.mListCard.addAll(mListCard);
        this.mCourseCards.clear();
        this.mCourseCards.addAll(mCourseCards);
        this.countList.clear();
        this.countList.addAll(countList);
        // 初始化数据
        initDate();
        notifyDataSetChanged();
    }

    // 初始化isSelected的数据
    private void initDate() {
        if (mListCard.size() > 0) {
            for (int i = 0; i < mListCard.size(); i++) {
                //循环获取上页已选中的
                boolean isSele = false;
                String count = "";
                CourseEntity courseCard = null;
                for (int j = 0; j < mCourseCards.size(); j++) {
                    if (TextUtils.equals(mListCard.get(i).getOrgCardId(),
                            mCourseCards.get(j).getOrgCardId())) {
                        isSele = true;
                        count = countList.get(j).getCourseCount();
                        courseCard = mCourseCards.get(j);
                        break;
                    }
                }
                getIsSelected().put(i, isSele);
                getCount().put(i, count);
                getIsCourseCardEntity().put(i, courseCard);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mListCard.size();
    }

    public void getSelect(int position, final CheckBox tvSelectCard, final RelativeLayout rlSelect) {
        tvSelectCard.setSelected(!tvSelectCard.isSelected());
        if (mListCard.size() > 0) {
            getIsSelected().put(position, tvSelectCard.isSelected());// 同时修改map的值保存状态
            CourseEntity entity = mListCard.get(position);
            //卡片类型
            switch (getCardType(entity)) {
                case 1://月卡
                case 2://学期卡
                case 3://年卡 不显示消课多少元
                    rlSelect.setVisibility(View.GONE);
                    getCount().put(position, "");// 年卡默认情况都为0
                    getIsCourseCardEntity().put(position, tvSelectCard.isSelected() ? entity : null);
                    break;
                case 4://折扣卡
                default:
                    rlSelect.setVisibility(tvSelectCard.isSelected() ? View.VISIBLE : View.GONE);
                    getCount().put(position, "");// 默认情况都为空
                    getIsCourseCardEntity().put(position, tvSelectCard.isSelected() ? entity : null);
                    break;
            }
        }
    }

    private int getCardType(CourseEntity entity) {
        if (TextUtils.equals(entity.getCardType(), "3")) {
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
                return 1;//月卡
            } else if (mm >= 4 && mm <= 9) {
                return 2;//学期卡
            } else if (mm >= 10) {
                return 3;//年卡
            } else {
                return 4;//折扣卡
            }
        }
        return 0;
    }

    public HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public HashMap<Integer, CourseEntity> getIsCourseCardEntity() {
        return mCourseModels;
    }

    public HashMap<Integer, String> getCount() {
        return mCount;
    }
}
