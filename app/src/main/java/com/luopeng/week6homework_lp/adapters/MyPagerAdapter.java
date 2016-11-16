package com.luopeng.week6homework_lp.adapters;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by my on 2016/11/12.
 */
public class MyPagerAdapter extends PagerAdapter {
    private List<ImageView> data;
    public MyPagerAdapter(List<ImageView> data) {
        this.data=data;
    }

    @Override
    public int getCount() {
        return data.size()==3?10000:0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        return super.instantiateItem(container, position);

            Log.d("flag", "---------->instantiateItem: " +position);
        if (data.size()==3) {

            ImageView child = data.get(position % 3);
            ViewParent parent = child.getParent();
            if (parent != null) {
                ViewGroup group= (ViewGroup) parent;
                group.removeView(child);
            }
            container.addView(child);
        }

        return data.size()==3?data.get(position%3):null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        //container.removeView(data.get(position%data.size()));
    }
}
