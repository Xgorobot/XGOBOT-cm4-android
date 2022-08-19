package com.luwu.xgo_robot.Product;

public abstract class Product {
    // X Y Z 平移范围
    private int TRANSLATION_LIMIT_X;
    private int TRANSLATION_LIMIT_Y;
    private int TRANSLATION_LIMIT_Z_MIN;
    private int TRANSLATION_LIMIT_Z_MAX;

    // Roll Pitch Yaw 姿态范围
    private int ATTITUDE_LIMIT_ROLL;
    private int ATTITUDE_LIMIT_PITCH;
    private int ATTITUDE_LIMIT_YAW;

    // 腿长范围
    private int LEG_LIMIT_X;
    private int LEG_LIMIT_Y;
    private int LEG_LIMIT_Z_MIN;
    private int LEG_LIMIT_Z_MAX;
    private int LEG_LIMIT_Z_DEFAULT;

    // 下 中 上 舵机范围
    private int MOTOR_LIMIT_LOWER_MIN;
    private int MOTOR_LIMIT_LOWER_MAX;
    private int MOTOR_LIMIT_MIDDLE_MIN;
    private int MOTOR_LIMIT_MIDDLE_MAX;
    private int MOTOR_LIMIT_UPPER_MIN;
    private int MOTOR_LIMIT_UPPER_MAX;

    // 原地踏步高度范围
    private int MARK_TIME_LIMIT_MIN;
    private int MARK_TIME_LIMIT_MAX;

    // X速度范围
    private int VX_LIMIT;
    // Y速度范围
    private int VY_LIMIT;
    // 旋转速度范围
    private int VYAW_LIMIT;

    public Product() {
    }

    public Product(int TRANSLATION_LIMIT_X, int TRANSLATION_LIMIT_Y, int TRANSLATION_LIMIT_Z_MIN, int TRANSLATION_LIMIT_Z_MAX, int ATTITUDE_LIMIT_ROLL, int ATTITUDE_LIMIT_PITCH, int ATTITUDE_LIMIT_YAW, int LEG_LIMIT_X, int LEG_LIMIT_Y, int LEG_LIMIT_Z_MIN, int LEG_LIMIT_Z_MAX, int LEG_LIMIT_Z_DEFAULT, int MOTOR_LIMIT_LOWER_MIN, int MOTOR_LIMIT_LOWER_MAX, int MOTOR_LIMIT_MIDDLE_MIN, int MOTOR_LIMIT_MIDDLE_MAX, int MOTOR_LIMIT_UPPER_MIN, int MOTOR_LIMIT_UPPER_MAX, int MARK_TIME_LIMIT_MIN, int MARK_TIME_LIMIT_MAX, int VX_LIMIT, int VY_LIMIT, int VYAW_LIMIT) {
        this.TRANSLATION_LIMIT_X = TRANSLATION_LIMIT_X;
        this.TRANSLATION_LIMIT_Y = TRANSLATION_LIMIT_Y;
        this.TRANSLATION_LIMIT_Z_MIN = TRANSLATION_LIMIT_Z_MIN;
        this.TRANSLATION_LIMIT_Z_MAX = TRANSLATION_LIMIT_Z_MAX;
        this.ATTITUDE_LIMIT_ROLL = ATTITUDE_LIMIT_ROLL;
        this.ATTITUDE_LIMIT_PITCH = ATTITUDE_LIMIT_PITCH;
        this.ATTITUDE_LIMIT_YAW = ATTITUDE_LIMIT_YAW;
        this.LEG_LIMIT_X = LEG_LIMIT_X;
        this.LEG_LIMIT_Y = LEG_LIMIT_Y;
        this.LEG_LIMIT_Z_MIN = LEG_LIMIT_Z_MIN;
        this.LEG_LIMIT_Z_MAX = LEG_LIMIT_Z_MAX;
        this.LEG_LIMIT_Z_DEFAULT = LEG_LIMIT_Z_DEFAULT;
        this.MOTOR_LIMIT_LOWER_MIN = MOTOR_LIMIT_LOWER_MIN;
        this.MOTOR_LIMIT_LOWER_MAX = MOTOR_LIMIT_LOWER_MAX;
        this.MOTOR_LIMIT_MIDDLE_MIN = MOTOR_LIMIT_MIDDLE_MIN;
        this.MOTOR_LIMIT_MIDDLE_MAX = MOTOR_LIMIT_MIDDLE_MAX;
        this.MOTOR_LIMIT_UPPER_MIN = MOTOR_LIMIT_UPPER_MIN;
        this.MOTOR_LIMIT_UPPER_MAX = MOTOR_LIMIT_UPPER_MAX;
        this.MARK_TIME_LIMIT_MIN = MARK_TIME_LIMIT_MIN;
        this.MARK_TIME_LIMIT_MAX = MARK_TIME_LIMIT_MAX;
        this.VX_LIMIT = VX_LIMIT;
        this.VY_LIMIT = VY_LIMIT;
        this.VYAW_LIMIT = VYAW_LIMIT;
    }



