package com.example.av.alarm_clock.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.example.av.alarm_clock.storage.AlarmContract;

/**
 * Created by mikrut on 29.11.15.
 */
public class Alarm {
    private Integer id;
    private short hour;
    private short minute;
    private boolean enabled;
    private boolean vibration;
    private String name;
    private Uri ringtone;
    private int countPhoto;
    private int dayMask;

    public Alarm(){}

    public Alarm(Cursor cursor) {
        setHour(cursor.getShort(AlarmContract.PROJECTION_HOUR_INDEX));
        setMinute(cursor.getShort(AlarmContract.PROJECTION_MINUTE_INDEX));
        setEnabled(cursor.getInt(AlarmContract.PROJECTION_IS_ENABLED_INDEX) > 0);
        setId(cursor.getInt(AlarmContract.PROJECTION_ID_INDEX));
        setVibration(cursor.getInt(AlarmContract.PROJECTION_VIBRATION_INDEX) > 0);
        setName(cursor.getString(AlarmContract.PROJECTION_NAME_INDEX));
        setRingtone(cursor.getString(AlarmContract.PROJECTION_RINGTONE_INDEX));
        setCountPhoto(cursor.getInt(AlarmContract.PROJECTION_COUNT_PHOTO_INDEX));
        setDayMask(cursor.getInt(AlarmContract.PROJECTION_DAYMASK_INDEX));
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
        values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_VIBRATION, isVibration());
        values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_NAME, getName());
        values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_RINGTONE, getRingtoneString());
        values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_COUNT_PHOTO, getCountPhoto());
        values.put(AlarmContract.AlarmEntry.COLUMN_NAME_ALARM_DAYMASK, getDayMask());
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

    public boolean isVibration() {
        return vibration;
    }

    public void setVibration(boolean vibration) {
        this.vibration = vibration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getRingtone() {
        return ringtone;
    }

    public String getRingtoneString() {
        return ringtone != null ? ringtone.toString() : null;
    }

    public void setRingtone(Uri ringtone) {
        this.ringtone = ringtone;
    }

    public void setRingtone(String ringtone) {
        setRingtone(ringtone != null ? Uri.parse(ringtone) : null);
    }

    public int getCountPhoto() {
        return countPhoto;
    }

    public void setCountPhoto(int countPhoto) {
        this.countPhoto = countPhoto;
    }

    public int getDayMask() {
        return dayMask;
    }

    public void setDayMask(int dayMask) {
        this.dayMask = dayMask;
    }
}
