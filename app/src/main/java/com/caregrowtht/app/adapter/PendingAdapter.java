package com.caregrowtht.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.library.utils.DateUtil;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.PendingActivity;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.TimeUtils;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerAdapter;
import com.caregrowtht.app.view.xrecyclerview.xrecycleradapter.XrecyclerViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import butterknife.BindView;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * haoruigang on 待处理(签到、请假)
 */
public class PendingAdapter extends XrecyclerAdapter {

    @BindView(R.id.iv_stu_icon)
    AvatarImageView ivStuIcon;
    @BindView(R.id.tv_stu_name)
    TextView tvStuName;

    Context mContext;
    List<StudentEntity> studentList = new ArrayList<>();
    CourseEntity courseData;

    // 用来控制CheckBox的选中状况
    private HashMap<Integer, Boolean> isSign = new HashMap<>();
    private HashMap<Integer, Boolean> isLevan = new HashMap<>();
    //学生id
    private HashMap<Integer, StudentEntity> isStudent = new HashMap<>();
    //签到备注
    private HashMap<Integer, String> isSignBz = new HashMap<>();
    //请假备注
    private HashMap<Integer, String> isLevaeBz = new HashMap<>();
    private boolean isShowSign = false;
    private boolean isShowLeave = false;
    private StudentEntity stuEntity;//学员详情

    public PendingAdapter(List datas, Context context) {
        super(datas, context);
        this.mContext = context;
        this.studentList.addAll(datas);
        // 初始化数据
        initDate();
        initSignBz("");
        initLeaveBz("");
    }

    // 初始化isSelected的数据
    private void initDate() {
        if (studentList.size() > 0) {
            for (int i = 0; i < studentList.size(); i++) {
                getIsSign().put(i, false);
                getIsLevan().put(i, false);
                getIsStudent().put(i, null);
            }
        }
    }

    /**
     * 批量签到(默认机构带签到)
     */
    public void initBatchSign() {
        if (studentList.size() > 0) {
            for (int i = 0; i < studentList.size(); i++) {
                getIsSignBz().put(i, "机构带签到");
                getIsSign().put(i, true);
                getIsLevan().put(i, false);
                getIsStudent().put(i, studentList.get(i));
            }
            notifyDataSetChanged();
        }
    }

    private void initSignBz(String bz) {
        if (studentList.size() > 0) {
            for (int i = 0; i < studentList.size(); i++) {
                getIsSignBz().put(i, bz);
            }
        }
    }

    private void initLeaveBz(String bz) {
        if (studentList.size() > 0) {
            for (int i = 0; i < studentList.size(); i++) {
                getIsLevaeBz().put(i, bz);
            }
        }
    }

    @Override
    public void convert(XrecyclerViewHolder holder, final int position, Context context) {
        String name = studentList.get(position).getStuName();
        if (!TextUtils.isEmpty(name)) {
            ivStuIcon.setTextAndColor(name.substring(0, 1),
                    mContext.getResources().getColor(R.color.b0b2b6));
        }
        GlideUtils.setGlideImg(mContext, studentList.get(position).getStuIcon(), 0, ivStuIcon);
        tvStuName.setText(name);

        final ToggleButton tvSign = (ToggleButton) holder.getView(R.id.tv_sign);
        final ToggleButton tvLeave = (ToggleButton) holder.getView(R.id.tv_leave);

        tvSign.setChecked(getIsSign().get(position));
        tvLeave.setChecked(getIsLevan().get(position));

        if (courseData != null) {
            Log.d("PendingAdapter", courseData.getEndAt());
            Long endAt = Long.parseLong(courseData.getEndAt());// 课程时间
            Long today = System.currentTimeMillis() / 1000;//今天的时间戳
            Long moday = DateUtil.getStringToDate(
                    TimeUtils.dateTiem(
                            DateUtil.getDate(today, "yyyy-MM-dd")
                            , 1, "yyyy-MM-dd"), "yyyy-MM-dd");// 明天的时间戳
            if (moday < endAt) {// 只能签今天或者今天以前的课
                tvSign.setVisibility(View.GONE);
            }
        }

        final TextView tvOrgSign = (TextView) holder.getView(R.id.tv_org_sign);
        final TextView tvOrgLeave = (TextView) holder.getView(R.id.tv_org_leave);

        tvOrgSign.setText(getIsSignBz().get(position));
        tvOrgLeave.setText(getIsLevaeBz().get(position));

        tvOrgSign.setVisibility(tvSign.isChecked() ? View.VISIBLE : View.GONE);
        tvOrgLeave.setVisibility(tvLeave.isChecked() ? View.VISIBLE : View.GONE);

        //签到添加监听事件
        tvSign.setOnClickListener(view -> {
            getIsSign().put(position, tvSign.isChecked());
            getSign(position, tvSign);
            if (tvSign.isChecked()) {
                getIsLevan().put(position, !tvSign.isChecked());
                getLevan(position, tvLeave);

                stuEntity = studentList.get(position);

                if (!isShowSign) {
                    //触发选中事件
                    showSignDialog(tvSign, position);
                } else {
                    notifyItemChanged(position);
                }
            } else {
                notifyItemChanged(position);
            }
            getIsStudent().put(position, stuEntity);
        });
        //请假添加监听事件
        tvLeave.setOnClickListener(view -> {
            getIsLevan().put(position, tvLeave.isChecked());
            getLevan(position, tvLeave);
            if (tvLeave.isChecked()) {
                getIsSign().put(position, !tvLeave.isChecked());
                getSign(position, tvSign);

                stuEntity = studentList.get(position);

                if (!isShowLeave) {
                    //触发选中事件
                    showLeaveDialog(tvLeave, position);
                } else {
                    notifyItemChanged(position);
                }
            } else {
                notifyItemChanged(position);
            }
            getIsStudent().put(position, stuEntity);
        });
        tvOrgSign.setOnClickListener(v -> {
            showSignDialog(tvSign, position);
        });
        tvOrgLeave.setOnClickListener(v -> {
            showLeaveDialog(tvLeave, position);
        });

    }

