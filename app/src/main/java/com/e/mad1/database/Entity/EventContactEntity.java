package com.e.mad1.database.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "eventContact",
        foreignKeys = @ForeignKey
                (entity = EventEntity.class,parentColumns = "eid",
                        childColumns = "eid",onDelete = ForeignKey.CASCADE),
        primaryKeys = {"eid","email"}
)
public class EventContactEntity{

    @ColumnInfo(name = "eid")
    public long eid;

    @NonNull
    @ColumnInfo(name = "email")
    public String email;

    public EventContactEntity(long eid, @NonNull String email) {
        this.eid = eid;
        this.email = email;
    }
}
