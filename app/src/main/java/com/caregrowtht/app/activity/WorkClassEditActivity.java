package com.caregrowtht.app.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.DensityUtil;
import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class WorkClassEditActivity extends BaseActivity {

    @BindView(R.id.iv_course_icon)
    CardView ivCourseIcon;
    @BindView(R.id.tv_course_type)
    TextView tvCourseType;
    @BindView(R.id.tv_select_course)
    CheckBox tvSelectCourse;
    @BindView(R.id.et_cancel_count)
    EditText etCancelCount;
    @BindView(R.id.tv_couont)
    TextView tvCouont;
    @BindView(R.id.rl_select)
    RelativeLayout rlSelect;
    @BindView(R.id.ll_item_course_vs)
    LinearLayout llItemCourseVs;
    @BindView(R.id.ll_course_vs)
    LinearLayout llCourseVs;

    @Override
    public int getLayoutId() {
        return R.layout.activity_work_class_edit;
    }

    @Override
    public void initView() {
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        ivCourseIcon.setVisibility(View.GONE);
        tvCourseType.setVisibility(View.GONE);

        String cardType = getIntent().getStringExtra("cardType");
        if (TextUtils.equals(cardType, "1")) {// 次数
            tvCouont.setText("次");
        } else {
            tvCouont.setText("元");
        }
        boolean isSelected = getIntent().getBooleanExtra("Selected", false);
        isCheckShow(isSelected);
        tvSelectCourse.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isCheckShow(isChecked);
            etCancelCount.setText("");
        });
    }

    private void isCheckShow(boolean isChecked) {
        tvSelectCourse.setSelected(isChecked);
        rlSelect.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }

    @Override
    public void initData() {
        View view = UserManager.getInstance().getCourVsview();
        TextView tvCourseType = view.findViewById(R.id.tv_course_type);
        // 固定到相对位置
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(llItemCourseVs.getLayoutParams());
        marginParams.leftMargin = left;
        marginParams.topMargin = top - DensityUtil.getStatusBarHeight(this);//减去状态栏的高度
        marginParams.width = view.getWidth();
        marginParams.height = tvCourseType.getHeight();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(marginParams);
        llItemCourseVs.setLayoutParams(params);
    }

    @OnClick(R.id.ll_course_vs)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_course_vs:
                EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_WORK_COUNT,
                        tvSelectCourse.isSelected(), etCancelCount.getText().toString()));
                supportFinishAfterTransition();
                break;
        }
    }

}
