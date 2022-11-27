package com.luwu.xgo_robot.mActivity.control.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.luwu.xgo_robot.R;
import com.luwu.xgo_robot.data.RobotConstants;
import com.luwu.xgo_robot.data.RobotFunction;
import com.luwu.xgo_robot.mView.ButtonView;

import static com.luwu.xgo_robot.mView.ButtonView.DOWNPRESS;
import static com.luwu.xgo_robot.mView.ButtonView.LEFTPRESS;
import static com.luwu.xgo_robot.mView.ButtonView.RIGHTPRESS;
import static com.luwu.xgo_robot.mView.ButtonView.UPPRESS;

/**
 * <p>文件描述：<p>
 * <p>作者：zhangyibin<p>
 * <p>创建时间：2022/11/6<p>
 */
public class MotionFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_motionmode, null);
        initView(view);
        mWeb.loadUrl(RobotFunction.getWebUrl(""));
        return view;
    }

    private ButtonView btnView;
    private WebView mWeb;
    private TextView mWalk_tv, mTrot_tv;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListener();

    }

    private void initListener() {
        btnView.setButtonViewListener(new ButtonView.IButtonViewListener() {
            @Override
            public void actionDown(int num) {
                switch (num) {
                    case UPPRESS:
                        RobotFunction.btnControl(1);
                        break;
                    case DOWNPRESS:
                        RobotFunction.btnControl(2);
                        break;
                    case LEFTPRESS:
                        RobotFunction.btnControl(3);
                        break;
                    case RIGHTPRESS:
                        RobotFunction.btnControl(4);
                        break;
                }
            }

            @Override
            public void actionUp(int num) {
                RobotFunction.btnControl(0);
            }
        });
        mWalk_tv.setOnClickListener(v -> {
            mWalk_tv.setTextColor(this.getResources().getColor(R.color.white));
            mWalk_tv.setBackgroundResource(R.drawable.bg_text_blue);
            mTrot_tv.setTextColor(Color.BLACK);
            mTrot_tv.setBackgroundResource(R.drawable.bg_text_white);

        });
        mTrot_tv.setOnClickListener(v -> {
            mWalk_tv.setTextColor(Color.BLACK);
            mWalk_tv.setBackgroundResource(R.drawable.bg_text_white);
            mTrot_tv.setTextColor(this.getResources().getColor(R.color.white));
            mTrot_tv.setBackgroundResource(R.drawable.bg_text_blue);
        });
    }

    private void initView(View view) {
        btnView = view.findViewById(R.id.motionmode_btn_view);
        mWeb = view.findViewById(R.id.motionmode_web);
        WebSettingsConfiguration(mWeb);
        mWalk_tv = view.findViewById(R.id.motionmode_walk_tv);
        mTrot_tv = view.findViewById(R.id.motionmode_trot_tv);
    }

    public void WebSettingsConfiguration(WebView webView) {
        WebSettings webSettings = webView.getSettings();
//如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);
//支持插件
        webSettings.setPluginState(WebSettings.PluginState.OFF);
//设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
//缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webSettings.setAllowContentAccess(true);
    }
}
