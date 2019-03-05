package com.caregrowtht.app.view.version;

import android.app.Activity;
import android.text.TextUtils;

import com.android.library.utils.SystemUtils;
import com.caregrowtht.app.uitil.LogUtils;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.utils.AppUpdateUtils;

import org.json.JSONObject;

/**
 * 新版本版本检测回调
 */
public class UpdateCallback extends com.vector.update_app.UpdateCallback {

    private final Activity mActivity;

    public UpdateCallback(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * 解析json,自定义协议
     *
     * @param json 服务器返回的json
     * @return UpdateAppBean
     */
    protected UpdateAppBean parseJson(String json) {
        UpdateBean updateAppBean = new UpdateBean();
        try {
            JSONObject jsonObject = new JSONObject(json);
            updateAppBean.setNewVersionCode(jsonObject.optString("new_version_code"));
            updateAppBean.setUpdate(jsonObject.optString("update"))
                    //存放json，方便自定义解析
                    .setOriginRes(json)
                    .setNewVersion(jsonObject.optString("new_version_name"))
                    .setApkFileUrl(jsonObject.optString("apk_file_url"))
                    .setTargetSize(jsonObject.optString("target_size"))
                    .setUpdateLog(jsonObject.optString("update_log"))
                    .setConstraint(jsonObject.optBoolean("constraint"))
                    .setNewMd5(jsonObject.optString("new_md5"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updateAppBean;
    }

    /**
     * 有新版本
     *
     * @param updateApp        新版本信息
     * @param updateAppManager app更新管理器
     */
    @Override
    protected void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
        int clientVersionCode = SystemUtils.getVersionCode(mActivity);
        int serverVersionCode = Integer.parseInt(((UpdateBean) updateApp).getNewVersionCode());
        String clientVersionName = AppUpdateUtils.getVersionName(mActivity);
        String serverVersionName = updateApp.getNewVersion();
        LogUtils.d("UpdateCallback11111", clientVersionCode + ":" + serverVersionCode + ":" + clientVersionName + ":" + serverVersionName);
        //有新版本
        if (!TextUtils.isEmpty(clientVersionName) && !TextUtils.isEmpty(serverVersionName)
                && clientVersionCode < serverVersionCode && !clientVersionName.equals(serverVersionName)) {
            LogUtils.d("UpdateCallback*2****", clientVersionCode + ":" + serverVersionCode + ":" + clientVersionName + ":" + serverVersionName);
            updateAppManager.showDialogFragment();
        }
    }

}
