package com.example.av.alarm_clock.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Михаил on 13.12.2015.
 */
public class ImageFile {
    private String filename;
    private Integer mediaId;
    private Integer id;
    private boolean friendly;

    public static final String folderName = "images";

    public ImageFile(@NotNull Cursor cursor) {

    }

    @NotNull
    public ContentValues getContentValues() {
        return null;
    }

    @Nullable
    public Bitmap getBitmap(Context context) {
        File imagesDir = new File(context.getFilesDir(), folderName);
        File imgFile = new File(imagesDir, filename);

        if (imgFile.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(imgFile);
                Bitmap myBitmap = BitmapFactory.decodeStream(fileInputStream);
                return myBitmap;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getMediaId() {
        return mediaId;
    }

    public void setMediaId(Integer mediaId) {
        this.mediaId = mediaId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isFriendly() {
        return friendly;
    }

    public void setFriendly(boolean friendly) {
        this.friendly = friendly;
    }
}
