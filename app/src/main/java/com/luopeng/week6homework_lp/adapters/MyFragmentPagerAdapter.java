package com.luopeng.week6homework_lp.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by my on 2016/11/11.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> data;
    private String[] titles=new String[]{"头条","百科","咨询","经营","数据"};
    public MyFragmentPagerAdapter(List<Fragment> data, FragmentManager manager) {
        super(manager);
        this.data=data;
    }

    @Override
    public Fragment getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data!=null?data.size():0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
