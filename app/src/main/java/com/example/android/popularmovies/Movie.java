package com.example.android.popularmovies;

import java.io.Serializable;

/**
 * Object used to store movie data used in populating {@link MainActivity}.
 *
 * @author Chase Strackbein
 * @version 1.0
 * @since 2016-09-14
 */
public class Movie implements Serializable {

    private String mTitle;
    private String mPosterUrl;
    private String mSynopsis;
    private String mReleaseDate;
    private double mUserRating;

    public Movie (String title, String posterUrl, String synopsis, String releaseDate,
                  double userRating) {
        mTitle = title;
        mPosterUrl = posterUrl;
        mSynopsis = synopsis;
        mReleaseDate = releaseDate;
        mUserRating = userRating;
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

    //
    public String getRatingAsString() {
        return Double.toString(mUserRating) + "/10";
    }

    public String getYear() {
        return mReleaseDate.substring(0,4);
    }
}
