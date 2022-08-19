package com.luwu.xgo_robot.mControl;

import android.app.Activity;
import android.util.Log;
import android.webkit.JavascriptInterface;

/*
 *文件定义了提供给javascript调用的函数 与generator.js一一对应
 * 2020/2/28放弃调用js 改为自己解析xml文件
 */
public class MoveInterface {

    private Activity activity;

    public MoveInterface(Activity activity) {
        this.activity = activity;
    }

    //运动
    @JavascriptInterface
    public synchronized void move_direction_speed(int direction, int speed) {
        switch (direction) {
            case 1://前
                Log.d("Tag", "我向前以"+speed+"的速度奔跑");
                break;
            case 2://后
                Log.d("Tag", "我向后以"+speed+"的速度奔跑");
                break;
            case 3://左
                Log.d("Tag", "我向左以"+speed+"的速度奔跑");
                break;
            case 4://右
                Log.d("Tag", "我向右以"+speed+"的速度奔跑");
                break;
            case 5://顺时针
                Log.d("Tag", "我顺时针以"+speed+"的速度奔跑");
                break;
            case 6://逆时针
                Log.d("Tag", "我逆时针以"+speed+"的速度奔跑");
                break;
            default:
                Log.d("Tag", "move_direction_speed指令错误");
                break;
        }
    }

    @JavascriptInterface
    public synchronized void move_direction_speed_time(int direction, int speed, int time) {
        switch (direction) {
            case 1://前
                Log.d("Tag", "我向前以"+speed+"的速度奔跑"+time+"秒");
                break;
            case 2://后
                Log.d("Tag", "我向后以"+speed+"的速度奔跑"+time+"秒");
                break;
            case 3://左
                Log.d("Tag", "我向左以"+speed+"的速度奔跑"+time+"秒");
                break;
            case 4://右
                Log.d("Tag", "我向右以"+speed+"的速度奔跑"+time+"秒");
                break;
            case 5://顺时针
                Log.d("Tag", "我顺时针以"+speed+"的速度奔跑"+time+"秒");
                break;
            case 6://逆时针
                Log.d("Tag", "我逆时针以"+speed+"的速度奔跑"+time+"秒");
                break;
            default:
                Log.d("Tag", "move_direction_speed_time指令错误");
                break;
        }
    }

    @JavascriptInterface
    public synchronized void move_zero_speed(int speed) {
        switch (speed) {
            case 1://很快
                Log.d("Tag", "我以很快的速度原地奔跑");
                break;
            case 2://快
                Log.d("Tag", "我以快的速度原地奔跑");
                break;
            case 3://慢
                Log.d("Tag", "我以慢的速度原地奔跑");
                break;
            case 4://很慢
                Log.d("Tag", "我以很慢的速度原地奔跑");
                break;
            default:
                Log.d("Tag", "move_zero_speed指令错误");
                break;
        }
    }

    @JavascriptInterface
    public synchronized void move_zero_speed_time(int speed, int time) {
        switch (speed) {
            case 1://很快
                Log.d("Tag", "我以很快的速度原地奔跑"+time+"秒");
                break;
            case 2://快
                Log.d("Tag", "我以快的速度原地奔跑"+time+"秒");
                break;
            case 3://慢
                Log.d("Tag", "我以慢的速度原地奔跑"+time+"秒");
                break;
            case 4://很慢
                Log.d("Tag", "我以很慢的速度原地奔跑"+time+"秒");
                break;
            default:
                Log.d("Tag", "move_zero_speed_time指令错误");
                break;
        }
    }

    @JavascriptInterface
    public synchronized void move_stop() {
        Log.d("Tag", "我停止了奔跑");
    }

    @JavascriptInterface
    public synchronized void shake_direction_speed(int direction, int speed) {
        switch (direction) {
            case 1://上下
                Log.d("Tag", "我以上下摇晃");
                break;
            case 2://左右
                Log.d("Tag", "我左右摇晃");
                break;
            case 3://前后
                Log.d("Tag", "我前后摇晃");
                break;
            default:
                Log.d("Tag", "shake_direction_speed指令错误");
                break;
        }
        switch (speed) {
            case 1://很快
                Log.d("Tag", "我很快摇晃");
                break;
            case 2://快
                Log.d("Tag", "我快摇晃");
                break;
            case 3://慢
                Log.d("Tag", "我慢摇晃");
                break;
            case 4://很慢
                Log.d("Tag", "我很慢摇晃");
                break;
            default:
                Log.d("Tag", "shake_direction_speed指令错误");
                break;
        }

    }

