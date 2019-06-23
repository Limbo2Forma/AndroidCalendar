package com.e.mad1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.e.mad1.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.AttendeesViewHolder> {

    private LayoutInflater layoutInflater;
    private ArrayList<String> attendeeList;

    // setup adapter
    public AttendeeAdapter(Context context,ArrayList<String> email) {
        this.layoutInflater = LayoutInflater.from(context);
        this.attendeeList = email;
    }

    // add new attendee to list
    public void addAttendee(String email) {
        attendeeList.add(email);
        notifyItemInserted(attendeeList.size() - 1  );
    }

    // get view holder
    @NonNull
    @Override
    public AttendeeAdapter.AttendeesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = layoutInflater.inflate(R.layout.attendee_cell, parent, false);
        return new AttendeeAdapter.AttendeesViewHolder(view);
    }

    // bind view holder
    @Override
    public void onBindViewHolder(@NonNull AttendeeAdapter.AttendeesViewHolder holder, int position) {
        if (attendeeList == null) {
            return;
        }
        final String attendee = attendeeList.get(position);
        holder.removeAttendee.setClickable(true);
        holder.removeAttendee.setOnClickListener((view -> {
            attendeeList.remove(position);
            notifyItemRemoved(position);
        }));
        if (attendee != null) {
            holder.email.setText(attendee);
        }
    }

    @Override
    public int getItemCount() { //require method of custom adapter
        if (attendeeList == null) {
            return 0;
        } else {
            return attendeeList.size();
        }
    }

    public ArrayList<String> getList() {
        return attendeeList;
    }

    class AttendeesViewHolder extends RecyclerView.ViewHolder {
        private TextView email;
        private Button removeAttendee;

        AttendeesViewHolder(View view) {
            super(view);
            email = view.findViewById(R.id.attendeeEmail);
            removeAttendee = view.findViewById(R.id.removeAttendee);
        }
    }
}
