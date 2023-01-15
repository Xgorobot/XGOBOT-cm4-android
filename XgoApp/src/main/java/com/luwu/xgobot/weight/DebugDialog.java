package com.luwu.xgobot.weight;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

    private Context mContext;

    public DebugDialog(@NonNull Context context) {
        super(context, R.style.ios_style_dialog);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_debug);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = d.widthPixels;
        lp.height = d.heightPixels;
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

}
