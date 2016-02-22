package com.example.gkulkarni.popularmoviesapp1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by GKULKARNI on 20-12-2015.
 */
public class Movie implements Parcelable {
    String id;
    String image;
    String original_title;
    String release_date;
    String vote_average;
    String overview;

    public Movie(String id, String image, String original_title, String release_date, String vote_average, String overview) {
        this.id = id;
        this.image = image;
        this.original_title = original_title;
        this.release_date = release_date;
        this.vote_average = vote_average;
        this.overview = overview;
    }

    private Movie(Parcel in){
        id = in.readString();
        image = in.readString();
        original_title = in.readString();
        release_date = in.readString();
        vote_average = in.readString();
        overview = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() { return id + "--" + image + "--" + original_title + "--" + release_date + "--" + vote_average + "--" + overview; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(image);
        dest.writeString(original_title);
        dest.writeString(release_date);
        dest.writeString(vote_average);
        dest.writeString(overview);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };
}
