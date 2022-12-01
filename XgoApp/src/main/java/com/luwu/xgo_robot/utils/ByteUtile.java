package com.luwu.xgo_robot.utils;

public class ByteUtile {
    public static String byteArrayToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (int index = 0, len = bytes.length; index <= len - 1; index += 1) {
            int char1 = ((bytes[index] >> 4) & 0xF);
            char chara1 = Character.forDigit(char1, 16);
            int char2 = ((bytes[index]) & 0xF);
            char chara2 = Character.forDigit(char2, 16);
            result.append(chara1);
            result.append(chara2);
        }
        return result.toString();
    }

    public static byte[] resetBS(byte[] bytes){
        byte[] result = new byte[bytes.length];
        for (int i = 0;i<bytes.length;i++){
            result[result.length-1-i] = bytes[i];
        }
        return result;
    }
}
