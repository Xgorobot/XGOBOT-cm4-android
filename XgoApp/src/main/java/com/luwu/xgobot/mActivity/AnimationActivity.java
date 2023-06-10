package com.luwu.xgobot.mActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;

import com.luwu.xgobot.R;

import java.util.Locale;

import static com.luwu.xgobot.mMothed.PublicMethod.hideBottomUIMenu;
import static com.luwu.xgobot.mMothed.PublicMethod.localeLanguage;

//进入App的图片
public class AnimationActivity extends AppCompatActivity {

    final private int timeDelay = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        updateLocale();
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

}
