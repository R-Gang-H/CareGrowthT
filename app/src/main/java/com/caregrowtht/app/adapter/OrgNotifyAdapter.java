package com.caregrowtht.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.OrgNotifyEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.ToUIEvent;
import com.caregrowtht.app.user.UserManager;
import com.caregrowtht.app.view.xrecyclerview.onitemclick.ViewOnItemClick;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * haoruigang on 2018-7-4 16:03:37
 * 机构通知适配器
 */
public class OrgNotifyAdapter extends XrecyclerAdapter {

    @BindView(R.id.iv_type)
    ImageView ivType;
    @BindView(R.id.tv_notify_count)
    TextView tvNotifyCount;
    @BindView(R.id.iv_status)
    TextView ivStatus;
    @BindView(R.id.tv_title_content)
    TextView tvTitleContent;
    @BindView(R.id.tv_handlerName)
    TextView tvHandlerName;
    @BindView(R.id.iv_Receipt_true)
    TextView ivReceiptTrue;
    @BindView(R.id.iv_Receipt_false)
    TextView ivReceiptFalse;
    @BindView(R.id.cl_isReceipt)
    ConstraintLayout clIsReceipt;
    @BindView(R.id.tv_send_notice)
    TextView tvSendNotice;

    private Context mContext;

    public List<OrgNotifyEntity> orgNotifyList = new ArrayList<>();

    private String type;


    public OrgNotifyAdapter(List datas, Context context, ViewOnItemClick itemClick) {
        super(datas, context, itemClick);
        this.mContext = context;
        this.orgNotifyList.addAll(datas);
    }

    @Override
    public void convert(XrecyclerViewHolder holder, int position, Context context) {
        OrgNotifyEntity notifyEntity = orgNotifyList.get(position);
        //是否需要回执 1：需要回执 2：不需要回执
        String isReceipt = notifyEntity.getIsReceipt();
        tvTitleContent.setText(notifyEntity.getContent());
        if (type.equals("15")) {// 15：|83：我的机构通知
            String receipt = notifyEntity.getReceipt();// 是否回执 1：回执 2：没回执
            ivStatus.setVisibility(TextUtils.equals(isReceipt, "1") ? View.VISIBLE : View.GONE);
            ivStatus.setText(receipt.equals("1") ? "已回执" : "待回执");
            ivStatus.setBackgroundResource(receipt.equals("1") ? R.mipmap.ic_complet : R.mipmap.ic_pending);
            tvNotifyCount.setText("机构通知");
            tvNotifyCount.setTextColor(context.getResources().getColor(R.color.color_3));
            tvNotifyCount.setTextSize(18);
            tvHandlerName.setText(String.format("%s\t更新",
                    DateUtil.getDate(Long.parseLong(notifyEntity.getUpdateAt()), "yyyy-MM-dd HH:mm")));
            clIsReceipt.setVisibility(View.GONE);
        } else {
            clIsReceipt.setVisibility(TextUtils.equals(isReceipt, "1") ? View.VISIBLE : View.GONE);
            // 通知的类型 1：自定义 2：放假通知 3：班级通知 4：教师通知 5：学员通知 6：全体通知
            switch (notifyEntity.getNotifiType()) {
                case "3"://3：班级通知
                    List<OrgNotifyEntity.CoursesBean> courses = notifyEntity.getCoursesBean();
                    if (courses != null && courses.size() > 0) {
                        OrgNotifyEntity.CoursesBean coursesBean = courses.get(0);
                        boolean isEmtity = coursesBean != null;
                        if (isEmtity) {
                            tvNotifyCount.setText(String.format("教师%s人\t学员%s人\n%s\t%s-%s\t%s\t人",
                                    notifyEntity.getTeacherCount(), notifyEntity.getStudentCount(),
                                    coursesBean.getCourseName(), DateUtil.getDate(Long.parseLong(coursesBean.getCourseStarTime()), "yyyy-MM-dd HH:mm"),
                                    DateUtil.getDate(Long.parseLong(coursesBean.getCourseEndTime()), "HH:mm"), coursesBean.getCourseCount()));
                        }
                    }
                    break;
                case "1"://1：自定义
                case "2"://2：放假通知
                case "4"://4：教师通知
                case "5"://5：学员通知
                case "6"://6：全体通知
                default:
                    tvNotifyCount.setText(String.format("教师%s人\t学员%s人", notifyEntity.getTeacherCount(), notifyEntity.getStudentCount()));
                    break;
            }
            ivReceiptTrue.setText(String.format("已回执\t%s人", notifyEntity.getReceiptCount()));
            ivReceiptFalse.setText(String.format("未回执\t%s人", notifyEntity.getUnReceiptCount()));

            StringBuffer sendNotifyTime = new StringBuffer();//notifyEntity.getSenderName() + "\t"
            String[] sendTime = notifyEntity.getTime().split(",");
            for (int i = 0; i < sendTime.length; i++) {
//            if (i == 0) {
                sendNotifyTime.append(sendTime[i]);
//            } else
                if (i < sendTime.length - 1) {
                    sendNotifyTime.append("\n");
//                sendNotifyTime.append(DateUtil.getDate(Long.parseLong(sendTime[i]), "yyyy-MM-dd HH:mm") + "\t再次发送\n");
                }
            }

            tvHandlerName.setText(sendNotifyTime);
            tvSendNotice.setOnClickListener(v -> {
                if (!UserManager.getInstance().isTrueRole("tz_1")) {
                    UserManager.getInstance().showSuccessDialog((Activity) mContext
                            , mContext.getString(R.string.text_role));
                } else {
                    HttpManager.getInstance().doSendNoticeAgain("OrgNotifyAdapter", notifyEntity.getNotifiId(),
                            new HttpCallBack<BaseDataModel<OrgNotifyEntity>>((Activity) mContext, true) {
                                @Override
                                public void onSuccess(BaseDataModel<OrgNotifyEntity> data) {
                                    EventBus.getDefault().post(new ToUIEvent(ToUIEvent.REFERSH_NOTIFY));
                                }

                                @Override
                                public void onFail(int statusCode, String errorMsg) {
                                    LogUtils.d("SchoolWorkActivity onFail", statusCode + ":" + errorMsg);
                                    if (statusCode == 1002 || statusCode == 1011) {//异地登录
                                        U.showToast("该账户在异地登录!");
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
            });
        }
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_notify_list;
    }

    @Override
    public int getItemCount() {
        return orgNotifyList.size();
    }

    public void setData(List<OrgNotifyEntity> messageAllList, String type) {
        this.orgNotifyList.clear();
        this.orgNotifyList.addAll(messageAllList);
        this.type = type;
        notifyDataSetChanged();
    }

}
