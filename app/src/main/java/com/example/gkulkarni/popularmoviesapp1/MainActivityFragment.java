package com.example.gkulkarni.popularmoviesapp1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private final String api_key = "";
    private MovieAdapter movieAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        // Get a reference to the GridView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movieAdapter.getItem(position);
//                Toast.makeText(getActivity(), movie.original_title, Toast.LENGTH_SHORT).show();
                Intent detailActivityIntent = new Intent(getActivity(), DetailActivity.class).putExtra("movie", movie);

                startActivity(detailActivityIntent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_order = sharedPrefs.getString(
                getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popularity));

        if(sort_order.equals("favourite")) {
            ArrayList<String> fav_ids = new ArrayList<String>();
            FavouritesDbHelper fDbHelper = new FavouritesDbHelper(getActivity());
            Cursor retCursor = fDbHelper.getReadableDatabase().query(
                    FavouritesContract.FavouritesEntry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            if(retCursor.moveToFirst()) {
                do {
                    fav_ids.add(retCursor.getString(1));
                    Log.i(LOG_TAG, "favourite movie id= " + retCursor.getString(1));
                } while(retCursor.moveToNext());
            }
            retCursor.close();

            FetchMoviesByIdTask movieByIdTask = new FetchMoviesByIdTask();
            movieByIdTask.execute(fav_ids);
        } else {
            FetchMovieTask movieTask = new FetchMovieTask();
            movieTask.execute(sort_order);
        }
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon().appendQueryParameter(SORT_BY_PARAM, params[0])
                        .appendQueryParameter(API_KEY_PARAM, api_key)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to moviedb api, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the movies data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        private Movie[] getMovieDataFromJson(String forecastJsonStr)
                throws JSONException {


            final String RESULTS = "results";
            JSONObject moviesJson = new JSONObject(forecastJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

            Movie[] movies = new Movie[20];

            for(int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieJson = moviesArray.getJSONObject(i);
                String poster_path = movieJson.getString("poster_path");
                String original_title = movieJson.getString("original_title");
                String release_date = movieJson.getString("release_date");
                String vote_average = movieJson.getString("vote_average");
                String overview = movieJson.getString("overview");

                String id = movieJson.getString("id");
                Movie movie = new Movie(id, poster_path, original_title, release_date, vote_average, overview);

                movies[i] = movie;
            }
            return movies;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null) {
                movieAdapter.clear();
                for (Movie movie : movies) {
                    movieAdapter.add(movie);
                }
            }
        }

    }

    public class FetchMoviesByIdTask extends AsyncTask<ArrayList<String>, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMoviesByIdTask.class.getSimpleName();

        @Override
        protected ArrayList<Movie> doInBackground(ArrayList<String>... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            ArrayList<Movie> movies = new ArrayList<Movie>();
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String API_KEY_PARAM = "api_key";

            for (String id: params[0]) {
                try {
                    Uri builtUri = Uri.parse(MOVIES_BASE_URL + id + "?").buildUpon()
                            .appendQueryParameter(API_KEY_PARAM, api_key)
                            .build();

                    URL url = new URL(builtUri.toString());

                    // Create the request to moviedb api, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    moviesJsonStr = buffer.toString();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    e.printStackTrace();
                    // If the code didn't successfully get the movies data, there's no point in attemping
                    // to parse it.
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }

                try {
                    Movie movie = getMovieDataFromJson(moviesJsonStr);
                    movies.add(movie);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return movies;
        }

        private Movie getMovieDataFromJson(String moviesJsonStr)
                throws JSONException {

            JSONObject movieJson = new JSONObject(moviesJsonStr);

            String poster_path = movieJson.getString("poster_path");
            String original_title = movieJson.getString("original_title");
            String release_date = movieJson.getString("release_date");
            String vote_average = movieJson.getString("vote_average");
            String overview = movieJson.getString("overview");

            String id = movieJson.getString("id");
            Movie movie = new Movie(id, poster_path, original_title, release_date, vote_average, overview);

            return movie;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if (movies != null) {
                movieAdapter.clear();
                for (Movie movie : movies) {
                    movieAdapter.add(movie);
                }
            }
        }
    }
}
