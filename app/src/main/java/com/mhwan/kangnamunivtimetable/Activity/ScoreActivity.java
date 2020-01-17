package com.mhwan.kangnamunivtimetable.Activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mhwan.kangnamunivtimetable.Activity.Fragment.CalculateScoreFragment;
import com.mhwan.kangnamunivtimetable.Activity.Fragment.ManageScoreFragment;
import com.mhwan.kangnamunivtimetable.Activity.Fragment.TotalFirstFragment;
import com.mhwan.kangnamunivtimetable.Activity.Fragment.TotalSecondFragment;
import com.mhwan.kangnamunivtimetable.CustomUI.CircleIndicator;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;
import com.mhwan.kangnamunivtimetable.Util.KnuUtil.KnuGrade;

import java.util.HashMap;

public class ScoreActivity extends BaseActivity {
    private KnuGrade.Semester[] semesters;
    private KnuGradeTask task;
    private HashMap<KnuGrade.Semester, KnuGrade.Grade> gradeList;
    private UpdateData total_listener1, total_listener2, total_listener3, manage_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentFromBaseView(R.layout.activity_score);
        setToolbar(R.id.base_toolbar, R.layout.toolbar_main);
        initView();
        getScoreWork();

    }

    class KnuGradeTask extends AsyncTask<Void, Void, Boolean> {
        private KnuGrade knuGrade;

        @Override
        protected void onPreExecute() {
            knuGrade = new KnuGrade(preference.getId(), preference.getCookies());
            if (!AppUtility.getAppinstance().isNetworkConnection()) {
                cancel(true);
            }
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean result = false;
            if (!isCancelled()) {
                gradeList = knuGrade.doGetAllGrade(knuGrade.doGetAvilableSemester());
                return true;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            //프래그먼트에 초기화
            if (aBoolean) {
                //Log.d("ffrra", "result!!");
                total_listener1.updateView(gradeList);
                total_listener2.updateView(gradeList);
                //total_listener3.updateView(gradeList);
                manage_listener.updateView(gradeList);
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(getActivity(), getString(R.string.message_cancel_grade_info), Toast.LENGTH_SHORT).show();
        }
    }

    private void getScoreWork() {
        if (task == null)
            task = new KnuGradeTask();
        task.execute();
    }

    private void initView() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.total_pager);
        TotalPageAdapter totaladapter = new TotalPageAdapter(getSupportFragmentManager(), 2);
        viewPager.setAdapter(totaladapter);
        final CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                indicator.selectDot(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.name_calculate_grade_semester)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.name_passed_semester)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final ViewPager sviewPager = findViewById(R.id.viewpager);
        PagerAdapter sadapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        sviewPager.setAdapter(sadapter);
        sviewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                sviewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        task.cancel(true);
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.name_score);
    }

    @Override
    protected Activity getActivity() {
        return ScoreActivity.this;
    }

    @Override
    protected void hideKeyboard() {
        return;
    }



    private class TotalPageAdapter extends FragmentPagerAdapter {
        FragmentManager fm;
        int mNumOfTabs;

        public TotalPageAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = TotalFirstFragment.newInstance();
                    break;
                case 1:
                    fragment = TotalSecondFragment.newInstance();
                    break;
                default:
                    fragment = null;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            FragmentTransaction bt = fm.beginTransaction();
            bt.remove((Fragment) object);
            bt.commit();
        }
    }

    public class PagerAdapter extends FragmentPagerAdapter {
        int mNumOfTabs;
        FragmentManager fm;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = CalculateScoreFragment.newInstance();
                    break;
                case 1:
                    fragment = ManageScoreFragment.newInstance();
                    break;
                default:
                    fragment = null;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            FragmentTransaction bt = fm.beginTransaction();
            bt.remove((Fragment) object);
            bt.commit();
        }
    }

    public void setTotal_listener1(UpdateData listener) {
        this.total_listener1 = listener;
    }


    public void setTotal_listener2(UpdateData listener) {
        this.total_listener2 = listener;
    }

/*
    public void setTotal_listener3(UpdateData listener) {
        this.total_listener3 = listener;
    }*/

    public void setManage_listener(UpdateData listener) {
        this.manage_listener = listener;
    }

    public interface UpdateData {
        void updateView(HashMap<KnuGrade.Semester, KnuGrade.Grade> gradelist);
    }
}
