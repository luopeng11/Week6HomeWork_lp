package com.luopeng.week6homework_lp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.luopeng.week6homework_lp.adapters.MyPagerAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class GuideFragment extends Fragment {


    private ViewPager mViewPager;
    private ArrayList<ImageView> mData = new ArrayList<>();;
    private MyPagerAdapter mAdapter;
    private LinearLayout mLinear;

    public GuideFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_guide, container, false);
        iniData();
        initView(ret);

        return ret;
    }

    private void iniData() {
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.mipmap.slide1);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mData.add(imageView);

        ImageView imageView1 = new ImageView(getContext());
        imageView1.setImageResource(R.mipmap.slide2);
        imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
        mData.add(imageView1);

        ImageView imageView2 = new ImageView(getContext());
        imageView2.setImageResource(R.mipmap.slide3);
        imageView2.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),MainActivity.class));

                SharedPreferences sharedPreferences = getContext().getSharedPreferences("appConfig", Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean("isFirst",false).commit();

                getActivity().finish();
            }
        });
        mData.add(imageView2);
    }
    private int currentPage=0;
    private void initView(View ret) {
        mLinear = (LinearLayout) ret.findViewById(R.id.guide_linear);
        mViewPager = ((ViewPager) ret.findViewById(R.id.guide_viewPager));
        mAdapter = new MyPagerAdapter(mData);
        mViewPager.setAdapter(mAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ImageView childAt = (ImageView) mLinear.getChildAt(currentPage);
                childAt.setImageResource(R.mipmap.page_now);

                ImageView childAt1 = (ImageView) mLinear.getChildAt(position);
                childAt1.setImageResource(R.mipmap.page);
                currentPage=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}
