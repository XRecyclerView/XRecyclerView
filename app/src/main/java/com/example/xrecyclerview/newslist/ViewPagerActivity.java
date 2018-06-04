package com.example.xrecyclerview.newslist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.example.xrecyclerview.R;


/**
 * Created by joim on 2018/6/4.
 */

public class ViewPagerActivity extends AppCompatActivity {


    private static final String[] TITLE_ARR = {"头条", "娱乐", "体育", "法律", "政治", "NBA", "音乐", "世界杯", "萌宠", "美女"};

    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.actigvity_view_pager);

        initUI();
    }

    private void initUI() {

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(new FragmentAdapter(this, getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(1);
    }

    private class FragmentAdapter extends FragmentStatePagerAdapter {

        private final Context mContext;

        private final FragmentManager mFragmentManager;

        public FragmentAdapter(Context context, FragmentManager fm) {
            super(fm);
            this.mContext = context;
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = mFragmentManager.findFragmentByTag(TITLE_ARR[position]);
            if (fragment == null) {

                Bundle bundle = new Bundle();
                bundle.putString("title", TITLE_ARR[position]);

                fragment = Fragment.instantiate(mContext, ChannelFragment.class.getName(), bundle);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return TITLE_ARR.length;
        }
    }
}
