package com.luwu.xgobot.socket;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.luwu.xgobot.data.DataHelper;
import com.luwu.xgobot.data.RobotConstants;
import com.luwu.xgobot.utils.ByteUtile;
import com.luwu.xgobot.utils.ThreadUtil;

import java.lang.ref.WeakReference;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class SocketManager implements TCPListener {
    private static final String TAG = "SocketManager";

    private TCPClient tcpSocket;
    private String mRobotAddress;
    Context context;
    Handler handler = new Handler(Looper.getMainLooper());

    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_CONNECTED =
            "socket.ACTION_GATT_CONNECTED";
    public final static String ACTION_DISCONNECTED =
            "socket.ACTION_GATT_DISCONNECTED";
    public final static String SOCKET_TCP_DATA =
            "socket.SOCKET_TCP_DATA";

    private static SocketManager instance;

    WeakReference<SocketStateListener> listener;

    public void setListener(SocketStateListener listener) {
        this.listener = new WeakReference<>(listener);
    }


    public static SocketManager getInstance() {
        if (instance == null) {
            synchronized (SocketManager.class) {
                if (instance == null) {
                    instance = new SocketManager();
                }
            }
        }
        return instance;
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        context.sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final String data) {
        final Intent intent = new Intent(action);
        intent.putExtra(SOCKET_TCP_DATA, data);
        context.sendBroadcast(intent);
    }

    public boolean isConnected(){
        if (tcpSocket == null)
            return false;
        return tcpSocket.isConnected();
    }

    public boolean connect(final String address,final int port) {
        if (tcpSocket == null) {
            tcpSocket = new TCPClient();
        }
        tcpSocket.setConfig(address,port);
        tcpSocket.setListener(this);

        // Previously connected device.  Try to reconnect.
        if (address.equals(mRobotAddress) && tcpSocket != null && tcpSocket.isConnecting()) {
           return true;
        }

        tcpSocket.setConfig(address,port);
        tcpSocket.connect();
        Log.d(TAG, "Trying to create a new connection.");
        mRobotAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    public void disconnect() {
        if (tcpSocket == null ) {
            return;
        }
        tcpSocket.disconnect();
    }

    public void write(byte[] bytes) {
        ThreadUtil.runIO(() -> {
            //将指令放置进特征中
            if (tcpSocket != null && tcpSocket.isConnected()){
                tcpSocket.sendMsgToServer(bytes, new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        Log.d(TAG, "operationComplete: " + future);
                    }
                });
            }
        });
    }

    public void write(String datas) {
        //将指令放置进特征中
        if (tcpSocket != null && tcpSocket.isConnected())
        tcpSocket.sendMsgToServer(datas, new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {

            }
        });
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (tcpSocket == null) {
            return;
        }
        if (tcpSocket.isConnected()){
            tcpSocket.disconnect();
            tcpSocket = null;
        }
    }

    private void startHeartBeat(){
        handler.post(heartRunnable);
    }

    private void stopHeartBeat(){
        handler.removeCallbacks(heartRunnable);
    }

    Runnable heartRunnable = new Runnable() {
        @Override
        public void run() {
            sendHeartBeat();
            handler.postDelayed(heartRunnable,5000);
        }
    };

    private void sendHeartBeat(){
        if (tcpSocket==null)
            return;
        if (!tcpSocket.isConnected()){
            return;
        }
        byte[] datas = new byte[]{0x01, 0x00};
        byte[] sendData = DataHelper.getSendBytes(RobotConstants.TYPE_DEFAULT, RobotConstants.GET_POWER,datas);

        tcpSocket.sendMsgToServer(sendData,channelFutureListener);
    }

    ChannelFutureListener channelFutureListener = future -> {

    };

    @Override
    public void onMessageResponse(Object msg) {
        if (! (msg instanceof String)){
            Log.e(TAG, "onMessageResponse: 收到来自服务器的消息   格式错误");
            return;
        }

        byte[] bytes = ((String) msg).getBytes();
        byte[] clipBytes = new byte[0];
        for (int i = 0;i<bytes.length-1;i++){
            if (bytes[i] == 0x23 && bytes[i+1] == 0x00){
                clipBytes = new byte[i+1];
                System.arraycopy(bytes,0,clipBytes,0,i+1);
                break;
            }
        }
        String bytesReceived = new String(clipBytes);

        String asciiString = ByteUtile.byteArrayToHex(clipBytes);

        byte[] hexBytes = asciiString.getBytes();
        String hexString = ByteUtile.byteArrayToHex(hexBytes);
        Log.d(TAG, "onMessageResponse: 收到来自服务器的消息:" + msg);
    }

    @Override
    public void onServiceStatusConnectChanged(int statusCode) {
        switch (statusCode){
            case TCPListener.STATUS_CONNECT_SUCCESS:
//                startHeartBeat();
                break;
            case TCPListener.STATUS_CONNECT_ERROR:
                stopHeartBeat();
                break;
            case TCPListener.STATUS_CONNECT_CLOSED:
                stopHeartBeat();
                break;
            default:
                break;
        }
    }

    @Override
    public void onServiceStateMsgChanged(String msg, boolean connected) {
        if (listener.get()!=null){
            listener.get().onStateChange(msg, connected);
        }
    }

}

