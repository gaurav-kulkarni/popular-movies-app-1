package com.example.gkulkarni.popularmoviesapp1;

import android.provider.BaseColumns;

/**
 * Created by gkulkarni on 17-02-2016.
 */
public class FavouritesContract {

    public static final class FavouritesEntry implements BaseColumns {

        public static final String TABLE_NAME = "favourite";

        public static final String COLUMN_FAVOURITE_ID = "favourite_id";
    }
}
