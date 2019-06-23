package com.e.mad1.listener;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.e.mad1.ui.MoviePickerDialog;
import com.e.mad1.ui.ScheduleEventActivity;

import java.text.SimpleDateFormat;

public class ScheduleEventListeners {
    private ScheduleEventActivity scheduleEventActivity;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d/MM/yyyy h:mm:ss a");

    public ScheduleEventListeners(ScheduleEventActivity schedule){
        this.scheduleEventActivity = schedule;
    }
    public DateTimePicker setDateTimePicker(TextView edit){
        return new DateTimePicker(edit);
    }
    public MoviePicker setMoviePicker(){
        return new MoviePicker();
    }
    public ContactPicker setContactPicker(DialogFragment df){
        return new ContactPicker(df);
    }

    public class DateTimePicker implements TextView.OnClickListener{
        private  TextView edit;
        private Calendar date;
        DateTimePicker(TextView edit){
            this.edit = edit;
        }
        @Override
        public void onClick(View v) {
            final Calendar currentDate = Calendar.getInstance();
            date = Calendar.getInstance();
            // launch datePicker
            new DatePickerDialog(scheduleEventActivity, (view, year, monthOfYear, dayOfMonth) -> {
                date.set(year, monthOfYear, dayOfMonth);
                // if user picked date, launch timePicker
                new TimePickerDialog(scheduleEventActivity, (view1, hourOfDay, minute) -> {
                    date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    date.set(Calendar.MINUTE, minute);
                    // set date
                    edit.setText(dateFormat.format(date.getTime()), TextView.BufferType.EDITABLE);
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
        }
    }

    public class MoviePicker implements Button.OnClickListener {
        @Override public void onClick(View view) {
            FragmentManager fm = scheduleEventActivity.getSupportFragmentManager();
            MoviePickerDialog moviePicker = new MoviePickerDialog();
            moviePicker.show(fm, "pick_movie");
        }
    }

    public class ContactPicker implements Button.OnClickListener {
        private DialogFragment contactPicker;
        ContactPicker(DialogFragment picker){
            this.contactPicker = picker;
        }
        @Override public void onClick(View view) {
            contactPicker.show(scheduleEventActivity.getSupportFragmentManager(), "attendee list");
        }
    }
}
