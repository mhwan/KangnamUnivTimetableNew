package com.mhwan.kangnamunivtimetable.Util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.mhwan.kangnamunivtimetable.Database.AppDatabaseHelper;
import com.mhwan.kangnamunivtimetable.Items.ScheduleItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AppReceiver extends BroadcastReceiver {
    private static AppReceiver receiver;
    private AppDatabaseHelper db;

    public static synchronized AppReceiver getReceiver() {
        if (receiver == null)
            receiver = new AppReceiver();

        return receiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        openDatabase(context);
        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            ArrayList<ScheduleItem> scheduleList = db.getAllNowScheduleItems(0, false);
            if (scheduleList != null && scheduleList.size() > 0) {
                for (ScheduleItem item : scheduleList) {
                    if (!item.isFinish() && item.getAlarmMills() != null && System.currentTimeMillis() <= AppUtility.getAppinstance().getTimeMillisFromStringDate(item.getAlarmMills(), AppUtility.AppBase.DATE_FORMAT_DB_SCHEDULED)) {
                        int request = item.getId();
                        //Log.d("boot_complete", ""+request);
                        Intent intent2 = new Intent(AppUtility.AppBase.ACTION_SCHEDULE);
                        intent2.putExtra(AppUtility.AppBase.ALARM_KEY_TITLE, item.getTitle());
                        intent2.putExtra(AppUtility.AppBase.ALARM_KEY_SUBJECT, item.getSubject_name());
                        intent2.putExtra(AppUtility.AppBase.ALARM_KEY_DATE, item.getTimemills());
                        intent2.putExtra(AppUtility.AppBase.ALARM_KEY_ID, item.getId());
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(AppContext.getContext(), request, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, AppUtility.getAppinstance().getTimeMillisFromStringDate(item.getAlarmMills(), AppUtility.AppBase.DATE_FORMAT_DB_SCHEDULED), pendingIntent);
                        } else {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, AppUtility.getAppinstance().getTimeMillisFromStringDate(item.getAlarmMills(), AppUtility.AppBase.DATE_FORMAT_DB_SCHEDULED), pendingIntent);
                        }
                    }
                }
            }
        } else if (action.equals(AppUtility.AppBase.ACTION_SCHEDULE)) {
            //Log.d("scheduled!", "!!!!");
            String title = intent.getStringExtra(AppUtility.AppBase.ALARM_KEY_TITLE);
            int id = intent.getIntExtra(AppUtility.AppBase.ALARM_KEY_ID, -1);
            String subject = intent.getStringExtra(AppUtility.AppBase.ALARM_KEY_SUBJECT);
            String date = intent.getStringExtra(AppUtility.AppBase.ALARM_KEY_DATE);

            Map<String, Object> data_map = new HashMap<>();
            data_map.put("id", id);
            data_map.put("title", title);
            data_map.put("subject", subject);
            data_map.put("time", date);


            NotificationHelper helper = new NotificationHelper(context);
            helper.showNotification(data_map);

        } else if (action.equals(AppUtility.AppBase.ACTION_SCHEDULE_FINISH)) {
            int id = intent.getIntExtra("contentID", -1);
            //Log.d("finish _ id", id+"");
            db.changeFinishedItem(id, true);
            NotificationHelper.getManager(context).cancel(id);
            Toast.makeText(context, "해당 일정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openDatabase(Context context) {
        if (db == null)
            db = new AppDatabaseHelper(context);
    }
}
