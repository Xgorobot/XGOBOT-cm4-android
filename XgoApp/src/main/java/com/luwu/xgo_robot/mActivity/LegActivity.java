package com.luwu.xgo_robot.mActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.luwu.xgo_robot.Product.Lite;
import com.luwu.xgo_robot.Product.Mini;
import com.luwu.xgo_robot.Product.Product;
import com.luwu.xgo_robot.R;
import com.luwu.xgo_robot.mMothed.PublicMethod;

import java.util.Locale;

import static com.luwu.xgo_robot.mMothed.PublicMethod.hideBottomUIMenu;
import static com.luwu.xgo_robot.mMothed.PublicMethod.localeLanguage;
import static com.luwu.xgo_robot.mMothed.PublicMethod.toOrderRange;

public class LegActivity extends AppCompatActivity {
    private ImageButton legBtnBack;
    private Button legBtnReset;
    private ButtonListener mButtonListener;
    private RadioGroup legRadioGroup;
    private RadioButton legRadio0, legRadio1, legRadio2, legRadio3;
    private int legChioce = 0;//选择哪条腿 0左前 1右前 2右后 3左后
    private long nowTime = 0;
    private long saveTime1 = 0, saveTime2 = 0, saveTime3 = 0;
    private SeekBar legSeekBarX, legSeekBarY, legSeekBarZ;
    private TextView legTextX, legTextY, legTextZ;
    private SeekBarListener mSeekBarListener;
    protected Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        updateLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leg);
        mButtonListener = new ButtonListener();
        legBtnBack = findViewById(R.id.legBtnBack);
        legBtnBack.setOnClickListener(mButtonListener);
        legBtnReset = findViewById(R.id.legBtnReset);
        legBtnReset.setOnClickListener(mButtonListener);
        legRadioGroup = findViewById(R.id.legRadioGroup);
        legRadio0 = findViewById(R.id.legRadio0);
        legRadio1 = findViewById(R.id.legRadio1);
        legRadio2 = findViewById(R.id.legRadio2);
        legRadio3 = findViewById(R.id.legRadio3);
        setRadioGropListener();
        mSeekBarListener = new SeekBarListener();
        legSeekBarX = findViewById(R.id.legSeekBarX);
        legSeekBarY = findViewById(R.id.legSeekBarY);
        legSeekBarZ = findViewById(R.id.legSeekBarZ);
        setSeekBar();
        legSeekBarX.setOnSeekBarChangeListener(mSeekBarListener);
        legSeekBarY.setOnSeekBarChangeListener(mSeekBarListener);
        legSeekBarZ.setOnSeekBarChangeListener(mSeekBarListener);
        legTextX = findViewById(R.id.legTextX);
        legTextY = findViewById(R.id.legTextY);
        legTextZ = findViewById(R.id.legTextZ);
        legSeekBarZ.setProgress(this.product.getLEG_LIMIT_Z_DEFAULT());