    public void setTRANSLATION_LIMIT_X(int TRANSLATION_LIMIT_X) {
        this.TRANSLATION_LIMIT_X = TRANSLATION_LIMIT_X;
    }

    public void setTRANSLATION_LIMIT_Y(int TRANSLATION_LIMIT_Y) {
        this.TRANSLATION_LIMIT_Y = TRANSLATION_LIMIT_Y;
    }

    public void setTRANSLATION_LIMIT_Z_MIN(int TRANSLATION_LIMIT_Z_MIN) {
        this.TRANSLATION_LIMIT_Z_MIN = TRANSLATION_LIMIT_Z_MIN;
    }

    public void setTRANSLATION_LIMIT_Z_MAX(int TRANSLATION_LIMIT_Z_MAX) {
        this.TRANSLATION_LIMIT_Z_MAX = TRANSLATION_LIMIT_Z_MAX;
    }

    public void setATTITUDE_LIMIT_ROLL(int ATTITUDE_LIMIT_ROLL) {
        this.ATTITUDE_LIMIT_ROLL = ATTITUDE_LIMIT_ROLL;
    }

    public void setATTITUDE_LIMIT_PITCH(int ATTITUDE_LIMIT_PITCH) {
        this.ATTITUDE_LIMIT_PITCH = ATTITUDE_LIMIT_PITCH;
    }

    public void setATTITUDE_LIMIT_YAW(int ATTITUDE_LIMIT_YAW) {
        this.ATTITUDE_LIMIT_YAW = ATTITUDE_LIMIT_YAW;
    }

    public void setLEG_LIMIT_X(int LEG_LIMIT_X) {
        this.LEG_LIMIT_X = LEG_LIMIT_X;
    }

    public void setLEG_LIMIT_Y(int LEG_LIMIT_Y) {
        this.LEG_LIMIT_Y = LEG_LIMIT_Y;
    }

    public void setLEG_LIMIT_Z_MIN(int LEG_LIMIT_Z_MIN) {
        this.LEG_LIMIT_Z_MIN = LEG_LIMIT_Z_MIN;
    }

    public void setLEG_LIMIT_Z_MAX(int LEG_LIMIT_Z_MAX) {
        this.LEG_LIMIT_Z_MAX = LEG_LIMIT_Z_MAX;
    }

    public int getLEG_LIMIT_Z_DEFAULT() {
        return LEG_LIMIT_Z_DEFAULT;
    }

    public void setLEG_LIMIT_Z_DEFAULT(int LEG_LIMIT_Z_DEFAULT) {
        this.LEG_LIMIT_Z_DEFAULT = LEG_LIMIT_Z_DEFAULT;
    }

    public void setMOTOR_LIMIT_LOWER_MIN(int MOTOR_LIMIT_LOWER_MIN) {
        this.MOTOR_LIMIT_LOWER_MIN = MOTOR_LIMIT_LOWER_MIN;
    }

    public void setMOTOR_LIMIT_LOWER_MAX(int MOTOR_LIMIT_LOWER_MAX) {
        this.MOTOR_LIMIT_LOWER_MAX = MOTOR_LIMIT_LOWER_MAX;
    }

    public void setMOTOR_LIMIT_MIDDLE_MIN(int MOTOR_LIMIT_MIDDLE_MIN) {
        this.MOTOR_LIMIT_MIDDLE_MIN = MOTOR_LIMIT_MIDDLE_MIN;
    }

