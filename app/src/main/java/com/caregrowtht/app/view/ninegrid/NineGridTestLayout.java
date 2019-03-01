package com.caregrowtht.app.view.ninegrid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.android.library.MyApplication;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.SpaceImageDetailActivity;
import com.caregrowtht.app.uitil.GlideUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

/**
 * 描述：
 * 作者：haoruigang
 * 时间：2018年7月11日11:10:36
 */
public class NineGridTestLayout extends NineGridLayout {

    protected static final int MAX_W_H_RATIO = 3;

    public NineGridTestLayout(Context context) {
        super(context);
    }

    public NineGridTestLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean displayOneImage(final RatioImageView imageView, final String url, final int parentWidth) {

        //---处理视频转图片预览
        String pUrl = previewHandle(url);
        //---处理视频转图片预览

        GlideUtils.getBitmap(mContext, pUrl, R.mipmap.ic_media_default, new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(mContext.getResources(), bitmap);
                circularBitmapDrawable.setCornerRadius(20); //设置圆角弧度
                imageView.setImageDrawable(circularBitmapDrawable);

                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                int newW;
                int newH;
                if (h > w * MAX_W_H_RATIO) {//h:w = 5:3
                    newW = parentWidth / 2;
                    newH = newW * 5 / 3;
                } else if (h < w) {//h:w = 2:3
                    newW = parentWidth * 2 / 3;
                    newH = newW * 2 / 3;
                } else {//newH:h = newW :w
                    newW = parentWidth / 2;
                    newH = h * newW / w;
                }
                setOneImageLayoutParams(imageView, newW, newH);
            }
        });
        return false;
    }

    /**
     * 处理视频转图片预览
     *
     * @param url
     * @return
     */
    private String previewHandle(final String url) {
        String pUrl;
        //是图片
        if (url.contains("mp4")) {
//            pUrl = url.replace("mp4", "jpg");
            pUrl = url + "?x-oss-process=video/snapshot,t_10000";// 获取视频第一帧
        } else {
            pUrl = url + "?x-oss-process=image/resize,w_200";// 显示缩略图
        }
        return pUrl;
    }

    @Override
    protected void displayImage(final RatioImageView imageView, String url) {
        //---处理视频转图片预览
        String pUrl = previewHandle(url);
        //---处理视频转图片预览
        GlideUtils.getBitmap(mContext, pUrl, R.mipmap.ic_media_default, imageView);
    }

    @Override
    protected void onClickImage(int i, String url, List<String> urlList) {
//        Toast.makeText(mContext, "点击了图片" + url, Toast.LENGTH_SHORT).show();
        ArrayList<String> arrImageList = new ArrayList<>(Arrays.asList(arrUrls));
        Intent intent = new Intent(MyApplication.getAppContext(), SpaceImageDetailActivity.class);
        intent.putExtra("images", arrImageList);//非必须
        intent.putExtra("position", i);
        mContext.startActivity(intent);
    }
}
