package com.caregrowtht.app.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.library.utils.U;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.MemoHistoryAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.permissions.PermissionCallBackM;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-5-4 17:53:19
 * 潜在学员详情
 */
public class PotentStuInfoActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_call_phone)
    ImageView ivCallPhone;
    @BindView(R.id.tv_potent_nickname)
    TextView tvPotentNickname;
    @BindView(R.id.tv_phone_num)
    TextView tvPhoneNum;
    @BindView(R.id.tv_add_time_v)
    TextView tvAddTimeV;
    @BindView(R.id.tv_comm_number_v)
    TextView tvCommNumberV;
    @BindView(R.id.tv_mark_v)
    TextView tvMarkV;
    @BindView(R.id.tv_recom_v)
    TextView tvRecomV;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private String stuId;
    private ArrayList<StudentEntity.MarkList> markList = new ArrayList<>();
    private StudentEntity studentDatas;
    private MemoHistoryAdapter memoHistAdapter;
    private String phoneNum;

    @Override
    public int getLayoutId() {
        return R.layout.activity_potent_stu_info;
    }

    @Override
    public void initView() {
        tvTitle.setText("潜在学员详情");
        stuId = getIntent().getStringExtra("stuId");
        getLeadsInfo();
        initRecyclerView(recyclerView, true);
    }

    private void getLeadsInfo() {
        //haoruigang on 2018-5-4 18:39:23 26.获取潜在学生（临时学生）详情
        HttpManager.getInstance().doLeadsInfo("PotentStuInfoActivity",
                stuId, new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        studentDatas = data.getData().get(0);
                        setData(studentDatas);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(PotentStuInfoActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    private void setData(StudentEntity studentDatas) {
        tvPotentNickname.setText(studentDatas.getStuName());
        phoneNum = studentDatas.getPhone();
        tvPhoneNum.setText(phoneNum);
        tvAddTimeV.setText(Constant.originArray[Integer.valueOf(studentDatas.getOriginId())]);
        tvCommNumberV.setText(Constant.statusArray[Integer.valueOf(studentDatas.getStatusId())]);
        markList = studentDatas.getMarkList();//备注历史
        tvMarkV.setText(((markList != null) ? markList.size() : 0) + "次");
        tvRecomV.setText(studentDatas.getReference());
        memoHistAdapter = new MemoHistoryAdapter(markList, this);
        recyclerView.setAdapter(memoHistAdapter);
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.rl_back_button, R.id.btn_submit, R.id.iv_call_phone})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.iv_call_phone:
                if (StrUtils.isEmpty(phoneNum)) {
                    break;
                }
                getCallPhone();
                break;
            case R.id.btn_submit:
                startActivity(new Intent(PotentStuInfoActivity.this, MarkActivity.class).putExtra("stuId", stuId));
                break;
        }
    }

    //获取CALL_PHONE权限 haoruigang on 2018-4-3 15:29:46
    public void getCallPhone() {
        requestPermission(
                Constant.RC_CALL_PHONE,
                new String[]{Manifest.permission.CALL_PHONE},
                getString(R.string.rationale_call_phone),
                new PermissionCallBackM() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionGrantedM(int requestCode, String... perms) {
                        LogUtils.e(PotentStuInfoActivity.this, "TODO: CALL_PHONE Granted", Toast.LENGTH_SHORT);
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum)));
                    }

                    @Override
                    public void onPermissionDeniedM(int requestCode, String... perms) {
                        LogUtils.e(PotentStuInfoActivity.this, "TODO: CALL_PHONE Denied", Toast.LENGTH_SHORT);
                    }
                });
    }
}
