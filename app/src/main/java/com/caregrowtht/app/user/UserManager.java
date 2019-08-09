package com.caregrowtht.app.user;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.library.utils.U;
import com.caregrowtht.app.AppManager;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.MyApplication;
import com.caregrowtht.app.R;
import com.caregrowtht.app.activity.AddOrgActivity;
import com.caregrowtht.app.activity.LoginActivity;
import com.caregrowtht.app.activity.MainActivity;
import com.caregrowtht.app.activity.SpaceImageDetailActivity;
import com.caregrowtht.app.model.BaseDataModel;
import com.caregrowtht.app.model.CourseEntity;
import com.caregrowtht.app.model.MessageEntity;
import com.caregrowtht.app.model.MomentMessageEntity;
import com.caregrowtht.app.model.OrgEntity;
import com.caregrowtht.app.model.PowersEntity;
import com.caregrowtht.app.model.RoleEntity;
import com.caregrowtht.app.model.StudentEntity;
import com.caregrowtht.app.model.UserEntity;
import com.caregrowtht.app.okhttp.HttpManager;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.okhttp.callback.OkHttpUtils;
import com.caregrowtht.app.okhttp.progress.MyProgressDialog;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.uitil.permissions.PermissionCallBackM;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import butterknife.Setter;
import zlc.season.rxdownload3.core.Succeed;

/**
 * Created by 1bu2bu on 2015/12/4.
 * <p/>
 * 用户所有行为信息的管理类
 */
public class UserManager {

    public UserEntity userData = new UserEntity();
    private List<CourseEntity> cardStuList = new ArrayList<>();
    private List<RoleEntity> roleEntityList = new ArrayList<>();
    private List<OrgEntity> orgEntityList = new ArrayList<>();

    private ArrayList<MomentMessageEntity> mMyStarCircles;

    private String OrgId;

    private View courVsview;

    private UserManager() {
    }

    private static UserManager mUserManager;

    public static UserManager getInstance() {
        if (mUserManager == null) {
            mUserManager = new UserManager();
        }
        return mUserManager;
    }


    public void save(Context context, UserEntity user) {
        userData = user;
        Log.e("-----", "userData=" + userData.toString());
        U.savePreferences("uid", userData.getUid());
        U.savePreferences("token", U.MD5(UserManager.getInstance().userData.getToken() + "_" + Constant.API_KEY));
        Constant.UID_TOKEN = "/uid/" + userData.getUid() + "/token/" + userData.getToken();
    }

    /**
     * 审核不通过，重新拼接机构Id
     *
     * @param orgId
     */
    public void isNoPass(String orgId) {
        String[] orgIds = UserManager.getInstance().userData.getOrgIds().split(",");
        StringBuilder OrgId = new StringBuilder();
        boolean isSave = false;
        for (String Id : orgIds) {
            if (!TextUtils.isEmpty(Id) && !Id.equals(orgId)) {
                if (isSave) {
                    OrgId.append(",");
                }
                OrgId.append(Id);
                isSave = true;
            }
        }
        userData.setOrgIds(OrgId.toString());
        userData.setPassOrgIds(OrgId.toString());
    }

    /**
     * 检查 在职或离职
     *
     * @param isStatus
     * @param userId
     * @param tercherData
     * @return
     */
    public boolean isStatus(boolean isStatus, String userId, ArrayList<StudentEntity> tercherData) {
        for (int i = 0; i < tercherData.size(); i++) {
            if (userId.equals(tercherData.get(i).getMobile())) {// 在职教师
                isStatus = true;
                break;
            }
        }
        return isStatus;
    }

    /**
     * 检查教师是否离职
     * true:在职动态 false:离职动态
     */
    public boolean CheckIsLeave(String orgId) {
        String[] passOrgIds = UserManager.getInstance().userData.getPassOrgIds().split(",");
        for (String passOrgId : passOrgIds) {
            if (passOrgId.equals(orgId)) {// 在职动态
                return true;
            }
        }
        return false;
    }

    /**
     * 添加机构、创建机构、机构分配的教师
     *
     * @param orgId
     */
    public void orgAddTeacher(String orgId) {
        String[] OrgIds = UserManager.getInstance().userData.getOrgIds().split(",");
        String OrgId = "";
        if (OrgIds.length > 0 && !TextUtils.isEmpty(OrgIds[0])) {// 已经有机构
            OrgId = UserManager.getInstance().userData.getOrgIds() + ",";
        }
        OrgId += orgId;
        userData.setOrgIds(OrgId);
        userData.setPassOrgIds(OrgId);
    }

