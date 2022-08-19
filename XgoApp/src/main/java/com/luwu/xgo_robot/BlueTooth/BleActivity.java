package com.luwu.xgo_robot.BlueTooth;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.luwu.xgo_robot.AppContext;
import com.luwu.xgo_robot.R;
import com.luwu.xgo_robot.mMothed.PublicMethod;
import com.luwu.xgo_robot.mMothed.mToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.luwu.xgo_robot.mMothed.PublicMethod.hideBottomUIMenu;

@SuppressLint("SetTextI18n")
public class BleActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int SEARCHACTIVITY_CONECT = 2;
    private TextView mBtConnectState;
    private BluetoothAdapter mBluetoothAdapter;
    private List<BleDeviceEntity> list = new ArrayList<>();//没注释 这是啥
    private List<BleDeviceEntity> bluetoothList = new ArrayList<>(); //这个和上面啥区别
    private final int CONNECTED = 0;
    private final int DISCONNECTED = 1;

    private ProgressDialog progressDialog;

    //    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    Long startTime = 0L;
    boolean isSearching = false;
    MyHandler mHandler = new MyHandler();
    private SensorManager sensorManager;  //定义传感器管理器
    private Vibrator vibrator;            //定义振动器



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        hideBottomUIMenu();
        setContentView(R.layout.activity_ble);
        Log.d("BluetoothLeService", "ble,onCreat");
        initView();
        initBluetooth();
        Log.d("BluetoothLeService", "ble,onCreated");
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu(BleActivity.this);
        Log.d("BluetoothLeService", "ble,Resume");
