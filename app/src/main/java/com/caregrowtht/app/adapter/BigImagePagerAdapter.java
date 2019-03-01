package com.caregrowtht.app.adapter;


import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.library.utils.U;
import com.bm.library.PhotoView;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.VideoActivity;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.ninegrid.NineGridLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zhoujie on 2018/3/29.
 * Update to haorugiang on 2019-1-9 11:42:34
 */

public class BigImagePagerAdapter extends PagerAdapter {

    ImageView mPlayBtn, mDelSign;
    private PhotoView mPhotoView;

    Activity mContext;

    private ArrayList<String> arrUrlList;
    private final String courseID;// 不为空,显示删除图标

    public BigImagePagerAdapter(List datas, String courseId, Activity context) {
        arrUrlList = new ArrayList<>();
        arrUrlList.addAll(datas);
        courseID = courseId;
        mContext = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_big_image, container, false);
        mPlayBtn = view.findViewById(R.id.iv_play_btn);
        mPhotoView = view.findViewById(R.id.img);
        mDelSign = view.findViewById(R.id.iv_del_sign);

        final String strUrl = arrUrlList.get(position);
        if (NineGridLayout.CheckIsImage(strUrl)) {
            mPlayBtn.setVisibility(View.GONE);

            // 启用图片缩放功能
            mPhotoView.enable();

            mPhotoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            mPhotoView.setOnClickListener(v -> {
                mContext.finish();
                mContext.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            });
            mPhotoView.setOnLongClickListener(v -> {
                UserManager.getInstance().showSaveDialog(mContext, strUrl);
                return false;
            });
        } else {
            mPlayBtn.setVisibility(View.VISIBLE);

            // 禁用图片缩放功能
            mPhotoView.disenable();

            mPhotoView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            mPhotoView.setOnClickListener(v -> {
                Intent pIntent = new Intent(mContext, VideoActivity.class);
                pIntent.putExtra("videoUrl", strUrl);
                mContext.startActivity(pIntent);
                mContext.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                mContext.finish();
            });
        }

        if (!TextUtils.isEmpty(courseID)) {// 显示垃圾桶
            mDelSign.setVisibility(View.VISIBLE);
            mDelSign.setOnClickListener(v -> delLesSignSheet());// 删除已上传的签到表
        }

        GlideUtils.setGlideImg(mContext, strUrl, R.mipmap.ic_media_default, mPhotoView);
        container.addView(view);

        return view;
    }

    private void delLesSignSheet() {
        HttpManager.getInstance().doDelLesSignSheet("BigImagePagerAdapter", courseID,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_SIGN_TABLE));
                        mContext.finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("BigImagePagerAdapter onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(mContext);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("BigImagePagerAdapter throwable", throwable.getMessage());
                    }
                });
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return arrUrlList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

}