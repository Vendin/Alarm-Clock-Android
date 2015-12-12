package com.example.av.alarm_clock.alarm_main;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.example.av.alarm_clock.R;
import com.example.av.alarm_clock.storage.AlarmContract;

/**
 * Created by Михаил on 12.12.2015.
 */
public class AlarmCursorAdapter extends CursorAdapter {
    private static final String timeFormat = "%d:%d";
    public AlarmCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.alarm_element_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView timeTextView  = (TextView) view.findViewById(R.id.timeTextView);
        Switch   enabledSwitch = (Switch) view.findViewById(R.id.enabledSwitch);

        int hour   = cursor.getInt(AlarmContract.PROJECTION_HOUR_INDEX);
        int minute = cursor.getInt(AlarmContract.PROJECTION_MINUTE_INDEX);
        boolean enabled = cursor.getInt(AlarmContract.PROJECTION_IS_ENABLED_INDEX) != 0;

        String timeText = String.format(timeFormat, hour, minute);
        timeTextView.setText(timeText);
        enabledSwitch.setChecked(enabled);
    }
}
