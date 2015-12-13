package com.example.av.alarm_clock.alarm_ringer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.av.alarm_clock.Alarm;
import com.example.av.alarm_clock.storage.AlarmTableHelper;

public class RingADingDingReceiver extends BroadcastReceiver {
    private static final String prefix = RingADingDingReceiver.class.getCanonicalName();
    public static final String RING_A_DING_DING = prefix + ".RING_A_DING_DING";
    // TODO: Add (?) alarm id support
    // public static final String EXTRA_ID = RING_A_DING_DING + ":ID";

    public RingADingDingReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(RING_A_DING_DING)) {
            ring(context, intent);
        } else {
            throw new UnsupportedOperationException("Not implemented");
        }
    }

    protected void ring(Context context, Intent intent) {
        Intent startRingingIntent = new Intent(context, RiseAndShineMrFreemanActivity.class);
        startRingingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
            Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(startRingingIntent);
    }
}
