package com.caregrowtht.app.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.library.utils.DateUtil;
import com.android.library.utils.U;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.StudentDetailsActivity;
import com.caregrowtht.app.activity.TimeCardSelectActivity;
import com.caregrowtht.app.model.BaseModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.uitil.GlideUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.UserManager;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import cn.carbs.android.avatarimageview.library.AvatarImageView;

/**
 * Created by haoruigang on 2018/4/26 16:31.
 */

public class StudentCardAdapter extends CommonAdapter {

    private Activity activity;
    ArrayList<StudentEntity> mArrDatas = new ArrayList<>();//签到的/请假的

    private CourseEntity courseEntity;

    // 用来控制CheckBox的选中状况
    private HashMap<Integer, Boolean> isSelected = new HashMap<>();
    //选中孩子的Id
    private HashMap<Integer, String> studentIds = new HashMap<>();
    private CheckBox cb;
    public Boolean isAll = false;
    private String orgId;
    private String lessonId;

    CheckBox tvSelectStu;

    public StudentCardAdapter(Activity activity, int layoutId, List datas, CourseEntity courseEntity) {
        super(activity, layoutId, datas);
        this.activity = activity;
        this.mArrDatas.addAll(datas);
        this.courseEntity = courseEntity;
        if (courseEntity != null) {
            isAll = false;
            // 初始化数据
            initDate();
            this.orgId = UserManager.getInstance().getOrgId();
            this.lessonId = courseEntity.getCourseId();
        }
    }

    // 初始化isSelected的数据
    public void initDate() {
        if (mArrDatas.size() > 0) {
            for (int i = 0; i < mArrDatas.size(); i++) {
                getIsSelected().put(i, isAll);
                getStudentIds().put(i, isAll ? mArrDatas.get(i).getStuId() : "");
            }
        }
    }

    public void setData(final CheckBox tvSelectStu) {
        this.tvSelectStu = tvSelectStu;
        if (TextUtils.isEmpty(lessonId) || TextUtils.isEmpty(orgId)) {//选择要发送的学员
            // 初始化数据
            initDate();
        }
        notifyDataSetChanged();
    }

    @Override
    protected void convert(ViewHolder viewHolder, Object item, int position) {
        StudentEntity stuData = mArrDatas.get(position);
        AvatarImageView ivStuAvatar = viewHolder.getView(R.id.iv_stuAvatar);
        ivStuAvatar.setTextAndColor(TextUtils.isEmpty(stuData.getStuName()) ? "" : stuData.getStuName().substring(0, 1),
                activity.getResources().getColor(R.color.b0b2b6));
        GlideUtils.setGlideImg(activity, stuData.getStuIcon(), 0, ivStuAvatar);
        TextView tvStuName = viewHolder.getView(R.id.tv_stu_name);
        tvStuName.setText(stuData.getStuName());
        RelativeLayout rlStuFront = viewHolder.getView(R.id.rl_stu_front);
        if (courseEntity != null) {//学员情况
            if (!TextUtils.isEmpty(lessonId) && !TextUtils.isEmpty(orgId)) {
                rlStuFront.setOnClickListener(view -> {
                    activity.startActivity(new Intent(activity, StudentDetailsActivity.class)
                            .putExtra("StudentEntity", stuData));
//                    getChildLesLog(stuData);
                });
            } else {//家庭共用学员
                checkCard(viewHolder, position, rlStuFront);
            }
        } else if (courseEntity == null) {//选择要发送的学员
            checkCard(viewHolder, position, rlStuFront);
        }
    }

    private void checkCard(ViewHolder viewHolder, int position, RelativeLayout rlStuFront) {
        if (mArrDatas.size() > 0) {
            // 根据isSelected来设置checkbox的选中状况
            cb = viewHolder.getView(R.id.iv_stuAvatar_check);
            cb.setVisibility(getIsSelected().get(position) ? View.VISIBLE : View.GONE);
            cb.setOnClickListener(v -> {
                getSelect(position);
                notifyDataSetChanged();
            });
            rlStuFront.setOnClickListener(v -> {
                getSelect(position);
                notifyDataSetChanged();
            });
        }
    }

