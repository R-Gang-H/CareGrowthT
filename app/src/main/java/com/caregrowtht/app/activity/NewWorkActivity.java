package com.caregrowtht.app.activity;

import android.app.Activity;
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

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

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

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * haoruigang on 2018-8-20 16:46:17
 * ????????????
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
    @BindView(R.id.tv_mainTeacher)
    TextView tvMainTeacher;
    @BindView(R.id.tv_school)
    TextView tvSchool;
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

    //????????????
    List<CourseEntity> courseTypeList = new ArrayList<>();
    //?????????????????????
    List<CourseEntity> classRoomList = new ArrayList<>();

    private List<UserEntity> mainTeachers = new ArrayList<>();
    private List<UserEntity> assistantTeachers = new ArrayList<>();
    private List<UserEntity> studentLists = new ArrayList<>();

    //????????????????????????
    private List<CourseEntity> mCourseModels = new ArrayList<>();
    //??????????????????????????????
    private List<CourseEntity> mEditCouModels = new ArrayList<>();
    //??????????????????????????????
    private List<CourseEntity> mCount = new ArrayList<>();

    private ClassTimeAdapter classTimeAdapter;

    private String cards;
    private String classNum = "0";
    private String stuNum = "0";
    private String audiPrice;
    private String isOrder = "2";//?????????????????? 1????????? 2????????????
    private String repeat = "1";//?????????????????? 1?????? 2????????? 3?????????
    private String repeatEver = "2";//1??????????????? 2?????????????????????
    private String repeatCount = "1";// ?????????????????? 1
    private String classroomId;
    private String classifyId = "0";
    private String courseName;
    private String orgId;
    private boolean updateAll = false;// true :????????????????????????
    private boolean createWork = false;// true :????????????????????????
    private String courseId;//??????id
    private String planId;//??????id

    private CourseEntity courseData = new CourseEntity<>();

    ArrayList<WorkClassEntity> workclassList = new ArrayList<>();
    StringBuffer sbCourse;
    private boolean isEtCourseSel = false;// ???????????????????????????
    private String planView = "1";// ???????????? 1????????? 2?????????
    private MessageEntity msgEntity;
    private String ifEditStu = "1";// ?????????2, 1????????????2?????????

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
            orgId = UserManager.getInstance().getOrgId();
        }
        if (updateAll) {
            CourseEntity editCourseData = (CourseEntity) getIntent().getSerializableExtra("courseData");
            courseId = editCourseData.getCourseId();
            courseName = editCourseData.getCourseName();
            tvTitle.setText("????????????????????????");
            createWork = true;
            courseLessonDetails();
        } else {
            tvTitle.setText("????????????");
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
                isEtCourseSel = true;// ??????
                tvCourse.setVisibility(View.VISIBLE);
                etClass.setVisibility(View.VISIBLE);
                tvClass.setVisibility(View.VISIBLE);
            }
        };
        etCourse.setOnFocusChangeListener(l);
        etClass.setOnFocusChangeListener(l);
        etAfter.setOnFocusChangeListener((v, hasFocus) -> {
            classTimeAdapter.isTimeEdit = "1";
        });
    }

    private void getOrgSetting() {
        HttpManager.getInstance().doGetOrgSetting("NewWorkActivity", orgId,
                new HttpCallBack<BaseDataModel<OrgEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<OrgEntity> data) {
                        planView = data.getData().get(0).getPlan_view();
                        if (planView.equals("1")) {// ???????????? 1????????? 2?????????
                            tvMainTeacher.setText("????????????*");
                        } else {
                            tvSchool.setText("??????*");
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NewWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
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
        getCoursesType();//38.???????????????????????????
        getClassroom();// 39.????????????????????????
        getOrgSetting();
    }

    @Override
    public void initData() {

    }

    private void courseLessonDetails() {
        //haoruigang on 2018-7-6 17:23:39 ???????????????????????????
        HttpManager.getInstance().doGetLessonInfoV2("NewWorkActivity", orgId
                , courseId, "2", new HttpCallBack<BaseModel<CourseEntity<String, String, String>>>() {
                    @Override
                    public void onSuccess(BaseModel<CourseEntity<String, String, String>> data) {
                        getOrgExistCard();//31.??????????????????????????????
                        getInitInfo();

                        courseData = data.getData();
                        setCourseData();
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NewWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
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
        planId = courseData.getPlanId();//??????Id
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
            String selectStartDate1 = DateUtil.getDate(Long.valueOf(startAt), "yyyy???MM???dd??? HH:mm");
            String selectEndTime = DateUtil.getDate(Long.valueOf(endAt), "HH:mm");
            classTimeAdapter.classTimes.add(String.format("%s~%s", selectStartDate1, selectEndTime));
        }
        classTimeAdapter.notifyDataSetChanged();

        String[] sexWeekly;
        if (UserManager.getInstance().removeDuplicateOrder(
                classTimeAdapter.classTimes).size() > 1) {// ??????????????????????????????
            sexWeekly = Constant.sexWeekly0;
        } else {
            sexWeekly = Constant.sexWeekly;
        }
        int repeat = Integer.valueOf(courseData.getRepeat()) - 1;//???1?????????????????????1
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
                position = 1;// "?????????"
                etAfter.setText(repeatEndTime);
            } else if (!TextUtils.isEmpty(repeatCount) &&
                    !TextUtils.equals(repeatCount, "0")) {
                position = 0;// "???"
                etAfter.setText(repeatCount);
            }
            getOf(Constant.sexWeekly1[position], position);// "???", "?????????","??????"(????????????)
        } else {
            getOf(Constant.sexWeekly1[0], 0);
        }
        String teacherId = courseData.getTeacherId();//??????
        getNoticeHuman(teacherId, "1", "1");
        String teacherIds = courseData.getTeacherIds();//??????
        getNoticeHuman(teacherIds, "2", "1");
        String students = (String) courseData.getStudents();//??????
        getPlanStudents(students);//77.??????????????????????????????

        classNum = courseData.getMinCount();
        stuNum = courseData.getMaxCount();
        getClsStuNum();
    }

    /**
     * ???????????????????????????
     */
    private void setCourCard() {
        //?????????
        workclassList.clear();
        sbCourse = new StringBuffer();
        String OrgCardId = courseData.getOrgCardIds();
        String[] orgCardIds = TextUtils.isEmpty(OrgCardId) ?
                new String[]{} : OrgCardId.split(",");
        //??????????????????????????????OrgCardId?????????
        for (int i = 0; i < orgCardIds.length; i++) {
            String orgCardId = orgCardIds[i];
            for (CourseEntity cards : mEditCouModels) {
                //???????????????????????????
                if (TextUtils.equals(orgCardId, cards.getOrgCardId())) {
                    String unit = "???";
                    String courseCount;
                    //?????????
                    WorkClassEntity workEntity = new WorkClassEntity();
                    workEntity.setOrgCardId(cards.getOrgCardId());

                    if (TextUtils.equals(cards.getCardType(), "1")) {
                        courseCount = String.valueOf(((CourseEntity) courseData.getCards().get(i)).getSingleTimes());
                        workEntity.setCount(courseCount);
                        workEntity.setPrice("");
                    } else {
                        unit = "???";
                        String relatilPrice = String.valueOf(((CourseEntity) courseData.getCards().get(i)).getSingleMoney());
                        courseCount = String.valueOf(Double.valueOf(relatilPrice) / 100);
                        workEntity.setCount("");
                        workEntity.setPrice(relatilPrice);
                    }
                    workEntity.setCardType(cards.getCardType());
                    workclassList.add(workEntity);
                    mCourseModels.add(cards);
                    CourseEntity counts = new CourseEntity();
                    counts.setCourseCount(courseCount);
                    mCount.add(counts);
                    //??????????????????
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
        //?????????
    }

    /**
     * 77.??????????????????????????????
     */
    private void getPlanStudents(String students) {
        HttpManager.getInstance().doGetPlanStudents("NewWorkActivity",
                orgId, students, new HttpCallBack<BaseDataModel<UserEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        //?????????????????????id
                        studentLists.addAll(data.getData());
                        //??????????????????
                        for (UserEntity student : studentLists) {
                            showStudentImage(student);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NewWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
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
        //haoruigang on 2018-8-28 16:13:46 38.???????????????????????????
        HttpManager.getInstance().doGetCoursesType("NewWorkActivity",
                orgId, new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        courseTypeList.clear();
                        courseTypeList.addAll(data.getData());
                        if (updateAll) {// ??????????????????
                            //????????????
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
                        } else {// ????????????
                            if (courseTypeList.size() > 0) {
                                classifyId = courseTypeList.get(0).getClassifyId();
                                tvCourseType.setText(courseTypeList.get(0).getClassifyName());// ????????????????????????
                            }
                        }

                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NewWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
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
     * 31.??????????????????????????????
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

                        setCourCard();//???????????????
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NewWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
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
     * 26.??????????????????
     *
     * @param teacherId
     * @param teacherType 1??????????????? 2???????????????
     * @param identity    //?????? 1????????? 2?????????
     */
    private void getNoticeHuman(String teacherId, String teacherType, String identity) {
        HttpManager.getInstance().doGetNoticeHuman("NewWorkActivity",
                orgId, identity, new HttpCallBack<BaseDataModel<UserEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        boolean isShowTea = false;
                        String[] teacherIds = teacherId.split(",");//??????
                        for (String aTeacherIds : teacherIds) {
                            for (UserEntity mTeacher : data.getData()) {
                                if (TextUtils.equals(mTeacher.getUserId(), aTeacherIds)) {
                                    if (identity.equals("1")) {//??????
                                        if (teacherType.equals("1")) {
                                            //?????????????????????id
                                            mainTeachers.add(mTeacher);
                                            rlMainTeacher.setVisibility(View.VISIBLE);
                                            ivAvatar.setTextAndColor(TextUtils.isEmpty(mTeacher.getUserName()) ? "" : mTeacher.getUserName().substring(0, 1), getResources().getColor(R.color.b0b2b6));
                                            GlideUtils.setGlideImg(NewWorkActivity.this,
                                                    mTeacher.getUserImage(), 0, ivAvatar);
                                            tvName.setText(mTeacher.getUserName());
                                            break;
                                        } else {
                                            //?????????????????????id
                                            isShowTea = true;
                                            assistantTeachers.add(mTeacher);
                                        }
                                    }
                                }
                            }
                        }
                        //??????????????????
                        if (isShowTea) {
                            for (UserEntity assistant : assistantTeachers) {
                                showTeacherImage(assistant);
                            }
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NewWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
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
        //haoruigang on 2018-8-28 16:47:53 39.???????????????????????????
        HttpManager.getInstance().doGetClassroom("NewWorkActivity",
                orgId, new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        classRoomList.clear();
                        classRoomList.addAll(data.getData());

                        if (updateAll) {
                            //??????
                            classroomId = courseData.getClassroomId();
                            for (CourseEntity classRoom : classRoomList) {
                                if (TextUtils.equals(classRoom.getId(), classroomId)) {
                                    classroomId = classRoom.getId();
                                    tvClassRoom.setText(classRoom.getClassroomName());
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("NewWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
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
                isEtCourseSel = true;// ??????
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
                        .putExtra("mainTeachers", (Serializable) mainTeachers), 3335);//???????????? 1??????????????? 2?????????
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//??????????????????
                break;
            case R.id.iv_assistant_arrow:
                if (mainTeachers.size() == 0) {
                    U.showToast("????????????????????????");
                    break;
                }
                startActivityForResult(new Intent(NewWorkActivity.this, TeacherSelectActivity.class)
                        .putExtra("teacherType", "2")
                        .putExtra("mainTeachers", (Serializable) mainTeachers)
                        .putExtra("assistantTeachers", (Serializable) assistantTeachers), 3395);//???????????? 1??????????????? 2?????????
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//??????????????????
                break;
            case R.id.iv_school:
                showSchoolDialog();
                break;
            case R.id.iv_time_card:
                startActivityForResult(new Intent(this, TimeCardSelectActivity.class)
                        .putExtra("mCourseModels", (Serializable) mCourseModels)
                        .putExtra("CourseEntity", (Serializable) mCount), 5157);
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//??????????????????
                break;
            case R.id.iv_student:
                if (mCourseModels.size() == 0) {
                    U.showToast("?????????????????????");
                    return;
                }
                String orgCardIds = "";
                if (!createWork) {// ????????????
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
                } else { // ????????????????????????
                    ifEditStu = "2";
                    orgCardIds = courseData.getOrgCardIds();
                }
                startActivityForResult(new Intent(this, FastenStudentActivity.class)
                        .putExtra("orgCardIds", orgCardIds)
                        .putExtra("studentList", (Serializable) studentLists), 3293);
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//??????????????????
                break;
            case R.id.iv_option:
                startActivityForResult(new Intent(this, OptionClassActivity.class)
                                .putExtra("classNum", classNum)
                                .putExtra("stuNum", stuNum)
                                .putExtra("audiPrice", audiPrice),
                        8533);
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//??????????????????
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
                    if (classTimeAdapter.isTimeEdit.equals("1")) {// ??????????????????????????? ?????????????????????
                        showWorkTimeDialog(getResources().getString(R.string.text_update_time_rule));
//                    } else if (classTimeAdapter.isTimeEdit.equals("0") && ifEditStu.equals("2")) {// ??????????????????????????????
//                        showWorkTimeDialog(getResources().getString(R.string.stu_ifeditstu));
                    } else {
                        // ????????????????????????
                        editALes("2");
                    }
                } else {
                    addCoursePlan("2");
                }
                break;
        }
    }

    /**
     * ????????????
     */
    private void showWorkTimeDialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_prompt_org, null);
        TextView tvDesc = view.findViewById(R.id.tv_desc);
        tvDesc.setText(text);
        TextView tv_ok = view.findViewById(R.id.tv_ok);
        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_ok.setOnClickListener(v -> {
            // ????????????????????????
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
     * 41.????????????/??????
     */
    private void addCoursePlan(String check) {//1:?????????????????? 2?????????????????????
        CheckValadata checkValadata = new CheckValadata(check).invoke();
        if (checkValadata.is()) {
            btnCheckConflict.setEnabled(true);
            btnDereactCourse.setEnabled(true);
            return;
        }
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
                            if (data != null && data.getData().size() > 0) {// ????????????????????????
                                showConflictDialog(data.getData().get(0));
                            } else {
                                U.showToast("??????????????????");
                                EventBus.getDefault().post(new ToUIEvent(ToUIEvent.TEACHER_REFERSH, msgEntity != null));// ??????????????????????????????OrgId ture ???????????? , false ??????????????????
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        btnCheckConflict.setEnabled(true);
                        btnDereactCourse.setEnabled(true);
                        LogUtils.d("NewWorkActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
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

    private void editALes(String check) {//1:?????????????????? 2?????????????????????
        CheckValadata checkValadata = new CheckValadata(check).invoke();
        if (checkValadata.is()) {
            btnCheckConflict.setEnabled(true);
            btnDereactCourse.setEnabled(true);
            return;
        }
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
                classNum, stuNum, tryPrice, cards, classifyId, classTimeAdapter.isTimeEdit, planId,
                courseId, ifEditStu,
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
                            if (data != null && data.getData().size() > 0) {// ????????????????????????
                                showConflictDialog(data.getData().get(0));
                            } else {
                                U.showToast("????????????");
                                EventBus.getDefault().post(new ToUIEvent(ToUIEvent.TEACHER_REFERSH, false));
                                finish();
                            }
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
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(NewWorkActivity.this);
                        } else if (statusCode == 1026 || statusCode == 1027) {//??????????????????????????????????????????
                            U.showToast("??????????????????????????????????????????!");
                        } else if (statusCode == 1067) {// ???????????????
                            U.showToast("???????????????!");
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
            if (requestCode == 3335) {//????????????
                mainTeachers = (List<UserEntity>) data.getSerializableExtra("UserEntity");
                rlMainTeacher.setVisibility(View.VISIBLE);
                ivAvatar.setTextAndColor(TextUtils.isEmpty(mainTeachers.get(0).getUserName()) ? "" : mainTeachers.get(0).getUserName().substring(0, 1), getResources().getColor(R.color.b0b2b6));
                GlideUtils.setGlideImg(this, mainTeachers.get(0).getUserImage(), 0, ivAvatar);
                tvName.setText(mainTeachers.get(0).getUserName());
            }
            if (requestCode == 3395) {//??????
                teacherMenu.removeAllViews();//??????????????????
                assistantTeachers = (List<UserEntity>) data.getSerializableExtra("UserEntity");
                for (UserEntity assistant : assistantTeachers) {
                    showTeacherImage(assistant);
                }
            }
            if (requestCode == 5157) {//?????????
                createWork = false;
                mCourseModels = (List<CourseEntity>) data.getSerializableExtra("CourseCardsEntity");
                mCount = (List<CourseEntity>) data.getSerializableExtra("Count");
                ArrayList<WorkClassEntity> workclassList = new ArrayList<>();
                for (int i = 0; i < mCourseModels.size(); i++) {
                    WorkClassEntity workEntity = new WorkClassEntity();
                    workEntity.setOrgCardId(mCourseModels.get(i).getOrgCardId());
                    if (!TextUtils.isEmpty(mCount.get(i).getCourseCount())
                            || mCourseModels.get(i).getCardType().equals("3")) {
                        if (TextUtils.equals(mCourseModels.get(i).getCardType(), "1")) {
                            workEntity.setCount(mCount.get(i).getCourseCount());
                            workEntity.setPrice("");
                        } else {
                            if (mCourseModels.get(i).getCardType().equals("3")) {// ?????????
                                mCount.get(i).setCourseCount("0");
                            }
                            workEntity.setCount("");
                            workEntity.setPrice(String.valueOf((Double.valueOf(mCount.get(i).getCourseCount()) * 100)));
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
                    String unit = "???";
                    if (!TextUtils.equals(cardType, "1")) {
                        unit = "???";
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
            if (requestCode == 3293) {//??????
                teacherMenu1.removeAllViews();//??????????????????
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
            sbClass.append(String.format("????????????\t%s\t???????????????", classNum));
        }
        if (!TextUtils.isEmpty(stuNum)) {
            sbClass.append(String.format("\n????????????\t%s\t?????????", stuNum));
        }
        if (!TextUtils.isEmpty(audiPrice)) {
            sbClass.append(String.format("\n??????\t%s\t???", audiPrice));
        }
        tvClassOption.setText(sbClass);
    }

    /**
     * ????????????????????????(?????????)
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
                U.showToast("????????????????????????????????????");
            } else {
                String selectStartDate1 = DateUtil.getDate(startTime.getTime() / 1000, "yyyy???MM???dd??? HH:mm");
                String selectStartDate = DateUtil.getDate(startTime.getTime() / 1000, "yyyy-MM-dd-HH-mm");
                String[] split = selectStartDate.split("-");
                startDate.set(Integer.parseInt(split[0]), Integer.parseInt(split[1]) - 1, Integer.parseInt(split[2]),//month???0??????
                        Integer.parseInt(split[3]), Integer.parseInt(split[4]));
                new TimePickerBuilder(this, (endTime, view) -> {
                    if (endTime.getTime() < startTime.getTime()) {
                        U.showToast("????????????????????????????????????");
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
                            U.showToast("????????????????????????????????????");
                            return;
                        }
                        addCourseTime(position, startTime, selectStartDate1, endTime, selectEndTime);
                    }
                }).setType(new boolean[]{false, false, false, true, true, false})
                        .setRangDate(startDate, startDate)
                        .setTitleText("??????????????????")
                        .setLabel("", "", "", "???", "???", "")
                        .build().show();
            }
        }).setType(new boolean[]{true, true, true, true, true, false})
                .setTitleText("??????????????????")
                .setRangDate(startDate, null)
                .setLabel("???", "???", "???", "???", "???", "")
                .build().show();
    }

    /**
     * ?????????????????????
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
            classTimeAdapter.isTimeEdit = "1";// ???????????????
            classTimeAdapter.timeList.remove(position);
            classTimeAdapter.timeList.add(position, timeEntity);

            classTimeAdapter.classTimes.remove(position);
            classTimeAdapter.classTimes.add(position, String.format("%s~%s", selectStartDate1, selectEndTime));
        } else {
            if (updateAll) {// ????????????????????????
                classTimeAdapter.isTimeEdit = "1";// ???????????????
            }
            classTimeAdapter.timeList.add(timeEntity);

            classTimeAdapter.classTimes.add(String.format("%s~%s", selectStartDate1, selectEndTime));
        }
        classTimeAdapter.notifyDataSetChanged();
    }

    /**
     * ?????? "???", "??????", "??????"
     */
    boolean isShowAfter = true;//?????????????????????????????????

    private void selectRepeat() {
        String[] sexWeekly;
        if (UserManager.getInstance().removeDuplicateOrder(
                classTimeAdapter.classTimes).size() > 1) {// ??????????????????????????????
            sexWeekly = Constant.sexWeekly0;
        } else {
            sexWeekly = Constant.sexWeekly;
        }
        WheelPopup pop = new WheelPopup(this, sexWeekly);
        pop.showAtLocation(View.inflate(this, R.layout.item_color_course, null),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        pop.setSelectListener((argValue, position) -> {
            getRepeat(argValue, position);
            return null;
        });
    }

    /**
     * ????????????
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
                if (isShowAfter) {//????????????2??????
                    repeatEver = "2";
                    break;
                }
        }
        if (UserManager.getInstance().removeDuplicateOrder(
                classTimeAdapter.classTimes).size() > 1) {// ??????????????????????????????
            repeat = position + 2 + "";
        } else {
            repeat = position + 1 + "";
        }
        tvWeekly.setText(argValue);
    }

    /**
     * ?????? "???", "?????????", "??????"
     */
    boolean isShowOfTime = false;// ??????????????????????????????

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
        isShowAfter = false;//?????????????????????
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
     * ???????????????
     */
    private void selectOfTime() {
        Calendar startDate = Calendar.getInstance();
        new TimePickerBuilder(this, (startTime, v) -> {
            String selectStartDate = DateUtil.getDate(startTime.getTime() / 1000, "yyyy/MM/dd");
            etAfter.setText(selectStartDate);
        }).setType(new boolean[]{true, true, true, false, false, false})
                .setTitleText("????????????")
                .setRangDate(startDate, null)
                .setLabel("???", "???", "???", "", "", "")
                .build().show();
    }

    /**
     * ?????????????????????
     */
    private void showTeacherImage(UserEntity userEntity) {
        // ??????TextView???LinearLayout
        // ????????????
        LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(this).inflate(R.layout.header_item, null);
        AvatarImageView images = view.findViewById(R.id.iv_avatar);
        TextView tvname = view.findViewById(R.id.tv_name);
        menuLinerLayoutParames.setMargins(6, 0, 6, 0);
        // ??????id?????????????????????
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
     * ?????????????????????
     */
    private void showStudentImage(UserEntity userEntity) {
        // ??????TextView???LinearLayout
        // ????????????
        LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(this).inflate(R.layout.header_item, null);
        AvatarImageView images = view.findViewById(R.id.iv_avatar);
        TextView tvname = view.findViewById(R.id.tv_name);
        menuLinerLayoutParames.setMargins(6, 0, 6, 0);
        // ??????id?????????????????????
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
     * ??????????????????
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
                if (!TextUtils.isEmpty(argValue) && !TextUtils.isEmpty(classRoomList.get(position).getId())) {
                    classroomId = classRoomList.get(position).getId();
                    tvClassRoom.setText(argValue);
                }
                return null;
            });
        } else {
            U.showToast("?????????????????????");
            return;
        }
    }

    /**
     * ??????????????????
     *
     * @param data
     */
    private void showConflictDialog(CourseEntity data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_prompt_org, null);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText("????????????");
        TextView tvDesc = view.findViewById(R.id.tv_desc);
        TextView tv_ok = view.findViewById(R.id.tv_ok);
        TextView tv_cancel = view.findViewById(R.id.tv_cancel);
        if (data != null) {
            String startAt = data.getStartAt();
            String startDate = "";
            if (!TextUtils.isEmpty(startAt)) {
                startDate = DateUtil.getDate(Long.valueOf(startAt), "yyyy???MM???dd??? HH:mm") + "-";
            }
            String endAt = data.getEndAt();
            String endDate = "";
            if (!TextUtils.isEmpty(endAt)) {
                endDate = DateUtil.getDate(Long.valueOf(endAt), "HH:mm");
            }
            if (!TextUtils.isEmpty(startAt) && !TextUtils.isEmpty(endAt)) {
                tvDesc.setText(Html.fromHtml(String.format("??????????????????%s????????????%s???%s????????????" +
                                "<br/>???????????????%s<br/><font color='#AAAAAA'>????????????????????????????????????</font>",
                        courseName, startDate + endDate, data.getCourseName(), data.getConflict())));
            } else {
                tv_ok.setVisibility(View.GONE);
                tvDesc.setText(Html.fromHtml(String.format("??????????????????%s????????????" +
                                "<br/>???????????????%s<br/><font color='#AAAAAA'>????????????????????????????????????</font>",
                        courseName, data.getConflict())));
            }
            tv_cancel.setText("????????????");
            tv_ok.setText("????????????????????????");
        } else {
            tvDesc.setText("?????????????????????");
            tv_ok.setText("????????????");
        }
        tv_ok.setOnClickListener(v -> {
            dialog.dismiss();
            if (updateAll) {
                if (classTimeAdapter.isTimeEdit.equals("1")) {// ??????????????????????????? ?????????????????????
                    showWorkTimeDialog(getResources().getString(R.string.text_update_time_rule));
                } else {
                    // ????????????????????????
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

        StringBuilder getCourseTime() {
            return courseTime;
        }

        String getRepeatCount() {
            if (repeatEver.equals("1")) {// ???????????? classTimeAdapter.isTimeEdit
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
            if (TextUtils.isEmpty(check) || TextUtils.isEmpty(orgId) || TextUtils.isEmpty(isOrder)) {
                LogUtils.d("NewWorkActivity", "check???orgId???isOrder");
                myResult = true;
                return this;
            }
            String courseC = etCourse.getText().toString();
            String classB = etClass.getText().toString();
            if (isEtCourseSel) {
                if (TextUtils.isEmpty(courseC) && TextUtils.isEmpty(classB)) {
                    U.showToast("?????????????????????");
                    myResult = true;
                    return this;
                } else if (TextUtils.isEmpty(courseC)) {
                    courseName = String.format("%s???", classB).trim();
                } else if (TextUtils.isEmpty(classB)) {
                    courseName = String.format("%s???", courseC).trim();
                } else {
                    courseName = String.format("%s???%s???", courseC, classB).trim();
                }
            }
            if (TextUtils.isEmpty(courseName)) {
                U.showToast("???????????????/????????????");
                myResult = true;
                return this;
            }
            courseTime = new StringBuilder();
            for (int i = 0; i < classTimeAdapter.timeList.size(); i++) {
                TimeEntity timeEntity = classTimeAdapter.timeList.get(i);
                if (classTimeAdapter.isTimeEdit.equals("1") && updateAll &&
                        Long.parseLong(timeEntity.getStartTime()) * 1000 < TimeUtils.getCurTimeLong()) {
                    showSuccessDialog(NewWorkActivity.this,
                            getResources().getString(R.string.text_update_time_rule));
                    myResult = true;
                    return this;
                } else {
                    courseTime.append(String.format("%s,%s", timeEntity.getStartTime(), timeEntity.getEndTime()));
                    if (i < classTimeAdapter.timeList.size() - 1) {
                        courseTime.append("#");
                    }
                }
            }
            if (TextUtils.isEmpty(courseTime)) {
                U.showToast("?????????????????????");
                myResult = true;
                return this;
            }
            mainTeacher = null;
            if (mainTeachers != null && mainTeachers.size() > 0) {
                mainTeacher = mainTeachers.get(0).getUserId();
                if (planView.equals("1")) {// ??????????????????
                    if (TextUtils.isEmpty(mainTeacher)) {
                        U.showToast("?????????????????????");
                        myResult = true;
                        return this;
                    }
                }
            } else {
                if (planView.equals("1")) {// ??????????????????
                    U.showToast("?????????????????????");
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
            if (planView.equals("2")) {// ??????????????????
                if (TextUtils.isEmpty(classroomId)) {
                    U.showToast("???????????????");
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
                classNum = "1";// ?????? ??????????????????1
            }
            if (stuNum.equals("0") || TextUtils.isEmpty(stuNum)) {
                stuNum = "50";// ?????? ??????????????????50
            }
            myResult = false;
            return this;
        }
    }

    /**
     * ?????????
     */
    public void showSuccessDialog(final Activity mContext, String desc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(mContext, R.layout.dialog_teach_lib, null);
        TextView tvDesc = view.findViewById(R.id.tv_desc);
        tvDesc.setText(desc);
        TextView tvOk = view.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(v -> dialog.dismiss());
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
    }
}
