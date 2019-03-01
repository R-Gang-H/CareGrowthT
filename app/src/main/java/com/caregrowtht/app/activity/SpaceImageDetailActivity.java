package com.caregrowtht.app.activity;

import android.graphics.Color;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.View;

import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.BigImagePagerAdapter;
import com.viewpagerindicator.CirclePageIndicator;
import com.yanzhenjie.sofia.Sofia;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * update haoruigang on 2018-07-14 13:40:32
 * 图片，视频 预览
 */

public class SpaceImageDetailActivity extends BaseActivity {


    @BindView(R.id.vp_big_image)
    ViewPager vpBigImage;
    @BindView(R.id.page_control)
    CirclePageIndicator mPageControl;

    ArrayList<String> mDatas;
    int mPosition;
    int mCurX = 0;

    private BigImagePagerAdapter mAdapter;


    @Override
    public int getLayoutId() {
        Sofia.with(this).statusBarBackground(Color.TRANSPARENT).statusBarLightFont();
        return R.layout.activity_space_image_detail;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void initView() {

        mDatas = (ArrayList<String>) getIntent().getSerializableExtra("images");
        mPosition = getIntent().getIntExtra("position", 0);
        String courseId = getIntent().getStringExtra("courseId");// 不为空,显示删除图标

        for (int i = 0; i < mDatas.size(); i++) {
            Log.e("------------", "mDatas=" + mDatas.get(i));
        }
        mAdapter = new BigImagePagerAdapter(mDatas, courseId, this);
        vpBigImage.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        vpBigImage.setAdapter(mAdapter);
        mPageControl.setViewPager(vpBigImage);

        mPageControl.setVisibility(mDatas.size() > 1 ? View.VISIBLE : View.GONE);
        Log.e("------------", "mDatas=" + mPosition);
        vpBigImage.setCurrentItem(mPosition);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
