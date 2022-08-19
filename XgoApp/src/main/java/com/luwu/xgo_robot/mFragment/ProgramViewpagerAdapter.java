package com.luwu.xgo_robot.mFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ProgramViewpagerAdapter extends FragmentPagerAdapter {
    private final int count = 2;
    private ListviewFragment defaultFragment;
    private ListviewFragment selfFragment;
    private ItemClickListener mListener;

    public ProgramViewpagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    public ProgramViewpagerAdapter(@NonNull FragmentManager fm, int behavior, String[] defaultList, String[] selfList) {
        super(fm, behavior);
        defaultFragment = new ListviewFragment(defaultList);
        selfFragment = new ListviewFragment(selfList);
        defaultFragment.setItemListener(new ListviewFragment.IListviewFragmentListener() {
            @Override
            public void onItemClick(int position) {
                if (mListener != null) {
                    mListener.defaultItemClick(position);
                }
            }
        });
        selfFragment.setItemListener(new ListviewFragment.IListviewFragmentListener() {
            @Override
            public void onItemClick(int position) {
                if (mListener != null) {
                    mListener.selfItemClick(position);
                }
            }
        });

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = defaultFragment;
                break;
            case 1:
                fragment = selfFragment;
                break;
            default:
                fragment = defaultFragment;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return count;
    }

    public void setDefaultNoListChioce() {
        this.defaultFragment.setNoListChioce();
    }

    public void setSelfNoListChioce() {
        this.selfFragment.setNoListChioce();
    }

    public void refreshSelfFrgment(String[] selfList) {
        this.selfFragment.setListArray(selfList);
    }

    public void setItemClickListener(ItemClickListener listener) {
        mListener = listener;
    }

    public interface ItemClickListener {
        void defaultItemClick(int position);

        void selfItemClick(int position);
    }
}
