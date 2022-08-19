package com.luwu.xgo_robot.mControl;

import android.util.Log;

import com.luwu.xgo_robot.mActivity.MainActivity;

import static com.luwu.xgo_robot.mMothed.PublicMethod.XGORAM_VALUE;
import static com.luwu.xgo_robot.mMothed.PublicMethod.XGORAM_ADDR;
import static com.luwu.xgo_robot.mMothed.PublicMethod.toOrderRange;

public class XMLResolverFunction {

    public XMLResolverFunction() {
    }

    //singleActivity中的
    public synchronized void input_position(int which, int top, int middle, int bottom) {
        switch (which) {
            case 1://左前腿
                MainActivity.addMessage(new byte[]{0x50, toOrderRange(bottom, 0, 255), toOrderRange(middle, 0, 255), toOrderRange(top, 0, 255)});
//                Log.d("Tag", "我左前腿各舵机位置-上："+top+" 中："+middle+" 下："+bottom);
                break;
            case 2://右前腿
                MainActivity.addMessage(new byte[]{0x53, toOrderRange(bottom, 0, 255), toOrderRange(middle, 0, 255), toOrderRange(top, 0, 255)});
//                Log.d("Tag", "我右前腿各舵机位置-上："+top+" 中："+middle+" 下："+bottom);
                break;
            case 3://右后腿
                MainActivity.addMessage(new byte[]{0x56, toOrderRange(bottom, 0, 255), toOrderRange(middle, 0, 255), toOrderRange(top, 0, 255)});
//                Log.d("Tag", "我右后腿各舵机位置-上："+top+" 中："+middle+" 下："+bottom);
                break;
            case 4://左后腿
                MainActivity.addMessage(new byte[]{0x59, toOrderRange(bottom, 0, 255), toOrderRange(middle, 0, 255), toOrderRange(top, 0, 255)});
//                Log.d("Tag", "我左后腿各舵机位置-上："+top+" 中："+middle+" 下："+bottom);
                break;
            default:
                Log.d("Tag", "input_position指令错误");
                break;
        }
    }

    public synchronized void set_motorspeed(int speed) {
        MainActivity.addMessage(new byte[]{0x5C, toOrderRange(speed, 0, 255)});//设置舵机模式中的速度
        Log.d("Tag", "设置舵机速度为" + speed);
    }

    public synchronized void input_reset() {
        //todo:这里改为舵机模式下 把所有舵机位置改为站立时的位置 需要测量
        MainActivity.addMessage(new byte[]{0x5D, (byte) 0x01});//恢复站立姿势 退出舵机模式
//        Log.d("Tag", "我各个腿恢复了指定位置");
    }

    //wholeActivity中的
    //运动
    public synchronized void move_direction_speed(int direction, int speed) {
        switch (direction) {
            case 1://前
                MainActivity.addMessage(new byte[]{0x30, toOrderRange(speed, -255, 255)});
//                Log.d("Tag", "我向前以" + speed + "的速度奔跑");
                break;
            case 2://后
                MainActivity.addMessage(new byte[]{0x30, toOrderRange(-speed, -255, 255)});
//                Log.d("Tag", "我向后以" + speed + "的速度奔跑");
                break;
            case 3://左
                MainActivity.addMessage(new byte[]{0x31, toOrderRange(speed, -255, 255)});
//                Log.d("Tag", "我向左以" + speed + "的速度奔跑");
                break;
            case 4://右
                MainActivity.addMessage(new byte[]{0x31, toOrderRange(-speed, -255, 255)});
//                Log.d("Tag", "我向右以" + speed + "的速度奔跑");
                break;
            case 5://顺时针
                MainActivity.addMessage(new byte[]{0x32, toOrderRange(speed, -255, 255)});
//                Log.d("Tag", "我顺时针以" + speed + "的速度奔跑");
                break;
            case 6://逆时针
                MainActivity.addMessage(new byte[]{0x32, toOrderRange(-speed, -255, 255)});
//                Log.d("Tag", "我逆时针以" + speed + "的速度奔跑");
                break;
            default:
                Log.d("Tag", "move_direction_speed指令错误");
                break;
        }
    }


