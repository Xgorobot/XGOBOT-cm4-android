package com.luwu.xgobot.mActivity;

import static com.luwu.xgobot.mMothed.PublicMethod.localeLanguage;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.ContextThemeWrapper;

import androidx.appcompat.app.AppCompatActivity;

import com.luwu.xgobot.AppContext;
import com.luwu.xgobot.R;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {


    @Override
    protected void attachBaseContext(Context newBase) {
        //你需要切换的语言
//        SpUtils.getSpUtils(MyApplication.getContext(), MyApplication.ACCOUNT_NAME).getStringValue(MyApplication.USER_LANGUAGE, null);
        Locale locale = getLocal();
        Context context = AppLanguageUtils.attachBaseContext(newBase, locale);
        final Configuration configuration = context.getResources().getConfiguration();
        // 此处的ContextThemeWrapper是androidx.appcompat.view包下的
        // 你也可以使用android.view.ContextThemeWrapper，但是使用该对象最低只兼容到API 17
        // 所以使用 androidx.appcompat.view.ContextThemeWrapper省心
        final ContextThemeWrapper wrappedContext = new ContextThemeWrapper(context,
                R.style.AppTheme) {
            @Override
            public void applyOverrideConfiguration(Configuration overrideConfiguration) {
                if (overrideConfiguration != null) {
                    overrideConfiguration.setTo(configuration);
                }
                super.applyOverrideConfiguration(overrideConfiguration);
            }
        };
        super.attachBaseContext(wrappedContext);

    }

    private Locale getLocal(){
        SharedPreferences languageInfo = AppContext.getappContext().getSharedPreferences("xgo_setting", MODE_PRIVATE);
        String setting_language = languageInfo.getString("setting_language", "auto");
        if (setting_language.equals("zh")) {
            localeLanguage = "zh";
            return Locale.CHINESE;
        } else if (setting_language.equals("en")) {
            localeLanguage = "en";
            return Locale.US;
        } else if (setting_language.equals("jp")) {//auto
            localeLanguage = Locale.getDefault().getLanguage();
            return Locale.JAPAN;
        } else {//auto
            localeLanguage = Locale.getDefault().getLanguage();
            return Locale.getDefault();
        }
    }
}
