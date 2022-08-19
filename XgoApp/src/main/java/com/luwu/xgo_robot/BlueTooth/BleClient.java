package com.luwu.xgo_robot.BlueTooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.luwu.xgo_robot.mMothed.PublicMethod;
import com.luwu.xgo_robot.mMothed.mToast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.luwu.xgo_robot.BlueTooth.BluetoothLeService.MTU_SIZE;
import static com.luwu.xgo_robot.mMothed.PublicMethod.isBluetoothConnect;

public class BleClient {
    //通讯协议
    private final static byte[] WORD_BEGIN = {0x55, 0x00};
    private final static byte ORDER_WRITE = 0x00;
    private final static byte ORDER_WRITE_RESPOND = 0x01;
    private final static byte WRITE_RESPOND = 0x11;
    private final static byte ORDER_READ = 0x02;
    private final static byte READ_READBACK = 0x12;
    private final static byte[] WORD_END = {0x00, (byte) 0xAA};
    private List<byte[]> msgList = new ArrayList<byte[]>();//储存要发送出去的命令
    private boolean runFlag = true;//线程循环指示变量
    private MessageThread messageThread;//发送信息的线程


    private final static String TAG = BleClient.class.getSimpleName();
    private String bleName = "";
    private String bleNameConnected = "";
    //需要发送的消息列表
    private ArrayList<BleMessageEntity> msgArrayList = new ArrayList<>();

    //只用于超时操作
    private Handler mHandler = new Handler();
    /**
     * 当前连接设备 连接状态
     */
    private BluetoothDevice mBluetoothDevice;
    private boolean connected = false;


    /**
     * 我们通信所需要的蓝牙gattCharacter
     */
    private BluetoothGattCharacteristic mSendCharacter, mReadCharacter;

    private static Context mContext;
    //蓝牙服务
    private BluetoothLeService mBluetoothLeService;

    private BleClient() {
    }

    public BleClient(Context context) {
        mContext = context;
        init(context);
        messageThread = new MessageThread();//蓝牙无误则启动发送线程
        messageThread.start();
    }

    private ConnectionRequest mConnectRequest;

    public String getBleName() {
        return bleName;
    }

    public void setBleName(String bleName) {
        this.bleName = bleName;
    }

    public String getBleNameConnected() {
        return bleNameConnected;
    }

