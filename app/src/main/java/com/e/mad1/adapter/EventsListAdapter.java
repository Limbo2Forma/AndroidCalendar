package com.e.mad1.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.e.mad1.database.Entity.EventEntity;
import com.e.mad1.R;
import com.e.mad1.listener.ListAdapterListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.EventsViewHolder> {

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat format = new SimpleDateFormat("d/MM/yyyy h:mm:ss a");
    private LayoutInflater layoutInflater;
    private List<EventEntity> eventList;
    private Context context;

    // constructor: context
    public EventsListAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    // set Event list in adapter
    public void setEventList(List<EventEntity> eventList) {
        this.eventList = eventList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventsListAdapter.EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = layoutInflater.inflate(R.layout.event_cell, parent, false);
        return new EventsListAdapter.EventsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsListAdapter.EventsViewHolder holder, int position) {
        ListAdapterListener listener = new ListAdapterListener(context);
        if (eventList == null) {
            return;
        }
        final EventEntity event = eventList.get(position);
        if (event != null) {
            holder.eventTitle.setText(event.getTitle());
            holder.eventStart.setText(format.format(event.getStartDate()));
            holder.eventEnd.setText(format.format(event.getEndDate()));
            holder.eventVenue.setText(event.getVenue());

            if (event.getStartDate().compareTo(Calendar.getInstance().getTime()) < 0){
                holder.eventTitle.setTextColor(Color.parseColor("#9F9FA3"));
                holder.eventStart.setTextColor(Color.parseColor("#9F9FA3"));
                holder.eventEnd.setTextColor(Color.parseColor("#9F9FA3"));
                holder.eventVenue.setTextColor(Color.parseColor("#9F9FA3"));
            }   else  {
                holder.eventTitle.setTextColor(Color.parseColor("#9C105B"));
                holder.eventStart.setTextColor(Color.parseColor("#9C105B"));
                holder.eventEnd.setTextColor(Color.parseColor("#9C105B"));
                holder.eventVenue.setTextColor(Color.parseColor("#9C105B"));
            }

            holder.eventView.setOnClickListener(listener.setOpenEditEvent(event.getId()));
            holder.deleteEvent.setOnClickListener(listener.setDeleteEvent(holder.eventView,event));
        }
    }

    @Override
    public int getItemCount() { // default method in extend adapter
        if (eventList == null) {
            return 0;
        } else {
            return eventList.size();
        }
    }

    public List<EventEntity> getList() {
        return eventList;
    }   // get event list

    class EventsViewHolder extends RecyclerView.ViewHolder {
        private TextView eventTitle;
        private TextView eventStart;
        private TextView eventEnd;
        private TextView eventVenue;
        private ImageButton deleteEvent;
        private ConstraintLayout eventView;

        EventsViewHolder(View view) {
            super(view);
            eventTitle = view.findViewById(R.id.title);
            eventStart = view.findViewById(R.id.startDate);
            eventEnd = view.findViewById(R.id.endDate);
            eventVenue = view.findViewById(R.id.venue);
            deleteEvent = view.findViewById(R.id.deleteEvent);
            eventView = view.findViewById(R.id.eventCell);
        }
    }
}

