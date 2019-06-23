package com.e.mad1.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.e.mad1.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CalendarAdapter extends ArrayAdapter {

    private LayoutInflater layoutInflater;
    private List<Date> monthlyDates;
    private Calendar currentDate;

    // constructor: context, list of dates in month, calendar have current choose date value
    public CalendarAdapter(Context context) {
        super(context, R.layout.calendar_cell);
        layoutInflater = LayoutInflater.from(context);
    }

    // update new dates, months for the calendar
    public void updateCalendar(List<Date> dates,Calendar currentDate) {
        clear();    //clear old date
        this.monthlyDates = dates;
        this.currentDate = currentDate;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        // get date to display
        Date mDate = monthlyDates.get(position);
        Date today = Calendar.getInstance().getTime();  // view special layout for today date
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(mDate); // set up date of the grid
        
        int dayValue = dateCal.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCal.get(Calendar.MONTH) + 1;
        int displayYear = dateCal.get(Calendar.YEAR);

        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentYear = currentDate.get(Calendar.YEAR);

        if(view == null){
            view = layoutInflater.inflate(R.layout.calendar_cell, parent, false);
        }
        view.setBackgroundColor(Color.WHITE);   // set default background
        TextView cellNumber= view.findViewById(R.id.singleDay);

        // gray out date from last and next month
        if(displayMonth == currentMonth && displayYear == currentYear){
            cellNumber.setTextColor(Color.parseColor("#9C105B"));
        }else{
            cellNumber.setTextColor(Color.parseColor("#9F9FA3"));
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

        // highlight today date(if have)
        if (format.format(mDate).equals(format.format(today))){
            cellNumber.setTextColor(Color.parseColor("#C618D6"));
        }
        // set day text view
        cellNumber.setText(String.valueOf(dayValue));

        return view;
    }
    @Override
    public int getCount() { return 42; }    // the size is always 42
    @Nullable
    @Override
    public Date getItem(int position) {
        return monthlyDates.get(position);
    }
    @Override
    public int getPosition(Object item) { return 0;}
}


