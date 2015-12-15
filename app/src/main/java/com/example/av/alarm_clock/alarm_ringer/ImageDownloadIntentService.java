package com.example.av.alarm_clock.alarm_ringer;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.av.alarm_clock.R;
import com.example.av.alarm_clock.api.Requester;
import com.example.av.alarm_clock.models.ImageFile;
import com.example.av.alarm_clock.storage.ImageTableHelper;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

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

        Requester requester = new Requester(getBaseContext());

        final int MAX_USED_FOLLOWEES = 5;
        LinkedHashSet<String> followeeIDs = requester.getFolloweeIDs();
        deleteUnusedFollowees(followeeIDs, MAX_USED_FOLLOWEES);

        int downloadedFrs = downloadFriendlies(necessary_friendlies_count - unshownFriendlies.size(),
                followeeIDs, requester, imageTableHelper);
        int downloadedOts = downloadOthers(necessary_others_count - unshownOthers.size(),
                fetch_size, requester, imageTableHelper);

        imageTableHelper.removeOld(true, downloadedFrs);
        imageTableHelper.removeOld(false, downloadedOts);
    }

    private int downloadFriendlies(int downloadNum,  LinkedHashSet<String> followeeIDs,
                                   Requester requester, ImageTableHelper imageTableHelper) {
        Log.d(this.getClass().getCanonicalName(), "Downloading " + downloadNum + " friendlies");
        final int FOLLOWEE_FETCH_SIZE = 3;
        int downloadedCtr = 0;

        for(String fweeID : followeeIDs) {
            List<ImageFile> photos = requester.getFolloweePhotos(fweeID, FOLLOWEE_FETCH_SIZE);
            downloadedCtr += downloadList(photos, requester, imageTableHelper, downloadNum);
        }

        Log.d(this.getClass().getCanonicalName(), "Downloaded " + downloadedCtr + " friendlies");
        return downloadedCtr;
    }

    private int downloadOthers(int downloadNum, int fetchSize, Requester requester, ImageTableHelper imageTableHelper) {
        Log.d(this.getClass().getCanonicalName(), "Downloading " + downloadNum + " others");

        List<ImageFile> photos = requester.getOtherPhotos(downloadNum, fetchSize);
        Log.d(this.getClass().getCanonicalName(), "Found " + photos.size() + " others");
        int downloadedCtr = downloadList(photos, requester, imageTableHelper, downloadNum);;
        Log.d(this.getClass().getCanonicalName(), "Downloaded " + downloadedCtr + " others");

        return downloadedCtr;
    }

    private int downloadList(List<ImageFile> photos, Requester requester,
                             ImageTableHelper imageTableHelper, int downloadNum) {
        int downloadedCtr = 0;
        for (ImageFile photo : photos) {
            if (requester.downloadPhoto(photo)) {
                imageTableHelper.saveImageFile(photo);
                downloadedCtr++;
                if (downloadedCtr >= downloadNum) {
                    return downloadedCtr;
                }
            }
        }
        return downloadedCtr;
    }

    private void deleteUnusedFollowees(LinkedHashSet<String> followeeIDs, int maxFollowees) {
        Random rand = new Random();
        while(followeeIDs.size() > maxFollowees) {
            int index = rand.nextInt(followeeIDs.size());
            Iterator<String> i = followeeIDs.iterator();
            String id = i.next();
            for(int ctr = 0; ctr < index; ctr++) {
                id = i.next();
            }
            followeeIDs.remove(id);
        }
    }
}
