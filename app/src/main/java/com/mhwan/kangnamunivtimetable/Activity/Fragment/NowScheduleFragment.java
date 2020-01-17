package com.mhwan.kangnamunivtimetable.Activity.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mhwan.kangnamunivtimetable.Activity.ScheduleActivity;
import com.mhwan.kangnamunivtimetable.Activity.SettingActivity;
import com.mhwan.kangnamunivtimetable.Callback.ActivityResultListener;
import com.mhwan.kangnamunivtimetable.Callback.OrderChangeListener;
import com.mhwan.kangnamunivtimetable.CustomUI.DividerItemDecoration;
import com.mhwan.kangnamunivtimetable.CustomUI.RecyclerSectionItemDecoration;
import com.mhwan.kangnamunivtimetable.CustomUI.ScheduleAdapter;
import com.mhwan.kangnamunivtimetable.Database.AppDatabaseHelper;
import com.mhwan.kangnamunivtimetable.Items.ScheduleItem;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;
import com.mhwan.kangnamunivtimetable.Util.ScreenUtility;

import java.util.ArrayList;


public class NowScheduleFragment extends Fragment implements OrderChangeListener, ActivityResultListener {
    private View view;
    private int[] colorArray;
    private RecyclerView recyclerView;
    private int orderType = 0;
    private ScheduleAdapter scheduleAdapter;
    private RecyclerSectionItemDecoration sectionItemDecoration;
    private AppDatabaseHelper databaseHelper;
    private TextView emptyTextview;
    private SearchView searchView;

    public NowScheduleFragment() {
    }

    public static NowScheduleFragment newInstance() {
        NowScheduleFragment fragment = new NowScheduleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d("status", "oncreate");
        ((ScheduleActivity) getActivity()).setOrderListener(this);
        ((ScheduleActivity) getActivity()).setActivityResultListener(this);
        colorArray = getResources().getIntArray(R.array.Color_array);
        databaseHelper = new AppDatabaseHelper(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Log.d("status", "oncreateview");
        view = inflater.inflate(R.layout.fragment_now_schedule, container, false);
        initView();
        return view;
    }

    private void initView() {
        emptyTextview = view.findViewById(R.id.empty_messages);
        searchView = view.findViewById(R.id.schedule_search_view);
        setSearchViewColor(searchView);

        recyclerView = view.findViewById(R.id.schedule_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        scheduleAdapter = new ScheduleAdapter(getContext(), getList(), colorArray, SettingActivity.SettingsFragment.getFinishedToOrdering(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST, R.drawable.line_divider, getDividerCallback(scheduleAdapter.getList())));
        recyclerView.setAdapter(scheduleAdapter);
        showEmptyMessage();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText))
                    scheduleAdapter.getFilter().filter("");
                else
                    scheduleAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private ArrayList<ScheduleItem> getList() {
        //Log.d("getlist", "aaa");
        return databaseHelper.getAllNowScheduleItems(orderType, SettingActivity.SettingsFragment.getFinishedToOrdering(getContext()));
    }

    private void setSearchViewColor(SearchView searchView) {
        LinearLayout ll = (LinearLayout) searchView.getChildAt(0);
        LinearLayout ll2 = (LinearLayout) ll.getChildAt(2);
        LinearLayout ll3 = (LinearLayout) ll2.getChildAt(1);

        SearchView.SearchAutoComplete autoComplete = searchView.findViewById(R.id.search_src_text);
        ImageView searchCloseButton = (ImageView) ll3.getChildAt(1);
        ImageView searchIcon = searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        ImageView labelView = (ImageView) ll.getChildAt(1);
        autoComplete.setTextSize(TypedValue.COMPLEX_UNIT_PX, ScreenUtility.getInstance().dpToPx(16));
        autoComplete.setHintTextColor(getResources().getColor(R.color.colorLightBlack));
        autoComplete.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));

        searchIcon.getDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        searchCloseButton.getDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        labelView.getDrawable().setColorFilter(getResources().getColor(R.color.colorLightBlack), PorterDuff.Mode.MULTIPLY);
    }

    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final ArrayList<ScheduleItem> list) {
        return new RecyclerSectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection(int position) {
                if (position < 0)
                    return false;
                return position == 0
                        || !list.get(position).getSubject_name().equals(list.get(position - 1).getSubject_name());
            }

            @Override
            public CharSequence getSectionHeader(int position) {
                if (position < 0)
                    return null;
                return list.get(position).getSubject_name();
            }

        };
    }

    private DividerItemDecoration.DividerCallback getDividerCallback(final ArrayList<ScheduleItem> list) {
        return new DividerItemDecoration.DividerCallback() {
            //다음뷰가 섹션이면 트루, 아니면 false
            @Override
            public boolean isSection(int position) {
                if (list.size() - 1 == position)
                    return true;
                if (orderType == 3) {
                    return list.get(position).getSubject_name() != list.get(position + 1).getSubject_name();
                } else {
                    return false;
                }

            }
        };
    }

    private void showEmptyMessage() {
        if (scheduleAdapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyTextview.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextview.setVisibility(View.GONE);
        }
    }

    /**
     * @param i order 0 : 다가오는순, 1 : 작성순, 2: 과목별 3: 기본
     */
    @Override
    public void onOrderChanged(int i) {
        orderType = i;
        refreshOrder(2);
    }

    private void refreshOrder(int type) {
        showEmptyMessage();
        if (orderType == 0 || orderType == 1 || orderType == 2) {
            if (sectionItemDecoration != null) {
                recyclerView.removeItemDecoration(sectionItemDecoration);
                sectionItemDecoration = null;
            }

            scheduleAdapter.refreshAll(orderType, getList(), type);

        } else {
            if (sectionItemDecoration == null) {
                sectionItemDecoration =
                        new RecyclerSectionItemDecoration(ScreenUtility.getInstance().dpToPx(30),
                                false,
                                getSectionCallback(scheduleAdapter.getList()));
                recyclerView.addItemDecoration(sectionItemDecoration);
            }

            scheduleAdapter.refreshAll(orderType, getList(), type);
        }
    }

    @Override
    public void onActivityResults(int requestcode, int resultcode, Intent data) {
        int type = 0;
        if (resultcode == AppUtility.AppBase.EDIT_SCHEDULE_RESULT_CODE)
            type = 1;
        else if (resultcode == Activity.RESULT_CANCELED)
            type = -1;

        refreshOrder(type);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Log.d("status", "ondestroyview");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d("status", "ondestroy");
    }
}
