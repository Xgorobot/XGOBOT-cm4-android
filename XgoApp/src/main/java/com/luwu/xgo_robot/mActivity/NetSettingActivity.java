package com.luwu.xgo_robot.mActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.SPUtils;
import com.luwu.xgo_robot.R;
import com.luwu.xgo_robot.socket.SocketManager;
import com.luwu.xgo_robot.socket.SocketStateListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NetSettingActivity extends AppCompatActivity implements SocketStateListener {
    private static final String TAG = "NetSettingActivity";
    private EditText ipEdit,portEdit,cameraPortEdit;
    private TextView stateText;
    private Button connectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        ipEdit = findViewById(R.id.edit_host);
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

        if (isCorrectIp(hostIp) && tcpPort>0 && cameraPort > 0){
            SPUtils.getInstance().put("host",hostIp);
            SPUtils.getInstance().put("tcpPort",tcpPort);
            SPUtils.getInstance().put("cameraPort",cameraPort);
            connect(hostIp,tcpPort);
        }
    }


    private void connect(String hostIp,int port){
        Log.d(TAG, "connect host: " + hostIp + "   port:" + port);
        SocketManager socketManager = SocketManager.getInstance();
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
    public void onStateChange(String newState) {
        testString = testString + "     ---->    " + newState;
        runOnUiThread(() -> stateText.setText(testString));

    }
}