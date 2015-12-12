package com.example.av.alarm_clock.alarm_main;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TimePicker;

import com.example.av.alarm_clock.Alarm;
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

        Intent callerIntent = getIntent();
        if (callerIntent.hasExtra(ALARM_ID_PARAMETER)) {
            alarmID = callerIntent.getIntExtra(ALARM_ID_PARAMETER, 0);
        }
        timePicker = (TimePicker) findViewById(R.id.timePicker);

        GregorianCalendar now = new GregorianCalendar();
        timePicker.setCurrentHour(now.get(Calendar.HOUR));
        timePicker.setCurrentMinute(now.get(Calendar.MINUTE));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveAction:
                saveAlarm();
            case R.id.cancelAction:
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
}
