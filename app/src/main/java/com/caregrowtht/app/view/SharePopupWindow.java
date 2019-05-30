package com.caregrowtht.app.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
//                        //由文件得到uri
                        Uri uri = null;
                        Intent cameraIntent = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));
                        if (cameraIntent.resolveActivity(mActivity.getPackageManager()) != null) {
                            if (Build.VERSION.SDK_INT < 24) {
                                uri = Uri.parse(file.getAbsolutePath());//Uri.fromFile(file)
                            } else {
                                //适配安卓7.0
                                ContentValues contentValues = new ContentValues(1);
                                contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                                uri = mActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                                if (uri == null) {
                                    uri = getImageContentUri(mActivity, file);
                                }
                                if (uri != null) {
                                    mActivity.grantUriPermission(mActivity.getPackageName(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                    cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                }
                            }
                        }
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        cameraIntent.putExtra("body", "内容");
                        cameraIntent.putExtra(Intent.EXTRA_STREAM, uri);// 分享的内容
                        cameraIntent.setType("image/png");// 分享发送的数据类型 image/*
                        mActivity.startActivity(Intent.createChooser(cameraIntent, "分享"));// 目标应用选择对话框的标题
                    }

                    @Override
                    public void onPermissionDeniedM(int requestCode, String... perms) {
                        LogUtils.e(mActivity, "TODO: SEND_SMS Denied", Toast.LENGTH_SHORT);
                    }
                });
    }

    // 分享
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

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param imageFile
     * @return content Uri
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

}
