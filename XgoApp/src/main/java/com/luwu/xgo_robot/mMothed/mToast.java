package com.luwu.xgo_robot.mMothed;

import android.content.Context;
import android.widget.Toast;
//只出现一个Toast
public class mToast {
    private static Toast myToast;

    public mToast(Context context, String text) {
    }

    public static void show(Context context, String message) {
        if(context!=null && message!=null) {
            if (myToast != null) {
                myToast.cancel();
                myToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            } else {
                myToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            }
            myToast.show();
        }
    }
    public static void showLong(Context context, String message) {
        if (context != null && message != null) {
            if (myToast != null) {
                myToast.cancel();
                myToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            } else {
                myToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            }
            myToast.show();
        }
    }
}
