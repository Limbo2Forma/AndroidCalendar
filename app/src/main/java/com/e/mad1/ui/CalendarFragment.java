package com.e.mad1.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.e.mad1.adapter.CalendarAdapter;
import com.e.mad1.adapter.EventsListAdapter;
import com.e.mad1.R;
import com.e.mad1.viewModel.EventsListViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarFragment extends Fragment {

    private Context context;
    private TextView currentMonthYear;
    private GridView calendarGridView;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMM yyyy");

    private Calendar calendar;
    private int previousSelectedPos;
    private int previousTextColor;
    private CalendarAdapter calendarAdapter;

    private EventsListAdapter listAdapter;
    private EventsListViewModel eventsListViewModel;

    private void updateCalendarAdapter(){
        previousSelectedPos = -1;

        List<Date> dayValueInCells = new ArrayList<>();
        Calendar setDate = (Calendar) calendar.clone();  //get
        setDate.set(Calendar.DAY_OF_MONTH, 1);
        Date newDate = setDate.getTime();
        int firstDayOfTheMonth = setDate.get(Calendar.DAY_OF_WEEK) - 1;
        setDate.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonth);

        while(dayValueInCells.size() < 42){ // worst case scenario is up to 42 days in a month view
            dayValueInCells.add(setDate.getTime());
            setDate.add(Calendar.DAY_OF_MONTH, 1);
        }

        currentMonthYear.setText(monthYearFormat.format(calendar.getTime()));
        calendarAdapter.updateCalendar(dayValueInCells, calendar);
        subscribeUi(newDate);
    }

    private void onCalendarClick(View view,int position){   // set background color of clicked grid
        TextView tv = (TextView) view;
        TextView previousSelectedView = (TextView) calendarGridView.getChildAt(previousSelectedPos);

        // if there is a previous selected view exists
        if (previousSelectedPos != -1) {
            previousSelectedView.setSelected(false);
            previousSelectedView.setBackgroundColor(Color.WHITE);
            previousSelectedView.setTextColor(previousTextColor);
        }
        previousSelectedPos = position;
        previousTextColor = tv.getCurrentTextColor();

        // set the current selected view position as previousSelectedPosition
        tv.setBackgroundColor(Color.parseColor("#D1648E"));
        tv.setTextColor(Color.WHITE);
    }

    private void subscribeUi(Date date) {
        // update the list when the data changes
        eventsListViewModel.getAllEventsInADate(date).observe(this, events -> listAdapter.setEventList(events));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        listAdapter = new EventsListAdapter(context);
        calendarAdapter = new CalendarAdapter(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
        Date today = Calendar.getInstance().getTime();
        eventsListViewModel = ViewModelProviders.of(this).get(EventsListViewModel.class);
        subscribeUi(today);
    }
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.event_calendar_fragment, container, false);

        currentMonthYear = v.findViewById(R.id.monthYear);
        calendarGridView = v.findViewById(R.id.dayView);
        calendarGridView.setAdapter(calendarAdapter);
        updateCalendarAdapter();

        calendarGridView.setOnItemClickListener((parent, view, position, id) -> {
            // get picked date event list
            subscribeUi(calendarAdapter.getItem(position));
            onCalendarClick(view,position);
        });

        // find id and assign on click listener for next and previous month button
        Button previousButton = v.findViewById(R.id.previous);
        previousButton.setOnClickListener(view -> {
            calendar.add(Calendar.MONTH, -1);
            updateCalendarAdapter();

        });
        Button nextButton = v.findViewById(R.id.next);
        nextButton.setOnClickListener(view -> {
            calendar.add(Calendar.MONTH, 1);
            updateCalendarAdapter();
        });

        // find and set to list view
        RecyclerView eventList = v.findViewById(R.id.dayList);
        eventList.setLayoutManager(new LinearLayoutManager(context));
        eventList.setAdapter(listAdapter);

        return v;
    }
}
