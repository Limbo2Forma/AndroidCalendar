package com.e.mad1.database;

import java.util.Date;

import androidx.room.TypeConverter;

public class DateConverter {    // convert date data type to long data type to store data.
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}

