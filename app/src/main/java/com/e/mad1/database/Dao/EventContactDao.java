package com.e.mad1.database.Dao;

import com.e.mad1.database.Entity.EventContactEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
@Dao
public interface EventContactDao {

    @Query("SELECT email FROM eventContact WHERE eid = :eid")
    List<String> getContactsSync(long eid);

    @Query("SELECT email FROM eventContact WHERE eid = :eid")
    LiveData<List<String>> getContacts(long eid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertContact(EventContactEntity eventContact);

    @Query("DELETE FROM eventContact WHERE eid = :eid")
    void deleteContacts(long eid);
}
