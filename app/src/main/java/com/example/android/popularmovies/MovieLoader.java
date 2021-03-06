package com.example.android.popularmovies;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Loader class used to perform HTTP requests in the background threads
 *
 * @author Chase Strackbein
 * @version 1.0
 * @since 2016-09-14
 */
public class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    // Global variable for the URL used to send the request
    private String mUrl;
    private Context mContext;

    /**
     * Constructor
     * @param context the activity utilizing the loader
     * @param url a URL to request a {@link Movie} list from
     */
    public MovieLoader (Context context, String url) {
        super(context);
        mUrl = url;
        mContext = context;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        // If the URL is empty, return null
        if (mUrl == null) {
            return new ArrayList<>();
        }
        if (mUrl.contains("popular") || mUrl.contains("top_rated")) {
            // Return a list of Movie objects
            return QueryUtils.fetchMovieData(mUrl);
        }
        return QueryUtils.fetchFavoritesData(Utility.getFavorites(mContext), mUrl);
    }
}
