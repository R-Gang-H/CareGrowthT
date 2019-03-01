package com.caregrowtht.app.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.wx.wheelview.adapter.BaseWheelAdapter;
import com.wx.wheelview.util.WheelUtils;
import com.wx.wheelview.widget.WheelView;

import java.util.List;


/**
 * Created by haoruigang on 2018-8-22 14:29:51.
 * 自定义颜色滚轮选择器
 */
public class ColorWheelPopup extends PopupWindow {

    private WheelView simpleWheelView;
    private ImageView m_tvCancelBtn;
    private View mMenuView;
    private Activity activity;
    private IOnSelectLister mSelectListener;

    public void setSelectListener(IOnSelectLister mSelectListener) {
        this.mSelectListener = mSelectListener;
    }

    @SuppressLint("ClickableViewAccessibility")
    public ColorWheelPopup(final Context context, final List<CourseEntity> courseTypeList) {
        super(context);

        if (context instanceof Activity) {
            activity = (Activity) context;
        }
        mMenuView = View.inflate(activity, R.layout.popup_custom_wheel, null);
        m_tvCancelBtn = mMenuView.findViewById(R.id.tv_cancel_btn);
        simpleWheelView = mMenuView.findViewById(R.id.simple_wheelview);
        simpleWheelView.setWheelAdapter(new SimpleWheelAdapter(activity));
        simpleWheelView.setWheelSize(5);
        if (courseTypeList.size() > 0) {
            simpleWheelView.setWheelData(courseTypeList);
        }
        simpleWheelView.setSkin(WheelView.Skin.Holo);
        simpleWheelView.setLoop(false);
        simpleWheelView.setWheelClickable(true);
        simpleWheelView.setOnWheelItemClickListener((position, o) -> {
            WheelUtils.log("click:" + position);
            //销毁弹出框
            //设置透明度，改变popurwindow上边视图
            setParams(1f);
            dismiss();

            CourseEntity value = courseTypeList.get(position);
            mSelectListener.getSelect(value, position);
        });
        simpleWheelView.setOnWheelItemSelectedListener((WheelView.OnWheelItemSelectedListener<CourseEntity>) (position, data) -> WheelUtils.log("selected:" + position));

        //取消按钮
        m_tvCancelBtn.setOnClickListener(v -> {
            //销毁弹出框
            //设置透明度，改变popurwindow上边视图
            setParams(1f);
            dismiss();
        });

        //设置按钮监听
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        // this.setAnimationStyle(R.style.popup_delete);
        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        //设置透明度，改变popurwindow上边视图
        setParams(0.5f);

        //  监听popupWindow消失
        this.setOnDismissListener(() -> {
            dismiss();
            setParams(1f);
        });

        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener((v, event) -> {

            int height = mMenuView.findViewById(R.id.pop_layout).getTop();
            int y = (int) event.getY();
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (y < height) {
                    //设置透明度，改变popurwindow上边视图
                    setParams(1f);
                    dismiss();
                }
            }
            return true;
        });
    }

    class SimpleWheelAdapter extends BaseWheelAdapter<CourseEntity> {

        private Context mContext;

        private SimpleWheelAdapter(Context context) {
            mContext = context;
        }

        @Override
        public View bindView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.item_color_course, null);
            }
            CourseEntity courseEntity = mList.get(position);
            CardView cardView = convertView.findViewById(R.id.iv_course_icon);
            cardView.setCardBackgroundColor(TextUtils.isEmpty(courseEntity.getColor()) ?
                    ResourcesUtils.getColor(R.color.blue) :
                    Color.parseColor(courseEntity.getColor()));
            TextView tvCourseType = convertView.findViewById(R.id.tv_courseType);
            tvCourseType.setText(courseEntity.getClassifyName());
            return convertView;
        }
    }


    public void setParams(float v) {
        //设置透明度，改变popurwindow上边视图
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.alpha = v;
        activity.getWindow().setAttributes(params);
    }

    public interface IOnSelectLister {
        void getSelect(CourseEntity argValue, int position);
    }
}
