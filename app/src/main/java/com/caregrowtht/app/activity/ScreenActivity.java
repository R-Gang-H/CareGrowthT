package com.caregrowtht.app.activity;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.ScreenAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.user.EventData;
import com.caregrowtht.app.user.MultipleItem;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.user.UserManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-5-4 14:11:33
 * 筛选条件
 */
public class ScreenActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private String orgId;

    private StudentEntity studentList;
    private ScreenAdapter screenAdapter;

    public static String originId;//来源筛选
    public static String statusId;//状态筛选

    @Override
    public int getLayoutId() {
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);//全屏显示需要添加的语句
        return R.layout.activity_view_menu;
    }

    @Override
    public void initView() {
        initRecyclerView(recyclerView, true);

        ArrayList<MultipleItem> list = new ArrayList<>();
        list.add(new MultipleItem(MultipleItem.TYPE_ORIGIN));
        list.add(new MultipleItem(MultipleItem.TYPE_STATUS));
        screenAdapter = new ScreenAdapter(this, list, studentList);
        recyclerView.setAdapter(screenAdapter);
    }

    @Override
    public void initData() {
//        orgId = getIntent().getStringExtra("orgId");
        orgId = UserManager.getInstance().getOrgId();
        //24.获取leads管理页筛选条件
        getLeadsType();
    }

    private void getLeadsType() {
        //haoruigang on 2018-5-4 10:41:52 获取leads管理页筛选条件
        HttpManager.getInstance().doLeadsType("StudentFragment",
                orgId, new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        studentList = data.getData().get(0);
                        screenAdapter.setData(studentList);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(ScreenActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    @OnClick({R.id.ll_cancel, R.id.rl_cancel, R.id.ll_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_cancel:
            case R.id.rl_cancel:
                finish();
                break;
            case R.id.ll_confirm:
                EventBus.getDefault().post(new EventData<String>().setContent(originId).setPar(statusId).setTag(EventData.TAGTYPE.ESERACH));
                finish();
                break;
        }
    }
}
