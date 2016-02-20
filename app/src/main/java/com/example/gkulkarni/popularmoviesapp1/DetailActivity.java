package com.example.gkulkarni.popularmoviesapp1;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class DetailActivity extends AppCompatActivity {
    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
//                fab.setImageDrawable(getResources().getDrawable(android.support.design.R.drawable.abc_btn_rating_star_on_mtrl_alpha, getApplicationContext().getTheme()));

                Cursor retCursor = null;
                Intent intent = getIntent();
                if (intent != null) {
                    Movie movie = (Movie) intent.getSerializableExtra("movie");
                    Snackbar.make(view, movie.original_title+" added to Favourites!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    FavouritesDbHelper fDbHelper = new FavouritesDbHelper(getApplicationContext());
                    final SQLiteDatabase db = fDbHelper.getWritableDatabase();
                    ContentValues favValues = new ContentValues();
                    favValues.put(FavouritesContract.FavouritesEntry.COLUMN_FAVOURITE_ID, movie.id);
                    long _id = db.insert(FavouritesContract.FavouritesEntry.TABLE_NAME, null, favValues);

//                    if ( _id > 0 ) {
//                        retCursor = fDbHelper.getReadableDatabase().query(
//                                FavouritesContract.FavouritesEntry.TABLE_NAME,
//                                null,
//                                null,
//                                null,
//                                null,
//                                null,
//                                null
//                        );
//                    }
//                    if(retCursor.moveToFirst()) {
//                        do {
//                            Log.i(LOG_TAG, "aaaaa= "+retCursor.getString(1));
//                        } while(retCursor.moveToNext());
//                    }
//
//                    retCursor.close();
                    db.close();
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }
