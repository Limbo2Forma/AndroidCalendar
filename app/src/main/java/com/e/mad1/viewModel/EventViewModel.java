package com.e.mad1.viewModel;

import android.app.Application;

import com.e.mad1.App;
import com.e.mad1.DataRepository;
import com.e.mad1.database.Entity.EventEntity;
import com.e.mad1.database.Entity.MovieEntity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

// This class is not yet use due to i have no time to reconstruct ScheduleEventActivity to use this viewModel

public class EventViewModel extends AndroidViewModel {

    private final LiveData<EventEntity> observableEvent;
    private final LiveData<List<String>> observableContacts;
    private final LiveData<List<MovieEntity>> observableMovies;

    private EventViewModel(@NonNull Application application, long id, DataRepository repository) {
        super(application);

        observableEvent = repository.getEvent(id);
        observableContacts = repository.getEventContacts(id);
        observableMovies = repository.getMoviesEvent(id);
    }

    public LiveData<EventEntity> getObservableEvent() {
        return observableEvent;
    }

    public LiveData<List<String>> getObservableContacts() {
        return observableContacts;
    }

    public LiveData<List<MovieEntity>> getObservableMovies() {
        return observableMovies;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application application;
        private final long eventId;
        private final DataRepository repository;

        public Factory(@NonNull Application app, long id) {
            application = app;
            eventId = id;
            repository = ((App) application).getRepository();
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new EventViewModel(application, eventId, repository);
        }
    }
}
