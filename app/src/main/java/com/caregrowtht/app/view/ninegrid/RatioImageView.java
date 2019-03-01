package com.caregrowtht.app.view.ninegrid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.caregrowtht.app.R;


/**
 * 根据宽高比例自动计算高度ImageView
 * Created by haoruigang on 2018-7-11 11:14:42.
 */
@SuppressLint("AppCompatCustomView")
public class RatioImageView extends ImageView {

    /**
     * 宽高比例
     */
    private float mRatio = 0f;

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView);

        mRatio = typedArray.getFloat(R.styleable.RatioImageView_ratio, 0f);
        typedArray.recycle();
    }

    public RatioImageView(Context context) {
        super(context);
    }

    /**
     * 设置ImageView的宽高比
     *
     * @param ratio
     */
    public void setRatio(float ratio) {
        mRatio = ratio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (mRatio != 0) {
            float height = width / mRatio;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Drawable drawable = getDrawable();
                if (drawable != null) {
                    drawable.mutate().setColorFilter(Color.GRAY,
                            PorterDuff.Mode.MULTIPLY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Drawable drawableUp = getDrawable();
                if (drawableUp != null) {
                    drawableUp.mutate().clearColorFilter();
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    private Paint paint;
    private Bitmap bitmap;
    private boolean isCenterImgShow;
    private Rect mSrcRect, mDestRect;
    private int mBitWidth, mBitHeight;

    public void setCenterImgShow(boolean centerImgShow) {
        isCenterImgShow = centerImgShow;
        if (isCenterImgShow) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_play_small);
            // invalidate()方法被调用后，onDraw()方法会重新被调用并重新绘制，这样就可以愉快地在ImageView的上面新加一个图层。
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitWidth = w;
        mBitHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isCenterImgShow && bitmap != null) {
            mSrcRect = new Rect(0, 0, mBitWidth, mBitHeight);
            mDestRect = new Rect(mBitWidth / 2 - bitmap.getWidth() / 2, mBitHeight / 2 - bitmap.getHeight() / 2,
                    mBitWidth / 2 + bitmap.getWidth() / 2, mBitHeight / 2 + bitmap.getHeight() / 2);
            //第一个Rect 代表要绘制的bitmap 区域，第二个 Rect 代表的是要将bitmap 绘制在屏幕的什么地方
            canvas.drawBitmap(bitmap, mSrcRect, mDestRect, paint);
        }
    }

}
