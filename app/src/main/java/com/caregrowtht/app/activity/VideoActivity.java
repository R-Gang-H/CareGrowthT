package com.caregrowtht.app.activity;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;

import androidx.core.view.ViewCompat;

import android.transition.Transition;
import android.view.View;
import android.widget.ImageView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.view.videoPlayer.listener.OnTransitionListener;
import com.caregrowtht.app.view.videoPlayer.view.SampleVideo;
import com.shuyu.gsyvideoplayer.GSYVideoManager;

import butterknife.BindView;


public class VideoActivity extends BaseActivity {

    public final static String IMG_TRANSITION = "IMG_TRANSITION";
    public final static String TRANSITION = "TRANSITION";

    @BindView(R.id.videoplayer)
    SampleVideo mPlayerView;

//    OrientationUtils orientationUtils;

    private boolean isTransition;
    private Transition transition;

    String mVideoUrl;

    @Override
    public int getLayoutId() {
        return R.layout.activity_video;
    }

    @Override
    public void initView() {

        isTransition = getIntent().getBooleanExtra(TRANSITION, false);
        mVideoUrl = (String) getIntent().getSerializableExtra("videoUrl");
        String mPreviewImageUrl = mVideoUrl + "?x-oss-process=video/snapshot,t_10000";// 获取视频第一帧;

        mPlayerView.setUp("" + mVideoUrl, true, "");

        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        GlideUtils.setGlideImg(this, mPreviewImageUrl, R.mipmap.ic_media_default, imageView);
        mPlayerView.setThumbImageView(imageView);
        //增加title
        mPlayerView.getTitleTextView().setVisibility(View.VISIBLE);
        //设置返回键
        mPlayerView.getBackButton().setVisibility(View.VISIBLE);
        //设置旋转
//        orientationUtils = new OrientationUtils(this, mPlayerView);
//        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
//        mPlayerView.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                orientationUtils.resolveByClick();
//            }
//        });
        //是否可以滑动调整
        mPlayerView.setIsTouchWiget(true);
        //设置返回按键功能
        mPlayerView.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //过渡动画
        initTransition();
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayerView.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayerView.onVideoResume();
    }

    @Override
    public void onBackPressed() {
        //先返回正常状态
//        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            mPlayerView.getFullscreenButton().performClick();
//            return;
//        }
        //释放所有
        mPlayerView.setVideoAllCallBack(null);
        GSYVideoManager.releaseAllVideos();

        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.onBackPressed();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                }
            }, 500);
        }
    }

    private void initTransition() {
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
            ViewCompat.setTransitionName(mPlayerView, IMG_TRANSITION);
            addTransitionListener();
            startPostponedEnterTransition();
        } else {
            mPlayerView.startPlayLogic();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean addTransitionListener() {
        transition = getWindow().getSharedElementEnterTransition();
        if (transition != null) {
            transition.addListener(new OnTransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    super.onTransitionEnd(transition);
                    mPlayerView.startPlayLogic();
                    transition.removeListener(this);
                }
            });
            return true;
        }
        return false;
    }

}
