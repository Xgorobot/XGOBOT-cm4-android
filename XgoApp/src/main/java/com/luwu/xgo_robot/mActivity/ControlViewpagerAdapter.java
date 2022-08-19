package com.luwu.xgo_robot.mActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.luwu.xgo_robot.mFragment.ButtonFragment;
import com.luwu.xgo_robot.mFragment.PostureFragment;
import com.luwu.xgo_robot.mFragment.RockerFragment;

public class ControlViewpagerAdapter extends FragmentPagerAdapter {
    private final int count = 3;
    private RockerFragment rockerFragment;
    private ButtonFragment buttonFragment;
    private PostureFragment postureFragment;

    public ControlViewpagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        rockerFragment = new RockerFragment();
        buttonFragment = new ButtonFragment();
        postureFragment = new PostureFragment();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = rockerFragment;
                break;
            case 1:
                fragment = buttonFragment;
                break;
            case 2:
                fragment = postureFragment;
                break;
            default:
                fragment = rockerFragment;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return count;
    }
}
