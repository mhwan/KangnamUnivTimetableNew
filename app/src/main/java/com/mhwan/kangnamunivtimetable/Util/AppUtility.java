package com.mhwan.kangnamunivtimetable.Util;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.util.Base64;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mhwan.kangnamunivtimetable.Items.Days;
import com.mhwan.kangnamunivtimetable.Items.ScheduleItem;
import com.mhwan.kangnamunivtimetable.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

public class AppUtility {
    private static AppUtility Appinstance;
    public static Map<String, String> GradeValueList;

    static {
        Map<String, String> temp = new HashMap<String, String>();
        temp.put("A+", "4.5");
        temp.put("A", "4.0");
        temp.put("B+", "3.5");
        temp.put("B", "3.0");
        temp.put("C+", "2.5");
        temp.put("C", "2.0");
        temp.put("D+", "1.5");
        temp.put("D", "1.0");
        temp.put("F", "0.0");
        GradeValueList = Collections.unmodifiableMap(temp);
    }

    private AppUtility() {
    }

    public synchronized static AppUtility getAppinstance() {
        if (Appinstance == null)
            Appinstance = new AppUtility();
        return Appinstance;
    }

    public String getName(String name) {
        String nameCode = new String(Base64.decode(name, 0));
        try {
            return URLDecoder.decode(nameCode, "utf-8");   // UTF-8
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "noName";
    }

    public void finishApplication(Activity activity) {
        activity.moveTaskToBack(true);
        activity.finish();
    }

    public String getFullLectureRoom(String string) {
        if (string.contains("경")) {
            string = string.replace("경", "경천관 ");
        } else if (string.contains("이")) {
            string = string.replace("이", "이공관 ");
        } else if (string.contains("천")) {
            string = string.replace("천", "천은관 ");
        } else if (string.contains("교")) {
            string = string.replace("교", "교육관 ");
        } else if (string.contains("후")) {
            string = string.replace("후", "후생관 ");
        } else if (string.contains("우")) {
            string = string.replace("우", "우원관 ");
        } else if (string.contains("예")) {
            string = string.replace("예", "예술관 ");
        } else if (string.contains("목")) {
            string = string.replace("목", "목양관 ");
        } else if (string.contains("인")) {
            string = string.replace("인", "인문사회관 ");
        } else if (string.contains("승")) {
            string = string.replace("승", "승리관 ");
        } else if (string.contains("샬")) {
            string = string.replace("샬", "샬롬관 ");
        } else if (string.contains("운")) {
            string = string.replace("운", "운동장 ");
        } else if (string.contains("심산")) {
            string = string.replace("심산", "심전산학관 ");
        } else if (string.contains("본")) {
            string = string.replace("본", "본관 ");
        }
        return string;
    }

    public String readInputStreamToString(HttpsURLConnection connection) {
        String result = null;
        StringBuffer sb = new StringBuffer();
        InputStream is = null;

        try {
            is = new BufferedInputStream(connection.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();
        } catch (Exception e) {
            System.out.print("error");
            result = null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    System.out.print("error");
                }
            }
        }

        return result;
    }

    public JsonObject getStringToJson(String string) {
        JsonObject result = new JsonParser().parse(string).getAsJsonObject();
        return result;
    }
    public Object[] reverseArray(Object[] arr) {
        List<Object> list = Arrays.asList(arr);
        Collections.reverse(list);
        return list.toArray();
    }


