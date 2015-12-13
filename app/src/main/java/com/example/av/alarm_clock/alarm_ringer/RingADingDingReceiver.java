package com.example.av.alarm_clock.alarm_ringer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.av.alarm_clock.R;
import com.example.av.alarm_clock.storage.AlarmTableHelper;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class RingADingDingReceiver extends BroadcastReceiver {
    private static final String prefix = RingADingDingReceiver.class.getCanonicalName();
    public static final String RING_A_DING_DING = prefix + ".RING_A_DING_DING";
    public static final String DAILY_DOWNLOAD = prefix + ".DAILY_DOWNLOAD";
    // TODO: Add (?) alarm id support
    // public static final String EXTRA_ID = RING_A_DING_DING + ":ID";

    public RingADingDingReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(RING_A_DING_DING)) {
            ring(context, intent);
        } else if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION) ||
                  action.equals(DAILY_DOWNLOAD)) {
            tryToUpload(context, intent);
        } else if (action.equals("android.intent.action.BOOT_COMPLETED") ||
                action.equals("android.intent.action.QUICKBOOT_POWERON")) {
            makeDailySetup(context, intent);
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

    protected void tryToUpload(Context context, Intent intent) {
        Calendar calendar = GregorianCalendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_YEAR);

        SharedPreferences preferences = context.getSharedPreferences(
                context.getString(R.string.app_pref_file), Context.MODE_PRIVATE);
        int lastUpload = preferences.getInt("last_upload_day", today + 1);

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        boolean connected = info != null && info.isAvailable() && info.isConnected();

        if (lastUpload != today && connected) {
            ImageDownloadIntentService.startActionUploadImages(context);
        }
    }

    protected void makeDailySetup(Context context, Intent intent) {
        AlarmTableHelper alarmTableHelper = new AlarmTableHelper(context);
        AlarmRegistrator.registerAlarms(context, alarmTableHelper.getAlarms());

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent downloadIntent = new Intent(context, RingADingDingReceiver.class);
        downloadIntent.setAction(DAILY_DOWNLOAD);

        PendingIntent scheduledDownloadIntent = PendingIntent.getBroadcast(context, 0,
                downloadIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 12);

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, scheduledDownloadIntent);
    }
}