    public void setData(ArrayList<StudentEntity> mArrDatas, CourseEntity courseData) {
        this.studentList.clear();
        this.studentList.addAll(mArrDatas);
        this.courseData = courseData;
        // 初始化数据
        isShowSign = false;
        isShowLeave = false;
        initDate();
        initSignBz("");
        initLeaveBz("");
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_pending;
    }

    public void getSign(final int position, final ToggleButton tvSign) {
        tvSign.setSelected(getIsSign().get(position));
    }

    public void getLevan(final int position, final ToggleButton tvLeave) {
        tvLeave.setSelected(getIsLevan().get(position));
    }

    public HashMap<Integer, Boolean> getIsSign() {
        return isSign;
    }

    public HashMap<Integer, Boolean> getIsLevan() {
        return isLevan;
    }

    public HashMap<Integer, StudentEntity> getIsStudent() {
        return isStudent;
    }

    public HashMap<Integer, String> getIsSignBz() {
        return isSignBz;
    }

    public HashMap<Integer, String> getIsLevaeBz() {
        return isLevaeBz;
    }

    /**
     * 签到弹窗
     */
    private void showSignDialog(final ToggleButton tvSign, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(mContext, R.layout.dialog_sign_on, null);
        final TextView tvOrgSign = view.findViewById(R.id.tv_org_sign);
        tvOrgSign.setOnClickListener(v -> {
            String orgSign = tvOrgSign.getText().toString();
            if (!isShowSign) {
                initSignBz(orgSign);//批量签到
                isShowSign = true;
            } else {
                getIsSignBz().put(position, orgSign);//单个签到
            }
            dialog.dismiss();
            notifyItemChanged(position);
        });
        final TextView tvStuSign = view.findViewById(R.id.tv_stu_sign);
        tvStuSign.setOnClickListener(v -> {
            String stuSign = tvStuSign.getText().toString();
            if (!isShowSign) {
                initSignBz(stuSign);//批量签到
                isShowSign = true;
            } else {
                getIsSignBz().put(position, stuSign);//单个签到
            }
            dialog.dismiss();
            notifyItemChanged(position);
        });
        final TextView tvCancel = view.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(v -> {
            getIsSign().put(position, false);
            isShowSign = false;
            dialog.dismiss();
            notifyItemChanged(position);
        });
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
        //设置弹窗在底部
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        //设置动画效果
        window.setWindowAnimations(R.style.Popupwindow);
        WindowManager m = ((PendingActivity) mContext).getWindowManager();
        Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = d.getWidth(); //宽度设置为屏幕
        dialog.getWindow().setAttributes(p); //设置生效
    }

    /**
     * 请假弹窗
     */
    private void showLeaveDialog(final ToggleButton tvLeave, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(mContext, R.layout.activity_levae_on, null);
        final TextView tvOrgSign = view.findViewById(R.id.tv_org_sign);
        tvOrgSign.setOnClickListener(v -> {
            String orgSign = tvOrgSign.getText().toString();
            if (!isShowLeave) {
                initLeaveBz(orgSign);//批量请假
                isShowLeave = true;
            } else {
                getIsLevaeBz().put(position, orgSign);//单个请假
            }
            dialog.dismiss();
            notifyItemChanged(position);
        });
        final TextView tvStuSign = view.findViewById(R.id.tv_stu_sign);
        tvStuSign.setOnClickListener(v -> {
            String stuSign = tvStuSign.getText().toString();
            if (!isShowLeave) {
                initLeaveBz(stuSign);//批量请假
                isShowLeave = true;
            } else {
                getIsLevaeBz().put(position, stuSign);//单个请假
            }
            dialog.dismiss();
            notifyItemChanged(position);
        });
        final TextView tvCancel = view.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(v -> {
            getIsLevan().put(position, false);
            isShowLeave = false;
            dialog.dismiss();
            notifyItemChanged(position);
        });
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
        //设置弹窗在底部
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        //设置动画效果
        window.setWindowAnimations(R.style.Popupwindow);
        WindowManager m = ((PendingActivity) mContext).getWindowManager();
        Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = d.getWidth(); //宽度设置为屏幕
        dialog.getWindow().setAttributes(p); //设置生效
    }

}
