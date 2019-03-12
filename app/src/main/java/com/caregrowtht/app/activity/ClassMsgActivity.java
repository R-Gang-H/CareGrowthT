package com.caregrowtht.app.activity;

import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.ClassMsgAdapter;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.LoadingFrameView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-10-29 14:08:20
 * 教室管理
 */
public class ClassMsgActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    //机构的所有教室
    List<CourseEntity> classRoomList = new ArrayList<>();
    private ClassMsgAdapter roomAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_class_msg;
    }

    @Override
    public void initView() {
        tvTitle.setText("教室管理");
        initRecyclerView(recyclerView, true);
        roomAdapter = new ClassMsgAdapter(classRoomList, this);
        recyclerView.setAdapter(roomAdapter);
    }

    @Override
    public void initData() {
        getClassroom();
    }


    @OnClick({R.id.rl_back_button, R.id.iv_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
            case R.id.iv_add:
                if (!UserManager.getInstance().isTrueRole("pk_2")) {
                    UserManager.getInstance().showSuccessDialog(this
                            , getString(R.string.text_role));
                    break;
                } else {
                    //添加教室
                    startActivityForResult(new Intent(this, AddClassRoomActivity.class), 2551);
                }
                break;
        }
    }

    private void getClassroom() {
        //haoruigang on 2018-11-27 15:59:32. 39.获取机构的所有教室
        loadView.setProgressShown(true);
        HttpManager.getInstance().doGetClassroom("ClassMsgActivity", UserManager.getInstance().getOrgId(),
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        if (data.getData().size() > 0) {
                            classRoomList.clear();
                            classRoomList.addAll(data.getData());
                            roomAdapter.setData(classRoomList);
                            loadView.delayShowContainer(true);
                        } else {
                            loadView.setNoShown(true);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("ClassMsgActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ClassMsgActivity.this);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 2551) {
                getClassroom();//刷新
            }
        }
    }
}
