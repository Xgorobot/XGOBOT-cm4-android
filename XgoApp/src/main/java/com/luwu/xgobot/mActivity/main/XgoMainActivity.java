package com.luwu.xgobot.mActivity.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.luwu.xgobot.R;
import com.luwu.xgobot.mActivity.NetSettingActivity;
import com.luwu.xgobot.mActivity.aimode.AiModeActivity;
import com.luwu.xgobot.mActivity.control.ControlActivity;

/**
 * <p>文件描述：<p>
 * <p>作者：zhangyibin<p>
 * <p>创建时间：2022/9/04<p>
 */
public class XgoMainActivity extends AppCompatActivity {
    private static final String TAG = "XgoMainActivity";

    private ImageView mSetImg;
    private LinearLayout mShow_layout, mContract_layout, mAimode_layout;
    private ImageView mSettingBtn,mBack_img;

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
        });
        /*mSettingBtn = findViewById(R.id.btn_setting);
        mSettingBtn.setOnClickListener(v -> {
            Intent intent = new Intent(XgoMainActivity.this, SettingActivity.class);
            startActivity(intent);
        });*/
        mSettingBtn = findViewById(R.id.btn_setting);
        mSettingBtn.setOnClickListener(v -> {
            Intent intent = new Intent(XgoMainActivity.this, NetSettingActivity.class);
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
