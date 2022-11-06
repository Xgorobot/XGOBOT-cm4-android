package com.luwu.xgo_robot.mActivity.control;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.FragmentUtils;
import com.luwu.xgo_robot.R;
import com.luwu.xgo_robot.mActivity.control.fragment.AdvanceedFragment;
import com.luwu.xgo_robot.mActivity.control.fragment.MotionFragment;
import com.luwu.xgo_robot.mActivity.control.fragment.NormalFragment;
import com.luwu.xgo_robot.mActivity.control.fragment.SingleLegFragment;
import com.luwu.xgo_robot.mActivity.control.fragment.XYZFragment;

/**
 * <p>文件描述：<p>
 * <p>作者：zhangyibin<p>
 * <p>创建时间：2022/9/08<p>
 */
public class ControlActivity extends AppCompatActivity {
    private Fragment /*advancedFragment,normalFramgent,*/singlegFragment,xyzFragment,motionFragment;
    private TextView mNormal_tv,mAdvance_tv,mSingleg_tv,mXYZ_tv,mMotion_tv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlnew);

        initFragment();

        initView();
    }

    private void initView() {
        FragmentUtils.hideAllShowFragment(singlegFragment);
       /* mNormal_tv = findViewById(R.id.control_normal_tv);
        mNormal_tv.setOnClickListener(v -> {
            FragmentUtils.hideAllShowFragment(normalFramgent);
        });
        mAdvance_tv = findViewById(R.id.control_advance_tv);
        mAdvance_tv.setOnClickListener(v -> {
            FragmentUtils.hideAllShowFragment(advancedFragment);
        });*/
        mSingleg_tv = findViewById(R.id.control_singleg_tv);
        mSingleg_tv.setOnClickListener(v -> FragmentUtils.hideAllShowFragment(singlegFragment));
        mXYZ_tv = findViewById(R.id.control_xyz_tv);
        mXYZ_tv.setOnClickListener(v -> FragmentUtils.hideAllShowFragment(xyzFragment));
        mMotion_tv = findViewById(R.id.control_motion_tv);
        mMotion_tv.setOnClickListener(v -> FragmentUtils.hideAllShowFragment(motionFragment));
    }


    private void initFragment() {
        singlegFragment = new SingleLegFragment();
        xyzFragment = new XYZFragment();
        motionFragment = new MotionFragment();


        FragmentUtils.addFragment(getSupportFragmentManager(),motionFragment,R.id.control_fragment,false);
        FragmentUtils.addFragment(getSupportFragmentManager(),singlegFragment,R.id.control_fragment,false);
        FragmentUtils.addFragment(getSupportFragmentManager(),xyzFragment,R.id.control_fragment,false);
        FragmentUtils.hideFragment(motionFragment);
        FragmentUtils.hideFragment(singlegFragment);
        FragmentUtils.hideFragment(xyzFragment);
    }
}
