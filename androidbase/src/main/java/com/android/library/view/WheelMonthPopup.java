package com.android.library.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.library.R;
import com.android.library.utils.U;
import com.android.library.view.wheelView.OnWheelChangedListener;
import com.android.library.view.wheelView.WheelView;
import com.android.library.view.wheelView.adapter.NumericWheelAdapter;

import java.util.Calendar;


/**
 * Created by Administrator on 2016/1/22.
 */
public class WheelMonthPopup extends PopupWindow {

    public TextView m_tvCancelBtn, m_tvOkBtn;
    private View mMenuView;
    private Activity activity;
    private IOnSelectLister mSelectListener;
    private String  m_strDate;
    private WheelView month,year,day,hour;
    private int minYear;
    private int curItemYear;
    private int curItemMonth;
    private int curItemDay;
    private int curItemHour;


    private String mTag;

    public void setLess(boolean tag) {
        if (tag) {
            this.mTag = "Less";
        } else {
            this.mTag = "notLess";
        }
    }

    public void setSelectListener(IOnSelectLister mSelectListener) {
        this.mSelectListener = mSelectListener;
    }

    public WheelMonthPopup(final Context context, String argDate) {
        super(context);

        if (context instanceof Activity) {
            activity=(Activity) context;
        }
        this.m_strDate = argDate;


        initView();

        setListener();
    }

    private void initView() {


        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popup_wheel_time_select, null);
        m_tvCancelBtn = (TextView) mMenuView.findViewById(R.id.tv_cancel_btn);
        m_tvOkBtn = (TextView) mMenuView.findViewById(R.id.tv_ok_btn);
        TextView tvDay = (TextView) mMenuView.findViewById(R.id.tv_day);

        Calendar calendar = Calendar.getInstance();
        month = (WheelView) mMenuView.findViewById(R.id.month);
        year = (WheelView)mMenuView.findViewById(R.id.year);
        day = (WheelView) mMenuView.findViewById(R.id.day);
        hour = (WheelView) mMenuView.findViewById(R.id.time);
        day.setVisibility(View.GONE);
        hour.setVisibility(View.GONE);
        tvDay.setVisibility(View.GONE);


        int curYear = calendar.get(Calendar.YEAR);
        int maxYear = curYear + 2;
        minYear = curYear-2;

        if (m_strDate != null) {
            String[] arr = m_strDate.split("-");
            if (arr.length == 2) {
                curItemYear = Integer.parseInt(arr[0]) - minYear;
                curItemMonth = Integer.parseInt(arr[1])-1;
            }
        }

        OnWheelChangedListener listener = new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue,
                                  int newValue) {
                updateDays(year, month);
            }
        };

        month.setViewAdapter(new DateNumericAdapter(activity, 1, 12));
        month.setCurrentItem(curItemMonth);
        month.addChangingListener(listener);

//        hour.setViewAdapter(new DateNumericAdapter(activity, 1, 24));
//        hour.setCurrentItem(curItemHour);
//        hour.addChangingListener(listener);

        // year
        year.setViewAdapter(new DateNumericAdapter(activity, minYear, maxYear));
        year.setCurrentItem(curItemYear);

        // day
        updateDays(year, month);
//        day.setCurrentItem(curItemDay);

        //??????SelectPicPopupWindow???View
        this.setContentView(mMenuView);
        //??????SelectPicPopupWindow??????????????????
        this.setWidth(LayoutParams.FILL_PARENT);
        //??????SelectPicPopupWindow??????????????????
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //??????SelectPicPopupWindow?????????????????????
        this.setFocusable(true);
        //??????SelectPicPopupWindow????????????????????????
        // this.setAnimationStyle(R.style.popup_delete);
        this.setAnimationStyle(R.style.AnimBottom);
        //???????????????ColorDrawable??????????????????
        ColorDrawable dw = new ColorDrawable(0x000000);
        //??????SelectPicPopupWindow?????????????????????
        this.setBackgroundDrawable(dw);

        //????????????????????????popurwindow????????????
        setParams(0.5f);

    }

    private void setListener() {
        //????????????
        m_tvCancelBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //???????????????
                //????????????????????????popurwindow????????????
                setParams(1f);
                dismiss();
            }
        });

        //????????????
        m_tvOkBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //???????????????
                //????????????????????????popurwindow????????????

                int y = year.getCurrentItem() + minYear;
                int m = month.getCurrentItem() + 1;
