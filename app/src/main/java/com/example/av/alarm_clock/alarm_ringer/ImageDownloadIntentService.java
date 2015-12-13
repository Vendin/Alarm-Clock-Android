package com.example.av.alarm_clock.alarm_ringer;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * It serves to a purpose of downloading images from Instagram and deleting old images.
 */
public class ImageDownloadIntentService extends IntentService {
    // com.example.av.alarm_clock.alarm_ringer.ImageDownloadIntentService
    private static final String ACTION_DOWNLOAD_IMAGES = "com.example.av.alarm_clock.alarm_ringer.ImageDownloadIntentService.ACTION_DOWNLOAD_IMAGES";
    public ImageDownloadIntentService() {
        super("ImageDownloadIntentService");
    }

    public static void startActionUploadImages(Context context) {
        Intent intent = new Intent(context, ImageDownloadIntentService.class);
        intent.setAction(ACTION_DOWNLOAD_IMAGES);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD_IMAGES.equals(action)) {
                handleActionDownloadImages();
            }
        }
    }

    private void handleActionDownloadImages() {
        Log.d(this.getClass().getCanonicalName(), "started download images action");
    }
}
