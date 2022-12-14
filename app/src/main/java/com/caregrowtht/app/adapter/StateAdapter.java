package com.caregrowtht.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.ArtificialActivity;
import com.caregrowtht.app.activity.AuditActivity;
import com.caregrowtht.app.activity.CourseNumActivity;
import com.caregrowtht.app.activity.CourserActivity;
import com.caregrowtht.app.activity.CourserCardMsgActivity;
import com.caregrowtht.app.activity.CourserCoverActivity;
import com.caregrowtht.app.activity.CourserFeedbackActivity;
import com.caregrowtht.app.activity.CreateOrgHintActivity;
import com.caregrowtht.app.activity.EliminateDetailActivity;
import com.caregrowtht.app.activity.EliminateWorkActivity;
import com.caregrowtht.app.activity.FormalActivity;
import com.caregrowtht.app.activity.InitDataActivity;
import com.caregrowtht.app.activity.MomentActivity;
import com.caregrowtht.app.activity.NewWorkActivity;
import com.caregrowtht.app.activity.NoClassStuActivity;
import com.caregrowtht.app.activity.NotifityInfoActivity;
import com.caregrowtht.app.activity.NotifyObjActivity;
import com.caregrowtht.app.activity.NullRemindActivity;
import com.caregrowtht.app.activity.OrgNotifyActivity;
import com.caregrowtht.app.activity.StatisReportActivity;
import com.caregrowtht.app.activity.StuLevaeActivity;
import com.caregrowtht.app.activity.StuOrderActivity;
import com.caregrowtht.app.activity.StuOrdersActivity;
import com.caregrowtht.app.activity.UserTermActivity;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.OrgNotifyEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.ninegrid.RatioImageView;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * haoruigang on 2018-7-4 16:03:37
 * ???????????????
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class StateAdapter extends XrecyclerAdapter {

    private Context mContext;
    private final UserManager instance;

    public List<MessageEntity> messageAllList = new ArrayList<>();

    @BindView(R.id.cl_item)
    ConstraintLayout clItem;
    @BindView(R.id.iv_type)
    ImageView ivType;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.iv_status)
    TextView ivStatus;
    @BindView(R.id.tv_course_info)
    TextView tvCourseInfo;
    @BindView(R.id.tv_title_content)
    TextView tvTitleContent;
    @BindView(R.id.tv_title_end)
    TextView tvTitleEnd;
    @BindView(R.id.iv_author_avatar)
    AvatarImageView ivAuthorAvatar;
    @BindView(R.id.tv_author_name)
    TextView tvAuthorName;
    @BindView(R.id.tv_likes)
    TextView tvLikes;
    @BindView(R.id.tv_comment)
    TextView tvComment;
    @BindView(R.id.cl_message)
    ConstraintLayout clMessage;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_circle)
    RatioImageView ivCircle;
    @BindView(R.id.tv_atter)
    TextView tvAtter;
    @BindView(R.id.cl_event)
    RelativeLayout clEvent;
    @BindView(R.id.tv_handlerName)
    TextView tvHandlerName;
    @BindView(R.id.tv_course_count_hour)
    TextView tvCourseCountHour;
    @BindView(R.id.tv_today_course_income)
    TextView tvTodayCourseIncome;
    @BindView(R.id.tv_today_add_income)
    TextView tvTodayAddIncome;
    @BindView(R.id.tv_today_add_stu)
    TextView tvTodayAddStu;
    @BindView(R.id.tv_today_refund)
    TextView tvTodayRefund;
    @BindView(R.id.tv_month_course_income)
    TextView tvMonthCourseIncome;
    @BindView(R.id.tv_month_add_income)
    TextView tvMonthAddIncome;
    @BindView(R.id.tv_month_add_stu)
    TextView tvMonthAddStu;
    @BindView(R.id.tv_month_refund)
    TextView tvMonthRefund;
    @BindView(R.id.ll_teacher_work)
    LinearLayout llTeacherWork;
    @BindView(R.id.tv_stu_work)
    TextView tvStuWork;
    @BindView(R.id.ll_work_daily)
    LinearLayout llWorkDaily;


    @BindView(R.id.rl_read_aicz)
    RelativeLayout rlReadAicz;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_text)
    TextView tvText;


    @BindView(R.id.rl_add_aicz)
    RelativeLayout rlAddAicz;
    @BindView(R.id.tv_add_time)
    TextView tvAddTime;
    @BindView(R.id.iv_add_status)
    TextView ivAddStatus;
    @BindView(R.id.btn_add_org)
    Button btnAddOrg;
    @BindView(R.id.btn_create_org)
    Button btnCreateOrg;


    @BindView(R.id.rl_sign_levae)
    RelativeLayout rlSignLevae;
    @BindView(R.id.iv_sign_leave_type)
    ImageView ivSignLeaveType;
    @BindView(R.id.tv_sign_leave_title)
    TextView tvSignLeaveTitle;
    @BindView(R.id.iv_sign_leave_status)
    TextView ivSignLeaveStatus;
    @BindView(R.id.tv_org_name)
    TextView tvOrgName;
    @BindView(R.id.tv_sign_leave_content)
    TextView tvSignLeaveContent;
    @BindView(R.id.rl_sign)
    RelativeLayout rlSign;
    @BindView(R.id.iv_levae_author_avatar)
    AvatarImageView ivLevaeAuthorAvatar;
    @BindView(R.id.tv_leave_author_name)
    TextView tvLevaeAuthorName;
    @BindView(R.id.tv_stu_detail)
    TextView tvStuDetail;
    @BindView(R.id.tv_in_time)
    TextView tvInTime;
    @BindView(R.id.cl_rb)
    RelativeLayout clRb;
    @BindView(R.id.tv_isDel)
    TextView tvIsDel;
    @BindView(R.id.ll_select_all)
    LinearLayout llSelectAll;
    @BindView(R.id.tv_select_detial)
    TextView tvSelectDetial;
    @BindView(R.id.tv_select_all)
    TextView tvSelectAll;
    @BindView(R.id.ll_check_all)
    LinearLayout llCheckAll;
    @BindView(R.id.tv_check_detial)
    TextView tvCheckDetial;
    @BindView(R.id.tv_check_all)
    TextView tvCheckAll;
    @BindView(R.id.tv_day_xk_monery)
    TextView tvDayXkMonery;
    @BindView(R.id.tv_day_xz_monery)
    TextView tvDayXzMonery;
    @BindView(R.id.tv_day_tf_monery)
    TextView tvDayTfMonery;
    @BindView(R.id.tv_month_xk_monery)
    TextView tvMonthXkMonery;
    @BindView(R.id.tv_month_xz_monery)
    TextView tvMonthXzMonery;
    @BindView(R.id.tv_month_tf_monery)
    TextView tvMonthTfMonery;
    @BindView(R.id.v_line_1)
    View Vline1;
    @BindView(R.id.tv_sign_leave_time)
    TextView tvSignLeaveTime;


    public StateAdapter(List datas, Context context) {
        super(datas, context);
        this.mContext = context;
        instance = UserManager.getInstance();
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        MessageEntity msgEntity = messageAllList.get(position);
        switch (msgEntity.getType()) {
            case "1":// 1???????????????
            case "26":// 26?????????????????????
                ivType.setImageResource(R.mipmap.ic_type_course);
                setVisibilitys(true, false, false, false);
                break;
            case "2":// 2???????????????
                ivType.setImageResource(R.mipmap.ic_type_feedback);
                setVisibilitys(true, false, false, false);
                break;
            case "3":// 3???????????????
                ivType.setImageResource(R.mipmap.ic_type_org);
                setVisibilitys(true, false, false, false);
                break;
            case "4":// 4???????????????
            case "5":// 5???????????????
                ivType.setImageResource(R.mipmap.ic_type_noti);
                setVisibilitys(true, false, false, false);
                break;
            case "6":// 6?????????????????????
                ivType.setImageResource(R.mipmap.ic_work_daily);
                setVisibilitys(true, false, false, false);
                break;
            case "7":// 7???????????????????????????
                setVisibilitys(false, true, false, false);
                break;
            case "8":// 8????????????????????????
                setVisibilitys(false, false, true, false);
                break;
            case "9":// 9??????????????????????????????
            case "10":// 10???????????????????????????
            case "12":// 12??????????????????????????????????????????
            case "13":// 13???????????????????????????
            case "14":// 14???????????????
            case "19":// 19?????????????????????
            case "20":// 20?????????????????????
                ivSignLeaveType.setImageResource(R.mipmap.ic_type_course);
                setVisibilitys(false, false, false, true);
                break;
            case "11":// 11???????????????????????????
                ivSignLeaveType.setImageResource(R.mipmap.ic_work_daily);
                setVisibilitys(false, false, false, true);
                break;
            case "15":// 15?????????????????????
                ivSignLeaveType.setImageResource(R.mipmap.ic_type_noti);
                setVisibilitys(false, false, false, true);
                break;
            case "16":// 16???????????????
            case "17":// 17?????????????????????
            case "18":// 18?????????????????????
            case "21":// 21????????????????????????
            case "22":// 22?????????????????????
            case "27":// 27????????????????????????
                ivSignLeaveType.setImageResource(R.mipmap.ic_type_org);
                setVisibilitys(false, false, false, true);
                break;
            case "23":// 23??????????????????????????????
            case "24":// 24??????????????????
            case "25":// 25???????????????
                ivSignLeaveType.setImageResource(R.mipmap.ic_type_feedback);
                setVisibilitys(false, false, false, true);
                break;
        }
        ivStatus.setVisibility(View.GONE);
        llWorkDaily.setVisibility(View.GONE);
        tvHandlerName.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);
        tvTitleEnd.setVisibility(View.GONE);
        tvCourseInfo.setVisibility(View.GONE);
        llSelectAll.setVisibility(View.GONE);
        clMessage.setVisibility(!TextUtils.isEmpty(msgEntity.getCircleLikeCount())
                || !TextUtils.isEmpty(msgEntity.getCircleCommentCount()) ? View.VISIBLE : View.GONE);
        clEvent.setVisibility(TextUtils.equals(msgEntity.getCircleId(), "0") ? View.GONE : View.VISIBLE);
        String rbMonthTime = TimeUtils.getDateToString(Long.parseLong(msgEntity.getUpdateTime()), "yyyy???MM???dd??? HH:mm");
        tvTitleContent.setTextColor(ResourcesUtils.getColor(R.color.color_3));
        String orgName = String.format("%s", TextUtils.isEmpty(msgEntity.getOrgChainName()) ?
                msgEntity.getOrgName() : msgEntity.getOrgName() + msgEntity.getOrgChainName());
        if (msgEntity.getType().equals("6")) {// 6?????????????????????
            tvTitle.setVisibility(View.VISIBLE);
            llWorkDaily.setVisibility(View.VISIBLE);
            String data = DateUtil.getDate(Long.parseLong(msgEntity.getUpdateTime()), "yyyy???MM???dd???");
            String week = TimeUtils.getWeekByDateStr(data);//????????????
            tvTime.setText(String.format("%s\t%s", data, week));
            tvTitleContent.setText(orgName);
            try {
                JSONObject jsonObject = new JSONObject(msgEntity.getContent());
                String courseCount = instance.getJsonString(jsonObject, "dpTodayCourseCount");
                String courseHour = instance.getJsonString(jsonObject, "dpTodayCourseHourMinutes");
                tvCourseCountHour.setText(Html.fromHtml(String.format("??????????????????:\t<font color='#333333'>%s???/%s</font>", courseCount, courseHour)));
                String todayIncome = instance.getJsonString(jsonObject, "dpTodayCourseIncome");
                tvTodayCourseIncome.setText(Html.fromHtml(String.format("??????????????????:\t<font color='#333333'>%s???</font>", String.valueOf(Float.parseFloat(todayIncome) / 100))));
                String todayCardIncome = instance.getJsonString(jsonObject, "dpTodayCourseCardIncome");
                tvTodayAddIncome.setText(Html.fromHtml(String.format("??????????????????:\t<font color='#333333'>%s???</font>", String.valueOf(Float.parseFloat(todayCardIncome) / 100))));
                String todayNewStu = instance.getJsonString(jsonObject, "dpTodayNewStudent");
                tvTodayAddStu.setText(Html.fromHtml(String.format("??????????????????:\t<font color='#333333'>%s???</font>", todayNewStu)));
                String todayRefund = instance.getJsonString(jsonObject, "dpTodayRefund");
                tvTodayRefund.setText(Html.fromHtml(String.format("????????????:\t<font color='#333333'>%s???</font>", String.valueOf(Float.parseFloat(todayRefund) / 100))));
                String monthIncome = instance.getJsonString(jsonObject, "dpMonthCourseIncome");
                tvMonthCourseIncome.setText(Html.fromHtml(String.format("??????????????????:\t<font color='#333333'>%s???</font>", String.valueOf(Float.parseFloat(monthIncome) / 100))));
                String monthCardIncome = instance.getJsonString(jsonObject, "dpMonthCourseCardIncome");
                tvMonthAddIncome.setText(Html.fromHtml(String.format("??????????????????:\t<font color='#333333'>%s???</font>", String.valueOf(Float.parseFloat(monthCardIncome) / 100))));
                String monthNewStu = instance.getJsonString(jsonObject, "dpMonthNewStudent");
                tvMonthAddStu.setText(Html.fromHtml(String.format("??????????????????:\t<font color='#333333'>%s???</font>", monthNewStu)));
                String monthRefund = instance.getJsonString(jsonObject, "dpMonthRefund");
                tvMonthRefund.setText(Html.fromHtml(String.format("????????????:\t<font color='#333333'>%s???</font>", String.valueOf(Float.parseFloat(monthRefund) / 100))));
                String yinChuQin = instance.getJsonString(jsonObject, "dpYingChuQin");
                String shiJiChuQin = instance.getJsonString(jsonObject, "dpShiJiChuQin");
                String qingJia = instance.getJsonString(jsonObject, "dpQingjia");
                String weiChuli = instance.getJsonString(jsonObject, "dpWeiChuLi");
                tvStuWork.setText(Html.fromHtml(String.format("??????????????????:\t<font color='#333333'>??????????????????%s??????????????????%s????????????%s???????????????%s???</font>"
                        , yinChuQin, shiJiChuQin, qingJia, weiChuli)));
                JSONArray teachers = jsonObject.getJSONArray("teacher");
                llTeacherWork.removeAllViews();
                TextView workTv = new TextView(mContext);
                workTv.setTextSize(16);
                workTv.setText("????????????????????????:");
                llTeacherWork.addView(workTv);
                for (int i = 0; i < teachers.length(); i++) {
                    JSONObject teacher = teachers.getJSONObject(i);
                    instance.getJsonString(teacher, "teacherId");
                    String teacherName = instance.getJsonString(teacher, "teacherName");
                    String courseCouont = instance.getJsonString(teacher, "courseCount");
                    String endCount = instance.getJsonString(teacher, "courseEndCount");
                    String feedCount = instance.getJsonString(teacher, "courseFeedbackCount");
                    String tvT = "";
                    if (teacherName.length() > 2) {
                        tvT = "\t\t";// 2
                    }
                    TextView textView = new TextView(mContext);
                    textView.setTextSize(16);
                    textView.setTextColor(mContext.getResources().getColor(R.color.color_3));
                    textView.setText(String.format("%s??????:\t????????????%s/%s??????\n" +
                                    tvT + "\t\t\t\t\t\t\t\t\t\t\t??????????????????%s/%s??????"// 11
                            , teacherName, endCount, courseCouont, feedCount, courseCouont));
                    llTeacherWork.addView(textView);
                }
            } catch (JSONException e) {
                LogUtils.e("StateAdapter", e.getMessage());
            }
        } else if (msgEntity.getType().equals("7") || msgEntity.getType().equals("8")) {
            if (msgEntity.getType2().equals("74")) {// ?????????????????????
                ivIcon.setImageResource(R.mipmap.icon_originator);
                tvText.setText("?????????????????????");
            } else if (msgEntity.getType2().equals("75")) {// ????????????????????????
                ivIcon.setImageResource(R.mipmap.icon_state);
                tvText.setText("3???????????????????????????");
            }
        } else if (msgEntity.getType().equals("9") || msgEntity.getType().equals("10")
                || msgEntity.getType().equals("11") || msgEntity.getType().equals("12")
                || msgEntity.getType().equals("13") || msgEntity.getType().equals("14")
                || msgEntity.getType().equals("15") || msgEntity.getType().equals("16")
                || msgEntity.getType().equals("17") || msgEntity.getType().equals("18")
                || msgEntity.getType().equals("19") || msgEntity.getType().equals("20")
                || msgEntity.getType().equals("21") || msgEntity.getType().equals("22")
                || msgEntity.getType().equals("23") || msgEntity.getType().equals("24")
                || msgEntity.getType().equals("25") || msgEntity.getType().equals("27")) {
            tvOrgName.setText(orgName);
            long courseStartAt = Long.valueOf(msgEntity.getCourseStartAt());
            String Month = TimeUtils.getDateToString(courseStartAt, "MM???dd???");
            String data = DateUtil.getDate(courseStartAt, "yyyy???MM???dd???");
            String week = TimeUtils.getWeekByDateStr(data);//????????????
            String time = TimeUtils.getDateToString(courseStartAt, "HH:mm");
            String teacherName = msgEntity.getTeacherName();
            String courseName = msgEntity.getCourseName();
            tvSignLeaveContent.setVisibility(View.VISIBLE);
            tvSignLeaveContent.setText(String.format("%s\t\t%s\t\t%s\n%s\t\t%s",
                    Month, week, time, teacherName, courseName));
            rlSign.setVisibility(View.VISIBLE);
            tvStuDetail.setVisibility(View.VISIBLE);
            tvStuDetail.setBackgroundResource(0);
            tvStuDetail.setPadding(0, 0, 0, 0);
            tvStuDetail.setTextColor(mContext.getResources().getColor(R.color.color_9));
            clRb.setVisibility(View.GONE);
            llCheckAll.setVisibility(View.GONE);
            Vline1.setVisibility(View.VISIBLE);
            tvSignLeaveTime.setVisibility(View.VISIBLE);
            tvSignLeaveTime.setText(String.format("%s\t\t%s", rbMonthTime, msgEntity.getRead().equals("0") ? "??????" : ""));
            if (msgEntity.getType().equals("9") || msgEntity.getType().equals("17")
                    || msgEntity.getType().equals("18") || msgEntity.getType().equals("19")) {
                tvStuDetail.setVisibility(View.GONE);
                ivLevaeAuthorAvatar.setTextAndColor(TextUtils.isEmpty(msgEntity.getShowName()) ? ""
                        : msgEntity.getShowName().substring(0, 1), mContext.getResources().getColor(R.color.b0b2b6));
                GlideUtils.setGlideImg(mContext, msgEntity.getShowHeadImage(), 0, ivLevaeAuthorAvatar);
                tvLevaeAuthorName.setText(String.format("%s(???)", msgEntity.getShowName()));
                if (msgEntity.getType().equals("9")) {// 9??????????????????????????????
                    tvSignLeaveTitle.setText("????????????");
                    llCheckAll.setVisibility(View.VISIBLE);
                    Vline1.setVisibility(View.GONE);
                } else if (msgEntity.getType().equals("17")) {// 17?????????????????????
                    tvSignLeaveTitle.setText("??????????????????");
                    tvSignLeaveContent.setVisibility(View.GONE);
                } else if (msgEntity.getType().equals("18")) {// 18?????????????????????
                    tvSignLeaveTitle.setText("??????????????????");
                    tvSignLeaveContent.setVisibility(View.GONE);
                } else if (msgEntity.getType().equals("19")) {// 19?????????????????????
                    tvSignLeaveTitle.setText("??????????????????");
                    llCheckAll.setVisibility(View.VISIBLE);
                    Vline1.setVisibility(View.GONE);
                    tvLevaeAuthorName.setText(Html.fromHtml(String.format("%s\t\t<font color='#F34B4B'>%s</font>"
                            , tvLevaeAuthorName.getText(), "??????????????????")));
                }
            } else if (msgEntity.getType().equals("10") || msgEntity.getType().equals("13")
                    || msgEntity.getType().equals("14") || msgEntity.getType().equals("15")
                    || msgEntity.getType().equals("16") || msgEntity.getType().equals("20")
                    || msgEntity.getType().equals("21") || msgEntity.getType().equals("22")
                    || msgEntity.getType().equals("23") || msgEntity.getType().equals("24")
                    || msgEntity.getType().equals("25") || msgEntity.getType().equals("27")) {
                rlSign.setVisibility(View.GONE);
                if (msgEntity.getType().equals("10")) {// 10???????????????????????????
                    tvSignLeaveTitle.setText("????????????");
                    try {
                        JSONObject jsonObject = new JSONObject(msgEntity.getContent());
                        String childNum = instance.getJsonString(jsonObject, "childNum");
                        String sign = instance.getJsonString(jsonObject, "sign");
                        String leav = instance.getJsonString(jsonObject, "leav");
                        String noTake = instance.getJsonString(jsonObject, "noTake");
                        tvStuDetail.setText(Html.fromHtml(String.format(
                                "??????\t%s???\t\t|\t\t??????\t%s???\t\t|\t\t??????\t%s???\t\t|\t\t?????????\t" +
                                        "<font color='#F54949'>%s</font>???", childNum, sign, leav, noTake)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    llCheckAll.setVisibility(View.VISIBLE);
                    Vline1.setVisibility(View.GONE);
                } else if (msgEntity.getType().equals("13")) {// 13.??????????????????
                    tvSignLeaveTitle.setText("??????????????????");
                    tvStuDetail.setText(String.format("??????:\t\t%s???\t\t|\t\t????????????:\t\t%s???"
                            , msgEntity.getChildNum(), msgEntity.getMinCount()));
                    llCheckAll.setVisibility(View.VISIBLE);
                    Vline1.setVisibility(View.GONE);
                } else if (msgEntity.getType().equals("14")) {// 14???|82???????????????
                    tvSignLeaveTitle.setText("????????????");
                    tvStuDetail.setText(String.format("??????:\t\t%s???", msgEntity.getChildNum()));
                    llCheckAll.setVisibility(View.VISIBLE);
                    Vline1.setVisibility(View.GONE);
                } else if (msgEntity.getType().equals("15")) {// 15???|83?????????????????????
                    setTitleDetail(context, "????????????", false, msgEntity.getContent());
                } else if (msgEntity.getType().equals("16")) {// 16???|84???????????????
                    tvStuDetail.setBackgroundResource(R.drawable.shape_state_handler_bg);
                    tvStuDetail.setPadding(15, 15, 15, 15);
                    tvOrgName.setText(Html.fromHtml(String.format("%s<br/>%s", orgName,
                            "<font color='#333333'>???????????????</font>")));
                    setTitleDetail(context, "????????????", false,
                            String.format("%s", msgEntity.getContent()));
                } else if (msgEntity.getType().equals("20")) {// 20?????????????????????
                    tvSignLeaveTitle.setText("??????????????????");
                    tvStuDetail.setText(String.format("??????:\t\t%s???", msgEntity.getChildNum()));
                    llCheckAll.setVisibility(View.VISIBLE);
                    Vline1.setVisibility(View.GONE);
                } else if (msgEntity.getType().equals("21")) {// 21????????????????????????
                    setTitleDetail(context, "?????????????????????", false,
                            String.format("???????????????:\t\t%s???", msgEntity.getChildNum()));
                } else if (msgEntity.getType().equals("22")) {// 22?????????????????????
                    setTitleDetail(context, "??????????????????", false,
                            String.format("??????????????????:\t\t%s???", msgEntity.getChildNum()));
                } else if (msgEntity.getType().equals("23")) {// 23??????????????????????????????
                    setTitleDetail(context, "????????????", false, msgEntity.getContent());
                } else if (msgEntity.getType().equals("24")) {// 24??????????????????
                    setTitleDetail(context, "???????????????", false,
                            "???????????????????????????/????????????????????????");
                } else if (msgEntity.getType().equals("25")) {// 25???????????????
                    setTitleDetail(context, "????????????", false,
                            "???????????????????????????/????????????");
                } else if (msgEntity.getType().equals("27")) {// 27????????????????????????
                    setTitleDetail(context, "?????????????????????", false,
                            String.format("?????????????????????????????????:\t\t%s???", msgEntity.getChildNum()));
                }
            } else if (msgEntity.getType().equals("12")) {// 12??????????????????????????????????????????
                tvSignLeaveTitle.setText("???????????????????????????");
                rlSign.setVisibility(View.GONE);
                tvStuDetail.setVisibility(View.GONE);
                llCheckAll.setVisibility(View.VISIBLE);
                Vline1.setVisibility(View.GONE);
            } else if (msgEntity.getType().equals("11")) {// 11???????????????????????????
                tvSignLeaveTitle.setText("????????????");
                tvSignLeaveContent.setVisibility(View.GONE);
                rlSign.setVisibility(View.GONE);
                tvStuDetail.setVisibility(View.GONE);
                clRb.setVisibility(View.VISIBLE);
                Vline1.setVisibility(View.GONE);
                tvSignLeaveTime.setVisibility(View.GONE);
                long updateTime = Long.valueOf(msgEntity.getTime());
                String rbMonth = TimeUtils.getDateToString(updateTime, "MM???dd???");
                String rbData = DateUtil.getDate(updateTime, "yyyy???MM???dd???");
                String rbWeek = TimeUtils.getWeekByDateStr(rbData);//????????????
                tvInTime.setText(String.format("%s\t%s", rbMonth, rbWeek));
                try {
                    JSONObject jsonObject = new JSONObject(msgEntity.getContent());
                    String todayIncome = instance.getJsonString(jsonObject, "dpTodayCourseIncome");
                    tvDayXkMonery.setText(String.format("%s???", String.valueOf(Float.parseFloat(todayIncome) / 100)));
                    String todayCardIncome = instance.getJsonString(jsonObject, "dpTodayCourseCardIncome");
                    tvDayXzMonery.setText(String.format("%s???", String.valueOf(Float.parseFloat(todayCardIncome) / 100)));
                    String todayRefund = instance.getJsonString(jsonObject, "dpTodayRefund");
                    tvDayTfMonery.setText(String.format("%s???", String.valueOf(Float.parseFloat(todayRefund) / 100)));
                    String monthIncome = instance.getJsonString(jsonObject, "dpMonthCourseIncome");
                    tvMonthXkMonery.setText(String.format("%s???", String.valueOf(Float.parseFloat(monthIncome) / 100)));
                    String monthCardIncome = instance.getJsonString(jsonObject, "dpMonthCourseCardIncome");
                    tvMonthXzMonery.setText(String.format("%s???", String.valueOf(Float.parseFloat(monthCardIncome) / 100)));
                    String monthRefund = instance.getJsonString(jsonObject, "dpMonthRefund");
                    tvMonthTfMonery.setText(String.format("%s???", String.valueOf(Float.parseFloat(monthRefund) / 100)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            tvTime.setText(TimeUtils.getDateToString(Long.parseLong(msgEntity.getTime()), "MM???dd??? HH:mm"));
            //?????????????????????????????????????????????type=5?????????????????????????????????????????????????????????
            if (msgEntity.getType().equals("4") || msgEntity.getType().equals("5")) {//4,5?????????????????????????????????
                switch (msgEntity.getReceipt()) {
//                case "1":// 1??????????????????
//                    ivStatus.setVisibility(View.VISIBLE);
//                    ivStatus.setText("???????????????");
//                    ivStatus.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_pending));
//                    break;
                    case "2"://2????????????
                        ivStatus.setVisibility(View.VISIBLE);
                        ivStatus.setText("?????????");
                        ivStatus.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_pending));
                        break;
                    case "3"://3???????????????
                        ivStatus.setVisibility(View.VISIBLE);
                        ivStatus.setText("????????????");
                        ivStatus.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_complet));
                        break;
                }
            }
            ivAuthorAvatar.setTextAndColor(TextUtils.isEmpty(msgEntity.getCircleAuthor()) ? ""
                    : msgEntity.getCircleAuthor().substring(0, 1), mContext.getResources().getColor(R.color.b0b2b6));
            GlideUtils.setGlideImg(mContext, msgEntity.getCircleIcon(), 0, ivAuthorAvatar);
            tvAuthorName.setText(msgEntity.getCircleAuthor());
            tvLikes.setText(msgEntity.getCircleLikeCount());
            tvComment.setText(msgEntity.getCircleCommentCount());

            boolean isCircle = !TextUtils.isEmpty(msgEntity.getCircleLikeCount())
                    || !TextUtils.isEmpty(msgEntity.getCircleCommentCount());
            String orgContent;
            String sentNotice = "?????????????????????";
            if (isCircle) {// ????????????
                orgContent = orgName + "\n??????" + msgEntity.getCircleAuthor() + "???" +
                        DateUtil.getDate(Long.parseLong(msgEntity.getCircleCourseBeginTime()), "MM???dd??? HH:mm") + "~"
                        + DateUtil.getDate(Long.parseLong(msgEntity.getCircleCourseEndTime()), "HH:mm") + "???\"" +
                        msgEntity.getCircleCourseName() + "\"???????????????????????????";
            } else if (msgEntity.getType().equals("4")) {//4????????????????????????
                String newNotice = "";
                if (msgEntity.getType2().equals("20")) {// ?????????
                    newNotice = "???????????????:";
                    if (msgEntity.getReceipt().equals("4")) {
                        sentNotice = "????????????????????????????????????";
                    }
                } else if (msgEntity.getType2().equals("24")) {// ?????????
                    newNotice = "?????????:";
                    if (msgEntity.getReceipt().equals("1")) {
                        sentNotice = "?????????????????????";
                    } else {
                        sentNotice = "??????????????????????????????";
                    }
                }
                orgContent = String.format("%s\n%s%s", orgName, newNotice, msgEntity.getContent());
            } else {
                orgContent = String.format("%s\n%s", orgName, msgEntity.getContent());
            }
            if (msgEntity.getType().equals("26")) {// 26:????????????
                tvTime.setText("??????????????????");
                tvTime.setTextColor(context.getResources().getColor(R.color.col_t9));
                tvTime.setTextSize(18);
                tvTitleContent.setTextColor(ResourcesUtils.getColor(R.color.color_9));
                tvTitleContent.setText(orgName);
                tvCourseInfo.setVisibility(View.VISIBLE);
                llSelectAll.setVisibility(View.VISIBLE);
                tvIsDel.setVisibility(msgEntity.getCircleId().equals("0") ? View.VISIBLE : View.GONE);// ????????????????????????
                long courseStartAt = Long.valueOf(msgEntity.getCourseStartAt());
                String Month = TimeUtils.getDateToString(courseStartAt, "MM???dd???");
                String data = DateUtil.getDate(courseStartAt, "yyyy???MM???dd???");
                String week = TimeUtils.getWeekByDateStr(data);//????????????
                String time = TimeUtils.getDateToString(courseStartAt, "HH:mm");
                String teacherName = msgEntity.getCircleAuthor();
                String courseName = msgEntity.getCourseName();
                tvCourseInfo.setText(String.format("%s\t\t%s\t\t%s\n%s\t\t%s",
                        Month, week, time, teacherName, courseName));
                tvHandlerName.setVisibility(View.VISIBLE);
                tvHandlerName.setText(String.format("%s\t\t%s", rbMonthTime, msgEntity.getRead().equals("0") ? "??????" : ""));
            } else {
                tvTitleContent.setText(TextUtils.isEmpty(orgContent) ? "" : orgContent.replace("\\n", "\n"));
                tvTitleEnd.setVisibility(View.VISIBLE);
                tvTitleEnd.setText(sentNotice);
            }

            String circleContent = msgEntity.getCircleContent();
            tvContent.setVisibility(TextUtils.isEmpty(circleContent) ? View.GONE : View.VISIBLE);
            tvContent.setText(TextUtils.isEmpty(circleContent) ? "" : circleContent.replace("\\n", "\n"));
            String[] circlePicture = {};
            if (!TextUtils.isEmpty(msgEntity.getCirclePictures())) {
                circlePicture = msgEntity.getCirclePictures().split("#");
            }
            int bannerSize = circlePicture.length;
            if (bannerSize > 0) {
                //---
                ivCircle.setScaleType(ImageView.ScaleType.CENTER_CROP);//fitXY????????????????????????ImageView??????????????????????????????????????????????????????????????????????????????????????????????????????
                if (circlePicture[0].contains("mp4")) {
                    ivCircle.setCenterImgShow(true);
                }
                GlideUtils.setGlideImg(mContext, circlePicture[0], R.mipmap.ic_media_default, ivCircle);// ?????????????????????
            }
            ivCircle.setVisibility(bannerSize > 0 ? View.VISIBLE : View.GONE);
            String circleAccessory = msgEntity.getCircleAccessory();
            tvAtter.setVisibility(TextUtils.isEmpty(circleAccessory) ? View.GONE : View.VISIBLE);
        }

        ivSignLeaveStatus.setText("?????????");
        ivSignLeaveStatus.setBackgroundResource(R.mipmap.ic_y_update);
        ivStatus.setText("?????????");
        ivStatus.setBackgroundResource(R.mipmap.ic_y_update);
        // 1????????? 2????????? 3???????????????
        switch (msgEntity.getStatus()) {//??????????????? ??????????????????????????????????????????
            case "1"://1????????????
                ivAddStatus.setText("?????????");
                ivAddStatus.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_pending));

                ivSignLeaveStatus.setVisibility(View.VISIBLE);
                ivSignLeaveStatus.setText("?????????");
                ivSignLeaveStatus.setBackgroundResource(R.mipmap.ic_pending);

                ivStatus.setVisibility(View.VISIBLE);
                ivStatus.setText("?????????");
                ivStatus.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_pending));
                break;
            case "2"://2????????????(?????????)
                ivAddStatus.setVisibility(View.VISIBLE);
                ivAddStatus.setText("?????????");
                ivAddStatus.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_complet));

                ivSignLeaveStatus.setVisibility(View.VISIBLE);
                ivSignLeaveStatus.setText("?????????");
                ivSignLeaveStatus.setBackgroundResource(R.mipmap.ic_complet);

                ivStatus.setVisibility(View.VISIBLE);
                ivStatus.setText("?????????");
                ivStatus.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_complet));
                String handleTime = msgEntity.getHandleTime();
                if (!handleTime.equals("0")) {
                    tvHandlerName.setVisibility(View.VISIBLE);
                    tvHandlerName.setText(String.format("%s\t\t%s\t\t??????", msgEntity.getHandlerName(),
                            TimeUtils.getDateToString(Long.parseLong(msgEntity.getHandleTime()), "MM???dd??? HH:mm")));
                }
                break;
            case "3"://3??????????????????

                ivStatus.setVisibility(StrUtils.isNotEmpty(msgEntity.getRead()) &&
                        msgEntity.getRead().equals("0") ? View.VISIBLE : View.GONE);
                ivSignLeaveStatus.setVisibility(StrUtils.isNotEmpty(msgEntity.getRead()) &&
                        msgEntity.getRead().equals("0") ? View.VISIBLE : View.GONE);

                tvAddTime.setText(rbMonthTime);
                break;
        }
