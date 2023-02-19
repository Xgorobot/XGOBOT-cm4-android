package com.luwu.xgobot.data;

import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.SPUtils;
import com.luwu.xgobot.socket.SocketManager;

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

    //步幅
    public static void setStepLength(int length){//0-100
        byte[] datas = new byte[]{(byte) length, 0x00};
        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_BFKD,datas);
        SocketManager.getInstance().write(sendData);
    }

    //方向：0=停止，1=向前，2=向后，3=向左，4=向右，5=左旋，6=右旋
    public static void btnControl(int direction){
        byte[] datas = new byte[]{(byte) direction, 0x00};
        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_AJKZ,datas);
        SocketManager.getInstance().write(sendData);
    }

    public static String getWebUrl(){
//        webviewReference = new WeakReference<>(webView);
        String hostIp = SPUtils.getInstance().getString("host");
        int cameraPort = SPUtils.getInstance().getInt("cameraPort");
        return hostIp + ":" + cameraPort + "/video_feed";
//        return "http://www.baidu.com";
    }


    //陀螺仪开关
    public static void autoBalance(boolean enable){
        byte[] datas = new byte[]{(byte) (enable?1:0), 0x00};
        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_ZWDMS,datas);
        SocketManager.getInstance().write(sendData);
    }

    //舵机设置
    //腿ID：=1左前腿，=2右前腿，=3右后腿，4=左后腿。
    //舵机限制：上[-31, 31]，中[-66, 93], 下[-73, 57]
    public static void servoControl(int legID,int up,int middle,int down){
        byte[] datas = new byte[]{(byte) legID,(byte) up,(byte) middle,(byte) down, 0x00};
        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_KZDJ,datas);
        SocketManager.getInstance().write(sendData);
    }

    public static void xyzControl(int z){
        heightControl(z);
        Log.d(TAG, "xyzControl: " + "z Control" + z);
    }

    public static void rollControl(int roll){
        byte[] datas = new byte[]{(byte) roll, 0x00};
        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_KZROLL,datas);
        SocketManager.getInstance().write(sendData);
        Log.d(TAG, "rollControl: " + "roll Control" + roll);
    }

    public static void pitchControl(int pitch){
        byte[] datas = new byte[]{(byte) pitch, 0x00};
        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_KZPITCH,datas);
        SocketManager.getInstance().write(sendData);
        Log.d(TAG, "pitchControl: " + "pitch Control" + pitch);
    }

    public static void yawControl(int yaw){
        byte[] datas = new byte[]{(byte) yaw, 0x00};
        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_KZYAW,datas);
        SocketManager.getInstance().write(sendData);
        Log.d(TAG, "yawControl: " + "yaw Control" + yaw);
    }

    //单腿控制
    //腿ID：=1左前腿，=2右前腿，=3右后腿，4=左后腿。
    //腿限制：X[-35, 35]，Y[-18, 18], Z[75, 115]
    public static void legControl(int legID,int x,int y,int z){
        x = Math.min(35,x);
        x = Math.max(-35,x);
        y = Math.min(18,y);
        y = Math.max(-18,y);
        z = Math.min(115,z);
        z = Math.max(75,z);
        byte[] datas = new byte[]{(byte) legID,(byte) x,(byte) y,(byte) z, 0x00};
        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_KZDT,datas);
        SocketManager.getInstance().write(sendData);
    }

    //设置高度
    //高低范围：75-115。
    public static void heightControl(int height){
        height = Math.max(75,height);
        height = Math.min(115,height);
        byte[] datas = new byte[]{(byte) height, 0x00};
        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_KZGDZT,datas);
        SocketManager.getInstance().write(sendData);
    }

    //步态调整
    //0 trot 1 walk
    public static void setStepState(int state){
//        Log.d(TAG, "setStepState: " + state);
        if (state != 1 && state!=0)
            return;

        byte[] datas = new byte[]{(byte) state, 0x00};
        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_BTTZ,datas);
        SocketManager.getInstance().write(sendData);
    }


    //0结束 1-255 各种动作，目前应该只有20个动作设计
    public static void showMode(int state){
        byte[] datas = new byte[]{(byte) state, 0x00};
        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_KZDZ,datas);
        SocketManager.getInstance().write(sendData);
    }


    //动作轮播，0和1哪个是开文档也没说，按照前面的协议随便写一个了
    public static void showRepeat(boolean enable){
        byte[] datas = new byte[]{(byte) (enable?1:0), 0x00};
        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_DZLB,datas);
        SocketManager.getInstance().write(sendData);
    }

    //设置标定模式
    public static void setDebugMode(boolean enable){
        byte[] datas = new byte[]{(byte) (enable?1:0), 0x00};
        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_DJXZ,datas);
        SocketManager.getInstance().write(sendData);
    }

    //上中下抓取对应 128 129 130 其实就是表演模式的动作事件
    public static void grap(int state) {
        byte[] datas = new byte[]{(byte) state, 0x00};
        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_KZDZ,datas);
        SocketManager.getInstance().write(sendData);
    }

    //开启摄像头 1标准模式 2 全屏模式
    public static void startCamera(int state) {
        byte[] datas = new byte[]{(byte) state, 0x00};
        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_SPKZ,datas);
        SocketManager.getInstance().write(sendData);
    }
//    //速度：0-100%,相对速度最大值的百分比。 调整单步步长
//    public static void stepLength(int speed){
//        byte[] datas = new byte[]{(byte) speed, 0x00};
//        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_BFKD,datas);
//        SocketManager.getInstance().write(sendData);
//    }
//
//    //速度：默认为中速2。1为低速，3为高速。。 调整步伐频率
//    public static void stepFrequency(int speed){
//        byte[] datas = new byte[]{(byte) speed, 0x00};
//        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_BFPL,datas);
//        SocketManager.getInstance().write(sendData);
//    }


    //步幅
    public static void setXTrans(int length){//-35 - +35
        length = Math.max(-35,length);
        length = Math.min(35,length);
        byte[] datas = new byte[]{(byte) length, 0x00};
        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_XZPY,datas);
        SocketManager.getInstance().write(sendData);
        Log.d(TAG, "setXTrans: " + "setXTrans" + length);
    }

    //步幅
    public static void setYTrans(int length){//-18 - +18
        length = Math.max(-18,length);
        length = Math.min(18,length);
        byte[] datas = new byte[]{(byte) length, 0x00};
        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_YZPY,datas);
        SocketManager.getInstance().write(sendData);
        Log.d(TAG, "setXTrans: " + "setYTrans" + length);
    }

    public static void loadDeviceVersion(){
        byte[] datas = new byte[]{ 0x01, 0x00};
        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.SET_DQBBH,datas);
        SocketManager.getInstance().write(sendData);
    }
}
