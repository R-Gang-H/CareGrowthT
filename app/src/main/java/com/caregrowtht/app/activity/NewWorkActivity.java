package com.caregrowtht.app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.android.library.view.WheelPopup;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.R;
import com.caregrowtht.app.adapter.ClassTimeAdapter;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.BaseModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.OrgEntity;
import com.caregrowtht.app.model.TimeEntity;
import com.caregrowtht.app.model.UserEntity;
import com.caregrowtht.app.model.WorkClassEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.ColorWheelPopup;
import com.caregrowtht.app.view.picker.builder.TimePickerBuilder;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

import static com.caregrowtht.app.Constant.sexWeekly;

/**
 * haoruigang on 2018-8-20 16:46:17
 * 新建排课
 */
public class NewWorkActivity extends BaseActivity implements ViewOnItemClick {

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.rl_back_button)
    RelativeLayout rlBackButton;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_online_priview)
    CheckBox tvOnlinePriview;
    @BindView(R.id.et_course)
    EditText etCourse;
    @BindView(R.id.tv_course)
    TextView tvCourse;
    @BindView(R.id.tv_class)
    TextView tvClass;
    @BindView(R.id.et_class)
    EditText etClass;
    @BindView(R.id.et_after)
    EditText etAfter;
    @BindView(R.id.tv_after_end)
    TextView tvAfterEnd;
    @BindView(R.id.iv_course_icon)
    CardView ivCourseIcon;
    @BindView(R.id.tv_courseType)
    TextView tvCourseType;
    @BindView(R.id.rv_class_times)
    RecyclerView rvClassTimes;
    @BindView(R.id.tv_weekly)
    TextView tvWeekly;
    @BindView(R.id.et_end)
    TextView etEnd;
    @BindView(R.id.tv_of)
    TextView tvOf;
    @BindView(R.id.rl_mainTeacher)
    RelativeLayout rlMainTeacher;
    @BindView(R.id.iv_avatar)
    AvatarImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.horizonMenu)
    HorizontalScrollView horizonMenu;
    @BindView(R.id.teacherMenu)
    LinearLayout teacherMenu;
    @BindView(R.id.horizonMenu1)
    HorizontalScrollView horizonMenu1;
    @BindView(R.id.teacherMenu1)
    LinearLayout teacherMenu1;
    @BindView(R.id.tv_classRoom)
    TextView tvClassRoom;
    @BindView(R.id.tv_card_time)
    TextView rvCardTime;
    @BindView(R.id.tv_class_option)
    TextView tvClassOption;
    @BindView(R.id.btn_check_conflict)
    Button btnCheckConflict;
    @BindView(R.id.btn_dereact_course)
    Button btnDereactCourse;

    //课程种类
    List<CourseEntity> courseTypeList = new ArrayList<>();
    //机构的所有教室
    List<CourseEntity> classRoomList = new ArrayList<>();

    private List<UserEntity> mainTeachers = new ArrayList<>();
    private List<UserEntity> assistantTeachers = new ArrayList<>();
    private List<UserEntity> studentLists = new ArrayList<>();

    //选中课时卡的信息
    private List<CourseEntity> mCourseModels = new ArrayList<>();
    //上次选中课时卡的信息
    private List<CourseEntity> mEditCouModels = new ArrayList<>();
    //选中课时卡的消课次数
    private List<CourseEntity> mCount = new ArrayList<>();

    private ClassTimeAdapter classTimeAdapter;

    private String cards;
    private String classNum = "0";
    private String stuNum = "0";
    private String audiPrice;
    private String isOrder = "2";//是否允许约课 1：允许 2：不允许
    private String repeat = "1";//表示重复周期 1：无 2：每天 3：每周
    private String repeatEver = "2";//1：永远重复 2：不是永远重复
    private String repeatCount = "1";// 重复次数默认 1
    private String classroomId;
    private String classifyId = "0";
    private String courseName;
    private String orgId;
    private boolean updateAll = false;// true :修改今后所有排课
    private boolean createWork = false;// true :修改今后所有排课
    private String courseId;//课程id
    private String planId;//排课id

    private CourseEntity courseData = new CourseEntity<>();

    ArrayList<WorkClassEntity> workclassList = new ArrayList<>();
    StringBuffer sbCourse;
    private boolean isEtCourseSel = false;// 是否编辑过课程名称
    private String planView = "1";// 排课条件 1：教师 2：教室
    private MessageEntity msgEntity;

    @Override
    public int getLayoutId() {
        return R.layout.activity_new_work;
    }

    @Override
    public void initView() {
        ivLeft.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_close_1));
        updateAll = getIntent().getBooleanExtra("updateAll", false);
        msgEntity = (MessageEntity) getIntent().getSerializableExtra("msgEntity");
        if (msgEntity != null) {
            orgId = msgEntity.getOrgId();
            UserManager.getInstance().setOrgId(orgId);
        } else {
            orgId = UserManager.getInstance().getOrgId(); //getIntent().getStringExtra("orgId");
        }
        if (updateAll) {
            CourseEntity editCourseData = (CourseEntity) getIntent().getSerializableExtra("courseData");
            courseId = editCourseData.getCourseId();
            courseName = editCourseData.getCourseName();
            tvTitle.setText("修改今后所有排课");
            createWork = true;
            courseLessonDetails();
        } else {
            tvTitle.setText("新建排课");
            getInitInfo();
        }
        tvOnlinePriview.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isOrder = "1";
            } else {
                isOrder = "2";
            }
            tvOnlinePriview.setSelected(isChecked);
        });
        initRecyclerView(rvClassTimes, true);
        classTimeAdapter = new ClassTimeAdapter(new ArrayList(), this, this);
        rvClassTimes.setAdapter(classTimeAdapter);
        View.OnFocusChangeListener l = (v, hasFocus) -> {
            if (hasFocus) {
                isEtCourseSel = true;// 编辑
                tvCourse.setVisibility(View.VISIBLE);
                etClass.setVisibility(View.VISIBLE);
                tvClass.setVisibility(View.VISIBLE);
            }
        };
        etCourse.setOnFocusChangeListener(l);
        etClass.setOnFocusChangeListener(l);
    }

    private void getOrgSetting() {
        HttpManager.getInstance().doGetOrgSetting("NewWorkActivity", orgId,
                new HttpCallBack<BaseDataModel<OrgEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<OrgEntity> data) {
                        planView = data.getData().get(0).getPlan_view();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NewWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(NewWorkActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("NewWorkActivity onError", throwable.getMessage());
                    }
                });
    }

    private void getInitInfo() {
        getCoursesType();//38.获取机构的课程种类
        getClassroom();// 39.获取机构所有教室
        getOrgSetting();
    }

    @Override
    public void initData() {

    }

    private void courseLessonDetails() {
        //haoruigang on 2018-7-6 17:23:39 获取排课的详细信息
        HttpManager.getInstance().doGetLessonInfoV2("NewWorkActivity", orgId
                , courseId, "2", new HttpCallBack<BaseModel<CourseEntity<String, String, String>>>() {
                    @Override
                    public void onSuccess(BaseModel<CourseEntity<String, String, String>> data) {
                        getOrgExistCard();//31.获取机构现有的课时卡
                        getInitInfo();

                        courseData = data.getData();
                        setCourseData();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NewWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(NewWorkActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("NewWorkActivity Throwable", throwable.getMessage());
                    }
                });
    }

    private void setCourseData() {
        planId = courseData.getPlanId();//排课Id
        String confirmed = courseData.getConfirmed();
        if (confirmed.equals("1")) {
            isOrder = "1";
            tvOnlinePriview.setSelected(true);
        } else {
            isOrder = "2";
        }
        tvCourse.setVisibility(View.GONE);
        etClass.setVisibility(View.GONE);
        tvClass.setVisibility(View.GONE);
        etCourse.setText(courseName);

        String[] lesTimes = courseData.getLesTime().split("#");// 1548046810,1548050410#1548135027,1548138627
        for (String lesTime1 : lesTimes) {
            String[] lesTime = lesTime1.split(",");// 1548046810,1548050410
            String startAt = lesTime[0];
            String endAt = lesTime[1];
            TimeEntity timeEntity = new TimeEntity();
            timeEntity.setStartTime(startAt);
            timeEntity.setEndTime(endAt);
            classTimeAdapter.timeList.add(timeEntity);
            String selectStartDate1 = DateUtil.getDate(Long.valueOf(startAt), "yyyy年MM月dd日 HH:mm");
            String selectEndTime = DateUtil.getDate(Long.valueOf(endAt), "HH:mm");
            classTimeAdapter.classTimes.add(String.format("%s~%s", selectStartDate1, selectEndTime));
        }
        classTimeAdapter.notifyDataSetChanged();

        int repeat = Integer.valueOf(courseData.getRepeat()) - 1;//从1开始的所以减去1
        if (repeat < sexWeekly.length) {
            getRepeat(sexWeekly[repeat], repeat);
        } else {
            getRepeat(sexWeekly[0], 0);
        }
        Integer repeatEver = Integer.valueOf(courseData.getRepeatEver());
        if (repeatEver < Constant.sexWeekly1.length) {
            int position = 2;
            String repeatEndTime = courseData.getRepeatEndTime();
            String repeatCount = courseData.getRepeatCount();
            if (!TextUtils.isEmpty(repeatEndTime) &&
                    !TextUtils.equals(repeatEndTime, "0")) {
                position = 1;// "于日前"
                etAfter.setText(repeatEndTime);
            } else if (!TextUtils.isEmpty(repeatCount) &&
                    !TextUtils.equals(repeatCount, "0")) {
                position = 0;// "于"
                etAfter.setText(repeatCount);
            }
            getOf(Constant.sexWeekly1[position], position);// "于", "于日前","永不"(永远重复)
        } else {
            getOf(Constant.sexWeekly1[0], 0);
        }
        String teacherId = courseData.getTeacherId();//主教
        getNoticeHuman(teacherId, "1", "1");
        String teacherIds = courseData.getTeacherIds();//助教
        getNoticeHuman(teacherIds, "2", "1");
        String students = (String) courseData.getStudents();//学员
        getPlanStudents(students);//77.获取机构下的学员详情

        classNum = courseData.getMinCount();
        stuNum = courseData.getMaxCount();
        getClsStuNum();
    }

    /**
     * 设置已选中的课时卡
     */
    private void setCourCard() {
        //课时卡
        workclassList.clear();
        sbCourse = new StringBuffer();
        String OrgCardId = courseData.getOrgCardIds();
        String[] orgCardIds = TextUtils.isEmpty(OrgCardId) ?
                new String[]{} : OrgCardId.split(",");
        //遍历筛选上次已选中的OrgCardId课时卡
        for (int i = 0; i < orgCardIds.length; i++) {
            String orgCardId = orgCardIds[i];
            for (CourseEntity cards : mEditCouModels) {
                //上次已选中的课时卡
                if (TextUtils.equals(orgCardId, cards.getOrgCardId())) {
                    String unit = "次";
                    String courseCount;
                    //课时卡
                    WorkClassEntity workEntity = new WorkClassEntity();
                    workEntity.setOrgCardId(cards.getOrgCardId());

                    if (TextUtils.equals(cards.getCardType(), "1")) {
                        courseCount = ((CourseEntity) courseData.getCards().get(i)).getSingleTimes();
                        workEntity.setCount(courseCount);
                        workEntity.setPrice("");
                    } else {
                        unit = "元";
                        String relatilPrice = ((CourseEntity) courseData.getCards().get(i)).getSingleMoney();
                        courseCount = String.valueOf((Integer.valueOf(relatilPrice) / 100));
                        workEntity.setCount("");
                        workEntity.setPrice(relatilPrice);
                    }
                    workEntity.setCardType(cards.getCardType());
                    workclassList.add(workEntity);
                    mCourseModels.add(cards);
                    CourseEntity counts = new CourseEntity();
                    counts.setCourseCount(courseCount);
                    mCount.add(counts);
                    //消课次数或元
                    if (i > 0) {
                        sbCourse.append("\n");
                    }
                    String cardName = String.format("%s\t\t-%s%s",
                            cards.getCardName(), courseCount, unit);
                    sbCourse.append(cardName);
                    break;
                }
            }
        }
        rvCardTime.setText(sbCourse);
        cards = new Gson().toJson(workclassList);
        //课时卡
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
                        LogUtils.d("NewWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(NewWorkActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("NewWorkActivity onError", throwable.getMessage());
                    }
                });
    }

    private void getCoursesType() {
        //haoruigang on 2018-8-28 16:13:46 38.获取机构的课程种类
        HttpManager.getInstance().doGetCoursesType("NewWorkActivity",
                orgId, new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        courseTypeList.clear();
                        courseTypeList.addAll(data.getData());
                        if (updateAll) {// 修改今后排课
                            //课程种类
                            String lessionId = courseData.getLesCourseId();
                            for (CourseEntity classType : courseTypeList) {
                                if (TextUtils.equals(classType.getClassifyId(), lessionId)) {
                                    ivCourseIcon.setCardBackgroundColor(TextUtils.isEmpty(classType.getColor()) ?
                                            ResourcesUtils.getColor(R.color.blue) : Color.parseColor(classType.getColor()));
                                    classifyId = classType.getClassifyId();
                                    tvCourseType.setText(classType.getClassifyName());
                                    break;
                                }
                            }
                        } else {// 新建排课
                            if (courseTypeList.size() > 0) {
                                classifyId = courseTypeList.get(0).getClassifyId();
                                tvCourseType.setText(courseTypeList.get(0).getClassifyName());// 默认是第一个分类
                            }
                        }

                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NewWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(NewWorkActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("NewWorkActivity onError", throwable.getMessage());
                    }
                });
    }

    /**
     * 31.获取机构现有的课时卡
     * haoruigang on 2018-8-17 17:53:42
     */
    private void getOrgExistCard() {
        HttpManager.getInstance().doGetOrgCard("NewWorkActivity",
                UserManager.getInstance().getOrgId(), null,
                new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        mEditCouModels.clear();
                        mEditCouModels.addAll(data.getData());

                        setCourCard();//设置课时卡
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NewWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(NewWorkActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.tag(" throwable " + throwable);
                    }
                });
    }

    /**
     * 26.选择通知对象
     *
     * @param teacherId
     * @param teacherType 1：主教老师 2：助教老师
     * @param identity    //身份 1：老师 2：学员
     */
    private void getNoticeHuman(String teacherId, String teacherType, String identity) {
        HttpManager.getInstance().doGetNoticeHuman("NewWorkActivity",
                orgId, identity, new HttpCallBack<BaseDataModel<UserEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        boolean isShowTea = false;
                        String[] teacherIds = teacherId.split(",");//助教
                        for (String aTeacherIds : teacherIds) {
                            for (UserEntity mTeacher : data.getData()) {
                                if (TextUtils.equals(mTeacher.getUserId(), aTeacherIds)) {
                                    if (identity.equals("1")) {//教师
                                        if (teacherType.equals("1")) {
                                            //筛选选中的主教id
                                            mainTeachers.add(mTeacher);
                                            rlMainTeacher.setVisibility(View.VISIBLE);
                                            ivAvatar.setTextAndColor(TextUtils.isEmpty(mTeacher.getUserName()) ? "" : mTeacher.getUserName().substring(0, 1), getResources().getColor(R.color.b0b2b6));
                                            GlideUtils.setGlideImg(NewWorkActivity.this,
                                                    mTeacher.getUserImage(), 0, ivAvatar);
                                            tvName.setText(mTeacher.getUserName());
                                            break;
                                        } else {
                                            //筛选选中的助教id
                                            isShowTea = true;
                                            assistantTeachers.add(mTeacher);
                                        }
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
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NewWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(NewWorkActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("NewWorkActivity onError", throwable.getMessage());
                    }
                });
    }

    private void getClassroom() {
        //haoruigang on 2018-8-28 16:47:53 39.获取机构的所有教室
        HttpManager.getInstance().doGetClassroom("NewWorkActivity",
                orgId, new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        classRoomList.clear();
                        classRoomList.addAll(data.getData());

                        //教室
                        classroomId = courseData.getClassroomId();
                        for (CourseEntity classRoom : classRoomList) {
                            if (TextUtils.equals(classRoom.getClassroomId(), classroomId)) {
                                classroomId = classRoom.getClassroomId();
                                tvClassRoom.setText(classRoom.getClassroomName());
                                break;
                            }
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NewWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(NewWorkActivity.this);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("NewWorkActivity onError", throwable.getMessage());
                    }
                });
    }

    @OnClick({R.id.rl_back_button, R.id.et_course, R.id.tv_courseType, R.id.iv_class_time, R.id.tv_weekly, R.id.tv_of, R.id.et_after,
            R.id.iv_mainTeacher_arrow, R.id.iv_assistant_arrow, R.id.iv_school, R.id.iv_time_card,
            R.id.iv_student, R.id.iv_option, R.id.btn_check_conflict, R.id.btn_dereact_course})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back_button:
                finish();
                overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
                break;
            case R.id.et_course:
                isEtCourseSel = true;// 编辑
                tvCourse.setVisibility(View.VISIBLE);
                etClass.setVisibility(View.VISIBLE);
                tvClass.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_courseType:
                hideKeyboard();
                showCourseTypeDialog();
                break;
            case R.id.iv_class_time:
                hideKeyboard();
                selectStartEndTime(-1);
                break;
            case R.id.tv_weekly:
                hideKeyboard();
                classTimeAdapter.isTimeEdit = "1";
                selectRepeat();
                break;
            case R.id.tv_of:
                hideKeyboard();
                classTimeAdapter.isTimeEdit = "1";
                selectOf();
                break;
            case R.id.et_after:
                hideKeyboard();
                classTimeAdapter.isTimeEdit = "1";
                if (isShowOfTime)
                    selectOfTime();
                break;
            case R.id.iv_mainTeacher_arrow:
                startActivityForResult(new Intent(NewWorkActivity.this, TeacherSelectActivity.class)
                        .putExtra("teacherType", "1")
                        .putExtra("mainTeachers", (Serializable) mainTeachers), 3335);//教师类型 1：主讲教师 2：助教
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
                break;
            case R.id.iv_assistant_arrow:
                if (mainTeachers.size() == 0) {
                    U.showToast("请先选择主讲教师");
                    break;
                }
                startActivityForResult(new Intent(NewWorkActivity.this, TeacherSelectActivity.class)
                        .putExtra("teacherType", "2")
                        .putExtra("mainTeachers", (Serializable) mainTeachers)
                        .putExtra("assistantTeachers", (Serializable) assistantTeachers), 3395);//教师类型 1：主讲教师 2：助教
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
                break;
            case R.id.iv_school:
                showSchoolDialog();
                break;
            case R.id.iv_time_card:
                startActivityForResult(new Intent(this, TimeCardSelectActivity.class)
                        .putExtra("mCourseModels", (Serializable) mCourseModels)
                        .putExtra("CourseEntity", (Serializable) mCount), 5157);
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
                break;
            case R.id.iv_student:
                if (mCourseModels.size() == 0) {
                    U.showToast("请先选择课时卡");
                    return;
                }
                String orgCardIds = "";
                if (!createWork) {// 新建排课
                    boolean isOne = false;
                    StringBuilder orgCardId = new StringBuilder();
                    if (mCourseModels != null) {
                        for (CourseEntity cardId : mCourseModels) {
                            if (isOne) {
                                orgCardId.append(",");
                            }
                            orgCardId.append(cardId.getOrgCardId());
                            isOne = true;
                        }
                        orgCardIds = orgCardId.toString();
                    }
                } else { // 修改今后所有排课
                    orgCardIds = courseData.getOrgCardIds();
                }
                startActivityForResult(new Intent(this, FastenStudentActivity.class)
                        .putExtra("orgCardIds", orgCardIds)
                        .putExtra("studentList", (Serializable) studentLists), 3293);
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
                break;
            case R.id.iv_option:
                startActivityForResult(new Intent(this, OptionClassActivity.class)
                                .putExtra("classNum", classNum)
                                .putExtra("stuNum", stuNum)
                                .putExtra("audiPrice", audiPrice),
                        8533);
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
                break;
            case R.id.btn_check_conflict:
                btnCheckConflict.setEnabled(false);
                if (updateAll) {
                    editALes("1");
                } else {
                    addCoursePlan("1");
                }
                break;
            case R.id.btn_dereact_course:
                btnDereactCourse.setEnabled(false);
                if (updateAll) {
                    if (classTimeAdapter.isTimeEdit.equals("1")) {// 修改了排课的时间， 要严重提示一下
                        showWorkTimeDialog();
                    } else {
                        // 修改今后所有排课
                        editALes("2");
                    }
                } else {
                    addCoursePlan("2");
                }
                break;
        }
    }

    /**
     * 重要提示
     */
    private void showWorkTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_prompt_org, null);
        TextView tvDesc = view.findViewById(R.id.tv_desc);
        tvDesc.setText(R.string.text_update_work_time);
        TextView tv_ok = view.findViewById(R.id.tv_ok);
        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_ok.setOnClickListener(v -> {
            // 修改今后所有排课
            editALes("2");
            dialog.dismiss();
        });
        tv_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
    }

    /**
     * 41.新建排课/班级
     */
    private void addCoursePlan(String check) {//1:检查排课冲突 2：直接排入课表
        CheckValadata checkValadata = new CheckValadata(check).invoke();
        if (checkValadata.is()) {
            btnCheckConflict.setEnabled(true);
            btnDereactCourse.setEnabled(true);
            return;
        }
        String orgId = checkValadata.getOrgId();
        StringBuilder courseTime = checkValadata.getCourseTime();
        String mainTeacher = checkValadata.getMainTeacher();
        StringBuilder subTeachers = checkValadata.getSubTeachers();
        StringBuilder students = checkValadata.getStudents();
        String tryPrice = checkValadata.getTryPrice();
        String courseId = checkValadata.getCourseId();
        String classroomId = checkValadata.getClassroomId();
        String classNum = checkValadata.getClassNum();
        String stuNum = checkValadata.getStuNum();
        String repeatCount = checkValadata.getRepeatCount();
        HttpManager.getInstance().doAddCoursePlan("NewWorkActivity", check, orgId, isOrder,
                courseName, courseTime.toString(), repeat, repeatCount, isShowOfTime,
                repeatEver, mainTeacher, subTeachers.toString(), students.toString(), classroomId,
                classNum, stuNum, tryPrice, cards, classifyId, courseId,
                new HttpCallBack<BaseDataModel<CourseEntity>>(this) {
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
                            U.showToast("新建排课成功");
                            EventBus.getDefault().post(new ToUIEvent(ToUIEvent.TEACHER_REFERSH));
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
                            HttpManager.getInstance().dologout(NewWorkActivity.this);
                        } else if (statusCode == 1057) {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("NewWorkActivity onError", throwable.getMessage());
                    }
                });
    }

    private void editALes(String check) {//1:检查排课冲突 2：直接排入课表
        CheckValadata checkValadata = new CheckValadata(check).invoke();
        if (checkValadata.is()) {
            btnCheckConflict.setEnabled(true);
            return;
        }
        String orgId = checkValadata.getOrgId();
        StringBuilder courseTime = checkValadata.getCourseTime();
        String mainTeacher = checkValadata.getMainTeacher();
        StringBuilder subTeachers = checkValadata.getSubTeachers();
        StringBuilder students = checkValadata.getStudents();
        String tryPrice = checkValadata.getTryPrice();
        String courseId = checkValadata.getCourseId();
        String classroomId = checkValadata.getClassroomId();
        String classNum = checkValadata.getClassNum();
        String stuNum = checkValadata.getStuNum();
        String repeatCount = checkValadata.getRepeatCount();
        HttpManager.getInstance().doEditALesV2("NewWorkActivity", check, orgId, isOrder,
                courseName, courseTime.toString(), repeat, repeatCount, isShowOfTime,
                repeatEver, mainTeacher, subTeachers.toString(), students.toString(), classroomId,
                classNum, stuNum, tryPrice, cards, classifyId, classTimeAdapter.isTimeEdit, planId, courseId,
                new HttpCallBack<BaseDataModel<CourseEntity>>(this) {
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
                            U.showToast("修改成功");
                            EventBus.getDefault().post(new ToUIEvent(ToUIEvent.TEACHER_REFERSH));
                            finish();
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (TextUtils.equals(check, "1")) {
                            btnCheckConflict.setEnabled(true);
                        } else {
                            btnDereactCourse.setEnabled(true);
                        }
                        LogUtils.d("NewWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(NewWorkActivity.this);
                        } else if (statusCode == 1026 || statusCode == 1027) {//课程已经开始，不能修改这节课
                            U.showToast("课程已经开始，不能修改这节课!");
                        } else if (statusCode == 1067) {// 排课已结束
                            U.showToast("排课已结束!");
                        } else if (statusCode == 1001) {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("NewWorkActivity onError", throwable.getMessage());
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 3335) {//主讲教师
                mainTeachers = (List<UserEntity>) data.getSerializableExtra("UserEntity");
                rlMainTeacher.setVisibility(View.VISIBLE);
                ivAvatar.setTextAndColor(TextUtils.isEmpty(mainTeachers.get(0).getUserName()) ? "" : mainTeachers.get(0).getUserName().substring(0, 1), getResources().getColor(R.color.b0b2b6));
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
            if (requestCode == 5157) {//课时卡
                createWork = false;
                mCourseModels = (List<CourseEntity>) data.getSerializableExtra("CourseCardsEntity");
                mCount = (List<CourseEntity>) data.getSerializableExtra("Count");
                ArrayList<WorkClassEntity> workclassList = new ArrayList<>();
                for (int i = 0; i < mCourseModels.size(); i++) {
                    WorkClassEntity workEntity = new WorkClassEntity();
                    workEntity.setOrgCardId(mCourseModels.get(i).getOrgCardId());
                    if (!TextUtils.isEmpty(mCount.get(i).getCourseCount())) {
                        if (TextUtils.equals(mCourseModels.get(i).getCardType(), "1")) {
                            workEntity.setCount(mCount.get(i).getCourseCount());
                            workEntity.setPrice("");
                        } else {
                            workEntity.setCount("");
                            workEntity.setPrice(String.valueOf((Integer.valueOf(mCount.get(i).getCourseCount()) * 100)));
                        }
                        workEntity.setCardType(mCourseModels.get(i).getCardType());
                        workclassList.add(workEntity);
                    }
                }
                StringBuffer sbCourse = new StringBuffer();
                String cardType = null;
                for (int i = 0; i < mCourseModels.size(); i++) {
                    if (i > 0) {
                        sbCourse.append("\n");
                    }
                    String cardName = null;
                    if (mCourseModels != null && mCourseModels.size() > 0) {
                        cardType = mCourseModels.get(i).getCardType();
                    }
                    String unit = "次";
                    if (!TextUtils.equals(cardType, "1")) {
                        unit = "元";
                    }
                    if (mCourseModels != null && mCourseModels.size() > 0) {
                        cardName = String.format("%s\t\t-%s%s",
                                mCourseModels.get(i).getCardName(), mCount.get(i).getCourseCount(), unit);
                    }
                    sbCourse.append(cardName);
                }
                rvCardTime.setText(sbCourse);
                cards = new Gson().toJson(workclassList);
            }
            if (requestCode == 3293) {//学员
                teacherMenu1.removeAllViews();//跳转之后清空
                studentLists = (List<UserEntity>) data.getSerializableExtra("UserEntity");
                for (UserEntity student : studentLists) {
                    showStudentImage(student);
                }
            }
            if (requestCode == 8533) {
                classNum = data.getStringExtra("classNum");
                stuNum = data.getStringExtra("stuNum");
                audiPrice = data.getStringExtra("audiPrice");
                getClsStuNum();
            }
        }
    }

    private void getClsStuNum() {
        StringBuffer sbClass = new StringBuffer();
        if (!TextUtils.isEmpty(classNum)) {
            sbClass.append(String.format("班级最少\t%s\t名学员开课", classNum));
        }
        if (!TextUtils.isEmpty(stuNum)) {
            sbClass.append(String.format("\n最多容纳\t%s\t名学员", stuNum));
        }
        if (!TextUtils.isEmpty(audiPrice)) {
            sbClass.append(String.format("\n试听\t%s\t元", audiPrice));
        }
        tvClassOption.setText(sbClass);
    }

    /**
     * 课程种类操作弹框(自定义)
     */
    private void showCourseTypeDialog() {
        ColorWheelPopup pop = new ColorWheelPopup(NewWorkActivity.this, courseTypeList);
        pop.showAtLocation(View.inflate(NewWorkActivity.this, R.layout.item_color_course, null), Gravity
                .BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        pop.setSelectListener((argValue, position) -> {
            ivCourseIcon.setCardBackgroundColor(TextUtils.isEmpty(argValue.getColor()) ?
                    ResourcesUtils.getColor(R.color.blue) : Color.parseColor(argValue.getColor()));
            classifyId = courseTypeList.get(position).getClassifyId();
            tvCourseType.setText(argValue.getClassifyName());
        });
    }

    /**
     * Select start and end time of CourseTimeAdapter's item.
     */
    private void selectStartEndTime(int position) {
        Calendar startDate = Calendar.getInstance();
        new TimePickerBuilder(this, (startTime, v) -> {
            if (startTime.getTime() < TimeUtils.getCurTimeLong()) {
                U.showToast("开始时间不能小于当前时间");
            } else {
                String selectStartDate1 = DateUtil.getDate(startTime.getTime() / 1000, "yyyy年MM月dd日 HH:mm");
                String selectStartDate = DateUtil.getDate(startTime.getTime() / 1000, "yyyy-MM-dd-HH-mm");
                String[] split = selectStartDate.split("-");
                startDate.set(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1, Integer.parseInt(split[2]),//month从0开始
                        Integer.parseInt(split[3]), Integer.parseInt(split[4]));
                new TimePickerBuilder(this, (endTime, view) -> {
                    if (endTime.getTime() < startTime.getTime()) {
                        U.showToast("结束时间不能小于当前时间");
                    } else {
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
                        addCourseTime(position, startTime, selectStartDate1, endTime, selectEndTime);
                    }
                }).setType(new boolean[]{false, false, false, true, true, false})
                        .setRangDate(startDate, startDate)
                        .setTitleText("选择结束时间")
                        .setLabel("", "", "", "时", "分", "")
                        .build().show();
            }
        }).setType(new boolean[]{true, true, true, true, true, false})
                .setTitleText("选择开始时间")
                .setRangDate(startDate, null)
                .setLabel("年", "月", "日", "时", "分", "")
                .build().show();
    }

    /**
     * 添加上课时间段
     *
     * @param position
     * @param startTime
     * @param selectStartDate1
     * @param endTime
     * @param selectEndTime
     */
    private void addCourseTime(int position, Date startTime, String selectStartDate1, Date endTime, String selectEndTime) {
        TimeEntity timeEntity = new TimeEntity();
        timeEntity.setStartTime(String.valueOf(startTime.getTime() / 1000));
        timeEntity.setEndTime(String.valueOf(endTime.getTime() / 1000));
        if (position >= 0) {
            classTimeAdapter.isTimeEdit = "1";// 编辑了课程
            classTimeAdapter.timeList.remove(position);
            classTimeAdapter.timeList.add(position, timeEntity);

            classTimeAdapter.classTimes.remove(position);
            classTimeAdapter.classTimes.add(position, String.format("%s~%s", selectStartDate1, selectEndTime));
        } else {
            if (updateAll) {// 修改今后所有排课
                classTimeAdapter.isTimeEdit = "1";// 编辑了课程
            }
            classTimeAdapter.timeList.add(timeEntity);

            classTimeAdapter.classTimes.add(String.format("%s~%s", selectStartDate1, selectEndTime));
        }
        classTimeAdapter.notifyDataSetChanged();
    }

    /**
     * 重复 "无", "每天", "每周"
     */
    boolean isShowAfter = true;//首次是否显示几次后结束

    private void selectRepeat() {
        WheelPopup pop = new WheelPopup(this, sexWeekly);
        pop.showAtLocation(View.inflate(this, R.layout.item_color_course, null),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        pop.setSelectListener((argValue, position) -> {
            getRepeat(argValue, position);
            return null;
        });
    }

    /**
     * 重复周期
     *
     * @param argValue
     * @param position
     */
    private void getRepeat(String argValue, int position) {
        switch (position) {
            case 0:
                etEnd.setVisibility(View.GONE);
                tvOf.setVisibility(View.GONE);
                etAfter.setVisibility(View.GONE);
                tvAfterEnd.setVisibility(View.GONE);
                etAfter.setText("");
                break;
            case 1:
            case 2:
            case 3:
                etEnd.setVisibility(View.VISIBLE);
                tvOf.setVisibility(View.VISIBLE);
                etAfter.setVisibility(View.VISIBLE);
                tvAfterEnd.setVisibility(View.VISIBLE);
                if (isShowAfter) {//首次默认2：于
                    repeatEver = "2";
                    break;
                }
        }
        repeat = position + 1 + "";
        tvWeekly.setText(argValue);
    }

    /**
     * 重复 "于", "于日前", "永不"
     */
    boolean isShowOfTime = false;// 是否显示选择时间弹框

    private void selectOf() {
        WheelPopup pop = new WheelPopup(this, Constant.sexWeekly1);
        pop.showAtLocation(View.inflate(this, R.layout.item_color_course, null), Gravity
                .BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        pop.setSelectListener((argValue, position) -> {
            etAfter.setText("");
            getOf(argValue, position);
            return null;
        });
    }

    private void getOf(String argValue, int position) {
        isShowAfter = false;//选择之后不显示
        switch (position) {
            case 0:
                isShowOfTime = false;
                repeatEver = "2";
                etAfter.setVisibility(View.VISIBLE);
                etAfter.setFocusable(true);
                etAfter.setFocusableInTouchMode(true);
                if (!updateAll) {
                    etAfter.setText("1");
                }
                tvAfterEnd.setVisibility(View.VISIBLE);
                break;
            case 1:
                isShowOfTime = true;
                repeatEver = "2";
                etAfter.setVisibility(View.VISIBLE);
                etAfter.setFocusable(false);
                if (!updateAll) {
                    etAfter.setText(DateUtil.getSysTimeType("yyyy/MM/dd"));
                }
                tvAfterEnd.setVisibility(View.GONE);
                break;
            case 2:
                repeatEver = "1";
                etAfter.setText("");
                etAfter.setVisibility(View.GONE);
                tvAfterEnd.setVisibility(View.GONE);
                break;
        }
        tvOf.setText(argValue);
    }

    /**
     * 结束于时间
     */
    private void selectOfTime() {
        Calendar startDate = Calendar.getInstance();
        new TimePickerBuilder(this, (startTime, v) -> {
            String selectStartDate = DateUtil.getDate(startTime.getTime() / 1000, "yyyy/MM/dd");
            etAfter.setText(selectStartDate);
        }).setType(new boolean[]{true, true, true, false, false, false})
                .setTitleText("选择时间")
                .setRangDate(startDate, null)
                .setLabel("年", "月", "日", "", "", "")
                .build().show();
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
        if (classRoomList.size() > 0) {
            String[] classRooms = new String[classRoomList.size()];
            for (int i = 0; i < classRoomList.size(); i++) {
                classRooms[i] = classRoomList.get(i).getClassroomName();
            }
            WheelPopup pop = new WheelPopup(this, classRooms);
            pop.showAtLocation(View.inflate(this, R.layout.item_color_course, null), Gravity
                    .BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            pop.setSelectListener((argValue, position) -> {
                if (!TextUtils.isEmpty(argValue) && !TextUtils.isEmpty(classRoomList.get(position).getClassroomId())) {
                    classroomId = classRoomList.get(position).getClassroomId();
                    tvClassRoom.setText(argValue);
                }
                return null;
            });
        } else {
            U.showToast("该机构暂无教室");
            return;
        }
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
            tvDesc.setText(Html.fromHtml(String.format("系统发现排课%s与本机构%s的%s有冲突。" +
                            "<br/>冲突原因：%s<br/><font color='#AAAAAA'>（冲突课程可能不限于此）</font>",
                    courseName, startDate + endDate, data.getCourseName(), data.getConflict())));
            tv_cancel.setText("修改排课");
            tv_ok.setText("忽略冲突排入课表");
        } else {
            tvDesc.setText("未发现排课冲突");
            tv_ok.setText("排入课表");
        }
        tv_ok.setOnClickListener(v -> {
            dialog.dismiss();
            if (updateAll) {
                if (classTimeAdapter.isTimeEdit.equals("1")) {// 修改了排课的时间， 要严重提示一下
                    showWorkTimeDialog();
                } else {
                    // 修改今后所有排课
                    editALes("2");
                }
            } else {
                addCoursePlan("2");
            }
        });
        tv_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
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

    @Override
    public void setOnItemClickListener(View view, int postion) {
        selectStartEndTime(postion);
    }

    private class CheckValadata {
        private boolean myResult;
        private String check;
        private String orgId;
        private StringBuilder courseTime;
        private String mainTeacher;
        private StringBuilder subTeachers;
        private StringBuilder students;
        private String tryPrice;
        private String courseId;

        CheckValadata(String check) {
            this.check = check;
        }

        boolean is() {
            return myResult;
        }

        public String getOrgId() {
            return orgId;
        }

        StringBuilder getCourseTime() {
            return courseTime;
        }

        String getRepeatCount() {
            if (repeatEver.equals("1")) {// 永不结束
                return "0";
            }
            String rpCount = etAfter.getText().toString().trim();
            if (TextUtils.isEmpty(rpCount)) {
                return repeatCount;
            }
            return rpCount;
        }

        String getMainTeacher() {
            return mainTeacher;
        }

        StringBuilder getSubTeachers() {
            return subTeachers;
        }

        String getClassroomId() {
            return classroomId;
        }

        String getClassNum() {
            return classNum;
        }

        String getStuNum() {
            return stuNum;
        }

        StringBuilder getStudents() {
            return students;
        }

        String getTryPrice() {
            return tryPrice;
        }

        String getCourseId() {
            courseId = TextUtils.isEmpty(courseId) ? "0" : courseId;
            return courseId;
        }

        CheckValadata invoke() {
            orgId = UserManager.getInstance().getOrgId();
            if (TextUtils.isEmpty(check) || TextUtils.isEmpty(orgId) || TextUtils.isEmpty(isOrder)) {
                LogUtils.d("NewWorkActivity", "check、orgId、isOrder");
                myResult = true;
                return this;
            }
            String courseC = etCourse.getText().toString();
            String classB = etClass.getText().toString();
            if (isEtCourseSel) {
                if (TextUtils.isEmpty(courseC) && TextUtils.isEmpty(classB)) {
                    U.showToast("请输入排课名称");
                    myResult = true;
                    return this;
                } else if (TextUtils.isEmpty(courseC)) {
                    courseName = String.format("%s班", classB).trim();
                } else if (TextUtils.isEmpty(classB)) {
                    courseName = String.format("%s课", courseC).trim();
                } else {
                    courseName = String.format("%s课%s班", courseC, classB).trim();
                }
            }
            courseTime = new StringBuilder();
            for (int i = 0; i < classTimeAdapter.timeList.size(); i++) {
                TimeEntity timeEntity = classTimeAdapter.timeList.get(i);
                courseTime.append(String.format("%s,%s", timeEntity.getStartTime(), timeEntity.getEndTime()));
                if (i < classTimeAdapter.timeList.size() - 1) {
                    courseTime.append("#");
                }
            }
            if (TextUtils.isEmpty(courseTime)) {
                U.showToast("请选择排课时间");
                myResult = true;
                return this;
            }
            mainTeacher = null;
            if (mainTeachers != null && mainTeachers.size() > 0) {
                mainTeacher = mainTeachers.get(0).getUserId();
                if (planView.equals("1")) {// 教师不能为空
                    if (TextUtils.isEmpty(mainTeacher)) {
                        U.showToast("请选择主讲教师");
                        myResult = true;
                        return this;
                    }
                }
            } else {
                if (planView.equals("1")) {// 教师不能为空
                    U.showToast("请选择主讲教师");
                    myResult = true;
                    return this;
                }
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
            if (planView.equals("2")) {// 教室不能为空
                if (TextUtils.isEmpty(classroomId)) {
                    U.showToast("请选择教室");
                    myResult = true;
                    return this;
                }
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
            tryPrice = null;
            if (!TextUtils.isEmpty(audiPrice)) {
                tryPrice = String.valueOf(Integer.valueOf(audiPrice) * 1000);
            }
            if (classNum.equals("0") || TextUtils.isEmpty(classNum)) {
                classNum = "1";// 默认 最小限制人数1
            }
            if (stuNum.equals("0") || TextUtils.isEmpty(stuNum)) {
                stuNum = "50";// 默认 最大限制人数50
            }
            myResult = false;
            return this;
        }
    }
}
