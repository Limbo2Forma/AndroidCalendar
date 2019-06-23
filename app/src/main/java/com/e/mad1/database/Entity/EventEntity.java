package com.e.mad1.database.Entity;

import com.e.mad1.model.Event;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "events")
        //,        indices = {@Index(value = "title", unique = true)})
public class EventEntity implements Event {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "eid")
    public long id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "startDate")
    public Date start;

    @ColumnInfo(name = "endDate")
    public Date end;

    @ColumnInfo(name = "venue")
    public String venue;

    @ColumnInfo(name = "location")
    public String location;

    @ColumnInfo(name = "notify")
    public boolean isNotified;

    public long getId() { return id; }
    public void setId(long id){
        this.id = id;
    }

    public String getTitle() { return title; }

    public Date getStartDate() {
        return start;
    }

    public Date getEndDate() {
        return end;
    }

    public String getVenue() {
        return venue;
    }

    public String getLocation() { return location; }

    public boolean notifiedStatus() { return isNotified; }

    public EventEntity(@NonNull String title, Date start, Date end, String venue, String location) {
        this.title = title;
        this.start = start;
        this.end = end;
        this.venue = venue;
        this.location = location;
        this.isNotified = false;
    }
}
