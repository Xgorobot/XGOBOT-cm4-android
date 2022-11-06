package com.luwu.xgo_robot.data;

import android.util.Log;
import android.webkit.WebView;

import java.lang.ref.WeakReference;

public class RobotFunction {
    private static final String TAG = "RobotFunction";


    public static void forward(){
        Log.d(TAG, "forward: ");
    }

    public static void backward(){
        Log.d(TAG, "backward: ");
    }
    public static void left(){
        Log.d(TAG, "left: ");
    }

    public static void right(){
        Log.d(TAG, "right: ");
    }

    public static String getWebUrl(String cameraUrl){
//        webviewReference = new WeakReference<>(webView);
//        return "192.168.31.1/webcam";
        return "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    }
}
