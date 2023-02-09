package com.luwu.xgobot.set;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.luwu.xgobot.R;
import com.luwu.xgobot.data.RobotFunction;
import com.luwu.xgobot.mActivity.BaseActivity;
import com.luwu.xgobot.mActivity.NetSettingActivity;
import com.luwu.xgobot.mActivity.SettingActivity;
import com.luwu.xgobot.mMothed.mToast;
import com.luwu.xgobot.socket.SocketManager;
import com.luwu.xgobot.socket.SocketStateListener;

/**
 * <p>文件描述：<p>
 * <p>作者：zhangyibin<p>
 * <p>创建时间：2023/1/8<p>
 *     设置页面
 */
public class SettingNewActivity extends BaseActivity implements SocketStateListener {

    private static final String TAG = "SettingNewActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingnew);

        initView();
        SocketManager.getInstance().setListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketManager.getInstance().setListener(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RobotFunction.loadDeviceVersion();
    }

    private ImageView mBack_img;
    private RadioButton mFolwSys_btn,mChinese_btn,mEnglish_btn,mConfirm_btn,mNo_btn;
    private TextView mDeviceinfo_tv;
    private void initView() {
        SharedPreferences info = getSharedPreferences("xgo_setting", MODE_PRIVATE);
        SharedPreferences.Editor edit = info.edit();

        mBack_img = findViewById(R.id.main_back_img);
        mBack_img.setOnClickListener(v -> {
            finish();
        });

        mFolwSys_btn = findViewById(R.id.setting_flowsys_btn);
        mChinese_btn = findViewById(R.id.setting_chinese_btn);
        mEnglish_btn = findViewById(R.id.setting_english_btn);

        //跟随系统
        mFolwSys_btn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mFolwSys_btn.setTextColor(isChecked?this.getResources().getColor(R.color.white):this.getResources().getColor(R.color.gray_8b));
            if(isChecked){
                mChinese_btn.setChecked(false);
                mEnglish_btn.setChecked(false);
                edit.putString("setting_language", "auto");
                edit.apply();
            }
        });
        //中文
        mChinese_btn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mChinese_btn.setTextColor(isChecked?this.getResources().getColor(R.color.white):this.getResources().getColor(R.color.gray_8b));
            if(isChecked){
                mFolwSys_btn.setChecked(false);
                mEnglish_btn.setChecked(false);
                edit.putString("setting_language", "zh");
                edit.apply();
            }
        });
        //英文
        mEnglish_btn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mEnglish_btn.setTextColor(isChecked?this.getResources().getColor(R.color.white):this.getResources().getColor(R.color.gray_8b));
            if(isChecked){
                mFolwSys_btn.setChecked(false);
                mChinese_btn.setChecked(false);
                edit.putString("setting_language", "en");
                edit.apply();
            }
        });

        mConfirm_btn = findViewById(R.id.setting_yes_btn);
        mNo_btn = findViewById(R.id.setting_no_btn);

        String setting_develop = info.getString("setting_develop", "no");


        switch (setting_develop) {
            case "yes":
                mConfirm_btn.setChecked(true);
                mNo_btn.setChecked(false);
                break;
            case "no":
                mConfirm_btn.setChecked(false);
                mNo_btn.setChecked(true);
                break;
        }

        //是
        mConfirm_btn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mConfirm_btn.setTextColor(isChecked?this.getResources().getColor(R.color.white):this.getResources().getColor(R.color.gray_8b));
            if(isChecked){
                mNo_btn.setChecked(false);
                edit.putString("setting_develop", "yes");
                edit.apply();
            }
        });
        //否
        mNo_btn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mNo_btn.setTextColor(isChecked?this.getResources().getColor(R.color.white):this.getResources().getColor(R.color.gray_8b));
            if(isChecked){
                mConfirm_btn.setChecked(false);

                edit.putString("setting_develop", "no");
                edit.apply();
            }
        });
        //设备信息
        mDeviceinfo_tv = findViewById(R.id.setting_deviceinfo_tv);

    }

    @Override
    public void onStateChange(String newState, boolean connected) {

    }

    @Override
    public void onMsgReceived(String msg) {
        try {
            String tag = msg.substring(3,5);
            String data = msg.substring(7,9);
            String name = "loading";
            Log.d(TAG, "onMsgReceived: tag:" + tag + "  data:" + data );
            if (tag.equals("20")){
                if (data.equals("00")){
                    name = "XGO-lite2";
                }else if (data.equals("01")){
                    name = "XGO-mini2";
                }
            }
            String finalName = name;
            runOnUiThread(() -> mDeviceinfo_tv.setText(finalName));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
