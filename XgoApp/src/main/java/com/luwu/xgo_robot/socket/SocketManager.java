package com.luwu.xgo_robot.socket;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.luwu.xgo_robot.utils.ThreadUtil;

import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class SocketManager implements TCPListener {
    private final static String TAG = SocketManager.class.getSimpleName();

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

    public boolean connect(final String address,final int port) {
        if (tcpSocket == null) {
            tcpSocket = new TCPClient();
            tcpSocket.setConfig(address,port);
            tcpSocket.setListener(this);
        }

        // Previously connected device.  Try to reconnect.
        if (mRobotAddress != null
                && address.equals(mRobotAddress)
                && tcpSocket != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (tcpSocket.isConnecting()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
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
        tcpSocket.sendMsgToServer("heartBeat",channelFutureListener);
    }

    ChannelFutureListener channelFutureListener = future -> {

    };

    @Override
    public void onMessageResponse(Object msg) {
//        UnpooledByteBufAllocator msgData = (UnpooledByteBufAllocator) msg;
//        msgData.buffer();
//        Log.d(TAG, "onMessageResponse: 收到来自服务器的消息" + msgData.buffer());
    }

    @Override
    public void onServiceStatusConnectChanged(int statusCode) {
        switch (statusCode){
            case TCPListener.STATUS_CONNECT_SUCCESS:
                startHeartBeat();
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
}

