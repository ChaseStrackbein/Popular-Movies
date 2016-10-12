package com.example.android.popularmovies;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Loader class used to perform HTTP requests in the background threads for Movie trailers and reviews.
 *
 * @author Chase Strackbein
 * @version 1.0
 * @since 2016-09-29
 */
public class MovieExtrasLoader extends AsyncTaskLoader<List<String>> {

    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String TRAILER_URL = "/videos";
    private static final String REVIEWS_URL = "/reviews";
    private static final String API_URL = "?api_key=";
    private static final String LANGUAGE_URL = "&language=en-US";

    private String mMovieId;

    /**
     * Constructor
     * @param context the activity utilizing the loader
     * @param movieId the ID of the Movie to retrieve data for
     */
    public MovieExtrasLoader(Context context, String movieId) {
        super(context);
        mMovieId = movieId;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<String> loadInBackground() {
        if (mMovieId == null) {
            return null;
        }

        List<String> movieExtras = new ArrayList<>();

        // Fetch and store the videos in the first (0th) index of the array
        String videosUrl = BASE_URL + mMovieId + TRAILER_URL + API_URL + BuildConfig.TMDB_API_KEY
                + LANGUAGE_URL;
        movieExtras.add(QueryUtils.fetchMovieExtras(videosUrl));

        // Fetch and store the reviews in the second (1st) index of the array
        String reviewsUrl = BASE_URL + mMovieId + REVIEWS_URL + API_URL + BuildConfig.TMDB_API_KEY
                + LANGUAGE_URL;
        movieExtras.add(QueryUtils.fetchMovieExtras(reviewsUrl));

        return movieExtras;
    }
}
