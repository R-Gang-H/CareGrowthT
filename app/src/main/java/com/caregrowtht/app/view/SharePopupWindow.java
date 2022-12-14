package com.caregrowtht.app.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
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
import com.caregrowtht.app.uitil.FilePickerUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.permissions.PermissionCallBackM;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.BaseMediaObject;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.io.File;


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

    private final File file;

    public SharePopupWindow(BaseActivity mActivity, BaseMediaObject web, File file) {
        this.mActivity = mActivity;
        this.web = web;
        this.file = file;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.popup_share, null);
        tv_cancel = view.findViewById(R.id.img_cancel);

        ll_weibo = view.findViewById(R.id.tv_sina);
        ll_wechat = view.findViewById(R.id.tv_wechat);
        ll_moment = view.findViewById(R.id.tv_moment);
        ll_message = view.findViewById(R.id.tv_message);
        ll_qq = view.findViewById(R.id.tv_qq);

        ll_wechat.setOnClickListener(v -> share(SHARE_MEDIA.WEIXIN));
        ll_moment.setOnClickListener(v -> share(SHARE_MEDIA.WEIXIN_CIRCLE));
        ll_weibo.setOnClickListener(v -> share(SHARE_MEDIA.SINA));
        ll_qq.setOnClickListener(v -> share(SHARE_MEDIA.QZONE));
        ll_message.setOnClickListener(view -> ShareMessage());

        // ??????SelectPicPopupWindow???View
        this.setContentView(view);
        // ??????SelectPicPopupWindow??????????????????
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        // ??????SelectPicPopupWindow??????????????????
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // ??????SelectPicPopupWindow?????????????????????
        this.setFocusable(true);
        // ??????SelectPicPopupWindow????????????????????????
        // this.setAnimationStyle(R.style.popup_delete);
        this.setAnimationStyle(R.style.AnimBottom);
        // ???????????????ColorDrawable??????????????????
        ColorDrawable dw = new ColorDrawable(0x000000);
        // ??????SelectPicPopupWindow?????????????????????
        this.setBackgroundDrawable(dw);

        setListener();
    }

    public void ShareMessage() {
        mActivity.requestPermission(
                Constant.RC_SEND,
                new String[]{Manifest.permission.SEND_SMS
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE},
                mActivity.getString(R.string.rationale_call_phone),
                new PermissionCallBackM() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionGrantedM(int requestCode, String... perms) {
                        LogUtils.e(mActivity, "TODO: SEND_SMS Granted" + file.getPath(), Toast.LENGTH_SHORT);
//                        //???????????????uri
                        Uri uri = null;
                        Intent cameraIntent = new Intent(Intent.ACTION_SEND);// , Uri.parse("mailto:")
                        if (Build.VERSION.SDK_INT < 24) {
                            uri = Uri.parse(file.getAbsolutePath());//Uri.fromFile(file)
                        } else {
                            //????????????7.0
                            ContentValues contentValues = new ContentValues(1);
                            contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                            uri = mActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                            if (uri == null) {
                                uri = FilePickerUtils.getInstance().getImageContentUri(mActivity, file);
                                if (uri == null) {
                                    uri = Uri.fromFile(file);
                                }
                            }
                            if (uri != null) {
                                mActivity.grantUriPermission(mActivity.getPackageName(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            }
                        }
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        cameraIntent.putExtra("body", "??????");
                        cameraIntent.putExtra(Intent.EXTRA_STREAM, uri);// ???????????????
                        cameraIntent.setType("image/png");// ??????????????????????????? image/*
                        mActivity.startActivity(Intent.createChooser(cameraIntent, "??????"));// ????????????????????????????????????
                    }

                    @Override
                    public void onPermissionDeniedM(int requestCode, String... perms) {
                        LogUtils.e(mActivity, "TODO: SEND_SMS Denied", Toast.LENGTH_SHORT);
                    }
                });
    }

    // ??????
    public void share(SHARE_MEDIA platform) {
        if (web instanceof UMWeb) {

            new ShareAction(mActivity)
                    .setPlatform(platform)
                    .setCallback(shareListener)
                    .withMedia((UMWeb) web)
                    .share();
        } else if (web instanceof UMImage) {

            new ShareAction(mActivity)
                    .setPlatform(platform)
                    .setCallback(shareListener)
                    .withMedia((UMImage) web)
                    .share();
        } else {
            File f = new File(file.getPath());
            new ShareAction(mActivity)
                    .withFile(f)
                    .withText(f.getPath().substring(f.getAbsolutePath().lastIndexOf("/") + 1))
                    .withSubject(f.getPath().substring(f.getAbsolutePath().lastIndexOf("/") + 1))
                    .setPlatform(platform)
                    .setCallback(shareListener)
                    .share();
        }
    }


    private UMShareListener shareListener = new UMShareListener() {
        /**
         * @descrption ?????????????????????
         * @param platform ????????????
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        /**
         * @descrption ?????????????????????
         * @param platform ????????????
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            setParams(1f);
            dismiss();
            Toast.makeText(mActivity, "????????????", Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption ?????????????????????
         * @param platform ????????????
         * @param t ????????????
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            setParams(1f);
            dismiss();
            Toast.makeText(mActivity, "????????????" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption ?????????????????????
         * @param platform ????????????
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            setParams(1f);
            dismiss();
            Toast.makeText(mActivity, "????????????", Toast.LENGTH_LONG).show();

        }
    };

    // ??????
    private void setListener() {
        //  ??????popupWindow??????
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
        // ????????????????????????popupWindow????????????
        WindowManager.LayoutParams params = mActivity.getWindow().getAttributes();
        params.alpha = v;
        mActivity.getWindow().setAttributes(params);
    }

}
