package com.caregrowtht.app.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.StrUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-5-21 18:39:02
 * 添加备注
 */
public class MarkActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.btn_wechat)
    RadioButton btnWechat;
    @BindView(R.id.btn_email)
    RadioButton btnEmail;
    @BindView(R.id.btn_inter)
    RadioButton btnInter;
    @BindView(R.id.btn_phone)
    RadioButton btnPhone;
    @BindView(R.id.btn_other)
    RadioButton btnOther;
    @BindView(R.id.btn_1)
    RadioButton btn1;
    @BindView(R.id.btn_2)
    RadioButton btn2;
    @BindView(R.id.btn_3)
    RadioButton btn3;
    @BindView(R.id.btn_4)
    RadioButton btn4;
    @BindView(R.id.btn_5)
    RadioButton btn5;
    @BindView(R.id.btn_complete)
    Button btnComplete;

    private int commWay;
    private int status;
    private String stuId;
    private String mark;

    @Override
    public int getLayoutId() {
        return R.layout.activity_mark;
    }


    List<RadioButton> mButtons1 = new ArrayList<>();
    List<RadioButton> mButtons2 = new ArrayList<>();

    @Override
    public void initView() {
        tvTitle.setText("添加备注");
        mButtons1.add(btnWechat);
        mButtons1.add(btnEmail);
        mButtons1.add(btnInter);
        mButtons1.add(btnPhone);
        mButtons1.add(btnOther);
        mButtons2.add(btn1);
        mButtons2.add(btn2);
        mButtons2.add(btn3);
        mButtons2.add(btn4);
        mButtons2.add(btn5);
        stuId = getIntent().getStringExtra("stuId");

    }

    @Override
    public void initData() {

    }

    //沟通方式、状态
    @OnClick({R.id.rl_back_button, R.id.btn_wechat, R.id.btn_email, R.id.btn_inter, R.id.btn_phone, R.id.btn_other, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4, R.id.btn_5, R.id.btn_complete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.btn_wechat:
                clearButton1();
                btnWechat.setSelected(true);
                commWay = 1;
                break;
            case R.id.btn_email:
                clearButton1();
                btnEmail.setSelected(true);
                commWay = 2;
                break;
            case R.id.btn_inter:
                clearButton1();
                btnInter.setSelected(true);
                commWay = 3;
                break;
            case R.id.btn_phone:
                clearButton1();
                btnPhone.setSelected(true);
                commWay = 4;
                break;
            case R.id.btn_other:
                clearButton1();
                btnOther.setSelected(true);
                commWay = 5;
                break;
            case R.id.btn_1:
                clearButton2();
                btn1.setSelected(true);
                status = 1;
                break;
            case R.id.btn_2:
                clearButton2();
                btn2.setSelected(true);
                status = 2;
                break;
            case R.id.btn_3:
                clearButton2();
                btn3.setSelected(true);
                status = 3;
                break;
            case R.id.btn_4:
                clearButton2();
                btn4.setSelected(true);
                status = 4;
                break;
            case R.id.btn_5:
                clearButton2();
                btn5.setSelected(true);
                status = 5;
                break;
            case R.id.btn_complete:
                mark = etContent.getText().toString().trim();
                if (StrUtils.isEmpty(mark)) {
                    U.showToast("请填写备注信息!");
                    break;
                }
                AddleadsLog();
                break;
        }
    }

    private void clearButton1() {
        for (int j = 0, size = mButtons1.size(); j < size; j++) {
            mButtons1.get(j).setSelected(false);
        }
    }

    private void clearButton2() {
        for (int j = 0, size = mButtons2.size(); j < size; j++) {
            mButtons2.get(j).setSelected(false);
        }
    }

    private void AddleadsLog() {
        //haoruigang on 2018-5-21 19:31:08 27.添加备注
        HttpManager.getInstance().doAddleadsLog("MarkActivity",
                stuId, mark, commWay, status, new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        U.showToast("添加备注成功!");
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MarkActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

}
