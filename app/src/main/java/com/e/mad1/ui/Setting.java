package com.e.mad1.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.e.mad1.R;
import com.e.mad1.backgroundNoti.CheckInternet;

public class Setting extends AppCompatActivity {

    private static final String PREFS_NAME = "com.e.mad1.PREFERENCE_FILE_KEY";
    private static final String THRESHOLD = "Notification Threshold";
    private static final String REMIND = "Notification Remind";
    private static final String PERIOD = "Notification Period";


    EditText editThreshold;
    EditText editRemind;
    EditText editPeriod;
    int threshold = 15;
    int remind = 15;
    int period = 2;

    private void checkFieldsEmpty(){// checks if any field is empty
        String error = "";
        String[] errorList = getResources().getStringArray(R.array.eventError);

        if (TextUtils.isEmpty(editThreshold.getText().toString())){
            error = error + errorList[0];
        } else {
            threshold = Integer.parseInt(editThreshold.getText().toString());
            if (threshold < 5){
                error = error + errorList[3];
            }
        }
        if (TextUtils.isEmpty(editRemind.getText().toString())){
            error = error + errorList[1];
        } else {
            remind = Integer.parseInt(editRemind.getText().toString());
            if (remind < 1 || remind > 45){
                error = error + errorList[4];
            }
        }
        if (TextUtils.isEmpty(editPeriod.getText().toString())){
            error = error + errorList[2];
        } else {
            period = Integer.parseInt(editPeriod.getText().toString());
            if (period < 1 || period > 20){
                error = error + errorList[5];
            }
        }
        if (error.equals("")){
            saveSetting(threshold,remind,period);
        }   else Toast.makeText(this,error,Toast.LENGTH_LONG).show();
    }

    private void saveSetting(int threshold, int remind, int period){
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putInt(THRESHOLD,threshold);
        editor.putInt(REMIND,remind);
        editor.putInt(PERIOD,period);
        editor.apply();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted yet
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 69);
        } else {
            startService(new Intent(getApplicationContext(), CheckInternet.class));
        }

        Setting.super.finish();
    }

    @SuppressLint("SetTextI18n")
    private void initSetting(){
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        threshold = preferences.getInt(THRESHOLD,15);
        remind = preferences.getInt(REMIND,15);
        period = preferences.getInt(PERIOD,2);

        editThreshold.setText(Integer.toString(threshold));
        editRemind.setText(Integer.toString(remind));
        editPeriod.setText(Integer.toString(period));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        editThreshold = findViewById(R.id.editThreshold);
        imm.hideSoftInputFromWindow(editThreshold.getApplicationWindowToken(), 0);
        editRemind = findViewById(R.id.editRemind);
        imm.hideSoftInputFromWindow(editRemind.getApplicationWindowToken(), 0);
        editPeriod = findViewById(R.id.editPeriod);
        imm.hideSoftInputFromWindow(editPeriod.getApplicationWindowToken(), 0);

        initSetting();

        final Button back = findViewById(R.id.back);
        back.setOnClickListener(view -> Setting.super.finish());

        final Button save = findViewById(R.id.save);
        save.setOnClickListener(view -> checkFieldsEmpty());
    }
}
