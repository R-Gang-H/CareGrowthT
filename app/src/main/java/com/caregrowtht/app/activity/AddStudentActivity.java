package com.caregrowtht.app.activity;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.user.EventData;
import com.caregrowtht.app.model.OrgEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.BitmapUtils;
import com.google.zxing.WriterException;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-6-6 14:52:07
 * 添加学员
 */
public class AddStudentActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.img_org_qrcode)
    ImageView imgOrgQrcode;
    @BindView(R.id.et_stu_name)
    EditText etStuName;
    @BindView(R.id.et_stu_phone)
    EditText etStuPhone;
    @BindView(R.id.btn_complete)
    Button btnComplete;
    private OrgEntity orgEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_student;
    }

    @Override
    public void initView() {
        tvTitle.setText("二维码");
    }

    @Override
    public void initData() {
        orgEntity = (OrgEntity) getIntent().getSerializableExtra("orgEntity");
        Bitmap bitmap = null;
        try {
            bitmap = BitmapUtils.create2DCode(orgEntity.getOrgQRCodeUrl());//根据内容生成二维码
            imgOrgQrcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.rl_back_button, R.id.btn_complete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.btn_complete:
                Addleads();
                break;
        }
    }

    private void Addleads() {
        Boolean isAddleads = true;
        String stuName = etStuName.getText().toString().trim();
        String mobile = etStuPhone.getText().toString().trim();
        if (StrUtils.isEmpty(stuName)) {
            U.showToast("请输入学员姓名!");
            isAddleads = false;
        }
        if (StrUtils.isEmpty(mobile)) {
            U.showToast("请输入学员联系方式!");
            isAddleads = false;
        }
        if (isAddleads) {
            //haoruigang on 2018-6-6 15:43:39
            HttpManager.getInstance().doAddleads("AddStudentActivity",
                    orgEntity.getOrgId(), stuName, mobile,
                    new HttpCallBack<BaseDataModel<StudentEntity>>() {
                        @Override
                        public void onSuccess(BaseDataModel<StudentEntity> data) {
                            U.showToast("添加试听信息成功!");
                            EventBus.getDefault().post(new EventData<>().setTag(EventData.TAGTYPE.ESERACH));
                            finish();
                        }

                        @Override
                        public void onFail(int statusCode, String errorMsg) {
                            if (statusCode == 1002 || statusCode == 1011) {//异地登录
                                U.showToast("该账户在异地登录!");
                                HttpManager.getInstance().dologout(AddStudentActivity.this);
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {

                        }
                    });
        }
    }
}
