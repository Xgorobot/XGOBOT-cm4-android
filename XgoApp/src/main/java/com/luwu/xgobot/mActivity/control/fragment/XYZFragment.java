package com.luwu.xgobot.mActivity.control.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.LogUtils;
import com.luwu.xgobot.R;
import com.luwu.xgobot.data.RobotFunction;
import com.luwu.xgobot.weight.CenterSeekBar;

/**
 * <p>文件描述：<p>
 * <p>作者：zhangyibin<p>
 * <p>创建时间：2022/9/12<p>
 * 位姿模式
 */
public class XYZFragment extends Fragment {
    private TextView mReset_tv;
    private CenterSeekBar mTranslate_x,mTranslate_y,mTranslate_z,mScroll_x,mScroll_y, mScroll_z;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xyzmode, container, false);
        initView(view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initClick();
    }
    /*
    * 初始化监听
    * 数值初始化
    * */
    private int mTranslatex_value=0,mTranslatey_value=0,mTranslatez_value=0,mScrollx_value=0,mScrolly_value=0,mScrollz_value=0;

    private void initClick() {
        //初始化进度
        mReset_tv.setOnClickListener(v -> {
            mTranslate_x.setProgress(0);
            mTranslate_y.setProgress(0);
            mTranslate_z.setProgress(0);
            mScroll_x.setProgress(0);
            mScroll_y.setProgress(0);
            mScroll_z.setProgress(0);
        });
        /*
        * X轴平移
        * */
        //todo XYZ平移 滚动已分类
        mTranslate_x.setBackgroundColor(this.getResources().getColor(R.color.redca));

        mTranslate_x.setOnSeekBarChangeListener(new CenterSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CenterSeekBar seekBar, int progress, boolean fromUser) {
                mTranslatex_value = progress;
            }

            @Override
            public void onStartTrackingTouch(CenterSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(CenterSeekBar seekBar) {
                updateXYZScroll();

            }
        });
        /*
         * Y轴平移
         * */
        mTranslate_y.setBackgroundColor(this.getResources().getColor(R.color.green12));

        mTranslate_y.setOnSeekBarChangeListener(new CenterSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CenterSeekBar seekBar, int progress, boolean fromUser) {
                mTranslatey_value = progress;
            }

            @Override
            public void onStartTrackingTouch(CenterSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(CenterSeekBar seekBar) {
                updateXYZScroll();

            }
        });
        /*
         * Z轴平移
         * */
        mTranslate_z.setBackgroundColor(this.getResources().getColor(R.color.blue28));

        mTranslate_z.setOnSeekBarChangeListener(new CenterSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CenterSeekBar seekBar, int progress, boolean fromUser) {
                mTranslatez_value = progress;
            }

            @Override
            public void onStartTrackingTouch(CenterSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(CenterSeekBar seekBar) {
                updateXYZScroll();

            }
        });

        /*
         * X轴滚动
         * */
        mScroll_x.setBackgroundColor(this.getResources().getColor(R.color.redca));

        mScroll_x.setOnSeekBarChangeListener(new CenterSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CenterSeekBar seekBar, int progress, boolean fromUser) {
                mScrollx_value = progress;
            }

            @Override
            public void onStartTrackingTouch(CenterSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(CenterSeekBar seekBar) {
                updateXYZScroll();

            }
        });
        /*
         * Y轴滚动
         * */
        mScroll_y.setBackgroundColor(this.getResources().getColor(R.color.green12));
        mScroll_y.setOnSeekBarChangeListener(new CenterSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CenterSeekBar seekBar, int progress, boolean fromUser) {
                mScrolly_value = progress;
            }

            @Override
            public void onStartTrackingTouch(CenterSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(CenterSeekBar seekBar) {
                updateXYZScroll();

            }
        });
        /*
         * Z轴滚动
         * */
        mScroll_z.setBackgroundColor(this.getResources().getColor(R.color.blue28));
        mScroll_z.setOnSeekBarChangeListener(new CenterSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CenterSeekBar seekBar, int progress, boolean fromUser) {
                mScrollz_value = progress;
            }

            @Override
            public void onStartTrackingTouch(CenterSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(CenterSeekBar seekBar) {
                updateXYZScroll();

            }
        });
       /* mScroll_z.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mScrollz_value = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateXYZScroll();
            }
        });*/
    }

    private void updateXYZTranslate(){
//        RobotFunction.servoControl();
    }
    private void updateXYZScroll(){
//        RobotFunction.servoControl();
    }
    /*
    * 初始化view
    * */
    private void initView(View view ) {
        mReset_tv = view.findViewById(R.id.xyz_reset_tv);
        mTranslate_x = view.findViewById(R.id.xyz_translate_x);
        mTranslate_y = view.findViewById(R.id.xyz_translate_y);
        mTranslate_z = view.findViewById(R.id.xyz_translate_z);
        mScroll_x = view.findViewById(R.id.xyz_scroll_x);
        mScroll_y = view.findViewById(R.id.xyz_scroll_y);
        mScroll_z = view.findViewById(R.id.xyz_scroll_z);
    }
}
