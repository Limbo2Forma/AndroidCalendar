package com.e.mad1.database.Dao;

import com.e.mad1.database.Entity.EventEntity;

import java.util.Date;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface EventDao {
    @Query("SELECT * FROM events ORDER BY startDate ASC")
    LiveData<List<EventEntity>> getAllEvents();

    @Query("SELECT * FROM events ORDER BY startDate DESC")
    LiveData<List<EventEntity>> getAllEventsDesc();

    @Query("SELECT * FROM events WHERE startDate BETWEEN :from AND :to ORDER BY startDate ASC")
    LiveData<List<EventEntity>> getAllEventsInATimeFrame(Date from, Date to);

    @Query("SELECT * FROM events WHERE startDate BETWEEN :from AND :to ORDER BY startDate ASC")
    List<EventEntity> getAllEventsInATimeFrameSync(Date from, Date to);

    @Query("SELECT * FROM events WHERE startDate > :from ORDER BY startDate ASC")
    LiveData<List<EventEntity>> getEventsClosest(Date from);

    @Query("SELECT * FROM events WHERE eid = :eid")
    LiveData<EventEntity> getEvent(long eid);

    @Query("SELECT * FROM events WHERE eid = :eid")
    EventEntity getEventSync(long eid);

    @Query("DELETE FROM events WHERE eid = :eventId")
    void deleteByEventId(long eventId);

    @Query("UPDATE events SET notify = :bool WHERE eid = :eventId")
    void setNotificationStatus(long eventId, boolean bool);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addNewEvent(EventEntity e);

    @Delete()
    void deleteEvents(EventEntity...EventEntity);
}