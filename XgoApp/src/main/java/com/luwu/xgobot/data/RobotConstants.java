package com.luwu.xgobot.data;

//保存机器人标签集
public class RobotConstants {
    public static final byte TYPE_DEFAULT = 0x01;//默认类型


    public static final byte GET_POWER = 0x02;//获取电量

    //视频控制
    public static final byte SET_SPKZ = 0x0F;//控制视频流

    //遥控界面
    public static final byte RECEIVE_YKJML = 0x10;//遥控界面 仅回执？
    public static final byte SET_YGKZ = 0x11;//摇杆控制
    public static final byte SET_AJKZ = 0x12;//按键控制
    public static final byte SET_BFKD = 0x13;//步伐宽度
    public static final byte SET_BFPL = 0x14;//步伐频率
    public static final byte SET_ZWDMS = 0x15;//自稳定模式

    public static final byte SET_BTTZ = 0x16;//步幅设置
    public static final byte SET_DJXZ = 0x17;//标定模式

    public static final byte SET_XZPY = 0x18;//x轴平移
    public static final byte SET_YZPY = 0x19;//y轴平移

    //身体姿态
    public static final byte RECEIVE_STZT = 0x20;//身体姿态
    public static final byte SET_KZSTZT = 0x21;//控制身体姿态
    public static final byte SET_KZGDZT = 0x22;//控制高低姿态
    public static final byte SET_KZJB = 0x23;//控制肩膀
    public static final byte SET_KZROLL = 0x21;//控制身体姿态
    public static final byte SET_KZPITCH = 0x24;//控制身体姿态
    public static final byte SET_KZYAW = 0x25;//控制身体姿态

    //动作
    public static final byte RECEIVE_DZ = 0x30;//身体姿态
    public static final byte SET_KZDZ = 0x31;//控制动作 0表示停止
    public static final byte SET_DZLB = 0x32;//动作轮播
    public static final byte SET_CSWZ = 0x33;//初始位置
    public static final byte SET_DQBBH = 0x34;//读取版本号

    //舵机控制
    public static final byte RECEIVE_DJKZ = 0x40;//身体姿态
    public static final byte SET_KZDJ = 0x41;//控制舵机


    //单腿控制
    public static final byte SET_KZDT = 0x51;//控制单腿


}
