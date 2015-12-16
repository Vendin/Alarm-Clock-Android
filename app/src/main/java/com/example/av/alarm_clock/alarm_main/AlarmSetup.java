package com.example.av.alarm_clock.alarm_main;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TimePicker;

import com.example.av.alarm_clock.models.Alarm;
import com.example.av.alarm_clock.R;
import com.example.av.alarm_clock.storage.AlarmTableHelper;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlarmSetup extends AppCompatActivity {
    public static final String ALARM_ID_PARAMETER = AlarmSetup.class.getCanonicalName() + ":ALARM_ID_PARAMETER";

    private Integer alarmID;
    private TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setup);

        timePicker = (TimePicker) findViewById(R.id.timePicker);

        GregorianCalendar now = new GregorianCalendar();
        timePicker.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(now.get(Calendar.MINUTE));

        Intent callerIntent = getIntent();
        if (callerIntent.hasExtra(ALARM_ID_PARAMETER)) {
            alarmID = callerIntent.getIntExtra(ALARM_ID_PARAMETER, 0);
            AlarmTableHelper alarmTableHelper = new AlarmTableHelper(this);
            Alarm alarm = alarmTableHelper.getAlarm(alarmID);
            if (alarm != null) {
                timePicker.setCurrentHour((int) alarm.getHour());
                timePicker.setCurrentMinute((int) alarm.getMinute());
            } else {
                alarmID = null;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm_edit, menu);
        if (alarmID == null) {
            MenuItem item = menu.findItem(R.id.deleteAction);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveAction:
                saveAlarm();
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.deleteAction:
                deleteAlarm();
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void saveAlarm() {
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();

        AlarmTableHelper alarmTableHelper = new AlarmTableHelper(this);
        Alarm alarmToSave = null;
        if (alarmID != null)
            alarmToSave = alarmTableHelper.getAlarm(alarmID);
        if (alarmToSave == null) {
            alarmToSave = new Alarm();
            alarmToSave.setHour((short) hour);
            alarmToSave.setMinute((short) minute);
            alarmToSave.setEnabled(true);
            alarmTableHelper.saveAlarm(alarmToSave);
        } else {
            alarmToSave.setHour((short) hour);
            alarmToSave.setMinute((short) minute);
            alarmTableHelper.updateAlarm(alarmToSave);
        }
    }

    protected void deleteAlarm() {
        AlarmTableHelper alarmTableHelper = new AlarmTableHelper(this);
        alarmTableHelper.deleteAlarm(alarmID);
    }
}
