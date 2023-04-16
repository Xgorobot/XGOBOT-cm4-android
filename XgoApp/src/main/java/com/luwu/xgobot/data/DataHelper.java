package com.luwu.xgobot.data;

import android.annotation.SuppressLint;

public class DataHelper {
    private static final String TAG = "DataHelper";
    private static final byte START = 0x24;//$的ackii码
    private static final byte END = 0x23;//#的ackii码

    @Deprecated
    public static void addMessage(byte[] bytes){
        //没个卵用的脏代码
    }

    @Deprecated
    public static void addMessageRead(byte[] bytes){
        //没个卵用的脏代码
    }

    /**
     *
     * @param id 类型ID 好像只有 TYPE_DEFAULT
     * @param func 方法名，对应 电量 前后左右 等不同指令
     * @param data 按格式填内容
     * @return 返回完整可直接发送的byte[]包
     */
    public static byte[] getSendBytes(byte id, byte func, byte[] data) {
        //此解析流程 大体是 byte 转 HEXString 再取byte 算 校验位 再加上包头和包尾
        //不是我定的，微笑脸
        byte length = (byte) (data.length * 2 + 2);

        byte[] bytes = new byte[data.length + 4];//除校验位和包头包尾以外的 byte组
        bytes[0] = id;
        bytes[1] = func;
        bytes[2] = length;
        System.arraycopy(data,0,bytes,3,data.length);

        byte add = 0x00;
        for (byte datum : bytes) {
            add = (byte) (add + datum);
        }
        bytes[bytes.length-1] = add;

        String resultString = bytesToHex(bytes);
        byte[] askiiBytes = resultString.getBytes();//转hexString后的askii数组

        byte[] resultByte = new byte[askiiBytes.length + 2];
        resultByte[0] = START;
        System.arraycopy(askiiBytes,0,resultByte,1,askiiBytes.length);
        resultByte[resultByte.length-1] = END;

        return resultByte;
    }

    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer sb = new StringBuffer();
                sb.append("0").append(str);// 左补0
                // sb.append(str).append("0");//右补0
                str = sb.toString();
                strLen = str.length();
            }
        }
        return str;
    }
}
