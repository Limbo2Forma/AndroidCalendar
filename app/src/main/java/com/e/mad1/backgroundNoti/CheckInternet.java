package com.e.mad1.backgroundNoti;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class CheckInternet extends Service {
    private static final String PREFS_NAME = "com.e.mad1.PREFERENCE_FILE_KEY";
    private static final String INTERNET_STATUS = "internet";
    private static final String PERIOD = "Notification Period";

    // to set notification alarm into background and even not using app
    NotificationAlarm checkLocation = new NotificationAlarm();

    private void createNotificationChannel() {
        // create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notify";
            String description = "Notify event nearby";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("Notify", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setInternetStatus(boolean bool){
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME,MODE_PRIVATE).edit();
        editor.putBoolean(INTERNET_STATUS,bool);
        editor.apply();
    }

    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context service = this;

        // get notification period from setting
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int period = preferences.getInt(PERIOD,2);

        // set alarm manager;
        checkLocation.setAlarm(service, period);

        // set current network status flag
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(INTERNET_STATUS, true);
        editor.apply();

        Handler handler = new Handler();
        int delay = 1000; //milliseconds
        handler.postDelayed(new Runnable(){
            public void run(){
                SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                boolean alarmSet = preferences.getBoolean(INTERNET_STATUS,true);

                ConnectivityManager cm = (ConnectivityManager)getApplicationContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if (!isConnected && alarmSet) {
                    checkLocation.cancelAlarm(service);
                    setInternetStatus(false);
                }
                if (isConnected && !alarmSet){
                    checkLocation.setAlarm(service, preferences.getInt(PERIOD,2));
                    Log.i("check notification","reset alarm");
                    setInternetStatus(true);
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
