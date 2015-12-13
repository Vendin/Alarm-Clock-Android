package com.example.av.alarm_clock.alarm_ringer;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.av.alarm_clock.R;
import com.example.av.alarm_clock.models.ImageFile;
import com.example.av.alarm_clock.storage.ImageTableHelper;

import java.util.List;

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

        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences(
                getBaseContext().getString(R.string.app_pref_file),
                Context.MODE_PRIVATE
        );

        int necessary_friendlies_count = sharedPreferences.getInt("necessary_friendlies_count", 10);
        int necessary_others_count = sharedPreferences.getInt("necessary_others_count", 10);
        int fetch_size = sharedPreferences.getInt("fetch_size", 100);

        ImageTableHelper imageTableHelper = new ImageTableHelper(getBaseContext());
        List<ImageFile> unshownFriendlies = imageTableHelper.getImageFiles(true,
                necessary_friendlies_count, 0);
        List<ImageFile> unshownOthers = imageTableHelper.getImageFiles(false,
                necessary_others_count, 0);

        downloadFriendlies(necessary_friendlies_count - unshownFriendlies.size(), fetch_size);
        downloadOthers(necessary_others_count - unshownOthers.size(), fetch_size);
    }

    private int downloadFriendlies(int downloadNum, int fetchSize) {
        Log.d(this.getClass().getCanonicalName(), "Downloading " + downloadNum + " friendlies");
        return 0;
    }

    private int downloadOthers(int downloadNum, int fetchSize) {
        Log.d(this.getClass().getCanonicalName(), "Downloading " + downloadNum + " others");
        return 0;
    }


}
