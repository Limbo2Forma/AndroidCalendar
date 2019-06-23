package com.e.mad1.backgroundNoti;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        // start intentService to execute in background.
        Intent newIntent = new Intent(context, ProcessNotification.class);
        context.startService(newIntent);
    }

    public void setAlarm(Context context,int min) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, -1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // repeat checking every 1 * x min
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000 * min, pendingIntent);
    }

    public void cancelAlarm(Context context) {
        Log.i("check notification","cancel alarm");
        Intent intent = new Intent(context, NotificationAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, -1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
