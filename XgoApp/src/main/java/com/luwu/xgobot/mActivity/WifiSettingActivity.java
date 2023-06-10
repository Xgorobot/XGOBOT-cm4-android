package com.luwu.xgobot.mActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.SPUtils;
import com.google.zxing.WriterException;
import com.luwu.xgobot.BuildConfig;
import com.luwu.xgobot.R;
import com.luwu.xgobot.mActivity.main.XgoMainActivity;
import com.luwu.xgobot.socket.SocketManager;
import com.luwu.xgobot.socket.SocketStateListener;
import com.luwu.xgobot.socket.UdpClient;
import com.luwu.xgobot.utils.QRCodeGenerator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class WifiSettingActivity extends BaseActivity {
    private static final String TAG = "NetSettingActivity";
    private EditText ssid,password;
    private Button getCodeBtn;
    private ImageView imageView;

    private static final boolean TEST = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        updateLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_activity);
        ssid = findViewById(R.id.edit_ssid);
        password = findViewById(R.id.edit_password);
        getCodeBtn = findViewById(R.id.button_code);
        imageView = findViewById(R.id.image_qr);
        getCodeBtn.setOnClickListener(this::onClick);
        findViewById(R.id.settingBtnBack).setOnClickListener(this::finish);
    }

    private void finish(View view) {
        finish();
    }

    private void onClick(View view) {
        String ssidStr = ssid.getText().toString();
        String passwordStr = password.getText().toString();
        if (TextUtils.isEmpty(ssidStr)||TextUtils.isEmpty(passwordStr)){
            return;
        }else {
            String qrCodeString = generateWiFiQRContent(ssidStr,passwordStr);
            try {
                int width = imageView.getWidth();
                int height = imageView.getHeight();
                Bitmap qrCodeBitmap = QRCodeGenerator.generateQRCodeBitmap(qrCodeString,width,height);
                imageView.setImageBitmap(qrCodeBitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
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

    public static String generateWiFiQRContent(String ssid, String password) {
        StringBuilder qrContent = new StringBuilder();
        qrContent.append("WIFI:");
        // 添加SSID
        qrContent.append("S:").append(ssid).append(';');
        // 添加密码
        qrContent.append("T:").append("WPA").append(';');
        qrContent.append("P:").append(password).append(';');
        // 添加隐藏网络标志位（可选）
        qrContent.append("H:").append("false").append(';');
        return qrContent.toString();
    }

}