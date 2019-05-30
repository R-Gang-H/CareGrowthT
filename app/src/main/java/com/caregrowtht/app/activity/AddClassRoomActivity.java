package com.caregrowtht.app.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.UserManager;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-10-29 15:14:16
 * 添加教室
 */
public class AddClassRoomActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_class_room_name)
    EditText etClassRoomName;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    String roomId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_class_room;
    }

    @Override
    public void initView() {
        tvTitle.setText("添加教室");
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.rl_back_button, R.id.btn_cancel, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_submit:
                addClassroom();
                break;
        }
    }

    private void addClassroom() {
        String roomName = etClassRoomName.getText().toString().trim();
        if (TextUtils.isEmpty(roomName)) {
            U.showToast("请输入教室名称");
            return;
        }
        HttpManager.getInstance().doAddClassroom("AddClassRoomActivity", UserManager.getInstance().getOrgId(),
                roomId, roomName, new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("AddClassRoomActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(AddClassRoomActivity.this);
                        } else if (statusCode == 1055) {
                            U.showToast("教室已存在");
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }
}
