package com.example.av.alarm_clock.alarm_ringer;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.av.alarm_clock.models.Alarm;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Михаил on 13.12.2015.
 */
public class AlarmRegistrator {
    private AlarmRegistrator(){}

    public static void registerAlarms(Context context, List<Alarm> alarmList) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, RingADingDingReceiver.class);
        intent.setAction(RingADingDingReceiver.RING_A_DING_DING);

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        short current_hour = (short) calendar.get(Calendar.HOUR_OF_DAY);
        short current_minute = (short) calendar.get(Calendar.MINUTE);

        for (Alarm alarm: alarmList) {
            if (alarm.isEnabled() && alarm.getHour() >= current_hour &&
                    alarm.getMinute() >= current_minute) {
                calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
                calendar.set(Calendar.MINUTE, alarm.getMinute());
                calendar.set(Calendar.MILLISECOND, 0);
                intent.putExtra(RingADingDingReceiver.EXTRA_ID, alarm.getId());

                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, alarm.getId(),
                        intent, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, alarmIntent);
            }
        }
    }

    public static void unregisterAlarm(Context context, int alarmId) {
        Intent intent = new Intent(context, RingADingDingReceiver.class);
        intent.setAction(RingADingDingReceiver.RING_A_DING_DING);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }
}
