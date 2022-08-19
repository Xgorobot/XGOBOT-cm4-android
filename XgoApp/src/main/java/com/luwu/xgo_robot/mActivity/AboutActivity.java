package com.luwu.xgo_robot.mActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.luwu.xgo_robot.R;

import static com.luwu.xgo_robot.mMothed.PublicMethod.hideBottomUIMenu;

public class AboutActivity extends AppCompatActivity {

    private ImageButton aboutBtnBack;
    private TextView privacyPolicy;
    private View.OnClickListener onClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.privacyPolicy:
                        Intent intent;
                        intent = new Intent(AboutActivity.this, PrivacyActivity.class);
                        startActivity(intent);
                }
            }
        };
        aboutBtnBack = findViewById(R.id.aboutBtnBack);
        aboutBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        privacyPolicy = findViewById(R.id.privacyPolicy);
        privacyPolicy.setOnClickListener(onClickListener);
    }

    protected void onResume() {
        super.onResume();
        hideBottomUIMenu(AboutActivity.this);
    }
}
