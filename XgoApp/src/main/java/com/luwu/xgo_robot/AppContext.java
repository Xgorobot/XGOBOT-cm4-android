package com.luwu.xgo_robot;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.luwu.xgo_robot.BlueTooth.BleClient;
import com.luwu.xgo_robot.mMothed.PublicMethod;
import com.luwu.xgo_robot.mMothed.mToast;

import java.util.Locale;

import static com.luwu.xgo_robot.mMothed.PublicMethod.localeLanguage;

public class AppContext extends Application {

    private static AppContext appContext;
    private static BleClient mBleClient;


    public static AppContext getappContext() {
        return appContext;
    }


    @Override
    public void onCreate() {//继承自Application 一开始就调用
        super.onCreate();
        appContext = this;
        initBle();
    }

    public void initBle(){
        mBleClient = new BleClient(this);
        mBleClient.setmConnectRequest(new BleClient.ConnectionRequest() {
            @Override
            public void connectSuccess() {
                PublicMethod.isBluetoothConnect = true;
                mToast.show(AppContext.this,"device connected");
            }

            @Override
            public void connectFailed() {
                PublicMethod.isBluetoothConnect = false;
                mToast.show(AppContext.this,"device disconnect");
            }
        });
    }
    public static BleClient getmBleClient(){
        return mBleClient;
    }

    @Override
    public void onTerminate() {
        //程序终止时运行
        mBleClient.onDestroy();
        super.onTerminate();
    }
}
