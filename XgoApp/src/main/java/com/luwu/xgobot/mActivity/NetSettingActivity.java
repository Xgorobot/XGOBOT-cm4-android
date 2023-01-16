package com.luwu.xgobot.mActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.luwu.xgobot.BuildConfig;
import com.luwu.xgobot.R;
import com.luwu.xgobot.mActivity.main.XgoMainActivity;
import com.luwu.xgobot.socket.SocketManager;
import com.luwu.xgobot.socket.SocketStateListener;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NetSettingActivity extends AppCompatActivity implements SocketStateListener {
    private static final String TAG = "NetSettingActivity";
    private EditText ipEdit,portEdit,cameraPortEdit;
    private TextView stateText;
    private Button connectBtn;

    private static final boolean TEST = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        ipEdit = findViewById(R.id.edit_host);
        ipEdit.setText("192.168.45.195");
        portEdit = findViewById(R.id.edit_tcp_port);
        cameraPortEdit = findViewById(R.id.edit_camera_port);
        connectBtn = findViewById(R.id.button_connect);
        stateText = findViewById(R.id.text_state);

        connectBtn.setOnClickListener(this::onClick);
        SocketManager.getInstance().setListener(this);
    }


    private void onClick(View view){
        String hostIp = ipEdit.getText().toString();
        int tcpPort = -1;
        int cameraPort = -1;
        try {
            tcpPort = Integer.parseInt(portEdit.getText().toString());
            cameraPort = Integer.parseInt(cameraPortEdit.getText().toString());
        }catch (NumberFormatException e){
            e.printStackTrace();
        }

        if (TEST && BuildConfig.DEBUG){
            Intent intent = new Intent(NetSettingActivity.this, XgoMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else if (isCorrectIp(hostIp) && tcpPort>0 && cameraPort > 0){
            SPUtils.getInstance().put("host",hostIp);
            SPUtils.getInstance().put("tcpPort",tcpPort);
            SPUtils.getInstance().put("cameraPort",cameraPort);
            connect(hostIp,tcpPort);
            connectBtn.setEnabled(false);
            Toast toast = Toast.makeText(NetSettingActivity.this,"Connecting ,please wait" , Toast.LENGTH_SHORT);
            toast.show();
        }else {
            if (!isCorrectIp(hostIp)){
                Toast toast = Toast.makeText(NetSettingActivity.this,"IP address is illegal" , Toast.LENGTH_LONG);
                toast.show();

            }
        }
    }


    private void connect(String hostIp,int port){
        Log.d(TAG, "connect host: " + hostIp + "   port:" + port);
        SocketManager socketManager = SocketManager.getInstance();
        socketManager.disconnect();
        socketManager.connect(hostIp,port);
    }

    /** * 判断是否为合法IP **/
    public static boolean isCorrectIp(String ipAddress) {
        String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }


    String testString = "start";
    @Override
    public void onStateChange(String newState, boolean connected) {
        testString = testString + "     ---->    " + newState;
        runOnUiThread(() -> stateText.setText(testString));

        if (connected){
            String host = SPUtils.getInstance().getString("host","hostIp");
            runOnUiThread(() -> Toast.makeText(NetSettingActivity.this,"Robot Connected : " + host , Toast.LENGTH_LONG).show());
            Intent intent = new Intent(NetSettingActivity.this, XgoMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else {

            String host = SPUtils.getInstance().getString("host","hostIp");
            int tcpPort = SPUtils.getInstance().getInt("tcpPort",9999);
            int cameraPort = SPUtils.getInstance().getInt("cameraPort",9999);
            runOnUiThread(() -> {
                connectBtn.setEnabled(true);
                Toast.makeText(NetSettingActivity.this,"Connect Fail，please check your network。  host:" + host , Toast.LENGTH_LONG).show();
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketManager.getInstance().setListener(null);
    }
}