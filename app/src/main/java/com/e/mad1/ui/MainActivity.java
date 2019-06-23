package com.e.mad1.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import com.e.mad1.adapter.TabAdapter;
import com.e.mad1.backgroundNoti.CheckInternet;
import com.e.mad1.R;
import com.e.mad1.listener.MainActivityListeners;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    private void setUpTab(ViewPager viewPager, TabLayout tabLayout){

        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        // add fragments to tap adapter
        adapter.addFragment(new ListFragment(),getString(R.string.tabList));
        adapter.addFragment(new CalendarFragment(),getString(R.string.tabCalendar));
        adapter.addFragment(new MapFragment(),getString(R.string.mapView));
        adapter.addFragment(new MoviesFragment(),getString(R.string.moviesView));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 69) {
            if (permissions.length == 1 &&
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService(new Intent(this, CheckInternet.class));
            } else {
                // Permission was denied. Display an error message.
                Toast.makeText(this, "No permission to get location", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 69);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivityListeners listeners = new MainActivityListeners(this);
        //new DistanceMatrix().execute("0");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted yet
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 69);
        } else {
            startService(new Intent(getApplicationContext(), CheckInternet.class));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpTab(findViewById(R.id.viewPager),findViewById(R.id.tabEvent));

        final ImageButton addEvent = findViewById(R.id.addEvent);
        addEvent.setOnClickListener(listeners.setNewEvent());

        final ImageButton setting = findViewById(R.id.setting);
        setting.setOnClickListener(listeners.setSetting());

    }
}
