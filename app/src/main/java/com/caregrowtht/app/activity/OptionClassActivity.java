package com.caregrowtht.app.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.uitil.ResourcesUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-8-29 17:43:45
 * 班级选项
 */
public class OptionClassActivity extends BaseActivity {

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_class_num)
    EditText etClassNum;
    @BindView(R.id.et_student_num)
    EditText etStudentNum;
    @BindView(R.id.et_audition_price)
    EditText etAuditionPrice;
    private String classNum;
    private String stuNum;
    private String audiPrice;

    @Override
    public int getLayoutId() {
        return R.layout.activity_option_class;
    }

    @Override
    public void initView() {
        tvTitle.setText("选项");
        ivLeft.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_close_1));
    }

    @Override
    public void initData() {
        classNum = getIntent().getStringExtra("classNum");
        stuNum = getIntent().getStringExtra("stuNum");
        audiPrice = getIntent().getStringExtra("audiPrice");
        etClassNum.setText(classNum);
        etStudentNum.setText(stuNum);
        etAuditionPrice.setText(audiPrice);
    }

    @OnClick({R.id.rl_back_button, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
                break;
            case R.id.btn_submit:
                String classNum = etClassNum.getText().toString().trim();
                String stuNum = etStudentNum.getText().toString().trim();
                String audiPrice = etAuditionPrice.getText().toString().trim();
                if (TextUtils.isEmpty(classNum) || TextUtils.isEmpty(stuNum)) {
                    U.showToast("请检查班级人数是否填写");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("classNum", classNum);
                intent.putExtra("stuNum", stuNum);
                intent.putExtra("audiPrice", audiPrice);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
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
