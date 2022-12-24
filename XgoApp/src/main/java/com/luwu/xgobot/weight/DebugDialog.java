package com.luwu.xgobot.weight;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.SPUtils;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.luwu.xgobot.R;

/**
 * <p>文件描述：<p>
 * <p>作者：zhangyibin<p>
 * <p>创建时间：2022/11/6<p>
 */
public class DebugDialog extends Dialog {
    private Context mContext;
    private TextView mLow_tv, mNormal_tv, mHeight_tv;
    private SegmentTabLayout mlayout;
    private RelativeLayout mMain_layout;
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
                        SPUtils.getInstance().put("speed",60);
                        break;
                    case 1:
                        SPUtils.getInstance().put("speed",80);
                        break;
                    case 2:
                        SPUtils.getInstance().put("speed",100);
                        break;
                }
            }

            @Override
            public void onTabReselect(int i) {

            }
        });
    }

    String[] titles = {"低速", "中速", "高速"};

    private void initView() {
        mlayout = findViewById(R.id.dialog_tablayout);
        mMain_layout = findViewById(R.id.debug_main);
        mlayout.setTabData(titles);
    }
}
