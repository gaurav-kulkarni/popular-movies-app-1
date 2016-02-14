package com.example.gkulkarni.popularmoviesapp1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private ReviewsArrayAdapter reviewAdapter;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // The detail Activity called via intent.  Inspect the intent for movies data.
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            Movie movie = (Movie) intent.getSerializableExtra("movie");
            ((TextView) rootView.findViewById(R.id.movieTitle))
                    .setText(movie.original_title);
            ImageView iconView = (ImageView) rootView.findViewById(R.id.moviePoster);
            Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185/"+movie.image).into(iconView);
            ((TextView) rootView.findViewById(R.id.movieSynopsis))
                    .setText(movie.overview);
            ((TextView) rootView.findViewById(R.id.movieRating))
                    .setText(movie.vote_average);
            ((TextView) rootView.findViewById(R.id.movieReleaseDate))
                    .setText(movie.release_date);

            reviewAdapter = new ReviewsArrayAdapter(getActivity(), new ArrayList<String>());
            ListView reviewsListView = (ListView) rootView.findViewById(R.id.reviewsListView);
            reviewsListView.setAdapter(reviewAdapter);

            FetchReviewsTrailersTask frtt = new FetchReviewsTrailersTask();
            frtt.execute(movie.id);

        }
        return rootView;
    }

    private class ReviewsArrayAdapter extends ArrayAdapter<String> {

        public ReviewsArrayAdapter(Context context, List<String> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String review = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_review, parent, false);
            }

            TextView reviewView = (TextView) convertView.findViewById(R.id.list_item_review);
            reviewView.setText(review);
            return convertView;
        }
    }

    public class FetchReviewsTrailersTask extends AsyncTask<String, Void, List<String>[]> {

        private final String LOG_TAG = FetchReviewsTrailersTask.class.getSimpleName();

        @Override
        protected List<String>[] doInBackground(String... params) {
            final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String API_KEY_PARAM = "api_key";
            String api_key = "";
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonDataStr;

            try {
                Uri builtUri = Uri.parse(MOVIES_BASE_URL+params[0]+"/reviews?").buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, api_key)
                        .build();

                URL url = null;
                url = new URL(builtUri.toString());
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
                jsonDataStr = buffer.toString();

                final String RESULTS = "results";
                JSONObject reviewsJson = new JSONObject(jsonDataStr);
                JSONArray reviewsArray = reviewsJson.getJSONArray(RESULTS);
                List<String> reviews = new ArrayList<String>();

                for(int i = 0; i < reviewsArray.length(); i++) {
                    JSONObject reviewJson = reviewsArray.getJSONObject(i);
                    String review = reviewJson.getString("content");
                    reviews.add(review);
                }
                return  new List[] {reviews};
            } catch (Exception e) {
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
        }

        @Override
        protected void onPostExecute(List<String>[] reviews) {
            if (reviews[0] != null) {
                for (String review : reviews[0]) {
                    reviewAdapter.add(review);

                }
            }
        }

    }

    private List<String> getTrailersFromId(String id) throws JSONException {
        final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/";
        final String API_KEY_PARAM = "api_key";
        String api_key = "";
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonDataStr;

        try {
            Uri builtUri = Uri.parse(MOVIES_BASE_URL+id+"/videos?").buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, api_key)
                    .build();

            URL url = null;
            url = new URL(builtUri.toString());
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
            jsonDataStr = buffer.toString();

            final String RESULTS = "results";
            JSONObject trailersJson = new JSONObject(jsonDataStr);
            JSONArray trailersArray = trailersJson.getJSONArray(RESULTS);
            List<String> trailers = new ArrayList<String>();

            for(int i = 0; i < trailersArray.length(); i++) {
                JSONObject reviewJson = trailersArray.getJSONObject(i);
                String trailer = "https://www.youtube.com/watch?v="+reviewJson.getString("key");
                trailers.add(trailer);
            }
            return  trailers;
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
    }
}
