package com.mhwan.kangnamunivtimetable.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mhwan.kangnamunivtimetable.Activity.Fragment.NowScheduleFragment;
import com.mhwan.kangnamunivtimetable.Activity.Fragment.PassedScheduleFragment;
import com.mhwan.kangnamunivtimetable.Activity.Fragment.SchoolScheduleFragment;
import com.mhwan.kangnamunivtimetable.Callback.ActivityResultListener;
import com.mhwan.kangnamunivtimetable.Callback.OrderChangeListener;
import com.mhwan.kangnamunivtimetable.Items.ScheduleItem;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;


public class ScheduleActivity extends BaseActivity {
    private int orderType = 0;
    private OrderChangeListener listener;
    private ImageView ic_order;
    private ActivityResultListener activityResultListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra("forward_create")) {
            ScheduleItem item = getIntent().getParcelableExtra("forward_create");
            Intent intent = new Intent(this, CreateScheduleActivity.class);
            intent.putExtra("key_schedule_edit", item);
            startActivityForResult(intent, AppUtility.AppBase.CREATE_REQUEST_CODE);
        } else if (getIntent().hasExtra("forward_create_with_subject")) {
            String subject = getIntent().getStringExtra("forward_create_with_subject");
            Intent intent = new Intent(this, CreateScheduleActivity.class);
            intent.putExtra("key_has_subject", subject);
            startActivityForResult(intent, AppUtility.AppBase.CREATE_REQUEST_CODE);
        }
        setContentFromBaseView(R.layout.activity_schedule);
        setToolbar(R.id.base_toolbar, R.layout.toolbar_order);

        viewInit();
    }

    private void showMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu1:
                        orderType = 0;
                        break;
                    case R.id.menu2:
                        orderType = 1;
                        break;
                    case R.id.menu3:
                        orderType = 2;
                        break;
                    case R.id.menu4:
                        orderType = 3;
                        break;
                }
                item.setChecked(!item.isChecked());
                if (listener != null)
                    listener.onOrderChanged(orderType);
                return true;
            }
        });
        popup.inflate(R.menu.order_menu);
        popup.getMenu().getItem(orderType).setChecked(true);
        popup.show();
    }

    private void viewInit() {
        ic_order = toolbarview.findViewById(R.id.main_toolbar_order);
        ic_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });
        final FloatingActionButton floatingActionButton = findViewById(R.id.button_create);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleActivity.this, CreateScheduleActivity.class);
                startActivityForResult(intent, AppUtility.AppBase.CREATE_REQUEST_CODE);
            }
        });
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        final ViewPager viewPager = findViewById(R.id.viewpager);
        tabLayout.setupWithViewPager(viewPager);
        SchedulePagerAdapter pagerAdapter = new SchedulePagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
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
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    floatingActionButton.show();
                    ic_order.setVisibility(View.VISIBLE);
                } else {
                    floatingActionButton.hide();
                    ic_order.setVisibility(View.GONE);
                }


            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

    }


    class SchedulePagerAdapter extends FragmentPagerAdapter {
        int mNumOfTabs;
        FragmentManager fm;
        private String[] tabTitles = getResources().getStringArray(R.array.Schedule_name);

        public SchedulePagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.fm = fm;
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = NowScheduleFragment.newInstance();
                    break;
                case 1:
                    fragment = SchoolScheduleFragment.newInstance();
                    break;

                case 2:
                    fragment = PassedScheduleFragment.newInstance();
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
        return getString(R.string.name_schedule);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppUtility.AppBase.CREATE_REQUEST_CODE) {
            activityResultListener.onActivityResults(requestCode, resultCode, data);
        }
    }

    @Override
    protected Activity getActivity() {
        return ScheduleActivity.this;
    }

    @Override
    protected void hideKeyboard() {
        AppUtility.getAppinstance().hideKeyboard(getActivity());
    }

    public void setOrderListener(OrderChangeListener listener) {
        this.listener = listener;
    }

    public void setActivityResultListener(ActivityResultListener listener) {
        this.activityResultListener = listener;
    }

}