//        legVersion = findViewById(R.id.textViewLegInit);
    }

    private void setSeekBar(){
        switch (PublicMethod.XGORAM_VALUE.versions){
            case 0: this.product = new Mini(); break;
            case 1: this.product = new Lite(); break;
            default: this.product = new Mini(); break;
        }

        legSeekBarX.setMax(this.product.getLEG_LIMIT_X());
        legSeekBarY.setMax(this.product.getLEG_LIMIT_Y());
        legSeekBarZ.setMax(this.product.getLEG_LIMIT_Z_MAX());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            legSeekBarX.setMin( - this.product.getLEG_LIMIT_X());
            legSeekBarY.setMin( - this.product.getLEG_LIMIT_Y());
            legSeekBarZ.setMin(this.product.getLEG_LIMIT_Z_MIN());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu(LegActivity.this);
    }

    private void setRadioGropListener() {
        legRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.legRadio0:
                        legChioce = 0;
                        break;
                    case R.id.legRadio1:
                        legChioce = 1;
                        break;
                    case R.id.legRadio2:
                        legChioce = 2;
                        break;
                    case R.id.legRadio3:
                        legChioce = 3;
                        break;
                }
            }
        });
    }

    private class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.legBtnBack:
                    finish();
                    break;
                case R.id.legBtnReset:
                    MainActivity.addMessage(new byte[]{PublicMethod.XGORAM_ADDR.action, (byte)0xff});
                    legSeekBarX.setProgress(0);
                    legSeekBarY.setProgress(0);
                    legSeekBarZ.setProgress(product.getLEG_LIMIT_Z_DEFAULT());
                    break;
            }
        }
    }

    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (seekBar.getId()) {
                case R.id.legSeekBarX:
                    legTextX.setText(String.valueOf(progress));
                    nowTime = System.currentTimeMillis();
                    if ((nowTime - saveTime1) > 200) {//200ms刷新
                        if (legChioce == 0) {
                            MainActivity.addMessage(new byte[]{PublicMethod.XGORAM_ADDR.legX_1, (byte) toOrderRange(progress, - product.getLEG_LIMIT_X(), product.getLEG_LIMIT_X())});
                        } else if (legChioce == 1) {
                            MainActivity.addMessage(new byte[]{PublicMethod.XGORAM_ADDR.legX_2, (byte) toOrderRange(progress, - product.getLEG_LIMIT_X(), product.getLEG_LIMIT_X())});
                        } else if (legChioce == 2) {
                            MainActivity.addMessage(new byte[]{PublicMethod.XGORAM_ADDR.legX_3, (byte) toOrderRange(progress, - product.getLEG_LIMIT_X(), product.getLEG_LIMIT_X())});
                        } else if (legChioce == 3) {
                            MainActivity.addMessage(new byte[]{PublicMethod.XGORAM_ADDR.legX_4, (byte) toOrderRange(progress, - product.getLEG_LIMIT_X(), product.getLEG_LIMIT_X())});
                        }
                        saveTime1 = nowTime;
                    }
                    break;
                case R.id.legSeekBarY:
                    legTextY.setText(String.valueOf(progress));
                    nowTime = System.currentTimeMillis();
                    if ((nowTime - saveTime2) > 200) {//200ms刷新
                        if (legChioce == 0) {
                            MainActivity.addMessage(new byte[]{PublicMethod.XGORAM_ADDR.legY_1, (byte) toOrderRange(progress, - product.getLEG_LIMIT_Y(), product.getLEG_LIMIT_Y())});
                        } else if (legChioce == 1) {
                            MainActivity.addMessage(new byte[]{PublicMethod.XGORAM_ADDR.legY_2, (byte) toOrderRange(progress, - product.getLEG_LIMIT_Y(), product.getLEG_LIMIT_Y())});
                        } else if (legChioce == 2) {
                            MainActivity.addMessage(new byte[]{PublicMethod.XGORAM_ADDR.legY_3, (byte) toOrderRange(progress, - product.getLEG_LIMIT_Y(), product.getLEG_LIMIT_Y())});
                        } else if (legChioce == 3) {
                            MainActivity.addMessage(new byte[]{PublicMethod.XGORAM_ADDR.legY_4, (byte) toOrderRange(progress, - product.getLEG_LIMIT_Y(), product.getLEG_LIMIT_Y())});
                        }
                        saveTime2 = nowTime;
                    }
                    break;
                case R.id.legSeekBarZ:
                    legTextZ.setText(String.valueOf(progress));
                    nowTime = System.currentTimeMillis();
                    if ((nowTime - saveTime3) > 200) {//200ms刷新
                        if (legChioce == 0) {
                            MainActivity.addMessage(new byte[]{PublicMethod.XGORAM_ADDR.legZ_1, (byte) toOrderRange(progress, product.getLEG_LIMIT_Z_MIN(), product.getLEG_LIMIT_Z_MAX())});
                        } else if (legChioce == 1) {
                            MainActivity.addMessage(new byte[]{PublicMethod.XGORAM_ADDR.legZ_2, (byte) toOrderRange(progress, product.getLEG_LIMIT_Z_MIN(), product.getLEG_LIMIT_Z_MAX())});
                        } else if (legChioce == 2) {
                            MainActivity.addMessage(new byte[]{PublicMethod.XGORAM_ADDR.legZ_3, (byte) toOrderRange(progress, product.getLEG_LIMIT_Z_MIN(), product.getLEG_LIMIT_Z_MAX())});
                        } else if (legChioce == 3) {
                            MainActivity.addMessage(new byte[]{PublicMethod.XGORAM_ADDR.legZ_4, (byte) toOrderRange(progress, product.getLEG_LIMIT_Z_MIN(), product.getLEG_LIMIT_Z_MAX())});
                        }
                        saveTime3 = nowTime;
                    }
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
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
        }
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        if (configuration.locale.getLanguage() != localeLanguage){
            if (localeLanguage.equals("zh")) {
                configuration.setLocale(Locale.CHINESE); // 设置为中文
            } else {
                configuration.setLocale(Locale.ENGLISH); // 设置为英文
            }
            DisplayMetrics metrics = new DisplayMetrics();
            resources.updateConfiguration(configuration, metrics); // 更新配置文件
        } else {
            if (!configuration.locale.getLanguage().equals("zh") & !configuration.locale.getLanguage().equals("en")){
                configuration.setLocale(Locale.ENGLISH); // 设置为英文
            }
        }
    }
}