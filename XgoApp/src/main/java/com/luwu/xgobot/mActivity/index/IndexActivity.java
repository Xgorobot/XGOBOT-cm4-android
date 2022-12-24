package com.luwu.xgobot.mActivity.index;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.luwu.xgobot.R;
import com.luwu.xgobot.mActivity.main.XgoMainActivity;

/**
 * <p>文件描述：<p>
 * <p>作者：zhangyibin<p>
 * <p>创建时间：2022/8/29<p>
 * 首页
 */
public class IndexActivity extends AppCompatActivity {
    private ImageView xgo_img;
    private TextView goactivity_tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        xgo_img = findViewById(R.id.xgo_img);//xgo图标
        goactivity_tv = findViewById(R.id.index_tv);
        setOnClick();

    }

    private void setOnClick() {
        xgo_img.setOnClickListener(view -> {
            //点击显示搜索框
            Intent intent = new Intent(this, WifiListActivity.class);
            startActivity(intent);
        });

        goactivity_tv.setOnClickListener(v -> {
            //进入首页
            Intent intent = new Intent(this, XgoMainActivity.class);
            startActivity(intent);
        });
    }
}