    public synchronized void move_direction_speed_time(int direction, int speed, int time) {
        switch (direction) {
            case 1://前
                MainActivity.addMessage(new byte[]{0x30, toOrderRange(speed, -255, 255)});
//                Log.d("Tag", "我向前以" + speed + "的速度奔跑" + time + "秒");
                break;
            case 2://后
                MainActivity.addMessage(new byte[]{0x30, toOrderRange(-speed, -255, 255)});
//                Log.d("Tag", "我向后以" + speed + "的速度奔跑" + time + "秒");
                break;
            case 3://左
                MainActivity.addMessage(new byte[]{0x31, toOrderRange(speed, -255, 255)});
//                Log.d("Tag", "我向左以" + speed + "的速度奔跑" + time + "秒");
                break;
            case 4://右
                MainActivity.addMessage(new byte[]{0x31, toOrderRange(-speed, -255, 255)});
//                Log.d("Tag", "我向右以" + speed + "的速度奔跑" + time + "秒");
                break;
            case 5://顺时针
                MainActivity.addMessage(new byte[]{0x32, toOrderRange(speed, -255, 255)});
//                Log.d("Tag", "我顺时针以" + speed + "的速度奔跑" + time + "秒");
                break;
            case 6://逆时针
                MainActivity.addMessage(new byte[]{0x32, toOrderRange(-speed, -255, 255)});
//                Log.d("Tag", "我逆时针以" + speed + "的速度奔跑" + time + "秒");
                break;
            default:
                Log.d("Tag", "move_direction_speed_time指令错误");
                break;
        }
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        switch (direction) {
            case 1://前
            case 2://后
                MainActivity.addMessage(new byte[]{0x30, (byte) 0x80});
                break;
            case 3://左
            case 4://右
                MainActivity.addMessage(new byte[]{0x31, (byte) 0x80});
                break;
            case 5://顺时针
            case 6://逆时针
                MainActivity.addMessage(new byte[]{0x32, (byte) 0x80});
                break;
            default:
                Log.d("Tag", "move_direction_speed_time指令错误");
                break;
        }
    }


    public synchronized void move_zero_speed(int speed) {
        switch (speed) {
            case 1://很快
                MainActivity.addMessage(new byte[]{0x3C, (byte) 0xff});
//                Log.d("Tag", "我以很快的速度原地奔跑");
                break;
            case 2://快
                MainActivity.addMessage(new byte[]{0x3C, (byte) 0xB5});
//                Log.d("Tag", "我以快的速度原地奔跑");
                break;
            case 3://慢
                MainActivity.addMessage(new byte[]{0x3C, (byte) 0x80});
//                Log.d("Tag", "我以慢的速度原地奔跑");
                break;
            case 4://很慢
                MainActivity.addMessage(new byte[]{0x3C, (byte) 0x50});
//                Log.d("Tag", "我以很慢的速度原地奔跑");
                break;
            default:
                Log.d("Tag", "move_zero_speed指令错误");
                break;
        }
    }


    public synchronized void move_zero_speed_time(int speed, int time) {
        switch (speed) {
            case 1://很快
                MainActivity.addMessage(new byte[]{0x3C, (byte) 0xff});
//                Log.d("Tag", "我以很快的速度原地奔跑" + time + "秒");
                break;
            case 2://快
                MainActivity.addMessage(new byte[]{0x3C, (byte) 0xB5});
//                Log.d("Tag", "我以快的速度原地奔跑" + time + "秒");
                break;
            case 3://慢
                MainActivity.addMessage(new byte[]{0x3C, (byte) 0x80});
//                Log.d("Tag", "我以慢的速度原地奔跑" + time + "秒");
                break;
            case 4://很慢
                MainActivity.addMessage(new byte[]{0x3C, (byte) 0x50});
//                Log.d("Tag", "我以很慢的速度原地奔跑" + time + "秒");
                break;
            default:
                Log.d("Tag", "move_zero_speed_time指令错误");
                break;
        }
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MainActivity.addMessage(new byte[]{0x3C, (byte) 0x00});
    }


