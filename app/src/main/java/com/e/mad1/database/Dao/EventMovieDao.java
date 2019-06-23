package com.e.mad1.database.Dao;

import com.e.mad1.database.Entity.EventMovieEntity;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface EventMovieDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addNewEventMovie(EventMovieEntity em);

    @Query("DELETE FROM eventMovie WHERE eid = :eid")
    void deleteMovies(long eid);
}
