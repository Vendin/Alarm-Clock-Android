package com.example.av.alarm_clock.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.av.alarm_clock.alarm_ringer.ImagePuzzle;
import com.example.av.alarm_clock.models.Alarm;
import com.example.av.alarm_clock.models.ImageFile;
import com.example.av.alarm_clock.storage.AlarmTableHelper;
import com.example.av.alarm_clock.storage.ImageTableHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Михаил on 20.12.2015.
 */
public class AlarmLoader extends AsyncTaskLoader<Alarm> {
    private final int alarmId;
    private Alarm alarm;

    public AlarmLoader(@NotNull Context context, int alarmId) {
        super(context);
        this.alarmId = alarmId;
    }

    @Override
    public Alarm loadInBackground() {
        AlarmTableHelper alarmTableHelper = new AlarmTableHelper(getContext());
        return alarm = alarmTableHelper.getAlarm(alarmId);
    }

    @Override
    protected void onStartLoading() {
        if (alarm != null) {
            deliverResult(alarm);
        } else {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        alarm = null;
    }
}
