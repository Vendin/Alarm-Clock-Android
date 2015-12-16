package com.example.av.alarm_clock.alarm_ringer;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.av.alarm_clock.models.ImageFile;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * Created by mikrut on 16.12.15.
 */
public class ImagePuzzle {
    public enum State {UNGUESSED, RECOGNIZED, MISRECOGNIZED};

    private State state = State.UNGUESSED;
    private ImageFile imageFile;
    private Bitmap cachedBitmap;
    private Context context;

    public ImagePuzzle(Context context, ImageFile imageFile) {
        this.imageFile = imageFile;
        this.context = context;
        if (imageFile == null || context == null)
            throw new InvalidParameterException("Image file and context cannot be null");
    }

    public State guess(boolean friendly) {
        if (state == State.UNGUESSED) {
            if (imageFile.isFriendly() == friendly) {
                state = State.RECOGNIZED;
            } else {
                state = State.MISRECOGNIZED;
            }
        }
        return state;
    }

    public State getState() {
        return state;
    }

    public Bitmap getBitmap() {
        if (cachedBitmap == null)
            cachedBitmap = imageFile.getBitmap(context);
        return  cachedBitmap;
    }

    public long getDbId() {
        return imageFile.getId();
    }

    public static void addListToList(Context context, List<ImagePuzzle> appendee,
                                     List<ImageFile> appendable) {
        for (ImageFile imageFile : appendable) {
            appendee.add(new ImagePuzzle(context, imageFile));
        }
    }
}