    public void setBleNameConnected(String bleNameConnected) {
        this.bleNameConnected = bleNameConnected;
    }
    /**
     * 初始化
     * 注册mGattUpdateReceiver
     *
     * @param context 上下文
     */
    private void init(Context context) {
        mContext = context;
        mContext.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());//注册广播
        Intent gattServiceIntent = new Intent(mContext, BluetoothLeService.class);
        mContext.bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);//绑定service
    }


    public void setmConnectRequest(ConnectionRequest mConnectRequest) {
        this.mConnectRequest = mConnectRequest;
    }

    /**
     * 使用本类结束时记得调用
     * 广播需要
     */
    public void onDestroy() {
        mContext.unregisterReceiver(mGattUpdateReceiver);
        runFlag = false;//停止发送线程
    }

    public void MsgThreadStop(){
        runFlag = false;//停止发送线程
    }

    public void MsgThreadWork(){
        runFlag = true;//继续发送线程
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d(TAG, "蓝牙服务已经启用");

            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                connected = true;
                isBluetoothConnect=true;
                mBluetoothDevice = mBluetoothLeService.getMsgBluetoothGatt().getDevice();
                Log.d(TAG, "蓝牙连接基础已建立");
                //已连接
                if (mConnectRequest != null) {
                    mConnectRequest.connectSuccess();
                }
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                connected = false;
                isBluetoothConnect=false;
                mBluetoothDevice = null;
                Log.d(TAG, "收到蓝牙断开的广播");
                switch(PublicMethod.localeLanguage){
                    case "zh":
                        mToast.show(mContext,"蓝牙已断开");
                        break;
                    default:
                        mToast.show(mContext,"bluetooth disconnected");
                }

                if (mConnectRequest != null) {
                    mConnectRequest.connectFailed();
                }
                //已断开
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.d(TAG, "发现service");
                //发现service
                mHandler.postDelayed(initBleService, 1000);
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //收到消息
                byte[] bytes = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                read(bytes);//处理收到的数据
            }
        }
    };


    public boolean connect(String address) {
//        address = "20:C3:8F:E3:38:5D";
        return mBluetoothLeService.connect(address);
    }

    public void disConnect() {
        mBluetoothLeService.disconnect();
    }

    public void closeGatt() {
        mBluetoothLeService.close();
    }

    private void write(byte[] bytes) {
        if (mBluetoothLeService != null) {
            mBluetoothLeService.write(bytes, mSendCharacter);
        } else {
            Log.d(TAG, "蓝牙服务为空");
        }
    }

    private void read(byte[] msg) {
        readMessage(msg);
    }


    /**
     * 获取所需要的广播列表
     *
     * @return 广播列表
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_WRITE_RESULT);
        return intentFilter;
    }

    Runnable initBleService = new Runnable() {
        @Override
        public void run() {
            initBleService();
        }
    };

    void initBleService() {
        //初始化发送和接受Character
        Log.d(TAG, "开始查找服务");
        BluetoothGatt mGatt = mBluetoothLeService.getMsgBluetoothGatt();
        BluetoothGattService myServices = mGatt.getService(UUID.fromString(SampleGattAttributes.MESSAGE_GATT));

        if (myServices != null) {
            Log.d(TAG, "开始查找服务1");
            mSendCharacter = myServices.getCharacteristic(UUID.fromString(SampleGattAttributes.MESSAGE_SEND));
            mReadCharacter = myServices.getCharacteristic(UUID.fromString(SampleGattAttributes.MESSAGE_READ));
            if (mSendCharacter != null && mReadCharacter != null) {
                Log.d(TAG, "开始查找服务2");
                int proper = mReadCharacter.getProperties();
                if ((0 != (proper & BluetoothGattCharacteristic.PROPERTY_NOTIFY))
                        || (0 != (proper & BluetoothGattCharacteristic.PROPERTY_INDICATE))) { // 通知
                    Log.d(TAG, "开始查找服务3");
                    mBluetoothLeService.getMsgBluetoothGatt().setCharacteristicNotification(
                            mReadCharacter, true);
                    BluetoothGattDescriptor descriptor = mReadCharacter.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothLeService.getMsgBluetoothGatt().writeDescriptor(descriptor);
                    if (mConnectRequest != null)
                        mConnectRequest.connectSuccess();
                } else {
                    Log.d(TAG, "开始查找服务4");
                    Log.d(TAG, "注册通知失败");
                    disConnect();

                    if (mConnectRequest != null) {
                        mConnectRequest.connectFailed();
                    }
                }
            } else {
                //找不到需要的服务 连接失败，需要断开重连
                Log.d(TAG, "读写服务未找到");
                disConnect();

                if (mConnectRequest != null) {
                    mConnectRequest.connectFailed();
                }
            }
        } else {
            Log.d(TAG, "Service未找到");
            if (mConnectRequest != null) {
                mConnectRequest.connectFailed();
            }
        }
    }

    //是否连接
    public boolean isConnected() {
        return connected;
    }


    public interface ConnectionRequest {
        void connectSuccess();

        void connectFailed();
    }

    /*
     *封装一些外部调用的方法
     */
    public String getBtName() {
        return mBluetoothDevice.getName();
    }

    public String getBtAddress() {
        return mBluetoothDevice.getAddress();
    }

    //查看消息队列的状态
    public int getMsgListLength(){
        if (isBluetoothConnect && (mBluetoothDevice != null)) {
            return msgList.size();
        } else {
            return -1;  //表示未连接
        }
    }

    //分段发送
    public void sendHugeMessage(byte[] msg){
        if (isBluetoothConnect && (mBluetoothDevice != null)) {
            int mtu = MTU_SIZE - 3;
            int i = 0, j = 0;
            byte[] temp;
            for (; i < msg.length/mtu; i++){
                temp = new byte[mtu];
                System.arraycopy(msg, j, temp, 0, mtu);
                write(temp);
                j += mtu;
                try {
                    System.out.println(temp);
                    Thread.sleep(9);//最高10ms发送一次
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (j < msg.length) {
                temp = new byte[msg.length - j];
                System.arraycopy(msg, j, temp, 0, msg.length -j);
                write(temp);
                try {
                    System.out.println(temp);
                    Thread.sleep(9);//最高10ms发送一次
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //发送数据指令 无应答
    public void addMessage(byte[] msg) {//msg含首地址及数据
        long nowTime = System.currentTimeMillis();
//        if ((nowTime - saveTime) < 100) {//改为用队列储存命令间隔发送
//            return;
//        }
        if (isBluetoothConnect && (mBluetoothDevice != null)) {
            byte length = (byte) (msg.length + 7);
            byte order = ORDER_WRITE;
            byte[] message = msg;
            int tempLength = ((int) length) < 0 ? ((int) length) + 256 : ((int) length);//处理byte的有符号
            int tempSum = tempLength + (int) order;
            for (byte a : message) {
                tempSum = tempSum + (((int) a) < 0 ? ((int) a) + 256 : ((int) a));//处理byte的有符号
            }
            if (tempSum > 255) {
                tempSum = tempSum % 256;
            }
            byte checksum = (byte) (255 - tempSum);//取反
            byte[] wholemsg = new byte[((int) length) < 0 ? ((int) length) + 256 : (int) length];
            try {
                wholemsg[0] = WORD_BEGIN[0];
                wholemsg[1] = WORD_BEGIN[1];
                wholemsg[2] = length;
                wholemsg[3] = order;
                for (int i = 0; i < message.length; i++) {
                    wholemsg[4 + i] = message[i];
                }
                wholemsg[length - 3] = checksum;
                wholemsg[length - 2] = WORD_END[0];
                wholemsg[length - 1] = WORD_END[1];
                sendMessage(wholemsg);
            } catch (Exception e) {
                Log.d("Tag", "addMessage: 数组越界");
            }
        }
    }

    //发送数据指令 有应答
    public void addMessageRespond(byte[] msg) {
        long nowTime = System.currentTimeMillis();
//        if ((nowTime - saveTime) < 100) {//改为用队列储存命令间隔发送
//            return;
//        }
        if (isBluetoothConnect && (mBluetoothDevice != null)) {
            byte length = (byte) (msg.length + 7);
            byte order = ORDER_WRITE_RESPOND;
            byte[] message = msg;
            int tempLength = ((int) length) < 0 ? ((int) length) + 256 : ((int) length);//处理byte的有符号
            int tempSum = tempLength + (int) order;
            for (byte a : message) {
                tempSum = tempSum + (((int) a) < 0 ? ((int) a) + 256 : ((int) a));//处理byte的有符号
            }
            if (tempSum > 255) {
                tempSum = tempSum % 256;
            }
            byte checksum = (byte) (255 - tempSum);//取反
            byte[] wholemsg = new byte[((int) length) < 0 ? ((int) length) + 256 : (int) length];
            try {
                wholemsg[0] = WORD_BEGIN[0];
                wholemsg[1] = WORD_BEGIN[1];
                wholemsg[2] = length;
                wholemsg[3] = order;
                for (int i = 0; i < message.length; i++) {
                    wholemsg[4 + i] = message[i];
                }
                wholemsg[length - 3] = checksum;
                wholemsg[length - 2] = WORD_END[0];
                wholemsg[length - 1] = WORD_END[1];
                sendMessage(wholemsg);
            } catch (Exception e) {
                Log.d("Tag", "addMessage: 数组越界");
            }
        }
    }

    //读取数据指令
    public void addMessageRead(byte[] msg) {
        long nowTime = System.currentTimeMillis();
//        if ((nowTime - saveTime) < 100) {//改为用队列储存命令间隔发送
//            return;
//        }
        if (isBluetoothConnect && (mBluetoothDevice != null)) {
            byte length = (byte) (msg.length + 7);
            byte order = ORDER_READ;
            byte[] message = msg;
            int tempLength = ((int) length) < 0 ? ((int) length) + 256 : ((int) length);//处理byte的有符号
            int tempSum = tempLength + (int) order;
            for (byte a : message) {
                tempSum = tempSum + (((int) a) < 0 ? ((int) a) + 256 : ((int) a));//处理byte的有符号
            }
            if (tempSum > 255) {
                tempSum = tempSum % 256;
            }
            byte checksum = (byte) (255 - tempSum);//取反
            byte[] wholemsg = new byte[((int) length) < 0 ? ((int) length) + 256 : (int) length];
            try {
                wholemsg[0] = WORD_BEGIN[0];
                wholemsg[1] = WORD_BEGIN[1];
                wholemsg[2] = length;
                wholemsg[3] = order;
                for (int i = 0; i < message.length; i++) {
                    wholemsg[4 + i] = message[i];
                }
                wholemsg[length - 3] = checksum;
                wholemsg[length - 2] = WORD_END[0];
                wholemsg[length - 1] = WORD_END[1];
                sendMessage(wholemsg);
            } catch (Exception e) {
                Log.d("Tag", "addMessage: 数组越界");
            }
        }
    }

    private void sendMessage(byte[] wholeMsg) {
        if (isBluetoothConnect && (mBluetoothDevice != null)) {
            msgList.add(wholeMsg);//将消息添加到最后一个
        }
    }

    //定义发送线程 一直循环
    private class MessageThread extends Thread {
        public MessageThread() {

        }

        @Override
        public void run() {
            while (runFlag) {
                if (msgList.size() > 0) {
                    if (isBluetoothConnect && (mBluetoothDevice != null)) {
                        try {
                            write(msgList.get(0));
                        }catch (Exception e){
                            Log.d(TAG,"Error with sendMessage");
                        }
                        msgList.remove(0);//发送之后移除该对象
                    }
                }
                try {
                    Thread.sleep(10);//最高10ms发送一次
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /*
     *TIPS:Socket通讯需要自己处理数据不全和粘包的问题
     */
    private List<Byte> readList = new ArrayList<Byte>() {
    };
    private static int flag = 0;
    private static int length = 0;

    synchronized private void readMessage(byte[] msg) {//用这个函数处理数据 同一时间只能调用一次
        for (byte a : msg) {
            switch (flag) {
                case 0://代表刚开始接收
                    if (a == 0x55) {//第一个字头
                        readList.clear();
                        readList.add(a);
                        flag = 1;
                    } else {
                        flag = 0;
                    }
                    break;
                case 1:
                    if (a == 0x00) {//第二个字头
                        readList.add(a);
                        flag = 2;
                    } else {
                        flag = 0;
                    }
                    break;
                case 2://长度
                    length = (((int) a) < 0 ? ((int) a) + 256 : ((int) a));
                    if (length < 5) {
                        flag = 0;
                        readList.clear();
                        return;
                    }
                    readList.add(a);
                    flag = 3;
                    break;
                case 3:
                    length = length - 1;
                    readList.add(a);
                    if (length == 4) {
                        if (a != 0x00) {
                            flag = 0;
                            readList.clear();
                            return;
                        }
                    }
                    if (length == 3) {
                        if (a != -86) {//0xAA
                            flag = 0;
                            readList.clear();
                            return;
                        } else {
                            dealMessage(readList);//处理接收到的数据
                            flag = 0;//接收完毕
                        }
                    }
                    if (length < 3) {
                        flag = 0;
                        readList.clear();
                        return;
                    }
                    break;
                default:
                    flag = 0;
                    readList.clear();
                    break;
            }
        }
    }

    //写入指令应答信息
    private byte[] messageRespond = new byte[]{};

    public byte[] getMessageRespond() {
        return messageRespond;
    }

    //读取数据返回数据包
    private byte[] messageRead = new byte[]{};

    public byte[] getMessageRead() {
        return messageRead;
    }

    //处理收到的数据
    private void dealMessage(List<Byte> wholeMsg) {
        if ((wholeMsg.get(0) == 0x55) && (wholeMsg.get(1) == 0x00) && (wholeMsg.get(wholeMsg.size() - 2) == 0x00) && (wholeMsg.get(wholeMsg.size() - 1) == -86)) {//字头字尾正确 -86代表0xAA
            int length = (((int) wholeMsg.get(2)) < 0 ? ((int) wholeMsg.get(2)) + 256 : ((int) wholeMsg.get(2)));
            byte order = wholeMsg.get(3);
            byte[] msg = new byte[wholeMsg.size() - 7];
            for (int i = 0; i < msg.length; i++) {
                msg[i] = wholeMsg.get(4 + i);
            }
            byte checksum = wholeMsg.get(wholeMsg.size() - 3);
            int tempSum = length + (int) order;
            for (byte a : msg) {
                tempSum = tempSum + (((int) a) < 0 ? ((int) a) + 256 : ((int) a));//处理byte的有符号
            }
            if (tempSum > 255) {
                tempSum = tempSum % 256;
            }
            if (checksum == (byte) (255 - tempSum)) {//校验和正确
                if (order == WRITE_RESPOND) {//写入指令应答包
                    messageRespond = msg;
                    autoDealMsgRespond(msg);
                } else if (order == READ_READBACK) {//读取指令返回数据包
                    messageRead = msg;
                    autoDealMsgRead(msg);
                }
            }
        }
    }

    //进入接收中断后会自动调用函数
    private void autoDealMsgRespond(byte[] msg) {

    }

    //自动处理读取到的数值并存储在变量中
    private void autoDealMsgRead(byte[] msg) {
        if ((msg != null) && (msg.length > 1)) {
            byte addr = msg[0];
            int i_msg = 1;
            while (i_msg < msg.length) {
                switch (addr) {
                    case PublicMethod.XGORAM_ADDR.workstate:
                        PublicMethod.XGORAM_VALUE.workstate = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.battery:
                        PublicMethod.XGORAM_VALUE.battery = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.versions:
                        PublicMethod.XGORAM_VALUE.versions = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.connectBt:
                        PublicMethod.XGORAM_VALUE.connectBt = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.baudrateBt:
                        PublicMethod.XGORAM_VALUE.baudrateBt = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.passwordBt:
                        PublicMethod.XGORAM_VALUE.passwordBt = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.nameBt:
                        PublicMethod.XGORAM_VALUE.nameBt = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.uninstallMotor:
                        PublicMethod.XGORAM_VALUE.uninstallMotor = PublicMethod.byteToInt(msg[i_msg]);
                        break;
//                    case XGORAM_ADDR.resetMotorZero:
//                        XGORAM_VALUE.resetMotorZero=PublicMethod.byteToInt(msg[i_msg]);
//                        break;
                    case PublicMethod.XGORAM_ADDR.speedVx:
                        PublicMethod.XGORAM_VALUE.speedVx = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.speedVy:
                        PublicMethod.XGORAM_VALUE.speedVy = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.speedVyaw:
                        PublicMethod.XGORAM_VALUE.speedVyaw = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.bodyX:
                        PublicMethod.XGORAM_VALUE.bodyX = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.bodyY:
                        PublicMethod.XGORAM_VALUE.bodyY = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.bodyZ:
                        PublicMethod.XGORAM_VALUE.bodyZ = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.bodyRoll:
                        PublicMethod.XGORAM_VALUE.bodyRoll = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.bodyPitch:
                        PublicMethod.XGORAM_VALUE.bodyPitch = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.bodyYaw:
                        PublicMethod.XGORAM_VALUE.bodyYaw = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.flagRoll:
                        PublicMethod.XGORAM_VALUE.flagRoll = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.flagPitch:
                        PublicMethod.XGORAM_VALUE.flagPitch = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.flagYaw:
                        PublicMethod.XGORAM_VALUE.flagYaw = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.flagStep:
                        PublicMethod.XGORAM_VALUE.flagStep = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.speedT:
                        PublicMethod.XGORAM_VALUE.speedT = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.legX_1:
                        PublicMethod.XGORAM_VALUE.legX_1 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.legY_1:
                        PublicMethod.XGORAM_VALUE.legY_1 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.legZ_1:
                        PublicMethod.XGORAM_VALUE.legZ_1 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.legX_2:
                        PublicMethod.XGORAM_VALUE.legX_2 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.legY_2:
                        PublicMethod.XGORAM_VALUE.legY_2 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.legZ_2:
                        PublicMethod.XGORAM_VALUE.legZ_2 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.legX_3:
                        PublicMethod.XGORAM_VALUE.legX_3 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.legY_3:
                        PublicMethod.XGORAM_VALUE.legY_3 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.legZ_3:
                        PublicMethod.XGORAM_VALUE.legZ_3 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.legX_4:
                        PublicMethod.XGORAM_VALUE.legX_4 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.legY_4:
                        PublicMethod.XGORAM_VALUE.legY_4 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.legZ_4:
                        PublicMethod.XGORAM_VALUE.legZ_4 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.motor_11:
                        PublicMethod.XGORAM_VALUE.motor_11 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.motor_12:
                        PublicMethod.XGORAM_VALUE.motor_12 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.motor_13:
                        PublicMethod.XGORAM_VALUE.motor_13 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.motor_21:
                        PublicMethod.XGORAM_VALUE.motor_21 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.motor_22:
                        PublicMethod.XGORAM_VALUE.motor_22 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.motor_23:
                        PublicMethod.XGORAM_VALUE.motor_23 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.motor_31:
                        PublicMethod.XGORAM_VALUE.motor_31 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.motor_32:
                        PublicMethod.XGORAM_VALUE.motor_32 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.motor_33:
                        PublicMethod.XGORAM_VALUE.motor_33 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.motor_41:
                        PublicMethod.XGORAM_VALUE.motor_41 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.motor_42:
                        PublicMethod.XGORAM_VALUE.motor_42 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.motor_43:
                        PublicMethod.XGORAM_VALUE.motor_43 = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.motor_speed:
                        PublicMethod.XGORAM_VALUE.motor_speed = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.motor_reset:
                        PublicMethod.XGORAM_VALUE.motor_reset = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.sensorLed:
                        PublicMethod.XGORAM_VALUE.sensorLed = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.sensorIMU:
                        PublicMethod.XGORAM_VALUE.sensorIMU = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.sensorDistence:
                        PublicMethod.XGORAM_VALUE.sensorDistence = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.sensorUltrasonicH:
                        PublicMethod.XGORAM_VALUE.sensorUltrasonicH = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.sensorUltrasonicL:
                        PublicMethod.XGORAM_VALUE.sensorUltrasonicL = PublicMethod.byteToInt(msg[i_msg]);
                        break;
//                    case XGORAM_ADDR.sensorRadio:
//                        XGORAM_VALUE.sensorRadio=PublicMethod.byteToInt(msg[i_msg]);
//                        break;
                    case PublicMethod.XGORAM_ADDR.sensorLedR:
                        PublicMethod.XGORAM_VALUE.sensorLedR = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.sensorLedG:
                        PublicMethod.XGORAM_VALUE.sensorLedG = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.sensorLedB:
                        PublicMethod.XGORAM_VALUE.sensorLedB = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    case PublicMethod.XGORAM_ADDR.sensorMagnet:
                        PublicMethod.XGORAM_VALUE.sensorMagnet = PublicMethod.byteToInt(msg[i_msg]);
                        break;
                    default:
                        break;
                }
                addr = (byte) (PublicMethod.byteToInt(addr) + 1);
                i_msg = i_msg + 1;
            }
        }
    }
}
