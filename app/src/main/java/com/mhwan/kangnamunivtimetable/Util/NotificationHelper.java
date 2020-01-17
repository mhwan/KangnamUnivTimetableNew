package com.mhwan.kangnamunivtimetable.Util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.mhwan.kangnamunivtimetable.Activity.CreateScheduleActivity;
import com.mhwan.kangnamunivtimetable.Activity.ScheduleActivity;
import com.mhwan.kangnamunivtimetable.Activity.SettingActivity;
import com.mhwan.kangnamunivtimetable.R;

import java.util.Map;

public class NotificationHelper {
    private static final String NOTI_CHANNEL = "과제 및 일정 알림 설정";
    private static final String CHANNEL_ID = "CHANNEL_SCHEDULE";
    private static NotificationChannel channel;
    private static final int NOTIFY_ID = 0x67;
    private Context context;

    public NotificationHelper(Context context) {
        this.context = context;
    }

    public void showNotification(Map<String, Object> map) {
        if (!SettingActivity.SettingsFragment.getIsShowAlarm(context))
            return;

        int id = (int) map.get("id");
        Intent backintent = new Intent(context, ScheduleActivity.class);
        Intent frontintent = new Intent(context, CreateScheduleActivity.class);
        frontintent.putExtra("key_is_alarm", true);
        frontintent.putExtra("key_alarm_content_id", id);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(ScheduleActivity.class);
        taskStackBuilder.addNextIntent(backintent);
        taskStackBuilder.addNextIntent(frontintent);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent actionIntent = new Intent(context, AppReceiver.class);
        actionIntent.setAction(AppUtility.AppBase.ACTION_SCHEDULE_FINISH);
        actionIntent.putExtra("contentID", id);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(context, id, actionIntent, 0);

        //Log.d("actionIntent", id+"");
        String subject = (map.get("subject") == null) ? "" : "(" + map.get("subject") + ")";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setLights(Color.CYAN, 500, 2000)
                .setContentTitle(map.get("title") + subject + " " + context.getString(R.string.message_soon_finished_schedule))
                .setContentText(context.getString(R.string.message_to_detail))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(0, context.getString(R.string.schedule_finish),
                        snoozePendingIntent);

        if (SettingActivity.SettingsFragment.getIsVibrate(context))
            builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});


        getManager(context).notify(id, builder.build());
    }

    public static void createNotificationChannel(Context context) {
        if (channel == null)
            createChannel(context);

    }

    private static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannelGroup group1 = new NotificationChannelGroup("channel_group_id", "과제 및 일정관리 알림");
            getManager(context).createNotificationChannelGroup(group1);

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, NOTI_CHANNEL, importance);
            channel.setDescription(context.getString(R.string.message_notify_helper_description));
            channel.setGroup("channel_group_id");
            channel.setLightColor(Color.CYAN);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            getManager(context).createNotificationChannel(channel);
        }
    }

    public static android.app.NotificationManager getManager(Context context) {
        return (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static void deleteChannel(Context context, String channel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getManager(context).deleteNotificationChannel(channel);
        }
    }
}
