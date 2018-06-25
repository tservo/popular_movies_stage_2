package com.example.android.popularmovies.utilities;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * this converter can be fount in T09b.10
 * as well as the android developer site:
 * https://developer.android.com/reference/android/arch/persistence/room/TypeConverter
 */
public class DateConverter {
        @TypeConverter
        public Date fromTimestamp(Long value) {
            return value == null ? null : new Date(value);
        }

        @TypeConverter
        public Long dateToTimestamp(Date date) {
            if (date == null) {
                return null;
            } else {
                return date.getTime();
            }
        }
}
