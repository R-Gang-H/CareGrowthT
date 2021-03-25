package com.caregrowtht.app.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.caregrowtht.app.fragment.TeacherHomeFragment;

import java.util.ArrayList;

/**
 * Created by haoruigang on 2018-4-23 14:48:37.
 */

public class OrgFragmentAdapter extends FragmentPagerAdapter {
    ArrayList<TeacherHomeFragment> fragmentList = new ArrayList<>();

    public OrgFragmentAdapter(FragmentManager fm, ArrayList<TeacherHomeFragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    // 刷新fragment
    public void setFragments(FragmentManager fm, ArrayList<TeacherHomeFragment> fragments) {
        if (fragmentList != null) {
            FragmentTransaction ft = fm.beginTransaction();
            for (Fragment f : fragmentList) {
                ft.remove(f);
            }
            ft.commitAllowingStateLoss();
            fm.executePendingTransactions();
        }
        this.fragmentList = fragments;
        notifyDataSetChanged();
    }
}