    public boolean isNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) AppContext.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public Days getTodayDay() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        return Days.getAllDays()[day - 1];
    }

    public String getDayKorean(Days days) {
        String[] str = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};

        return str[days.getIndex()];
    }

    public String getScoreValue(int i) {
        List<String> scores = Arrays.asList(AppContext.getContext().getResources().getStringArray(R.array.Score_value_array));
        if (i < scores.size() - 1)
            return scores.get(i);
        else
            return null;
    }

    public void hideKeyboard(Activity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            // Ignore exceptions if any
            //Log.e("KeyBoardUtil", e.toString(), e);
        }
    }

    public int getScoreOrder(String str) {
        str = str.toUpperCase();
        List<String> scores = Arrays.asList(AppContext.getContext().getResources().getStringArray(R.array.Score_value_array));
        return scores.indexOf(str);
    }

    public String changeDateTimeFormat(String time, String original_format, String new_format) {
        SimpleDateFormat fOriginal = new SimpleDateFormat(original_format);
        SimpleDateFormat fNew = new SimpleDateFormat(new_format);
        String newtime;
        try {
            Date d = fOriginal.parse(time);
            newtime = fNew.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
            newtime = time;
        }

        return newtime;
    }

    public Calendar getTimeToCalendar(String time, String format) {
        SimpleDateFormat dateFormatformat = new SimpleDateFormat(format);
        Calendar result;
        try {
            Date date = dateFormatformat.parse(time);
            result = Calendar.getInstance();
            result.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
            result = null;
        }

        return result;
    }

    public long getTimeMillisFromStringDate(String time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        long result = -1;
        try {
            date = sdf.parse(time);
            result = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getRemainTime(long timemillis, long nowmillis) {
        System.out.println(timemillis + ", " + nowmillis);
        if (nowmillis >= timemillis)
            return null;
        int remainSecond = (int) (TimeUnit.MILLISECONDS.toSeconds(timemillis - nowmillis));
        int hour = remainSecond / (60 * 60);
        int minute = (remainSecond - (hour * 3600)) / 60;

        StringBuffer buffer = new StringBuffer("남음");

        if (hour > 24) {
            int day = hour / 24;
            hour %= day;
            buffer.insert(0, day + "일 ");
        } else {
            if (minute > 0)
                buffer.insert(0, minute + "분 ");
            if (hour > 0)
                buffer.insert(0, hour + "시간 ");
            if (hour == 0 && minute == 0 && remainSecond > 0)
                buffer.insert(0, "1분 이내로 ");

        }

        System.out.println(buffer);
        return buffer.toString();
    }

    public boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    public String getNowSemester() {
        Calendar now = Calendar.getInstance();
        String year = String.valueOf(now.get(Calendar.YEAR));
        Calendar compare = Calendar.getInstance();
        compare.set(Integer.parseInt(year), Calendar.MARCH, 01, 0, 0);
        compare.set(Calendar.SECOND, 0);
        compare.set(Calendar.MILLISECOND, 0);
        //3월 이전
        if (compare.compareTo(now) > 0)
            return (Integer.parseInt(year) - 1) + "-2";
        else {
            compare.set(Integer.parseInt(year), Calendar.SEPTEMBER, 01);
            if (now.after(compare) || isSameDay(now, compare))
                return year + "-2";
            else
                return year + "-1";
        }
    }

    public int getOnlySemester() {
        Calendar now = Calendar.getInstance();
        String year = String.valueOf(now.get(Calendar.YEAR));
        Calendar compare = Calendar.getInstance();
        compare.set(Integer.parseInt(year), Calendar.MARCH, 01);
        compare.set(Calendar.SECOND, 0);
        compare.set(Calendar.MILLISECOND, 0);

        //3월 이전
        if (now.before(compare))
            return 2;
        else {
            compare.set(Integer.parseInt(year), Calendar.SEPTEMBER, 01);
            if (now.after(compare) || isSameDay(now, compare))
                return 2;
            else
                return 1;
        }
    }

    public void cancelRegisterAlarm(int id) {
        AlarmManager am = (AlarmManager) AppContext.getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AppContext.getContext(), AppReceiver.class);
        intent.setAction(AppUtility.AppBase.ACTION_SCHEDULE);
        //Log.d("to cancel", id+"");
        PendingIntent sender = PendingIntent.getBroadcast(AppContext.getContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (sender != null) {
            //Log.d("alarm", "cancel"+id);
            am.cancel(sender);
            //sender.cancel();
        }
    }

    public boolean reRegisterAlarm(ScheduleItem item) {
        if (item.getAlarmMills() != null) {
            //Log.d("reRegister", item.getId()+"");
            int request = item.getId();
            Intent intent2 = new Intent(AppContext.getContext(), AppReceiver.class);
            intent2.setAction(AppUtility.AppBase.ACTION_SCHEDULE);
            intent2.putExtra(AppUtility.AppBase.ALARM_KEY_TITLE, item.getTitle());
            intent2.putExtra(AppUtility.AppBase.ALARM_KEY_SUBJECT, item.getSubject_name());
            intent2.putExtra(AppUtility.AppBase.ALARM_KEY_DATE, item.getTimemills());
            intent2.putExtra(AppUtility.AppBase.ALARM_KEY_ID, request);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(AppContext.getContext(), request, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) AppContext.getContext().getSystemService(Context.ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, AppUtility.getAppinstance().getTimeMillisFromStringDate(item.getAlarmMills(), AppUtility.AppBase.DATE_FORMAT_DB_SCHEDULED), pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, AppUtility.getAppinstance().getTimeMillisFromStringDate(item.getAlarmMills(), AppUtility.AppBase.DATE_FORMAT_DB_SCHEDULED), pendingIntent);
            }
            return true;
        }

        return false;
    }

    public String getAppVersion() {
        String version = "";
        try {
            PackageInfo i = AppContext.getContext().getPackageManager().getPackageInfo(AppContext.getContext().getPackageName(), 0);
            version = i.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return version;
    }

    public class AppBase {
        public static final String DATE_FORMAT_CREATE_SCHEDULE = "a HH:mm/ yyyy.MM.dd 까지";
        public static final String DATE_FORMAT_DB_SCHEDULED = "yyyy-MM-dd HH:mm";
        public static final String DATE_FORMAT_DB_WRITE = "yyyy-MM-dd HH:mm:ss";
        public static final String DATE_FORMAT_LIST_SCHEDULED = "MM/dd HH:mm 까지";
        public static final String DATE_FORMAT_LIST_WRITE = "MM/dd HH:mm 작성";
        public static final String DATE_FORMAT_SHUTTLBUS = "HH:mm";

        public static final int CREATE_REQUEST_CODE = 0x121;
        public static final int EDIT_SCHEDULE_RESULT_CODE = 0x123;

        public static final String ALARM_KEY_TITLE = "broadcast_key_title";
        public static final String ALARM_KEY_ID = "broadcast_key_id";
        public static final String ALARM_KEY_SUBJECT = "broadcast_key_subject";
        public static final String ALARM_KEY_DATE = "broadcast_key_date";
        public static final String ACTION_SCHEDULE = "Mhwan.action.Schedule";
        public static final String ACTION_SCHEDULE_FINISH = "Mhwan.action.finishSchedule";
    }
}
