package com.e.mad1.listener;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.e.mad1.ui.MainActivity;
import com.e.mad1.ui.ScheduleEventActivity;

public class MainActivityListeners {
    private MainActivity main;

    public MainActivityListeners(MainActivity main){
        this.main = main;
    }

    public NewEventButton setNewEvent(){
        return new NewEventButton();
    }

    public SettingButton setSetting(){
        return new SettingButton();
    }

    public class NewEventButton implements Button.OnClickListener {
        @Override public void onClick(View view) {
            Intent intent = new Intent(main, ScheduleEventActivity.class);
            intent.putExtra("com.e.mad1.UI", 0);
            main.startActivity(intent);
        }
    }

    public class SettingButton implements Button.OnClickListener {
        @Override public void onClick(View view) {
            Intent intent = new Intent(main, com.e.mad1.ui.Setting.class);
            main.startActivity(intent);
        }
    }
}
