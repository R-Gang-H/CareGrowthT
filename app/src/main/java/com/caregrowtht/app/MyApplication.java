package com.caregrowtht.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.bulong.rudeness.RudenessScreenHelper;
import com.caregrowtht.app.uitil.LogUtils;
import com.growingio.android.sdk.collection.Configuration;
import com.growingio.android.sdk.collection.GrowingIO;
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
    private static final String TAG = "TPush";

    public static MyApplication getInstance() {
        if (mApplication == null) {
            mApplication = new MyApplication();

        }
        return mApplication;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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

        GrowingIO.startWithConfiguration(this, new Configuration()
                .trackAllFragments()
                .setChannel("XXX应用商店")
        );

        initUmeng();

        initCloudChannel(this);

        struct();

    }

    private void initUmeng() {
        //友盟统计
        MobclickAgent.openActivityDurationTrack(false);


        //友盟分享
        UMConfigure.init(this, "6036088b668f9e17b8bd53f6"
                , "umeng", UMConfigure.DEVICE_TYPE_PHONE, "");

//        UMShareAPI.get(this);
//        PlatformConfig.setWeixin("wx699f25a2f9978b9f", "5a2c39a2b277418d422d4054d9e67d03");
//        //豆瓣RENREN平台目前只能在服务器端配置
//        PlatformConfig.setSinaWeibo("970470769", "1455c39e51ca095cd1f3aa20355caa17", "http://sns.whalecloud.com");
//        PlatformConfig.setQQZone("1106958872", "ggBNNbMZuSVzhYOL");

        //bugly
        CrashReport.initCrashReport(getApplicationContext(), "a2416f876e", false);

        //友盟统计
        MobclickAgent.openActivityDurationTrack(false);
    }

    /**
     * 初始化云推送通道
     *
     * @param applicationContext
     */
    private void initCloudChannel(final Context applicationContext) {
        // 创建notificaiton channel
        this.createNotificationChannel();
        PushServiceFactory.init(applicationContext);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                Log.i(TAG, "init cloudchannel success");
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Log.e(TAG, "init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
    }

    public static NotificationManager createNotificationChannel() {
        NotificationManager mNotificationManager = (NotificationManager) getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 通知渠道的id
            String id = getAppContext().getPackageName();
            // 用户可以看到的通知渠道的名字.
            CharSequence name = getAppContext().getString(R.string.app_name);
            // 用户可以看到的通知渠道的描述
            String description = "notification description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //最后在notificationmanager中创建该通知渠道
            mNotificationManager.createNotificationChannel(mChannel);
        }
        return mNotificationManager;
    }


    //解决NetworkOnMainThreadException异常
    public void struct() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork() // or
                // .detectAll()
                // for
                // all
                // detectable
                // problems
                .permitAll()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects() // 探测SQLite数据库操作
                .penaltyLog() // 打印logcat
                .penaltyDeath().build());
    }

}
