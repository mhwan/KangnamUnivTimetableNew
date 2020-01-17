package com.mhwan.kangnamunivtimetable.Activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mhwan.kangnamunivtimetable.Database.AppDatabaseHelper;
import com.mhwan.kangnamunivtimetable.Items.Days;
import com.mhwan.kangnamunivtimetable.Items.ScheduleItem;
import com.mhwan.kangnamunivtimetable.Items.TimetableSubject;
import com.mhwan.kangnamunivtimetable.R;
import com.mhwan.kangnamunivtimetable.Util.AppContext;
import com.mhwan.kangnamunivtimetable.Util.AppReceiver;
import com.mhwan.kangnamunivtimetable.Util.AppUtility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class CreateScheduleActivity extends AppCompatActivity implements View.OnClickListener {
    private Calendar scheduled_date = null;
    private boolean isScheduled = false;
    private TextView time_textview, subject_textview, alarm_textview;
    private EditText editText_title, editText_content;
    private RatingBar ratingBar;
    private int rating_value = 0;
    private int mode = 0; //수정일경우 1
    private String selectSubject = null;
    private AppDatabaseHelper helper;
    private ScheduleItem item = null;
    private View alarm_view;
    private int alarm_type = -1; //0 :마감시간에 알림, 1 : 마감 1시간 전 알림, 2 : 마감 12시간 전 알림 3 : 알림 안받음
    private String[] alarmStrings;
    private boolean isEdit = false;

    private TextWatcher editWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            isEdit = true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);
        helper = new AppDatabaseHelper(this);
        if (getIntent().hasExtra("key_schedule_edit")) {
            mode = 1;
            item = getIntent().getParcelableExtra("key_schedule_edit");
        } else if (getIntent().hasExtra("key_has_subject")) {
            mode = 2;
            selectSubject = getIntent().getStringExtra("key_has_subject");
        } else if (getIntent().hasExtra("key_is_alarm")) {
            mode = 1;
            int id = getIntent().getIntExtra("key_alarm_content_id", -1);
            if (id > 0)
                item = helper.getScheduleItem(id);
        }
        alarmStrings = getResources().getStringArray(R.array.Alarm_time_values);

        initToolbar();
        initView();
    }

    private void initView() {
        time_textview = findViewById(R.id.create_time);
        subject_textview = findViewById(R.id.create_subject);
        editText_title = findViewById(R.id.create_title);
        editText_content = findViewById(R.id.create_content);
        editText_title.addTextChangedListener(editWatcher);
        editText_content.addTextChangedListener(editWatcher);
        alarm_view = findViewById(R.id.alarm_frame);
        alarm_textview = findViewById(R.id.create_subject_real_alarm_time);
        ratingBar = findViewById(R.id.create_priority);
        ratingBar.setFocusable(false);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                isEdit = true;
                rating_value = (int) rating;
            }
        });
        time_textview.setOnClickListener(this);
        subject_textview.setOnClickListener(this);
        alarm_textview.setOnClickListener(this);

        if (mode == 1) {
            editText_title.setText(item.getTitle());
            editText_content.setText(item.getContent());
            ratingBar.setRating(item.getPriority());

            alarm_type = item.getAlarm_type();

            if (alarm_type >= 0 && item.getTimemills() != null) {
                showAlarmText();
            }

            //과목, 일정
            if (item.getSubject_name() != null) {
                selectSubject = item.getSubject_name();
                subject_textview.setText(selectSubject);
            }
            if (item.getTimemills() != null) {
                isScheduled = true;
                scheduled_date = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat(AppUtility.AppBase.DATE_FORMAT_DB_SCHEDULED);
                try {
                    scheduled_date.setTime(dateFormat.parse(item.getTimemills()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                time_textview.setText(AppUtility.getAppinstance().changeDateTimeFormat(item.getTimemills(), AppUtility.AppBase.DATE_FORMAT_DB_SCHEDULED, AppUtility.AppBase.DATE_FORMAT_CREATE_SCHEDULE));
            }
        } else if (mode == 2) {
            subject_textview.setText(selectSubject);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_create);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        View toolbarview = LayoutInflater.from(this).inflate(R.layout.toolbar_write, null);
        ((TextView) toolbarview.findViewById(R.id.toolbar_write_title)).setText(getToolbarTitle());
        toolbarview.findViewById(R.id.toolbar_write_close).setOnClickListener(this);
        toolbarview.findViewById(R.id.toolbar_write_add).setOnClickListener(this);

        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getSupportActionBar().setCustomView(toolbarview, layoutParams);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_subject:
                openSelectionDialog();
                break;
            case R.id.create_time:
                openDateTimePicker();
                break;
            case R.id.create_subject_real_alarm_time:
                openSelectAlarmDialog();
                break;
            case R.id.toolbar_write_close:
                checkClose();
                break;
            case R.id.toolbar_write_add:
                if (checkValidData(false))
                    writeNew();
        }
    }


    private int findindex(String[] subject_arr) {
        if (selectSubject != null)
            return Arrays.asList(subject_arr).indexOf(selectSubject);
        return 0;
    }

    private void checkClose() {
        if (isEdit || !checkValidData(true)) {
            setResult(RESULT_CANCELED);
            finish();
        } else
            showCloseCheckDialog();
    }

    private void showCloseCheckDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.message_really_close))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean checkValidData(boolean toFinish) {
        if (editText_content.getText().toString().length() == 0) {
            if (!toFinish)
                Toast.makeText(this, getString(R.string.message_no_content), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (editText_title.getText().toString().length() == 0) {
            if (!toFinish)
                Toast.makeText(this, getString(R.string.message_no_title), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (scheduled_date != null && scheduled_date.before(new Date(System.currentTimeMillis()))) {
            if (!toFinish)
                Toast.makeText(this, getString(R.string.message_invalid_schedule_time), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        checkClose();
    }

    private void writeNew() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(AppUtility.AppBase.DATE_FORMAT_DB_WRITE);
        if (item == null)
            item = new ScheduleItem();
        item.setSubject_name(selectSubject);
        item.setWriteTime(dateFormat.format(new Date(System.currentTimeMillis())));
        item.setFinish(false);
        item.setPriority(rating_value);
        item.setSemester(AppUtility.getAppinstance().getNowSemester());
        item.setTitle(getScheduleItemTitle());
        item.setContent(editText_content.getText().toString());
        dateFormat.applyPattern(AppUtility.AppBase.DATE_FORMAT_DB_SCHEDULED);
        if (!isScheduled)
            item.setTimemills(null);
        else
            item.setTimemills(dateFormat.format(scheduled_date.getTime()));

        Calendar newAlarm = getRealAlarmTime();
        if (alarm_type >= 0) {
            item.setAlarm_type(alarm_type);
            if (alarm_type != 3)
                item.setAlarmMills(dateFormat.format(newAlarm.getTime()));
            else {
                item.setAlarmMills(null);
            }
        }

        int request = -1;
        if (mode == 1) {
            helper.editItems(item.getId(), item);
            request = item.getId();

            if (alarm_type == 3)
                AppUtility.getAppinstance().cancelRegisterAlarm(request);

            setResult(AppUtility.AppBase.EDIT_SCHEDULE_RESULT_CODE);
        } else {
            request = helper.addNewScheduleItem(item);
            setResult(RESULT_OK);
        }

        if (isScheduled)
            setAlarmManager(request, newAlarm);

        finish();
    }

    private void setAlarmManager(int request, Calendar alarmTime) {
        if (alarmTime == null)
            return;

        Log.d("alarmregister", request + "");

        Intent intent = new Intent(AppContext.getContext(), AppReceiver.class);
        intent.setAction(AppUtility.AppBase.ACTION_SCHEDULE);
        intent.putExtra(AppUtility.AppBase.ALARM_KEY_ID, request);
        intent.putExtra(AppUtility.AppBase.ALARM_KEY_TITLE, item.getTitle());
        intent.putExtra(AppUtility.AppBase.ALARM_KEY_SUBJECT, item.getSubject_name());
        intent.putExtra(AppUtility.AppBase.ALARM_KEY_DATE, item.getTimemills());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AppContext.getContext(), request, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) AppContext.getContext().getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
        }

        Log.d("register", "alarm!!");
    }

    private Calendar getRealAlarmTime() {
        Calendar newAlarmTime = null;
        if (alarm_type >= 0 && scheduled_date != null && alarm_type < 3) {
            newAlarmTime = Calendar.getInstance();
            newAlarmTime.setTime(scheduled_date.getTime());
            int reduce = 0;
            switch (alarm_type) {
                case 1:
                    reduce = -1;
                    break;
                case 2:
                    reduce = -12;
                    break;
            }
            newAlarmTime.add(Calendar.HOUR, reduce);
        }

        return newAlarmTime;
    }

    private String getScheduleItemTitle() {
        String title = editText_title.getText().toString();
        if (title.length() > 0)
            return title;
        else {
            //내용을 파싱해서 던져주면됨
            title = editText_content.getText().toString();
        }
        return title;
    }

    private String[] getSubjectList() {
        List<String> list = new LinkedList<>(Arrays.asList(helper.getSubjectList()));
        list.add(0, getString(R.string.no_subject));
        return list.toArray(new String[list.size()]);
    }

    private void openSelectionDialog() {
        isEdit = true;
        final String[] listItems = getSubjectList();
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle(getString(R.string.choose_subject));
        mBuilder.setSingleChoiceItems(listItems, findindex(listItems), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i != 0)
                    selectSubject = listItems[i];
                subject_textview.setText(listItems[i]);

                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void openSelectAlarmDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle(getString(R.string.select_alarm));
        mBuilder.setSingleChoiceItems(alarmStrings, alarm_type, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alarm_type = i;
                showAlarmText();
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();

    }

    private void openDateTimePicker() {
        if (scheduled_date == null)
            scheduled_date = Calendar.getInstance();
        final Calendar now = Calendar.getInstance();

        isEdit = true;
        if (selectSubject != null) {
            TimetableSubject subject = helper.getTimeTableItem(selectSubject);
            if (subject != null) {
                getNearDate(subject.getDay(), now, subject.getStartTime());
            }
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, final int year,
                                  final int monthOfYear, final int dayOfMonth) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateScheduleActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        scheduled_date.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);
                        setTimeText(scheduled_date.getTime());
                        showAlarmText();
                    }
                }, scheduled_date.get(Calendar.HOUR_OF_DAY), scheduled_date.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        }, scheduled_date.get(Calendar.YEAR), scheduled_date.get(Calendar.MONTH), scheduled_date.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showAlarmText() {
        if (alarm_view.getVisibility() == View.GONE)
            alarm_view.setVisibility(View.VISIBLE);
        if (alarm_type < 0)
            alarm_type = SettingActivity.SettingsFragment.getDefaultAlarmTime(this);
        alarm_textview.setText(alarmStrings[alarm_type]);
    }

    private String getToolbarTitle() {
        return (mode != 1) ? getString(R.string.create_schedule) : getString(R.string.edit_schedule);
    }

    private void setTimeText(Date date) {
        isScheduled = true;
        SimpleDateFormat format = new SimpleDateFormat(AppUtility.AppBase.DATE_FORMAT_CREATE_SCHEDULE, Locale.US);
        time_textview.setText(format.format(date));
        //return format.format(date);
    }

    /**
     * 가장 가까운날로
     * 다음주일경우
     *
     * @param days
     * @param starttime
     */
    //과목이 선택됬을때 가장 가까운 수업하는 날을 선택해줌
    private void getNearDate(Days days, Calendar calendar, String starttime) {
        scheduled_date.setTimeInMillis(System.currentTimeMillis());
        int y = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        String[] time = starttime.split(":");
        int dis = days.getIndex() - y;
        if (dis > 0) {
            //이번주 아직 해당 요일이 안됨
            scheduled_date.add(Calendar.DATE, dis);
            scheduled_date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
            scheduled_date.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        } else if (dis == 0) {
            //당일
            scheduled_date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
            scheduled_date.set(Calendar.MINUTE, Integer.parseInt(time[1]));

            Calendar now = Calendar.getInstance();
            if (scheduled_date.before(now)) {
                scheduled_date.add(Calendar.DATE, dis + 7);
            }
        } else {
            //다음주
            dis += 7;
            scheduled_date.add(Calendar.DATE, dis);
            scheduled_date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
            scheduled_date.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        }
    }
}
