package com.luwu.xgo_robot.data;

import android.util.Log;

import java.util.HashMap;

public class RobotFunction {
    private static final String TAG = "RobotFunction";
    /**
     *
     * @param ySpeed 向前为正，向后为负
     * @param xSpeed 向右为正，向左为负
     */
    public static void move(int ySpeed,int xSpeed){
//        RobotControlBean bean = new RobotControlBean(new RobotControlBean.ControlBean("type",new HashMap<>()) ,"ok","0");
        Log.d(TAG, "move:  forwardSpeed:" + ySpeed  + "   horizontalSpeed" + xSpeed);
    }
}
