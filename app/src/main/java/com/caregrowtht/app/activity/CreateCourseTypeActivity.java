package com.caregrowtht.app.activity;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.CourseColorAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-10-25 16:39:15
 * 新建 / 编辑课程分类
 */
public class CreateCourseTypeActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.rl_course_type_name)
    RelativeLayout rlCourseTypeName;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_course_type_name)
    EditText etCourseTypeName;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private String[] colorS = {"#A4D463", "#E69558", "#7DD4D4", "#EE8182", "#F08DB5", "#C9A1CE",
            "#EDC46B", "#61ABE8", "#CCA172", "#28BCBC"};//主颜色 机构端放大课表时侧边栏颜色
    private String[] viceColor = {"#C1E096", "#EDB589", "#A4E1E1", "#F3ADAD", "#F5B4CF", "#DCC1DF",
            "#F2D597", "#9AC8EF", "#DEC2A3", "#71D3D3"};//浅颜色 副颜色 机构端放大课表时背景颜色
    private String[] color3S = {"#DEEDCA", "#F5D5BB", "#CBEEEE", "#F9D9D9", "#FADCE9", "#EFE2F1",
            "#F7E7C3", "#D3E6F6", "#F0E3D5", "#BBEBEB"};//浅颜色
    private String[] color4S = {"#EEF6E4", "#FAEADD", "#E5F6F6", "#FCECEC", "#FCEDF4", "#F7F0F8",
            "#FBF3E1", "#E9F2FA", "#F7F1EA", "#DDF5F5"};//浅颜色

    ArrayList<CourseEntity> couTypeArrayList = new ArrayList<>();//课程类型集合
    private CourseColorAdapter courColAdapter;
    private String orgId;
    private String classifyId;
    private String color;
    private String tintColor;
    private String color3;
    private String color4;
    private CourseEntity courseEntity;


    @Override
    public int getLayoutId() {
        return R.layout.activity_create_course_type;
    }

    @Override
    public void initView() {
        ivLeft.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_close_1));
        initRecyclerGrid(recyclerView, 5);
        courColAdapter = new CourseColorAdapter(couTypeArrayList, this, this);
        recyclerView.setAdapter(courColAdapter);
    }

    @Override
    public void initData() {
        orgId = UserManager.getInstance().getOrgId();
        courseEntity = (CourseEntity) getIntent().getSerializableExtra("CourseEntity");
        if (courseEntity == null) {
            tvTitle.setText("新建课程分类");
        } else {
            tvTitle.setText("编辑课程分类");
            classifyId = courseEntity.getClassifyId();
            String classifyName = courseEntity.getClassifyName();
            etCourseTypeName.setText(classifyName);
            color = courseEntity.getColor();
            tintColor = courseEntity.getVice_color();
            color3 = courseEntity.getColor3();
            color4 = courseEntity.getColor4();
            if (TextUtils.equals("无分类课程", classifyName)) {
                rlCourseTypeName.setVisibility(View.GONE);
                etCourseTypeName.setFocusable(false);
            }
        }
        setData();//制造课程分类数据
        courColAdapter.setData(couTypeArrayList, courseEntity);
    }

    private void setData() {
        for (int i = 0; i < colorS.length; i++) {
            CourseEntity couType = new CourseEntity();
            couType.setColor(colorS[i]);
            couType.setVice_color(viceColor[i]);
            couType.setColor3(color3S[i]);
            couType.setColor4(color4S[i]);
            couTypeArrayList.add(couType);
        }
    }

    @OnClick({R.id.rl_back_button, R.id.btn_cancel, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
            case R.id.btn_cancel:
                finish();
                overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
                break;
            case R.id.btn_submit:
                editCourseType();
                break;
        }
    }

    private void editCourseType() {
        String classifyName = etCourseTypeName.getText().toString().trim();
        if (TextUtils.isEmpty(classifyName)) {
            U.showToast("课程分类名称");
            return;
        }
        HttpManager.getInstance().doEditCourseType("CreateCourseTypeActivity", orgId, classifyId,
                classifyName, color, tintColor, color3, color4, new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        U.showToast("成功");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_COURSE_TYPE));
                        finish();
                        overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CreateCourseTypeActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CreateCourseTypeActivity.this);
                        } else if (statusCode == 1055) {
                            U.showToast("分类已经存在.");
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });

    }

    @Override
    public void setOnItemClickListener(View view, int postion) {
        courColAdapter.setSelectItem(postion);
        CourseEntity courseEntity = couTypeArrayList.get(postion);
        color = courseEntity.getColor();
        tintColor = courseEntity.getVice_color();
        color3 = courseEntity.getColor3();
        color4 = courseEntity.getColor4();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) {
        if ((paramKeyEvent.getAction() == 0)
                && (paramKeyEvent.getKeyCode() == 4)) {
            finish();
            overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
        }
        return super.dispatchKeyEvent(paramKeyEvent);
    }

}
