package com.luwu.xgo_robot.mMothed;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//提供全局静态方法和静态变量
public class PublicMethod {
    public static String localeLanguage; //语言
    public static boolean isBluetoothConnect = false;//蓝牙是否连接

    //定义XGO的内存表
    public static class XGORAM_ADDR {
        public final static byte workstate = 0x00;
        public final static byte battery = 0x01;
        public final static byte versions = 0x02;

        public final static byte connectBt = 0x10;
        public final static byte baudrateBt = 0x11;
        public final static byte passwordBt = 0x12;
        public final static byte nameBt = 0x13;

        public final static byte uninstallMotor = 0x20;
        public final static byte resetMotorZero = 0x21;

        public final static byte speedVx = 0x30;
        public final static byte speedVy = 0x31;
        public final static byte speedVyaw = 0x32;
        public final static byte bodyX = 0x33;
        public final static byte bodyY = 0x34;
        public final static byte bodyZ = 0x35;
        public final static byte bodyRoll = 0x36;
        public final static byte bodyPitch = 0x37;
        public final static byte bodyYaw = 0x38;
        public final static byte flagRoll = 0x39;
        public final static byte flagPitch = 0x3A;
        public final static byte flagYaw = 0x3B;
        public final static byte flagStep = 0x3C;
        public final static byte speedT = 0x3D;
        public final static byte action = 0x3E;

        public final static byte legX_1 = 0x40;
        public final static byte legY_1 = 0x41;
        public final static byte legZ_1 = 0x42;
        public final static byte legX_2 = 0x43;
        public final static byte legY_2 = 0x44;
        public final static byte legZ_2 = 0x45;
        public final static byte legX_3 = 0x46;
        public final static byte legY_3 = 0x47;
        public final static byte legZ_3 = 0x48;
        public final static byte legX_4 = 0x49;
        public final static byte legY_4 = 0x4A;
        public final static byte legZ_4 = 0x4B;

        public final static byte motor_11 = 0x50;
        public final static byte motor_12 = 0x51;
        public final static byte motor_13 = 0x52;
        public final static byte motor_21 = 0x53;
        public final static byte motor_22 = 0x54;
        public final static byte motor_23 = 0x55;
        public final static byte motor_31 = 0x56;
        public final static byte motor_32 = 0x57;
        public final static byte motor_33 = 0x58;
        public final static byte motor_41 = 0x59;
        public final static byte motor_42 = 0x5A;
        public final static byte motor_43 = 0x5B;
        public final static byte motor_speed = 0x5C;
        public final static byte motor_reset = 0x5D;

        public final static byte sensorLed = 0x60;
        public final static byte sensorIMU = 0x61;
        public final static byte sensorDistence = 0x62;
        public final static byte sensorUltrasonicH = 0x63;
        public final static byte sensorUltrasonicL = 0x64;
        public final static byte sensorRadio = 0x65;
        public final static byte sensorLedR = 0x66;
        public final static byte sensorLedG = 0x67;
        public final static byte sensorLedB = 0x68;
        public final static byte sensorMagnet = 0x69;
    }

    //定义XGO读回的数据
    public static class XGORAM_VALUE {

        public static int workstate = 0;
        public static int battery = 0;
        public static int versions = 0;

        public static int connectBt = 0;
        public static int baudrateBt = 0;
        public static int passwordBt = 0;
        public static int nameBt = 0;

        public static int uninstallMotor = 0;
        public static int resetMotorZero = 0;

        public static int speedVx = 0;
        public static int speedVy = 0;
        public static int speedVyaw = 0;
        public static int bodyX = 0;
        public static int bodyY = 0;
        public static int bodyZ = 0;
        public static int bodyRoll = 0;
        public static int bodyPitch = 0;
        public static int bodyYaw = 0;
        public static int flagRoll = 0;
        public static int flagPitch = 0;
        public static int flagYaw = 0;
        public static int flagStep = 0;
        public static int speedT = 0;

        public static int legX_1 = 0;
        public static int legY_1 = 0;
        public static int legZ_1 = 0;
        public static int legX_2 = 0;
        public static int legY_2 = 0;
        public static int legZ_2 = 0;
        public static int legX_3 = 0;
        public static int legY_3 = 0;
        public static int legZ_3 = 0;
        public static int legX_4 = 0;
        public static int legY_4 = 0;
        public static int legZ_4 = 0;

        public static int motor_11 = 0;
        public static int motor_12 = 0;
        public static int motor_13 = 0;
        public static int motor_21 = 0;
        public static int motor_22 = 0;
        public static int motor_23 = 0;
        public static int motor_31 = 0;
        public static int motor_32 = 0;
        public static int motor_33 = 0;
        public static int motor_41 = 0;
        public static int motor_42 = 0;
        public static int motor_43 = 0;
        public static int motor_speed = 0;
        public static int motor_reset = 0;

        public static int sensorLed = 0;
        public static int sensorIMU = 0;
        public static int sensorDistence = 0;
        public static int sensorUltrasonicH = 0;
        public static int sensorUltrasonicL = 0;
        public static int sensorRadio = 0;
        public static int sensorLedR = 0;
        public static int sensorLedG = 0;
        public static int sensorLedB = 0;
        public static int sensorMagnet = 0;
    }

    public static void hideBottomUIMenu(Context mcontext) {// 隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {//for lower api
            View v = ((Activity) mcontext).getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {//for new api versions.
            View decorView = ((Activity) mcontext).getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public static void hideBottomUIDialog(Dialog mDialog) {//Dialog隐藏虚拟按键 全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {//for lower api
            View v = mDialog.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {//for new api versions.
            View decorView = mDialog.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    //输入文件名只能输入汉字、英文字母和数字，并且数字不能开头
    //长度不能过长
    public static boolean verifyFilename(String name) {
//        if(name.length()>20){
//            return false;
//        }
        Pattern pattern = Pattern.compile("^[a-zA-Z\\u4e00-\\u9fa5][a-zA-Z0-9\\u4e00-\\u9fa5]*$");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            String temp = matcher.group();
            if (name.equals(temp)) {
                return true;//文件名合法
            }
        }
        return false;//文件名不合法
    }

    //把传入的int数据改为0x00-0xff数据 并返回byte类型
    public static byte toOrderRange(int num, int min, int max) {
        int temp = (num - min) * 255 / (max - min);
        return (byte) temp;
    }
    //把传入的byte(0x00-0xff)类型数据转为int值(0-255)
    public static int byteToInt(byte value){
        int temp = ((int) value) < 0 ? ((int) value) + 256 : ((int) value);
        return temp;
    }
}
