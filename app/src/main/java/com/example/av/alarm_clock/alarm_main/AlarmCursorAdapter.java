package com.example.av.alarm_clock.alarm_main;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.example.av.alarm_clock.Alarm;
import com.example.av.alarm_clock.R;
import com.example.av.alarm_clock.storage.AlarmContract;
import com.example.av.alarm_clock.storage.AlarmTableHelper;

/**
 * Created by Михаил on 12.12.2015.
 */
public class AlarmCursorAdapter extends CursorAdapter {
    private static final String timeFormat = "%d:%d";
    static final int TAG_ID = R.id.alarmTagId;
    protected Context context;

    public AlarmCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.alarm_element_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView timeTextView  = (TextView) view.findViewById(R.id.timeTextView);
        Switch   enabledSwitch = (Switch) view.findViewById(R.id.enabledSwitch);

        int id = cursor.getInt(AlarmContract.PROJECTION_ID_INDEX);
        view.setTag(TAG_ID, id);

        int hour   = cursor.getInt(AlarmContract.PROJECTION_HOUR_INDEX);
        int minute = cursor.getInt(AlarmContract.PROJECTION_MINUTE_INDEX);
        boolean enabled = cursor.getInt(AlarmContract.PROJECTION_IS_ENABLED_INDEX) != 0;

        String timeText = String.format(timeFormat, hour, minute);
        timeTextView.setText(timeText);
        enabledSwitch.setChecked(enabled);

        enabledSwitch.setOnCheckedChangeListener(switchCheckListenter);
    }

    CompoundButton.OnCheckedChangeListener switchCheckListenter =
            new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            View parentView = (View) buttonView.getParent();
            int alarmId = (Integer) parentView.getTag(TAG_ID);

            AlarmTableHelper alarmTableHelper = new AlarmTableHelper(context);
            Alarm alarm = alarmTableHelper.getAlarm(alarmId);
            if (alarm != null && alarm.isEnabled() != isChecked) {
                alarm.setEnabled(isChecked);
                alarmTableHelper.updateAlarm(alarm);
            }
        }
    };
}
