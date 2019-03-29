package com.caregrowtht.app.activity;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.StudentSituatAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by haoruigang on 2018-4-26 10:34:11.
 * 学生情况
 */

public class StudentSituatActivity extends BaseActivity {

    @BindView(R.id.rv_stu_situat)
    RecyclerView recyclerView;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private String lessonId;

    ArrayList<StudentEntity> studentEntities = new ArrayList<>();//签到的/请假的
    private StudentSituatAdapter studentSitAdapter;
    private CourseEntity courseData;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_stu_situat;
    }

    @Override
    public void initView() {
        courseData = (CourseEntity) getIntent().getSerializableExtra("courseData");

        lessonId = courseData.getCourseId();
        tvTitle.setText(courseData.getCourseName());

        initRecyclerView(recyclerView, true);
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setOnRefreshListener(refreshlayout -> getLessonDetail3());
        studentSitAdapter = new StudentSituatAdapter(studentEntities, this, courseData, null);
        recyclerView.setAdapter(studentSitAdapter);
    }

    @Override
    public void initData() {
        getLessonDetail3();
    }

    public void getLessonDetail3() {
        //haoruigang on 2018-4-26 11:25:39 12.获取课程列表详情（学员情况）
        HttpManager.getInstance().doLessonChild("StudentSituatActivity",
                lessonId, new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        studentEntities.clear();
                        studentEntities.addAll(data.getData());
                        List<StudentEntity> stuStatus = removeDuplicateOrder(studentEntities);//筛选学员请假状态
                        studentSitAdapter.setData(stuStatus, studentEntities);
                        refreshLayout.finishLoadmore();
                        refreshLayout.finishRefresh();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(StudentSituatActivity.this);
                        } else {
//                    U.showToast(errorMsg);//不能打开
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    /**
     * 根据status去重
     *
     * @param orderList
     * @return
     * @author haoruigang
     */
    private static List<StudentEntity> removeDuplicateOrder(List<StudentEntity> orderList) {
        Set<StudentEntity> set = new TreeSet<>((a, b) -> {
            // 字符串则按照asicc码升序排列
            return a.getStatus().compareTo(b.getStatus());
        });
        set.addAll(orderList);
        return new ArrayList<>(set);
    }

    @OnClick(R.id.rl_back_button)
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.rl_back_button:
                finish();
                break;
        }
    }
}