    private void getChildLesLog(StudentEntity stuData) {
        HttpManager.getInstance().doGetChildLesLog("StudentCardAdapter",
                stuData.getStuId(), orgId, lessonId, stuData.getStudentType(),
                new HttpCallBack<BaseModel<StudentEntity>>() {

                    @Override
                    public void onSuccess(BaseModel<StudentEntity> data) {
                        showStudentDialog(stuData, data.getData());
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("StudentCardAdapter onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout((Activity) mContext);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("StudentCardAdapter onError", throwable.getMessage());
                    }
                });
    }

    /**
     * 学员当前状态详情
     */
    private void showStudentDialog(StudentEntity studentEntity, StudentEntity stuLogData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(activity, R.layout.dialog_stu_stuta, null);
        view.setOnClickListener(v -> {
            activity.startActivity(new Intent(activity, StudentDetailsActivity.class)
                    .putExtra("StudentEntity", studentEntity));
        });
        AvatarImageView ivAvatar = view.findViewById(R.id.iv_avatar);
        if (!TextUtils.isEmpty(studentEntity.getStuName())) {
            ivAvatar.setTextAndColor(TextUtils.isEmpty(studentEntity.getStuName()) ? "" : studentEntity.getStuName().substring(0, 1),
                    activity.getResources().getColor(R.color.b0b2b6));
        }
        GlideUtils.setGlideImg(activity, studentEntity.getStuIcon(), 0, ivAvatar);
        TextView tvName = view.findViewById(R.id.tv_name);
        tvName.setText(studentEntity.getStuName());
        TextView tvStatus = view.findViewById(R.id.tv_status);
        String statusText = null;
        switch (studentEntity.getStatus()) {//状态 1未签到 2已签到 3请假
            case "1":
                statusText = String.format("当前状态\t\t<font color='#69ace5'>%s</font>", "未签到");
                break;
            case "2":
                statusText = String.format("当前状态\t\t<font color='#999999'>%s</font>", "已签到");
                break;
            case "3":
                statusText = String.format("当前状态\t\t<font color='#999999'>%s</font>", "已请假");
                break;
        }
        tvStatus.setText(Html.fromHtml(statusText));
        List<StudentEntity.LogsList> logs = stuLogData.getLogs();
        TextView tvHandler = view.findViewById(R.id.tv_handler);
        if (logs.size() > 0) {
            tvHandler.setVisibility(View.VISIBLE);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < logs.size(); i++) {
                if (logs.size() - 1 == i) {
                    sb.append("\n");
                }
                StudentEntity.LogsList logList = logs.get(i);
                sb.append(String.format("%s\t%s%s\t%s",
                        DateUtil.getDate(Long.valueOf(logList.getCreate_at()), "yyyy-MM-dd HH:mm"),
                        logList.getOperateRelation(), logList.getOperateName(), logList.getTitle()));
            }
            tvHandler.setText(sb);
        }
        TextView tvCourseCanel = view.findViewById(R.id.tv_course_canel);
        if (TextUtils.equals(stuLogData.getIfCostError(), "0")) {//0: 消课失败 1：消课成功
            tvCourseCanel.setVisibility(View.VISIBLE);
        }
        tvCourseCanel.setOnClickListener(v -> {
            activity.startActivity(new Intent(mContext, TimeCardSelectActivity.class));
            activity.overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);//底部弹出动画
            dialog.dismiss();
        });
        ImageButton ib_colse = view.findViewById(R.id.ib_colse);
        ib_colse.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
    }

    public void getSelect(int position) {
        cb.setSelected(!getIsSelected().get(position));
        getIsSelected().put(position, cb.isSelected());// 同时修改map的值保存状态
        getStudentIds().put(position, cb.isSelected() &&
                !CheckIsSelecte(mArrDatas.get(position).getStuId()) ?
                mArrDatas.get(position).getStuId() : "");
        checkAll();
    }

    public void checkAll() {
        //遍历是否全选
        boolean isSelect = true;
        for (int i = 0; i < mArrDatas.size(); i++) {
            if (!getIsSelected().get(i)) {//有未选中的学员
                isSelect = false;
                break;
            }
        }
        if (tvSelectStu != null) {
            tvSelectStu.setChecked(isSelect);
            tvSelectStu.setSelected(isSelect);
        }
    }

    /**
     * 检查是否已选择
     *
     * @param stuId
     */
    private boolean CheckIsSelecte(String stuId) {
        if (studentIds != null) {//解决空指针异常
            for (int i = 0; i < studentIds.size(); i++) {
                if (studentIds.get(i).equals(stuId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public HashMap<Integer, String> getStudentIds() {
        return studentIds;
    }
}
