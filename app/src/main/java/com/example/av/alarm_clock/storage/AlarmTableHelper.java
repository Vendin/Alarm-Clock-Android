package com.example.av.alarm_clock.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.av.alarm_clock.storage.AlarmContract.AlarmEntry;

import com.example.av.alarm_clock.Alarm;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by mikrut on 29.11.15.
 */
public class AlarmTableHelper {

    private AlarmClockDbHelper dbHelper;

    public AlarmTableHelper(Context context) {
        dbHelper = new AlarmClockDbHelper(context);
    }

    public long saveAlarm(@NotNull Alarm alarm) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AlarmEntry.COLUMN_NAME_ALARM_HOUR, alarm.getHour());
        values.put(AlarmEntry.COLUMN_NAME_ALARM_MINUTE, alarm.getMinute());
        values.put(AlarmEntry.COLUMN_NAME_ALARM_IS_ENABLED, alarm.isEnabled());

        return db.insert(AlarmEntry.TABLE_NAME, null, values);
    }

    @NotNull
    public Cursor getAlarmsCursor() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        final String sortOrder = AlarmEntry.COLUMN_NAME_ALARM_HOUR + " ASC," +
                AlarmEntry.COLUMN_NAME_ALARM_MINUTE + " ASC";
        return db.query(
                AlarmEntry.TABLE_NAME,
                AlarmContract.ALARM_PROJECTION,
                null, // Return all alarms
                null, // We don't use WHERE so we don't need args
                null, // We don't group rows
                null, // We don't filter groups
                sortOrder
        );
    }

    @NotNull
    public ArrayList<Alarm> getAlarms() {
        ArrayList<Alarm> alarms = new ArrayList<>();
        Cursor cursor = getAlarmsCursor();

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Alarm alarm = new Alarm();
            alarm.setHour(cursor.getShort(AlarmContract.PROJECTION_HOUR_INDEX));
            alarm.setMinute(cursor.getShort(AlarmContract.PROJECTION_MINUTE_INDEX));
            alarm.setEnabled(cursor.getInt(AlarmContract.PROJECTION_IS_ENABLED_INDEX) > 0);
            alarm.setId(cursor.getInt(AlarmContract.PROJECTION_ID_INDEX));
            alarms.add(alarm);
        }

        return alarms;
    }

    public void deleteAlarm(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = AlarmEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        db.delete(AlarmEntry.TABLE_NAME, selection, selectionArgs);
    }
}
