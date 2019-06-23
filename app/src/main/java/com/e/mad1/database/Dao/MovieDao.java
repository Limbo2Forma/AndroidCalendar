package com.e.mad1.database.Dao;

import com.e.mad1.database.Entity.MovieEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies ORDER BY title ASC")
    LiveData<List<MovieEntity>> getMovies();

    @Query("SELECT * FROM movies ORDER BY year DESC")
    LiveData<List<MovieEntity>> getMovieNew();

    @Query("SELECT * FROM movies ORDER BY year ASC")
    LiveData<List<MovieEntity>> getMovieOld();

    @Query("SELECT movies.mid,movies.title,movies.year,movies.image FROM movies INNER JOIN eventMovie ON movies.mid = eventMovie.mid WHERE eId = :eventId")
    MovieEntity getMovieEventSync(long eventId);

    @Query("SELECT movies.mid,movies.title,movies.year,movies.image FROM movies INNER JOIN eventMovie ON movies.mid = eventMovie.mid WHERE eId = :eventId")
    LiveData<List<MovieEntity>> getMoviesEvent(long eventId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(MovieEntity movie);
}