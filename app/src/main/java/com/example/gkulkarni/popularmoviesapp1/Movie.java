package com.example.gkulkarni.popularmoviesapp1;

import java.io.Serializable;

/**
 * Created by GKULKARNI on 20-12-2015.
 */
public class Movie implements Serializable {
    String image;
    String original_title;
    String release_date;
    String vote_average;
    String overview;

    public Movie(String image, String original_title, String release_date, String vote_average, String overview) {
        this.image = image;
        this.original_title = original_title;
        this.release_date = release_date;
        this.vote_average = vote_average;
        this.overview = overview;
    }
}
