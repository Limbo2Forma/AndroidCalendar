package com.e.mad1.database;

import android.content.Context;
import android.util.Log;

import com.e.mad1.AppExecutors;
import com.e.mad1.database.Dao.EventContactDao;
import com.e.mad1.database.Dao.EventDao;
import com.e.mad1.database.Dao.EventMovieDao;
import com.e.mad1.database.Dao.MovieDao;
import com.e.mad1.database.Entity.EventContactEntity;
import com.e.mad1.database.Entity.EventEntity;
import com.e.mad1.database.Entity.EventMovieEntity;
import com.e.mad1.database.Entity.MovieEntity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

@androidx.room.Database(entities = {EventEntity.class, MovieEntity.class,
        EventMovieEntity.class, EventContactEntity.class}, version = 2)
@TypeConverters(DateConverter.class)
public abstract class Database extends RoomDatabase {   // database class

    private static Database databaseInstance;
    public abstract EventDao eventDao();
    public abstract MovieDao movieDao();
    public abstract EventMovieDao eventMovieDao();
    public abstract EventContactDao eventContactDao();

    private final MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();

    // get instance for current database in use
    public static Database getInstance(final Context context, final AppExecutors executors) {
        if (databaseInstance == null) {
            synchronized (Database.class) {
                if (databaseInstance == null) {
                    databaseInstance = buildDatabase(context.getApplicationContext(), executors);
                    databaseInstance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return databaseInstance;
    }

    // build database if it not build yet
    private static Database buildDatabase(final Context context, final AppExecutors executors) {
        return Room.databaseBuilder(context, Database.class, "mad-database")
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@androidx.annotation.NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        executors.diskIO().execute(() -> {
                            Database database = Database.getInstance(context, executors);
                            // Start to get data from txt file to add here,
                            //  but the txt file become irrelevant after add
                            //   and my data structure is different compare the the text
                            //    so i decide to manually add the movies only, let user add fresh event

                            final MovieEntity mv1 = new MovieEntity("Blade Runner",1982,"blade_runner1982");
                            mv1.setId(1);
                            final MovieEntity mv2 = new MovieEntity("hackers",1995,"hackers");
                            mv2.setId(2);
                            PrePopulateValue populateValue = new PrePopulateValue(context);
                            database.runInTransaction(() -> {
                                populateValue.populateEvents(database.eventDao());
                                Log.i("database", "run in transaction");
                                database.movieDao().insert(mv1);
                                database.movieDao().insert(mv2);
                            });

                            // Added all data from text, can i assume that ?
                            database.setDatabaseCreated();
                        });
                    }
                })
                //.allowMainThreadQueries()   // to execute load in for some function
                .build();
    }

    // update created database
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath("mad-database").exists()) {
            setDatabaseCreated();
        }
    }

    // set if database is created
    private void setDatabaseCreated(){
        mutableLiveData.postValue(true);
    }

    // get value if database is created
    public LiveData<Boolean> getDatabaseCreated() {
        return mutableLiveData;
    }
}
