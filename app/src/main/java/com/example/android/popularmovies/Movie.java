package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

/**
 * Object used to store movie data used in populating {@link MainFragment}.
 *
 * @author Chase Strackbein
 * @version 1.0
 * @since 2016-09-14
 */
public class Movie implements Parcelable {

    private String mMovieId;
    private String mTitle;
    private String mPosterUrl;
    private String mSynopsis;
    private String mReleaseDate;
    private double mUserRating;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMovieId);
        dest.writeString(mTitle);
        dest.writeString(mPosterUrl);
        dest.writeString(mSynopsis);
        dest.writeString(mReleaseDate);
        dest.writeDouble(mUserRating);
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel data) {
            return new Movie(data);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie (String movieId, String title, String posterUrl, String synopsis, String releaseDate,
                  double userRating) {
        mMovieId = movieId;
        mTitle = title;
        mPosterUrl = posterUrl;
        mSynopsis = synopsis;
        mReleaseDate = releaseDate;
        mUserRating = userRating;
    }

    private Movie(Parcel data) {
        mMovieId = data.readString();
        mTitle = data.readString();
        mPosterUrl = data.readString();
        mSynopsis = data.readString();
        mReleaseDate = data.readString();
        mUserRating = data.readDouble();
    }

    public String getMovieId() {
        return mMovieId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPosterUrl() {
        return mPosterUrl;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public double getRating() {
        return mUserRating;
    }

    public String getRatingAsString() {
        return String.format(Locale.ENGLISH ,"%1$,.1f/10", mUserRating);
    }

    public String getYear() {
        return mReleaseDate.substring(0,4);
    }
}
