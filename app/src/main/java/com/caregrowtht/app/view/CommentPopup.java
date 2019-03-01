package com.caregrowtht.app.view;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.zyyoona7.popup.EasyPopup;
import com.zyyoona7.popup.XGravity;
import com.zyyoona7.popup.YGravity;


/**
 * Created by haoruigang on 2018-7-24 16:13:56
 * 微信朋友圈评论弹窗
 */
public class CommentPopup {

    private Activity mActivity;

    private static final String TAG = "";
    private EasyPopup mCirclePop;

    private OnCommentPopupClickListener mOnCommentPopupClickListener;
    private OnPopupClickListener mOnPopupClickListener;

    public CommentPopup(Activity context) {
        this.mActivity = context;
        initCirclePop();
    }

    //=============================================================Getter/Setter
    public void setOnCommentPopupClickListener(OnCommentPopupClickListener onCommentPopupClickListener) {
        this.mOnCommentPopupClickListener = onCommentPopupClickListener;
    }

    public OnPopupClickListener getmOnPopupClickListener() {
        return mOnPopupClickListener;
    }

    public void setOnPopupClickListener(OnPopupClickListener mOnPopupClickListener) {
        this.mOnPopupClickListener = mOnPopupClickListener;
    }

    public void showPopupWindow(View view) {
        mCirclePop.showAtAnchorView(view, YGravity.CENTER, XGravity.LEFT, 0, 0);
    }

    public interface OnCommentPopupClickListener {

        void onLikeClick(View v, TextView likeText);

        void onCommentClick(View v);
    }

    public interface OnPopupClickListener {

        void onLikeClick(String like);

    }
    //=============================================================abortMethods


    //=======================EasyPopup==================
    private void initCirclePop() {
        mCirclePop = EasyPopup.create()
                .setContentView(mActivity, R.layout.popup_comment)
                .setAnimationStyle(R.style.fade)
                .setFocusAndOutsideEnable(true)
                .setOnViewListener((view, popup) -> {
                    final TextView mLikeText = view.findViewById(R.id.item_like);
                    setOnPopupClickListener(like -> {
                        mLikeText.setText(like);
                    });
                    mLikeText.setOnClickListener(v -> {
                        if (mOnCommentPopupClickListener != null) {
                            mOnCommentPopupClickListener.onLikeClick(v, mLikeText);
                        }
                        popup.dismiss();
                    });
                    view.findViewById(R.id.item_comment).setOnClickListener(v -> {
                        if (mOnCommentPopupClickListener != null) {
                            mOnCommentPopupClickListener.onCommentClick(v);
                            popup.dismiss();
                        }
                    });
                })
                .apply();

        mCirclePop.setOnDismissListener(() -> Log.e(TAG, "onDismiss: mCirclePop"));
    }
}
