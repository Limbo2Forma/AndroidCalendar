package com.e.mad1.ui;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.e.mad1.adapter.EventsListAdapter;
import com.e.mad1.R;
import com.e.mad1.viewModel.EventsListViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListFragment extends Fragment {
    private Context context;
    private EventsListAdapter listAdapter;
    private EventsListViewModel eventsListViewModel;

    private RecyclerView eventList;

    private void subscribeUi(int selection) {
        // update the list when the data changes
        if (selection == 0){
            eventsListViewModel.getAllEvents().observe(this, events -> listAdapter.setEventList(events));
        }   else {
            eventsListViewModel.getAllEventsDesc().observe(this, events -> listAdapter.setEventList(events));
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        listAdapter = new EventsListAdapter(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventsListViewModel = ViewModelProviders.of(this).get(EventsListViewModel.class);
        subscribeUi(0);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.event_list_fragment, container, false);

        Spinner sort = v.findViewById(R.id.spinnerEvent);
        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                subscribeUi(position);
                eventList.smoothScrollToPosition(0);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        eventList = v.findViewById(R.id.listView);
        eventList.setLayoutManager(new LinearLayoutManager(context));
        eventList.setAdapter(listAdapter);

        return v;
    }
}
