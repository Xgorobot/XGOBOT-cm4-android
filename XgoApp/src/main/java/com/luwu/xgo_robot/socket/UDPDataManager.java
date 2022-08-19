package com.luwu.xgo_robot.socket;

import android.text.TextUtils;

import com.luwu.xgo_robot.data.DataManager;

public class UDPDataManager {
    public static void ResloveUDPMsg(String msg){
        if (收到机器狗广播){
            String tcpAddress;
            String tcpPort;
            if (TextUtils.isEmpty(tcpAddress) || TextUtils.isEmpty(tcpPort)){
                return;
            }
            DataManager.getInstance().connectTCP(tcpAddress,tcpPort);
        }
    }
}