    public void setMOTOR_LIMIT_MIDDLE_MAX(int MOTOR_LIMIT_MIDDLE_MAX) {
        this.MOTOR_LIMIT_MIDDLE_MAX = MOTOR_LIMIT_MIDDLE_MAX;
    }

    public void setMOTOR_LIMIT_UPPER_MIN(int MOTOR_LIMIT_UPPER_MIN) {
        this.MOTOR_LIMIT_UPPER_MIN = MOTOR_LIMIT_UPPER_MIN;
    }

    public void setMOTOR_LIMIT_UPPER_MAX(int MOTOR_LIMIT_UPPER_MAX) {
        this.MOTOR_LIMIT_UPPER_MAX = MOTOR_LIMIT_UPPER_MAX;
    }

    public void setMARK_TIME_LIMIT_MIN(int MARK_TIME_LIMIT_MIN) {
        this.MARK_TIME_LIMIT_MIN = MARK_TIME_LIMIT_MIN;
    }

    public void setMARK_TIME_LIMIT_MAX(int MARK_TIME_LIMIT_MAX) {
        this.MARK_TIME_LIMIT_MAX = MARK_TIME_LIMIT_MAX;
    }

    public void setVX_LIMIT(int VX_LIMIT) {
        this.VX_LIMIT = VX_LIMIT;
    }

    public void setVY_LIMIT(int VY_LIMIT) {
        this.VY_LIMIT = VY_LIMIT;
    }

    public void setVYAW_LIMIT(int VYAW_LIMIT) {
        this.VYAW_LIMIT = VYAW_LIMIT;
    }

    public int getTRANSLATION_LIMIT_X() {
        return TRANSLATION_LIMIT_X;
    }

    public int getTRANSLATION_LIMIT_Y() {
        return TRANSLATION_LIMIT_Y;
    }

    public int getTRANSLATION_LIMIT_Z_MIN() {
        return TRANSLATION_LIMIT_Z_MIN;
    }

    public int getTRANSLATION_LIMIT_Z_MAX() {
        return TRANSLATION_LIMIT_Z_MAX;
    }

    public int getATTITUDE_LIMIT_ROLL() {
        return ATTITUDE_LIMIT_ROLL;
    }

    public int getATTITUDE_LIMIT_PITCH() {
        return ATTITUDE_LIMIT_PITCH;
    }

    public int getATTITUDE_LIMIT_YAW() {
        return ATTITUDE_LIMIT_YAW;
    }

    public int getLEG_LIMIT_X() {
        return LEG_LIMIT_X;
    }

    public int getLEG_LIMIT_Y() {
        return LEG_LIMIT_Y;
    }

    public int getLEG_LIMIT_Z_MIN() {
        return LEG_LIMIT_Z_MIN;
    }

    public int getLEG_LIMIT_Z_MAX() {
        return LEG_LIMIT_Z_MAX;
    }

    public int getMOTOR_LIMIT_LOWER_MIN() {
        return MOTOR_LIMIT_LOWER_MIN;
    }

    public int getMOTOR_LIMIT_LOWER_MAX() {
        return MOTOR_LIMIT_LOWER_MAX;
    }

    public int getMOTOR_LIMIT_MIDDLE_MIN() {
        return MOTOR_LIMIT_MIDDLE_MIN;
    }

    public int getMOTOR_LIMIT_MIDDLE_MAX() {
        return MOTOR_LIMIT_MIDDLE_MAX;
    }

    public int getMOTOR_LIMIT_UPPER_MIN() {
        return MOTOR_LIMIT_UPPER_MIN;
    }

    public int getMOTOR_LIMIT_UPPER_MAX() {
        return MOTOR_LIMIT_UPPER_MAX;
    }

    public int getMARK_TIME_LIMIT_MIN() {
        return MARK_TIME_LIMIT_MIN;
    }

    public int getMARK_TIME_LIMIT_MAX() {
        return MARK_TIME_LIMIT_MAX;
    }

    public int getVX_LIMIT() {
        return VX_LIMIT;
    }

    public int getVY_LIMIT() {
        return VY_LIMIT;
    }

    public int getVYAW_LIMIT() {
        return VYAW_LIMIT;
    }
}
