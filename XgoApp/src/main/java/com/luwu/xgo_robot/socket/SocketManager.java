package com.luwu.xgo_robot.socket;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class SocketManager{
    private final static String TAG = SocketManager.class.getSimpleName();

    private TCPClient tcpSocket;
    private String mRobotAddress;
    Context context;

    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    protected static final int MTU_SIZE = 23;   // 500 + 3，实际可用为500，为了传输固件文件 20210929

    public final static String ACTION_CONNECTED =
            "socket.ACTION_GATT_CONNECTED";
    public final static String ACTION_DISCONNECTED =
            "socket.ACTION_GATT_DISCONNECTED";
    public final static String SOCKET_TCP_DATA =
            "socket.SOCKET_TCP_DATA";




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
        if (tcpSocket == null || address == null) {
            Log.w(TAG, "tcpSocket not initialized or unspecified address.");
            return false;
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
                tcpSocket.setConfig(address,port);
                tcpSocket.connect();
                return false;
            }
        }
        //连接之前先关掉GATT
        close();

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
        //将指令放置进特征中
        tcpSocket.sendMsgToServer(bytes, new ChannelFutureListener() {
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
        tcpSocket.disconnect();
        tcpSocket = null;
    }

}

