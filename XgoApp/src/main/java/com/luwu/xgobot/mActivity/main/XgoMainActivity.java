package com.luwu.xgobot.mActivity.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.luwu.xgobot.R;
import com.luwu.xgobot.mActivity.ActorActivity;
import com.luwu.xgobot.mActivity.DebugActivity;
import com.luwu.xgobot.mActivity.MainActivity;
import com.luwu.xgobot.mActivity.NetSettingActivity;
import com.luwu.xgobot.mActivity.SettingActivity;
import com.luwu.xgobot.mActivity.aimode.AiModeActivity;
import com.luwu.xgobot.mActivity.control.ControlActivity;
import com.luwu.xgobot.set.SettingNewActivity;

/**
 * <p>文件描述：<p>
 * <p>作者：zhangyibin<p>
 * <p>创建时间：2022/9/04<p>
 */
public class XgoMainActivity extends AppCompatActivity {
    private static final String TAG = "XgoMainActivity";

    private ImageView mSetImg;
    private LinearLayout mShow_layout, mContract_layout, mAimode_layout;
    private ImageView mDebugBtn,mSettingBtn,mBack_img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainnew);

      /*  mSetImg = findViewById(R.id.main_set_img);
        mSetImg.setOnClickListener(v -> {
            //首页设置img
        });*/

        mShow_layout = findViewById(R.id.main_show_layout);
        mShow_layout.setOnClickListener(v -> {
            //跳转表演页面
            Intent intent = new Intent(XgoMainActivity.this, ActorActivity.class);
            startActivity(intent);
        });
        /*mSettingBtn = findViewById(R.id.btn_setting);
        mSettingBtn.setOnClickListener(v -> {
            Intent intent = new Intent(XgoMainActivity.this, SettingActivity.class);
            startActivity(intent);
        });*/
        mDebugBtn = findViewById(R.id.btn_debug);
        SharedPreferences info = getSharedPreferences("xgo_setting", MODE_PRIVATE);
        String setting_develop = info.getString("setting_develop", "no");
        if (setting_develop.equals("yes")) {
            mDebugBtn.setVisibility(View.VISIBLE);
        } else {
            mDebugBtn.setVisibility(View.GONE);
        }
        mDebugBtn.setOnClickListener(v -> {
            Intent intent = new Intent(XgoMainActivity.this, DebugActivity.class);
            startActivity(intent);
        });
        mSettingBtn = findViewById(R.id.btn_setting);
        mSettingBtn.setOnClickListener(v -> {
            Intent intent = new Intent(XgoMainActivity.this, SettingNewActivity.class);
            startActivity(intent);
        });
        mContract_layout = findViewById(R.id.main_contract_layout);
        mContract_layout.setOnClickListener(v -> {
            //跳转控制页面
            Intent intent = new Intent(this, ControlActivity.class);
            startActivity(intent);
        });
        mAimode_layout = findViewById(R.id.main_aimode_layout);
        mAimode_layout.setOnClickListener(v -> {
            //跳转aimode页面
            Intent intent = new Intent(this, AiModeActivity.class);
            startActivity(intent);
        });
        mBack_img = findViewById(R.id.main_back_img);
        mBack_img.setOnClickListener(v -> finish());
//        test();
    }


}
