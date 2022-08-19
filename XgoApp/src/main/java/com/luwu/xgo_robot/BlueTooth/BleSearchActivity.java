package com.luwu.xgo_robot.BlueTooth;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.luwu.xgo_robot.AppContext;
import com.luwu.xgo_robot.R;
import com.luwu.xgo_robot.mMothed.PublicMethod;
import com.luwu.xgo_robot.mMothed.mToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BleSearchActivity extends Activity {

    private static final String TAG = "SearchActivity";
    //    private BluetoothAdapter mBluetoothAdapter;
    private List<BleDeviceEntity> list = new ArrayList<>();
    private List<BleDeviceEntity> bluetoothList = new ArrayList<>();

    private BleAdapter bleAdapter;
    private ListView listView;
    private ImageView button;
    private TextView helpMsg;

    private ProgressDialog progressDialog;
    private ProgressDialog progressDialogChangeName;

    private final int CONNECTED = 0;
    private final int DISCONNECTED = 1;

    boolean startConnect = false;//当前是否正在开始连接,用于连接失败时自动重连

    MyHandler myHandler = new MyHandler();

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CONNECTED:
                    progressDialog.dismiss();

                    if ("XGO".equals((String) msg.obj)) {
                        setNameDialog();
                        //改名
                    } else {
                        finish();
                    }
                    break;
                case DISCONNECTED:
                    Log.d(TAG, "连接断开");
                    switch(PublicMethod.localeLanguage){
                        case "zh":
                            mToast.show(BleSearchActivity.this,"断开连接");
                            break;
                        default:
                            mToast.show(BleSearchActivity.this,"disconnect");
                    }
                    progressDialog.dismiss();
                    finish();
                    break;
                default:
                    break;
            }
        }
    }


    //////////*************蓝牙命名的弹出窗口*********************///////
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
                title.setText("蓝牙连接成功，起个新名字吧！");
                break;
            default:
                title.setText("Connected,Rename it!");
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
            final String bleName = String.valueOf(items_name_edit.getText());
            //改名指令
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        BleDeviceEntity.changeBleName(bleName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    bluetoothList.get(0).setName(bleName);
                    //todo 需要判断改名是否成功吗？
                }
            }).start();

            dialog.dismiss();
            if ("BleActivity".equals(getClass().getSimpleName())) {
                switch(PublicMethod.localeLanguage){
                    case "zh":
                        progressDialogChangeName = ProgressDialog.show(BleSearchActivity.this, "蓝牙已被重命名", "请重新连接", true);//显示加载框
                        break;
                    default:
                        progressDialogChangeName = ProgressDialog.show(BleSearchActivity.this, "Renamed", "Please reconnect", true);//显示加载框

                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialogChangeName.dismiss();
                    }
                }, 1500);
            }
        } else {
            switch(PublicMethod.localeLanguage){
                case "zh":
                    Toast.makeText(BleSearchActivity.this, "名字不可为空", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(BleSearchActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_search);
        initView();
//        AppContext.getmBleClient().disConnect();
//        timer = new Timer();
//        timer.schedule(new TimerTask1(),3000,2000);//tiemr.schedule(执行的方法,延迟时间,多久执行一次)

    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 111) {
                bluetoothList.clear();
                bluetoothList.addAll(list);
                Collections.sort(bluetoothList);
                bleAdapter.notifyDataSetChanged();
            }
            return true;
        }
    });

    BleDeviceEntity deviceForConnect;
    private void initView() {
        button = findViewById(R.id.bluetooth_close);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        listView = (ListView) findViewById(R.id.search_listview);
        bleAdapter = new BleAdapter(this, R.layout.ble_item_search, bluetoothList);
        listView.setAdapter(bleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                deviceForConnect = bluetoothList.get(i);
                switch(PublicMethod.localeLanguage){
                    case "zh":
                        progressDialog = ProgressDialog.show(BleSearchActivity.this,
                                " 正在连接，请稍等...",
                                "连接中...",
                                true);//显示加载框
                        break;
                    default:
                        progressDialog = ProgressDialog.show(BleSearchActivity.this,
                                " Connecting, wait a minute",
                                "connecting",
                                true);//显示加载框
                }


                //停止搜索
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    scanner.stopScan(scanCallback);
                }else{
                    mBluetoothAdapter.startLeScan(leScanCallback);
                }

                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BluetoothDevice device = deviceForConnect.getDevice();
//                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                        cm.setText(device.getAddress());
                        startConnect = true;
//                Toast.makeText(getApplicationContext(), R.string.connecting_please_wait_a_moment, Toast.LENGTH_LONG);
                        try {
                            AppContext.getmBleClient().setmConnectRequest(connectionRequest);
                            Boolean isSucess = AppContext.getmBleClient().connect(device.getAddress());
                            Log.d("搜索到的蓝牙mac", device.getAddress());
                            if (!isSucess) {
                                setResult(RESULT_CANCELED);
                                myHandler.sendEmptyMessage(DISCONNECTED);
                                Log.d("", "蓝牙未开启或者权限不足");
                            }
                            bluetoothList.clear();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },200);

            }
        });
        helpMsg = findViewById(R.id.search_help);
        initBluetooth();
    }

    BluetoothLeScanner scanner;
    BluetoothManager bluetoothManager;
    BluetoothAdapter mBluetoothAdapter;
    private void initBluetooth() {
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 1);
        }else {
            Log.d(TAG, "initBluetooth: 开始扫描");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                initScanCallback();
                scanner = mBluetoothAdapter.getBluetoothLeScanner();
                scanner.startScan(scanCallback);
            }else{
                mBluetoothAdapter.startLeScan(leScanCallback);
            }
            helpMsg.setVisibility(View.VISIBLE);
        }
    }

    BleClient.ConnectionRequest connectionRequest = new BleClient.ConnectionRequest() {
        @Override
        public void connectSuccess() {
            AppContext.getmBleClient().setBleNameConnected(deviceForConnect.getName());
            setResult(RESULT_OK);
            Message msg = new Message();
            msg.what = CONNECTED;
            msg.obj = deviceForConnect.getName();
            myHandler.sendMessage(msg);
//                                progressDialog.dismiss();
        }

        @Override
        public void connectFailed() {
            setResult(RESULT_CANCELED);

            if (startConnect){
                startConnect = false;
                Log.d(TAG, "connectFailed: 连接失败,准备重连");
                AppContext.getmBleClient().disConnect();
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BluetoothDevice device = deviceForConnect.getDevice();
                        AppContext.getmBleClient().connect(device.getAddress());
                    }
                },1000);
            }else {
                Log.d(TAG, "connectFailed: 连接失败,退出");
                myHandler.sendEmptyMessage(DISCONNECTED);
            }

        }
    };

    ScanCallback scanCallback;
    private void initScanCallback(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    onDeviceScan(result.getRssi(),result.getDevice());
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                }
            };
        }else {
            leScanCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    onDeviceScan(rssi,device);
                }
            };
        }
    }

    BluetoothAdapter.LeScanCallback leScanCallback;

    private void onDeviceScan(int rssi,BluetoothDevice device){
        String ibeaconName = "";
        if (null == device.getName()) {
            ibeaconName = "unknown_bluetooth";
        } else {
            ibeaconName = device.getName();
        }
        String mac = device.getAddress();
        boolean flag = true;
        for (int x = 0; x < list.size(); x++) {
            if (mac.equals(list.get(x).getMac())) {
                flag = false;
            }
        }
        if (flag) {
            if (ibeaconName.length() >= 4) {
                if (ibeaconName.startsWith("XGO")) {
                    helpMsg.setVisibility(View.GONE);
                    list.add(new BleDeviceEntity(ibeaconName, mac, rssi, device));
                    Log.d("扫描到设备:", device.getAddress());
                    handler.sendEmptyMessage(111);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppContext.getmBleClient().setmConnectRequest(null);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            scanner.stopScan(scanCallback);
        }else{
            mBluetoothAdapter.startLeScan(leScanCallback);
        }
        Log.d("ble", "Search,Destroy");
    }
}