//        //为加速度传感器注册监听器
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        Log.d("ble", "Ble,Resume");
    }

    private void initView() {
        findViewById(R.id.home_ble_search).setOnClickListener(this);
        findViewById(R.id.home_ble_close).setOnClickListener(this);
        mBtConnectState = findViewById(R.id.home_ble_state);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);  // 获取传感器管理器
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);  //获取振动器服务
    }

    private void initBluetooth() {
        //申请定位权限，才能用蓝牙
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (this.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                        this.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    String[] list =new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT};
                    requestPermissions(list, 123);
                }
            }
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 123);
            }else {
//                //判断蓝牙是否开启
//                BluetoothAdapter bleAdapter = BluetoothAdapter.getDefaultAdapter();
//                if (!bleAdapter.isEnabled()){
//                    bleAdapter.enable();
//                }
            }
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //判断蓝牙是否开启
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        float[] values = sensorEvent.values;  //获取传感器X、Y、Z三个轴的输出信息
        int sensorType = sensorEvent.sensor.getType();  // 获取传感器类型
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {  //如果是加速度传感器
            //X轴输出信息>15,Y轴输出信息>15,Z轴输出信息>20
            if ((values[0] > 15 || values[1] > 15 || values[2] > 20) && !isSearching) {
                Log.d("BluetoothLeService", "shake");
                scanLeDevice(true);//开始搜索
                isSearching = true;
                if (!AppContext.getmBleClient().isConnected()) {
                    //todo ????????????为什么有时候会出空指针，找不到activity
                    if (!isFinishing()) {
                        startTime = System.currentTimeMillis();
                        switch(PublicMethod.localeLanguage){
                            case "zh":
                                progressDialog = ProgressDialog.show(BleActivity.this,
                                        "蓝牙搜索中...",
                                        "请稍等...",
                                        true);//显示加载框
                                handler.post(shakePhoneRunnable);
                                break;
                            default:
                                progressDialog = ProgressDialog.show(BleActivity.this,
                                        "Searching...",
                                        "wait a minute...",
                                        true);//显示加载框
                                handler.post(shakePhoneRunnable);
                        }
                    } else {
                        Log.d("BluetoothLeService", "ble is finished");
                    }
                } else {
                    switch(PublicMethod.localeLanguage){
                        case "zh":
                            mToast.show(BleActivity.this, "蓝牙已连接");
                            break;
                        default:
                            mToast.show(BleActivity.this, "Bluetooth connected");
                    }
                    isSearching = false;
                }
            }
        }
    }

    ////******************************震动传感器触发事件************************/////
    Runnable shakePhoneRunnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //如果列表中没有蓝牙
            if (bluetoothList.size() == 0) {
                Long currentTime = System.currentTimeMillis();
                if (currentTime - startTime > 5000) {
                    if(progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }

                    switch(PublicMethod.localeLanguage){
                        case "zh":
                            progressDialog = ProgressDialog.show(BleActivity.this, "未找到蓝牙设备-_-", "请重新尝试", true);//显示加载框
                            break;
                        default:
                            progressDialog = ProgressDialog.show(BleActivity.this, "Bluetooth device not found-_-", "Please tey again", true);//显示加载框
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 1000);
//                    mHandler.sendEmptyMessage(DISCONNECTED);
                    isSearching = false;
                    handler.removeCallbacks(shakePhoneRunnable);
                    scanLeDevice(false);
                } else {
                    handler.postDelayed(this, 200);
                }
            } else {
                //如果列表中有蓝牙
//                scanLeDevice(false);
                final BleDeviceEntity ble = bluetoothList.get(0);
                BluetoothDevice device = ble.getDevice();
                try {
                    AppContext.getmBleClient().connect(device.getAddress());

                    //如果5秒后还未连接，则断开连接，并提示重连
//                    conenecting = true;
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(conenecting){
//                                progressDialog.dismiss();

//                    Toast.makeText(BleActivity.this, "蓝牙连接失败，请重试", Toast.LENGTH_SHORT).show();


//                                progressDialog = ProgressDialog.show(BleActivity.this, "蓝牙连接失败，请重试", "尝试连接 " + ble.getName() + "失败", true);//显示加载框
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        progressDialog.dismiss();
//                                    }
//                                }, 1000);


//                                AppContext.getmBleClient().disConnect();
//                                conenecting = false;
//                            }
//                        }
//                    }, 12000L);
                    progressDialog.dismiss();
                    switch(PublicMethod.localeLanguage){
                        case "zh":
                            progressDialog = ProgressDialog.show(BleActivity.this, "正在连接", "已连接设备 " + ble.getName(), true);//显示加载框
                            break;
                        default:
                            progressDialog = ProgressDialog.show(BleActivity.this, "Connecting", ble.getName() + " Connected", true);//显示加载框
                    }
                    AppContext.getmBleClient().setmConnectRequest(new BleClient.ConnectionRequest() {
                        @Override
                        public void connectSuccess() {
                            AppContext.getmBleClient().setBleNameConnected(ble.getName());
                            mHandler.sendEmptyMessage(CONNECTED);
                        }

                        @Override
                        public void connectFailed() {
                            mHandler.sendEmptyMessage(DISCONNECTED);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                vibrator.vibrate(200);                    //设置振动器频率
                sensorManager.unregisterListener(BleActivity.this);  //取消注册监听器
                isSearching = false;
                handler.removeCallbacks(shakePhoneRunnable);
            }
        }
    };

    //////////*************蓝牙命名的弹出窗口*********************///////////////////////
    private void setNameDialog() {

        final Dialog dialog = new Dialog(this,
                R.style.myNewsDialogStyle);
        View layout = View.inflate(this, R.layout.ble_change_name,
                null);
        dialog.setContentView(layout);

        Button cancel_btn = (Button) layout.findViewById(R.id.cancel_btn);
        Button confirm_btn = (Button) layout.findViewById(R.id.confirm_btn);
        TextView title = (TextView) layout.findViewById(R.id.title);
        switch(PublicMethod.localeLanguage){
            case "zh":
                title.setText("蓝牙连接成功，起一个新名字");
                break;
            default:
                title.setText("Connected. Rename it");
        }
        final EditText items_name_edit = layout.findViewById(R.id.items_new_name);
        ImageView iv_close = (ImageView) layout.findViewById(R.id.iv_close);
        dialog.show();
        WindowManager.LayoutParams params =
                dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        // 设置取消按钮的事件
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        // 设置确定按钮的事件
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeName(dialog, items_name_edit);
            }
        });
    }

    //********************************蓝牙改名**********************************//
    public void changeName(Dialog dialog, EditText items_name_edit) {
        if (!"".equals(items_name_edit.getText().toString().trim())) {
            AppContext.getmBleClient().setBleName(String.valueOf(items_name_edit.getText()));
            //改名指令
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        BleDeviceEntity.changeBleName(AppContext.getmBleClient().getBleName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    bluetoothList.get(0).setName(AppContext.getmBleClient().getBleName());
                    //todo 需要判断改名是否成功吗？
                }
            }).start();
            //为加速度传感器注册监听器
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_GAME);
            dialog.dismiss();
            if ("BleActivity".equals(getClass().getSimpleName())) {
                switch(PublicMethod.localeLanguage){
                    case "zh":
                        progressDialog = ProgressDialog.show(BleActivity.this, "蓝牙已被重命名", "请重新连接", true);//显示加载框
                        break;
                    default:
                        progressDialog = ProgressDialog.show(BleActivity.this, "Renamed", "Please reconnect", true);//显示加载框
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 1500);
            }
        } else {
            switch(PublicMethod.localeLanguage){
                case "zh":
                    Toast.makeText(BleActivity.this, "名字不可为空", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(BleActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //****************************新的蓝牙搜索方法**************************
    boolean mScanning = false;
    long SCAN_PERIOD = 10000;

    private void scanLeDevice(final boolean enable) {

//todo  安卓5,0之后建议用这个函数，不知道稳定不稳定，还没试

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
//            scanner.startScan(new ScanCallback() {
//                @Override
//                public void onScanResult(int callbackType, ScanResult result) {
//                    super.onScanResult(callbackType, result);
//                }
//            });
//        }
//        scanner.stopScan(leCallback);
        if (enable) {
//             Stops scanning after a pre-defined scan period.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);
//
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback); //开始搜索
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);//停止搜索
        }
    }
    // 搜索的操作最好放在Activity的onResume里面或者服务里面，我有发现放在onCreate有时响应不及时

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            //todo 换到新线程中执行？
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (device == null || device.getName() == null) return;
                    bluetoothListAdd(device, rssi);
                }
            }).start();

