package com.e.mad1;

import android.os.AsyncTask;
import android.util.Log;

import com.e.mad1.database.Dao.EventContactDao;
import com.e.mad1.database.Dao.EventDao;
import com.e.mad1.database.Dao.EventMovieDao;
import com.e.mad1.database.Database;
import com.e.mad1.database.Entity.EventContactEntity;
import com.e.mad1.database.Entity.EventEntity;
import com.e.mad1.database.Entity.EventMovieEntity;
import com.e.mad1.database.Entity.MovieEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class DataRepository {

    private static DataRepository sInstance;

    private final Database database;
    private MediatorLiveData<List<EventEntity>> mObservableEvents;
    private MediatorLiveData<List<MovieEntity>> mObservableMovies;

    private DataRepository(final Database db) {
        database = db;
        mObservableEvents = new MediatorLiveData<>();

        mObservableEvents.addSource(database.eventDao().getAllEvents(), events -> {
                    if (database.getDatabaseCreated().getValue() != null) {
                        mObservableEvents.postValue(events);
                    }
                });

        mObservableMovies = new MediatorLiveData<>();

        mObservableMovies.addSource(database.movieDao().getMovies(), movies -> {
                    if (database.getDatabaseCreated().getValue() != null) {
                        mObservableMovies.postValue(movies);
                    }
                });
    }

    static DataRepository getInstance(final Database database) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(database);
                }
            }
        }
        return sInstance;
    }

    // get movies in alphabetical order
    public LiveData<List<MovieEntity>> getMovies() {   // get movie list
        return mObservableMovies;
    }
    // get movies sort by old to new
    public LiveData<List<MovieEntity>> getMoviesOld() {   // get movie list
        return database.movieDao().getMovieOld();
    }
    // get movies sort by new to old
    public LiveData<List<MovieEntity>> getMoviesNew() {   // get movie list
        return database.movieDao().getMovieNew();
    }
    // get contacts list assigned with an eventId async (not worked yet)
    public LiveData<List<String>> getEventContacts(long eventId){
        return database.eventContactDao().getContacts(eventId);
    }
    // get movies list assigned with an eventId async (not worked yet)
    public LiveData<List<MovieEntity>> getMoviesEvent(long eventId){
        return database.movieDao().getMoviesEvent(eventId);
    }
    // get event detail assigned with an eventId (not worked yet)
    public LiveData<EventEntity> getEvent(long eventId){
        return database.eventDao().getEvent(eventId);
    }
    public void getEventSync(long eventId, getEventAsync.AsyncResponse asyncResponse){
        new getEventAsync(database.eventDao(), asyncResponse).execute(eventId);
    }

    public static class getEventAsync extends AsyncTask<Long, Void, EventEntity> {
        private EventDao asyncTaskDao;
        public interface AsyncResponse {
            void processFinish(EventEntity event);
        }
        AsyncResponse delegate;

        getEventAsync(EventDao dao,AsyncResponse delegate) {
            this.delegate = delegate;
            this.asyncTaskDao = dao;
        }
        @Override
        protected EventEntity doInBackground(final Long... params) {
            return asyncTaskDao.getEventSync(params[0]);
        }
        @Override
        protected void onPostExecute(EventEntity event){
            delegate.processFinish(event);
        }
    }

    // get all events sort by date ascend
    public LiveData<List<EventEntity>> getAllEvents() { return mObservableEvents; }
    // get all events sort by date descend
    public LiveData<List<EventEntity>> getAllEventsDesc(){     // sort event date descending
        return database.eventDao().getAllEventsDesc();
    }
    // get all events in a date time ascend order
    public LiveData<List<EventEntity>> getAllEventsInATimeFrame(Date from,Date to){
        return database.eventDao().getAllEventsInATimeFrame(from,to);
    }

    public List<EventEntity> getAllEventsInATimeFrameSync(Date from,Date to){
        return database.eventDao().getAllEventsInATimeFrameSync(from,to);
    }

    public LiveData<List<EventEntity>> getEventsClosest(Date date){
        return database.eventDao().getEventsClosest(date);
    }

    // add event to the database async, include update (use onConflict REPLACE)
    public void addEvent(EventEntity e, @Nullable long[] mvId,ArrayList<String> email) {
        new eventAddAsyncTask(database.eventDao(),database.eventMovieDao(),
                database.eventContactDao(),mvId,email).execute(e);
    }

    // async class to add event
    private static class eventAddAsyncTask extends AsyncTask<EventEntity, Void, Void> {

        private EventDao eventDao;
        private EventContactDao eventContactDao;
        private EventMovieDao eventMovieDao;
        private long[] mvId;
        private ArrayList<String> email;

        eventAddAsyncTask(EventDao eventDao, EventMovieDao eventMovieDao, EventContactDao eventContactDao,
        @Nullable long[] mvId,ArrayList<String> email) {
            this.eventDao = eventDao;
            this.eventContactDao = eventContactDao;
            this.eventMovieDao = eventMovieDao;
            this.mvId = mvId;
            this.email = email;
        }

        @Override
        protected Void doInBackground(final EventEntity... params) {
            long id = eventDao.addNewEvent(params[0]);
            eventContactDao.deleteContacts(id);
            eventMovieDao.deleteMovies(id);
            if (mvId != null){
                for (long aMvId : mvId) {
                    Log.d("foreign key"," event id: " + id + " movie id" + aMvId);
                    eventMovieDao.addNewEventMovie(new EventMovieEntity(id,aMvId));
                }
            }
            if (!email.isEmpty()){
                for (String attendee : email) {
                    eventContactDao.insertContact(new EventContactEntity(id,attendee));
                }
            }
            return null;
        }
    }
    // delete a event from the database async

    public void setEventNotifyStatus(long eventId){
        new SetNotifyStatusASync(database.eventDao()).execute(eventId);
    }

    private static class SetNotifyStatusASync extends AsyncTask<Long, Void, Void> {

        private EventDao asyncTaskDao;
        SetNotifyStatusASync(EventDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Long... params) {
            asyncTaskDao.setNotificationStatus(params[0],true);
            return null;
        }
    }

    public void deleteEventById(long eventId){
        new DeleteByIdAsyncTask(database.eventDao()).execute(eventId);
    }

    private static class DeleteByIdAsyncTask extends AsyncTask<Long, Void, Void> {

        private EventDao asyncTaskDao;
        DeleteByIdAsyncTask(EventDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Long... params) {
            asyncTaskDao.deleteByEventId(params[0]);
            return null;
        }
    }
}

