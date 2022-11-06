package com.luwu.xgo_robot.mActivity.control.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luwu.xgo_robot.R;

/**
 * <p>文件描述：<p>
 * <p>作者：zhangyibin<p>
 * <p>创建时间：2022/11/6<p>
 */
public class MotionFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_motionmode, container, false);
        return view;
    }
}
