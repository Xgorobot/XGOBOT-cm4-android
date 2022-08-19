package com.luwu.xgo_robot.socket;

public interface SocketServerApi {
    void startUDPListener();
    void onUDPMsgGet(byte[] bytes);
    void connectTCP(String ip,int port);
    void onTCPStateChange(int state,String ip);
    void onTCPMsgGet(byte[] bytes);
    void sentTCPMsg(byte[] bytes);
}
