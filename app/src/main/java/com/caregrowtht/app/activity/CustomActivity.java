package com.caregrowtht.app.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.OrgNotifyEntity;
import com.caregrowtht.app.model.UserEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.user.ToUIEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * haoruigang on 2018年7月30日15:11:55
 * 自定义通知
 * <p>
 * 1：自定义 2：放假通知 3：班级通知 4：教师通知 5：学员通知 6：全体通知
 */
public class CustomActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {


    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_group)
    RadioGroup rlGroup;
    @BindView(R.id.rb_reipt)
    RadioButton rbReipt;
    @BindView(R.id.rb_notify)
    RadioButton rbNotify;
    @BindView(R.id.iv_add_notify)
    ImageView ivAddNotify;
    @BindView(R.id.tv_the_stu_count)
    TextView tvTheStuCount;
    @BindView(R.id.iv_edit_notify)
    ImageView ivEditNotify;
    @BindView(R.id.tv_notify_content)
    EditText tvNotifyContent;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.tv_teacher_all)
    TextView tvTeacherAll;
    @BindView(R.id.tv_student_all)
    TextView tvStudentAll;
    @BindView(R.id.iv_teacher_all)
    ImageView ivTeacherAll;
    @BindView(R.id.iv_student_all)
    ImageView ivStudentAll;
    @BindView(R.id.rl_all)
    RelativeLayout rlAll;
    @BindView(R.id.ll_all)
    LinearLayout llAll;


    private String tvTitleName;
    private String OrgId;
    private String notifyType = "1";
    private String isReceipt = "1";
    private List<UserEntity> teacherList = new ArrayList<>();//教师信息集合
    private List<UserEntity> studentList = new ArrayList<>();//学员信息集合
    private boolean isTheAll = false;//教师是否全选
    private boolean isStuAll = false;//学员是否全选
    private StringBuffer courseIds = new StringBuffer();
    private StringBuffer toStudent = new StringBuffer();
    private StringBuffer toTeacher = new StringBuffer();
    private List<CourseEntity> courseList = new ArrayList<>();//课程信息集合

    @Override
    public int getLayoutId() {
        return R.layout.activity_custom;
    }

    @Override
    public void initView() {
        rlGroup.setOnCheckedChangeListener(this);
        rbReipt.setSelected(true);
    }

    @Override
    public void initData() {
        OrgId = getIntent().getStringExtra("orgId");// 机构id
        notifyType = getIntent().getStringExtra("notifyType");// 通知类型
        //1:标星学员 2：活跃学员 3：非活跃待确认 4：非活跃历史 5待审核;
        String status = "1";
        switch (notifyType) {//通知的类型 1：自定义 2：放假通知 3：班级通知 4：教师通知 5：学员通知 6：全体通知
            case "1":
                tvTitleName = "自定义";
                break;
            case "2":
                tvTitleName = "标星学员";
                rlAll.setVisibility(View.VISIBLE);
                tvTeacherAll.setVisibility(View.GONE);
                ivTeacherAll.setVisibility(View.GONE);
                ivAddNotify.setVisibility(View.GONE);
                ivEditNotify.setVisibility(View.VISIBLE);
                tvStudentAll.setText(tvTitleName);
                getStudent(status);
                break;
            case "3":
                tvTitleName = "班级";
                break;
            case "4":
                tvTitleName = "教师";
                rlAll.setVisibility(View.VISIBLE);
                tvStudentAll.setVisibility(View.GONE);
                ivStudentAll.setVisibility(View.GONE);
                ivTeacherAll.setVisibility(View.GONE);
                ivAddNotify.setVisibility(View.GONE);
                ivEditNotify.setVisibility(View.VISIBLE);
                getNoticeHuman(1 + "");//身份 1：老师 2：学员
                break;
            case "5":
                tvTitleName = "学员";
                rlAll.setVisibility(View.VISIBLE);
                ivStudentAll.setVisibility(View.GONE);
                tvTeacherAll.setVisibility(View.GONE);
                ivTeacherAll.setVisibility(View.GONE);
                ivAddNotify.setVisibility(View.GONE);
                ivEditNotify.setVisibility(View.VISIBLE);
                getNoticeHuman(2 + "");//身份 1：老师 2：学员
                break;
            case "6":
                tvTitleName = "全体";
                rlAll.setVisibility(View.VISIBLE);
                ivTeacherAll.setVisibility(View.GONE);
                ivStudentAll.setVisibility(View.GONE);
                ivAddNotify.setVisibility(View.GONE);
                ivEditNotify.setVisibility(View.GONE);
                for (int i = 1; i < 3; i++) {
                    getNoticeHuman(i + "");//身份 1：老师 2：学员
                }
                break;
        }
        tvTitle.setText(String.format(tvTitleName + "%s", "通知"));
    }

    @OnClick({R.id.rl_back_button, R.id.iv_add_notify, R.id.iv_edit_notify,
            R.id.iv_teacher_all, R.id.iv_student_all, R.id.btn_cancel, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.iv_add_notify:
                if (TextUtils.equals(notifyType, "3")) {//班级通知
                    startActivity(new Intent(this, SchoolWorkActivity.class));
                } else {
                    startActivity(new Intent(this, ContactsActivity.class)
                            .putExtra("orgId", OrgId).putExtra("notifyType", notifyType));
                }
                break;
            case R.id.iv_teacher_all:
                teacherList.clear();
                tvTeacherAll.setVisibility(View.GONE);
                ivTeacherAll.setVisibility(View.GONE);
                break;
            case R.id.iv_student_all:
                studentList.clear();
                tvStudentAll.setVisibility(View.GONE);
                ivStudentAll.setVisibility(View.GONE);
                break;
            case R.id.iv_edit_notify:
                if (TextUtils.equals(notifyType, "3")) {//班级通知
                    startActivity(new Intent(this, SchoolWorkActivity.class));
                } else {
                    Intent intent = new Intent(this, ContactsActivity.class)
                            .putExtra("orgId", OrgId)
                            .putExtra("notifyType", notifyType)
                            .putExtra("isTheAll", isTheAll)
                            .putExtra("isStuAll", isStuAll)
                            .putExtra("teacherList", (Serializable) teacherList)
                            .putExtra("studentList", (Serializable) studentList);
                    startActivity(intent);
                }
                break;
            case R.id.btn_submit:
                buildNewNotice();
                break;
        }
    }

    /**
     * 新建一个通知
     */
    private void buildNewNotice() {
        String content = tvNotifyContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            U.showToast("请编写通知内容");
            return;
        }
        if (courseList.size() > 0) {
            boolean courseCount = false;
            for (CourseEntity course : courseList) {
                if (courseCount) {
                    courseIds.append(",");
                }
                courseIds.append(course.getCourseId());//班级通知这个字段有值
                courseCount = true;
            }
        }
        boolean teacherCount = false;
        for (UserEntity teacher : teacherList) {
            if (teacherCount) {
                toTeacher.append(",");
            }
            toTeacher.append(teacher.getUserId());
            teacherCount = true;
        }
        boolean studCount = false;
        for (UserEntity student : studentList) {
            if (studCount) {
                toStudent.append(",");
            }
            String userId;
            if (notifyType.equals("2")) {//2：标星学员通知
                userId = student.getStuId();
            } else {
                userId = student.getUserId();
            }
            toStudent.append(userId);
            studCount = true;
        }
        assert courseIds != null;
        HttpManager.getInstance().doBuildNewNotice("CustomActivity", OrgId, notifyType,
                isReceipt, content, courseIds.toString(), toStudent.toString(),
                toTeacher.toString(), new HttpCallBack<BaseDataModel<OrgNotifyEntity>>(this, true) {
                    @Override
                    public void onSuccess(BaseDataModel<OrgNotifyEntity> data) {
                        U.showToast("发布成功");
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_NOTIFY));
                        finish();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CustomActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CustomActivity.this);
                        } else if (statusCode == 1001) {
                            U.showToast("请选择通知对象");
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CustomActivity onError", throwable.getMessage());
                    }
                });
    }

    /**
     * 26.选择通知对象
     */
    private void getNoticeHuman(String identity) {//身份 1：老师 2：学员
        HttpManager.getInstance().doGetNoticeHuman("CustomActivity",
                OrgId, identity, new HttpCallBack<BaseDataModel<UserEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        if (TextUtils.equals(identity, "1")) {// 教师
                            teacherList.clear();
                            teacherList.addAll(data.getData());
                        }
                        if (TextUtils.equals(identity, "2")) {// 学员
                            studentList.clear();
                            studentList.addAll(data.getData());
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CustomActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CustomActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("CustomActivity onError", throwable.getMessage());
                    }
                });
    }

    private void getStudent(final String status) {
        //10.获取机构的正式学员 haoruigang on 2018-8-7 15:50:55
        HttpManager.getInstance().doGetOrgChild("CustomActivity",
                OrgId, status, pageIndex + "", "",
                new HttpCallBack<BaseDataModel<UserEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        studentList.clear();
                        studentList.addAll(data.getData());
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("CustomActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(CustomActivity.this);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.tag("CustomActivity onError " + throwable);
                    }
                });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getCheckedRadioButtonId()) {
            case R.id.rb_reipt:
                isReceipt = "1";// 1:需要回执
                rbReipt.setSelected(true);
                rbNotify.setSelected(false);
                break;
            case R.id.rb_notify:
                isReceipt = "2";// 2:不需要回执
                rbReipt.setSelected(false);
                rbNotify.setSelected(true);
                break;
        }
    }

    @Override
    public void onEvent(ToUIEvent event) {
        super.onEvent(event);
        if (event.getWhat() == ToUIEvent.CLASS_NOTIFY) {//班级通知选择后
            ivAddNotify.setVisibility(View.GONE);
            ivEditNotify.setVisibility(View.VISIBLE);
            CourseEntity courseData = (CourseEntity) event.getObj();//课程信息
            for (int i = 0; i < courseList.size(); i++) {
                if (TextUtils.equals(courseList.get(i).getCourseId(), courseData.getCourseId())) {
                    break;//如果已存在不再添加
                }
            }
            addCourse(courseData);
        } else {
            teacherList.clear();
            studentList.clear();
            tvTheStuCount.setText("");
            teacherList.addAll((List<UserEntity>) event.getObj());// 1：老师 选择的教师通知对象
            studentList.addAll((List<UserEntity>) event.getObj1());// 2：学员 选择的学员通知对象
            isTheAll = (boolean) event.getObj2();//教师是否全选
            isStuAll = (boolean) event.getObj3();//学员是否全选

            //默认隐藏
            tvTeacherAll.setVisibility(View.GONE);
            ivTeacherAll.setVisibility(View.GONE);
            tvStudentAll.setVisibility(View.GONE);
            ivStudentAll.setVisibility(View.GONE);
            tvTheStuCount.setVisibility(View.VISIBLE);
            if (isTheAll) {//教师全选
                rlAll.setVisibility(View.VISIBLE);
                tvTeacherAll.setVisibility(View.VISIBLE);
                ivTeacherAll.setVisibility(View.VISIBLE);
            }
            if (isStuAll) {//学员全选
                rlAll.setVisibility(View.VISIBLE);
                tvStudentAll.setVisibility(View.VISIBLE);
                ivStudentAll.setVisibility(View.VISIBLE);
            }
            boolean isTeacherEmtity = teacherList == null || teacherList.size() == 0;//教师是否为空
            boolean isStudentEmtity = studentList == null || studentList.size() == 0;//学员是否为空

            switch (event.getWhat()) {
                case ToUIEvent.CUSTOM_CHECK_NOTIFY://自定义通知选择后
                    if (!isTheAll) {
                        if (!isTeacherEmtity) {
                            setTheStuCount(isTeacherEmtity, "%s等老师%s人", teacherList);
                        }
                    }
                    if (!isStuAll) {
                        if (!isStudentEmtity) {
                            setTheStuCount(isStudentEmtity, "%s等学员%s人", studentList);
                        }
                    }
                    if (!isTheAll && !isStuAll) {//教师,学员非全选
                        if (!isTeacherEmtity && !isStudentEmtity) {
                            tvTheStuCount.setText(String.format("%s等老师%s人,%s等学员%s人",
                                    isTeacherEmtity ? "" : teacherList.get(0).getUserName(),
                                    isTeacherEmtity ? "0" : teacherList.size(),
                                    isStudentEmtity ? "" : studentList.get(0).getUserName(),
                                    isStudentEmtity ? "0" : studentList.size()));
                        }
                    }
                    ivAddNotify.setVisibility(View.GONE);
                    ivEditNotify.setVisibility(View.VISIBLE);
                    break;
                case ToUIEvent.HOLIDAY_NOTIFY://标星学员通知选择后
                    tvTheStuCount.setText(String.format("%s等学员%s人",
                            isStudentEmtity ? "" : studentList.get(0).getStuName(),
                            isStudentEmtity ? "0" : studentList.size()));
                    ivAddNotify.setVisibility(View.GONE);
                    ivEditNotify.setVisibility(View.VISIBLE);
                    break;
                case ToUIEvent.TEACHER_NOTIFY://教师通知选择后
                    setTheStuCount(isTeacherEmtity, "%s等老师%s人", teacherList);
                    break;
                case ToUIEvent.STUDENT_NOTIFY://学员通知选择后
                    setTheStuCount(isStudentEmtity, "%s等学员%s人", studentList);
                    break;
            }
        }

    }

    private void setTheStuCount(boolean isEmtity, String s, List<UserEntity> studentList) {
        tvTheStuCount.setText(String.format(s,
                isEmtity ? "" : studentList.get(0).getUserName(),
                isEmtity ? "0" : studentList.size()));
    }

    /**
     * 添加课程信息
     *
     * @param courseEntity
     */
    private void addCourse(CourseEntity courseEntity) {
        // 参数设置
        LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(this).inflate(R.layout.course_item, null);
        TextView tvCourseInfo = view.findViewById(R.id.tv_course_info);
        ImageView ivDelCourse = view.findViewById(R.id.iv_delete_course);
        ivDelCourse.setOnClickListener(v -> {
            deleteCourse(courseEntity);//删除选择的课程信息
        });
        menuLinerLayoutParames.setMargins(6, 6, 6, 6);
        long startTime = Long.parseLong(courseEntity.getStartAt());
        long endTime = Long.parseLong(courseEntity.getEndAt());
        tvCourseInfo.setText(String.format("%s\t%s", courseEntity.getCourseName(), TimeUtils.getDateToString(startTime, "MM月dd日 HH:mm") + "-" + TimeUtils.getDateToString(endTime, "HH:mm")));
        // 设置id，方便后面删除
        view.setTag(courseEntity);
        llAll.addView(view, menuLinerLayoutParames);
        courseList.add(courseEntity);//选择的课程信息
    }

    /**
     * 删除课程信息
     *
     * @param courseEntity
     */
    private void deleteCourse(CourseEntity courseEntity) {
        View view = llAll.findViewWithTag(courseEntity);
        llAll.removeView(view);
        for (int i = 0; i < courseList.size(); i++) {
            if (TextUtils.equals(courseList.get(i).getCourseId(), courseEntity.getCourseId())) {
                courseList.remove(i);
            }
        }
    }
}
