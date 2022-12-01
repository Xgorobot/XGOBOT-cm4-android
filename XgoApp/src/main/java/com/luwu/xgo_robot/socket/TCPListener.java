package com.luwu.xgo_robot.socket;

/**
 * 作者：Lin on 2019/10/24 16:53
 * 邮箱：yuanwenlin2014@foxmail.com
 */
public interface TCPListener {

    public final static byte STATUS_CONNECT_SUCCESS = 1;//连接成功

    public final static byte STATUS_CONNECT_CLOSED = 0;//关闭连接

    public final static byte STATUS_CONNECT_ERROR = 2;//连接失败


    /**
     * 当接收到系统消息
     */
    void onMessageResponse(Object msg);

    /**
     * 当连接状态发生变化时调用
     */
    public void onServiceStatusConnectChanged(int statusCode);

    public void onServiceStateMsgChanged(String msg);
}