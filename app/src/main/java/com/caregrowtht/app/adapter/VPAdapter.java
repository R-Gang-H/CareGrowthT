package com.caregrowtht.app.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1bu2bu-4 on 2016/1/20.
 */
public class VPAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private ArrayList<String> mTitleList;

    public VPAdapter(FragmentManager fm, List<Fragment> list, ArrayList<String> mTitleList) {
        super(fm);
        this.fragmentList = list;
        this.mTitleList = mTitleList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }

    // 刷新fragment
    public void setFragments(FragmentManager fm, ArrayList<Fragment> fragments, ArrayList<String> mTitleList) {
        if (fragments != null) {
            FragmentTransaction ft = fm.beginTransaction();
            for (Fragment f : fragments) {
                ft.remove(f);
            }
            ft.commitAllowingStateLoss();
            fm.executePendingTransactions();
        }
        this.fragmentList = fragments;
        this.mTitleList = mTitleList;
        notifyDataSetChanged();
    }
}
