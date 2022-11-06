package com.luwu.xgo_robot.data;

import com.luwu.xgo_robot.AppContext;

public class DataHelper {
    //供外界调用
    public static void MsgThreadStop() {
//        AppContext.getmBleClient().MsgThreadStop();
    }

    public static void MsgThreadWork() {
//        AppContext.getmBleClient().MsgThreadWork();
    }

    public static int getMsgListState() {
//        return AppContext.getmBleClient().getMsgListLength();
        return 0;
    }

    public static void sendHugeMessage(byte[] msg) {
//        AppContext.getmBleClient().sendHugeMessage(msg);
    }

    public static void addMessage(byte[] msg) {
//        AppContext.getmBleClient().addMessage(msg);
    }

    public static void addMessageRespond(byte[] msg) {
//        AppContext.getmBleClient().addMessageRespond(msg);
    }

    public static void addMessageRead(byte[] msg) {
//        System.out.println(Arrays.toString(msg));
//        AppContext.getmBleClient().addMessageRead(msg);
    }

    //消息会被覆盖 不靠谱 仅在TestBtActivity中使用
    public static byte[] getMessageRespond() {
        return new byte[0];
    }

    //消息会被覆盖 不靠谱 仅在仅在TestBtActivity中使用
    public static byte[] getMessageRead() {
        return new byte[0];
    }
}
