package com.luwu.xgo_robot;

import android.app.Application;

import com.luwu.xgo_robot.BlueTooth.BleClient;
import com.luwu.xgo_robot.mMothed.PublicMethod;
import com.luwu.xgo_robot.mMothed.mToast;

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
                PublicMethod.isSocketConnect = true;
                mToast.show(AppContext.this,"device connected");
            }

            @Override
            public void connectFailed() {
                PublicMethod.isSocketConnect = false;
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
