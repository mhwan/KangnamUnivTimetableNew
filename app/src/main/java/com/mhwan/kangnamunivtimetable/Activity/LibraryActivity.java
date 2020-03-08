package com.mhwan.kangnamunivtimetable.Activity;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.mhwan.kangnamunivtimetable.Activity.Fragment.LibrarySearchFragment;
import com.mhwan.kangnamunivtimetable.Activity.Fragment.LibrarySeatFragment;
import com.mhwan.kangnamunivtimetable.R;

public class LibraryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentFromBaseView(R.layout.activity_library);
        setToolbar(R.id.base_toolbar, R.layout.toolbar_main);
        initView();
    }

    private void initView(){
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        final ViewPager viewPager = findViewById(R.id.viewpager);
        tabLayout.setupWithViewPager(viewPager);
        LibraryPagerAdapter pagerAdapter = new LibraryPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    class LibraryPagerAdapter extends FragmentPagerAdapter {
        int numOfTabs;
        FragmentManager fm;
        private String[] tabTitles = getResources().getStringArray(R.array.Library_name);

        public LibraryPagerAdapter(FragmentManager fm, int numOfTabs){
            super(fm);
            this.fm = fm;
            this.numOfTabs = numOfTabs;
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment;
            switch (i) {
                case 0:
                    fragment = LibrarySeatFragment.newInstance();
                    break;
                case 1:
                    fragment = LibrarySearchFragment.newInstance();
                    break;
                default:
                    fragment = null;
            }
            return fragment;
        }


        @Override
        public int getCount() {
            return numOfTabs;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.destroyItem(container, position, object);
            FragmentTransaction bt = fm.beginTransaction();
            bt.remove((Fragment) object);
            bt.commit();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
    @Override
    protected String getToolbarTitle() {
        return getString(R.string.name_activity_library);
    }

    @Override
    protected Activity getActivity() {
        return LibraryActivity.this;
    }

    @Override
    protected void hideKeyboard() {

    }

    @Override
    protected void finishActivity() {
        this.finish();
    }
}
