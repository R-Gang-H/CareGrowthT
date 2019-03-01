package com.caregrowtht.app.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;


/**
 * 活动列表 ViewPager的适配器
 * Created by haoruigang on 2018-5-3 12:22:58.
 */

public class ActionListAdapter extends FragmentPagerAdapter {

    private String[] title;
    private List<Fragment> fragments;

    public ActionListAdapter(FragmentManager fm, String[] title, List<Fragment> fragments) {
        super(fm);
        this.title = title;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position % title.length];
    }

    @Override
    public int getCount() {
        return title.length;
    }
}
