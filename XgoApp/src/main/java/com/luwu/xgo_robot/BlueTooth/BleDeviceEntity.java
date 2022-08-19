package com.luwu.xgo_robot.BlueTooth;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

import com.luwu.xgo_robot.mActivity.MainActivity;

import java.nio.charset.StandardCharsets;


public class BleDeviceEntity implements Comparable<BleDeviceEntity> {

    private String name;
    private String mac;
    private int rssi;
    private BluetoothDevice device;

    public BleDeviceEntity() {
    }

    public BleDeviceEntity(String name, String mac, int rssi, BluetoothDevice device) {
        this.name = name;
        this.mac = mac;
        this.rssi = rssi;
        this.device = device;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    @Override
    public int compareTo(@NonNull BleDeviceEntity bleDeviceEntity) {
        int i = bleDeviceEntity.getRssi() - this.getRssi();//先按照年龄排序
        return i;
    }

    public static void changeBleName(String bleName)  {
        byte[] bleNameBytes = bleName.getBytes(StandardCharsets.US_ASCII);
        byte[] bleNameBytesAll = new byte[bleNameBytes.length + 1];
        bleNameBytesAll[0] = 0x13;  //指令类型
        System.arraycopy(bleNameBytes, 0, bleNameBytesAll, 1, bleNameBytesAll.length - 1);
        MainActivity.addMessage(bleNameBytesAll);
//        String lenth = UnicodeUtil.toHexString(String.valueOf(bleName.length()));
//        String nameHex = UnicodeUtil.stringToAscii(bleName);
//        String msg = "04 02 F3 00 00 " + lenth + nameHex;
//        AppContext.getmBleClient().addMsg(UnicodeUtil.hex2bytes(msg), msgRequest, timeout, timeoutResend, isLongByte)(msg, msgRequest, context);
    }
}
