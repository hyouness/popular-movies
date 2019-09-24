package com.example.popularmovies.database;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

class DateConverter {
    @TypeConverter
    static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
