package com.e.mad1.viewModel;

import android.app.Application;

import com.e.mad1.App;
import com.e.mad1.DataRepository;
import com.e.mad1.database.Entity.MovieEntity;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class MoviesListViewModel extends AndroidViewModel {

    private final DataRepository repository;

    // MediatorLiveData can observe other LiveData objects and react on their emissions.
    private final MediatorLiveData<List<MovieEntity>> observableMovies;

    public MoviesListViewModel(Application app) {
        super(app);

        observableMovies = new MediatorLiveData<>();
        // set by default null, until we get data from the database.
        observableMovies.setValue(null);

        repository = ((App) app).getRepository();

        LiveData<List<MovieEntity>> events = repository.getMovies();

        // observe the changes of the products from the database and forward them
        observableMovies.addSource(events, observableMovies::setValue);
    }

    public LiveData<List<MovieEntity>> getMovies() {
        return observableMovies;
    }

    public LiveData<List<MovieEntity>> getMoviesOld() {
        return repository.getMoviesOld();
    }

    public LiveData<List<MovieEntity>> getMoviesNew() {
        return repository.getMoviesNew();
    }
}
