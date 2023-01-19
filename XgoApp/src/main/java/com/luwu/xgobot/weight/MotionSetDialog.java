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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.SPUtils;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.luwu.xgobot.AppContext;
import com.luwu.xgobot.R;
import com.luwu.xgobot.data.RobotFunction;

import java.util.Locale;

/**
 * <p>文件描述：<p>
 * <p>作者：zhangyibin<p>
 * <p>创建时间：2022/11/6<p>
 */
public class MotionSetDialog extends Dialog {
    private static final String TAG = "DebugDialog";

//    private Context mContext;
    private TextView mLow_tv, mNormal_tv, mHeight_tv;
    private SegmentTabLayout mlayout;
    private RelativeLayout mMain_layout;

    Switch tuoluoyi;
    SeekBar robotHeight,robotStep;
    TextView textHeight,textStep;
    ImageView resetHeight,resetStep;

    public MotionSetDialog(@NonNull Context context) {
        super(context, R.style.ios_style_dialog);
//        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_motionset);
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
        initListener();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    private void initListener() {
        mMain_layout.setOnClickListener(v -> dismiss());
        mlayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int i) {
                switch (i){
                    case 0:
                        RobotFunction.setSpeed(1);
                        SPUtils.getInstance().put("speed",60);
                        break;
                    case 1:
                        RobotFunction.setSpeed(2);
                        SPUtils.getInstance().put("speed",80);
                        break;
                    case 2:
                        RobotFunction.setSpeed(3);
                        SPUtils.getInstance().put("speed",100);
                        break;
                }
            }

            @Override
            public void onTabReselect(int i) {

            }
        });
        tuoluoyi.setOnCheckedChangeListener((buttonView, isChecked) -> RobotFunction.autoBalance(isChecked));
        robotHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textHeight.setText(String.valueOf(progress));
                RobotFunction.heightControl(75 + progress * 2 / 5);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        robotStep.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textStep.setText(String.valueOf(progress));
                RobotFunction.setStepLength(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        resetStep.setOnClickListener(v -> {
            robotStep.setProgress(60);
        });

        resetHeight.setOnClickListener(v -> {
            robotHeight.setProgress(60);
        });
    }

    String[] titles = {"低速", "中速", "高速"};
    String[] titlesEnglish = {"Low", "Normal", "High"};

    private void initView() {
        mlayout = findViewById(R.id.dialog_tablayout);
        mMain_layout = findViewById(R.id.debug_main);
        Locale local = getContext().getResources().getConfiguration().locale;
        if("CN".equals(local.getCountry())){
            mlayout.setTabData(titles);
        }else {
            mlayout.setTabData(titlesEnglish);
        }
        tuoluoyi = findViewById(R.id.tuoluoyi);
        robotHeight = findViewById(R.id.robot_height);
        robotStep = findViewById(R.id.robot_step);
        textHeight = findViewById(R.id.text_height);
        textStep = findViewById(R.id.text_step);
        resetHeight = findViewById(R.id.iv_reset_height);
        resetStep = findViewById(R.id.iv_reset_step);
    }
}
