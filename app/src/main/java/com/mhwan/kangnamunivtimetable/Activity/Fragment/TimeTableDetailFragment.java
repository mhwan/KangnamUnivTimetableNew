package com.mhwan.kangnamunivtimetable.Activity.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mhwan.kangnamunivtimetable.Activity.MapActivity;
import com.mhwan.kangnamunivtimetable.Activity.ScheduleActivity;
import com.mhwan.kangnamunivtimetable.CustomUI.ChangeColorDialog;
import com.mhwan.kangnamunivtimetable.CustomUI.CircleView;
import com.mhwan.kangnamunivtimetable.Database.AppDatabaseHelper;
import com.mhwan.kangnamunivtimetable.Items.ScheduleItem;
import com.mhwan.kangnamunivtimetable.Items.TimetableSubject;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class TimeTableDetailFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    private TimetableSubject subject;
    private ArrayList<ScheduleItem> schedulelist;
    private TextView subject_name, subject_classroom, subject_time, no_data_view;
    private CircleView circleView;
    private int[] colorArray;
    private TableChangeColorListener listener;
    private String fullLectureName;
    private LinearLayout schedule_frame;

    public static TimeTableDetailFragment getInstance() {
        return new TimeTableDetailFragment();
    }

    public void setObject(TimetableSubject subject, ArrayList schedulelist) {
        this.subject = subject;
        this.schedulelist = schedulelist;
    }

    public void setListener(TableChangeColorListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ui_detail_timetable, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        colorArray = getResources().getIntArray(R.array.Color_array);
        view.findViewById(R.id.bottom_add_button).setOnClickListener(this);
        subject_name = view.findViewById(R.id.bottom_name);
        subject_classroom = view.findViewById(R.id.bottom_classroom);
        subject_time = view.findViewById(R.id.bottom_time);
        circleView = view.findViewById(R.id.bottom_circle_view);
        schedule_frame = view.findViewById(R.id.bottom_schedule);
        no_data_view = view.findViewById(R.id.bottom_no_data);
        view.findViewById(R.id.bottom_change_color).setOnClickListener(this);
        view.findViewById(R.id.bottom_location).setOnClickListener(this);
        setData();
    }

    private void setData() {
        fullLectureName = AppUtility.getAppinstance().getFullLectureRoom(subject.getClassRoom());
        subject_name.setText(subject.getName());
        subject_classroom.setText(fullLectureName);
        subject_time.setText(AppUtility.getAppinstance().getDayKorean(subject.getDay()) + " | " + subject.getStartTime() + " ~ " + subject.getEndTime() + "(" + subject.getStartTimeCode() + "~" + subject.getEndTimeCode() + ")");
        circleView.setCircleBackgroundColor(colorArray[subject.getColor()]);

        if (schedulelist != null) {
            no_data_view.setVisibility(View.GONE);
            schedule_frame.setVisibility(View.VISIBLE);

            int i = 0;
            int last_index = schedulelist.size() - 1;
            for (ScheduleItem item : schedulelist) {
                View listview = LayoutInflater.from(getActivity()).inflate(R.layout.ui_item_schedule_approach, schedule_frame, false);
                View background = listview.findViewById(R.id.approach_bg);
                ((TextView) listview.findViewById(R.id.approach_title)).setText(item.getTitle());
                TextView remainText = listview.findViewById(R.id.approach_remain);
                if (item.getTimemills() == null) {
                    ((TextView) listview.findViewById(R.id.approach_enddate)).setText(AppUtility.getAppinstance().changeDateTimeFormat(item.getWriteTime(), AppUtility.AppBase.DATE_FORMAT_DB_WRITE, AppUtility.AppBase.DATE_FORMAT_LIST_WRITE));
                    remainText.setVisibility(View.GONE);
                } else {
                    ((TextView) listview.findViewById(R.id.approach_enddate)).setText(AppUtility.getAppinstance().changeDateTimeFormat(item.getTimemills(), AppUtility.AppBase.DATE_FORMAT_DB_SCHEDULED, AppUtility.AppBase.DATE_FORMAT_LIST_SCHEDULED));
                    Calendar calendar = Calendar.getInstance();
                    String text = AppUtility.getAppinstance().getRemainTime(AppUtility.getAppinstance().getTimeToCalendar(item.getTimemills(), AppUtility.AppBase.DATE_FORMAT_DB_SCHEDULED).getTimeInMillis(), calendar.getTimeInMillis());
                    remainText.setVisibility(View.VISIBLE);
                    if (text != null)
                        remainText.setText(text);
                    else
                        remainText.setText(getString(R.string.passed_schedule));
                }

                //listview.findViewById(R.id.list_divider).setVisibility((i == last_index)? View.GONE : View.VISIBLE);
                background.setTag(i);
                background.setOnClickListener(this);

                schedule_frame.addView(listview);
                i++;
            }
        }
    }

    private void changeColor(int id) {
        AppDatabaseHelper dbHelper = new AppDatabaseHelper(getContext());
        dbHelper.updateColor(subject.getId(), id);
        subject.setColor(id);
        circleView.setCircleBackgroundColor(colorArray[id]);
        listener.onChangeColor(subject.getId(), id);

        Toast.makeText(getContext(), getString(R.string.message_change_timetable_color), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom_location:
                Intent intent = new Intent(getContext(), MapActivity.class);
                intent.putExtra("loc_index", getIndex(fullLectureName));
                startActivity(intent);
                this.dismiss();
                getActivity().finish();
                break;
            case R.id.bottom_change_color:
                final ChangeColorDialog dialog = new ChangeColorDialog(getContext());
                dialog.setListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeColor(dialog.getCheckid());
                        dialog.dismiss();
                    }
                });
                dialog.setCheckid(subject.getColor());
                dialog.show();
                break;
            case R.id.bottom_add_button:
                Intent intent1 = new Intent(getContext(), ScheduleActivity.class);
                intent1.putExtra("forward_create_with_subject", subject.getName());
                startActivity(intent1);
                this.dismiss();
                getActivity().finish();
                break;
            case R.id.approach_bg:
                ScheduleItem item = schedulelist.get((Integer) v.getTag());

                Intent intent2 = new Intent(getActivity(), ScheduleActivity.class);
                intent2.putExtra("forward_create", item);

                startActivity(intent2);
                this.dismiss();
                getActivity().finish();
        }
    }

    private int getIndex(String str) {
        String loc = str.substring(0, str.indexOf(" "));
        String[] locations = getContext().getResources().getStringArray(R.array.Building_name_array);
        List<String> arr = Arrays.asList(locations);

        if (arr.contains(loc))
            return arr.indexOf(loc);
        return -1;
    }

    public interface TableChangeColorListener {
        void onChangeColor(int id, int color);
    }
}
