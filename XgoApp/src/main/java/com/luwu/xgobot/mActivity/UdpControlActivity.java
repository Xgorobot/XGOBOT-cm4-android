package com.luwu.xgobot.mActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.luwu.xgobot.R;
import com.luwu.xgobot.socket.SocketManager;
import com.luwu.xgobot.socket.UdpClient;
import com.luwu.xgobot.utils.QRCodeGenerator;


public class UdpControlActivity extends BaseActivity {
    private static final String TAG = "NetSettingActivity";
    private ImageButton startBtn,stopBtn;
    private ImageView backImg;

    private static final boolean TEST = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        updateLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.udpcontrol_activity);
        startBtn = findViewById(R.id.btn_start);
        stopBtn = findViewById(R.id.btn_end);
        backImg = findViewById(R.id.settingBtnBack);

        startBtn.setOnClickListener(this::startUdp);
        stopBtn.setOnClickListener(this::stopUdp);
        backImg.setOnClickListener(v -> finish());

    }

    private void startUdp(View view) {
        try {
            UdpClient.sendBroadCast("1",6001);
            startBtn.setImageResource(R.drawable.start1);
            stopBtn.setImageResource(R.drawable.end);
        } catch (Exception e) {
            Toast.makeText(this,"sendBroadCastFail",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void stopUdp(View view) {
        try {
            UdpClient.sendBroadCast("0",6001);
            startBtn.setImageResource(R.drawable.start);
            stopBtn.setImageResource(R.drawable.end1);
        } catch (Exception e) {
            Toast.makeText(this,"sendBroadCastFail",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: isConnected:" +SocketManager.getInstance().isConnected() );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketManager.getInstance().setListener(null);
    }


}