package com.luwu.xgobot.mActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.luwu.xgobot.R;
import com.luwu.xgobot.mMothed.mToast;
import com.luwu.xgobot.mView.XRadioGroup;

import java.util.Locale;

import static com.luwu.xgobot.mMothed.PublicMethod.hideBottomUIMenu;
import static com.luwu.xgobot.mMothed.PublicMethod.localeLanguage;

public class SettingActivity extends AppCompatActivity {

    private Button settingBtnPhoto;
    private ImageView settingBtnBack;
    private RadioGroup  settingGroupDevel, settingGroupClose, settingGroupLanguage;
    private XRadioGroup settingGroupMode;
    private RadioButton settingRadioMachine, settingRadioCute, settingRadioSimple, settingRadioOld, settingRadioCold, settingRadioWarm;
    private RadioButton settingRadioClose,settingRadioNoClose;
    private RadioButton settingRadioDevelop, settingRadioAntiDevelop;
    private RadioButton settingLanWithSystem, settingLanChinese, settingLanEnglish;
    private Button hexBtn;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        updateLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //RadioButton
        settingRadioMachine = findViewById(R.id.settingRadioMachine);
        settingRadioCute = findViewById(R.id.settingRadioCute);
        settingRadioSimple = findViewById(R.id.settingRadioSimple);
        settingRadioOld = findViewById(R.id.settingRadioOld);
        settingRadioCold = findViewById(R.id.settingRadioCold);
        settingRadioWarm = findViewById(R.id.settingRadioWarm);
        settingRadioClose = findViewById(R.id.settingRadioClose);
        settingRadioNoClose = findViewById(R.id.settingRadioNoClose);
        settingRadioDevelop = findViewById(R.id.settingRadioDevelop);
        settingRadioAntiDevelop = findViewById(R.id.settingRadioAntiDevelop);
//Button
        settingBtnBack = findViewById(R.id.settingBtnBack);
        settingBtnPhoto = findViewById(R.id.settingBtnPhoto);
        //RadioGroup
        settingGroupClose = findViewById(R.id.settingGroupClose);
        settingGroupMode = findViewById(R.id.settingGroupMode);
        settingGroupDevel = findViewById(R.id.settingGroupDevel);
        settingGroupLanguage = findViewById(R.id.settingGroupLanguage);

        //lan
        settingLanWithSystem = findViewById(R.id.settingLanWithSystem);
        settingLanChinese = findViewById(R.id.settingLanChinese);
        settingLanEnglish = findViewById(R.id.settingLanEnglish);

//        hexBtn = findViewById(R.id.hexBtn);