    public synchronized void move_stop() {
        MainActivity.addMessage(new byte[]{0x30, (byte) 0x80, (byte) 0x80, (byte) 0x80});
//        Log.d("Tag", "我停止了奔跑");
    }


    public synchronized void shake_direction_speed(int direction, int speed) {
        byte addr = 0x39, value = 0x00;
        switch (direction) {
            case 1://上下
                addr = (byte) 0x3A;
//                Log.d("Tag", "我上下摇晃");
                break;
            case 2://左右
                addr = (byte) 0x39;
//                Log.d("Tag", "我左右摇晃");
                break;
            case 3://前后
                addr = (byte) 0x3B;
//                Log.d("Tag", "我前后摇晃");
                break;
            default:
                Log.d("Tag", "shake_direction_speed指令错误");
                break;
        }
        switch (speed) {
            case 1://很快
                value = (byte) 0xff;
//                Log.d("Tag", "我很快摇晃");
                break;
            case 2://快
                value = (byte) 0xB5;
//                Log.d("Tag", "我快摇晃");
                break;
            case 3://慢
                value = (byte) 0x80;
//                Log.d("Tag", "我慢摇晃");
                break;
            case 4://很慢
                value = (byte) 0x50;
//                Log.d("Tag", "我很慢摇晃");
                break;
            default:
                Log.d("Tag", "shake_direction_speed指令错误");
                break;
        }
        MainActivity.addMessage(new byte[]{addr, value});
    }


    public synchronized void shake_direction_speed_time(int direction, int speed, int time) {
        byte addr = 0x39, value = 0x00;
        switch (direction) {
            case 1://上下
                addr = (byte) 0x3A;
//                Log.d("Tag", "我以上下摇晃" + time + "秒");
                break;
            case 2://左右
                addr = (byte) 0x39;
//                Log.d("Tag", "我左右摇晃" + time + "秒");
                break;
            case 3://前后
                addr = (byte) 0x3B;
//                Log.d("Tag", "我前后摇晃" + time + "秒");
                break;
            default:
                Log.d("Tag", "shake_direction_speed_time指令错误");
                break;
        }
        switch (speed) {
            case 1://很快
                value = (byte) 0xff;
//                Log.d("Tag", "我很快摇晃" + time + "秒");
                break;
            case 2://快
                value = (byte) 0xB5;
//                Log.d("Tag", "我快摇晃" + time + "秒");
                break;
            case 3://慢
                value = (byte) 0x80;
//                Log.d("Tag", "我慢摇晃" + time + "秒");
                break;
            case 4://很慢
                value = (byte) 0x50;
//                Log.d("Tag", "我很慢摇晃" + time + "秒");
                break;
            default:
                Log.d("Tag", "shake_direction_speed_time指令错误");
                break;
        }
        MainActivity.addMessage(new byte[]{addr, value});
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        switch (direction) {
            case 1://上下
                MainActivity.addMessage(new byte[]{0x3A, 0x00});
                break;
            case 2://左右
                MainActivity.addMessage(new byte[]{0x39, 0x00});
                break;
            case 3://前后
                MainActivity.addMessage(new byte[]{0x3B, 0x00});
                break;
            default:
                Log.d("Tag", "shake_direction_speed_time指令错误");
                break;
        }
    }


    public synchronized void shake_stop() {
        MainActivity.addMessage(new byte[]{0x39, 0x00, 0x00, 0x00});
//        Log.d("Tag", "我停止了摇晃");
    }

    public synchronized void all_stop() {
        MainActivity.addMessage(new byte[]{0x30, (byte) 0x80, (byte) 0x80, (byte) 0x80});
        MainActivity.addMessage(new byte[]{0x39, 0x00, 0x00, 0x00});
        Log.d("Tag", "我停止了运动");
    }

