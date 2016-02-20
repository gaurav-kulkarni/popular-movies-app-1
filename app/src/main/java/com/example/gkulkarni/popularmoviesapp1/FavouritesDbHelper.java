package com.example.gkulkarni.popularmoviesapp1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.gkulkarni.popularmoviesapp1.FavouritesContract.FavouritesEntry;

/**
 * Created by gkulkarni on 17-02-2016.
 */
public class FavouritesDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movie.db";

    public FavouritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold favourites.
        final String SQL_CREATE_FAVOURITE_TABLE = "CREATE TABLE " + FavouritesEntry.TABLE_NAME + " (" +
                FavouritesEntry._ID + " INTEGER PRIMARY KEY," +
                FavouritesEntry.COLUMN_FAVOURITE_ID + " INTEGER UNIQUE NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavouritesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}


