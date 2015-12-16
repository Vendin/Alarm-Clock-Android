package com.example.av.alarm_clock;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.av.alarm_clock.storage.AlarmContract;

import java.net.ConnectException;

/**
 * Created by mikrut on 29.11.15.
 */
public class Alarm {
    private Integer id;
    private short hour;
    private short minute;
    private boolean enabled;

    public Alarm(){}

    public Alarm(Cursor cursor) {
        setHour(cursor.getShort(AlarmContract.PROJECTION_HOUR_INDEX));
        setMinute(cursor.getShort(AlarmContract.PROJECTION_MINUTE_INDEX));
        setEnabled(cursor.getInt(AlarmContract.PROJECTION_IS_ENABLED_INDEX) > 0);
        setId(cursor.getInt(AlarmContract.PROJECTION_ID_INDEX));
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public short getHour() {
        return hour;
    }

    public void setHour(short hour) {
        if (hour >= 0 && hour <= 23) {
            this.hour = hour;
        } else {
            throw new IllegalArgumentException("Hour can be from 0 to 23");
        }
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_HOUR, getHour());
        values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_MINUTE, getMinute());
        values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_IS_ENABLED, isEnabled());
        return values;
    }

    public short getMinute() {
        return minute;
    }

    public void setMinute(short minute) {
        if (minute >= 0 && minute <= 59) {
            this.minute = minute;
        } else {
            throw new IllegalArgumentException("Minute can be from 0 to 59");
        }
    }
}