    /**
     * 设置机构简称（目前支持4个字以下的）
     *
     * @param tvName
     * @param orgShortName
     */
    public void getOrgShortName(TextView tvName, String orgShortName) {
        if (StrUtils.isNotEmpty(orgShortName)) {
            if (orgShortName.length() == 4) {
                tvName.setText(String.format("%s\n%s", orgShortName.substring(0, 2),
                        orgShortName.substring(2, 4)));
            } else if (orgShortName.length() < 4) {
                tvName.setText(orgShortName);
            } else {
                tvName.setText(orgShortName);
            }
        }
    }

    /**
     * 提示语
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

    /**
     * 自动登录
     * haoruigang on 2018-4-2 17:30:17
     *
     * @param context
     * @param uid
     * @param token
     */
    public void autoLogin(final Activity context, String uid, String token) {
        if (uid != null && token != null) {
            HttpManager.getInstance().doAutoLogin(context.getClass().getName(),
                    uid, token, U.getVersionName(), new HttpCallBack<BaseDataModel<UserEntity>>() {

                        @Override
                        public void onSuccess(BaseDataModel<UserEntity> data) {
                            save(context, data.getData().get(0));

                            refreshMyCollectDatas(context);

                            context.startActivity(new Intent(context, MainActivity.class));
                            context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            context.finish();
                        }

                        @Override
                        public void onFail(int statusCode, String errorMsg) {
                            U.showToast("登录信息失效，请重新登录");
                            U.savePreferences("uid", "");
                            U.savePreferences("token", "");
                            context.startActivity(new Intent(context, LoginActivity.class));
                            context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            LogUtils.d("autoLogin", throwable.getMessage());
                        }
                    });
        }
    }


    public ArrayList<MomentMessageEntity> getmMyStarCircles() {
        return mMyStarCircles;
    }

