package com.luwu.xgo_robot.data;

import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.luwu.xgo_robot.socket.SocketManager;

import java.lang.ref.WeakReference;

/**
 * 机器人指令定义
 */
public class RobotFunction {
    private static final String TAG = "RobotFunction";

    //速度：默认为中速2。1为低速，3为高速。
    public static void setSpeed(int speed){
        byte[] datas = new byte[]{(byte) speed, 0x00};
        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_BFPL,datas);
        SocketManager.getInstance().write(sendData);
    }

    //方向：0=停止，1=向前，2=向后，3=向左，4=向右，5=左旋，6=右旋
    public static void btnControl(int direction){
        byte[] datas = new byte[]{(byte) direction, 0x00};
        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_AJKZ,datas);
        SocketManager.getInstance().write(sendData);
    }

    public static String getWebUrl(String cameraUrl){
//        webviewReference = new WeakReference<>(webView);
//        return "192.168.31.1/webcam";
        return "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    }

}
