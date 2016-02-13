package com.example.av.alarm_clock.alarm_ringer;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.example.av.alarm_clock.R;

import java.io.IOException;

/**
 * Created by Михаил on 20.12.2015.
 */
public class MosEisleyOrchestraService extends Service implements MediaPlayer.OnPreparedListener {
    private final static String STOP_ACTION = MosEisleyOrchestraService.class.getCanonicalName() + "STOP_ACTION";
    private final static int ALARM_NOTIFICATION_ID = 1;

    public final static String RINGTONE_URI =
            MosEisleyOrchestraService.class.getCanonicalName() + ".RINGTONE_URI";
    public final static String VIBRATE =
            MosEisleyOrchestraService.class.getCanonicalName() + ".VIBRATE";

    private boolean intentReceived = false;

    private Uri ringtoneUri;
    private boolean vibrate;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

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

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setLooping(true);
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
        String uri = null;
        if (intent.hasExtra(RINGTONE_URI)) {
            uri = intent.getStringExtra(RINGTONE_URI);
        }
        if (uri != null) {
            ringtoneUri = Uri.parse(uri);
        } else {
            ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }

        try {
            mediaPlayer.setDataSource(this, ringtoneUri);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        vibrate = intent.getBooleanExtra(VIBRATE, false);
        if (vibrate) {
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(5*60*1000); // Vibrate 5 minutes MAX
        }

        if (!mediaPlayer.isPlaying())
            mediaPlayer.prepareAsync();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        if (vibrator != null) {
            vibrator.cancel();
            vibrator = null;
        }
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