        setSelected();//设置监听之前调用
        setListener();//监听事件

    }



    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu(SettingActivity.this);
    }

    private void setSelected() {
        SharedPreferences info = getSharedPreferences("xgo_setting", MODE_PRIVATE);
        String setting_mode = info.getString("setting_mode", "机械");
        String setting_connect = info.getString("setting_connect", "no");
        String setting_close = info.getString("setting_close","no");
        //language 20210707
        String setting_language = info.getString("setting_language","auto");
        String setting_develop = info.getString("setting_develop", "no");
        String setting_bluetooth = info.getString("setting_bluetooth", "SPP");
        switch (setting_mode) {
            case "机械":
                settingRadioMachine.setChecked(true);
                settingRadioMachine.setTextColor(getResources().getColor(R.color.white));
                break;
            case "可爱":
                settingRadioCute.setChecked(true);
                settingRadioCute.setTextColor(getResources().getColor(R.color.white));
                break;
            case "简约":
                settingRadioSimple.setChecked(true);
                settingRadioSimple.setTextColor(getResources().getColor(R.color.white));
                break;
            case "复古":
                settingRadioOld.setChecked(true);
                settingRadioOld.setTextColor(getResources().getColor(R.color.white));
                break;
            case "冷色调":
                settingRadioCold.setChecked(true);
                settingRadioCold.setTextColor(getResources().getColor(R.color.white));
                break;
            case "暖色调":
                settingRadioWarm.setChecked(true);
                settingRadioWarm.setTextColor(getResources().getColor(R.color.white));
                break;
            default:
                settingRadioMachine.setChecked(true);
                settingRadioMachine.setTextColor(getResources().getColor(R.color.white));
                break;
        }
        switch (setting_close) {
            case "yes":
                settingRadioClose.setChecked(true);
                settingRadioClose.setTextColor(getResources().getColor(R.color.white));
                break;
            case "no":
                settingRadioNoClose.setChecked(true);
                settingRadioNoClose.setTextColor(getResources().getColor(R.color.white));
                break;
            default:
                settingRadioNoClose.setChecked(true);
                settingRadioNoClose.setTextColor(getResources().getColor(R.color.white));
                break;
        }
        switch (setting_language) {
            case "auto":
                settingLanWithSystem.setChecked(true);
                settingLanWithSystem.setTextColor(getResources().getColor(R.color.white));
                break;
            case "zh":
                settingLanChinese.setChecked(true);
                settingLanChinese.setTextColor(getResources().getColor(R.color.white));
                break;
            case "en":
                settingLanEnglish.setChecked(true);
                settingLanEnglish.setTextColor(getResources().getColor(R.color.white));
                break;
            default:
                settingLanChinese.setChecked(true);
                settingLanChinese.setTextColor(getResources().getColor(R.color.white));
                break;
        }
        switch (setting_develop) {
            case "yes":
                settingRadioDevelop.setChecked(true);
                settingRadioDevelop.setTextColor(getResources().getColor(R.color.white));
                break;
            case "no":
                settingRadioAntiDevelop.setChecked(true);
                settingRadioAntiDevelop.setTextColor(getResources().getColor(R.color.white));
                break;
            default:
                settingRadioDevelop.setChecked(true);
                settingRadioDevelop.setTextColor(getResources().getColor(R.color.white));
                break;
        }
    }

    private void setListener() {
        settingBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        settingBtnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (localeLanguage) {
                    case "zh":
                        mToast.show(SettingActivity.this, "功能暂未开放");
                        break;
                    default:
                        mToast.show(SettingActivity.this, "Not open");
                }
            }
        });

        settingGroupMode.setOnCheckedChangeListener(new XRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(XRadioGroup group, int checkedId) {
                SharedPreferences info = getSharedPreferences("xgo_setting", MODE_PRIVATE);
                SharedPreferences.Editor edit = info.edit();
//                setUncheckedColor(0);
                switch (checkedId) {
                    case R.id.settingRadioMachine:
                        edit.putString("setting_mode", "机械");
                        edit.commit();
                        settingRadioMachine.setTextColor(getResources().getColor(R.color.white));
//                        mToast.show(SettingActivity.this, "模式改为机械");
                        break;
                    case R.id.settingRadioCute:
                        edit.putString("setting_mode", "可爱");
                        edit.commit();
                        settingRadioCute.setTextColor(getResources().getColor(R.color.white));
//                        mToast.show(SettingActivity.this, "模式改为可爱");
                        break;
                    case R.id.settingRadioSimple:
                        edit.putString("setting_mode", "简约");
                        edit.commit();
                        settingRadioSimple.setTextColor(getResources().getColor(R.color.white));
//                        mToast.show(SettingActivity.this, "模式改为简约");
                        break;
                    case R.id.settingRadioOld:
                        edit.putString("setting_mode", "复古");
                        edit.commit();
                        settingRadioOld.setTextColor(getResources().getColor(R.color.white));
//                        mToast.show(SettingActivity.this, "模式改为复古");
                        break;
                    case R.id.settingRadioCold:
                        edit.putString("setting_mode", "冷色调");
                        edit.commit();
                        settingRadioCold.setTextColor(getResources().getColor(R.color.white));
//                        mToast.show(SettingActivity.this, "模式改为冷色调");
                        break;
                    case R.id.settingRadioWarm:
                        edit.putString("setting_mode", "暖色调");
                        edit.commit();
                        settingRadioWarm.setTextColor(getResources().getColor(R.color.white));
//                        mToast.show(SettingActivity.this, "模式改为暖色调");
                        break;
                }
            }
        });

        settingGroupDevel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences info = getSharedPreferences("xgo_setting", MODE_PRIVATE);
                SharedPreferences.Editor edit = info.edit();
