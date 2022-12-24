package com.luwu.xgobot.mActivity.control.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luwu.xgobot.R;
import com.luwu.xgobot.data.RobotFunction;

/**
 * <p>文件描述：<p>
 * <p>作者：zhangyibin<p>
 * <p>创建时间：2022/9/12<p>
 * 单腿模式
 */
public class SingleLegFragment extends Fragment {
    private static final String TAG = "SingleLegFragment";
    int selectedLeg = 1;//腿ID：=1左前腿，=2右前腿，=3右后腿，4=左后腿。

    int xValue = 0;
    int yValue = 0;
    int zValue = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_singlelegmode, container, false);
        initView(view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initView(View view){
        RadioGroup group = view.findViewById(R.id.group_radio);
        group.setOnCheckedChangeListener((group1, checkedId) -> {
            Log.d(TAG, "onCheckedChanged: " + checkedId);
            switch (checkedId){
                case R.id.radio_a:
                    selectedLeg = 1;
                    break;
                case R.id.radio_b:
                    selectedLeg = 2;
                    break;
                case R.id.radio_c:
                    selectedLeg = 3;
                    break;
                case R.id.radio_d:
                    selectedLeg = 4;
                    break;
                default:
                    break;
            }
        });
        SeekBar seekX = view.findViewById(R.id.seek_x);
        SeekBar seekY = view.findViewById(R.id.seek_y);
        SeekBar seekZ = view.findViewById(R.id.seek_z);
        seekX.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                xValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekY.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                yValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekZ.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                zValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setProgress();
            }
        });

    }

    private void setProgress() {
        RobotFunction.legControl(selectedLeg,xValue,yValue,zValue);
        Log.d(TAG, "setProgress: " + xValue + "  " + yValue + " " + zValue);
    }
}
