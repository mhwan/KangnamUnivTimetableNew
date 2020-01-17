package com.mhwan.kangnamunivtimetable.Activity.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AppContext;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;
import com.mhwan.kangnamunivtimetable.Util.ScreenUtility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ShuttleBusFragment extends Fragment {
    private static final String JSON_SHUTTLEBUS_VALUE = "{\"data_1\":[\"08:10\", \"08:15\", \"08:20\", \"08:25\", \"08:30\", \"08:35\", \"08:40\", \"08:45\", \"08:50\",  \"08:55\", \"09:00\", \"09:10\", \"09:30\", \"09:55\", \"10:20\", \"10:45\", \"11:10\" , \"11:35\", \"12:00\", \"12:50\", \"13:15\", \"13:40\" , \"14:05\", \"14:30\", \"14:55\", \"15:20\", \"15:45\", \"16:10\", \"16:35\", \"17:00\", \"17:25\", \"17:50\", \"18:15\", \"18:40\", \"19:05\"], \"data_2\":[\"08:20\", \"08:25\", \"08:30\", \"08:35\", \"08:40\", \"08:45\", \"08:50\", \"08:55\", \"09:00\", \"09:05\", \"09:10\", \"09:20\", \"09:40\", \"10:05\", \"10:30\", \"10:55\", \"11:20\", \"11:45\", \"13:00\", \"13:25\", \"13:50\", \"14:15\", \"14:40\" , \"15:05\", \"15:30\", \"15:55\", \"16:20\", \"16:45\", \"17:10\", \"17:35\", \"18:00\", \"18:25\", \"18:50\", \"19:15\"], \"data_3\":[\"08:10\", \"08:30\", \"08:50\", \"09:10\"], \"data_4\":[\"08:20\", \"08:40\", \"09:00\"]}";
    private HashMap<String, ArrayList> shuttlebusList;
    private String[] shuttlebus_destinationlist;
    private View view;
    private LinearLayout frame_shuttlbus_extra, button_shuttlebus_arrow;
    private boolean isTimelate = false, is_expanded = false;
    private Calendar now_calendar, temp_calendar;
    private HashMap<Integer, ArrayList> textView_time;
    private List<Integer> keylist;
    private boolean isloop = true;

    public ShuttleBusFragment() {
        // Log.d("init", "shuttlbus");
        shuttlebus_destinationlist = AppContext.getContext().getResources().getStringArray(R.array.Bus_type);
        initData();
    }

    private void initData() {
        shuttlebusList = new HashMap<>();
        JsonObject object = AppUtility.getAppinstance().getStringToJson(JSON_SHUTTLEBUS_VALUE);
        for (int i = 0; i < 4; i++) {
            JsonArray array = object.getAsJsonArray("data_" + (i + 1));
            ArrayList<String> list = new ArrayList();
            for (int j = 0; j < array.size(); j++) {
                list.add(array.get(j).toString().replace("\"", ""));
            }
            shuttlebusList.put(shuttlebus_destinationlist[i], list);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shuttlebus, container, false);

        initView();

        return view;
    }

    private void initView() {
        final ImageView ic_arrow = view.findViewById(R.id.ic_shuttlebus_arrow);
        frame_shuttlbus_extra = view.findViewById(R.id.shuttlbus_extra_frame);
        button_shuttlebus_arrow = view.findViewById(R.id.bg_shuttlebus_arrow);
        button_shuttlebus_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTimelate) {
                    if (is_expanded) {
                        collapse(frame_shuttlbus_extra);
                        ic_arrow.animate().rotation(360).start();
                    } else {
                        expand(frame_shuttlbus_extra);
                        ic_arrow.animate().rotation(180).start();
                    }
                    is_expanded = !is_expanded;
                }

            }
        });

        resizeView();
    }

    /**
     * 현재 시간을 갖고오고 10시 이전이라면 4개의 데이터를 보여주고, 이후라면 2개만 보여준다
     * 뷰 사이즈를 통해 채울수 있는 데이터 수를 갖고옴
     * 현재 시간을 받아오고 다가오는 시간의 데이터를 그 수만큼 텍스트뷰에 넣어줌
     */

    private void resizeView() {
        final ViewGroup viewGroup = view.findViewById(R.id.ui_item_0);
        final LinearLayout root = viewGroup.findViewById(R.id.shuttlebus_item_time_frame);
        final TextView textView = viewGroup.findViewById(R.id.shuttlbus_item_0);
        ViewTreeObserver viewTreeObserver = root.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    root.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int viewWidth = root.getMeasuredWidth();
                int textWidth = textView.getMeasuredWidth();
                viewResized(viewWidth, textWidth);
            }
        });
    }

    private void viewResized(int root_width, int text_width) {
        int count = (root_width) / text_width;
        getTodayTime();
        textView_time = new HashMap<>();
        drawViewWithResize(0, count);

        if (!isTimelate) {
            button_shuttlebus_arrow.setVisibility(View.VISIBLE);
            drawViewWithResize(2, count);

        } else {
            button_shuttlebus_arrow.setVisibility(View.GONE);
        }


        Runnable runnable = new CurrentTimeRunner();
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void drawViewWithResize(int start, int count) {
        String layoutid = "ui_item_";

        for (int i = start; i < start + 2; i++) {
            ArrayList<TextView> viewlist = new ArrayList<>();
            int viewId = getResources().getIdentifier(layoutid + i, "id", getContext().getPackageName());
            ViewGroup viewGroup = view.findViewById(viewId);
            TextView title = viewGroup.findViewById(R.id.shuttlebus_item_title);
            TextView item1 = viewGroup.findViewById(R.id.shuttlbus_item_0);
            title.setText(shuttlebus_destinationlist[i]);

            String[] value = getAroundTimeList(count, shuttlebus_destinationlist[i]);
            if (value.length == 0) {
                item1.setText(getString(R.string.message_no_bus));
            } else {
                item1.setText(value[0]);
                viewlist.add(item1);
                if (value.length > 1)
                    drawTextItem((ViewGroup) viewGroup.findViewById(R.id.shuttlebus_item_time_frame), value, viewlist);
            }

            textView_time.put(i, viewlist);
        }
    }

    private String[] getAroundTimeList(int count, String destination) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> list = shuttlebusList.get(destination);
        Calendar temp_cal = Calendar.getInstance();
        int find = -1;

        if (isWeekDay(temp_cal)) {
            for (int i = 0; i < list.size(); i++) {
                String[] tempTime = list.get(i).split(":");
                temp_cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tempTime[0]));
                temp_cal.set(Calendar.MINUTE, Integer.parseInt(tempTime[1]));
                if (now_calendar.before(temp_cal)) {
                    find++;
                    if (find >= count)
                        break;
                    result.add(list.get(i));
                }
            }
        }

        return result.toArray(new String[result.size()]);
    }

    private boolean isWeekDay(Calendar temp_calendar) {
        int week = temp_calendar.get(Calendar.DAY_OF_WEEK);
        return week != Calendar.SATURDAY && week != Calendar.SUNDAY;
    }

    private void drawTextItem(ViewGroup frame, String[] values, ArrayList<TextView> viewList) {
        for (int j = 1; j < values.length; j++) {
            TextView textView = new TextView(getActivity());
            textView.setText(values[j]);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            textView.setTextColor(getResources().getColor(R.color.colorLightBlack));
            textView.setPadding(0, 0, ScreenUtility.getInstance().dpToPx(14), 0);

            if (j == values.length - 1) {
                textView.setSingleLine(true); //한줄로 나오게 하기.
            }
            viewList.add(textView);

            frame.addView(textView);
        }

    }

    private void getTodayTime() {
        if (now_calendar == null)
            now_calendar = Calendar.getInstance();

        Calendar ten_clock = Calendar.getInstance();
        ten_clock.set(now_calendar.get(Calendar.YEAR), now_calendar.get(Calendar.MONTH), now_calendar.get(Calendar.DAY_OF_MONTH), 9, 11);
        if (now_calendar.after(ten_clock))
            isTimelate = true;
    }

    public void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private class CurrentTimeRunner implements Runnable {
        @Override
        public void run() {
            //Log.d("loop", isloop+"");
            while (!Thread.currentThread().isInterrupted() && textView_time != null && isloop && textView_time.size() > 0) {
                try {
                    //쓰레드 내에서 ui를 바꿀수 있는 쓰레드
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findNearText();
                            //Log.d("aaa", "asdf");
                        }
                    });
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private long getTimeMillis(String timetext) {
        String[] temp = timetext.split(":");
        temp_calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(temp[0]));
        temp_calendar.set(Calendar.MINUTE, Integer.parseInt(temp[1]));

        return temp_calendar.getTimeInMillis();
    }

    private void findNearText() {
        if (textView_time.size() > 0) {
            if (keylist == null)
                keylist = new ArrayList<Integer>(textView_time.keySet());
            if (temp_calendar == null)
                temp_calendar = Calendar.getInstance();

            for (Integer i : keylist) {
                ArrayList<TextView> temps = textView_time.get(i);
                if (temps.size() > 0) {
                    TextView temp_textview = temps.get(0);
                    long diff = getTimeMillis(temp_textview.getText().toString()) - System.currentTimeMillis();
                    //Log.d("diff", diff + " - " + i);
                    //차이가 0보다 작다면 지난시간이므로 검사
                    if (temps.size() > 2 && diff < 0) {
                        temp_textview.setTextColor(ContextCompat.getColor(AppContext.getContext(), R.color.colorLightBlack));
                        for (int j = 1; j < temps.size(); j++) {
                            temp_textview = temps.get(j);
                            diff = getTimeMillis(temp_textview.getText().toString()) - System.currentTimeMillis();

                            if (diff > 0)
                                break;
                        }
                    }
                    temp_textview.setTextColor(ContextCompat.getColor(AppContext.getContext(), R.color.colorPrimary));
                    System.out.print(temp_textview.getText().toString());
                }
            }
        }
    }

    public void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    @Override
    public void onStop() {
        super.onStop();
        isloop = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isloop = true;
    }
}
