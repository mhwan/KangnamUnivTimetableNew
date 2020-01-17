package com.mhwan.kangnamunivtimetable.Activity.Fragment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mhwan.kangnamunivtimetable.Callback.TimeTableDataListener;
import com.mhwan.kangnamunivtimetable.Database.AppDatabaseHelper;
import com.mhwan.kangnamunivtimetable.Items.Days;
import com.mhwan.kangnamunivtimetable.Items.TimetableSubject;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AccountPreference;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;
import com.mhwan.kangnamunivtimetable.Util.GetTimeTableTask;

import java.util.ArrayList;
import java.util.HashMap;

public class TimeTableFragment extends Fragment implements View.OnClickListener, TimeTableDataListener {

    private View view;
    private RelativeLayout table_rootlayout;
    private TextView empty_textview;
    private TextView[][] timeTable_textview = new TextView[6][10];
    private onTableClickListener listener;
    private ArrayList<TimetableSubject> timetableSubjects;
    private HashMap<Integer, TextView> drawedTableList;
    private int[] colorArray;
    private AppDatabaseHelper dbHelper;
    private int selectId = -1;

    public TimeTableFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_timetable, container, false);
        initView();
        return view;
    }

    private void getTableList() {
        if (dbHelper == null)
            dbHelper = new AppDatabaseHelper(getContext());
        ArrayList<TimetableSubject> subjects = dbHelper.getAllTimeTable();
        if (subjects == null) {
            AccountPreference preference = new AccountPreference(getContext());
            GetTimeTableTask tableTask = new GetTimeTableTask(getContext(), this);
            tableTask.setIdCookie(preference.getId(), preference.getCookies());
            tableTask.execute();
        } else {
            this.timetableSubjects = subjects;
            getViewSize();
        }
    }

    private void initView() {
        colorArray = getResources().getIntArray(R.array.Color_array);
        table_rootlayout = view.findViewById(R.id.time_table_root);

        empty_textview = view.findViewById(R.id.empty_text_timetable);
        Days[] d = Days.getWeekDays();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 6; j++) {
                String tempId = "time_table_";
                if (j == 0)
                    tempId += "time_" + i;
                else {
                    tempId += (d[j - 1].getDesc().toLowerCase() + "_" + i);
                }
                Log.d("id", tempId);
                int viewId = getResources().getIdentifier(tempId, "id", getContext().getPackageName());
                timeTable_textview[j][i] = view.findViewById(viewId);
            }
        }

        getTableList();
    }


    private void getViewSize() {
        if (timetableSubjects != null && timetableSubjects.size() > 0) {
            empty_textview.setVisibility(View.GONE);
            view.findViewById(R.id.time_table_border).setVisibility(View.VISIBLE);
            ViewTreeObserver treeObserver = table_rootlayout.getViewTreeObserver();
            treeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        table_rootlayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        table_rootlayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    int viewWidth = table_rootlayout.getMeasuredWidth();
                    drawViewWithResize(viewWidth);
                }
            });
        } else {
            view.findViewById(R.id.time_table_border).setVisibility(View.GONE);
            empty_textview.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), getString(R.string.message_no_timetable_data), Toast.LENGTH_SHORT).show();
        }
    }

    private void drawViewWithResize(int viewWidth) {
        int timeWidth = 120;
        int newWidth = (viewWidth - timeWidth) / 5;

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 6; j++) {
                ViewGroup.LayoutParams layoutParams = timeTable_textview[j][i].getLayoutParams();
                layoutParams.width = timeWidth;
                if (j > 0) {
                    layoutParams.width = newWidth;
                }

                Log.d("aa", "" + newWidth);
                layoutParams.height = checkDensityHeight();
                timeTable_textview[j][i].setLayoutParams(layoutParams);
                timeTable_textview[j][i].requestLayout();
            }
        }

        changeTodayColor();
        drawTime();

    }

    private void drawTime() {
        drawedTableList = new HashMap<>();
        for (int j = 0; j < timetableSubjects.size(); j++) {
            TimetableSubject subject = timetableSubjects.get(j);
            int i = subject.getDay().getIndex();

            int width = timeTable_textview[i][1].getLayoutParams().width;
            int height = timeTable_textview[i][1].getLayoutParams().height;

            double start = getOrdertoTimeCode(subject.getStartTimeCode());
            double end = getOrdertoTimeCode(subject.getEndTimeCode());
            int x = timeTable_textview[0][0].getLayoutParams().width + (i - 1) * width + i;
            int y = (int) (timeTable_textview[1][0].getLayoutParams().height + ((start - 1) * height) + Math.floor(start));

            height = (int) (height * (end - start + 0.5)) + 2;
            TextView textView = new TextView(getActivity());
            textView.setWidth(width);
            textView.setHeight(height);
            textView.setX(x);
            textView.setY(y);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            textView.setText(subject.getName() + "\n" + subject.getClassRoom());
            textView.setTextColor(Color.WHITE);
            textView.setTag(j);
            textView.setOnClickListener(this);
            textView.setBackgroundColor(colorArray[subject.getColor()]);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // If we're running on Honeycomb or newer, then we can use the Theme's
                // selectableItemBackground to ensure that the View has a pressed state
                TypedValue outValue = new TypedValue();
                getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                textView.setForeground(ContextCompat.getDrawable(getContext(), outValue.resourceId));
            }
            textView.setPadding(7, 5, 1, 1);

            drawedTableList.put(subject.getId(), textView);
            table_rootlayout.addView(textView);
        }

    }

    private double getOrdertoTimeCode(String code) {
        char[] c = code.substring(code.length() - 1).toCharArray();
        int i = Integer.parseInt(code.substring(0, code.length() - 1));

        double a = (c[0] - 'a');
        return i + a / 2;
    }

    private int checkDensityHeight() {
        int dpi = getResources().getDisplayMetrics().densityDpi;
        /*
         * 0 < dpi <= 160  == LDPI == 32
         * 160 < dpi <= 240 == HDPI == 50
         * 240 < dpi <= 320 == XHDPI == 66
         * 320 < dpi <= 480 == XXHDPI == 100
         * 480 < dpi <= 640 == XXXHDPI == 134
         */
        if (0 < dpi && dpi <= DisplayMetrics.DENSITY_MEDIUM) {
            return 28;
        } else if (DisplayMetrics.DENSITY_MEDIUM < dpi && dpi <= DisplayMetrics.DENSITY_HIGH) {
            return 46;
        } else if (DisplayMetrics.DENSITY_HIGH < dpi && dpi <= DisplayMetrics.DENSITY_XHIGH) {
            return 66;
        } else if (DisplayMetrics.DENSITY_XHIGH < dpi && dpi <= DisplayMetrics.DENSITY_XXHIGH) {
            return 96;
        } else if (DisplayMetrics.DENSITY_XXHIGH < dpi && dpi <= DisplayMetrics.DENSITY_XXXHIGH) {
            return 128;
        }
        return 96;
    }

    //오늘 날짜를 하이라이트함, 만약 주말이면 월요일날짜를 하이라이트
    private void changeTodayColor() {
        Days day = AppUtility.getAppinstance().getTodayDay();
        if (day == Days.SATURDAY || day == Days.SUNDAY) {
            day = Days.MONDAY;
        }
        timeTable_textview[day.getIndex()][0].setTextColor(getResources().getColor(R.color.colorPrimaryDarkPressed));
        timeTable_textview[day.getIndex()][0].setTextSize(13);
    }

    public void changeBackgroundColor(int colorid, int id) {
        drawedTableList.get(id).setBackgroundColor(colorArray[colorid]);
        //여기서 리스트를 갱신해줘야함
        if (selectId > -1) {
            timetableSubjects.get(selectId).setColor(colorid);
            selectId = -1;
        }
    }

    @Override
    public void onClick(View v) {
        selectId = (int) v.getTag();
        listener.onTimeTableClicked(timetableSubjects.get(selectId));
    }

    public void setListener(onTableClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDataSet(ArrayList<TimetableSubject> list) {
        dbHelper.addAllTimeTable(list);
        this.timetableSubjects = dbHelper.getAllTimeTable();
        getViewSize();
    }

    public interface onTableClickListener {
        void onTimeTableClicked(Object o);
    }
}
