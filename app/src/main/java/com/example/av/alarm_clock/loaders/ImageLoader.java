package com.example.av.alarm_clock.loaders;

import android.content.Context;
import android.content.AsyncTaskLoader;
import android.util.Log;

import com.example.av.alarm_clock.alarm_ringer.ImagePuzzle;
import com.example.av.alarm_clock.models.ImageFile;
import com.example.av.alarm_clock.storage.ImageTableHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Михаил on 20.12.2015.
 */
public class ImageLoader extends AsyncTaskLoader<List<ImagePuzzle>> {
    private static final int NEED_FRIEND = 3;
    private static final int NEED_OTHERS = 3;
    private static final int NEED_TOTAL  = NEED_FRIEND + NEED_OTHERS;

    List<ImagePuzzle> mPuzzles;

    public ImageLoader(@NotNull Context context) {
        super(context);
    }

    @Override
    public List<ImagePuzzle> loadInBackground() {
        ImageTableHelper imageTableHelper = new ImageTableHelper(getContext());
        List<ImagePuzzle> puzzles = new ArrayList<>();

        List<ImageFile> unshownFrs = imageTableHelper.getImageFiles(true, NEED_FRIEND, 0);
        List<ImageFile> unshownOts = imageTableHelper.getImageFiles(false, NEED_OTHERS, 0);

        int necessaryAdditional = NEED_TOTAL - unshownFrs.size() - unshownOts.size();
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