//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    //在这里可以把搜索到的设备保存起来
//                    //device.getName();获取蓝牙设备名字
//                    //device.getAddress();获取蓝牙设备mac地址
//                    if (device == null || device.getName() == null) return;
//                    bluetoothListAdd(device, rssi);
//                }
//            });
        }
    };

    //****************************搜索到新的蓝牙时添加到List中********************************
    private void bluetoothListAdd(BluetoothDevice device, int rssi) {
        String ibeaconName = "";
        if (null == device.getName()) {
            ibeaconName = "unknown_bluetooth";
        } else {
            ibeaconName = device.getName();
        }
        String mac = device.getAddress();
        if (ibeaconName.length() >= 4) {
            boolean flag = true;
            for (int x = 0; x < list.size(); x++) {
                if (mac.equals(list.get(x).getMac())) {
                    flag = false;
                }
            }
            if (flag) {
                if ("XGO".equals(ibeaconName.substring(0, 3))) {
                    list.add(new BleDeviceEntity(ibeaconName, mac, rssi, device));
                    Log.d("麦克mac", mac);
                    handler.sendEmptyMessage(111);
                }
            }
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 111) {
                bluetoothList.clear();
                bluetoothList.addAll(list);
                Collections.sort(bluetoothList);
            }
            return true;
        }
    });

    //***********************************************************************************************//

//    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                //获取蓝牙设备
//                BluetoothDevice scanDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                if (scanDevice == null || scanDevice.getName() == null) return;
////                Log.d(TAG, "name="+scanDevice.getName()+"address="+scanDevice.getAddress());
//                //蓝牙设备名称
//                int rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
//
//                String ibeaconName = "";
//                if (null == scanDevice.getName()) {
//                    ibeaconName = "unknown_bluetooth";
//                } else {
//                    ibeaconName = scanDevice.getName();
//                }
//                String mac = scanDevice.getAddress();
//                boolean flag = true;
//                for (int x = 0; x < list.size(); x++) {
//                    if (mac.equals(list.get(x).getMac())) {
//                        flag = false;
//                    }
//                }
//                if (flag) {
//                    if (ibeaconName.length() >= 4) {
//                        if ("Find".equals(ibeaconName.substring(0, 4))) {
//                            list.add(new BleDeviceEntity(ibeaconName, mac, rssi, scanDevice));
//                            handler.sendEmptyMessage(111);
//                        }
//                    }
//                }
//            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//            }
//        }
//    };

    //todo  dialog是否会引起内存泄漏
//    static class DiaHandler extends Handler{
//
//        }

    //是否连接成功的消息处理
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CONNECTED:
                    progressDialog.dismiss();
                    if ("XGO".equals(AppContext.getmBleClient().getBleNameConnected())) {
                        setNameDialog();
                        //改名
                    } else {
                        if (!isFinishing()) {
                            switch(PublicMethod.localeLanguage){
                                case "zh":
                                    progressDialog = ProgressDialog.show(BleActivity.this, "蓝牙已成功连接", "蓝牙连接设备" + AppContext.getmBleClient().getBleNameConnected(), true);//显示加载框
                                    break;
                                default:
                                    progressDialog = ProgressDialog.show(BleActivity.this, "Bluetooth connected", "connect to device" + AppContext.getmBleClient().getBleNameConnected(), true);//显示加载框
                            }

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    finish();
                                }
                            }, 500);
                        } else {
                            Log.d("BluetoothLeService", "ble is finished");
                        }
                    }
                    break;
                case DISCONNECTED:
//                    //为加速度传感器注册监听器
//                    sensorManager.registerListener(BleActivity.this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//                            SensorManager.SENSOR_DELAY_GAME);
                    progressDialog.dismiss();
                    if ("BleActivity".equals(getClass().getSimpleName())) {
                        switch(PublicMethod.localeLanguage){
                            case "zh":
                                progressDialog = ProgressDialog.show(BleActivity.this,
                                        "蓝牙连接失败，请重试",
                                        "",
                                        true);//显示加载框
                                break;
                            default:
                                progressDialog = ProgressDialog.show(BleActivity.this,
                                        "Connect failed, please try again",
                                        "",
                                        true);//显示加载框
                        }


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                            }
                        }, 1000);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_ble_search:
                Intent intent = new Intent(BleActivity.this, BleSearchActivity.class);
                startActivityForResult(intent, SEARCHACTIVITY_CONECT);
                break;
            case R.id.home_ble_close:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEARCHACTIVITY_CONECT:
                if (resultCode == RESULT_OK) {
                    //TODO 显示连接结果
//                    Toast.makeText(BleActivity.this,"连接成功",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case REQUEST_ENABLE_BT:
                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = bluetoothManager.getAdapter();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //取消搜索
        scanLeDevice(false);
        sensorManager.unregisterListener(BleActivity.this);  //取消注册监听器
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消搜索
        scanLeDevice(false);
        Log.d("ble", "Ble,Pause");
    }
}
