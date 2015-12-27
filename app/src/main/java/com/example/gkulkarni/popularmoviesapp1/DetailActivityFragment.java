package com.example.gkulkarni.popularmoviesapp1;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

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
        }

        return rootView;
    }
}
