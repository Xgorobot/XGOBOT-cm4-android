package com.luwu.xgobot;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import com.blankj.utilcode.util.LanguageUtils;

import java.util.Locale;

import me.jessyan.autosize.AutoSizeConfig;

public class AppContext extends Application {

    private static AppContext appContext;

    public static AppContext getappContext() {
        return appContext;
    }

    @Override
    public void onCreate() {//继承自Application 一开始就调用
        super.onCreate();
        appContext = this;
        AutoSizeConfig.getInstance().setCustomFragment(true);
        LanguageUtils.applySystemLanguage();
    }

    @Override
    public void onTerminate() {
        //程序终止时运行
        super.onTerminate();
    }
}
