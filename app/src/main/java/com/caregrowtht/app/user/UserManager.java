package com.caregrowtht.app.user;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
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

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
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
 * ????????????????????????????????????
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


    public void save(UserEntity user) {
        userData = user;
        Log.e("-----", "userData=" + userData.toString());
        U.savePreferences("uid", userData.getUid());
        U.savePreferences("token", U.MD5(UserManager.getInstance().userData.getToken() + "_" + Constant.API_KEY));
        Constant.UID_TOKEN = "/uid/" + userData.getUid() + "/token/" + userData.getToken();
    }

    /**
     * ????????????????????????????????????Id
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
     * ?????? ???????????????
     *
     * @param isStatus
     * @param userId
     * @param tercherData
     * @return
     */
    public boolean isStatus(boolean isStatus, String userId, ArrayList<StudentEntity> tercherData) {
        for (int i = 0; i < tercherData.size(); i++) {
            if (userId.equals(tercherData.get(i).getMobile())) {// ????????????
                isStatus = true;
                break;
            }
        }
        return isStatus;
    }

    /**
     * ????????????????????????
     * true:???????????? false:????????????
     */
    public boolean CheckIsLeave(String orgId) {
        String[] passOrgIds = UserManager.getInstance().userData.getPassOrgIds().split(",");
        for (String passOrgId : passOrgIds) {
            if (passOrgId.equals(orgId)) {// ????????????
                return true;
            }
        }
        return false;
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param orgId
     */
    public void orgAddTeacher(String orgId) {
        String[] OrgIds = UserManager.getInstance().userData.getOrgIds().split(",");
        String OrgId = "";
        if (OrgIds.length > 0 && !TextUtils.isEmpty(OrgIds[0])) {// ???????????????
            OrgId = UserManager.getInstance().userData.getOrgIds() + ",";
        }
        OrgId += orgId;
        userData.setOrgIds(OrgId);
        userData.setPassOrgIds(OrgId);
    }

    /**
     * ?????????????????????????????????4??????????????????
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

    /**
     * ????????????
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
                            save( data.getData().get(0));

                            refreshMyCollectDatas(context);

                            context.startActivity(new Intent(context, MainActivity.class));
                            context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            context.finish();
                        }

                        @Override
                        public void onFail(int statusCode, String errorMsg) {
                            U.showToast("????????????????????????????????????");
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
    static int clickNotificationCode = 888;
    public void autoLogin(final Context context, String uid, String token, OpenNotification openNotification) {
        if (uid != null && token != null) {
            HttpManager.getInstance().doAutoLogin(context.getClass().getName(),
                    uid, token, U.getVersionName(), new HttpCallBack<BaseDataModel<UserEntity>>() {

                        @Override
                        public void onSuccess(BaseDataModel<UserEntity> data) {

                            save(data.getData().get(0));

                            Intent intent = new Intent(context, MainActivity.class);
                            intent.setAction(Intent.ACTION_MAIN);
//                                appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//????????????????????????????????????
                            PendingIntent contentIntent = PendingIntent.getActivity(context, clickNotificationCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            openNotification.Notification(contentIntent);
                        }

                        @Override
                        public void onFail(int statusCode, String errorMsg) {
                            U.showToast("????????????????????????????????????");
                            U.savePreferences("uid", "");
                            U.savePreferences("token", "");
                            context.startActivity(new Intent(context, LoginActivity.class));
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
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
                            HttpManager.getInstance().dologout(argActivity);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
    }

    /**
     * ?????????????????????
     *
     * @param argCircleId
     */
    public boolean CheckIsCircles(String argCircleId) {
        if (mMyStarCircles != null) {//?????????????????????
            for (MomentMessageEntity pEntity : mMyStarCircles) {
                if (pEntity.getCircleId().equals(argCircleId + "")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * ??????
     *
     * @param context
     */
    public void logout(final Activity context, final MyProgressDialog dialog) {

        String uid = userData.getUid();
        String token = userData.getToken();

        if (uid != null && token != null) {
            AppManager.getAppManager().finishAllActivity();// finish ??????Activity
            HttpManager.getInstance().doLogout(context.getClass().getName(),
                    uid, token, new HttpCallBack<BaseDataModel<UserEntity>>(context) {
                        @Override
                        public void onSuccess(BaseDataModel<UserEntity> data) {
                            dialog.dismiss();
                            U.showToast("??????????????????");
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
                        if (!CheckIsLeave(msgEntity.getOrgId())) {// ???????????????
                            leave.isLeave(true);
                        } else {
                            leave.isLeave(false);
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

    /**
     * @param orgId       ??????Id
     * @param argActivity ?????????
     * @param type        1:???????????? 2:????????????
     */
    public void getOrgInfo(String orgId, Activity argActivity, String type, Role role) {
        if (StrUtils.isEmpty(orgId)) {
            U.showToast("?????????????????????");
            return;
        }
        //???????????????????????? haoruigang on 2018-4-4 16:09:48
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

                                // 79.????????????????????????
                                UserManager.getInstance().getOrgRole(argActivity, orgEntity, role);

                                UserManager.getInstance().getOrgEntityList(orgEntity);
                            }
                        }
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        LogUtils.d("AddOrgActivity onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
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

    // 79.????????????????????????
    public void getOrgRole(Context mContext, OrgEntity orgEntity, Role role) {
        String orgId = orgEntity.getOrgId();
        HttpManager.getInstance().doGetOrgRole("TeacherHomeFragment", orgId,
                new HttpCallBack<BaseDataModel<RoleEntity>>() {
                    @Override
                    public void onSuccess(BaseDataModel<RoleEntity> data) {
                        role.isRole(true);
                        ArrayList<RoleEntity> roleDatas = data.getData();
                        UserManager.getInstance().getRoleEntityList(roleDatas);// ???????????? ????????????????????? ??????
                    }

                    @Override
                    public void onFail(int statusCode, String errorMsg) {
                        role.isRole(false);
                        LogUtils.d("TeacherHomeFragment onFail", statusCode + ":" + errorMsg);
                        if (statusCode == 1002 || statusCode == 1011) {//????????????
                            U.showToast("????????????????????????!");
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
     * ???????????? ??????/?????? ??????
     */
    public void showSaveDialog(Activity mActivity, final String strUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity, R.style.CustomDialog);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(mActivity, R.layout.dialog_sign_on, null);
        final TextView tvOrgSign = view.findViewById(R.id.tv_org_sign);
        tvOrgSign.setText("??????");
        tvOrgSign.setOnClickListener(v -> {
            //??????????????????
            ((SpaceImageDetailActivity) mActivity).requestPermission(
                    Constant.REQUEST_CODE_WRITE,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    mActivity.getString(R.string.rationale_file),
                    new PermissionCallBackM() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onPermissionGrantedM(int requestCode, String... perms) {
                            //??????????????????
                            OkHttpUtils.download(strUrl, status -> {
                                if (status instanceof Succeed) {
                                    U.showToast("??????????????????");
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
        //?????????????????????
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        //??????????????????
        window.setWindowAnimations(R.style.Popupwindow);
        WindowManager m = mActivity.getWindowManager();
        Display d = m.getDefaultDisplay(); //????????????????????????
        WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //?????????????????????????????????
        p.width = d.getWidth(); //?????????????????????
        dialog.getWindow().setAttributes(p); //????????????
    }

    public String getOrgId() {
        return OrgId;
    }

    public void setOrgId(String orgId) {
        OrgId = orgId;
    }

    private CloudPushService mPushService = PushServiceFactory.getCloudPushService();

    public void xgPush(String account) {
        mPushService.bindAccount(account, new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                LogUtils.d("TPush1", s + "???????????????????????????token??????" + account);
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                LogUtils.d("TPush1", "?????????????????????????????????" + errorMsg + ",???????????????" + errorMsg);
            }
        });
    }

    public void xgUnPush(Activity activity) {
        // 1.????????????Token
        String account = U.MD5(UserManager.getInstance().userData.getUid());
        final MyProgressDialog dialog = new MyProgressDialog(activity, true);
        dialog.show();

        mPushService.unbindAccount(new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                UserManager.getInstance().logout(activity, dialog);
                LogUtils.d("TPush2", "??????????????????" + s);
            }

            @Override
            public void onFailed(String errorCode, String errorMsg) {
                LogUtils.d("TPush2", "??????????????????" + " errCode = " + errorCode + " , msg = " + errorMsg);
            }
        });
    }

    /**
     * ????????????
     * return true ??? ,false ??????
     */
    private boolean getIsRole(RoleEntity roleEntity, String roleText) {
        List<PowersEntity> powers = roleEntity.getPowers();
        if (powers != null && powers.size() > 0) {
            for (PowersEntity funcation : powers) {
                if (funcation.getFunctionId().equals(roleText)) {// ???????????? ??????
                    if (funcation.getIsHave().equals("2")) {//?????????????????? 1?????? 2????????? ????????????(2????????????????????????????????? 3????????????)
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
     * ????????????
     * return true ??? ,false ??????
     */
    private boolean getIsTxRole(RoleEntity roleEntity, String roleText) {
        List<PowersEntity> powers = roleEntity.getPowers2();
        if (powers != null && powers.size() > 0) {
            for (PowersEntity funcation : powers) {
                if (funcation.getFunctionId().equals(roleText)) {// ???????????? ??????
                    if (funcation.getIsHave().equals("2")) {//???????????????????????? (2???????????? ??????????????????????????? 3???????????? ???)
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
     * ?????????????????????Id????????????????????????????????????Id
     *
     * @param orgId
     * @return
     */
    private OrgEntity getSqlIsOrgEntity(String orgId) {
        if (orgEntityList.size() > 0) {
            for (int i = 0; i < orgEntityList.size(); i++) {
                String org_Id = orgEntityList.get(i).getOrgId();
                if (orgId.equals(org_Id)) {// ?????????????????????
                    return orgEntityList.get(i);
                }
            }
        }
        return null;
    }

    private RoleEntity getSqlIsRoleEntity(String powerId) {
        List<RoleEntity> roleEntities = getRoleEntityList(null);//??????????????????
        if (roleEntities.size() > 0) {
            for (int i = 0; i < roleEntities.size(); i++) {
                if (roleEntities.get(i) != null && !TextUtils.isEmpty(powerId)) {
                    String power_Id = roleEntities.get(i).getPowerId();
                    if (powerId.equals(power_Id)) {// ?????????????????????
                        return roleEntities.get(i);
                    }
                }
            }
        }
        return null;
    }

    /**
     * ?????????????????????????????????
     *
     * @return true:????????? false:?????????
     */
    public boolean isTrueRole(String functionId) {
        boolean isZy = false;
        String orgId = UserManager.getInstance().getOrgId();
        OrgEntity orgEntity = getSqlIsOrgEntity(orgId);
        String powerId = null;
        if (orgEntity != null) {
            powerId = orgEntity.getPowerId();
        }
        if (StrUtils.isNotEmpty(powerId) && powerId.equals("99999")) {// ???????????????
            isZy = true;
        } else {
            // ?????? ????????????????????? ??????
            RoleEntity roleEntity = getSqlIsRoleEntity(powerId);
            if (roleEntity != null) {
                isZy = getIsRole(roleEntity, functionId);
            }
        }
        return isZy;
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @return true:????????? false:?????????
     */
    public void isTxRole(String functionId, Role role) {
        boolean isZy = false;
        String orgId = UserManager.getInstance().getOrgId();
        OrgEntity orgEntity = getSqlIsOrgEntity(orgId);
        String powerId = null;
        if (orgEntity != null) {
            powerId = orgEntity.getPowerId();
        }
        if (StrUtils.isNotEmpty(powerId) && powerId.equals("99999")) {// ???????????????
            isZy = true;
        } else {
            // ?????? ????????????????????? ??????
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
        if (roleDatas != null && roleDatas.size() > 0) {//?????????????????????
            for (RoleEntity entity : roleDatas) {
                boolean isExc = false;// ????????????????????????
                for (int i = 0; i < roleEntityList.size(); i++) {
                    RoleEntity entity1 = roleEntityList.get(i);
                    if (entity.getPowerId().equals(entity1.getPowerId())) {// ??????????????????
                        isExc = true;
                        roleEntityList.set(i, entity);
                        break;
                    }
                }
                if (!isExc) {
                    roleEntityList.add(entity);// ??????????????????
                }
            }
        }
        return roleEntityList;
    }

    public List<OrgEntity> getOrgEntityList(OrgEntity orgEntity) {
        boolean isExc = false;// ????????????????????????
        if (orgEntity != null) {//?????????????????????
            for (int i = 0; i < orgEntityList.size(); i++) {
                OrgEntity entity = orgEntityList.get(i);
                if (entity.getOrgId().equals(orgEntity.getOrgId())) {// ??????????????????
                    isExc = true;
                    orgEntityList.set(i, orgEntity);
                }
            }
            if (!isExc) {
                orgEntityList.add(orgEntity);// ??????????????????
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
     * ?????? ????????? ????????? ??????
     *
     * @param orderList
     * @return
     * @author haoruigang
     */
    public ArrayList<String> removeDuplicateOrder(List<String> orderList) {
        Set<String> set = new TreeSet<>((a, b) -> {
            // ??????????????????asicc???????????????
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

    public interface OpenNotification {
        void Notification(PendingIntent intent);
    }

}
