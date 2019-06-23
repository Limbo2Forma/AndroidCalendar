package com.e.mad1.listener;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.e.mad1.ui.AddAttendeeDialog;

import java.util.Objects;

public class AddAttendeeListeners {
    private AddAttendeeDialog attendeeDialog;

    public AddAttendeeListeners(AddAttendeeDialog addAttendeeDialog){
        this.attendeeDialog = addAttendeeDialog;
    }

    public OpenContactPicker setPicker(){ return new OpenContactPicker(); }
    public SaveAttendees setSave(){ return new SaveAttendees(); }
    public SetEnterAttendee setEnterAttendee(EditText editText){ return new SetEnterAttendee(editText); }

    public class OpenContactPicker implements Button.OnClickListener {
        @Override public void onClick(View view) {
            // TODO: add code here
            while (ContextCompat.checkSelfPermission(Objects.requireNonNull(attendeeDialog.getActivity()),
                    Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted yet
                ActivityCompat.requestPermissions(attendeeDialog.getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS}, 1069);
            }
            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            attendeeDialog.startActivityForResult(contactPickerIntent, 1069);
        }
    }

    public class SaveAttendees implements Button.OnClickListener {
        @Override public void onClick(View view) {
            // TODO: add code here
            attendeeDialog.getAttendeeListener().addedAttendees(attendeeDialog.getAttendeeAdapter().getList());
            attendeeDialog.dismiss();
        }
    }

    public class SetEnterAttendee implements TextView.OnEditorActionListener {

        private EditText editText;
        SetEnterAttendee(EditText edit){ this.editText = edit; }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {   // press enter to add attendee email to list

                InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(attendeeDialog.getContext()).
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                attendeeDialog.getAttendeeAdapter().addAttendee(editText.getText().toString());
                editText.setText("");
                return true;
            }
            return false;
        }
    }
}
