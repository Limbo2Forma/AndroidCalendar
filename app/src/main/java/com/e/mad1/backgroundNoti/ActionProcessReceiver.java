package com.e.mad1.backgroundNoti;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.e.mad1.ui.OpenDialog;

import java.util.ArrayList;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class ActionProcessReceiver extends BroadcastReceiver {
    private static final String PREFS_NAME = "com.e.mad1.PREFERENCE_FILE_KEY";
    private static final String REMIND = "Notification Remind";
    private static final String NOTIFICATION_SEQUENCE = "Notification Sequence";

    private long eventId;
    private int notificationId;
    private Context context;
    private SharedPreferences preferences;
    private ArrayList<String> notificationIdSequence = new ArrayList<>();

    private void saveSequence(){
        notificationIdSequence.set(notificationId/4,"1");

        StringBuilder stringBuilder = new StringBuilder();
        for (String id:notificationIdSequence){
            stringBuilder.append(",").append(id);
        }
        preferences.edit().putString(NOTIFICATION_SEQUENCE,stringBuilder.toString()).apply();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String command = intent.getStringExtra("action");
        preferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String notificationIds = preferences.getString(NOTIFICATION_SEQUENCE,"1");
        Log.i("check notification","Received noti sequence " + notificationIds);
        notificationIdSequence.addAll(Arrays.asList(notificationIds.split(",")));

        Log.i("check location","received intent " + command);
        String[] id = command.split(",");
        eventId = Long.parseLong(id[1]);
        notificationId = Integer.parseInt(id[2]);
        this.context = context;

        NotificationManager notificationManager
                = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        switch (id[0]) {
            case "remind":
                Toast.makeText(context, "Remind " + Long.toString(eventId), Toast.LENGTH_SHORT).show();
                remind();
                notificationManager.cancel(notificationId);
                break;
            case "cancel":
                Toast.makeText(context, "Cancel " + Long.toString(eventId), Toast.LENGTH_SHORT).show();
                cancel();
                break;
            case "dismiss":
                Toast.makeText(context, "Dismiss " + Long.toString(eventId), Toast.LENGTH_SHORT).show();
                dismiss();
                saveSequence();
                notificationManager.cancel(notificationId);
                break;
        }
    }

    private void remind(){
        Log.i("check notification","clicked: remind");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, CreateNotification.class);
        intent.putExtra("eventID",eventId);
        intent.putExtra("notificationID",notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId + 3,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        int min = preferences.getInt(REMIND,15);
        am.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + 60000 * min,pendingIntent);
    }

    private void cancel(){
        Log.i("check notification","clicked: cancel");
        Intent intent = new Intent(context.getApplicationContext(), OpenDialog.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("eventID",eventId);
        intent.putExtra("notificationID",notificationId);
        context.startActivity(intent);
    }

    private void dismiss(){
        Log.i("check notification","clicked: dismiss");
    }
}
