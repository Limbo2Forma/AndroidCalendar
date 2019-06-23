package com.e.mad1.ui;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.e.mad1.database.Entity.EventEntity;

import com.e.mad1.R;
import com.e.mad1.viewModel.EventsListViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.Calendar;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap gmap;
    private MapView mapView;
    private EventsListViewModel eventsListViewModel;
    private static final String MAP_BUNDLE_KEY = "MapViewBundleKey";

    private void subscribeUi() {
        // update the list when the data changes
        eventsListViewModel.getEventsClosest(Calendar.getInstance().getTime()).observe(this, this::setLocation);
    }

    private void setLocation(List<EventEntity> events){
        Log.i("map","subscribe");
        Log.i("map",events.toString());
        gmap.clear();
        byte i = 0;
        for (EventEntity e : events) {
            if( i < 3) {
                Log.i("map", e.getLocation());
                String[] arrOfStr = e.getLocation().split(",");
                LatLng location = new LatLng(Double.parseDouble(arrOfStr[0]), Double.parseDouble(arrOfStr[1]));

                gmap.moveCamera(CameraUpdateFactory.newLatLng(location));
                MarkerOptions marker = new MarkerOptions();
                marker.position(location);
                marker.title(e.getTitle());
                gmap.addMarker(marker);
                i++;
            } else break;
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(12);
        Log.i("map","map ready");
        subscribeUi();
        // move to event location
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    public void onDestroy() {
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

        Bundle mapViewBundle = outState.getBundle(MAP_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventsListViewModel = ViewModelProviders.of(this).get(EventsListViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.event_map_fragment, container, false);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_BUNDLE_KEY);
        }

        mapView = v.findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        Log.i("map","created");
        
        return v;
    }
}
