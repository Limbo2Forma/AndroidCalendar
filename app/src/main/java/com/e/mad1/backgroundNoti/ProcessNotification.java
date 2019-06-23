package com.e.mad1.backgroundNoti;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.e.mad1.App;
import com.e.mad1.DataRepository;
import com.e.mad1.R;
import com.e.mad1.database.Entity.EventEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */

public class ProcessNotification extends IntentService {
    private static final String PREFS_NAME = "com.e.mad1.PREFERENCE_FILE_KEY";
    private static final String THRESHOLD = "Notification Threshold";
    private static final String NOTIFICATION_SEQUENCE = "Notification Sequence";

    public ProcessNotification() {
        super("ProcessNotification");
    }

    //ping google and receive result to check internet connection

    private String requestLocation() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        locationCriteria.setAltitudeRequired(false);
        locationCriteria.setBearingRequired(false);
        locationCriteria.setCostAllowed(true);
        locationCriteria.setPowerRequirement(Criteria.POWER_MEDIUM);

        String providerName = locationManager.getBestProvider(locationCriteria, true);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestSingleUpdate(providerName, new LocationListener() {
                @Override public void onLocationChanged(Location location) { }
                @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
                @Override public void onProviderEnabled(String provider) { }
                @Override public void onProviderDisabled(String provider) { }
            },null);
            Location location = locationManager.getLastKnownLocation(providerName);
            return location.getLatitude() + "," + location.getLongitude();
        } else return null;
    }

    private List<EventEntity> getEvents(Date now){
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int threshold = preferences.getInt(THRESHOLD,2);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, threshold);
        Date to = calendar.getTime();

        return ((App) getApplication()).getRepository().getAllEventsInATimeFrameSync(now,to);
    }

    private void assignNotificationId(EventEntity event){
        CreateNotification createNotification = new CreateNotification();
        createNotification.setContext(this);
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String notificationIds = preferences.getString(NOTIFICATION_SEQUENCE,"1");
        ArrayList<String> notificationIdSequence = new ArrayList<>();

        assert notificationIds != null;
        if (!notificationIds.equals("1")){
            notificationIdSequence.addAll(Arrays.asList(notificationIds.split(",")));
        } else notificationIdSequence.add("1");
        int i = 0;
        Log.i("Check Location",event.getStartDate().toString());
        StringBuilder stringBuilder = new StringBuilder();
        boolean isNotified = false;

        for (String id:notificationIdSequence){
            if (id.equals("1") && !isNotified){
                NotificationCompat.Builder builder = createNotification.buildNotification(event, i);
                NotificationManagerCompat.from(this).notify(i, builder.build());
                notificationIdSequence.set(i/4,Integer.toString(i));
                id = Integer.toString(i);
                isNotified = true;
            } else i = i + 4;
            stringBuilder.append(",").append(id);
        }
        Log.i("check notification", "notification sequence: " + notificationIds);
        if ( i/4 > (notificationIdSequence.size() - 1)){
            stringBuilder.append(",").append(i);
            notificationIdSequence.add(Integer.toString(i));
            NotificationCompat.Builder builder = createNotification.buildNotification(event, i);
            NotificationManagerCompat.from(this).notify(i, builder.build());
        }

        notificationIds = stringBuilder.deleteCharAt(0).toString();
        preferences.edit().putString(NOTIFICATION_SEQUENCE,notificationIds).apply();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            DistanceMatrix distanceMatrix = new DistanceMatrix(requestLocation(),getString(R.string.google_maps_key));
            Calendar now = Calendar.getInstance();
            List<EventEntity> events = getEvents(now.getTime());

            for (EventEntity e:events) {
               if (!e.notifiedStatus()){
                   Calendar duration = (Calendar) now.clone();
                   int distance = distanceMatrix.getDuration(e.getLocation());
                   if (distance != -1) {
                       duration.add(Calendar.SECOND, distance);
                       if (e.getStartDate().after(duration.getTime())) {
                           assignNotificationId(e);
                           DataRepository repository = ((App) getApplication()).getRepository();
                           repository.setEventNotifyStatus(e.getId());
                       }
                   } else Log.i("check notification", "error read duration of event " + e.getId());
               }
            }
        }
    }
}
