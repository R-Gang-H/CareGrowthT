package com.caregrowtht.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class WorkClassAdapter extends XrecyclerAdapter {

    //选中课程的信息
    private List<CourseEntity> mCourseModels = new ArrayList<>();
    //选中课程的消课次数
    private List<CourseEntity> mCount = new ArrayList<>();
    private String cardType;

    @BindView(R.id.tv_card_name)
    TextView tvCardName;

    public WorkClassAdapter(List datas, List mCount, Context context) {
        super(datas, context);
        this.mCourseModels.addAll(datas);
        this.mCount.addAll(mCount);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        StringBuilder cardName = new StringBuilder();
        if (mCourseModels != null && mCourseModels.size() > 0) {
            cardType = mCourseModels.get(position).getCardType();
        }
        String unit = "次";
        if (!TextUtils.equals(cardType, "1")) {
            unit = "元";
        }
        if (mCourseModels != null && mCourseModels.size() > 0) {
            cardName.append(String.format("%s\t\t",
                    mCourseModels.get(position).getCourseName()));
            if (!TextUtils.equals(cardType, "3")) {//不是年卡
                cardName.append(String.format("-%s%s",
                        mCount.get(position).getCourseCount(), unit));
            }
        }
        tvCardName.setText(cardName);
    }

    public void setData(List<CourseEntity> mCourseModels, List<CourseEntity> mCount, String cardType) {
        this.mCourseModels.clear();
        this.mCourseModels.addAll(mCourseModels);
        this.cardType = cardType;
        this.mCount.clear();
        this.mCount.addAll(mCount);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mCourseModels != null && mCourseModels.size() > 0) {
            return mCourseModels.size();
        }
        return 0;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.recycle_work_class_item;
    }

}
