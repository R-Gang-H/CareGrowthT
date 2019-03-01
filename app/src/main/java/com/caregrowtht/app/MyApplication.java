package com.caregrowtht.app;

import android.util.Log;

import com.bulong.rudeness.RudenessScreenHelper;
import com.caregrowtht.app.uitil.LogUtils;
import com.lzy.okgo.OkGo;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zxy.tiny.Tiny;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by haoruigang on 2018-5-14 10:21:23.
 */

public class MyApplication extends com.android.library.MyApplication {
    private static MyApplication mApplication;

    public static MyApplication getInstance() {
        if (mApplication == null) {
            mApplication = new MyApplication();

        }
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //设计图标注的宽度
        int designWidth = 750;
        new RudenessScreenHelper(this, designWidth).activate();

        Logger.addLogAdapter(new AndroidLogAdapter());  // 初始化Logger

        // 开启log会影响滑动体验, 调试时才开启
        if (Constant.isTest) {
            LogUtils.openAll();
        } else {
            LogUtils.closeAll();
        }

        //压缩图片工具类
        Tiny.getInstance().init(this);

        //友盟分享
        UMConfigure.init(this, "5b23350c8f4a9d79a40000d0"
                , "umeng", UMConfigure.DEVICE_TYPE_PHONE, "");

        UMShareAPI.get(this);
        PlatformConfig.setWeixin("wx699f25a2f9978b9f", "5a2c39a2b277418d422d4054d9e67d03");
        //豆瓣RENREN平台目前只能在服务器端配置
        PlatformConfig.setSinaWeibo("970470769", "1455c39e51ca095cd1f3aa20355caa17", "http://sns.whalecloud.com");
        PlatformConfig.setQQZone("1106958872", "ggBNNbMZuSVzhYOL");

        //bugly
        CrashReport.initCrashReport(getApplicationContext(), "a2416f876e", false);

        //友盟统计
        MobclickAgent.openActivityDurationTrack(false);

        // ZXing
        ZXingLibrary.initDisplayOpinion(this);

        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
            public Map<String, String> onCrashHandleStart(int crashType, String errorType, String errorMessage, String errorStack) {
                LinkedHashMap<String, String> map = new LinkedHashMap<>();
                String x5CrashInfo = com.tencent.smtt.sdk.WebView.getCrashExtraMessage(getApplicationContext());
                map.put("x5crashInfo", x5CrashInfo);
                return map;
            }

            @Override


            public byte[] onCrashHandleStart2GetExtraDatas(int crashType, String errorType, String errorMessage, String errorStack) {
                try {
                    return "Extra data.".getBytes("UTF-8");
                } catch (Exception e) {
                    return null;
                }
            }
        });

        // 版本更新用的okhttp-utils
        OkHttpUtils.getInstance()
                .init(this)
                .debug(true, "okHttp")
                .timeout(20 * 1000);
        OkGo.getInstance().init(this);
    }
}
