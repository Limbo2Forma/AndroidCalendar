package com.e.mad1;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import com.e.mad1.backgroundNoti.CheckInternet;
import com.e.mad1.database.Database;

public class App extends Application {

    private AppExecutors appExecutors;

    @Override
    public void onCreate() {
        super.onCreate();
        appExecutors = new AppExecutors();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startService(new Intent(this, CheckInternet.class));
        }
    }
    // execute database on diskIO executor
    public Database getDatabase() {
        return Database.getInstance(this, appExecutors);
    }

    public DataRepository getRepository() {
        return DataRepository.getInstance(getDatabase());
    }
}