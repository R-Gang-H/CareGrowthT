package com.caregrowtht.app.activity;

import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.android.library.view.WheelPopup;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.BaseModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.UserEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.picker.builder.TimePickerBuilder;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import butterknife.OnClick;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * haoruigang on 2018-9-4 11:43:23
 * 修改这节课
 */
public class MadifyCourseActivity extends BaseActivity {

    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_course_name)
    TextView tvCourseName;
    @BindView(R.id.tv_class_times)
    TextView tvClassTimes;
    @BindView(R.id.iv_avatar)
    AvatarImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.rl_mainTeacher)
    RelativeLayout rlMainTeacher;
    @BindView(R.id.teacherMenu)
    LinearLayout teacherMenu;
    @BindView(R.id.horizonMenu)
    HorizontalScrollView horizonMenu;
    @BindView(R.id.teacherMenu1)
    LinearLayout teacherMenu1;
    @BindView(R.id.horizonMenu1)
    HorizontalScrollView horizonMenu1;
    @BindView(R.id.tv_classRoom)
    TextView tvClassRoom;
    @BindView(R.id.btn_check_conflict)
    Button btnCheckConflict;
    @BindView(R.id.btn_dereact_course)
    Button btnDereactCourse;

    private CourseEntity courseData = new CourseEntity<>();
    private List<UserEntity> mainTeachers = new ArrayList<>();
    private List<UserEntity> assistantTeachers = new ArrayList<>();
    private List<UserEntity> studentLists = new ArrayList<>();
    //机构的所有教室
    List<CourseEntity> classRoomList = new ArrayList<>();
    private String courseTime;

    private CourseEntity editCourseData = new CourseEntity();//上页传过来的值
    private String orgId;
    private String courseId;
    private String courseName;
    private String classroomId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_madify_course;
    }

    @Override
    public void initView() {
        tvTitle.setText("修改这节课");
        ivLeft.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_close_1));
    }

    @Override
    public void initData() {
        editCourseData = (CourseEntity) getIntent().getSerializableExtra("courseData");
        orgId = UserManager.getInstance().getOrgId();
        courseId = editCourseData.getCourseId();
        courseName = editCourseData.getCourseName();
        getClassroom();
        courseLessonDetails();
    }

    private void courseLessonDetails() {
        //haoruigang on 2018-7-6 17:23:39 获取排课的详细信息
        HttpManager.getInstance().doGetLessonInfoV2("MadifyCourseActivity", orgId
                , courseId, "1", new HttpCallBack<BaseModel<CourseEntity<String, String, String>>>() {
                    @Override
                    public void onSuccess(BaseModel<CourseEntity<String, String, String>> data) {
                        courseData = data.getData();
                        setCourseData();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("MadifyCourseActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MadifyCourseActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("MadifyCourseActivity Throwable", throwable.getMessage());
                    }
                });
    }

    private void setCourseData() {
        tvCourseName.setText(String.format("所属排课:%s", courseName));
        String[] lesTime = courseData.getLesTime().split(",");
        String startAt = lesTime[0];
        String endAt = lesTime[1];
        String selectStartDate1 = DateUtil.getDate(Long.valueOf(startAt), "yyyy年MM月dd日 HH:mm");
        String selectEndTime = DateUtil.getDate(Long.valueOf(endAt), "HH:mm");
        tvClassTimes.setText(String.format("%s~%s", selectStartDate1, selectEndTime));
        courseTime = String.format("%s,%s", startAt, endAt);

        long nowTime = System.currentTimeMillis() / 1000;
        long endTime = Long.valueOf(endAt);
        boolean isToday = TimeUtils.IsToday(DateUtil.getDate(endTime, "yyyy-MM-dd"));// 是否为今天
        if (isToday) {
            if (nowTime > endTime) {
                tvClassTimes.setEnabled(false);// 禁用TextView
            }
        }

        String teacherId = courseData.getTeacherId();//主教
        getNoticeHuman(teacherId, "1", "1");
        String teacherIds = courseData.getTeacherIds();//助教
        getNoticeHuman(teacherIds, "2", "1");
        String students = (String) courseData.getStudents();//学员
//        getNoticeHuman(students, "", "2");
        getPlanStudents(students);//77.获取机构下的学员详情

        classroomId = courseData.getClassroomId();
        for (CourseEntity classRoom : classRoomList) {
            if (TextUtils.equals(classRoom.getId(), classroomId)) {
                classroomId = classRoom.getId();
                tvClassRoom.setText(classRoom.getClassroomName());
                break;
            }
        }
    }

    /**
     * 26.选择通知对象
     *
     * @param teacherId
     * @param teacherType 1：主教老师 2：助教老师
     * @param identity    //身份 1：老师 2：学员
     */
    private void getNoticeHuman(String teacherId, String teacherType, String identity) {
        HttpManager.getInstance().doGetNoticeHuman("MadifyCourseActivity",
                orgId, identity, new HttpCallBack<BaseDataModel<UserEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        boolean isShowTea = false;
                        boolean isShowStu = false;
                        String[] teacherIds = teacherId.split(",");//助教
                        for (String aTeacherIds : teacherIds) {
                            for (UserEntity mTeacher : data.getData()) {
                                if (TextUtils.equals(mTeacher.getUserId(), aTeacherIds)) {
                                    if (identity.equals("1")) {//教师
                                        if (teacherType.equals("1")) {
                                            //筛选选中的主教id
                                            mainTeachers.add(mTeacher);
                                            rlMainTeacher.setVisibility(View.VISIBLE);
                                            ivAvatar.setTextAndColor(TextUtils.isEmpty(mTeacher.getUserName()) ? "" :
                                                    mTeacher.getUserName().substring(0, 1), getResources().getColor(R.color.b0b2b6));
                                            GlideUtils.setGlideImg(MadifyCourseActivity.this,
                                                    mTeacher.getUserImage(), 0, ivAvatar);
                                            tvName.setText(mTeacher.getUserName());
                                            break;
                                        } else {
                                            //筛选选中的助教id
                                            isShowTea = true;
                                            assistantTeachers.add(mTeacher);
                                        }
                                    } else {//学员
                                        //筛选选中的学员id
                                        isShowStu = true;
                                        studentLists.add(mTeacher);
                                    }
                                }
                            }
                        }
                        //绘制助教头像
                        if (isShowTea) {
                            for (UserEntity assistant : assistantTeachers) {
                                showTeacherImage(assistant);
                            }
                        }
                        //绘制学员头像
                        if (isShowStu) {
                            for (UserEntity student : studentLists) {
                                showStudentImage(student);
                            }
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("MadifyCourseActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MadifyCourseActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("MadifyCourseActivity Throwable", throwable.getMessage());
                    }
                });
    }

    /**
     * 77.获取机构下的学员详情
     */
    private void getPlanStudents(String students) {
        HttpManager.getInstance().doGetPlanStudents("NewWorkActivity",
                orgId, students, new HttpCallBack<BaseDataModel<UserEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        //筛选选中的学员id
                        studentLists.addAll(data.getData());
                        //绘制学员头像
                        for (UserEntity student : studentLists) {
                            showStudentImage(student);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("MadifyCourseActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MadifyCourseActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("MadifyCourseActivity Throwable", throwable.getMessage());
                    }
                });
    }

    private void getClassroom() {
        //haoruigang on 2018-8-28 16:47:53 39.获取机构的所有教室
        HttpManager.getInstance().doGetClassroom("MadifyCourseActivity", orgId,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        classRoomList.clear();
                        classRoomList.addAll(data.getData());
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("MadifyCourseActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MadifyCourseActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("MadifyCourseActivity Throwable", throwable.getMessage());
                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.tv_class_times, R.id.iv_mainTeacher_arrow, R.id.iv_assistant_arrow,
            R.id.iv_student, R.id.iv_school, R.id.btn_check_conflict, R.id.btn_dereact_course})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
                finish();
                break;
            case R.id.tv_class_times:
                selectStartEndTime();
                break;
            case R.id.iv_mainTeacher_arrow:
                startActivityForResult(new Intent(MadifyCourseActivity.this, TeacherSelectActivity.class)
                        .putExtra("teacherType", "1")
                        .putExtra("mainTeachers", (Serializable) mainTeachers), 3335);//教师类型 1：主讲教师 2：助教
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
                break;
            case R.id.iv_assistant_arrow:
                if (mainTeachers.size() == 0) {
                    U.showToast("请先选择主讲教师");
                    break;
                }
                startActivityForResult(new Intent(MadifyCourseActivity.this, TeacherSelectActivity.class)
                        .putExtra("teacherType", "2")
                        .putExtra("mainTeachers", (Serializable) mainTeachers)
                        .putExtra("assistantTeachers", (Serializable) assistantTeachers), 3395);//教师类型 1：主讲教师 2：助教
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
                break;
            case R.id.iv_student:
                if (TextUtils.isEmpty(courseData.getOrgCardIds())) {
                    showNoCardDialog("“此排课没有设置课时卡”,请在“修改这节课以后的排课”中设置排课所需的课时卡。");
                    return;
                }
                startActivityForResult(new Intent(this, FastenStudentActivity.class)
                        .putExtra("orgCardIds", courseData.getOrgCardIds())
                        .putExtra("studentList", (Serializable) studentLists), 3293);
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
                break;
            case R.id.iv_school:
                showSchoolDialog();
                break;
            case R.id.btn_check_conflict:
                btnCheckConflict.setEnabled(false);
                modifyCourseLesson("1");
                break;
            case R.id.btn_dereact_course:
                btnDereactCourse.setEnabled(false);
                // 修改这一节课
                modifyCourseLesson("2");
                break;
        }
    }

    /**
     * 无课时卡提示
     */
    private void showNoCardDialog(String desc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_teach_lib, null);
        TextView tvDesc = view.findViewById(R.id.tv_desc);
        tvDesc.setText(Html.fromHtml(desc));
        TextView tvOk = view.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(v -> dialog.dismiss());
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
    }

    /**
     * 44.修改一节课
     */
    private void modifyCourseLesson(String check) {//1:检查排课冲突 2：直接排入课表
        CheckValadata checkValadata = new CheckValadata(check).invoke();
        if (checkValadata.is()) {
            btnCheckConflict.setEnabled(true);
            btnDereactCourse.setEnabled(true);
            return;
        }
        String mainTeacher = checkValadata.getMainTeacher();
        StringBuilder subTeachers = checkValadata.getSubTeachers();
        StringBuilder students = checkValadata.getStudents();
        String classroomId = checkValadata.getClassroomId();
        HttpManager.getInstance().doModifyCourseLesson("MadifyCourseActivity", orgId, check,
                courseId, courseTime.toString(), mainTeacher, subTeachers.toString(), students.toString(),
                classroomId, new HttpCallBack<BaseDataModel<CourseEntity>>(this) {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        if (TextUtils.equals(check, "1")) {
                            btnCheckConflict.setEnabled(true);
                            if (data != null && data.getData().size() > 0) {
                                showConflictDialog(data.getData().get(0));
                            } else {
                                showConflictDialog(null);
                            }
                        } else {
                            btnDereactCourse.setEnabled(true);
                            U.showToast("修改排课成功");
                            EventBus.getDefault().post(new ToUIEvent(ToUIEvent.TEACHER_REFERSH, false));
                            finish();
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        btnCheckConflict.setEnabled(true);
                        btnDereactCourse.setEnabled(true);
                        LogUtils.d("NewWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(MadifyCourseActivity.this);
                        } else if (statusCode == 1026 || statusCode == 1027) {//课程已经开始，不能修改这节课
                            U.showToast("课程已经开始，不能修改这节课!");
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("NewWorkActivity onError", throwable.getMessage());
                    }
                });
    }

    /**
     * Select start and end time of CourseTimeAdapter's item.
     */
    private void selectStartEndTime() {
        Calendar startDate = Calendar.getInstance();
        new TimePickerBuilder(this, (startTime, v) -> {
            String selectStartDate1 = DateUtil.getDate(startTime.getTime() / 1000, "yyyy年MM月dd日 HH:mm");
            String selectStartDate = DateUtil.getDate(startTime.getTime() / 1000, "yyyy-MM-dd-HH-mm");
            String[] split = selectStartDate.split("-");
            startDate.set(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1, Integer.parseInt(split[2]),//month从0开始
                    Integer.parseInt(split[3]), Integer.parseInt(split[4]));
            new TimePickerBuilder(this, (endTime, view) -> {
                String selectEndTime = DateUtil.getDate(endTime.getTime() / 1000, "HH:mm");
                Logger.d(selectStartDate + " ~ " + selectEndTime);

                // Traversal the timeList to find the duplicate element and delete it.
                String[] endArr = selectEndTime.split(":");
                int startHour = Integer.parseInt(split[3]);
                int startMinute = Integer.parseInt(split[4]);
                int endHour = Integer.parseInt(endArr[0]);
                int endMinute = Integer.parseInt(endArr[1]);
                if (startHour > endHour || (startHour == endHour && startMinute >= endMinute)) {
                    U.showToast("开始时间不能小于结束时间");
                    return;
                }
                addCourseTime(startTime, selectStartDate1, endTime, selectEndTime);
            }).setType(new boolean[]{false, false, false, true, true, false})
                    .setRangDate(startDate, startDate)
                    .setTitleText("选择结束时间")
                    .setLabel("", "", "", "时", "分", "")
                    .build().show();
        }).setType(new boolean[]{true, true, true, true, true, false})
                .setTitleText("选择开始时间")
                .setRangDate(startDate, null)
                .setLabel("年", "月", "日", "时", "分", "")
                .build().show();
    }

    /**
     * 添加上课时间段
     *
     * @param startTime
     * @param selectStartDate1
     * @param endTime
     * @param selectEndTime
     */
    private void addCourseTime(Date startTime, String selectStartDate1, Date endTime, String selectEndTime) {
        String startAt = String.valueOf(startTime.getTime() / 1000);
        String endAt = String.valueOf(endTime.getTime() / 1000);

        tvClassTimes.setText(String.format("%s~%s", selectStartDate1, selectEndTime));
        courseTime = String.format("%s,%s", startAt, endAt);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 3335) {//主讲教师
                mainTeachers = (List<UserEntity>) data.getSerializableExtra("UserEntity");
                rlMainTeacher.setVisibility(View.VISIBLE);
                ivAvatar.setTextAndColor(TextUtils.isEmpty(mainTeachers.get(0).getUserName()) ? "" :
                        mainTeachers.get(0).getUserName().substring(0, 1), getResources().getColor(R.color.b0b2b6));
                GlideUtils.setGlideImg(this, mainTeachers.get(0).getUserImage(), 0, ivAvatar);
                tvName.setText(mainTeachers.get(0).getUserName());
            }
            if (requestCode == 3395) {//助教
                teacherMenu.removeAllViews();//跳转之后清空
                assistantTeachers = (List<UserEntity>) data.getSerializableExtra("UserEntity");
                for (UserEntity assistant : assistantTeachers) {
                    showTeacherImage(assistant);
                }
            }
            if (requestCode == 3293) {//学员
                teacherMenu1.removeAllViews();//跳转之后清空
                studentLists = (List<UserEntity>) data.getSerializableExtra("UserEntity");
                for (UserEntity student : studentLists) {
                    showStudentImage(student);
                }
            }
        }
    }

    /**
     * 显示选择的教师
     */
    private void showTeacherImage(UserEntity userEntity) {
        // 包含TextView的LinearLayout
        // 参数设置
        LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(this).inflate(R.layout.header_item, null);
        AvatarImageView images = view.findViewById(R.id.iv_avatar);
        TextView tvname = view.findViewById(R.id.tv_name);
        menuLinerLayoutParames.setMargins(6, 0, 6, 0);
        // 设置id，方便后面删除
        view.setTag(userEntity);
        images.setImageResource(R.mipmap.ic_avatar_default);
        images.setTextAndColor(TextUtils.isEmpty(userEntity.getUserName()) ? "" :
                userEntity.getUserName().substring(0, 1), getResources().getColor(R.color.b0b2b6));
        GlideUtils.setGlideImg(this, userEntity.getUserImage(), 0, images);
        tvname.setText(userEntity.getUserName());

        teacherMenu.addView(view, menuLinerLayoutParames);
        horizonMenu.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
    }

    /**
     * 显示选择的学员
     */
    private void showStudentImage(UserEntity userEntity) {
        // 包含TextView的LinearLayout
        // 参数设置
        LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(this).inflate(R.layout.header_item, null);
        AvatarImageView images = view.findViewById(R.id.iv_avatar);
        TextView tvname = view.findViewById(R.id.tv_name);
        menuLinerLayoutParames.setMargins(6, 0, 6, 0);
        // 设置id，方便后面删除
        view.setTag(userEntity);
        images.setImageResource(R.mipmap.ic_avatar_default);
        images.setTextAndColor(TextUtils.isEmpty(userEntity.getUserName()) ? "" :
                userEntity.getUserName().substring(0, 1), getResources().getColor(R.color.b0b2b6));
        GlideUtils.setGlideImg(this, userEntity.getUserImage(), 0, images);
        tvname.setText(userEntity.getUserName());

        teacherMenu1.addView(view, menuLinerLayoutParames);
        horizonMenu1.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
    }

    /**
     * 教室操作弹框
     */
    private void showSchoolDialog() {
        String[] classRooms = new String[classRoomList.size()];
        for (int i = 0; i < classRoomList.size(); i++) {
            classRooms[i] = classRoomList.get(i).getClassroomName();
        }
        WheelPopup pop = new WheelPopup(this, classRooms);
        pop.showAtLocation(View.inflate(this, R.layout.item_color_course, null), Gravity
                .BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        pop.setSelectListener((argValue, position) -> {
            classroomId = classRoomList.get(position).getId();
            tvClassRoom.setText(argValue);
            return null;
        });
    }

    /**
     * 排课冲突提示
     *
     * @param data
     */
    private void showConflictDialog(CourseEntity data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_prompt_org, null);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText("排课冲突");
        TextView tvDesc = view.findViewById(R.id.tv_desc);
        TextView tv_ok = view.findViewById(R.id.tv_ok);
        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        if (data != null) {
            String startAt = data.getStartAt();
            String startDate = "";
            if (!TextUtils.isEmpty(startAt)) {
                startDate = DateUtil.getDate(Long.valueOf(startAt), "yyyy年MM月dd日 HH:mm") + "-";
            }
            String endAt = data.getEndAt();
            String endDate = "";
            if (!TextUtils.isEmpty(endAt)) {
                endDate = DateUtil.getDate(Long.valueOf(endAt), "HH:mm");
            }
            tvDesc.setText(Html.fromHtml(String.format("系统发现新建的排课%s与本机构%s的%s有冲突。" +
                            "<br/>冲突原因：%s<br/><font color='#AAAAAA'>（冲突课程可能不限于此）</font>",
                    courseName, startDate + endDate, data.getCourseName(), data.getConflict())));
            tv_cancel.setText("修改排课");
            tv_ok.setText("忽略冲突排入课表");
        } else {
            tvDesc.setText("未发现排课冲突");
            tv_ok.setText("排入课表");
        }
        tv_ok.setOnClickListener(v -> {
            // 修改这一节课
            modifyCourseLesson("2");
            dialog.dismiss();
        });
        tv_cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
    }

    private class CheckValadata {
        private boolean myResult;
        private String check;
        private String mainTeacher;
        private StringBuilder subTeachers;
        private StringBuilder students;
        private String roomId;

        CheckValadata(String check) {
            this.check = check;
        }

        boolean is() {
            return myResult;
        }

        String getCourseTime() {
            return courseTime;
        }

        String getMainTeacher() {
            return mainTeacher;
        }

        StringBuilder getSubTeachers() {
            return subTeachers;
        }

        StringBuilder getStudents() {
            return students;
        }

        String getClassroomId() {
            return classroomId;
        }

        CheckValadata invoke() {
            if (TextUtils.isEmpty(check) || TextUtils.isEmpty(orgId)) {
                LogUtils.d("NewWorkActivity", "check、orgId");
                myResult = true;
                return this;
            }
            if (TextUtils.isEmpty(courseTime)) {
                U.showToast("请选择排课时间");
                myResult = true;
                return this;
            }
            mainTeacher = null;
            if (mainTeachers != null && mainTeachers.size() > 0) {
                mainTeacher = mainTeachers.get(0).getUserId();
                if (TextUtils.isEmpty(mainTeacher)) {
                    U.showToast("请选择主讲教师");
                    myResult = true;
                    return this;
                }
            } else {
                U.showToast("请选择主讲教师");
                myResult = true;
                return this;
            }
            subTeachers = new StringBuilder();
            if (assistantTeachers != null && assistantTeachers.size() > 0) {
                for (int i = 0; i < assistantTeachers.size(); i++) {
                    UserEntity userEntity = assistantTeachers.get(i);
                    subTeachers.append(userEntity.getUserId());
                    if (i < assistantTeachers.size() - 1) {
                        subTeachers.append(",");
                    }
                }
            }
            roomId = classroomId;
            if (TextUtils.isEmpty(roomId)) {
                U.showToast("请选择教室");
                myResult = true;
                return this;
            }
            students = new StringBuilder();
            if (studentLists != null && studentLists.size() > 0) {
                for (int i = 0; i < studentLists.size(); i++) {
                    UserEntity userEntity = studentLists.get(i);
                    students.append(userEntity.getUserId());
                    if (i < studentLists.size() - 1) {
                        students.append(",");
                    }
                }
            }
            myResult = false;
            return this;
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
