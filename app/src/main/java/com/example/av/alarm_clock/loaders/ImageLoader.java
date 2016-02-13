package com.example.av.alarm_clock.loaders;

import android.content.Context;
import android.content.AsyncTaskLoader;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.av.alarm_clock.R;
import com.example.av.alarm_clock.alarm_ringer.ImagePuzzle;
import com.example.av.alarm_clock.models.ImageFile;
import com.example.av.alarm_clock.storage.ImageTableHelper;
import com.example.av.alarm_clock.storage.PreferenceConstants;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Михаил on 20.12.2015.
 */
public class ImageLoader extends AsyncTaskLoader<List<ImagePuzzle>> {
    public static final String TOTAL_PICTURES =
            ImageLoader.class.getCanonicalName() + ".TOTAL_PICTURES";
    private static final int DEFAULT_TOTAL = 6;

    private List<ImagePuzzle> mPuzzles;
    private int pictureNumber;

    public ImageLoader(@NotNull Context context, Integer pictureNumber) {
        super(context);
        if (pictureNumber != null && pictureNumber >= 0 && pictureNumber <= 10) {
            this.pictureNumber = pictureNumber;
        } else {
            this.pictureNumber = DEFAULT_TOTAL;
        }

        SharedPreferences sharedPreferences = getContext()
                .getSharedPreferences(PreferenceConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(PreferenceConstants.ACCESS_TOKEN, null);
        if (token == null || token.equals(PreferenceConstants.USER_IS_GUEST)) {
            this.pictureNumber = 0;
        }
    }

    @Override
    public List<ImagePuzzle> loadInBackground() {
        ImageTableHelper imageTableHelper = new ImageTableHelper(getContext());
        List<ImagePuzzle> puzzles = new ArrayList<>();

        int needOthers = pictureNumber / 2;
        int needFriend = pictureNumber - needOthers;
        List<ImageFile> unshownFrs = imageTableHelper.getImageFiles(true, needFriend, 0);
        List<ImageFile> unshownOts = imageTableHelper.getImageFiles(false, needOthers, 0);

        int necessaryAdditional = pictureNumber - unshownFrs.size() - unshownOts.size();
        List<ImageFile> shownFrs = imageTableHelper.getImageFiles(true, necessaryAdditional,
                0, true);
        necessaryAdditional = necessaryAdditional - shownFrs.size();
        List<ImageFile> shownOts = imageTableHelper.getImageFiles(false, necessaryAdditional,
                0, true);

        ImagePuzzle.addListToList(getContext(), puzzles, unshownFrs);
        ImagePuzzle.addListToList(getContext(), puzzles, unshownOts);
        ImagePuzzle.addListToList(getContext(), puzzles, shownFrs);
        ImagePuzzle.addListToList(getContext(), puzzles, shownOts);

        // Randomizing puzzles order
        Collections.shuffle(puzzles);

        for (ImagePuzzle puzzle : puzzles) {
            puzzle.getImageFile().setShown(true); // Caching in memory + marking image as shown
            imageTableHelper.updateImageFile(puzzle.getImageFile());
        }
        return puzzles;
    }

    @Override
    protected void onStartLoading() {
        if (mPuzzles != null) {
            deliverResult(mPuzzles);
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
        mPuzzles = null;
    }
}
