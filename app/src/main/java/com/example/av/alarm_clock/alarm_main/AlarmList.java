package com.example.av.alarm_clock.alarm_main;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.av.alarm_clock.R;
import com.example.av.alarm_clock.storage.AlarmTableHelper;

public class AlarmList extends AppCompatActivity {
    private ListView alarmListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        alarmListView = (ListView) findViewById(R.id.alarmListView);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlarmList.this, AlarmSetup.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        AlarmTableHelper alarmTableHelper = new AlarmTableHelper(this);
        Cursor cursor = alarmTableHelper.getAlarmsCursor();

        AlarmCursorAdapter alarmCursorAdapter = new AlarmCursorAdapter(this, cursor, 0);
        alarmListView.setAdapter(alarmCursorAdapter);
        alarmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int alarmId = (Integer) view.getTag(AlarmCursorAdapter.TAG_ID);
                Intent intent = new Intent(AlarmList.this, AlarmSetup.class);
                intent.putExtra(AlarmSetup.ALARM_ID_PARAMETER, alarmId);
                startActivity(intent);
            }
        });
    }

}
