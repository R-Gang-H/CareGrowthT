package com.caregrowtht.app.activity;


import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.FormalSearchAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.LoadingFrameView;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018-08-07 19:37:29
 * 正式学员搜索
 */
public class FormalSearchActivity extends BaseActivity implements ViewOnItemClick {


    @BindView(R.id.tv_title)
    EditText tvTitle;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.rv_student)
    RecyclerView rvStudent;
    @BindView(R.id.load_view)
    LoadingFrameView loadView;

    FormalSearchAdapter mFormalAdapter;
    List<StudentEntity> mFormalList = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_formal_search;
    }

    @Override
    public void initView() {
        tvTitle.setOnEditorActionListener((textView, i, keyEvent) -> {
            getSearch();
            LogUtils.tag("sousuo");
            return false;
        });
        initRecyclerView(rvStudent, true);
        mFormalAdapter = new FormalSearchAdapter(mFormalList, this);
        rvStudent.setAdapter(mFormalAdapter);
    }

    @Override
    public void initData() {

    }


    @OnClick(R.id.btn_cancel)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    // 搜索相关学员
    private void getSearch() {
        HashMap<Integer, List<StudentEntity>> studentList = new HashMap<>();// 学员信息
        List<Integer> position = new ArrayList<>();// 当前坐标
        studentList.clear();
        position.clear();

        loadView.setVisibility(View.VISIBLE);
        loadView.setProgressShown(true);

        if (TextUtils.isEmpty(U.stringFilter(tvTitle.getText().toString()))) {
            mFormalAdapter.update(studentList, position, true);
            loadView.setNoShown(true);
            return;
        }
        HttpManager.getInstance().doGetSearchChild("FormalSearchActivity",
                UserManager.getInstance().getOrgId(), tvTitle.getText().toString(),
                new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        if (data != null && data.getData().size() > 0) {
                            mFormalList.clear();
                            mFormalList.addAll(data.getData());
                            for (int i = 0; i < 3; i++) {
                                List<StudentEntity> students = getData(i);
                                if (students.size() > 0) {
                                    studentList.put(i, students);
                                    position.add(i);
                                }
                            }
                            loadView.delayShowContainer(true);
                        } else {
                            loadView.setNoShown(true);
                        }
                        mFormalAdapter.update(studentList, position, true);
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("FormalSearchActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(FormalSearchActivity.this);
                        } else {
                            U.showToast(errorMsg);
                            loadView.setErrorShown(true, v -> getSearch());
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (loadView != null) {
                            LogUtils.d("FormalSearchActivity onFail", throwable.getMessage());
                            loadView.setErrorShown(true, v -> getSearch());
                        }
                    }
                });
    }

    //筛选当前学员
    private List<StudentEntity> getData(int position) {
        List<StudentEntity> datas = new ArrayList<>();
        for (StudentEntity formalEntity : mFormalList) {
            if (position == 0) {
                if (formalEntity.getIsStar().equals("1")) {//是否标星 1：已经标星 2：没有标星
                    datas.add(formalEntity);
                }
            }
            if (position == 1) {
                if (formalEntity.getStatus().equals("1")) {//1：活跃学员 2：非活跃学员 3：标星学员
                    datas.add(formalEntity);
                }
            }
            if (position == 2) {
                if (formalEntity.getStatus().equals("2")) {//1：活跃学员 2：非活跃学员 3：标星学员
                    datas.add(formalEntity);
                }
            }
        }
        return datas;
    }

    @Override
    public void setOnItemClickListener(View view, int postion) {
        startActivity(new Intent(this, StudentDetailsActivity.class)
                .putExtra("StudentEntity", mFormalList.get(postion)));
    }
}
