package com.caregrowtht.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.library.MyApplication;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.PutOrPayActivity;
import com.caregrowtht.app.activity.SpaceImageDetailActivity;
import com.caregrowtht.app.model.PutPayEntity;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

public class PutPayAdapter extends XrecyclerAdapter {

    @BindView(R.id.tv_card_type)
    TextView tvCardType;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_card_content)
    TextView tvCardContent;
    @BindView(R.id.tv_teacher)
    TextView tvTeacher;
    @BindView(R.id.tv_voucher)
    TextView tvVoucher;
    @BindView(R.id.tv_remark)
    TextView tvRemark;
    @BindView(R.id.tv_teacher_time)
    TextView tvTeacherTime;
    @BindView(R.id.tv_sys_create)
    TextView tvSysCreate;
    @BindView(R.id.iv_edit_notify)
    ImageView ivEditNotify;
    @BindView(R.id.iv_invalid)
    ImageView ivInvalid;
    @BindView(R.id.iv_emtity)
    ImageView ivEmtity;

    private List<PutPayEntity.TableData> listDatas = new ArrayList<>();

    public PutPayAdapter(List datas, Context context) {
        super(datas, context);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        PutPayEntity.TableData entity = listDatas.get(position);
        boolean isPut = entity.getClassTitle().trim().equals("??????");// ????????????
        tvCardType.setText(entity.getTradeType());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.00");// ???????????????
        tvMoney.setText(String.format("%s\t??%s", isPut ? "+" : "-",
                decimalFormat.format(Double.valueOf(entity.getPrice2()))));
        tvMoney.setTextColor(ResourcesUtils.getColor(isPut ? R.color.color_ee8 : R.color.greenLight));
        boolean isSys = entity.getIsInits().equals("??????") || entity.getIsInits().equals("?????????");
        String detail = StrUtils.parseEmpty(entity.getDetail());
        tvCardContent.setVisibility(isSys || StrUtils.isNotEmpty(detail) ? View.VISIBLE : View.GONE);
        String cardContent;
        if (isSys) {
            cardContent = String.format("%s%s\t\t%s", entity.getChildType(), entity.getChildName(), detail);
        } else {
            cardContent = String.format("%s", detail);
        }
        tvCardContent.setText(cardContent);
        tvTeacher.setText(String.format("????????????:\t\t%s", StrUtils.isEmpty(entity.getAgentManName()) ?
                "???" : entity.getAgentManName()));
        String voucher = entity.getVoucher();
        String voucherName = "";
        if (StrUtils.isNotEmpty(voucher)) {
            voucherName = voucher.substring(voucher.lastIndexOf("/") + 1);
        }
        tvVoucher.setText(StrUtils.isEmpty(voucher) ? "???" : voucherName);
        tvVoucher.setCompoundDrawablesRelativeWithIntrinsicBounds(StrUtils.isEmpty(voucher) ? 0 : R.mipmap.ic_atter, 0, 0, 0);
        tvVoucher.setTextColor(ResourcesUtils.getColor(StrUtils.isEmpty(voucher) ? R.color.color_3 : R.color.blueLight));
        if (StrUtils.isNotEmpty(voucher)) {
            tvVoucher.setOnClickListener(v -> {
                setSignSheet(voucher);
            });
        }
        tvRemark.setText(String.format("??????:\t\t%s", StrUtils.isEmpty(entity.getRemark()) ? "???" : entity.getRemark()));
        tvTeacherTime.setText(String.format("%s\t\t%s", entity.getSaleManName()
                , TimeUtils.getDateToString(Long.valueOf(entity.getCreate_at()), "yyyy/MM/dd HH:mm")));
        ivInvalid.setVisibility(entity.getIsActive().equals("2") ? View.VISIBLE : View.GONE);// ???????????? 1????????? 2?????????
        String isInits = entity.getIsInits(), sysCreate;
        sysCreate = isInits;
        if (isInits.contains("??????")) {
            sysCreate = "????????????";
        } else if (isInits.contains("??????")) {
            sysCreate = "????????????";
        }
        tvSysCreate.setText(sysCreate);
        ivEmtity.setVisibility(position == listDatas.size() - 1 ? View.VISIBLE : View.GONE);
        ivEditNotify.setOnClickListener(v -> {
            //1???????????? 2????????????
            context.startActivity(new Intent(context, PutOrPayActivity.class)
                    .putExtra("type", isPut ? "1" : "2") //1???????????? 2????????????
                    .putExtra("tableData", entity));
        });
    }

    private void setSignSheet(String signSheet) {
        if (!TextUtils.isEmpty(signSheet)) {// ??????????????????
            String[] circlePicture = {};
            if (!TextUtils.isEmpty(signSheet)) {
                circlePicture = signSheet.split("#");
            }
            ArrayList<String> arrImageList = new ArrayList<>();
            Collections.addAll(arrImageList, circlePicture);//???????????????
            Intent intent = new Intent(MyApplication.getAppContext(), SpaceImageDetailActivity.class);
            intent.putExtra("images", arrImageList);//?????????
            intent.putExtra("position", 0);
            mContext.startActivity(intent);
        }
    }

    public void setData(List<PutPayEntity.TableData> listDatas) {
        this.listDatas.clear();
        this.listDatas.addAll(listDatas);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listDatas.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_put_pay;
    }
}
