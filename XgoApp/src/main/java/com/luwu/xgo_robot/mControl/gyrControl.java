package com.luwu.xgo_robot.mControl;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
//通过手机姿态调整机器人姿态 2.0弃用
public class gyrControl implements SensorEventListener {
    private final float accuracy = 1.5f;//灵敏度
    public float pitch, roll, yaw;//方位角模拟传感器
    private float pitchTemp, rollTemp, yawTemp;
    private float yawZero, yawBefore,yawAdd;
    private boolean firstFlag;
    //记录rotationMatrix矩阵值
    private float[] r = new float[9];
    //记录通过getOrientation()计算出来的方位横滚俯仰值
    private float[] values = new float[3];
    private float[] gravity = null;
    private float[] geomagnetic = null;
    public float accx, accy, accz;//重力传感器
    public float anglex, angley, anglez;//陀螺仪传感器
    public float magx, magy, magz;//磁场传感器

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor gyroscopeSensor;
    private Sensor magneticSensor;
    private static final float NS2S = 1.0f / 1000000000.0f;// 将纳秒转化为秒
    private float timestamp;
    private float angle[] = new float[3];

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (gravity != null && geomagnetic != null) {
                    if (SensorManager.getRotationMatrix(r, null, gravity, geomagnetic)) {
                        SensorManager.getOrientation(r, values);
                        if (firstFlag) {
                            firstFlag = false;
                            yawZero = (float) Math.toDegrees(values[0]);
                            yawBefore = yawZero;
                        } else {
                            if ((float) Math.toDegrees(values[0]) - yawBefore > 200) {//由180突变至-180
                            yawAdd-=360;
                            }else if(yawBefore - (float) Math.toDegrees(values[0]) > 200){//由-180突变至180
                                yawAdd+=360;
                            }
                            yawBefore = (float) Math.toDegrees(values[0]);
                            yawTemp=(float) Math.toDegrees(values[0])-yawZero+yawAdd;
                        }
                        pitchTemp = (float) Math.toDegrees(values[1]);
                        rollTemp = (float) Math.toDegrees(values[2]);
                        if(Math.abs(pitch-pitchTemp)>accuracy){
                            pitch=pitchTemp;
                        }
                        if(Math.abs(roll-rollTemp)>accuracy){
                            roll=rollTemp;
                        }
                        if(Math.abs(yaw-yawTemp)>accuracy){
                            yaw=yawTemp;
                        }
                    }
                }
            }
        }
    };

    public gyrControl(Context m) {
        firstFlag = true;
        sensorManager = (SensorManager) m.getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);

    }

    //坐标轴都是手机从左侧到右侧的水平方向为x轴正向，从手机下部到上部为y轴正向，垂直于手机屏幕向上为z轴正向
//传感器数值改变
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
// x,y,z分别存储坐标轴x,y,z上的加速度
            accx = event.values[0];
            accy = event.values[1];
            accz = event.values[2];
            gravity = event.values;
            handler.sendEmptyMessage(0);
// 根据三个方向上的加速度值得到总的加速度值a
//          float a = (float) Math.sqrt(x * x + y * y + z * z);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
// 三个坐标轴方向上的电磁强度，单位是微特拉斯(micro-Tesla)，用uT表示，也可以是高斯(Gauss),1Tesla=10000Gauss
            magx = event.values[0];
            magy = event.values[1];
            magz = event.values[2];
            geomagnetic = event.values;
            handler.sendEmptyMessage(0);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
//从 x、y、z 轴的正向位置观看处于原始方位的设备，如果设备逆时针旋转，将会收到正值；否则，为负值
            if (timestamp != 0) {
// 得到两次检测到手机旋转的时间差（纳秒），并将其转化为秒
                float dT = (event.timestamp - timestamp) * NS2S;
// 将手机在各个轴上的旋转角度相加，即可得到当前位置相对于初始位置的旋转弧度
                angle[0] += event.values[0] * dT;
                angle[1] += event.values[1] * dT;
                angle[2] += event.values[2] * dT;
// 将弧度转化为角度
                anglex = (float) Math.toDegrees(angle[0]);
                angley = (float) Math.toDegrees(angle[1]);
                anglez = (float) Math.toDegrees(angle[2]);
            }
            timestamp = event.timestamp;
        }
    }

    //传感器精度改变
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//TODO Auto-generated method stub
    }
}
