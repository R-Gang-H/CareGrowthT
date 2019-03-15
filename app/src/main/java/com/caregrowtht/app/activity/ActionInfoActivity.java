package com.caregrowtht.app.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.OrgEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.BitmapUtils;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.ImgLabelUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.view.RecoDialog;
import com.caregrowtht.app.view.SharePopupWindow;
import com.google.zxing.WriterException;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-4-8 12:45:24 活动详情
 */
public class ActionInfoActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_actImage)
    ImageView ivActImage;
    @BindView(R.id.tv_actName)
    TextView tvActName;
    @BindView(R.id.tv_actOrg)
    TextView tvActOrg;
    @BindView(R.id.tv_publishTime)
    TextView tvPublishTime;
    @BindView(R.id.tv_action_detail)
    TextView tvActionDetail;
    @BindView(R.id.tv_actBeginTime)
    TextView tvActBeginTime;
    @BindView(R.id.tv_actendTime)
    TextView tvActendTime;
    @BindView(R.id.tv_actSite)
    TextView tvActSite;
    @BindView(R.id.tv_rmDesc)
    TextView tvRmDesc;
    @BindView(R.id.tv_actMoney)
    TextView tvActMoney;
    @BindView(R.id.img_act_qrcode)
    ImageView imgActQrcode;
    @BindView(R.id.img_org_qrcode)
    ImageView imgOrgQrcode;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.ll_share)
    LinearLayout llShare;
    @BindView(R.id.ll_join)
    LinearLayout llJoin;
    @BindView(R.id.tv_actTime)
    TextView tvActTime;
    @BindView(R.id.tv_Site)
    TextView tvSite;
    @BindView(R.id.tv_rm)
    TextView tvRm;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.ll_follow)
    LinearLayout llFollow;
    @BindView(R.id.img_follow)
    ImageView imgFollow;
    @BindView(R.id.tv_follow)
    TextView tvFollow;
    @BindView(R.id.tv_join)
    TextView tvJoin;

    private String actId;
    private int target;
    private String actName;
    private String actOrg;
    private String mActImage;

    private OrgEntity mOrgEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_action_info;
    }

    @Override
    public void initView() {
        tvTitle.setText("活动详情");
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            getActivityInfo();//活动详情
        });
    }

    @Override
    public void initData() {
        actId = getIntent().getStringExtra("actId");
        getActivityInfo();
    }

    public void getActivityInfo() {
        //haoruigang on 2018-4-8 15:11:49 活动详情
        HttpManager.getInstance().doActivityInfo("ActionInfoActivity",
                actId, new HttpCallBack<BaseDataModel<OrgEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<OrgEntity> data) {
                        LogUtils.d("ActionInfoActivity onSuccess", data.getData().toString());
                        setData(data.getData().get(0));
                        refreshLayout.finishLoadmore();
                        refreshLayout.finishRefresh();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("ActionInfoActivity onFail", statusCode + ":" + errorMsg);
                        refreshLayout.finishLoadmore();
                        refreshLayout.finishRefresh();
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ActionInfoActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("ActionInfoActivity onError", throwable.getMessage());
                        refreshLayout.finishLoadmore();
                        refreshLayout.finishRefresh();
                    }
                });
    }

    public void setData(OrgEntity data) {

        mOrgEntity = data;
        mActImage = data.getActImage();
        GlideUtils.setGlideImg(ActionInfoActivity.this, mActImage, R.mipmap.ic_avatar_default, ivActImage);
        actName = data.getActName();
        tvActName.setText(actName);
        actOrg = data.getActOrg();
        tvActOrg.setText(actOrg);
        tvPublishTime.setText(DateUtil.getDate(Long.parseLong(data.getPublishTime()),
                "yyyy-MM-dd HH:mm"));
        ImgLabelUtils.getInstance().htmlThree(tvActionDetail, data.getDetail());//加载Html富文本及图片
        tvActBeginTime.setText(String.format("%s至",
                DateUtil.getDate(Long.parseLong(data.getActBeginTime()), "yyyy-MM-dd HH:mm")));
        tvActendTime.setText(DateUtil.getDate(Long.parseLong(data.getActendTime()), "yyyy-MM-dd HH:mm"));
        //item.getCname() + "\t" +
        tvActSite.setText(String.format("%s-%s\t%s", data.getSname(), data.getAname(), data.getAddress()));
        tvRmDesc.setText(data.getTvRmDesc());
        tvActMoney.setText(data.getActMoney());
        Bitmap bitmapAct = null;
        Bitmap bitmapOrg = null;
        try {
            bitmapAct = BitmapUtils.create2DCode(data.getActQRCodeUrl());//根据内容生成二维码
            bitmapOrg = BitmapUtils.create2DCode(data.getOrgQRCodeUrl());//根据内容生成二维码
            imgActQrcode.setImageBitmap(bitmapAct);
            imgOrgQrcode.setImageBitmap(bitmapOrg);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        data.getLimitNum();//最大报名人数
        tvJoin.setText(String.format("报名%s人", data.getJoinNum()));//已报名人数

        if (data.getIsStar() == 0) {//是否关注了这个活动 0：未关注 1：已关注
            tvFollow.setText("关注");
            imgFollow.setImageResource(R.mipmap.ic_follow_no);
            target = 1;//1:关注 2:取消关注
        } else {
            tvFollow.setText("已关注");
            imgFollow.setImageResource(R.mipmap.ic_follow_yes);
            target = 2;//1:关注 2:取消关注
        }
    }

    @OnClick({R.id.rl_back_button, R.id.ll_follow, R.id.ll_share, R.id.ll_join})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.ll_follow:
                followActivity();
                break;
            case R.id.ll_share:
                UMImage image = new UMImage(this, mActImage);
                //分享页上传
                String shareUrl = mOrgEntity.getActQRCodeUrl();
                Log.e("----------", "sharUrl===" + shareUrl);
                UMWeb web = new UMWeb(shareUrl);
                web.setTitle(actName);//标题
                web.setThumb(image);  //缩略图
                web.setDescription(mOrgEntity.getBrief());//描述
                new RecoDialog(this, web, view1 -> {
                }).show();
                break;
            case R.id.ll_join:
                startActivity(new Intent(ActionInfoActivity.this, JoinInfoActionActivity.class).putExtra("actId", actId));
                break;
        }
    }

    private void followActivity() {
        //haoruigang on 2018-4-9 10:49:47 关注或者取消关注活动
        HttpManager.getInstance().dofollowAction("ActionInfoActivity", actId, target,
                new HttpCallBack<BaseDataModel<OrgEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<OrgEntity> data) {
                        if (mOrgEntity.getIsStar() == 0) {//0:未关注 1:已关注
                            tvFollow.setText("关注");
                            imgFollow.setImageResource(R.mipmap.ic_follow_no);
                            mOrgEntity.setIsStar(1);
                        } else if (mOrgEntity.getIsStar() == 1) {
                            tvFollow.setText("已关注");
                            imgFollow.setImageResource(R.mipmap.ic_follow_yes);
                            mOrgEntity.setIsStar(0);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ActionInfoActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

}