//                int d = day.getCurrentItem() + 1;
//                int h = hour.getCurrentItem() + 1 ;

                if (TextUtils.equals(mTag, "Less")) {
                    if (y > curItemYear + minYear) {
                        U.showToast("????????????????????????????????????");
                        return;
                    }
                    if (y == curItemYear + minYear && m > curItemMonth + 1) {
                        U.showToast("????????????????????????????????????");
                        return;
                    }
//                    if (y == curItemYear + minYear && m == curItemMonth + 1 && d > curItemDay + 1) {
//                        U.ShowToast("????????????????????????????????????");
//                        return;
//                    }
                } else if (TextUtils.equals(mTag, "notLess")) {
                    if (y <= curItemYear + minYear && m < curItemMonth + 1) {
                        U.showToast("????????????????????????????????????");
                        return;
                    }
//                    if (y <= curItemYear + minYear && m <= curItemMonth + 1 && d < curItemDay + 1) {
//                        U.ShowToast("????????????????????????????????????");
//                        return;
//                    }
                }


                setParams(1f);
                dismiss();

                String date = y + "-"
                        + (m < 10 ? "0" + m : m) ;
                String date2 =  y + ""
                        + (m < 10 ? "0" + m : m) ;

//                String date = y + "-"
//                        + (m < 10 ? "0" + m : m) + "-"
//                        + (d < 10 ? "0" + d : d) + " "
//                        + (h < 10 ? "0" + h + ":00" : h + ":00") ;
//                String date2 =  y + ""
//                        + (m < 10 ? "0" + m : m) + ""
//                        + (d < 10 ? "0" + d : d) + ""
//                        + (h < 10 ? "0" + h + "00" : h + "00")+"00" ;

                mSelectListener.getSelect(date,date2);
            }
        });

        //??????????????????

        //  ??????popupWindow??????
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                dismiss();
                setParams(1f);
            }
        });

        //mMenuView??????OnTouchListener????????????????????????????????????????????????????????????????????????
        mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        //????????????????????????popurwindow????????????
                        setParams(1f);
                        dismiss();

                    }
                }
                return true;
            }
        });

    }

    public void setParams(float v) {
        //????????????????????????popupWindow????????????
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.alpha = v;
        activity.getWindow().setAttributes(params);
    }

    /**
     * Updates day wheel. Sets max days according to selected month and year
     */
    void updateDays(WheelView year, WheelView month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,
                calendar.get(Calendar.YEAR) + year.getCurrentItem());
        calendar.set(Calendar.MONTH, month.getCurrentItem());

//        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//
//        day.setViewAdapter(new DateNumericAdapter(activity, 1, maxDays));
//        if (day.getCurrentItem() > maxDays - 1) {
//            day.setCurrentItem(maxDays - 1, true);
//        } else {
//            day.setCurrentItem(day.getCurrentItem());
//        }

    }

    /**
     * Adapter for numeric wheels. Highlights the current value.
     */
    private class DateNumericAdapter extends NumericWheelAdapter {

        // Index of current item
        int currentItem;

        /**
         * Constructor
         */
        public DateNumericAdapter(Context context, int minValue, int maxValue) {
            super(context, minValue, maxValue);
        }

        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == getCurrentItem()) {
                view.setTextColor(0xFF222222);
                //view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            } else {
                view.setTextColor(0xFF999999);
            }
            view.setTypeface(Typeface.DEFAULT);
            view.setTextSize(22);

        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }


    public interface IOnSelectLister {
        public void getSelect(String argValue, String argValue2);
    }

}
