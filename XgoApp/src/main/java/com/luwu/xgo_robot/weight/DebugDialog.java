package com.luwu.xgo_robot.weight;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.luwu.xgo_robot.R;

/**
 * <p>文件描述：<p>
 * <p>作者：zhangyibin<p>
 * <p>创建时间：2022/11/6<p>
 */
public class DebugDialog extends Dialog {
    private Context mContext;
    private TextView mLow_tv,mNormal_tv,mHeight_tv;
    public DebugDialog(@NonNull Context context) {
        super(context,R.style.ios_style_dialog);
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
        lp.gravity =  Gravity.TOP | Gravity.END;
        lp.y = 50;
        lp.x = 100;
        dialogWindow.setAttributes(lp);
        
        initView();
        initListener();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    private void initListener() {
        mLow_tv.setOnClickListener(v -> {
            SPUtils.getInstance().put("speed",60);
            ToastUtils.showShort("设置成功");
        });

        mNormal_tv.setOnClickListener(v -> {
            SPUtils.getInstance().put("speed",80);
            ToastUtils.showShort("设置成功");
        });

        mHeight_tv.setOnClickListener(v -> {
            SPUtils.getInstance().put("speed",100);
            ToastUtils.showShort("设置成功");
        });
    }

    private void initView() {
        mLow_tv = findViewById(R.id.low_tv);
        mNormal_tv = findViewById(R.id.normal_tv);
        mHeight_tv = findViewById(R.id.height_tv);
    }
}
