package com.luwu.xgo_robot.mActivity.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ActivityUtils;
import com.luwu.xgo_robot.R;
import com.luwu.xgo_robot.data.RobotFunction;
import com.luwu.xgo_robot.mActivity.MainActivity;
import com.luwu.xgo_robot.mActivity.SettingActivity;
import com.luwu.xgo_robot.mActivity.aimode.AiModeActivity;
import com.luwu.xgo_robot.mActivity.control.ControlActivity;

/**
 * <p>文件描述：<p>
 * <p>作者：zhangyibin<p>
 * <p>创建时间：2022/9/04<p>
 */
public class XgoMainActivity extends AppCompatActivity {
    private ImageView mSetImg;
    private LinearLayout mShow_layout, mContract_layout, mAimode_layout;
    private ImageButton mSettingBtn;

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
    }
}
