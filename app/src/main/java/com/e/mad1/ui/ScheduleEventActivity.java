package com.e.mad1.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.e.mad1.App;
import com.e.mad1.DataRepository;
import com.e.mad1.R;
import com.e.mad1.database.Entity.EventEntity;
import com.e.mad1.listener.ScheduleEventListeners;
import com.e.mad1.viewModel.EventViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("SetTextI18n")    // suppressLint ask for text hardcoding, these text actually show numbers
public class ScheduleEventActivity extends AppCompatActivity
        implements MoviePickerDialog.passMoviePick, AddAttendeeDialog.passAttendeeList,
                    OnMapReadyCallback {
    EditText title;
    TextView start;
    TextView end;
    EditText venue;
    EditText location;
    TextView addAttendee;
    TextView addMovie;
    ArrayList<String> email;
    long id;
    long movieId;
    DataRepository repository;
    GoogleMap gmap;
    MapView mapView;

    private static final String MAP_VIEW_BUNDLE_KEY = "SingleEventMapBundle";

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("d/MM/yyyy h:mm:ss a");

    private void subscribeUI(EventViewModel viewModel){
        viewModel.getObservableEvent().observe(this, event -> {
            title.setText(event.getTitle());
            start.setText(dateFormat.format(event.getStartDate()));
            end.setText(dateFormat.format(event.getEndDate()));
            venue.setText(event.getVenue());
            location.setText(event.getLocation());

            convertToDouble(location.getText().toString());
        });
        viewModel.getObservableContacts().observe(this, contacts ->{
            email.addAll(contacts);
            addAttendee.setText(Integer.toString(email.size()));
        });
        viewModel.getObservableMovies().observe(this, mv ->{
            if (!mv.isEmpty()) {
                Log.i("save debug", mv.toString());
                addMovie.setText(mv.get(0).getTitle());
                movieId = mv.get(0).getId();
            } else {
                addMovie.setText(getResources().getString(R.string.addNewMovie));
                movieId = 0;
            }
        });
    }

    public void addedMovie(long movieId, String movieTitle){    // function from implemented interface
        this.movieId = movieId;
        addMovie.setText(movieTitle);
    }

    public void addedAttendees(ArrayList<String> email) {   // function from implemented interface
        this.email = email;
        addAttendee.setText(Integer.toString(email.size()));
    }

    private void saveEvent(Date startDate, Date endDate){   // save event to database
        EventEntity e = new EventEntity(title.getText().toString(), startDate, endDate,
                venue.getText().toString(),location.getText().toString());
        if (id != -1){
            e.setId(id);
        }
        // I use array of movieId instead of single movie id for potential pick multiple movie for a event
        //  not yet implement since there is no time left.
        long[] mvId = null;
        if (movieId != 0){
            mvId = new long[1];
            mvId[0] = movieId;
        }
        // save event directly to data repository, not through viewModel
        repository.addEvent(e,mvId,email);
        ScheduleEventActivity.super.finish();
    }

    private void convertToDouble(String s){
        String regex = "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?), \\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(s);

        if (matcher.matches()){
            String [] arrOfStr = s.split(",");
            setLocation(Double.parseDouble(arrOfStr[0]),Double.parseDouble(arrOfStr[1]));
        } else Toast.makeText(this,getResources().getStringArray(R.array.eventError)[5],Toast.LENGTH_LONG).show();
    }

    private void checkFieldsEmpty(){    // checks if any field is empty
        String error = "";

        String[] errorList = getResources().getStringArray(R.array.eventError);
        Date startDate = null,endDate = null;
        if (TextUtils.isEmpty(title.getText().toString())){
            error = error + errorList[0];
        }
        if (TextUtils.isEmpty(start.getText().toString())){
            error = error + errorList[1];
        }   else startDate = dateFormat.parse(start.getText().toString(),new ParsePosition(0));

        if (TextUtils.isEmpty(end.getText().toString())){
            error = error + errorList[2];
        }   else endDate = dateFormat.parse(end.getText().toString(),new ParsePosition(0));

        if (startDate != null && endDate != null && startDate.compareTo(endDate) > 0){
            error = error + errorList[3];
        }
        if (TextUtils.isEmpty(venue.getText().toString())){
            error = error + errorList[4];
        }
        if (error.equals("")){
            saveEvent(startDate,endDate);
        }   else Toast.makeText(this,error,Toast.LENGTH_LONG).show();
    }

    private void assignIds(){
        title = findViewById(R.id.editTitle);
        start = findViewById(R.id.editStart);
        end = findViewById(R.id.editEnd);
        venue = findViewById(R.id.editVenue);
        addAttendee = findViewById(R.id.viewAttendees);
        addMovie = findViewById(R.id.pickMovie);
        location = findViewById(R.id.editLatLon);
        mapView = findViewById(R.id.mapView);
    }

    private void setTitle(){
        Intent intent = getIntent();
        id = intent.getLongExtra("com.e.mad1.UI",0);

        final TextView event = findViewById(R.id.scheduleEvent);
        if (id == 0){
            event.setText(getString(R.string.newEvent));
        }   else {
            event.setText(getString(R.string.editEvent));
            EventViewModel.Factory factory = new EventViewModel.Factory(getApplication(), id);

            final EventViewModel viewModel = ViewModelProviders.of(this, factory).get(EventViewModel.class);
            subscribeUI(viewModel);
        }
    }

    private void setListeners(){
        ScheduleEventListeners listeners = new ScheduleEventListeners(this);
        DialogFragment contactPicker = AddAttendeeDialog.newInstance(email);
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(title.getApplicationWindowToken(), 0);
        start.setOnClickListener(listeners.setDateTimePicker(start));
        end.setOnClickListener(listeners.setDateTimePicker(end));
        imm.hideSoftInputFromWindow(venue.getApplicationWindowToken(), 0);

        addMovie.setOnClickListener(listeners.setMoviePicker());

        addAttendee.setClickable(true);
        addAttendee.setOnClickListener(listeners.setContactPicker(contactPicker));

        location.setOnEditorActionListener((v, actionId, e) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                convertToDouble(location.getText().toString());
                imm.hideSoftInputFromWindow(location.getApplicationWindowToken(), 0);
                return true;
            }
            return false;
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
    private void setLocation(double v, double v1){
        gmap.clear();
        LatLng location = new LatLng(v,v1);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(location));
        gmap.addMarker(new MarkerOptions().position(location));
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(22);
        //convertToDouble(location.getText().toString());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_event);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        // get data repository
        repository = ((App) getApplication()).getRepository();
        email = new ArrayList<>();
        assignIds();
        setTitle();
        setListeners();
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        // get intent from activities to get event ID if its edit event

        final Button back = findViewById(R.id.back);
        back.setOnClickListener(view -> ScheduleEventActivity.super.finish());

        final Button save = findViewById(R.id.save);
        save.setOnClickListener(view -> checkFieldsEmpty());
    }
}
