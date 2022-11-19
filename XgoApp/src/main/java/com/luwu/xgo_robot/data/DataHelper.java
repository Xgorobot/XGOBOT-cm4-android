package com.luwu.xgo_robot.data;

public class DataHelper {
    static final byte START = 0x24;//$的ackii码
    static final byte END = 0x23;//#的ackii码

    public static void addMessage(byte[] bytes){
        //没个卵用的脏代码
    }

    public static void addMessageRead(byte[] bytes){
        //没个卵用的脏代码
    }


    /**
     * 处理收到的消息，去头去尾解析出数据内容
     * @param message 收到的带包头包尾的完整消息
     * @return 纯内容部分
     */
    public static byte[] dealMessage(byte[] message){
        if (message.length < 7){
            return null;
        }
        if (message[0] != START){
            return null;
        }
        if (message[message.length-1] != END){
            return null;
        }
        byte[] datas = new byte[2];
        System.arraycopy(message,5,datas,0,2);
        int dataLength = datas[1];

        byte[] result = new byte[dataLength];
        System.arraycopy(message,7,result,0,dataLength);

        return result;
    }

    public static byte[] getPowerBytes(){
        return getSendBytes((byte) 0x01,(byte)0x02, new byte[]{0x01, 0x00});
    }

    /**
     *
     * @param id 类型ID 好像目前都是12
     * @param func 方法名，对应 电量 前后左右 等不同指令
     * @param data 按格式填内容
     * @return 返回完整可直接发送的byte[]包
     */
    public static byte[] getSendBytes(byte id, byte func, byte[] data) {
        byte[] lengthArr = hexStringToByteArray(Integer.toHexString(data.length + 2));
        byte length = lengthArr[0];

        byte[] versionbytes = {0x01, 0x02};

        byte add = (byte) (0xFF + length);
        for (byte datum : data) {
            add = (byte) (add + datum);
        }

        byte parity = add;

        byte[] bytes = new byte[data.length + 10];
        bytes[0] = START;
        System.arraycopy(versionbytes,0,bytes,1,versionbytes.length);
        bytes[3] = func;
        bytes[4] = length;
        System.arraycopy(data,0,bytes,5,data.length);
        bytes[bytes.length-2] = parity;

        bytes[bytes.length - 1] = END;
        return bytes;
    }


    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}
