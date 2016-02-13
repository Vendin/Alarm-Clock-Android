package com.example.av.alarm_clock.alarm_ringer;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.av.alarm_clock.R;
import com.example.av.alarm_clock.api.RemoteImage;
import com.example.av.alarm_clock.api.Requester;
import com.example.av.alarm_clock.models.ImageFile;
import com.example.av.alarm_clock.storage.ImageTableHelper;
import com.example.av.alarm_clock.storage.PreferenceConstants;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
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
                PreferenceConstants.PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );

        int necessary_friendlies_count = sharedPreferences.getInt(PreferenceConstants.NECESSARY_FRIENDLIES, 10);
        int necessary_others_count = sharedPreferences.getInt(PreferenceConstants.NECESSARY_OTHERS, 10);
        int fetch_size = sharedPreferences.getInt(PreferenceConstants.FETCH_SIZE, 100);

        ImageTableHelper imageTableHelper = new ImageTableHelper(getBaseContext());
        List<ImageFile> unshownFriendlies = imageTableHelper.getImageFiles(true,
                necessary_friendlies_count, 0);
        List<ImageFile> unshownOthers = imageTableHelper.getImageFiles(false,
                necessary_others_count, 0);

        Requester requester = new Requester(getBaseContext());

        final int MAX_USED_FOLLOWEES = 5;
        LinkedHashSet<String> followeeIDs = requester.getFolloweeIDs();
        HashSet<String> allFolloweeIDs = (HashSet<String>) followeeIDs.clone();
        deleteUnusedFollowees(followeeIDs, MAX_USED_FOLLOWEES);

        int downloadedFrs = downloadFriendlies(necessary_friendlies_count - unshownFriendlies.size(),
                followeeIDs, requester, imageTableHelper);
        int downloadedOts = downloadOthers(necessary_others_count - unshownOthers.size(),
                fetch_size, requester, imageTableHelper, allFolloweeIDs);

        imageTableHelper.removeOld(true, downloadedFrs);
        imageTableHelper.removeOld(false, downloadedOts);
    }

    private int downloadFriendlies(int downloadNum,  LinkedHashSet<String> followeeIDs,
                                   Requester requester, ImageTableHelper imageTableHelper) {
        final int FOLLOWEE_FETCH_SIZE = 3;
        int downloadedCtr = 0;

        for(String fweeID : followeeIDs) {
            List<RemoteImage> photos = requester.getFolloweePhotos(fweeID, FOLLOWEE_FETCH_SIZE);
            List<RemoteImage> filteredPhotos = new LinkedList<>();
            for (RemoteImage photo : photos) {
                boolean isInDB = imageTableHelper.getImageFile(photo.getMediaId()) != null;
                if (!isInDB) {
                    filteredPhotos.add(photo);
                }
            }
            downloadedCtr += downloadList(filteredPhotos, requester, imageTableHelper, downloadNum);
        }

        return downloadedCtr;
    }

    private int downloadOthers(int downloadNum, int fetchSize, Requester requester,
                               ImageTableHelper imageTableHelper, HashSet<String> followeeIDs) {
        List<RemoteImage> photos = requester.getOtherPhotos(downloadNum, fetchSize);
        List<RemoteImage> filteredPhotos = new LinkedList<>();
        for (RemoteImage photo : photos) {
            boolean isFollowees = followeeIDs.contains(photo.getCreatorID());
            boolean isInDB = imageTableHelper.getImageFile(photo.getMediaId()) != null;
            if (!isFollowees && !isInDB) {
                filteredPhotos.add(photo);
            }
        }
        int downloadedCtr = downloadList(filteredPhotos, requester, imageTableHelper, downloadNum);

        return downloadedCtr;
    }

    private int downloadList(List<RemoteImage> photos, Requester requester,
                             ImageTableHelper imageTableHelper, int downloadNum) {
        int downloadedCtr = 0;
        for (RemoteImage photo : photos) {
            if (downloadedCtr >= downloadNum) {
                return downloadedCtr;
            }
            if (requester.downloadPhoto(photo)) {
                imageTableHelper.saveImageFile(photo);
                downloadedCtr++;
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
