package com.luwu.xgo_robot.mActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.util.DisplayMetrics;

import com.luwu.xgo_robot.R;
import com.luwu.xgo_robot.mMothed.mToast;

import java.util.Locale;

import static com.luwu.xgo_robot.mMothed.PublicMethod.hideBottomUIMenu;
import static com.luwu.xgo_robot.mMothed.PublicMethod.localeLanguage;

//进入App的图片
public class AnimationActivity extends AppCompatActivity {

    final private int timeDelay = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        updateLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(AnimationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();//不再返回该页面
            }
        },timeDelay);

    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu(AnimationActivity.this);
    }

    private void updateLocale(){
        SharedPreferences languageInfo = getSharedPreferences("xgo_setting", MODE_PRIVATE);
        String setting_language = languageInfo.getString("setting_language", "auto");
        if (setting_language.equals("zh")) {
            localeLanguage = "zh";
        } else if(setting_language.equals("en")) {
            localeLanguage = "en";
        } else {//auto
            localeLanguage = Locale.getDefault().getLanguage();
            if (!localeLanguage.equals("zh")){
                localeLanguage = "en";
            }
        }
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        if (configuration.locale.getLanguage() != localeLanguage){
            if (localeLanguage.equals("zh")) {
                configuration.setLocale(Locale.CHINESE); // 设置为中文
            } else {
                configuration.setLocale(Locale.ENGLISH); // 设置为英文
                localeLanguage = "en";
            }
            DisplayMetrics metrics = new DisplayMetrics();
            resources.updateConfiguration(configuration, metrics); // 更新配置文件
        } else {

        }
    }
}
