package com.caregrowtht.app.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.BaseActivity;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.permissions.PermissionCallBackM;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.BaseMediaObject;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

/**
 * Created by ZMM on 2017/12/6.
 */

public class SharePopupWindow extends PopupWindow {

    private BaseActivity mActivity;
    private BaseMediaObject web;
    private View view;
    private TextView ll_wechat;
    private TextView ll_moment;
    private TextView ll_weibo;
    private TextView ll_message;
    private TextView ll_qq;
    private RelativeLayout tv_cancel;

    public SharePopupWindow(BaseActivity mActivity, BaseMediaObject web) {
        this.mActivity = mActivity;
        this.web = web;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.popup_share, null);
        tv_cancel =  view.findViewById(R.id.img_cancel);

        ll_weibo  = view.findViewById(R.id.tv_sina);
        ll_wechat = view.findViewById(R.id.tv_wechat);
        ll_moment = view.findViewById(R.id.tv_moment);
        ll_message = view.findViewById(R.id.tv_message);
        ll_qq = view.findViewById(R.id.tv_qq);

        ll_wechat.setOnClickListener(v -> share(SHARE_MEDIA.WEIXIN));
        ll_moment.setOnClickListener(v -> share(SHARE_MEDIA.WEIXIN_CIRCLE));
        ll_weibo.setOnClickListener(v -> share(SHARE_MEDIA.SINA));
        ll_qq.setOnClickListener(v -> share(SHARE_MEDIA.QZONE));
        ll_message.setOnClickListener(view -> mActivity.requestPermission(
                Constant.RC_SEND,
                new String[]{Manifest.permission.SEND_SMS},
                mActivity.getString(R.string.rationale_call_phone),
                new PermissionCallBackM() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionGrantedM(int requestCode, String... perms) {
                        LogUtils.e(mActivity, "TODO: SEND_SMS Granted", Toast.LENGTH_SHORT);
                        Intent textIntent = new Intent(Intent.ACTION_SEND);
                        textIntent.setType("text/plain");
                        textIntent.putExtra(Intent.EXTRA_TEXT, "这是一段分享的文字");
                        mActivity.startActivity(Intent.createChooser(textIntent, "分享"));
                    }

                    @Override
                    public void onPermissionDeniedM(int requestCode, String... perms) {
                        LogUtils.e(mActivity, "TODO: SEND_SMS Denied", Toast.LENGTH_SHORT);
                    }
                }));

        // 设置SelectPicPopupWindow的View
        this.setContentView(view);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        // this.setAnimationStyle(R.style.popup_delete);
        this.setAnimationStyle(R.style.AnimBottom);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        // 设置透明度，改变popupWindow上边视图
        setParams(0.5f);
        setListener();
    }

    // 分享
    private void share(SHARE_MEDIA platform) {
        if (web instanceof UMWeb){

            new ShareAction(mActivity)
                    .setPlatform(platform)
                    .setCallback(shareListener)
                    .withMedia((UMWeb)web)
                    .share();
        }

        if (web instanceof UMImage){

            new ShareAction(mActivity)
                    .setPlatform(platform)
                    .setCallback(shareListener)
                    .withMedia((UMImage)web)
                    .share();
        }
    }

    private UMShareListener shareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            setParams(1f);
            dismiss();
            Toast.makeText(mActivity, "分享成功", Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            setParams(1f);
            dismiss();
            Toast.makeText(mActivity, "分享失败" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            setParams(1f);
            dismiss();
            Toast.makeText(mActivity, "取消分享", Toast.LENGTH_LONG).show();

        }
    };

    // 监听
    private void setListener() {
        //  监听popupWindow消失
        this.setOnDismissListener(() -> {
            setParams(1f);
            dismiss();
        });
        tv_cancel.setOnClickListener(v -> {
            setParams(1f);
            dismiss();
        });
    }

    public void setParams(float v) {
        // 设置透明度，改变popupWindow上边视图
        WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
        params.alpha = v;
        mActivity.getWindow().setAttributes(params);
    }

}
