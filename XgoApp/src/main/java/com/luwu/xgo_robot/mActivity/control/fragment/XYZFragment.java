package com.luwu.xgo_robot.mActivity.control.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.luwu.xgo_robot.R;

/**
 * <p>文件描述：<p>
 * <p>作者：zhangyibin<p>
 * <p>创建时间：2022/9/12<p>
 * XYZ轴
 */
public class XYZFragment extends Fragment {
    private TextView mReset_tv;
    private ProgressBar mTranslate_x,mTranslate_y,mTranslate_z,mScroll_x,mScroll_y,mScroll_z;
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
    * */
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
        mTranslate_x.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

        });
        /*
         * Y轴平移
         * */
        mTranslate_y.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

        });
        /*
         * Z轴平移
         * */
        mTranslate_z.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

        });
        /*
         * X轴滚动
         * */
        mScroll_x.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

        });
        /*
         * Y轴滚动
         * */
        mScroll_y.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

        });
        /*
         * Z轴滚动
         * */
        mScroll_z.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

        });
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
