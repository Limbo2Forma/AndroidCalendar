package com.e.mad1.database.Entity;

import com.e.mad1.model.Movie;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movies")
        //,indices = {@Index(value = "title", unique = true)})
public class MovieEntity implements Movie {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mid")
    public long id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "year")
    int year;   // DO NOT set private since Database needs to access

    @ColumnInfo(name = "image")
    String poster;  // DO NOT set private since Database needs to access

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getPoster() {
        return poster;
    }

    public MovieEntity(@NonNull String title, int year, @NonNull String poster) {
        this.title = title;
        this.year = year;
        this.poster = poster;
    }
}
