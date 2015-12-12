package com.example.av.alarm_clock.storage;

import android.provider.BaseColumns;

import com.example.av.alarm_clock.Alarm;

/**
 * Created by mikrut on 29.11.15.
 */
public final class AlarmContract {
    private AlarmContract() {}

    private static final String SMALL_TYPE = " INTEGER";
    private static final String BOOL_TYPE = " BOOLEAN";
    private static final String COMMA_SEP = ",";

    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + AlarmEntry.TABLE_NAME + " (" +
                    AlarmEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    AlarmEntry.COLUMN_NAME_ALARM_HOUR + SMALL_TYPE +
                    " CHECK ("+ AlarmEntry.COLUMN_NAME_ALARM_HOUR +" >= 0 AND "+
                    AlarmEntry.COLUMN_NAME_ALARM_HOUR + " <= 23) NOT NULL" + COMMA_SEP +
                    AlarmEntry.COLUMN_NAME_ALARM_MINUTE + SMALL_TYPE +
                    " CHECK ("+AlarmEntry.COLUMN_NAME_ALARM_MINUTE + ">=0 AND "+
                    AlarmEntry.COLUMN_NAME_ALARM_MINUTE + "<=59) NOT NULL" + COMMA_SEP +
                    AlarmEntry.COLUMN_NAME_ALARM_IS_ENABLED + BOOL_TYPE + " NOT NULL" +
                    ")";

    static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + AlarmEntry.TABLE_NAME;

    static final String[] ALARM_PROJECTION = {
            AlarmEntry._ID,
            AlarmEntry.COLUMN_NAME_ALARM_HOUR,
            AlarmEntry.COLUMN_NAME_ALARM_MINUTE,
            AlarmEntry.COLUMN_NAME_ALARM_IS_ENABLED
    };

    public static final int PROJECTION_ID_INDEX = 0;
    public static final int PROJECTION_HOUR_INDEX = 1;
    public static final int PROJECTION_MINUTE_INDEX = 2;
    public static final int PROJECTION_IS_ENABLED_INDEX = 3;

    public static abstract class AlarmEntry implements BaseColumns {
        public static final String TABLE_NAME = "alarm";
        public static final String COLUMN_NAME_ALARM_HOUR = "hour";
        public static final String COLUMN_NAME_ALARM_MINUTE = "minute";
        public static final String COLUMN_NAME_ALARM_IS_ENABLED = "is_enabled";
    }
}
