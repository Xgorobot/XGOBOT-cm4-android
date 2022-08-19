package com.luwu.xgo_robot.BlueTooth;

public class BleMessageEntity {
    private BleMessageEntity() {
    }

//    public BleMessageEntity(byte[] bytes, BleInterface.MessageCallback msgRequestInterface) {
//        this.bytes = bytes;
//        this.msgRequestInterface = msgRequestInterface;
//    }


    public BleMessageEntity(byte[] bytes, BleInterface.MessageCallback msgRequestInterface, long timeout, boolean timeoutReSend, int keyCode) {
        this.bytes = bytes;
        this.msgRequestInterface = msgRequestInterface;
        this.timeout = timeout;
        this.timeoutReSend = timeoutReSend;
        this.keyCode = keyCode;
    }

    private int keyCode;

    byte[] bytes;

    boolean timeoutReSend = true;//回复数据超时后是否重发

    long timeout = 250;//超时时长

    BleInterface.MessageCallback msgRequestInterface;//回调接口

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public BleInterface.MessageCallback getMsgRequestInterface() {
        return msgRequestInterface;
    }

    public void setMsgRequestInterface(BleInterface.MessageCallback msgRequestInterface) {
        this.msgRequestInterface = msgRequestInterface;
    }

    public boolean isTimeoutReSend() {
        return timeoutReSend;
    }

    public void setTimeoutReSend(boolean timeoutReSend) {
        this.timeoutReSend = timeoutReSend;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getKeyCode() {
        return keyCode;
    }
}
