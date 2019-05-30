package com.caregrowtht.app.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.BaseMsgAdapter;
import com.caregrowtht.app.adapter.BaseMsgTimeAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.BaseModel;
import com.caregrowtht.app.model.OrgEntity;
import com.caregrowtht.app.model.PayEntity;
import com.caregrowtht.app.model.PriceMsgEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.uitil.pay.PayUtil;
import com.caregrowtht.app.uitil.permissions.PermissionCallBackM;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;

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
 * 购买
 */
public class BuyActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_trial)
    RelativeLayout rlTrial;
    @BindView(R.id.rl_version_info)
    RelativeLayout rlVersionInfo;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.recycler_view_time)
    RecyclerView recyclerViewTime;
    @BindView(R.id.tv_ver_msg)
    TextView tvVerMsg;
    @BindView(R.id.tv_stu_num_msg)
    TextView tvStuNumMsg;
    @BindView(R.id.tv_stu_volume)
    TextView tvStuVolume;
    @BindView(R.id.tv_validity_msg)
    TextView tvValidityMsg;
    @BindView(R.id.tv_stu_num)
    TextView tvStuNum;
    @BindView(R.id.tv_validity)
    TextView tvValidity;
    @BindView(R.id.tv_old_price)
    TextView tvOldPrice;
    @BindView(R.id.tv_left_price)
    TextView tvLeftPrice;
    @BindView(R.id.tv_price)
    TextView tvPrice;

    BaseMsgAdapter baseMsgAdapter;
    List<PriceMsgEntity> priceData = new ArrayList<>();

    BaseMsgTimeAdapter baseMsgTimeAdapter;
    List<PriceMsgEntity> priceTimeData = new ArrayList<>();
    private String stuNumber = "0";
    private PriceMsgEntity priceEntity;
    private PriceMsgEntity onePriceEntity;
    private String leftPrice;// 折扣金额
    private String OrgId, orgStuNum, facePrice;// 机构Id，当前机构的学员人数
    private boolean isRenew;


    @Override
    public int getLayoutId() {
        return R.layout.activity_buy;
    }

    @Override
    public void initView() {
        initRecyclerGrid(recyclerView, 3);
        baseMsgAdapter = new BaseMsgAdapter(priceData, this, this);
        recyclerView.setAdapter(baseMsgAdapter);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(10));

        initRecyclerGrid(recyclerViewTime, 3);
        baseMsgTimeAdapter = new BaseMsgTimeAdapter(priceTimeData, this, this);
        recyclerViewTime.setAdapter(baseMsgTimeAdapter);
        recyclerViewTime.addItemDecoration(new ItemOffsetDecoration(10));

        isRenew = getIntent().getBooleanExtra("renew", false);// 是否续费
        tvTitle.setText(isRenew ? "我的爱成长" : "购买");

    }

    class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mSpacing;

        public ItemOffsetDecoration(int itemOffset) {
            mSpacing = itemOffset;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mSpacing, mSpacing / 2,
                    0, mSpacing / 2);
        }
    }

    @Override
    public void initData() {
        String orgId = getIntent().getStringExtra("orgId");
        if (StrUtils.isNotEmpty(orgId)) {
            OrgId = orgId;
            UserManager.getInstance().setOrgId(OrgId);
        } else {
            OrgId = UserManager.getInstance().getOrgId();
        }
        getProducts("2");
        requestPermission(
                Constant.PERMISSIONS_REQUEST_CODE,
                new String[]{Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                getString(R.string.rationale_file),
                new PermissionCallBackM() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionGrantedM(int requestCode, String... perms) {
                        LogUtils.e(BuyActivity.this, "TODO:  Granted", Toast.LENGTH_SHORT);
                        // 所需的权限均正常获取
//                        U.showToast(getString(R.string.permission_granted));
                    }

                    @Override
                    public void onPermissionDeniedM(int requestCode, String... perms) {
                        LogUtils.e(BuyActivity.this, "TODO:  Denied", Toast.LENGTH_SHORT);
                        U.showToast(getResources().getString(R.string.auth_failed));
                    }
                });
        getOrgLeftPrice();
        getOrgChildNum();
    }

    private void getOrgLeftPrice() {
        HttpManager.getInstance().doGetOrgLeftPrice("BuyActivity", OrgId,
                new HttpCallBack<BaseModel<PriceMsgEntity>>() {
                    @Override
                    public void onSuccess(BaseModel<PriceMsgEntity> data) {
                        leftPrice = data.getData().getLeftPrice();
                        tvOldPrice.setVisibility(leftPrice.equals("0") ? View.GONE : View.VISIBLE);
                        tvLeftPrice.setVisibility(leftPrice.equals("0") ? View.GONE : View.VISIBLE);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("BuyActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(BuyActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("BuyActivity onError", throwable.getMessage());
                    }
                });
    }

    /**
     * @param orgVersion：2 基础版 3：是高级版
     */
    private void getProducts(String orgVersion) {
        HttpManager.getInstance().doGetProducts("BuyActivity", orgVersion,
                new HttpCallBack<BaseDataModel<PriceMsgEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<PriceMsgEntity> data) {
                        priceData.clear();
                        priceData.addAll(data.getData());
                        baseMsgAdapter.setData(priceData);
                        if (StrUtils.isNotEmpty(priceData)) {// 默认选中第一项
                            getPro(0);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("BuyActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(BuyActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("BuyActivity onError", throwable.getMessage());
                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.btn_questionnaire, R.id.btn_pay, R.id.tv_view_fct, R.id.btn_pay_base})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.btn_questionnaire:
                Intent intent = new Intent(this, UserTermActivity.class);
                String uId = UserManager.getInstance().userData.getUid();
                intent.setData(Uri.parse(String.format(Constant.QUESTIONNAIRE, OrgId, uId, "1", "1")));
                intent.putExtra("openType", "2");// 调查问卷
                startActivity(intent);
                break;
            case R.id.btn_pay:
                getOtherInfo("1");// 试用99,获取订单信息
                break;
            case R.id.btn_pay_base:
                if (Integer.valueOf(orgStuNum) > Integer.valueOf(stuNumber)) {// 机构学员人数 > 续费的学员人数
                    showSuccessDialog(BuyActivity.this, String.format("您的机构活跃学员人数已经达到%s人" +
                            "，请您选择%s人以上的续费包进行续费!", orgStuNum, orgStuNum));
                    return;
                }
                getOtherInfo("2");// 购买或续费,获取订单信息
                break;
            case R.id.tv_view_fct:
                startActivity(new Intent(this, FCTListActivity.class));
                break;
        }
    }

    public void showSuccessDialog(final Activity mContext, String desc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(mContext, R.layout.dialog_teach_lib, null);
        TextView tvDesc = view.findViewById(R.id.tv_desc);
        tvDesc.setText(desc);
        TextView tvOk = view.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(v -> dialog.dismiss());
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
    }

    private void getOtherInfo(String type) {
        String orgId = UserManager.getInstance().getOrgId();
        String subject = "", price = "", end_at = "", version = "", stu_num_allowed = "", total_day = "";
        if (type.equals("1")) {//  试用99,获取订单信息
            subject = "试用99";
            version = "1";
            stu_num_allowed = "20000";
            price = "9900";
            total_day = "30";
            String validData = TimeUtils.getMonthAgo(new Date(Calendar.getInstance().getTimeInMillis()),
                    Integer.parseInt("1"), "yyyy年MM月dd日");// 有效期转化为日期
            end_at = String.valueOf(DateUtil.getStringToDate(validData, "yyyy年MM月dd日"));
        } else {// 购买或续费,获取订单信息
            total_day = String.valueOf(Integer.valueOf(onePriceEntity.getBuy_month()) * 30);
            if (StrUtils.isNotEmpty(priceEntity)) {
                subject = priceEntity.getTitle();
                version = priceEntity.getVersion();
                stu_num_allowed = priceEntity.getMaxnum();
            }
            if (StrUtils.isNotEmpty(onePriceEntity)) {
                price = String.valueOf(Integer.valueOf(onePriceEntity.getPrice()) - Integer.valueOf(leftPrice));// 计算支付金额
                String validData = TimeUtils.getMonthAgo(new Date(Calendar.getInstance().getTimeInMillis()),
                        Integer.parseInt(onePriceEntity.getBuy_month()), "yyyy年MM月dd日");// 有效期转化为日期
                end_at = String.valueOf(DateUtil.getStringToDate(validData, "yyyy年MM月dd日"));
            }
        }
        if (Integer.valueOf(price) < 0) {
            showSuccessDialog(BuyActivity.this, "不可以操作");
            return;
        }
        HttpManager.getInstance().doGetOrderInfo("BuyActivity", orgId, subject, price, end_at
                , version, stu_num_allowed, total_day, facePrice, new HttpCallBack<BaseDataModel<String>>() {
                    @Override
                    public void onSuccess(BaseDataModel<String> data) {
                        PayUtil.PAY_UTIL.payResult(data.getData().get(0),
                                BuyActivity.this, new PayUtil.OnPayResultListener() {
                                    @Override
                                    public void paySuccess(PayUtil.payResult result, PayEntity payEntity) {
                                        if (PayUtil.payResult.ALIPAY_SUCCESS.equals(result)) {// 成功
                                            andUpdateOrder(payEntity);
                                        }
                                    }

                                    @Override
                                    public void payResult(PayUtil.payResult result) {
                                        if (PayUtil.payResult.ALIPAY_UNKNOWN.equals(result)) {
                                            U.showToast("支付结果未知");
                                        } else if (PayUtil.payResult.ALIPAY_REPEAT.equals(result)) {
                                            U.showToast("重复请求");
                                        } else {// 失败
                                            U.showToast("支付失败");
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("BuyActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(BuyActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("BuyActivity onError", throwable.getMessage());
                    }
                });
    }

    private void andUpdateOrder(PayEntity payEntity) {
        String orderNumber = payEntity.getAlipay_trade_app_pay_response().getOut_trade_no();
        HttpManager.getInstance().doAndUpdateOrder("BuyActivity", OrgId, orderNumber, new HttpCallBack<BaseDataModel>() {
            @Override
            public void onSuccess(BaseDataModel data) {
                EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_TEACHER_HOME));
                startActivity(new Intent(BuyActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFail(int statusCode, String errorMsg) {
                LogUtils.d("BuyActivity onFail", statusCode + ":" + errorMsg);
                if (statusCode == 1002 || statusCode == 1011) {//异地登录
                    U.showToast("该账户在异地登录!");
                    HttpManager.getInstance().dologout(BuyActivity.this);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtils.d("BuyActivity onError", throwable.getMessage());
            }
        });
    }

    @Override
    public void setOnItemClickListener(View view, int position) {
        switch (view.getId()) {
            case R.id.rl_bg:
                getPro(position);
                break;
            case R.id.rb_year:
                getOneProduct(position);
                break;
        }
    }

    private void getPro(int position) {
        priceEntity = priceData.get(position);
        String pid = priceEntity.getId();
        stuNumber = priceEntity.getMaxnum();// 学员人数
        facePrice = priceEntity.getView_price();
        baseMsgAdapter.getSelect(position);
        getProDetail(pid);
    }

    private void getOneProduct(int position) {
        String id = priceTimeData.get(position).getId();
        baseMsgTimeAdapter.getSelect(position);
        getOneProductDetail(id);
    }

    private void getProDetail(String pid) {
        HttpManager.getInstance().doGetPriceList("BuyActivity",
                pid, new HttpCallBack<BaseDataModel<PriceMsgEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<PriceMsgEntity> data) {
                        priceTimeData.clear();
                        priceTimeData.addAll(data.getData());
                        baseMsgTimeAdapter.setData(priceTimeData);

                        if (StrUtils.isNotEmpty(priceTimeData)) {// 默认选中第一项
                            getOneProduct(0);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("BuyActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(BuyActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("BuyActivity onError", throwable.getMessage());
                    }
                });
    }

    private void getOneProductDetail(String pid) {
        HttpManager.getInstance().doGetOneProductDetail("BuyActivity",
                pid, new HttpCallBack<BaseDataModel<PriceMsgEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<PriceMsgEntity> data) {
                        onePriceEntity = data.getData().get(0);
                        tvStuNum.setText(String.format("学员人数：%s人", stuNumber));
                        String validData = TimeUtils.getMonthAgo(new Date(Calendar.getInstance().getTimeInMillis()),
                                Integer.parseInt(onePriceEntity.getBuy_month()), "yyyy年MM月dd日");// 有效期转化为日期
                        tvValidity.setText(String.format("有效期至 %s", validData));
                        Integer oldPrice = Integer.valueOf(onePriceEntity.getPrice()) / 100;
                        tvOldPrice.setText(String.format("价格:￥%s元", oldPrice));
                        Integer balance = Integer.valueOf(leftPrice) / 100;
                        tvLeftPrice.setText(String.format("剩余折扣:\t-\t%s元", balance));
                        tvPrice.setText(String.format("¥%s", oldPrice - balance));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("BuyActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(BuyActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("BuyActivity onError", throwable.getMessage());
                    }
                });
    }

    public void getOrgChildNum() {
        //haoruigang on 2018-8-7 17:11:12 29.获取机构的正式学员数量
        HttpManager.getInstance().doGetOrgChildNum("BuyActivity",
                OrgId, "2", new HttpCallBack<BaseDataModel<StudentEntity>>() {

                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        orgStuNum = data.getData().get(0).getCount();
                        OrgEntity orgEntity = UserManager.getInstance().getOrgEntity();
                        if (StrUtils.isNotEmpty(orgEntity)) { // 是否续费
                            String endAt = orgEntity.getEnd_at();
                            rlTrial.setVisibility((!StrUtils.isEmpty(endAt) && !endAt.equals("0")) ?
                                    View.GONE : View.VISIBLE);// 未购买或未续费,过期
                            rlVersionInfo.setVisibility(isRenew && !StrUtils.isEmpty(endAt)
                                    && !endAt.equals("0") ? View.VISIBLE : View.GONE);
                            if (isRenew && !StrUtils.isEmpty(endAt) && !endAt.equals("0")) {
                                String versionText = "", stuVolume = "";
                                if (StrUtils.isEmpty(orgEntity.getVersion())) {
                                    orgEntity.setVersion("1");
                                }
                                switch (orgEntity.getVersion()) {
                                    case "1":// 试用版
                                        versionText = "试用版";
                                        stuVolume = "无限量";
                                        orgEntity.setMaxStudent(stuVolume);
                                        break;
                                    case "2":// 基础版
                                        versionText = "基础管理版";
                                        break;
                                    case "3":// 市场招生版
                                        versionText = "市场招生版";
                                        break;
                                }
                                tvVerMsg.setText(versionText);
                                tvStuNumMsg.setText(orgStuNum);
                                tvStuVolume.setText(String.format("/%s", orgEntity.getMaxStudent()));
                                tvValidityMsg.setText(String.format("有效期至:\t%s",
                                        DateUtil.getDate(Long.parseLong(endAt), "yyyy/MM/dd")));
                            }
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("BuyActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(BuyActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.tag("BuyActivity throwable " + throwable);
                    }
                });
    }
}
