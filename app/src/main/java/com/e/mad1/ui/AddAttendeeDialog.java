package com.e.mad1.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.e.mad1.adapter.AttendeeAdapter;
import com.e.mad1.R;
import com.e.mad1.listener.AddAttendeeListeners;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;

public class AddAttendeeDialog extends DialogFragment{
    private Context context;
    private AttendeeAdapter attendeeAdapter;
    private ArrayList<String> email;
    private AddAttendeeListeners actionListener = new AddAttendeeListeners(this);

    // interface to pass attendee list back to ScheduleEventActivity activity
    private passAttendeeList attendeeListener;
    public interface passAttendeeList {
        void addedAttendees(ArrayList<String> email);
    }

    public AttendeeAdapter getAttendeeAdapter() { return attendeeAdapter; }
    public passAttendeeList getAttendeeListener(){ return attendeeListener; }

    // static to get bundle which contain previous saved attendee list
    static AddAttendeeDialog newInstance(ArrayList<String> emails) {

        AddAttendeeDialog fragment = new AddAttendeeDialog();
        Bundle args = new Bundle();
        args.putStringArrayList("attendee list", emails);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        this.attendeeListener = (passAttendeeList) context; // set the context
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        email = new ArrayList<>();
        email.addAll(Objects.requireNonNull(
                Objects.requireNonNull(getArguments()).getStringArrayList("attendee list")));
        attendeeAdapter = new AttendeeAdapter(context, email);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attendee_list_dialog, container, false);
        EditText addEmail = view.findViewById(R.id.addEmail);
        //set on enter click
        addEmail.setOnEditorActionListener(actionListener.setEnterAttendee(addEmail));

        final Button back = view.findViewById(R.id.back);
        back.setClickable(true);
        back.setOnClickListener(v -> AddAttendeeDialog.this.dismiss());

        final Button save = view.findViewById(R.id.saveAttendees);
        save.setClickable(true);
        save.setOnClickListener(actionListener.setSave());

        final Button openContactPicker = view.findViewById(R.id.openContactPicker);
        openContactPicker.setClickable(true);
        openContactPicker.setOnClickListener(actionListener.setPicker());

        RecyclerView attendees = view.findViewById(R.id.attendeesList);
        attendees.setLayoutManager(new LinearLayoutManager(context));
        attendeeAdapter = new AttendeeAdapter(context,email);
        attendees.setAdapter(attendeeAdapter);

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        // if request is cancelled, the result arrays are empty.
        if (requestCode == 1069) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(context, "No permission to get contact detail", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                        new String[]{Manifest.permission.READ_CONTACTS}, 1069);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1069) {
                Cursor cursor = null;
                try {
                    String id = Objects.requireNonNull(data.getData()).getLastPathSegment();    // query for everything email
                    cursor = context.getContentResolver().query(Email.CONTENT_URI,
                            null, Email.CONTACT_ID + "=?", new String[]{id}, null);
                    int emailId = Objects.requireNonNull(cursor).getColumnIndex(Email.DATA);
                    // get the first email
                    if (cursor.moveToFirst()) {
                        String email = cursor.getString(emailId);
                        Log.v("contact picker", "Got email: " + email);
                        attendeeAdapter.addAttendee(email);
                    }
                } catch (Exception e) {
                    Log.e("contact picker", "Failed to get email data", e);
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }
}
