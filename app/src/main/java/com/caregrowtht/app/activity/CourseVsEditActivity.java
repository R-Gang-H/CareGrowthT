package com.caregrowtht.app.activity;

import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.library.utils.DensityUtil;
import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 编辑课时卡下的排课
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CourseVsEditActivity extends BaseActivity {

    @BindView(R.id.ll_item_course_vs)
    LinearLayout llItemCourseVs;
    @BindView(R.id.iv_course_icon)
    CardView ivCourseIcon;
    @BindView(R.id.tv_course_type)
    TextView tvCourseType;
    @BindView(R.id.et_number)
    EditText etNumber;
    @BindView(R.id.tv_unit)
    TextView tvUnit;
    @BindView(R.id.tv_select_course)
    CheckBox tvSelectCourse;

    private CourseEntity courseCardEntity;
    private CourseEntity courseEntity;

    String type = "1";//操作的类型 1：添加或编辑 2：取消关联

    @Override
    public int getLayoutId() {
        return R.layout.activity_course_vs_edit;
    }

    @Override
    public void initView() {
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        courseCardEntity = (CourseEntity) getIntent().getSerializableExtra("courseCardEntity");
        courseEntity = (CourseEntity) getIntent().getSerializableExtra("courseEntity");
        String number = getIntent().getStringExtra("etNumber");
        boolean isSelectCourse = getIntent().getBooleanExtra("tvSelectCourse", false);

        // 卡的类型 1：次卡 2：储值卡 3：年卡 4：折扣卡
        if (TextUtils.equals(courseCardEntity.getCardType(), "1")) {
            tvUnit.setText("次");
            setVisibilityView(true, true, false);
        } else if (TextUtils.equals(courseCardEntity.getCardType(), "3")) {
            setVisibilityView(false, false, true);
            tvSelectCourse.setSelected(isSelectCourse);
            if (!isSelectCourse) {
                type = "2";// 取消关联
            } else {
                type = "1";// 关联
            }
        } else {
            tvUnit.setText("元");
            setVisibilityView(true, true, false);
        }
        ivCourseIcon.setCardBackgroundColor(TextUtils.isEmpty(courseEntity.getColor())
                ? ResourcesUtils.getColor(R.color.blue)
                : Color.parseColor(courseEntity.getColor()));
        tvCourseType.setText(courseEntity.getCourseName());
        if (!TextUtils.isEmpty(number)) {
//            if (TextUtils.equals(courseCardEntity.getCardType(), "1")) {
//                etNumber.setText(String.format("%s",
//                        TextUtils.equals(number, "0") ?
//                                "" : number));
//            } else {
//                etNumber.setText(String.format("%s",
//                        String.valueOf(TextUtils.equals(number, "0") ?
//                                "" : Integer.parseInt(number))));
//            }
            etNumber.setText(number);
        }

        tvSelectCourse.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                type = "2";// 取消关联
            } else {
                type = "1";// 关联
            }
            tvSelectCourse.setSelected(isChecked);
        });
    }

    @Override
    public void initData() {
        View view = UserManager.getInstance().getCourVsview();
        // 固定到相对位置
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(llItemCourseVs.getLayoutParams());
        marginParams.leftMargin = left;
        marginParams.topMargin = top - DensityUtil.getStatusBarHeight(this);//减去状态栏的高度
        marginParams.width = view.getWidth();
        marginParams.height = view.getHeight();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(marginParams);
        llItemCourseVs.setLayoutParams(params);
    }

    @OnClick(R.id.ll_course_vs)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_course_vs:
                if (!UserManager.getInstance().isTrueRole("ksk_1")) {
                    UserManager.getInstance().showSuccessDialog(this
                            , getString(R.string.text_role));
                } else {
                    String number = etNumber.getText().toString().trim();
                    if (TextUtils.equals(courseCardEntity.getCardType(), "3")) {
                        editCardLesson(number);
                    } else {
                        if (TextUtils.isEmpty(number)) {
                            type = "2";// 2：取消关联
                        }
                        // 如果填入数据与缓存数据相同返回
//                        if (!TextUtils.isEmpty(number)) {// && Double.parseDouble(number) > 0
                        // 编辑结束之后
                        editCardLesson(number);
//                        }
                    }
                }
                supportFinishAfterTransition();
                break;
        }
    }

    private void editCardLesson(String number) {
        String orgCardId = courseCardEntity.getOrgCardId();
        String orgId = UserManager.getInstance().getOrgId();
        String courseId = courseEntity.getCourseId();
        String courseName = courseEntity.getCourseName();
        String color = courseEntity.getColor();
        String count = "";
        String price = "";
        // 卡的类型 1：次卡 2：储值卡 3：年卡 4：折扣卡
        if (TextUtils.equals(courseCardEntity.getCardType(), "1")) {
            count = number;
        } else {
            if (!TextUtils.isEmpty(number)) {
                price = String.valueOf(Integer.parseInt(number) * 100);
            }
        }
        String finalCount = count;
        String finalPrice = price;
        String cardType = courseCardEntity.getCardType();
        HttpManager.getInstance().doEditCardLesson("CourserVsAdapter",
                orgCardId, orgId, courseId, count, price, type, cardType,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        CourseEntity courseEntity = new CourseEntity();
                        courseEntity.setCourseId(courseId);
                        courseEntity.setCourseName(courseName);
                        courseEntity.setColor(color);
                        courseEntity.setSingleTimes(finalCount);
                        courseEntity.setSingleMoney(finalPrice);
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_COURSE_WORK, courseEntity, tvSelectCourse.isSelected()));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CourserVsAdapter onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CourseVsEditActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CourserVsAdapter onError", throwable.getMessage());
                    }
                });
    }

    private void setVisibilityView(boolean isNubmer, boolean isUnit, boolean isSelect) {
        etNumber.setVisibility(isNubmer ? View.VISIBLE : View.GONE);
        tvUnit.setVisibility(isUnit ? View.VISIBLE : View.GONE);
        tvSelectCourse.setVisibility(isSelect ? View.VISIBLE : View.GONE);
    }

}
