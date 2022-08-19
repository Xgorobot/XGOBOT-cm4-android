package com.luwu.xgo_robot.socket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.luwu.xgo_robot.AppContext;

public class WifiStateChangeManager {
    private WifiStateChangeManager instance;

    public WifiStateChangeManager getInstance() {
        if (instance == null){
            instance = new WifiStateChangeManager();
            init();
        }
        return instance;
    }

    private WifiStateChangeManager(){}

    private void init(){
       AppContext.getappContext().registerReceiver(broadcastReceiver,new IntentFilter(一些WIFI状态的改变));

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (WIFI连接){
                开启UDP
            }
            if (WIFI断开){
                关闭UDP
            }
        }
    };

    private void initUDP(){
        new UdpClient().setMsgListener;
    }
}
