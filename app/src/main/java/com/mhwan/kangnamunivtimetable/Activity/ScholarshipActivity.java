package com.mhwan.kangnamunivtimetable.Activity;

import android.app.Activity;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.mhwan.kangnamunivtimetable.Activity.Fragment.NowScheduleFragment;
import com.mhwan.kangnamunivtimetable.Activity.Fragment.PassedScheduleFragment;
import com.mhwan.kangnamunivtimetable.Activity.Fragment.ScholarshipFragment;
import com.mhwan.kangnamunivtimetable.Activity.Fragment.SchoolScheduleFragment;
import com.mhwan.kangnamunivtimetable.Activity.Fragment.TuitionFragment;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AppContext;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;
import com.mhwan.kangnamunivtimetable.Util.KnuUtil.KnuScholarship;
import com.mhwan.kangnamunivtimetable.Util.KnuUtil.KnuTuition;

import java.util.ArrayList;

public class ScholarshipActivity extends BaseActivity {
    private UpdateAllData updateListener1, updateListener2;
    private KnuTuitionTask task;
    private KnuScholarshipTask s_task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentFromBaseView(R.layout.activity_scholarship);
        setToolbar(R.id.base_toolbar, R.layout.toolbar_main);
        initView();
        getTuitionScholarshipWork();
    }

    private void initView(){
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        final ViewPager viewPager = findViewById(R.id.viewpager);
        tabLayout.setupWithViewPager(viewPager);
        ScholarshipPagerAdapter pagerAdapter = new ScholarshipPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
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

    private void getTuitionScholarshipWork(){
        if (task == null)
            task = new KnuTuitionTask();
        if (s_task == null)
            s_task = new KnuScholarshipTask();

        task.execute();
        s_task.execute();
    }

    class KnuScholarshipTask extends AsyncTask<Void, Void, Object> {
        private KnuScholarship knuScholarship;

        @Override
        protected void onPreExecute() {
            knuScholarship = new KnuScholarship(preference.getId(), preference.getCookies());
            if (!AppUtility.getAppinstance().isNetworkConnection()) {
                cancel(true);
            }
        }

        @Override
        protected void onPostExecute(Object object) {
            if (object instanceof Integer) {
                Toast.makeText(AppContext.getContext(), AppContext.getContext().getString(R.string.message_error_invalid_ssl_certificate), Toast.LENGTH_SHORT).show();
            } else {
                ArrayList<KnuScholarship.ScholarshipItem> arrayList = null;
                if (object instanceof ArrayList)
                    arrayList = (ArrayList<KnuScholarship.ScholarshipItem>) object;
                updateListener2.updateScholarshipData(arrayList);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(getContext(), getString(R.string.message_cancel_tuition_scholarship), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Object doInBackground(Void... voids) {
            Object list = knuScholarship.doGetAllScholarship2();
            return list;
        }
    }
    class KnuTuitionTask extends AsyncTask<Void, Void, Object> {
        private KnuTuition knuTuition;

        @Override
        protected void onPreExecute() {
            knuTuition = new KnuTuition(preference.getId(), preference.getCookies());
            if (!AppUtility.getAppinstance().isNetworkConnection()) {
                cancel(true);
            }
        }



        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(getContext(), getString(R.string.message_cancel_tuition_scholarship), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Object arrayList) {
            if (arrayList instanceof Integer){
                Toast.makeText(AppContext.getContext(), AppContext.getContext().getString(R.string.message_error_invalid_ssl_certificate), Toast.LENGTH_SHORT).show();
            } else {
                ArrayList<KnuTuition.TuitionItem> list = null;
                if (arrayList instanceof ArrayList)
                    list = (ArrayList<KnuTuition.TuitionItem>) arrayList;
                updateListener1.updateTuitionData(list);
            }
        }

        @Override
        protected Object doInBackground(Void... voids) {
            Object result = knuTuition.doGetAllTuition();

            return result;
        }
    }

    class ScholarshipPagerAdapter extends FragmentPagerAdapter {
        int mNumOfTabs;
        FragmentManager fm;
        private String[] tabTitles = getResources().getStringArray(R.array.Scholarship_name);

        public ScholarshipPagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.fm = fm;
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = new TuitionFragment();
                    break;
                case 1:
                    fragment = new ScholarshipFragment();
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

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.name_scholarship);
    }

    @Override
    protected Activity getActivity() {
        return ScholarshipActivity.this;
    }

    @Override
    protected void hideKeyboard() {

    }
    @Override
    protected void finishActivity() {
        this.finish();
    }

    public void setUpdateListener1(UpdateAllData listener) {
        this.updateListener1 = listener;
    }
    public void setUpdateListener2(UpdateAllData listener) {
        this.updateListener2 = listener;
    }
    public interface UpdateAllData {
        void updateScholarshipData(ArrayList<KnuScholarship.ScholarshipItem> scholarshipItems);
        void updateTuitionData(ArrayList<KnuTuition.TuitionItem> tuitionItems);
    }
}
