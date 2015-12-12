package com.example.av.alarm_clock.alarm_main;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TimePicker;

import com.example.av.alarm_clock.R;

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
}
