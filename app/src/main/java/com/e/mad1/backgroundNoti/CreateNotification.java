package com.e.mad1.backgroundNoti;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.e.mad1.App;
import com.e.mad1.DataRepository;
import com.e.mad1.R;
import com.e.mad1.database.Entity.EventEntity;

public class CreateNotification extends BroadcastReceiver implements DataRepository.getEventAsync.AsyncResponse {
    private Context context;
    private int notificationId;

    public void setContext(Context context) { this.context = context; }

    private PendingIntent pi(String action, int uniqueIntentId, long eventId, int notificationID) {
        Intent intent = new Intent(context.getApplicationContext(), ActionProcessReceiver.class);
        String command = action + "," + eventId + "," + notificationID;
        intent.putExtra("action", command);
        return PendingIntent.getBroadcast(context, uniqueIntentId, intent, PendingIntent.FLAG_ONE_SHOT);
    }

    NotificationCompat.Builder buildNotification(EventEntity event, int id) {
        long eventId = event.getId();
        Log.i("check notification", "show noti id " + id + " of eventID: " + eventId);
        return new NotificationCompat.Builder(context, "Notify")
                .setContentTitle(event.getTitle())
                .setCategory(Notification.CATEGORY_REMINDER)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText(event.getStartDate().toString())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(0, context.getString(R.string.remind), pi("remind", id, eventId, id))
                .addAction(0, context.getString(R.string.cancel), pi("cancel", id + 1, eventId, id))
                .addAction(0, context.getString(R.string.dismiss), pi("dismiss", id + 2, eventId, id));
    }

    @Override
    public void processFinish(EventEntity event) {
        if (event != null) {
            NotificationCompat.Builder builder = buildNotification(event, notificationId);
            NotificationManagerCompat.from(context).notify(notificationId, builder.build());
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("check notification","Reminded");
        notificationId = intent.getIntExtra("notificationID",-1);
        long eventId = intent.getLongExtra("eventID",-1);
        DataRepository repository = ((App) context.getApplicationContext()).getRepository();
        this.context = context;
        repository.getEventSync(eventId, this);
    }
}