    private void refreshMyCollectDatas(Activity argActivity) {
        if (mMyStarCircles == null) {
            mMyStarCircles = new ArrayList<>();
        }
        HttpManager.getInstance().getMyStarCircleId("refreshMyStarCircleId",
                new HttpCallBack<BaseDataModel<MomentMessageEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<MomentMessageEntity> data) {
                        mMyStarCircles.addAll(data.getData());
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(argActivity);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    /**
     * 检查是否送过花
     *
     * @param argCircleId
     */
    public boolean CheckIsCircles(String argCircleId) {
        if (mMyStarCircles != null) {//解决空指针异常
            for (MomentMessageEntity pEntity : mMyStarCircles) {
                if (pEntity.getCircleId().equals(argCircleId + "")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 登出
     *
     * @param context
     */
    public void logout(final Activity context, final MyProgressDialog dialog) {

        String uid = userData.getUid();
        String token = userData.getToken();

        if (uid != null && token != null) {
            AppManager.getAppManager().finishAllActivity();// finish 所有Activity
            HttpManager.getInstance().doLogout(context.getClass().getName(),
                    uid, token, new HttpCallBack<BaseDataModel<UserEntity>>(context) {
                        @Override
                        public void onSuccess(BaseDataModel<UserEntity> data) {
                            dialog.dismiss();
                            U.showToast("退出登录成功");
                            userData = null;
                            clearInfo(context);
                        }

                        @Override
                        public void onFail(int statusCode, String errorMsg) {
                            dialog.dismiss();
                            clearInfo(context);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            clearInfo(context);
                        }
                    });
        } else {
            clearInfo(context);
        }
    }

    private void clearInfo(Activity context) {
        userData = null;
        U.savePreferences("uid", "");
        U.savePreferences("token", "");
        U.savePreferences("orgId", "");
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getAppContext().startActivity(intent);
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void teacherOrgStrand(Activity mContext, MessageEntity msgEntity, Leave leave) {
        setOrgId(msgEntity.getOrgId());
        HttpManager.getInstance().doTeacherOrgStrand("EliminateDetailActivity",
                new HttpCallBack<BaseDataModel<UserEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<UserEntity> data) {
                        userData.setOrgIds(data.getData().get(0).getOrgIds());
                        userData.setPassOrgIds(data.getData().get(0).getPassOrgIds());
                        if (!CheckIsLeave(msgEntity.getOrgId())) {// 教师已离职
                            leave.isLeave(true);
                        } else {
                            leave.isLeave(false);
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
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

    /**
     * @param orgId       机构Id
     * @param argActivity 上下文
     * @param type        1:添加机构 2:获取权限
     */
    public void getOrgInfo(String orgId, Activity argActivity, String type, Role role) {
        if (StrUtils.isEmpty(orgId)) {
            U.showToast("请输入机构代码");
            return;
        }
        //获取机构主页详情 haoruigang on 2018-4-4 16:09:48
        HttpManager.getInstance().doOrgInfo("AddOrgActivity", orgId, "1",
                new HttpCallBack<BaseDataModel<OrgEntity>>(argActivity, true) {
                    @Override
                    public void onSuccess(BaseDataModel<OrgEntity> data) {
                        LogUtils.d("AddOrgActivity onSuccess", data.getData().toString());
                        if (type.equals("1")) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("OrgEntity", data.getData().get(0));
                            argActivity.startActivity(new Intent(argActivity, AddOrgActivity.class).putExtras(bundle));
                        } else {
                            LogUtils.d("TeacherHomeFragment onSuccess", data.getData().toString());
                            OrgEntity orgEntity = data.getData().get(0);
                            if (orgEntity != null) {

                                // 79.获取机构权限配置
                                UserManager.getInstance().getOrgRole(argActivity, orgEntity, role);

                                UserManager.getInstance().getOrgEntityList(orgEntity);
                            }
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("AddOrgActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout(argActivity);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("AddOrgActivity onError", throwable.getMessage());
                    }
                });
    }

    // 79.获取机构权限配置
    public void getOrgRole(Context mContext, OrgEntity orgEntity, Role role) {
        String orgId = orgEntity.getOrgId();
        HttpManager.getInstance().doGetOrgRole("TeacherHomeFragment", orgId,
                new HttpCallBack<BaseDataModel<RoleEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<RoleEntity> data) {
                        role.isRole(true);
                        ArrayList<RoleEntity> roleDatas = data.getData();
                        UserManager.getInstance().getRoleEntityList(roleDatas);// 保存当前 机构的权限配置 数据
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        role.isRole(false);
                        LogUtils.d("TeacherHomeFragment onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//异地登录
                            U.showToast("该账户在异地登录!");
                            HttpManager.getInstance().dologout((Activity) mContext);
                        } else {
                            U.showToast(errorMsg);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtils.d("TeacherHomeFragment throwable", throwable.getMessage());
                    }
                });
    }

    /**
     * 查看大图 保存/取消 弹窗
     */
    public void showSaveDialog(Activity mActivity, final String strUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(mActivity, R.layout.dialog_sign_on, null);
        final TextView tvOrgSign = view.findViewById(R.id.tv_org_sign);
        tvOrgSign.setText("保存");
        tvOrgSign.setOnClickListener(v -> {
            //动态权限申请
            ((SpaceImageDetailActivity) mActivity).requestPermission(
                    Constant.REQUEST_CODE_WRITE,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    mActivity.getString(R.string.rationale_file),
                    new PermissionCallBackM() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onPermissionGrantedM(int requestCode, String... perms) {
                            //下载的文件名
                            OkHttpUtils.download(strUrl, status -> {
                                if (status instanceof Succeed) {
                                    U.showToast("已保存至相册");
                                }
                            });
                            dialog.dismiss();
                        }

                        @Override
                        public void onPermissionDeniedM(int requestCode, String... perms) {
                            LogUtils.e(mActivity, "TODO: WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT);
                        }
                    });
        });
        final TextView tvStuSign = view.findViewById(R.id.tv_stu_sign);
        tvStuSign.setVisibility(View.GONE);
        final TextView tvCancel = view.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.setCancelable(false);
        dialog.setView(view);
        dialog.show();
        //设置弹窗在底部
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        //设置动画效果
        window.setWindowAnimations(R.style.Popupwindow);
        WindowManager m = mActivity.getWindowManager();
        Display d = m.getDefaultDisplay(); //为获取屏幕宽、高
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = d.getWidth(); //宽度设置为屏幕
        dialog.getWindow().setAttributes(p); //设置生效
    }

    public String getOrgId() {
        return OrgId;
    }

    public void setOrgId(String orgId) {
        OrgId = orgId;
    }

    private Message m;

    public void xgPush(String account) {
        //开启信鸽的日志输出，线上版本不建议调用
        XGPushConfig.enableDebug(MyApplication.getAppContext(), true);
        XGPushConfig.getToken(MyApplication.getAppContext());
        //注册数据更新监听器
        MsgReceiver updateListViewReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.qq.xgdemo.activity.UPDATE_LISTVIEW");
        MyApplication.getAppContext().registerReceiver(updateListViewReceiver, intentFilter);
        // 1.获取设备Token
        Handler handler = new HandlerExtension(MyApplication.getInstance());
        m = handler.obtainMessage();
        /*
        注册信鸽服务的接口
        启动并注册APP，同时绑定账号
        */
        XGPushManager.registerPush(MyApplication.getAppContext(), account,
                new XGIOperateCallback() {
                    @Override
                    public void onSuccess(Object data, int flag) {
                        LogUtils.d("TPush", "推送注册成功，设备token为：" + data);
                        m.obj = "register push sucess. token:" + data;
                        m.sendToTarget();
                    }

                    @Override
                    public void onFail(Object data, int errCode, String msg) {
                        LogUtils.d("TPush", "推送注册失败，错误码：" + errCode + ",错误信息：" + msg);
                        m.obj = "register push fail. token:" + data
                                + ", errCode:" + errCode + ",msg:" + msg;
                        m.sendToTarget();
                    }
                });

        // 获取token
        XGPushConfig.getToken(MyApplication.getAppContext());
    }

    public void xgUnPush(Activity activity) {
        MyProgressDialog dialog = new MyProgressDialog(activity, true);
        dialog.show();
        //反注册代码，线上版本不能调用
        XGPushManager.unregisterPush(activity, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object o, int i) {
                LogUtils.d("TPush", "推送反注册成功" + o + " flag = " + i);
                UserManager.getInstance().logout(activity, dialog);
            }

            @Override
            public void onFail(Object o, int i, String s) {
                LogUtils.d("TPush", "推送反注册失败" + o + " errCode = " + i + " , msg = " + s);
                UserManager.getInstance().logout(activity, dialog);
            }
        });
    }


    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
        }
    }

    private static class HandlerExtension extends Handler {
        WeakReference<Application> mActivity;

        HandlerExtension(Application application) {
            mActivity = new WeakReference<>(application);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Application theActivity = mActivity.get();
            if (theActivity == null) {
                theActivity = new Application();
            }
            if (msg != null) {
                Log.d("TPush", msg.obj.toString());
                XGPushConfig.getToken(theActivity);//获取Token
            }
            // XGPushManager.registerCustomNotification(theActivity,
            // "BACKSTREET", "BOYS", System.currentTimeMillis() + 5000, 0);
        }
    }

    /**
     * 权限匹配
     * return true 有 ,false 没有
     */
    private boolean getIsRole(RoleEntity roleEntity, String roleText) {
        List<PowersEntity> powers = roleEntity.getPowers();
        if (powers != null && powers.size() > 0) {
            for (PowersEntity funcation : powers) {
                if (funcation.getFunctionId().equals(roleText)) {// 添加排课 权限
                    if (funcation.getIsHave().equals("2")) {//是否有此权限 1：有 2：没有 提醒权限(2：我的课（隐藏机构课） 3：机构课)
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 权限匹配
     * return true 有 ,false 没有
     */
    private boolean getIsTxRole(RoleEntity roleEntity, String roleText) {
        List<PowersEntity> powers = roleEntity.getPowers2();
        if (powers != null && powers.size() > 0) {
            for (PowersEntity funcation : powers) {
                if (funcation.getFunctionId().equals(roleText)) {// 添加排课 权限
                    if (funcation.getIsHave().equals("2")) {//是否有此提醒权限 (2：我的课 没有（隐藏机构课） 3：机构课 有)
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 根据滑动的当前Id遍历获取当前的机构及身份Id
     *
     * @param orgId
     * @return
     */
    private OrgEntity getSqlIsOrgEntity(String orgId) {
        if (orgEntityList.size() > 0) {
            for (int i = 0; i < orgEntityList.size(); i++) {
                String org_Id = orgEntityList.get(i).getOrgId();
                if (orgId.equals(org_Id)) {// 和当前机构匹配
                    return orgEntityList.get(i);
                }
            }
        }
        return null;
    }

    private RoleEntity getSqlIsRoleEntity(String powerId) {
        List<RoleEntity> roleEntities = getRoleEntityList(null);//设置权限参数
        if (roleEntities.size() > 0) {
            for (int i = 0; i < roleEntities.size(); i++) {
                if (roleEntities.get(i) != null && !TextUtils.isEmpty(powerId)) {
                    String power_Id = roleEntities.get(i).getPowerId();
                    if (powerId.equals(power_Id)) {// 和当前机构匹配
                        return roleEntities.get(i);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取当前操作是否有权限
     *
     * @return true:有权限 false:没权限
     */
    public boolean isTrueRole(String functionId) {
        boolean isZy = false;
        String orgId = UserManager.getInstance().getOrgId();
        OrgEntity orgEntity = getSqlIsOrgEntity(orgId);
        String powerId = null;
        if (orgEntity != null) {
            powerId = orgEntity.getPowerId();
        }
        if (StrUtils.isNotEmpty(powerId) && powerId.equals("99999")) {// 超级管理员
            isZy = true;
        } else {
            // 当前 机构的权限配置 数据
            RoleEntity roleEntity = getSqlIsRoleEntity(powerId);
            if (roleEntity != null) {
                isZy = getIsRole(roleEntity, functionId);
            }
        }
        return isZy;
    }

    /**
     * 获取当前操作是否可以查看机构课程
     *
     * @return true:有权限 false:没权限
     */
    public void isTxRole(String functionId, Role role) {
        boolean isZy = false;
        String orgId = UserManager.getInstance().getOrgId();
        OrgEntity orgEntity = getSqlIsOrgEntity(orgId);
        String powerId = null;
        if (orgEntity != null) {
            powerId = orgEntity.getPowerId();
        }
        if (StrUtils.isNotEmpty(powerId) && powerId.equals("99999")) {// 超级管理员
            isZy = true;
        } else {
            // 当前 机构的权限配置 数据
            RoleEntity roleEntity = getSqlIsRoleEntity(powerId);
            if (roleEntity != null) {
                isZy = getIsTxRole(roleEntity, functionId);
            }
        }
        role.isRole(isZy);
    }

    public View getCourVsview() {
        return courVsview;
    }

    public void setCourVsview(View courVsview) {
        this.courVsview = courVsview;
    }

    public String getJsonString(JSONObject jsonObject, String object) throws JSONException {
        return jsonObject.getString(object);
    }

    public OrgEntity getOrgEntity() {
        String orgId = getOrgId();
        return getSqlIsOrgEntity(orgId);
    }

    public List<RoleEntity> getRoleEntityList(ArrayList<RoleEntity> roleDatas) {
        if (roleDatas != null && roleDatas.size() > 0) {//解决空指针异常
            for (RoleEntity entity : roleDatas) {
                boolean isExc = false;// 默认不存在权限中
                for (int i = 0; i < roleEntityList.size(); i++) {
                    RoleEntity entity1 = roleEntityList.get(i);
                    if (entity.getPowerId().equals(entity1.getPowerId())) {// 已存在列表中
                        isExc = true;
                        roleEntityList.set(i, entity);
                        break;
                    }
                }
                if (!isExc) {
                    roleEntityList.add(entity);// 添加所有权限
                }
            }
        }
        return roleEntityList;
    }

    public List<OrgEntity> getOrgEntityList(OrgEntity orgEntity) {
        boolean isExc = false;// 默认不存在机构中
        if (orgEntity != null) {//解决空指针异常
            for (int i = 0; i < orgEntityList.size(); i++) {
                OrgEntity entity = orgEntityList.get(i);
                if (entity.getOrgId().equals(orgEntity.getOrgId())) {// 已存在列表中
                    isExc = true;
                    orgEntityList.set(i, orgEntity);
                }
            }
            if (!isExc) {
                orgEntityList.add(orgEntity);// 添加所有机构
            }
        }
        return orgEntityList;
    }

    public static <T extends View, V> void apply(List<T> list,
                                                 Setter<? super T, V> setter, V value) {
        for (int i = 0, count = list.size(); i < count; i++) {
            setter.set(list.get(i), value, i);
        }
    }

    public List<CourseEntity> getCardStuList() {
        return cardStuList;
    }

    /**
     * 根据 年月日 时间戳 去重
     *
     * @param orderList
     * @return
     * @author haoruigang
     */
    public ArrayList<String> removeDuplicateOrder(List<String> orderList) {
        Set<String> set = new TreeSet<>((a, b) -> {
            // 字符串则按照asicc码升序排列
            return a.split(" ")[0].compareTo(b.split(" ")[0]);
        });
        set.addAll(orderList);
        return new ArrayList<>(set);
    }

    public interface Role {
        void isRole(boolean isRole);
    }

    public interface Leave {
        void isLeave(boolean isLeave);
    }

}
