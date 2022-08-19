package com.luwu.xgo_robot.BlueTooth;

public class BleInterface {
    public interface MessageCallback {
        void onMessageReceive(String data);

        void onMessageFailed(String msg);
    }
}
