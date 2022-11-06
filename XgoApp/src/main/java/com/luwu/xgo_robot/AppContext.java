package com.luwu.xgo_robot;

import android.app.Application;

public class AppContext extends Application {

    private static AppContext appContext;

    public static AppContext getappContext() {
        return appContext;
    }

    @Override
    public void onCreate() {//继承自Application 一开始就调用
        super.onCreate();
        appContext = this;
    }

    @Override
    public void onTerminate() {
        //程序终止时运行
        super.onTerminate();
    }
}
