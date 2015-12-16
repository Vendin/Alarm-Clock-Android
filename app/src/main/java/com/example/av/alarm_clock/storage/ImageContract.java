package com.example.av.alarm_clock.storage;

import android.provider.BaseColumns;

/**
 * Created by Михаил on 13.12.2015.
 */
public class ImageContract {
    private ImageContract(){}

    public static final String CREATE_TABLE = "CREATE TABLE " +
            ImageEntry.TABLE_NAME + " (" +
            ImageEntry._ID + " INTEGER PRIMARY KEY," +
            ImageEntry.COLUMN_NAME_FILENAME + " TEXT," +
            ImageEntry.COLUMN_NAME_FRIENDLY + " BOOLEAN," +
            ImageEntry.COLUMN_NAME_SHOWN + " BOOLEAN," +
            ImageEntry.COLUMN_NAME_MEDIA_ID + " TEXT" + ")";
    public static final String DELETE_TABLE = "DROP TABLE IF EXISTS" + ImageEntry.TABLE_NAME;

    public static abstract class ImageEntry implements BaseColumns {
        public static final String TABLE_NAME = "images";

        public static final String COLUMN_NAME_FILENAME = "filename";
        public static final String COLUMN_NAME_FRIENDLY = "friendly";
        public static final String COLUMN_NAME_MEDIA_ID = "media_id";
        public static final String COLUMN_NAME_SHOWN    = "shown";
    }
}
