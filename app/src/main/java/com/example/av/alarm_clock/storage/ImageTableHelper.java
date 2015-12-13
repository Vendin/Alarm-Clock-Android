package com.example.av.alarm_clock.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;

import com.example.av.alarm_clock.models.ImageFile;
import com.example.av.alarm_clock.storage.ImageContract;
import com.example.av.alarm_clock.storage.ImageContract.ImageEntry;

/**
 * Created by Михаил on 13.12.2015.
 */
public class ImageTableHelper {
    private final Context context;
    private AlarmClockDbHelper dbHelper;

    public final static String[] IMAGE_PROJECTION = {
            ImageEntry._ID,
            ImageEntry.COLUMN_NAME_FILENAME,
            ImageEntry.COLUMN_NAME_FRIENDLY,
            ImageEntry.COLUMN_NAME_MEDIA_ID
    };

    public static final int PROJECTION_ID_INDEX = 0;
    public static final int PROJECTION_FILENAME_INDEX = 1;
    public static final int PROJECTION_FRIENDLY_INDEX = 2;
    public static final int PROJECTION_MEDIA_ID_INDEX = 3;


    public ImageTableHelper(Context context) {
        this.context = context;
        dbHelper = new AlarmClockDbHelper(context);
    }

    public long saveImageFile(ImageFile imageFile) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = imageFile.getContentValues();
        long result = db.insert(ImageEntry.TABLE_NAME, null, values);
        return result;
    }

    public void updateImageFile(ImageFile imageFile) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = imageFile.getContentValues();
        String whereClause = ImageEntry._ID + " = ?";
        String[] whereArgs = {
          imageFile.getId().toString()
        };
        db.update(ImageEntry.TABLE_NAME, values, whereClause, whereArgs);
    }

    public void deleteImageFile(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = ImageEntry._ID + " = ?";
        String[] whereArgs = {
                String.valueOf(id)
        };
        db.delete(ImageEntry.TABLE_NAME, whereClause, whereArgs);
    }

    public Cursor getImagesCursor(boolean friendly, int limitation, int offset) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String whereClause = ImageEntry.COLUMN_NAME_FRIENDLY + (friendly ? " > 0" : " = 0");
        return db.query(
                ImageEntry.TABLE_NAME,
                IMAGE_PROJECTION,
                whereClause, // Return all friendlies
                null,
                null,
                null,
                null,
                String.valueOf(offset) + "," + String.valueOf(limitation)
        );
    }
}
