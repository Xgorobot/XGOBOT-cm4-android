package com.luwu.xgo_robot.data;

import android.util.Log;
import android.webkit.WebView;

import com.luwu.xgo_robot.socket.SocketManager;

import java.lang.ref.WeakReference;

public class RobotFunction {
    private static final String TAG = "RobotFunction";

    /*
    * speed 左正右负 上正下负
    * */
    public static void forward(int speed){
        Log.d(TAG, "forward: ");
        String str = String.format("{\"func\":\"forward\",\"speed\":%s}",speed);
        SocketManager.getInstance().write(str);
    }

    public static void backward(int speed){
        Log.d(TAG, "backward: ");
        String str = String.format("{\"func\":\"backward\",\"speed\":%s}",speed);
        SocketManager.getInstance().write(str);
    }
    public static void left(int speed){
        Log.d(TAG, "left: ");
        String str = String.format("{\"func\":\"left\",\"speed\":%s}",speed);
        SocketManager.getInstance().write(str);
    }

    public static void right(int speed){
        Log.d(TAG, "right: ");
        String str = String.format("{\"func\":\"right\",\"speed\":%s}",speed);
        SocketManager.getInstance().write(str);
    }

    public static String getWebUrl(String cameraUrl){
//        webviewReference = new WeakReference<>(webView);
//        return "192.168.31.1/webcam";
        return "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    }
}
