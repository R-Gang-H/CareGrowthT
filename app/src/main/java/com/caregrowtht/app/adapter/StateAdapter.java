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
import com.caregrowtht.app.activity.AuditActivity;
import com.caregrowtht.app.activity.CourseNumActivity;
import com.caregrowtht.app.activity.CourserActivity;
import com.caregrowtht.app.activity.CourserCardMsgActivity;
import com.caregrowtht.app.activity.CourserCoverActivity;
import com.caregrowtht.app.activity.CourserFeedbackActivity;
import com.caregrowtht.app.activity.CreateOrgHintActivity;
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
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.OrgNotifyEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
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
 * 动态适配器
 */
public class StateAdapter extends XrecyclerAdapter {

    private Context mContext;

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
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        // 强行关闭复用
        holder.setIsRecyclable(false);
        MessageEntity msgEntity = messageAllList.get(position);
        switch (msgEntity.getType()) {
            case "1":// 1：课程代办
            case "26":// 26：课程反馈提醒
                ivType.setImageResource(R.mipmap.ic_type_course);
                setVisibilitys(true, false, false, false);
                break;
            case "2":// 2：课程反馈
                ivType.setImageResource(R.mipmap.ic_type_feedback);
                setVisibilitys(true, false, false, false);
                break;
            case "3":// 3：机构待办
                ivType.setImageResource(R.mipmap.ic_type_org);
                setVisibilitys(true, false, false, false);
                break;
            case "4":// 4：机构通知
            case "5":// 5：系统通知
                ivType.setImageResource(R.mipmap.ic_type_noti);
                setVisibilitys(true, false, false, false);
                break;
            case "6":// 6：每日工作日报
                ivType.setImageResource(R.mipmap.ic_work_daily);
                setVisibilitys(true, false, false, false);
                break;
            case "7":// 7：创始人看懂爱成长
                setVisibilitys(false, true, false, false);
                break;
            case "8":// 8：欢迎加入爱成长
                setVisibilitys(false, false, true, false);
                break;
            case "9":// 9：有学员请假汇总动态
            case "10":// 10：签到提醒汇总动态
            case "11":// 11：每日日报汇总动态
            case "12":// 12：未发布课程反馈提醒汇总动态
            case "13":// 13：最小开课人数提醒
            case "14":// 14：空位提醒
            case "15":// 15：我的机构通知
            case "16":// 16：通知管理
            case "17":// 17：审核教师提醒
            case "18":// 18：审核学员提醒
            case "19":// 19：人工消课提醒
            case "20":// 20：学员预约提醒
            case "21":// 21：非活跃学员提醒
            case "22":// 22：学员续费提醒
            case "23":// 23：机构主页未设置提醒
            case "24":// 24：初始化提醒
            case "25":// 25：排课提醒
            case "27":// 27：未出勤学员提醒
                setVisibilitys(false, false, false, true);
                break;
        }
        ivStatus.setVisibility(View.GONE);
        llWorkDaily.setVisibility(View.GONE);
        tvHandlerName.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);
        tvTitleEnd.setVisibility(View.GONE);
        clMessage.setVisibility(!TextUtils.isEmpty(msgEntity.getCircleLikeCount())
                || !TextUtils.isEmpty(msgEntity.getCircleCommentCount()) ? View.VISIBLE : View.GONE);
        clEvent.setVisibility(TextUtils.equals(msgEntity.getCircleId(), "0") ? View.GONE : View.VISIBLE);
        UserManager instance = UserManager.getInstance();
        if (msgEntity.getType().equals("6")) {// 6：每日工作日报
            tvTitle.setVisibility(View.VISIBLE);
            llWorkDaily.setVisibility(View.VISIBLE);
            String data = DateUtil.getDate(Long.parseLong(msgEntity.getUpdateTime()), "yyyy年MM月dd日");
            String week = TimeUtils.getWeekByDateStr(data);//获取周几
            tvTime.setText(String.format("%s\t%s", data, week));
            tvTitleContent.setText(msgEntity.getOrgName());
            try {
                JSONObject jsonObject = new JSONObject(msgEntity.getContent());
                String courseCount = instance.getJsonString(jsonObject, "dpTodayCourseCount");
                String courseHour = instance.getJsonString(jsonObject, "dpTodayCourseHourMinutes");
                tvCourseCountHour.setText(Html.fromHtml(String.format("机构排课情况:\t<font color='#333333'>%s节/%s</font>", courseCount, courseHour)));
                String todayIncome = instance.getJsonString(jsonObject, "dpTodayCourseIncome");
                tvTodayCourseIncome.setText(Html.fromHtml(String.format("今日消课收入:\t<font color='#333333'>%s元</font>", String.valueOf(Float.parseFloat(todayIncome) / 100))));
                String todayCardIncome = instance.getJsonString(jsonObject, "dpTodayCourseCardIncome");
                tvTodayAddIncome.setText(Html.fromHtml(String.format("今日新增收入:\t<font color='#333333'>%s元</font>", String.valueOf(Float.parseFloat(todayCardIncome) / 100))));
                String todayNewStu = instance.getJsonString(jsonObject, "dpTodayNewStudent");
                tvTodayAddStu.setText(Html.fromHtml(String.format("今日新增学员:\t<font color='#333333'>%s人</font>", todayNewStu)));
                String todayRefund = instance.getJsonString(jsonObject, "dpTodayRefund");
                tvTodayRefund.setText(Html.fromHtml(String.format("今日退费:\t<font color='#333333'>%s元</font>", String.valueOf(Float.parseFloat(todayRefund) / 100))));
                String monthIncome = instance.getJsonString(jsonObject, "dpMonthCourseIncome");
                tvMonthCourseIncome.setText(Html.fromHtml(String.format("本月消课收入:\t<font color='#333333'>%s元</font>", String.valueOf(Float.parseFloat(monthIncome) / 100))));
                String monthCardIncome = instance.getJsonString(jsonObject, "dpMonthCourseCardIncome");
                tvMonthAddIncome.setText(Html.fromHtml(String.format("本月新增收入:\t<font color='#333333'>%s元</font>", String.valueOf(Float.parseFloat(monthCardIncome) / 100))));
                String monthNewStu = instance.getJsonString(jsonObject, "dpMonthNewStudent");
                tvMonthAddStu.setText(Html.fromHtml(String.format("本月新增学员:\t<font color='#333333'>%s人</font>", monthNewStu)));
                String monthRefund = instance.getJsonString(jsonObject, "dpMonthRefund");
                tvMonthRefund.setText(Html.fromHtml(String.format("本月退费:\t<font color='#333333'>%s元</font>", String.valueOf(Float.parseFloat(monthRefund) / 100))));
                String yinChuQin = instance.getJsonString(jsonObject, "dpYingChuQin");
                String shiJiChuQin = instance.getJsonString(jsonObject, "dpShiJiChuQin");
                String qingJia = instance.getJsonString(jsonObject, "dpQingjia");
                String weiChuli = instance.getJsonString(jsonObject, "dpWeiChuLi");
                tvStuWork.setText(Html.fromHtml(String.format("学员出勤情况:\t<font color='#333333'>预计出勤学员%s人、实际签到%s人、请假%s人、未处理%s人</font>"
                        , yinChuQin, shiJiChuQin, qingJia, weiChuli)));
                JSONArray teachers = jsonObject.getJSONArray("teacher");
                llTeacherWork.removeAllViews();
                TextView workTv = new TextView(mContext);
                workTv.setTextSize(16);
                workTv.setText("教师工作完成情况:");
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
                    textView.setText(String.format("%s老师:\t出勤完成%s/%s节课\n" +
                                    tvT + "\t\t\t\t\t\t\t\t\t\t\t发布课程反馈%s/%s节课"// 11
                            , teacherName, endCount, courseCouont, feedCount, courseCouont));
                    llTeacherWork.addView(textView);
                }
            } catch (JSONException e) {
                LogUtils.e("StateAdapter", e.getMessage());
            }
        } else if (msgEntity.getType().equals("7") || msgEntity.getType().equals("8")) {
            if (msgEntity.getType2().equals("74")) {// 来自创始人的信
                ivIcon.setImageResource(R.mipmap.icon_originator);
                tvText.setText("来自创始人的信");
            } else if (msgEntity.getType2().equals("75")) {// 一分钟看懂爱成长
                ivIcon.setImageResource(R.mipmap.icon_state);
                tvText.setText("3分钟快速了解爱成长");
            }
            tvAddTime.setText(DateUtil.getDate(Long.parseLong(msgEntity.getUpdateTime()), "MM月dd日 HH:mm"));
            // 1待处理 2已处理 3不需要处理
            switch (msgEntity.getStatus()) {//动态的状态 不需要处理的动态该字段没有值
                case "1"://1：待处理
                    ivAddStatus.setText("待处理");
                    ivAddStatus.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_pending));
                    break;
                case "2"://2：已完成(已处理)
                    ivAddStatus.setText("已完成");
                    ivAddStatus.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_complet));
                    break;
            }
        } else if (msgEntity.getType().equals("9") || msgEntity.getType().equals("10")
                || msgEntity.getType().equals("11") || msgEntity.getType().equals("12")
                || msgEntity.getType().equals("13") || msgEntity.getType().equals("14")
                || msgEntity.getType().equals("15") || msgEntity.getType().equals("16")
                || msgEntity.getType().equals("17") || msgEntity.getType().equals("18")
                || msgEntity.getType().equals("19") || msgEntity.getType().equals("20")
                || msgEntity.getType().equals("21") || msgEntity.getType().equals("22")
                || msgEntity.getType().equals("23")
                || msgEntity.getType().equals("27")) {
            tvOrgName.setText(msgEntity.getOrgName());
            long courseStartAt = Long.valueOf(msgEntity.getCourseStartAt());
            String Month = TimeUtils.getDateToString(courseStartAt, "MM月dd日");
            String data = DateUtil.getDate(courseStartAt, "yyyy年MM月dd日");
            String week = TimeUtils.getWeekByDateStr(data);//获取周几
            String time = TimeUtils.getDateToString(courseStartAt, "HH:mm");
            String teacherName = msgEntity.getTeacherName();
            String courseName = msgEntity.getCourseName();
            tvSignLeaveContent.setText(String.format("%s\t\t%s\t\t%s\n%s\t\t%s",
                    Month, week, time, teacherName, courseName));
            ivSignLeaveStatus.setVisibility(msgEntity.getRead().equals("0") ? View.VISIBLE : View.GONE);
            tvSignLeaveTime.setText(String.format("%s\t\t%s", DateUtil.getDate(Long.parseLong(msgEntity.getUpdateTime()), "MM月dd日 HH:mm")
                    , msgEntity.getRead().equals("0") ? "更新" : ""));
            if (msgEntity.getType().equals("9") || msgEntity.getType().equals("17")
                    || msgEntity.getType().equals("18") || msgEntity.getType().equals("19")) {
                if (msgEntity.getType().equals("9")) {// 9：有学员请假汇总动态
                    tvSignLeaveTitle.setText("学员请假");
                } else if (msgEntity.getType().equals("17")) {// 17：审核教师提醒
                    tvSignLeaveTitle.setText("审核教师提醒");
                    ivSignLeaveType.setImageResource(R.mipmap.ic_type_org);
                    ivSignLeaveStatus.setBackgroundResource(R.mipmap.ic_pending);
                    ivSignLeaveStatus.setText("待处理");
                    tvSignLeaveContent.setVisibility(View.GONE);
                } else if (msgEntity.getType().equals("18")) {// 18：审核学员提醒
                    tvSignLeaveTitle.setText("审核学员提醒");
                    ivSignLeaveType.setImageResource(R.mipmap.ic_type_org);
                    ivSignLeaveStatus.setBackgroundResource(R.mipmap.ic_pending);
                    ivSignLeaveStatus.setText("待处理");
                    tvSignLeaveContent.setVisibility(View.GONE);
                } else if (msgEntity.getType().equals("19")) {// 19：人工消课提醒
                    tvSignLeaveTitle.setText("人工消课提醒");
                    ivSignLeaveStatus.setBackgroundResource(R.mipmap.ic_pending);
                    ivSignLeaveStatus.setText("待处理");
                }
                rlSign.setVisibility(View.VISIBLE);
                tvStuDetail.setVisibility(View.GONE);
                clRb.setVisibility(View.GONE);
                ivLevaeAuthorAvatar.setTextAndColor(TextUtils.isEmpty(msgEntity.getShowName()) ? ""
                        : msgEntity.getShowName().substring(0, 1), mContext.getResources().getColor(R.color.b0b2b6));
                GlideUtils.setGlideImg(mContext, msgEntity.getShowHeadImage(), 0, ivLevaeAuthorAvatar);
                tvLevaeAuthorName.setText(String.format("%s(等)", msgEntity.getShowName()));
            } else if (msgEntity.getType().equals("10") || msgEntity.getType().equals("13")
                    || msgEntity.getType().equals("14") || msgEntity.getType().equals("15")
                    || msgEntity.getType().equals("16") || msgEntity.getType().equals("20")
                    || msgEntity.getType().equals("21") || msgEntity.getType().equals("22")
                    || msgEntity.getType().equals("23") || msgEntity.getType().equals("24")
                    || msgEntity.getType().equals("27")) {
                rlSign.setVisibility(View.GONE);
                tvStuDetail.setVisibility(View.VISIBLE);
                clRb.setVisibility(View.GONE);
                if (msgEntity.getType().equals("10")) {// 10：签到提醒汇总动态
                    tvSignLeaveTitle.setText("签到提醒");
                    ivSignLeaveStatus.setBackgroundResource(R.mipmap.ic_pending);
                    ivSignLeaveStatus.setText("待处理");
                    try {
                        JSONObject jsonObject = new JSONObject(msgEntity.getContent());
                        String childNum = instance.getJsonString(jsonObject, "childNum");
                        String sign = instance.getJsonString(jsonObject, "sign");
                        String leav = instance.getJsonString(jsonObject, "leav");
                        String noTake = instance.getJsonString(jsonObject, "noTake");
                        tvStuDetail.setText(String.format("学员\t%s人\t\t|\t\t签到\t%s人\t\t|\t\t请假\t%s人\t\t|\t\t待处理\t%s人",
                                childNum, sign, leav, noTake));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msgEntity.getType().equals("13")) {// 13.开课人数提醒
                    tvSignLeaveTitle.setText("开课人数提醒");
                    tvStuDetail.setText(String.format("学员\t%s人\t\t|\t\t开课人数\t%s人"
                            , msgEntity.getChildNum(), msgEntity.getMinCount()));
                } else if (msgEntity.getType().equals("14")) {// 14：|82：空位提醒
                    tvSignLeaveTitle.setText("空位提醒");
                    ivSignLeaveStatus.setBackgroundResource(R.mipmap.ic_pending);
                    ivSignLeaveStatus.setText("待处理");
                    tvStuDetail.setText(String.format("空位\t%s人", msgEntity.getChildNum()));
                } else if (msgEntity.getType().equals("15")) {// 15：|83：我的机构通知
                    ivSignLeaveType.setImageResource(R.mipmap.ic_type_noti);
                    setTitleDetail(context, "机构通知", false, msgEntity.getContent());
                } else if (msgEntity.getType().equals("16")) {// 16：|84：通知管理
                    ivSignLeaveType.setImageResource(R.mipmap.ic_type_org);
                    setTitleDetail(context, "通知管理", false,
                            String.format("已发送通知\n%s\n点击查看通知对象情况", msgEntity.getContent()));
                } else if (msgEntity.getType().equals("20")) {// 20：学员预约提醒
                    tvSignLeaveTitle.setText("学员预约提醒");
                    tvStuDetail.setText(String.format("学员\t%s位", msgEntity.getChildNum()));
                } else if (msgEntity.getType().equals("21")) {// 21：非活跃学员提醒
                    ivSignLeaveType.setImageResource(R.mipmap.ic_type_org);
                    setTitleDetail(context, "非活跃学员提醒", false,
                            String.format("非活跃学员:%s位", msgEntity.getChildNum()));
                } else if (msgEntity.getType().equals("22")) {// 22：学员续费提醒
                    ivSignLeaveType.setImageResource(R.mipmap.ic_type_org);
                    setTitleDetail(context, "学员续费提醒", false,
                            String.format("续费提醒学员:%s位", msgEntity.getChildNum()));
                } else if (msgEntity.getType().equals("23")) {// 23：机构主页未设置提醒
                    ivSignLeaveType.setImageResource(R.mipmap.ic_type_feedback);
                    setTitleDetail(context, "机构主页", false, msgEntity.getContent());
                } else if (msgEntity.getType().equals("24")) {// 24：初始化提醒
                    ivSignLeaveType.setImageResource(R.mipmap.ic_type_feedback);
                    setTitleDetail(context, "初始化提醒", false, msgEntity.getContent());
                } else if (msgEntity.getType().equals("25")) {// 25：排课提醒
                    ivSignLeaveType.setImageResource(R.mipmap.ic_type_feedback);
                    setTitleDetail(context, "排课提醒", false, msgEntity.getContent());
                } else if (msgEntity.getType().equals("27")) {// 27：未出勤学员提醒
                    ivSignLeaveType.setImageResource(R.mipmap.ic_type_org);
                    setTitleDetail(context, "未出勤学员提醒", false,
                            String.format("超过一个月未上课的学员:%s位", msgEntity.getChildNum()));
                }
            } else if (msgEntity.getType().equals("12")) {// 12：未发布课程反馈提醒汇总动态
                tvSignLeaveTitle.setText("未发布课程反馈提醒");
                ivSignLeaveStatus.setBackgroundResource(R.mipmap.ic_pending);
                ivSignLeaveStatus.setText("待处理");
                rlSign.setVisibility(View.GONE);
                tvStuDetail.setVisibility(View.GONE);
                clRb.setVisibility(View.GONE);
            } else if (msgEntity.getType().equals("11")) {// 11：每日日报汇总动态
                ivSignLeaveType.setImageResource(R.mipmap.ic_work_daily);
                tvSignLeaveTitle.setText("每日日报");
                tvSignLeaveContent.setVisibility(View.GONE);
                rlSign.setVisibility(View.GONE);
                tvStuDetail.setVisibility(View.GONE);
                Vline1.setVisibility(View.GONE);
                tvSignLeaveTime.setVisibility(View.GONE);
                long updateTime = Long.valueOf(msgEntity.getUpdateTime());
                String rbMonth = TimeUtils.getDateToString(updateTime, "MM月dd日");
                String rbData = DateUtil.getDate(updateTime, "yyyy年MM月dd日");
                String rbWeek = TimeUtils.getWeekByDateStr(rbData);//获取周几
                tvInTime.setText(String.format("%s\t%s", rbMonth, rbWeek));
                try {
                    JSONObject jsonObject = new JSONObject(msgEntity.getContent());
                    String todayIncome = instance.getJsonString(jsonObject, "dpTodayCourseIncome");
                    tvDayXkMonery.setText(String.format("%s元", String.valueOf(Float.parseFloat(todayIncome) / 100)));
                    String todayCardIncome = instance.getJsonString(jsonObject, "dpTodayCourseCardIncome");
                    tvDayXzMonery.setText(String.format("%s元", String.valueOf(Float.parseFloat(todayCardIncome) / 100)));
                    String todayRefund = instance.getJsonString(jsonObject, "dpTodayRefund");
                    tvDayTfMonery.setText(String.format("%s元", String.valueOf(Float.parseFloat(todayRefund) / 100)));
                    String monthIncome = instance.getJsonString(jsonObject, "dpMonthCourseIncome");
                    tvMonthXkMonery.setText(String.format("%s元", String.valueOf(Float.parseFloat(monthIncome) / 100)));
                    String monthCardIncome = instance.getJsonString(jsonObject, "dpMonthCourseCardIncome");
                    tvMonthXzMonery.setText(String.format("%s元", String.valueOf(Float.parseFloat(monthCardIncome) / 100)));
                    String monthRefund = instance.getJsonString(jsonObject, "dpMonthRefund");
                    tvMonthTfMonery.setText(String.format("%s元", String.valueOf(Float.parseFloat(monthRefund) / 100)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            tvTime.setText(DateUtil.getDate(Long.parseLong(msgEntity.getUpdateTime()), "MM月dd日 HH:mm"));
            switch (msgEntity.getStatus()) {//动态的状态 不需要处理的动态该字段没有值
                case "1"://1：待处理
                    ivStatus.setVisibility(View.VISIBLE);
                    ivStatus.setText("待处理");
                    ivStatus.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_pending));
                    break;
                case "2"://2：已完成(已处理)
                    ivStatus.setVisibility(View.VISIBLE);
                    tvHandlerName.setVisibility(View.VISIBLE);
                    ivStatus.setText("已完成");
                    ivStatus.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_complet));
                    tvHandlerName.setText(String.format("%s\t\t%s\t\t完成", msgEntity.getHandlerName(), DateUtil.getDate(Long.parseLong(msgEntity.getHandleTime()), "MM月dd日 HH:mm")));
                    break;
            }
            //表示机构发出的通知是否需要回执type=5的时候该字段有值，其余类型该字段没有值
            if (msgEntity.getType().equals("4") || msgEntity.getType().equals("5")) {//4,5：机构和系统发出的通知
                switch (msgEntity.getReceipt()) {
//                case "1":// 1：不需要回执
//                    ivStatus.setVisibility(View.VISIBLE);
//                    ivStatus.setText("不需要回执");
//                    ivStatus.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_pending));
//                    break;
                    case "2"://2：待回执
                        ivStatus.setVisibility(View.VISIBLE);
                        ivStatus.setText("待回执");
                        ivStatus.setBackground(ResourcesUtils.getDrawable(R.mipmap.ic_pending));
                        break;
                    case "3"://3：已经回执
                        ivStatus.setVisibility(View.VISIBLE);
                        ivStatus.setText("已经回执");
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
            String sentNotice = "请点击查看详情";
            if (isCircle) {// 课程反馈
                orgContent = msgEntity.getOrgName() + "\n教师" + msgEntity.getCircleAuthor() + "为" +
                        DateUtil.getDate(Long.parseLong(msgEntity.getCircleCourseBeginTime()), "MM月dd日 HH:mm") + "~"
                        + DateUtil.getDate(Long.parseLong(msgEntity.getCircleCourseEndTime()), "HH:mm") + "的\"" +
                        msgEntity.getCircleCourseName() + "\"发布了一条课程反馈";
            } else if (msgEntity.getType().equals("4")) {//4：机构发出的通知
                String newNotice = "";
                if (msgEntity.getType2().equals("20")) {// 发送者
                    newNotice = "已发送通知:";
                    if (msgEntity.getReceipt().equals("4")) {
                        sentNotice = "请点击查看通知的回执状态";
                    }
                } else if (msgEntity.getType2().equals("24")) {// 接收者
                    newNotice = "新通知:";
                    if (msgEntity.getReceipt().equals("1")) {
                        sentNotice = "请点击查看详情";
                    } else {
                        sentNotice = "请点击查看详情并回执";
                    }
                }
                orgContent = String.format("%s\n%s%s", msgEntity.getOrgName(), newNotice, msgEntity.getContent());
            } else {
                orgContent = String.format("%s\n%s", msgEntity.getOrgName(), msgEntity.getContent());
            }
            if (msgEntity.getType().equals("26")) {// 26:课程反馈
                tvTime.setText("课程反馈");
                tvTime.setTextColor(context.getResources().getColor(R.color.col_t9));
                tvTime.setTextSize(18);
                tvTitleContent.setText(msgEntity.getOrgName());
                tvCourseInfo.setVisibility(View.VISIBLE);
                long courseStartAt = Long.valueOf(msgEntity.getCircleCourseBeginTime());
                String Month = TimeUtils.getDateToString(courseStartAt, "MM月dd日");
                String data = DateUtil.getDate(courseStartAt, "yyyy年MM月dd日");
                String week = TimeUtils.getWeekByDateStr(data);//获取周几
                String time = TimeUtils.getDateToString(courseStartAt, "HH:mm");
                String teacherName = msgEntity.getCircleAuthor();
                String courseName = msgEntity.getCourseName();
                tvCourseInfo.setText(String.format("%s\t\t%s\t\t%s\n%s\t\t%s",
                        Month, week, time, teacherName, courseName));
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
                ivCircle.setScaleType(ImageView.ScaleType.FIT_XY);//fitXY的目标是填充整个ImageView，对图片进行一些缩放操作，在缩放的过程中，它不会按照原图的比例来缩放
                if (circlePicture[0].contains("mp4")) {
                    ivCircle.setCenterImgShow(true);
                }
                GlideUtils.setGlideImg(mContext, circlePicture[0], R.mipmap.ic_media_default, ivCircle);// 默认显示第一个
            }
            ivCircle.setVisibility(bannerSize > 0 ? View.VISIBLE : View.GONE);
            String circleAccessory = msgEntity.getCircleAccessory();
            tvAtter.setVisibility(TextUtils.isEmpty(circleAccessory) ? View.GONE : View.VISIBLE);
        }
//        tvAtter.setText(circleAccessory);
        clItem.setOnClickListener(v -> {
            if (msgEntity.getType().equals("6")) {
                return;
            }
            UserManager.getInstance().getOrgInfo(msgEntity.getOrgId(), (Activity) mContext, "2");// 获取权限配置参数
            if (!UserManager.getInstance().CheckIsLeave(msgEntity.getOrgId())) {// 教师已离职
                U.showToast("已离职");
                return;
            }
            if (TextUtils.equals(msgEntity.getType(), "1")) {
                // tpye2 1：请假签到 3：空位出现  4：教师审核 19：有学员预约了课程
                switch (msgEntity.getType2()) {
                    case "1": // 1：请假签到
                        startActivity(msgEntity, CourserActivity.class);
                        break;
                    case "3": // 3：空位出现
                        startActivity(msgEntity, CourserCoverActivity.class);
                        break;
                    case "21":// 21,27,28消课失败,手动消课
                    case "27":
                    case "28":
                        UserManager.getInstance().showSuccessDialog((Activity) mContext
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
                // tpye2 23：课程反馈
                switch (msgEntity.getType2()) {
                    case "22": // 22：课程反馈
                        startActivity(msgEntity, CourserActivity.class);
                        break;
                    case "23": // 23：课程反馈记录
                        mContext.startActivity(new Intent(mContext, CourserFeedbackActivity.class)
                                .putExtra("imputType", "2")
                                .putExtra("msgEntity", msgEntity)
                                .putExtra("circleId", msgEntity.getTargetId()));// imputType：1:学员详情进入的课程反馈 2:动态进入的记录详情
                        break;
                    default:
                        startActivity(msgEntity, NotifityInfoActivity.class);
                        break;
                }
            } else if (TextUtils.equals(msgEntity.getType(), "3")) {
                switch (msgEntity.getType2()) {
                    case "4": // 4：教师审核
                        getOrgTeachers(msgEntity.getOrgId());
                        break;
                    case "6": // 6：学员审核
                        getAudit(msgEntity.getOrgId());
                        break;
                    case "7": // 7：完善机构信息
                        UserManager.getInstance().showSuccessDialog((Activity) mContext
                                , mContext.getString(R.string.function_limit));
                        break;
                    case "8":// 8：导入课时卡信息
                        startActivity(msgEntity, CourserCardMsgActivity.class);
                        break;
                    case "9": // 9：活跃学员
                        mContext.startActivity(new Intent(mContext, FormalActivity.class)
                                .putExtra("msgEntity", msgEntity));
                        break;
                    case "10":// 10：新建排课
                        mContext.startActivity(new Intent(mContext, NewWorkActivity.class)
                                .putExtra("msgEntity", msgEntity));
                        ((Activity) mContext).overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
                        break;
                    case "25":// 25：机构初始化工作
                        startActivity(msgEntity, InitDataActivity.class);
                        break;
                    case "72":// 72：课程详情
                        startActivity(msgEntity, CourserActivity.class);
                        break;
                    default:
                        startActivity(msgEntity, NotifityInfoActivity.class);
                        break;
                }
            } else if (TextUtils.equals(msgEntity.getType(), "4")) {
                switch (msgEntity.getType2()) {
                    case "13":// 13：与机构解除绑定
                        U.showToast("已离职");
                        break;
                    case "20":// 20：查看通知回执的状态
                        if (msgEntity.getReceipt().equals("1") || msgEntity.getReceipt().equals("2")) {// 不需要回执/ 待回执
                            startActivity(msgEntity, NotifityInfoActivity.class);
                        } else {
                            OrgNotifyEntity notifyEntity = new OrgNotifyEntity();
                            notifyEntity.setIsReceipt("1");// 如果点击跳转通知对象,默认值是1需要回执
                            notifyEntity.setNotifiId(msgEntity.getTargetId());
                            mContext.startActivity(new Intent(mContext, NotifyObjActivity.class)
                                    .putExtra("msgEntity", msgEntity)
                                    .putExtra("notifyEntity", notifyEntity));
                            ((Activity) mContext).overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
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
                    case "5": // 5,12,14：查看消息详情
                    case "12":
                    case "14":
                        startActivity(msgEntity, NotifityInfoActivity.class);
                        break;
                    case "11":// 11:有学员近一个月没有来上课请您查看
                        mContext.startActivity(new Intent(mContext, NoClassStuActivity.class)
                                .putExtra("msgEntity", msgEntity)
                                .putExtra("key", "6"));
                        break;
                    case "16":// 16：有学员课时卡需要续费
                        mContext.startActivity(new Intent(mContext, NoClassStuActivity.class)
                                .putExtra("msgEntity", msgEntity)
                                .putExtra("key", "4"));
                        break;
                    case "17":// 17,18：排课修改了教师点击查看教师课表
                    case "18":
                        EventBus.getDefault().post(new ToUIEvent(ToUIEvent.STATE_EVENT, 1));
                        break;
                    case "19":// 19：有学员预约了课程
                        startActivity(msgEntity, StuOrderActivity.class);
                        break;
                    case "29":// 29：有学员被系统置为非活跃
                        mContext.startActivity(new Intent(mContext, FormalActivity.class)
                                .putExtra("msgEntity", msgEntity)
                                .putExtra("status", "3"));// 3：非活跃待确认
                        break;
                    default:
                        startActivity(msgEntity, NotifityInfoActivity.class);
                        break;
                }
            } else if (msgEntity.getType().equals("26")) {// 26：课程反馈提醒
                startActivity(msgEntity, MomentActivity.class);
            }
        });
        rlReadAicz.setOnClickListener(v -> {
            if (msgEntity.getType2().equals("74")) {// 来自创始人的信
                mContext.startActivity(new Intent(mContext, UserTermActivity.class)
                        .setData(Uri.parse(msgEntity.getContent()))
                        .putExtra("openType", "4") // 来自创始人的信
                );
            } else if (msgEntity.getType2().equals("75")) {// 一分钟看懂爱成长
                mContext.startActivity(new Intent(mContext, UserTermActivity.class)
                        .setData(Uri.parse(msgEntity.getContent()))
                        .putExtra("openType", "5") // 3分钟快速了解爱成长
                );
            }
        });
        btnAddOrg.setOnClickListener(v -> {
            EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_ADD_ORG));
        });
        btnCreateOrg.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, CreateOrgHintActivity.class));
        });
        rlSignLevae.setOnClickListener(v -> {
            if (msgEntity.getType().equals("9") || msgEntity.getType().equals("10") || msgEntity.getType().equals("12")) {
                startActivity(msgEntity, StuLevaeActivity.class);
            } else if (msgEntity.getType().equals("11")) {
                startActivity(msgEntity, StatisReportActivity.class);
            } else if (msgEntity.getType().equals("13")) {// 13.开课人数提醒
                startActivity(msgEntity, CourseNumActivity.class);
            } else if (msgEntity.getType().equals("14")) {// 14：|82：空位提醒
                startActivity(msgEntity, NullRemindActivity.class);
            } else if (msgEntity.getType().equals("15")) {// 15：|83：我的机构通知
                startActivity(msgEntity, OrgNotifyActivity.class);
            } else if (msgEntity.getType().equals("16")) {// 16：|84：通知管理
                startActivity(msgEntity, OrgNotifyActivity.class);
            } else if (msgEntity.getType().equals("17")) {// 17：审核教师提醒
                getOrgTeachers(msgEntity.getOrgId());
            } else if (msgEntity.getType().equals("18")) {// 18：审核学员提醒
                getAudit(msgEntity.getOrgId());
            } else if (msgEntity.getType().equals("19")) {// 19：人工消课提醒
                startActivity(msgEntity, EliminateWorkActivity.class);
            } else if (msgEntity.getType().equals("20")) {// 20：学员预约提醒
                startActivity(msgEntity, StuOrdersActivity.class);
            } else if (msgEntity.getType().equals("21")) {// 21：非活跃学员提醒
                mContext.startActivity(new Intent(mContext, FormalActivity.class)
                        .putExtra("msgEntity", msgEntity)
                        .putExtra("status", "3"));// 3：非活跃待确认
            } else if (msgEntity.getType().equals("22")) {// 22：学员续费提醒
                mContext.startActivity(new Intent(mContext, NoClassStuActivity.class)
                        .putExtra("msgEntity", msgEntity)
                        .putExtra("key", "4"));
            } else if (msgEntity.getType().equals("23")) {// 23：机构主页未设置提醒
                UserManager.getInstance().showSuccessDialog((Activity) mContext
                        , mContext.getString(R.string.function_limit));
            } else if (msgEntity.getType().equals("24")) {// 24：初始化提醒
                startActivity(msgEntity, InitDataActivity.class);
            } else if (msgEntity.getType().equals("25")) {// 25：排课提醒
                mContext.startActivity(new Intent(mContext, NewWorkActivity.class)
                        .putExtra("msgEntity", msgEntity));
                ((Activity) mContext).overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
            } else if (msgEntity.getType().equals("27")) {// 27：未出勤学员提醒
                mContext.startActivity(new Intent(mContext, NoClassStuActivity.class)
                        .putExtra("msgEntity", msgEntity)
                        .putExtra("key", "6"));
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
     * 48.获取机构的教师
     */
    private ArrayList<StudentEntity> auditData = new ArrayList<>();

    private void getOrgTeachers(String orgId) {
        HttpManager.getInstance().doGetOrgTeachers("TeacherMsgActivity", orgId, "2", "1",
                new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        if (TextUtils.equals("2", "2")) {//待审核
                            auditData.clear();
                            auditData.addAll(data.getData());
                            mContext.startActivity(new Intent(mContext, AuditActivity.class)
                                    .putExtra("auditData", auditData)
                                    .putExtra("type", "2"));//type 1: 添加学员 2: 添加教师
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("TeacherMsgActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
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
        //21.获取机构的待审核学员
        HttpManager.getInstance().doGetVerifyChild("StateAdapter", orgId,
                new HttpCallBack<BaseDataModel<StudentEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<StudentEntity> data) {
                        auditData.clear();
                        auditData.addAll(data.getData());
                        mContext.startActivity(new Intent(mContext, AuditActivity.class)
                                .putExtra("auditData", auditData)
                                .putExtra("type", "1"));//type 1: 添加学员 2: 添加教师
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("StateAdapter onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
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

}