    @JavascriptInterface
    public synchronized void shake_direction_speed_time(int direction, int speed, int time) {
        switch (direction) {
            case 1://上下
                Log.d("Tag", "我以上下摇晃"+time+"秒");
                break;
            case 2://左右
                Log.d("Tag", "我左右摇晃"+time+"秒");
                break;
            case 3://前后
                Log.d("Tag", "我前后摇晃"+time+"秒");
                break;
            default:
                Log.d("Tag", "shake_direction_speed_time指令错误");
                break;
        }
        switch (speed) {
            case 1://很快
                Log.d("Tag", "我很快摇晃"+time+"秒");
                break;
            case 2://快
                Log.d("Tag", "我快摇晃"+time+"秒");
                break;
            case 3://慢
                Log.d("Tag", "我慢摇晃"+time+"秒");
                break;
            case 4://很慢
                Log.d("Tag", "我很慢摇晃"+time+"秒");
                break;
            default:
                Log.d("Tag", "shake_direction_speed_time指令错误");
                break;
        }
    }

    @JavascriptInterface
    public synchronized void shake_stop() {
        Log.d("Tag", "我停止了摇晃");
    }

    @JavascriptInterface
    public synchronized void all_stop() {
        Log.d("Tag", "我停止了运动");
    }

    @JavascriptInterface
    public synchronized void open_imu(int imu) {
        switch (imu) {
            case 1://开启IMU
                Log.d("Tag", "打开了IMU");
                break;
            case 2://关闭IMU
                Log.d("Tag", "关闭了IMU");
                break;
            default:
                Log.d("Tag", "open_imu指令错误");
                break;
        }
    }

    @JavascriptInterface
    public synchronized boolean state_imu() {
        boolean state = true;
        Log.d("Tag", "IMU开了");
        return state;
    }

    //传感器
    public synchronized boolean sensor_avoid(int which) {
        boolean state = true;
        switch (which) {
            case 1://前方传感器
                Log.d("Tag", "前方传感器是开的");
                break;
            case 2://后方传感器
                Log.d("Tag", "后方传感器是开的");
                break;
            case 3://左方传感器
                Log.d("Tag", "左方传感器是开的");
                break;
            case 4://右方传感器
                Log.d("Tag", "右方传感器是开的");
                break;
            default:
                Log.d("Tag", "sensor_avoid指令错误");
                break;
        }

        return state;
    }

    //声光
    @JavascriptInterface
    public synchronized void open_led(int state, int which) {
        switch (state) {
            case 1://开启LED
                Log.d("Tag", "打开了LED");
                break;
            case 2://关闭LED
                Log.d("Tag", "关闭了LED");
                break;
            default:
                Log.d("Tag", "open_led指令错误");
                break;
        }
        switch (which) {
            case 1://前方LED
                Log.d("Tag", "前方LED");
                break;
            case 2://后方LED
                Log.d("Tag", "后方LED");
                break;
            case 3://左方LED
                Log.d("Tag", "左方LED");
                break;
            case 4://右方LED
                Log.d("Tag", "右方LED");
                break;
            default:
                Log.d("Tag", "open_led指令错误");
                break;
        }
    }

    @JavascriptInterface
    public synchronized boolean state_led(int which) {
        boolean state = false;
        switch (which) {
            case 1://前方LED
                Log.d("Tag", "前方LED是关的");
                break;
            case 2://后方LED
                Log.d("Tag", "后方LED是关的");
                break;
            case 3://左方LED
                state = true;
                Log.d("Tag", "左方LED是开的");
                break;
            case 4://右方LED
                state = true;
                Log.d("Tag", "右方LED是开的");
                break;
            default:
                Log.d("Tag", "state_led指令错误");
                break;
        }
        return state;
    }

    //逻辑
    @JavascriptInterface
    public synchronized void control_wait(int time) {
        Log.d("Tag", "等待了"+time+"秒");
    }
}
