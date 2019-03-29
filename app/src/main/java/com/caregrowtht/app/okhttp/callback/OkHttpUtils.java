package com.caregrowtht.app.okhttp.callback;

import android.os.Build;
import android.util.Log;

import com.android.library.utils.RSAUtils;
import com.android.library.utils.U;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.uitil.LogUtils;
import com.caregrowtht.app.user.UserManager;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import zlc.season.rxdownload3.RxDownload;
import zlc.season.rxdownload3.core.Downloading;
import zlc.season.rxdownload3.core.Failed;
import zlc.season.rxdownload3.core.Normal;
import zlc.season.rxdownload3.core.Status;
import zlc.season.rxdownload3.core.Succeed;
import zlc.season.rxdownload3.core.Suspend;
import zlc.season.rxdownload3.core.Waiting;
import zlc.season.rxdownload3.extension.ApkInstallExtension;

/**
 * @author haoruigang
 * @Package com.haoruigang.okhttpdome
 * @project OkHttpDome
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2017/11/26 14:05
 */
public class OkHttpUtils {

    /**
     * 加密的参数时调用
     *
     * @param url
     * @param encodeMap 加密//     * @param obj       数据模型
     * @return
     */
    public static void getOkHttpJsonRequest(String tag, String url, Map<String, String> encodeMap, HttpCallBack callBack) {
        JSONObject jsonObj = new JSONObject(encodeMap);
        String jsonParams = jsonObj.toString();
        Log.d(tag, "请求:" + url + "    加密前的参数：" + jsonParams);

        if (encodeMap != null) {
            if (!url.contains("/code") && !url.contains("/login") && !url.contains("/autoLogin") && !url.contains("/reg")) {
                String token = U.MD5(UserManager.getInstance().userData.getToken() + "_" + Constant.API_KEY);
                encodeMap.put("uid", UserManager.getInstance().userData.getUid());
                encodeMap.put("token", token);
            }
            encodeMap.put("version", "3");
            encodeMap.put("deviceId", Build.SERIAL);
            encodeMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
            encodeMap.put("appKey", U.MD5(Constant.API_KEY));
            encodeMap.put("registerType", "2");    // registerType 是账号类型 1：家长端  2：教师端
            LogUtils.d(tag, "参数----" + U.transMap2String(encodeMap));
            // RSA
            try {
                // 从字符串中得到公钥
                RSAUtils.loadPublicKey(Constant.PUBLIC_KEY);
                // 加密
                String encryptByte = RSAUtils.encryptWithRSA(U.transMap2String(encodeMap));
                Logger.d("加密：" + encryptByte);
                HashMap<String, String> params = new HashMap<>();
                params.put("params", encryptByte);  // 加密的参数串
                com.lzy.okhttputils.OkHttpUtils.post(url)
                        .params(params)
                        .execute(callBack);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            com.lzy.okhttputils.OkHttpUtils.post(url)
                    .execute(callBack);
        }
    }

    /**
     * 不加密的参数时调用
     *
     * @param url
     * @param encodeMap
     * @param map
     * @param callBack
     */
    public static void getOkHttpJsonRequest(String tag, String url, Map<String, String> encodeMap, Map<String, String> map, HttpCallBack callBack) {
        JSONObject jsonObj = new JSONObject(encodeMap);
        String jsonParams = jsonObj.toString();
        Log.d(tag, "请求:" + url + "    不加密的参数：" + jsonParams);

        if (map != null && encodeMap != null) {
            String token = U.MD5(UserManager.getInstance().userData.getToken() + "_" + Constant.API_KEY);
            if (!url.contains("/code") && !url.contains("/login")) {
                encodeMap.put("uid", UserManager.getInstance().userData.getUid());
                encodeMap.put("token", token);
            }
            encodeMap.put("version", "3");
            encodeMap.put("deviceId", Build.SERIAL);
            encodeMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
            encodeMap.put("appKey", U.MD5(Constant.API_KEY));
            encodeMap.put("registerType", "2");   // registerType 是账号类型 1：家长端  2：教师端
            Logger.d("参数----" + U.transMap2String(encodeMap));
            // RSA
            try {
                // 从字符串中得到公钥
                RSAUtils.loadPublicKey(Constant.PUBLIC_KEY);
                // 加密
                String encryptByte = RSAUtils.encryptWithRSA(U.transMap2String(encodeMap));

                Logger.d("不加密：" + U.transMap2String(map));
                Logger.d("加密：" + encryptByte);
                HashMap<String, String> params = new HashMap<>();
                params.put("params", encryptByte);  // 加密的参数串
                params.putAll(map);   // 不加密的参数
                com.lzy.okhttputils.OkHttpUtils.post(url)
                        .params(params)
                        .execute(callBack);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            com.lzy.okhttputils.OkHttpUtils.post(url)
                    .execute(callBack);
        }
    }


    /**
     * 下载文件
     *
     * @param url
     * @param download
     * @return
     */
    public static Disposable download(String url, Download download) {
        return RxDownload.INSTANCE.create(url, true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(download::onSuccess);
    }

    public static void setProgress(Status status) {
        int Max = (int) status.getTotalSize();
        int Progresss = (int) status.getDownloadSize();
        String showText = status.percent();
        String sizeText = status.formatString();
    }

    public static String setActionText(Status status) {
        String text = "";
        if (status instanceof Normal) {
            text = "开始";
        } else if (status instanceof Suspend) {
            text = "已暂停";
        } else if (status instanceof Waiting) {
            text = "等待中";
        } else if (status instanceof Downloading) {
            text = "暂停";
        } else if (status instanceof Failed) {
            text = "失败";
        } else if (status instanceof Succeed) {
            text = "安装";
        } else if (status instanceof ApkInstallExtension.Installing) {
            text = "安装中";
        } else if (status instanceof ApkInstallExtension.Installed) {
            text = "打开";
        }
        return text;
    }

    public static void dispatchClick(Status currentStatus, String url) {
        if (currentStatus instanceof Normal) {
            start(url);
        } else if (currentStatus instanceof Suspend) {
            start(url);
        } else if (currentStatus instanceof Failed) {
            start(url);
        } else if (currentStatus instanceof Downloading) {
            stop(url);
        } else if (currentStatus instanceof Succeed) {
            install(url);
        } else if (currentStatus instanceof ApkInstallExtension.Installed) {
            open();
        }
    }

    private static void start(String url) {
        RxDownload.INSTANCE.start(url).subscribe();
    }

    private static void install(String url) {
        RxDownload.INSTANCE.extension(url, ApkInstallExtension.class).subscribe();
    }

    private static void stop(String url) {
        RxDownload.INSTANCE.stop(url).subscribe();
    }

    private static void open() {
        //TODO: open app
    }

    public interface Download {
        void onSuccess(Status status);
    }
}
