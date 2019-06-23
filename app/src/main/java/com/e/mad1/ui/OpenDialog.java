package com.e.mad1.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.e.mad1.App;
import com.e.mad1.R;

import java.util.ArrayList;
import java.util.Arrays;

public class OpenDialog extends AppCompatActivity {

    private static final String PREFS_NAME = "com.e.mad1.PREFERENCE_FILE_KEY";
    private static final String NOTIFICATION_SEQUENCE = "Notification Sequence";

    private void saveSequence(int notificationId){
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String notificationIds = preferences.getString(NOTIFICATION_SEQUENCE,"1");
        ArrayList<String> notificationIdSequence = new ArrayList<>(Arrays.asList(notificationIds.split(",")));
        notificationIdSequence.set(notificationId/4,"1");

        StringBuilder stringBuilder = new StringBuilder();
        for (String id:notificationIdSequence){
            stringBuilder.append(",").append(id);
        }
        preferences.edit().putString(NOTIFICATION_SEQUENCE,stringBuilder.toString()).apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long eventId = getIntent().getLongExtra("eventID",0);
        int notificationId = getIntent().getIntExtra("NotificationID",0);
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    ((App) getApplicationContext()).getRepository().deleteEventById(eventId);
                    saveSequence(notificationId);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
            this.finish();
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.deleteMessage)).setPositiveButton(getString(R.string.yes),
                dialogClickListener).setNegativeButton(this.getString(R.string.no),
                dialogClickListener).show();
    }
}
