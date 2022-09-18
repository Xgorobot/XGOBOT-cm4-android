package com.luwu.xgo_robot.data;

public class DataManager implements DataApi{

    private static DataApi instance;

    private DataManager() {}

    public static DataApi getInstance() {
        if (instance == null){
            synchronized (DataManager.class){
                if (instance == null){
                    instance = new DataManager();
                }
            }
        }
        return instance;
    }

    @Override
    public void sendData(byte[] datas) {

    }

    @Override
    public void MsgThreadStop() {

    }

    @Override
    public void MsgThreadWork() {

    }

    @Override
    public int getMsgListState() {
        return 0;
    }

    @Override
    public void sendHugeMessage(byte[] msg) {

    }

    @Override
    public void addMessage(byte[] msg) {

    }

    @Override
    public void addMessageRespond(byte[] msg) {

    }

    @Override
    public void addMessageRead(byte[] msg) {

    }

    @Override
    public byte[] getMessageRespond() {
        return new byte[0];
    }

    @Override
    public byte[] getMessageRead() {
        return new byte[0];
    }
}
