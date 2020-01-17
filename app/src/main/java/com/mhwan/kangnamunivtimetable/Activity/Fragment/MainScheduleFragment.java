package com.mhwan.kangnamunivtimetable.Activity.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mhwan.kangnamunivtimetable.Activity.ScheduleActivity;
import com.mhwan.kangnamunivtimetable.Database.AppDatabaseHelper;
import com.mhwan.kangnamunivtimetable.Items.ScheduleItem;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;

import java.util.ArrayList;
import java.util.Calendar;

import static com.mhwan.kangnamunivtimetable.Util.AppContext.getContext;

public class MainScheduleFragment extends Fragment implements View.OnClickListener {
    private ArrayList<ScheduleItem> schedulelist;
    private AppDatabaseHelper databaseHelper;
    private View view;
    private LinearLayout scheduleFrame;

    public MainScheduleFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (databaseHelper == null)
            databaseHelper = new AppDatabaseHelper(getContext());
        schedulelist = databaseHelper.getApproachScheduleItems(null, 3);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_schedule, container, false);
        initView();
        return view;
    }

    private void initView() {
        scheduleFrame = view.findViewById(R.id.main_approach_schedule_frame);
        TextView no_data_text = view.findViewById(R.id.main_no_schedule);
        if (schedulelist != null) {
            scheduleFrame.setVisibility(View.VISIBLE);
            no_data_text.setVisibility(View.GONE);
            int i = 0;
            int last_index = schedulelist.size() - 1;
            for (ScheduleItem item : schedulelist) {
                View listview = LayoutInflater.from(getActivity()).inflate(R.layout.ui_item_schedule_approach, scheduleFrame, false);
                View background = listview.findViewById(R.id.approach_bg);
                ((TextView) listview.findViewById(R.id.approach_title)).setText(item.getTitle());
                ((TextView) listview.findViewById(R.id.approach_enddate)).setText(getSubjectName(item.getSubject_name(), item.getSubject_color_id()));
                TextView remainText = listview.findViewById(R.id.approach_remain);
                if (item.getTimemills() != null) {
                    remainText.setVisibility(View.VISIBLE);
                    Calendar calendar = Calendar.getInstance();
                    String text = AppUtility.getAppinstance().getRemainTime(AppUtility.getAppinstance().getTimeToCalendar(item.getTimemills(), AppUtility.AppBase.DATE_FORMAT_DB_SCHEDULED).getTimeInMillis(), calendar.getTimeInMillis());
                    if (text != null)
                        remainText.setText(text);
                    else
                        remainText.setText(getString(R.string.passed_schedule));
                } else
                    remainText.setVisibility(View.GONE);

                listview.findViewById(R.id.list_divider).setVisibility((i == last_index) ? View.GONE : View.VISIBLE);
                background.setTag(i);
                background.setOnClickListener(this);

                scheduleFrame.addView(listview);
                i++;
            }
        } else {
            no_data_text.setVisibility(View.VISIBLE);
            scheduleFrame.setVisibility(View.GONE);
        }
    }

    private String getSubjectName(String subjectName, int color) {
        if (subjectName == null)
            return getString(R.string.no_subject);
        else {
            if (color == -1)
                return subjectName + " " + getString(R.string.no_timetable);
            else
                return subjectName;
        }
    }

    @Override
    public void onClick(View v) {
        ScheduleItem item = schedulelist.get((Integer) v.getTag());

        Intent intent = new Intent(getActivity(), ScheduleActivity.class);
        intent.putExtra("forward_create", item);

        startActivity(intent);

    }
}
