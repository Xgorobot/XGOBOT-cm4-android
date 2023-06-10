package com.luwu.xgobot.weight;

import static com.luwu.xgobot.mMothed.PublicMethod.localeLanguage;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.SPUtils;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.luwu.xgobot.R;
import com.luwu.xgobot.data.RobotFunction;

import java.util.Locale;

/**
 * <p>文件描述：<p>
 * <p>作者：zhangyibin<p>
 * <p>创建时间：2022/11/6<p>
 */
public class DebugDialog extends Dialog {
    private static final String TAG = "DebugDialog";

//    private Context mContext;

    public DebugDialog(@NonNull Context context) {
        super(context, R.style.ios_style_dialog);
//        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        updateLocale();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_debug);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        lp.width = screenWidth;
        lp.height = screenHeight;
        Log.d(TAG, "onCreate: width:" + lp.width + " height:" + lp.height);
        dialogWindow.setAttributes(lp);
        initView();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }
    private TextView mConfirm_tv;
    private void initView() {
        mConfirm_tv = findViewById(R.id.debug_confirm_tv);
        mConfirm_tv.setOnClickListener(v -> {
            dismiss();
        });
    }

    private void updateLocale() {
        SharedPreferences languageInfo = getContext().getSharedPreferences("xgo_setting", Context.MODE_PRIVATE);
        String setting_language = languageInfo.getString("setting_language", "auto");
        if (setting_language.equals("zh")) {
            localeLanguage = "zh";
        } else if (setting_language.equals("en")) {
            localeLanguage = "en";
        } else {//auto
            localeLanguage = Locale.getDefault().getLanguage();
            if (!localeLanguage.equals("zh")) {
                localeLanguage = "en";
            }
        }
        Resources resources = getContext().getResources();
        Configuration configuration = resources.getConfiguration();
        if (configuration.locale.getLanguage() != localeLanguage) {
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