    public synchronized void open_imu(int imu) {
        switch (imu) {
            case 1://开启IMU
                MainActivity.addMessage(new byte[]{0x61, 0x01});
//                Log.d("Tag", "打开了IMU");
                break;
            case 2://关闭IMU
                MainActivity.addMessage(new byte[]{0x61, 0x00});
//                Log.d("Tag", "关闭了IMU");
                break;
            default:
                Log.d("Tag", "open_imu指令错误");
                break;
        }
    }

    public synchronized String state_imu() {

        if (XGORAM_VALUE.sensorIMU == 0x01) {
            return "TRUE";
        }
//        Log.d("Tag", "IMU开了");
        return "FALSE";
    }

    //传感器
    public synchronized String sensor_avoid(int which) {
//        switch (which) {
//            case 1://前方传感器
////                Log.d("Tag", "前方传感器是开的");
//                break;
//            case 2://后方传感器
////                Log.d("Tag", "后方传感器是开的");
//                break;
//            case 3://左方传感器
////                Log.d("Tag", "左方传感器是开的");
//                break;
//            case 4://右方传感器
////                Log.d("Tag", "右方传感器是开的");
//                break;
//            default:
//                Log.d("Tag", "sensor_avoid指令错误");
//                break;
//        }
        if (XGORAM_VALUE.sensorDistence == 0x01) {
            return "TRUE";
        }
        return "FALSE";
    }

    public int sensor_Ultrasonic() {
        return XGORAM_VALUE.sensorUltrasonicH * 255 + XGORAM_VALUE.sensorUltrasonicL;
    }

    public String sensor_Magnet() {
        if (XGORAM_VALUE.sensorMagnet == 0x01) {
            return "TRUE";
        }
        return "FALSE";
    }

    //声光
    public synchronized void open_led(int state, int which) {
        switch (state) {
            case 1://开启LED
                MainActivity.addMessage(new byte[]{0x60, 0x01});
//                Log.d("Tag", "打开了LED");
                break;
            case 2://关闭LED
                MainActivity.addMessage(new byte[]{0x60, 0x00});
//                Log.d("Tag", "关闭了LED");
                break;
            default:
                Log.d("Tag", "open_led指令错误");
                break;
        }
//        switch (which) {
//            case 1://前方LED
//                Log.d("Tag", "前方LED");
//                break;
//            case 2://后方LED
//                Log.d("Tag", "后方LED");
//                break;
//            case 3://左方LED
//                Log.d("Tag", "左方LED");
//                break;
//            case 4://右方LED
//                Log.d("Tag", "右方LED");
//                break;
//            default:
//                Log.d("Tag", "open_led指令错误");
//                break;
//        }
    }


    public synchronized String state_led(int which) {
//        switch (which) {
//            case 1://前方LED
//                Log.d("Tag", "前方LED是关的");
//                break;
//            case 2://后方LED
//                Log.d("Tag", "后方LED是关的");
//                break;
//            case 3://左方LED
//                Log.d("Tag", "左方LED是开的");
//                break;
//            case 4://右方LED
//                Log.d("Tag", "右方LED是开的");
//                break;
//            default:
//                Log.d("Tag", "state_led指令错误");
//                break;
//        }

        if (XGORAM_VALUE.sensorLed == 0x01) {
            return "TRUE";
        }
        return "FALSE";
    }

    public void play_radio() {
        MainActivity.addMessage(new byte[]{XGORAM_ADDR.sensorRadio,0x01});
    }

    public void open_ledRGB(int r, int g, int b) {
        MainActivity.addMessage(new byte[]{XGORAM_ADDR.sensorLedR,toOrderRange(r,0,255),toOrderRange(g,0,255),toOrderRange(b,0,255)});
    }

    //逻辑
    public synchronized void control_wait(int time, int which) {
        try {
            if (which == 1) {
                Thread.sleep(time * 1000);
            } else {
                Thread.sleep(time);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        Log.d("Tag", "等待了" + time + "秒");
    }

}