//        tvAtter.setText(circleAccessory);
        clItem.setOnClickListener(v -> {
            if (msgEntity.getType().equals("6") || msgEntity.getType().equals("26")) {// 26?????????????????????
                return;
            }
            // ?????????&????????????????????????
            instance.teacherOrgStrand((Activity) mContext, msgEntity, isLeave -> {
                if (isLeave) {// ?????????
                    instance.showSuccessDialog((Activity) mContext
                            , mContext.getString(R.string.string_have_leave));
                } else {
                    // 79.????????????????????????
                    instance.getOrgInfo(msgEntity.getOrgId(), (Activity) mContext, "2", isRole -> {
                        if (isRole) {// ????????????????????????
                            if (TextUtils.equals(msgEntity.getType(), "1")) {
                                // tpye2 1??????????????? 3???????????????  4??????????????? 19???????????????????????????
                                switch (msgEntity.getType2()) {
                                    case "1": // 1???????????????
                                        startActivity(msgEntity, CourserActivity.class);
                                        break;
                                    case "3": // 3???????????????
                                        startActivity(msgEntity, CourserCoverActivity.class);
                                        break;
                                    case "21":// 21,27,28????????????,????????????
                                    case "27":
                                    case "28":
                                        instance.showSuccessDialog((Activity) mContext
                                                , mContext.getString(R.string.function_limit));
                                        break;
                                    case "50":
                                        startActivity(msgEntity, CourserActivity.class);
                                        break;
                                    default:
                                        startActivity(msgEntity, NotifityInfoActivity.class);
                                        break;
                                }
                            } else if (TextUtils.equals(msgEntity.getType(), "2")) {
                                // tpye2 23???????????????
                                switch (msgEntity.getType2()) {
                                    case "22": // 22???????????????
                                        startActivity(msgEntity, CourserActivity.class);
                                        break;
                                    case "23": // 23?????????????????????
                                        mContext.startActivity(new Intent(mContext, CourserFeedbackActivity.class)
                                                .putExtra("imputType", "2")
                                                .putExtra("msgEntity", msgEntity)
                                                .putExtra("circleId", msgEntity.getTargetId()));// imputType???1:????????????????????????????????? 2:???????????????????????????
                                        break;
                                    default:
                                        startActivity(msgEntity, NotifityInfoActivity.class);
                                        break;
                                }
                            } else if (TextUtils.equals(msgEntity.getType(), "3")) {
                                switch (msgEntity.getType2()) {
                                    case "4": // 4???????????????
                                        getOrgTeachers(msgEntity.getOrgId(), "2", "1");
                                        break;
                                    case "6": // 6???????????????
                                        getAudit(msgEntity.getOrgId());
                                        break;
                                    case "7": // 7?????????????????????
                                        instance.showSuccessDialog((Activity) mContext
                                                , mContext.getString(R.string.function_limit));
                                        break;
                                    case "8": // 8????????????????????????
                                        startActivity(msgEntity, CourserCardMsgActivity.class);
                                        break;
                                    case "9": // 9???????????????
                                        mContext.startActivity(new Intent(mContext, FormalActivity.class)
                                                .putExtra("msgEntity", msgEntity));
                                        break;
                                    case "10":// 10???????????????
                                        mContext.startActivity(new Intent(mContext, NewWorkActivity.class)
                                                .putExtra("msgEntity", msgEntity));
                                        ((Activity) mContext).overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//??????????????????
                                        break;
                                    case "25":// 25????????????????????????
                                        startActivity(msgEntity, InitDataActivity.class);
                                        break;
                                    case "72":// 72???????????????
                                        startActivity(msgEntity, CourserActivity.class);
                                        break;
                                    default:
                                        startActivity(msgEntity, NotifityInfoActivity.class);
                                        break;
                                }
                            } else if (TextUtils.equals(msgEntity.getType(), "4")) {
                                switch (msgEntity.getType2()) {
                                    case "13":// 13????????????????????????
                                        U.showToast("?????????");
                                        break;
                                    case "20":// 20??????????????????????????????
                                        if (msgEntity.getReceipt().equals("1") || msgEntity.getReceipt().equals("2")) {// ???????????????/ ?????????
                                            startActivity(msgEntity, NotifityInfoActivity.class);
                                        } else {
                                            OrgNotifyEntity notifyEntity = new OrgNotifyEntity();
                                            notifyEntity.setIsReceipt("1");// ??????????????????????????????,????????????1????????????
                                            notifyEntity.setNotifiId(msgEntity.getTargetId());
                                            mContext.startActivity(new Intent(mContext, NotifyObjActivity.class)
                                                    .putExtra("msgEntity", msgEntity)
                                                    .putExtra("notifyEntity", notifyEntity));
                                            ((Activity) mContext).overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//??????????????????
                                        }
                                        break;
                                    case "24":
                                        startActivity(msgEntity, NotifityInfoActivity.class);
                                        break;
                                    default:
                                        startActivity(msgEntity, NotifityInfoActivity.class);
                                        break;
                                }
                            } else if (TextUtils.equals(msgEntity.getType(), "5")) {
                                switch (msgEntity.getType2()) {
                                    case "5": // 5,12,14?????????????????????
                                    case "12":
                                    case "14":
                                        startActivity(msgEntity, NotifityInfoActivity.class);
                                        break;
                                    case "11":// 11:????????????????????????????????????????????????
                                        mContext.startActivity(new Intent(mContext, NoClassStuActivity.class)
                                                .putExtra("msgEntity", msgEntity)
                                                .putExtra("key", "6"));
                                        break;
                                    case "16":// 16?????????????????????????????????
                                        mContext.startActivity(new Intent(mContext, NoClassStuActivity.class)
                                                .putExtra("msgEntity", msgEntity)
                                                .putExtra("key", "4"));
                                        break;
                                    case "17":// 17,18????????????????????????????????????????????????
                                    case "18":
                                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.STATE_EVENT, 1));
                                        break;
                                    case "19":// 19???????????????????????????
                                        startActivity(msgEntity, StuOrderActivity.class);
                                        break;
                                    case "29":// 29????????????????????????????????????
                                        mContext.startActivity(new Intent(mContext, FormalActivity.class)
                                                .putExtra("msgEntity", msgEntity)
                                                .putExtra("status", "3"));// 3?????????????????????
                                        break;
                                    default:
                                        startActivity(msgEntity, NotifityInfoActivity.class);
                                        break;
                                }
//                        } else if (msgEntity.getType().equals("26")) {// 26?????????????????????
//                            startActivity(msgEntity, MomentActivity.class);
                            }
                        }
                    });
                }
            });
        });
        rlReadAicz.setOnClickListener(v -> {
            if (msgEntity.getType2().equals("74")) {// ?????????????????????
                mContext.startActivity(new Intent(mContext, UserTermActivity.class)
                        .setData(Uri.parse(msgEntity.getContent()))
                        .putExtra("openType", "4") // ?????????????????????
                );
            } else if (msgEntity.getType2().equals("75")) {// ????????????????????????
                mContext.startActivity(new Intent(mContext, UserTermActivity.class)
                        .setData(Uri.parse(msgEntity.getContent()))
                        .putExtra("openType", "5") // 3???????????????????????????
                );
            }
        });
        btnAddOrg.setOnClickListener(v -> {
            EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_ADD_ORG));
        });
        btnCreateOrg.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, CreateOrgHintActivity.class));
        });
        tvSelectDetial.setOnClickListener(v -> {
            // ?????????&????????????????????????
            instance.teacherOrgStrand((Activity) mContext, msgEntity, isLeave -> {
                if (isLeave) {
                    instance.showSuccessDialog((Activity) mContext
                            , mContext.getString(R.string.string_have_leave));
                } else {
                    instance.getOrgInfo(msgEntity.getOrgId(), (Activity) mContext, "2", isRole -> {
                        if (isRole) {// ????????????????????????
                            setDynamicRead(msgEntity);
                            if (msgEntity.getType().equals("26")) {// 26?????????????????????
                                mContext.startActivity(new Intent(mContext, CourserFeedbackActivity.class)
                                        .putExtra("imputType", "2")
                                        .putExtra("msgEntity", msgEntity)
                                        .putExtra("circleId", msgEntity.getCircleId()));// imputType???1:????????????????????????????????? 2:???????????????????????????
                            }
                        }
                    });
                }
            });
        });
        tvSelectAll.setOnClickListener(v -> {
            // ?????????&????????????????????????
            instance.teacherOrgStrand((Activity) mContext, msgEntity, isLeave -> {
                if (isLeave) {
                    instance.showSuccessDialog((Activity) mContext
                            , mContext.getString(R.string.string_have_leave));
                } else {
                    instance.getOrgInfo(msgEntity.getOrgId(), (Activity) mContext, "2", isRole -> {
                        if (isRole) {// ????????????????????????
                            if (msgEntity.getType().equals("26")) {// 26?????????????????????
                                startActivity(msgEntity, MomentActivity.class);
                            }
                        }
                    });
                }
            });
        });
        tvCheckDetial.setOnClickListener(v -> {
            // ?????????&????????????????????????
            instance.teacherOrgStrand((Activity) mContext, msgEntity, isLeave -> {
                if (isLeave) {
                    instance.showSuccessDialog((Activity) mContext
                            , mContext.getString(R.string.string_have_leave));
                } else {
                    instance.getOrgInfo(msgEntity.getOrgId(), (Activity) mContext, "2", isRole -> {
                        if (isRole) {// ????????????????????????
                            setDynamicRead(msgEntity);
                            if (msgEntity.getType().equals("9") || msgEntity.getType().equals("10") || msgEntity.getType().equals("12")) {
                                startActivity(msgEntity, CourserActivity.class);
                            } else if (msgEntity.getType().equals("13")) {// 13.??????????????????
                                startActivity(msgEntity, CourserActivity.class);
                            } else if (msgEntity.getType().equals("14")) {// 14???|82???????????????
                                startActivity(msgEntity, CourserCoverActivity.class);
                            } else if (msgEntity.getType().equals("19")) {// 19?????????????????????
                                childSignStatus(msgEntity);
                            } else if (msgEntity.getType().equals("20")) {// 20?????????????????????
                                startActivity(msgEntity, CourserActivity.class);
                            }
                        }
                    });
                }
            });
        });
        tvCheckAll.setOnClickListener(v -> {
            // ?????????&????????????????????????
            instance.teacherOrgStrand((Activity) mContext, msgEntity, isLeave -> {
                if (isLeave) {
                    instance.showSuccessDialog((Activity) mContext
                            , mContext.getString(R.string.string_have_leave));
                } else {
                    instance.getOrgInfo(msgEntity.getOrgId(), (Activity) mContext, "2", isRole -> {
                        if (isRole) {// ????????????????????????
                            if (msgEntity.getType().equals("9") || msgEntity.getType().equals("10") || msgEntity.getType().equals("12")) {
                                startActivity(msgEntity, StuLevaeActivity.class);
                            } else if (msgEntity.getType().equals("13")) {// 13.??????????????????
                                startActivity(msgEntity, CourseNumActivity.class);
                            } else if (msgEntity.getType().equals("14")) {// 14???|82???????????????
                                startActivity(msgEntity, NullRemindActivity.class);
                            } else if (msgEntity.getType().equals("19")) {// 19?????????????????????
                                startActivity(msgEntity, EliminateWorkActivity.class);
                            } else if (msgEntity.getType().equals("20")) {// 20?????????????????????
                                startActivity(msgEntity, StuOrdersActivity.class);
                            }
                        }
                    });
                }
            });
        });
        rlSignLevae.setOnClickListener(v -> {
            if (msgEntity.getType().equals("9") || msgEntity.getType().equals("10") || msgEntity.getType().equals("12")
                    || msgEntity.getType().equals("13") || msgEntity.getType().equals("14")
                    || msgEntity.getType().equals("19") || msgEntity.getType().equals("20")) {
                return;
            }
            // ?????????&????????????????????????
            instance.teacherOrgStrand((Activity) mContext, msgEntity, isLeave -> {
                if (isLeave) {
                    instance.showSuccessDialog((Activity) mContext
                            , mContext.getString(R.string.string_have_leave));
                } else {
                    instance.getOrgInfo(msgEntity.getOrgId(), (Activity) mContext, "2", isRole -> {
                        if (isRole) {// ????????????????????????
//                        if (msgEntity.getType().equals("9") || msgEntity.getType().equals("10") || msgEntity.getType().equals("12")) {
//                            startActivity(msgEntity, StuLevaeActivity.class);
//                        } else
                            if (msgEntity.getType().equals("11")) {
                                startActivity(msgEntity, StatisReportActivity.class);
//                        } else if (msgEntity.getType().equals("13")) {// 13.??????????????????
//                            startActivity(msgEntity, CourseNumActivity.class);
//                        } else if (msgEntity.getType().equals("14")) {// 14???|82???????????????
//                            startActivity(msgEntity, NullRemindActivity.class);
                            } else if (msgEntity.getType().equals("15")) {// 15???|83?????????????????????
                                startActivity(msgEntity, OrgNotifyActivity.class);
                            } else if (msgEntity.getType().equals("16")) {// 16???|84???????????????
                                setDynamicRead(msgEntity);// ??????????????????
                                startActivity(msgEntity, OrgNotifyActivity.class);
                            } else if (msgEntity.getType().equals("17")) {// 17?????????????????????
                                getOrgTeachers(msgEntity.getOrgId(), "2", "1");
                            } else if (msgEntity.getType().equals("18")) {// 18?????????????????????
                                getAudit(msgEntity.getOrgId());
//                        } else if (msgEntity.getType().equals("19")) {// 19?????????????????????
//                            startActivity(msgEntity, EliminateWorkActivity.class);
//                        } else if (msgEntity.getType().equals("20")) {// 20?????????????????????
//                            startActivity(msgEntity, StuOrdersActivity.class);
                            } else if (msgEntity.getType().equals("21")) {// 21????????????????????????
                                mContext.startActivity(new Intent(mContext, FormalActivity.class)
                                        .putExtra("msgEntity", msgEntity)
                                        .putExtra("status", "3"));// 3?????????????????????
                            } else if (msgEntity.getType().equals("22")) {// 22?????????????????????
                                mContext.startActivity(new Intent(mContext, NoClassStuActivity.class)
                                        .putExtra("msgEntity", msgEntity)
                                        .putExtra("key", "4"));
                            } else if (msgEntity.getType().equals("23")) {// 23??????????????????????????????
                                instance.showSuccessDialog((Activity) mContext
                                        , mContext.getString(R.string.function_limit));
                            } else if (msgEntity.getType().equals("24")) {// 24??????????????????
                                startActivity(msgEntity, InitDataActivity.class);
                            } else if (msgEntity.getType().equals("25")) {// 25???????????????
                                mContext.startActivity(new Intent(mContext, NewWorkActivity.class)
                                        .putExtra("msgEntity", msgEntity));
                                ((Activity) mContext).overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//??????????????????
                            } else if (msgEntity.getType().equals("27")) {// 27????????????????????????
                                mContext.startActivity(new Intent(mContext, NoClassStuActivity.class)
                                        .putExtra("msgEntity", msgEntity)
                                        .putExtra("key", "6"));
                            }
                        }
                    });
                }
            });
        });
    }

    private void setDynamicRead(MessageEntity msgEntity) {
        HttpManager.getInstance().doSetDynamicRead("StateAdapter", msgEntity.getOrgId(), msgEntity.getType()
                , new HttpCallBack<BaseDataModel<MessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_TEACHER));
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout((Activity) mContext);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    private void setTitleDetail(Context context, String orgName, boolean isShow, String content) {
        tvSignLeaveTitle.setText(orgName);
        tvSignLeaveContent.setVisibility(isShow ? View.VISIBLE : View.GONE);
        tvStuDetail.setText(content);
        tvStuDetail.setTextColor(context.getResources().getColor(R.color.color_3));
    }

    private void setVisibilitys(boolean isItem, boolean isRead, boolean isAdd, boolean isSignLevae) {
        clItem.setVisibility(isItem ? View.VISIBLE : View.GONE);
        rlReadAicz.setVisibility(isRead ? View.VISIBLE : View.GONE);
        rlAddAicz.setVisibility(isAdd ? View.VISIBLE : View.GONE);
        rlSignLevae.setVisibility(isSignLevae ? View.VISIBLE : View.GONE);
    }

    private void startActivity(MessageEntity msgEntity, Class activity) {
        mContext.startActivity(new Intent(mContext, activity)
                .putExtra("msgEntity", msgEntity)
                .putExtra("courseId", msgEntity.getTargetId()));
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_state_list;
    }

    @Override
    public int getItemCount() {
        return messageAllList.size();
    }

    public void setData(List<MessageEntity> messageAllList) {
        this.messageAllList.clear();
        this.messageAllList.addAll(messageAllList);
        notifyDataSetChanged();
    }

    /**
     * 48.?????????????????????
     */
    private ArrayList<StudentEntity> auditData = new ArrayList<>();

    private void getOrgTeachers(String orgId, String status, String leave_status) {
        HttpManager.getInstance().doGetOrgTeachers("TeacherMsgActivity", orgId, status,
                leave_status, new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        if (TextUtils.equals("2", "2")) {//?????????
                            auditData.clear();
                            auditData.addAll(data.getData());
                            mContext.startActivity(new Intent(mContext, AuditActivity.class)
                                    .putExtra("auditData", auditData)
                                    .putExtra("type", "2"));//type 1: ???????????? 2: ????????????
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("TeacherMsgActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout((Activity) mContext);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("TeacherMsgActivity onFail", throwable.getMessage());
                    }
                });
    }

    private void getAudit(String orgId) {
        //21.??????????????????????????????
        HttpManager.getInstance().doGetVerifyChild("StateAdapter", orgId,
                new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        auditData.clear();
                        auditData.addAll(data.getData());
                        mContext.startActivity(new Intent(mContext, AuditActivity.class)
                                .putExtra("auditData", auditData)
                                .putExtra("type", "1"));//type 1: ???????????? 2: ????????????
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("StateAdapter onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout((Activity) mContext);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("StateAdapter throwable", throwable.getMessage());
                    }
                });
    }

    private void getLessonCardLog(MessageEntity msgEntity) {
        HttpManager.getInstance().doGetLessonCardLog("EliminateWorkAdapter", msgEntity.getOrgId(),
                msgEntity.getTargetId(), msgEntity.getChildId(),
                new HttpCallBack<BaseDataModel<CourseEntity>>((Activity) mContext) {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        // ???????????????
                        List<CourseEntity> entity = data.getData();
                        if (entity.size() > 0) {
                            msgEntity.setCourseList(entity);
                            msgEntity.setCount("1");// ?????? ??????????????????
                            startActivity(msgEntity, EliminateDetailActivity.class);
                        } else {
                            // ?????????????????????
                            getStudentCourseCard(msgEntity);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("EliminateWorkAdapter onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout((Activity) mContext);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("EliminateWorkAdapter onError", throwable.getMessage());
                    }
                });
    }

    private void getStudentCourseCard(MessageEntity msgEntity) {
        HttpManager.getInstance().doGetStuCard("EliminateDetailActivity",
                msgEntity.getOrgId(), msgEntity.getTargetId(), msgEntity.getChildId()
                , new HttpCallBack<BaseDataModel<CourseEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<CourseEntity> data) {
                        ArrayList<Object> mListCard = new ArrayList<>(data.getData());
                        if (mListCard.size() > 0) {
                            startActivity(msgEntity, ArtificialActivity.class);
                        } else {
                            msgEntity.setCount(mListCard.size() + "");// ??????????????????
                            startActivity(msgEntity, EliminateDetailActivity.class);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("EliminateDetailActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout((Activity) mContext);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("EliminateDetailActivity onError", throwable.getMessage());
                    }
                });
    }

    private void childSignStatus(MessageEntity msgEntity) {
        HttpManager.getInstance().doChildSignStatus("EliminateDetailActivity",
                msgEntity.getChildId(), msgEntity.getTargetId(),
                new HttpCallBack<BaseDataModel<MessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MessageEntity> data) {
                        // status 1???????????? 2???????????????
                        MessageEntity msg = data.getData().get(0);
                        msgEntity.setStatus(msg.getStatus());
                        if (msg.getStatus().equals("1")) {
                            getLessonCardLog(msgEntity);
                        } else {
                            msgEntity.setCount("1");// ?????? ??????????????????
                            startActivity(msgEntity, EliminateDetailActivity.class);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout((Activity) mContext);
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
