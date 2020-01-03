package com.android.library;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();
    }

    /**
     * 获取系统上下文
     */
    public static Context getAppContext() {
        return mAppContext;
    }

}