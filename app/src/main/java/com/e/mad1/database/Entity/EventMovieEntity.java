package com.e.mad1.database.Entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(tableName = "eventMovie",
        indices = {@Index(value = {"eid","mid"},unique = true)},
        foreignKeys = {@ForeignKey
                (entity = EventEntity.class,parentColumns = "eid",
                        childColumns = "eid",onDelete = ForeignKey.CASCADE),
                        @ForeignKey
                (entity = MovieEntity.class,parentColumns = "mid",
                        childColumns = "mid")},
        primaryKeys = {"eid","mid"}
)
public class EventMovieEntity{
    @ColumnInfo(name="eid")
    public long eid;

    @ColumnInfo(name = "mid")
    public long mid;

    public EventMovieEntity(long eid,long mid) {
        this.mid = mid;
        this.eid = eid;
    }
}
