package com.zetterstrom.android.list.db;

import com.zetterstrom.android.list.db.ListContract.ListEntry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ListDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database
    // version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Lists.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
            + ListEntry.TABLE_NAME + " (" + ListEntry._ID
            + " INTEGER PRIMARY KEY," + ListEntry.COLUMN_NAME_LIST_ID
            + TEXT_TYPE + COMMA_SEP + ListEntry.COLUMN_NAME_LIST_NAME
            + TEXT_TYPE + COMMA_SEP + ListEntry.COLUMN_NAME_SORT + " INTEGER"
            + " )";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
            + ListEntry.TABLE_NAME;

    public ListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy
        // is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
