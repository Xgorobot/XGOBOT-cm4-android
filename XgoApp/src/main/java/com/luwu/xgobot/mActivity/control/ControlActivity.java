package com.luwu.xgobot.mActivity.control;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.luwu.xgobot.R;
import com.luwu.xgobot.data.RobotFunction;
import com.luwu.xgobot.mActivity.control.fragment.MotionFragment;
import com.luwu.xgobot.mActivity.control.fragment.SingleLegFragment;
import com.luwu.xgobot.mActivity.control.fragment.XYZFragment;
import com.luwu.xgobot.weight.MotionSetDialog;

import java.util.ArrayList;
import java.util.List;

import me.jessyan.autosize.AutoSizeCompat;

/**
 * <p>文件描述：<p>
 * <p>作者：zhangyibin<p>
 * <p>创建时间：2022/9/08<p>
 */
public class ControlActivity extends AppCompatActivity {
    private Fragment /*advancedFragment,normalFramgent,*/singlegFragment, xyzFragment, motionFragment;
    private TextView mNormal_tv, mAdvance_tv, mSingleg_tv, mXYZ_tv, mMotion_tv;
    private ImageView mDebug_img,mBack_img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlnew);
        initFragment();

        initView();
    }

    private MotionSetDialog mMotionSetDialog;

    private void initView() {
        mDebug_img = findViewById(R.id.controlnew_debug_img);
        mDebug_img.setOnClickListener(v -> {
            if (mMotionSetDialog == null) {
                mMotionSetDialog = new MotionSetDialog(this);
            }
            if (mMotionSetDialog.isShowing()) {
                mMotionSetDialog.dismiss();
            }
            mMotionSetDialog.show();
        });
       /* mNormal_tv = findViewById(R.id.control_normal_tv);
        mNormal_tv.setOnClickListener(v -> {
            FragmentUtils.hideAllShowFragment(normalFramgent);
        });
        mAdvance_tv = findViewById(R.id.control_advance_tv);
        mAdvance_tv.setOnClickListener(v -> {
            FragmentUtils.hideAllShowFragment(advancedFragment);
        });*/

        mSingleg_tv = findViewById(R.id.control_singleg_tv);
        mSingleg_tv.setOnClickListener(v -> {
            initTvColor();
            switchFragment(singlegFragment);
            mSingleg_tv.setBackgroundResource(R.drawable.gradient_blue_bg);
            mSingleg_tv.setTextColor(Color.WHITE);
        });
        mXYZ_tv = findViewById(R.id.control_xyz_tv);
        mXYZ_tv.setOnClickListener(v -> {
            initTvColor();
            switchFragment(xyzFragment);
            mXYZ_tv.setBackgroundResource(R.drawable.gradient_blue_bg);
            mXYZ_tv.setTextColor(Color.WHITE);
        });
        mMotion_tv = findViewById(R.id.control_motion_tv);
        mMotion_tv.setOnClickListener(v -> {
            initTvColor();
            switchFragment(motionFragment);
            mMotion_tv.setBackgroundResource(R.drawable.gradient_blue_bg);
            mMotion_tv.setTextColor(Color.WHITE);
        });

        mMotion_tv.setBackgroundResource(R.drawable.gradient_blue_bg);
        mMotion_tv.setTextColor(Color.WHITE);

        mBack_img= findViewById(R.id.control_back_img);
        mBack_img.setOnClickListener(v -> {
            finish();
        });
    }

    private List<Fragment> mList = new ArrayList<>();

    private void initFragment() {
        singlegFragment = new SingleLegFragment();
        xyzFragment = new XYZFragment();
        motionFragment = new MotionFragment();
       /* mList.add(singlegFragment);
        mList.add(xyzFragment);
        mList.add(motionFragment);
        FragmentUtils.add(getSupportFragmentManager(), motionFragment, R.id.control_fragment, false);
        FragmentUtils.add(getSupportFragmentManager(), singlegFragment, R.id.control_fragment, false);
        FragmentUtils.add(getSupportFragmentManager(), xyzFragment, R.id.control_fragment, false);
        FragmentUtils.hide(motionFragment);
        FragmentUtils.hide(singlegFragment);
        FragmentUtils.hide(xyzFragment);*/
        switchFragment(motionFragment);
        RobotFunction.startCamera(1);
    }


    public void initTvColor() {
        mMotion_tv.setBackgroundResource(R.drawable.shape_bottom_blue1b);
        mMotion_tv.setTextColor(this.getResources().getColor(R.color.gray_8b));

        mSingleg_tv.setBackgroundResource(R.drawable.shape_bottom_blue1b);
        mSingleg_tv.setTextColor(this.getResources().getColor(R.color.gray_8b));

        mXYZ_tv.setBackgroundResource(R.drawable.shape_bottom_blue1b);
        mXYZ_tv.setTextColor(this.getResources().getColor(R.color.gray_8b));

    }

    private Fragment currentFragment=new MotionFragment();
    private void switchFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        if (!targetFragment.isAdded()) {
            transaction
                    .hide(currentFragment)
                    .add(R.id.control_fragment, targetFragment)
                    .commit();
        } else {
            transaction
                    .hide(currentFragment)
                    .show(targetFragment)
                    .commit();
        }
        currentFragment = targetFragment;
    }

    @Override
    public Resources getResources() {
        AutoSizeCompat.autoConvertDensityOfGlobal(super.getResources());
        return super.getResources();
    }
}
