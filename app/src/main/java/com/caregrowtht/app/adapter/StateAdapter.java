package com.caregrowtht.app.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AlertDialog;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.AuditActivity;
import com.caregrowtht.app.activity.CourserActivity;
import com.caregrowtht.app.activity.CourserCardMsgActivity;
import com.caregrowtht.app.activity.CourserCoverActivity;
import com.caregrowtht.app.activity.CourserFeedbackActivity;
import com.caregrowtht.app.activity.FormalActivity;
import com.caregrowtht.app.activity.InitDataActivity;
import com.caregrowtht.app.activity.NewWorkActivity;
import com.caregrowtht.app.activity.NoClassStuActivity;
import com.caregrowtht.app.activity.NotifityInfoActivity;
import com.caregrowtht.app.activity.NotifyObjActivity;
import com.caregrowtht.app.activity.StuOrderActivity;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.OrgNotifyEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.ResourcesUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.ninegrid.RatioImageView;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import org.greenrobot.eventbus.EventBus;

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

    @BindView(R.id.iv_type)
    ImageView ivType;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.iv_status)
    TextView ivStatus;
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

    public StateAdapter(List datas, Context context) {
        super(datas, context);
        this.mContext = context;
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        MessageEntity msgEntity = messageAllList.get(position);
        // 1：课程代办 2：课程反馈 3：机构待办 4：机构通知 5：系统通知
        switch (msgEntity.getType()) {
            case "1":// 1：课程代办
                ivType.setImageResource(R.mipmap.ic_type_course);
                break;
            case "2":// 2：课程反馈
                ivType.setImageResource(R.mipmap.ic_type_feedback);
                break;
            case "3":// 3：机构待办
                ivType.setImageResource(R.mipmap.ic_type_org);
                break;
            case "4"://4：机构通知
                ivType.setImageResource(R.mipmap.ic_type_noti);
                break;
            case "5":// 5：系统通知
                ivType.setImageResource(R.mipmap.ic_type_noti);
                break;
        }
        tvTime.setText(DateUtil.getDate(Long.parseLong(msgEntity.getUpdateTime()), "MM月dd日 HH:mm"));
        ivStatus.setVisibility(View.GONE);
        tvHandlerName.setVisibility(View.GONE);
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
        clMessage.setVisibility(!TextUtils.isEmpty(msgEntity.getCircleLikeCount())
                || !TextUtils.isEmpty(msgEntity.getCircleCommentCount()) ? View.VISIBLE : View.GONE);
        ivAuthorAvatar.setTextAndColor(TextUtils.isEmpty(msgEntity.getCircleAuthor()) ? ""
                : msgEntity.getCircleAuthor().substring(0, 1), mContext.getResources().getColor(R.color.b0b2b6));
        GlideUtils.setGlideImg(mContext, msgEntity.getCircleIcon(), 0, ivAuthorAvatar);
        tvAuthorName.setText(msgEntity.getCircleAuthor());
        tvLikes.setText(msgEntity.getCircleLikeCount());
        tvComment.setText(msgEntity.getCircleCommentCount());

        boolean isCircle = !TextUtils.isEmpty(msgEntity.getCircleLikeCount())
                || !TextUtils.isEmpty(msgEntity.getCircleCommentCount());
        String orgContent = "";
        String sentNotice = "请点击查看详情";
        if (isCircle) {// 课程反馈
            orgContent = "教师" + msgEntity.getCircleAuthor() + "为" +
                    DateUtil.getDate(Long.parseLong(msgEntity.getCircleCourseBeginTime()), "MM月dd日 HH:mm") + "~"
                    + DateUtil.getDate(Long.parseLong(msgEntity.getCircleCourseEndTime()), "HH:mm") + "的\"" +
                    msgEntity.getCircleCourseName() + "\"发布了一条课程反馈";
        } else if (msgEntity.getType().equals("4")) {//4：机构发出的通知
            String newNotice = "";
            switch (msgEntity.getReceipt()) {
                case "2"://2：待回执
                    newNotice = "新通知:";
                    sentNotice = "请点击查看详情并回执";
                    break;
                case "3"://3：已经回执
                case "4":
                    newNotice = "已发送通知:";
                    sentNotice = "请点击查看通知的回执状态";
                    break;
            }
            orgContent = String.format("%s\n%s%s", msgEntity.getOrgName(), newNotice, msgEntity.getContent());
        } else {
            orgContent = String.format("%s\n%s", msgEntity.getOrgName(), msgEntity.getContent());
        }
        tvTitleContent.setText(orgContent);
        tvTitleEnd.setVisibility(View.VISIBLE);
        tvTitleEnd.setText(sentNotice);

        clEvent.setVisibility(TextUtils.equals(msgEntity.getCircleId(), "0") ? View.GONE : View.VISIBLE);
        tvContent.setVisibility(TextUtils.isEmpty(msgEntity.getCircleContent()) ? View.GONE : View.VISIBLE);
        tvContent.setText(msgEntity.getCircleContent());
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
//        tvAtter.setText(circleAccessory);

        holder.getView(R.id.cl_item).setOnClickListener(v -> {
            UserManager.getInstance().getOrgInfo(msgEntity.getOrgId(), (Activity) mContext, "2");// 获取权限配置参数
            if (!CheckIsLeave(msgEntity.getOrgId())) {// 教师已离职
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
                        showSuccessDialog("请登录机构端" +
                                "<font color='#52A6EF'>https://admin.ilovegrowth.cn进行处理。</font>");
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
                                .putExtra("circleId", msgEntity.getTargetId()));// imputType：1:学员详情进入的课程反馈 2:动态进入的记录详情
                        break;
                    default:
                        startActivity(msgEntity, NotifityInfoActivity.class);
                        break;
                }
            } else if (TextUtils.equals(msgEntity.getType(), "3")) {
                switch (msgEntity.getType2()) {
                    case "4": // 4：教师审核
                        getOrgTeachers();
                        break;
                    case "6": // 6：学员审核
                        getAudit();
                        break;
                    case "7": // 7：完善机构信息
                        showSuccessDialog("请在PC端设置机构主页,登录地址:" +
                                "<font color='#52A6EF'>https://admin.ilovegrowth.cn</font>");
                        break;
                    case "8":// 8：导入课时卡信息
                        startActivity(msgEntity, CourserCardMsgActivity.class);
                        break;
                    case "9": // 9：活跃学员
                        mContext.startActivity(new Intent(mContext, FormalActivity.class));
                        break;
                    case "10":// 10：新建排课
                        mContext.startActivity(new Intent(mContext, NewWorkActivity.class));
                        ((Activity) mContext).overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
                        break;
                    case "25":// 25：机构初始化工作
                        startActivity(msgEntity, InitDataActivity.class);
                        break;
                    default:
                        startActivity(msgEntity, NotifityInfoActivity.class);
                        break;
                }
            } else if (TextUtils.equals(msgEntity.getType(), "4")) {
                switch (msgEntity.getType2()) {
                    case "20":// 20：查看通知回执的状态
                        OrgNotifyEntity notifyEntity = new OrgNotifyEntity();
                        notifyEntity.setIsReceipt(msgEntity.getReceipt());
                        notifyEntity.setNotifiId(msgEntity.getTargetId());
                        mContext.startActivity(new Intent(mContext, NotifyObjActivity.class)
                                .putExtra("notifyEntity", notifyEntity));
                        ((Activity) mContext).overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
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
                                .putExtra("key", "6"));
                        break;
                    case "16":// 16：有学员课时卡需要续费
                        mContext.startActivity(new Intent(mContext, NoClassStuActivity.class)
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
                                .putExtra("status", "3"));// 3：非活跃待确认
                        break;
                    default:
                        startActivity(msgEntity, NotifityInfoActivity.class);
                        break;
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startActivity(MessageEntity msgEntity, Class activity) {
        mContext.startActivity(new Intent(mContext, activity)
                .putExtra("msgEntity", msgEntity)
                .putExtra("courseId", msgEntity.getTargetId()));
    }

    /**
     * 检查教师是否离职
     * true:在职动态 false:离职动态
     */
    private boolean CheckIsLeave(String orgId) {
        String[] passOrgIds = UserManager.getInstance().userData.getPassOrgIds().split(",");
        for (String passOrgId : passOrgIds) {
            if (passOrgId.equals(orgId)) {// 在职动态
                return true;
            }
        }
        return false;
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
     * 机构端处理提示
     */
    private void showSuccessDialog(String desc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(mContext, R.layout.dialog_teach_lib, null);
        TextView tvDesc = view.findViewById(R.id.tv_desc);
        tvDesc.setText(Html.fromHtml(desc));
        TextView tvOk = view.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(v -> dialog.dismiss());
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
    }

    /**
     * 48.获取机构的教师
     */
    private ArrayList<StudentEntity> auditData = new ArrayList<>();

    private void getOrgTeachers() {
        HttpManager.getInstance().doGetOrgTeachers("TeacherMsgActivity",
                UserManager.getInstance().getOrgId(), "2", "1",
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

    private void getAudit() {
        //21.获取机构的待审核学员
        HttpManager.getInstance().doGetVerifyChild("StateAdapter",
                UserManager.getInstance().getOrgId(),
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