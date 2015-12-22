package com.example.av.alarm_clock.alarm_ringer;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.example.av.alarm_clock.R;

import java.io.IOException;

/**
 * Created by Михаил on 20.12.2015.
 */
public class MosEisleyOrchestraService extends Service implements MediaPlayer.OnPreparedListener {
    private final static String STOP_ACTION = MosEisleyOrchestraService.class.getCanonicalName() + "STOP_ACTION";
    private final static int ALARM_NOTIFICATION_ID = 1;

    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        Intent closeIntent = new Intent(this, MosEisleyOrchestraService.class);
        closeIntent.setAction(STOP_ACTION);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, closeIntent, 0);

        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_alarm_black_18dp)
                .setTicker("Disable the alarm")
                .setContentTitle("Будильник")
                .setContentText("Чтобы отключить будильник, нажмите на это сообщение")
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setOngoing(true);

        Notification notification = builder.getNotification();
        startForeground(ALARM_NOTIFICATION_ID, notification);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setVolume(1.0f, 1.0f);
        try {
            mediaPlayer.setDataSource(this,
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null && intent.getAction().equals(STOP_ACTION)) {
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