//                setUncheckedColor(2);
                switch (checkedId) {
                    case R.id.settingRadioDevelop:
                        edit.putString("setting_develop", "yes");
                        edit.apply();
                        settingRadioDevelop.setTextColor(getResources().getColor(R.color.white));
                        mToast.show(SettingActivity.this, "开发者模式");
                        break;
                    case R.id.settingRadioAntiDevelop:
                        edit.putString("setting_develop", "no");
                        edit.apply();
                        settingRadioAntiDevelop.setTextColor(getResources().getColor(R.color.white));
                        mToast.show(SettingActivity.this, "取消开发者模式");
                        break;
                }
            }
        });

        settingGroupClose.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences info = getSharedPreferences("xgo_setting", MODE_PRIVATE);
                SharedPreferences.Editor edit = info.edit();
//                setUncheckedColor(4);
                switch (checkedId) {
                    case R.id.settingRadioClose:
                        edit.putString("setting_close", "yes");
                        edit.commit();
                        settingRadioClose.setTextColor(getResources().getColor(R.color.white));
//                        mToast.show(SettingActivity.this, "关闭蓝牙");
                        break;
                    case R.id.settingRadioNoClose:
                        edit.putString("setting_close", "no");
                        edit.commit();
                        settingRadioNoClose.setTextColor(getResources().getColor(R.color.white));
//                        mToast.show(SettingActivity.this, "不关闭蓝牙");
                        break;
                }
            }
        });

        settingGroupLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences info = getSharedPreferences("xgo_setting", MODE_PRIVATE);
                SharedPreferences.Editor edit = info.edit();
//                setUncheckedColor(5);
                switch (checkedId) {
                    case R.id.settingLanWithSystem:
                        edit.putString("setting_language", "auto");
                        edit.commit();
                        settingLanWithSystem.setTextColor(getResources().getColor(R.color.white));
                        break;
                    case R.id.settingLanChinese:
                        edit.putString("setting_language", "zh");
                        edit.commit();
                        settingLanChinese.setTextColor(getResources().getColor(R.color.white));
                        break;
                    case R.id.settingLanEnglish:
                        edit.putString("setting_language", "en");
                        edit.commit();
                        settingLanEnglish.setTextColor(getResources().getColor(R.color.white));
                        break;
                }
//                updateLocale();
            }
        });
    }

    private void setUncheckedColor(int i) {
        switch (i) {
            case 0:
                settingRadioMachine.setTextColor(getResources().getColor(R.color.programInactive));
                settingRadioCute.setTextColor(getResources().getColor(R.color.programInactive));
                settingRadioSimple.setTextColor(getResources().getColor(R.color.programInactive));
                settingRadioOld.setTextColor(getResources().getColor(R.color.programInactive));
                settingRadioCold.setTextColor(getResources().getColor(R.color.programInactive));
                settingRadioWarm.setTextColor(getResources().getColor(R.color.programInactive));
                break;
            case 2:
                settingRadioDevelop.setTextColor(getResources().getColor(R.color.programInactive));
                settingRadioAntiDevelop.setTextColor(getResources().getColor(R.color.programInactive));
                break;
            case 4:
                settingRadioClose.setTextColor(getResources().getColor(R.color.programInactive));
                settingRadioNoClose.setTextColor(getResources().getColor(R.color.programInactive));
                break;
            case 5:
                settingLanWithSystem.setTextColor(getResources().getColor(R.color.programInactive));
                settingLanChinese.setTextColor(getResources().getColor(R.color.programInactive));
                settingLanEnglish.setTextColor(getResources().getColor(R.color.programInactive));
                break;
        }
    }

    //更新语言设置
    private void updateLocale(){
        SharedPreferences languageInfo = getSharedPreferences("xgo_setting", MODE_PRIVATE);
        String setting_language = languageInfo.getString("setting_language", "auto");
        if (setting_language.equals("zh")) {
            localeLanguage = "zh";
        } else if(setting_language.equals("en")) {
            localeLanguage = "en";
        } else {//auto
            localeLanguage = Locale.getDefault().getLanguage(); //系统语言
            if (!localeLanguage.equals("zh")){
                localeLanguage = "en";
            }
        }
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        //System.out.println("！！！！！应用语言： "+configuration.locale.getLanguage());
        if (configuration.locale.getLanguage() != localeLanguage){
            if (localeLanguage.equals("zh")) {
                configuration.setLocale(Locale.CHINESE); // 设置为中文
            } else {
                configuration.setLocale(Locale.ENGLISH); // 设置为英文
                localeLanguage = "en";
            }
            DisplayMetrics metrics = new DisplayMetrics();
            resources.updateConfiguration(configuration, metrics); // 更新配置文件
            this.recreate();
        } else {

        }
    }
}