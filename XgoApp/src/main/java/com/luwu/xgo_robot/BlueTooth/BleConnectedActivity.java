package com.luwu.xgo_robot.BlueTooth;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.luwu.xgo_robot.mActivity.MainActivity;
import com.luwu.xgo_robot.mMothed.DeviceUtil;
import com.luwu.xgo_robot.mMothed.PublicMethod;
import com.luwu.xgo_robot.mMothed.mToast;

import java.util.ArrayList;
import java.util.List;

import static com.luwu.xgo_robot.mMothed.PublicMethod.hideBottomUIMenu;

@SuppressLint("SetTextI18n")
public class BleConnectedActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_ENABLE_BT = 1;
    private TextView mBtConnectState;
    private BluetoothAdapter mBluetoothAdapter;
    private List<BleDeviceEntity> list = new ArrayList<>();
    private List<BleDeviceEntity> bluetoothList = new ArrayList<>();
    private final int CONNECTED = 0;
    private final int DISCONNECTED = 1;
    private ProgressDialog progressDialog;
    Long startTime = 0L;
    boolean isSearching = false;
    private static boolean flagLoop = false;
    private getProductTypeThread getProductType; //处理产品型号读取线程
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideBottomUIMenu(BleConnectedActivity.this);
        setContentView(R.layout.activity_ble_connected);
        mHandler = new Handler();
        flagLoop = true;
        getProductType = new getProductTypeThread();
        getProductType.start();
        initView();
    }

    private void initView() {
        findViewById(R.id.ble_conected_disconected).setOnClickListener(this);
        findViewById(R.id.ble_conected_rename).setOnClickListener(this);
        findViewById(R.id.ble_conected_close).setOnClickListener(this);
        findViewById(R.id.ble_conected_search).setOnClickListener(this);
        mBtConnectState = findViewById(R.id.ble_conected_state);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mBtConnectState.setText(getResources().getString(R.string.conectedsuccess) + AppContext.getmBleClient().getBleNameConected());
        flagLoop = true;
        String connectState;
        switch(PublicMethod.localeLanguage){
            case "zh":
                connectState = String.format("连接成功 "+ AppContext.getmBleClient().getBleNameConnected());
                break;
            default:
                connectState = String.format(AppContext.getmBleClient().getBleNameConnected() + " connected");
        }

        mBtConnectState.setText(connectState);
//        getProductType = new getProductTypeThread();
//        getProductType.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ble_conected_search:
                Intent intent = new Intent(BleConnectedActivity.this, BleSearchActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.ble_conected_close:
                finish();
                break;
            case R.id.ble_conected_disconected:
                AppContext.getmBleClient().disConnect();
                if (DeviceUtil.isTabletDevice(BleConnectedActivity.this)){
//                    Intent intent2 = new Intent(BleConectedActivity.this, SearchActivity.class);
//                    startActivity(intent2);
                }else {
                    Intent intent2 = new Intent(BleConnectedActivity.this, BleActivity.class);
                    startActivity(intent2);
                }
                finish();
                break;
            case R.id.ble_conected_rename:
                changeNameDialog();
                break;
            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        flagLoop = false;
    }

    private void changeNameDialog() {

        final Dialog dialog = new Dialog(this,
                R.style.myNewsDialogStyle);
        View layout = View.inflate(this, R.layout.ble_change_name,
                null);
        dialog.setContentView(layout);

        Button cancel_btn = (Button) layout.findViewById(R.id.cancel_btn);
        Button confirm_btn = (Button) layout.findViewById(R.id.confirm_btn);
        TextView title = (TextView) layout.findViewById(R.id.title);
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
            }
        });
        // 设置取消按钮的事件
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

// 设置确定按钮的事件
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查名称是否为空，再检查蓝牙是否连接
                if (!"".equals(items_name_edit.getText().toString().trim())) {
                    final String bleName = String.valueOf(items_name_edit.getText());
                    //改名指令
                    if (AppContext.getmBleClient().isConnected()) {
                        dialog.dismiss();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    BleDeviceEntity.changeBleName(bleName);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //todo 需要判断改名是否成功吗？
                            }
                        }).start();
                        switch(PublicMethod.localeLanguage){
                            case "zh":
                                Toast.makeText(BleConnectedActivity.this,
                                        "蓝牙已被重命名" + "，" +
                                                "请重新连接",
                                        Toast.LENGTH_LONG).show();
                                progressDialog = ProgressDialog.show(BleConnectedActivity.this, "蓝牙已被重命名",
                                        "请重新连接", false, true);
                                break;
                            default:
                                Toast.makeText(BleConnectedActivity.this,
                                        "Renamed" + "," +
                                                "please reconnect",
                                        Toast.LENGTH_LONG).show();
                                progressDialog = ProgressDialog.show(BleConnectedActivity.this, "Renamed",
                                        "please reconnect", false, true);
                        }


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                AppContext.getmBleClient().disConnect();
                                if (DeviceUtil.isTabletDevice(BleConnectedActivity.this)){
                                    Intent intent2 = new Intent(BleConnectedActivity.this, BleSearchActivity.class);
                                    startActivity(intent2);
                                }else {
                                    Intent intent2 = new Intent(BleConnectedActivity.this, BleActivity.class);
                                    startActivity(intent2);
                                }
                            }
                        }, 4500);

                    } else {
                        switch(PublicMethod.localeLanguage){
                            case "zh":
                                new AlertDialog.Builder(BleConnectedActivity.this)
                                        .setTitle("注意")
                                        .setMessage("蓝牙未连接")
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .setPositiveButton("连接", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (DeviceUtil.isTabletDevice(BleConnectedActivity.this)){
                                                    Intent intent2 = new Intent(BleConnectedActivity.this, BleSearchActivity.class);
                                                    startActivity(intent2);
                                                }else {
                                                    Intent intent2 = new Intent(BleConnectedActivity.this, BleActivity.class);
                                                    startActivity(intent2);
                                                }

                                            }
                                        })
                                        .create().show();
                                break;
                            default:
                                new AlertDialog.Builder(BleConnectedActivity.this)
                                        .setTitle("Attention")
                                        .setMessage("Bluetooth not connected")
                                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .setPositiveButton("connect", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (DeviceUtil.isTabletDevice(BleConnectedActivity.this)){
                                                    Intent intent2 = new Intent(BleConnectedActivity.this, BleSearchActivity.class);
                                                    startActivity(intent2);
                                                }else {
                                                    Intent intent2 = new Intent(BleConnectedActivity.this, BleActivity.class);
                                                    startActivity(intent2);
                                                }

                                            }
                                        })
                                        .create().show();
                        }

                    }
                } else {
                    switch(PublicMethod.localeLanguage){
                        case "zh":
                            Toast.makeText(BleConnectedActivity.this, "名字不可为空", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(BleConnectedActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    //TODO 显示连接结果
//                    Toast.makeText(BleActivity.this,"连接成功",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case 2:
                break;
            default:
                break;
        }
    }

    // 读取设备类型
    private class getProductTypeThread extends Thread {
        @Override
        public void run() {
            while (currentThread().isAlive() && flagLoop) {
                MainActivity.addMessageRead(new byte[]{PublicMethod.XGORAM_ADDR.versions, 0x01});
                Message message = new Message();
//                message.what = 0;
                mHandler.sendMessageDelayed(message, 200);//200ms以后拿结果
                try {
                    sleep(5000);//5s更新一次
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


