package com.luwu.xgo_robot.socket;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.luwu.xgo_robot.BlueTooth.SampleGattAttributes;

import java.lang.reflect.Method;
import java.util.UUID;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class SocketService extends Service {
    private final static String TAG = SocketService.class.getSimpleName();

//    private BluetoothManager mBluetoothManager;
//    private BluetoothAdapter mBluetoothAdapter;
    private String mRobotAddress;
//    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    protected static final int MTU_SIZE = 23;   // 500 + 3，实际可用为500，为了传输固件文件 20210929

    public final static String ACTION_CONNECTED =
            "socket.ACTION_GATT_CONNECTED";
    public final static String ACTION_DISCONNECTED =
            "socket.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_DATA_AVAILABLE =
            "socket.ACTION_DATA_AVAILABLE";
    public final static String ACTION_WRITE_RESULT =
            "socket.ACTION_WRITE_RESULT";
    public final static String EXTRA_DATA =
            "socket.EXTRA_DATA";

    // connection change and services discovered.
    private final SocketStateCallback socketStateCallBack = new SocketStateCallback() {

//        @Override
        public void onConnectionStateChange(int status, int newState) {

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
                close();
            }
        }

        /**
         * Clears the internal cache and forces a refresh of the services from the
         * remote device.
         */
        public boolean clean() {

            return false;
        }


        @Override
        public void onTCPMsgRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }


        @Override
        public void onTCPMsgWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_WRITE_RESULT, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final String data) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DATA, data);
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();


    public boolean connect(final String address) {
        if (tcpSocket == null || address == null) {
            Log.w(TAG, "tcpSocket not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mRobotAddress != null
                && address.equals(mRobotAddress)
                && tcpSocket != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
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
        tcpSocket.close();
        tcpSocket = null;
    }

}

