package com.example.av.alarm_clock.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.av.alarm_clock.storage.ImageContract;
import com.example.av.alarm_clock.storage.ImageTableHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Михаил on 13.12.2015.
 */
public class ImageFile {
    private String filename;
    private String mediaId;
    private Integer id;
    private boolean friendly = false;
    private boolean shown = false;

    private String photoURL;

    public static final String FOLDER_NAME = "images";

    public ImageFile() {}

    public ImageFile(@NotNull Cursor cursor) {
        setFilename(cursor.getString(ImageTableHelper.PROJECTION_FILENAME_INDEX));
        setMediaId(cursor.getString(ImageTableHelper.PROJECTION_MEDIA_ID_INDEX));
        setId(cursor.getInt(ImageTableHelper.PROJECTION_ID_INDEX));
        setFriendly(cursor.getInt(ImageTableHelper.PROJECTION_FRIENDLY_INDEX) > 0);
        setShown(cursor.getInt(ImageTableHelper.PROJECTION_SHOWN_INDEX) > 0);
    }

    @NotNull
    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(ImageContract.ImageEntry.COLUMN_NAME_FILENAME, getFilename());
        cv.put(ImageContract.ImageEntry.COLUMN_NAME_FRIENDLY, isFriendly());
        cv.put(ImageContract.ImageEntry.COLUMN_NAME_MEDIA_ID, getMediaId());
        cv.put(ImageContract.ImageEntry.COLUMN_NAME_SHOWN, isShown());
        return cv;
    }

    public static File getImagesFolder(Context context) {
        return new File(context.getFilesDir(), FOLDER_NAME);
    }

    @Nullable
    public static ImageFile fromJSONObject(JSONObject imageObject) throws JSONException {
        String type = imageObject.getString("type");

        ImageFile imageFile = null;
        if (type.equals("image")) {
            String imageMediaID = imageObject.getString("id");
            String imageLink    = imageObject.getJSONObject("images").getJSONObject("standard_resolution").getString("url");

            imageFile = new ImageFile();
            imageFile.setMediaId(imageMediaID);
            imageFile.setPhotoURL(imageLink);
        }

        return imageFile;
    }

    @Nullable
    public Bitmap getBitmap(Context context) {

        File imgFile = new File(getImagesFolder(context), filename);

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

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
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

    public boolean isShown() {
        return shown;
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }
}
