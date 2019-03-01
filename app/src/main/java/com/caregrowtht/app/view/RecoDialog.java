package com.caregrowtht.app.view;

import androidx.cardview.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.BaseActivity;
import com.umeng.socialize.media.BaseMediaObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 推荐App
 */
public class RecoDialog {

    @BindView(R.id.tv_wechat)
    TextView mtvWechat;
    @BindView(R.id.tv_moment)
    TextView mtvCircle;
    @BindView(R.id.tv_qq)
    TextView mtvQq;
    @BindView(R.id.tv_sina)
    TextView mtvSina;
    @BindView(R.id.img_cancel)
    RelativeLayout mimgCancel;

    @BindView(R.id.cv_bg)
    CardView mCvBg;

    private SharePopupWindow mPopupWindow;
    private View mRootView;
    BaseActivity mContext;

    public RecoDialog(BaseActivity context, BaseMediaObject pObject, View.OnClickListener clickListener) {
//        view = LayoutInflater.from(context).inflate(R.layout.reco_app, null);
        mContext = context;
        mRootView = LayoutInflater.from(context).inflate(R.layout.popup_share, null);
        ButterKnife.bind(this, mRootView);
        mPopupWindow = new SharePopupWindow(context, pObject);
        mPopupWindow.setClippingEnabled(false);
        setOnclickListener(clickListener);
        mimgCancel.setOnClickListener(view -> RecoDialog.this.dismiss());
    }

    /**
     * view设置监听
     *
     * @param listener
     */
    private void setOnclickListener(View.OnClickListener listener) {
        mimgCancel.setOnClickListener(listener);
        mtvCircle.setOnClickListener(listener);
        mtvQq.setOnClickListener(listener);
        mtvSina.setOnClickListener(listener);
        mtvWechat.setOnClickListener(listener);
    }

    /**
     * 展示
     */
    public void show() {
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(mRootView, Gravity.NO_GRAVITY, 0, 0);

            Animation rotateAnim = AnimationUtils.loadAnimation(mContext, R.anim.window_out);
            LinearInterpolator lin = new LinearInterpolator();
            rotateAnim.setInterpolator(lin);
            mCvBg.startAnimation(rotateAnim);
        }
        mContext.CheckSharePermision();

    }

    /**
     * 隐藏
     */
    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }
}
