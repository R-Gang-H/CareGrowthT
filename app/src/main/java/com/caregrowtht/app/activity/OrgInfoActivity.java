package com.caregrowtht.app.activity;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.android.library.view.CircleImageView;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.OrgEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.UserManager;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import butterknife.BindView;
import butterknife.OnClick;

import static com.caregrowtht.app.Constant.BASE_ORG_URL;

/**
 * haoruigang on 2018-10-19 15:28:40
 * 机构信息
 */
public class OrgInfoActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.rl_next_button)
    RelativeLayout rlNextButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.cv_name_bg)
    CardView cvNameBg;
    @BindView(R.id.img_org)
    CircleImageView imgOrg;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_org_short_name)
    TextView tvOrgShortName;
    @BindView(R.id.tv_org_name)
    TextView tvOrgName;
    @BindView(R.id.tv_org_phone)
    TextView tvOrgPhone;
    @BindView(R.id.tv_org_mobile)
    TextView tvOrgMobile;
    @BindView(R.id.tv_org_address)
    TextView tvOrgAddress;

    private OrgEntity orgEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_org_info;
    }

    @Override
    public void initView() {
        tvTitleRight.setTextSize(16);
        tvTitleRight.setText("编辑");
        rlNextButton.setVisibility(View.VISIBLE);
        tvTitle.setText("机构信息");
    }

    @Override
    public void initData() {
        getOrgInfo();
    }

    @OnClick({R.id.rl_back_button, R.id.rl_next_button, R.id.img_org, R.id.cv_name_bg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.rl_next_button:
                startActivity(new Intent(this, CreateOrgActivity.class)
                        .putExtra("orgEntity", orgEntity));
                break;
            case R.id.img_org:
            case R.id.cv_name_bg:
                if (TextUtils.isEmpty(orgEntity.getIntro())) {
                    startActivity(new Intent(this, OrgNoSettingActivity.class)
                            .putExtra("orgEntity", orgEntity));
                } else {
                    Intent intent = new Intent(this, UserTermActivity.class);
                    intent.setData(Uri.parse(BASE_ORG_URL + UserManager.getInstance().getOrgId()));
                    intent.putExtra("openType", "3");// 用户协议
                    startActivity(intent);
                }
                break;
        }
    }

    private void getOrgInfo() {
        //2:返回机构的详细信息 haoruigang on 2018-4-4 16:09:48
        HttpManager.getInstance().doOrgInfo("OrgInfoActivity", UserManager.getInstance().getOrgId(),
                "2", new HttpCallBack<BaseDataModel<OrgEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<OrgEntity> data) {
                        LogUtils.d("OrgInfoActivity onSuccess", data.getData().toString());
                        setData(data.getData());
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("OrgInfoActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(OrgInfoActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("OrgInfoActivity onError", throwable.getMessage());
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getOrgInfo();//更新编辑后的信息
    }

    /**
     * 机构详情数据处理
     *
     * @param data
     */
    private void setData(ArrayList<OrgEntity> data) {
        orgEntity = data.get(0);

        if (!TextUtils.isEmpty(orgEntity.getOrgImage())) {
            GlideUtils.setGlideImg(this, orgEntity.getOrgImage(), R.mipmap.ic_logo_default, imgOrg);
            cvNameBg.setVisibility(View.GONE);
            imgOrg.setVisibility(View.VISIBLE);
        } else {
            imgOrg.setVisibility(View.GONE);
            cvNameBg.setVisibility(View.VISIBLE);
            String orgShortName = orgEntity.getOrgShortName();
            UserManager.getInstance().getOrgShortName(tvName, orgShortName);// 设置机构简称
            tvName.setTextColor(getResources().getColor(R.color.white));
        }

        tvOrgShortName.setText(orgEntity.getOrgShortName());
        tvOrgName.setText(String.format("%s", TextUtils.isEmpty(orgEntity.getOrgChainName()) ?
                orgEntity.getOrgName() : orgEntity.getOrgName() + orgEntity.getOrgChainName()));
        tvOrgPhone.setText(orgEntity.getTelephone());
        tvOrgMobile.setText(orgEntity.getOrgPhone());
        tvOrgAddress.setText(String.format("%s\t%s\t%s\t%s", orgEntity.getSname(), orgEntity.getCname(),
                orgEntity.getAname(), orgEntity.getOrgAddress()));
    }

}
