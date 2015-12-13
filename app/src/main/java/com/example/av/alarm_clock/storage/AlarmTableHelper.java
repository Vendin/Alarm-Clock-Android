package com.example.av.alarm_clock.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.av.alarm_clock.alarm_ringer.AlarmRegistrator;
import com.example.av.alarm_clock.storage.AlarmContract.AlarmEntry;

import com.example.av.alarm_clock.models.Alarm;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;

/**
 * Created by mikrut on 29.11.15.
 */
public class AlarmTableHelper {

    private AlarmClockDbHelper dbHelper;
    private Context context;

    public AlarmTableHelper(Context context) {
        this.context = context;
        dbHelper = new AlarmClockDbHelper(context);
    }

    public void updateAlarm(@NotNull Alarm alarm) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = alarm.getContentValues();
        String whereClause = AlarmEntry._ID + " = ?";
        String[] whereArgs={alarm.getId().toString()};
        db.update(AlarmEntry.TABLE_NAME, values, whereClause, whereArgs);
        registerAlarms();
    }

    public long saveAlarm(@NotNull Alarm alarm) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = alarm.getContentValues();
        long result = db.insert(AlarmEntry.TABLE_NAME, null, values);
        registerAlarms();
        return result;
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
            Alarm alarm = new Alarm(cursor);
            alarms.add(alarm);
        }

        return alarms;
    }

    @Nullable
    public Alarm getAlarm(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection =  AlarmEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        Cursor cursor = db.query(AlarmEntry.TABLE_NAME, AlarmContract.ALARM_PROJECTION,
                selection, selectionArgs,
                null, null, null,
                "1");
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            Alarm alarm = new Alarm(cursor);
            return alarm;
        } else {
            return null;
        }
    }

    public void deleteAlarm(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = AlarmEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        db.delete(AlarmEntry.TABLE_NAME, selection, selectionArgs);
        registerAlarms();
    }

    protected void registerAlarms() {
        AlarmRegistrator.registerAlarms(context, getAlarms());
    }
}
