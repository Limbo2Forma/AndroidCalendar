package com.e.mad1.viewModel;

import android.app.Application;

import com.e.mad1.App;
import com.e.mad1.DataRepository;
import com.e.mad1.database.Entity.EventEntity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class EventsListViewModel extends AndroidViewModel {
    private final DataRepository repository;

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<List<EventEntity>> observableEventsAsc;

    public EventsListViewModel(Application application) {
        super(application);

        observableEventsAsc = new MediatorLiveData<>();
        // set by default null, until we get data from the database.
        observableEventsAsc.setValue(null);

        repository = ((App) application).getRepository();

        LiveData<List<EventEntity>> events = repository.getAllEvents();

        // observe the changes of the products from the database and forward them
        observableEventsAsc.addSource(events, observableEventsAsc::setValue);
    }

    public void deleteEvent(long eventId){
        repository.deleteEventById(eventId);
    }

    public LiveData<List<EventEntity>> getAllEvents() {
        return observableEventsAsc;
    }
    public  LiveData<List<EventEntity>> getAllEventsDesc(){     //sort event date descending
        return repository.getAllEventsDesc();
    }
    public  LiveData<List<EventEntity>> getEventsClosest(Date date) { return repository.getEventsClosest(date); }
    public  LiveData<List<EventEntity>> getAllEventsInADate(Date date){

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        Date from = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY,23);
        cal.set(Calendar.MINUTE,59);
        cal.set(Calendar.SECOND,59);
        cal.set(Calendar.MILLISECOND,59);
        Date to = cal.getTime();

        // get all events between 0:00:00:000 and 23:59:59:999 of a date
        return repository.getAllEventsInATimeFrame(from, to);
    }
}
