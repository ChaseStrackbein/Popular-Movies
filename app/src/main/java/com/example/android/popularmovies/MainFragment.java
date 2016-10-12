package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for handling the GridView used to display the Movie results from TheMovieDP API queries.
 *
 * @author Chase Strackbein
 * @version 1.0
 * @since 2016-09-29
 */

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final int MOVIE_LOADER_ID = 0;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private MovieAdapter mMovieAdapter;
    private GridView mGridView;

    private int mPosition = GridView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Movie movie);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // Initialize the loader when the Activity is created
        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    /*
     * Method to handle when the sort order has changed.
     */
    void onSortOrderChanged() {
        // Restart the loader
        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Retrieve the connection info
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // The MovieAdapter will take data from out List of Movie objects and populate the GridView.
        mMovieAdapter = new MovieAdapter(getActivity(), new ArrayList<Movie>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // If there is currently a network connection, populate the GridView
        if(networkInfo != null && networkInfo.isConnected()) {

            // Show the loading spinner while the information is being received
            ProgressBar loadingBar = (ProgressBar) rootView.findViewById(R.id.loading_spinner);
            loadingBar.setVisibility(View.VISIBLE);

            // Get a reference to the GridView, and attach this adapter to it.
            mGridView = (GridView) rootView.findViewById(R.id.gridview);
            mGridView.setAdapter(mMovieAdapter);

            // Attach an OnItemClickListener to the GridView to alert our MainActivity when
            // a Movie has been selected
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Movie movie = (Movie) parent.getItemAtPosition(position);
                    if (movie != null) {
                        ((Callback) getActivity()).onItemSelected(movie);
                    }
                }
            });

            // If there's an instance state, mine it for the useful information.
            // The end-goal here is that the user never knows that turning their device sideways
            // does crazy lifecycle-related things. It should feel like some stuff stretched out,
            // or magically appeared to take advantage of room, but data or place in the app was never
            // actually lost
            if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
                // The GridView probably hasn't even been populated yet. Actually perform the
                // swapout in onLoadFinished
                mPosition = savedInstanceState.getInt(SELECTED_KEY);
            }
        } else {
            // Else if there is no internet connection, display the no connection text
            TextView noNetwork = (TextView) rootView.findViewById(R.id.no_connection_textview);
            noNetwork.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    /**
     * Updates the GridView
     * @param movies a List of Movies to populate the GridView with
     */
    private void updateUi(final List<Movie> movies) {

        mMovieAdapter = new MovieAdapter(getActivity(), movies);
        mGridView.setAdapter(mMovieAdapter);

        // Set the empty view if there are no Movies to be displayed
        mGridView.setEmptyView(getActivity().findViewById(R.id.no_content_textview));

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to GridView.INVALID_POSTIION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        // Get the preferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Find out which method the user would like to sort by
        String orderBy = sharedPrefs.getString(
                getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_default)
        );

        // String values to append to the request URL
        final String API_KEY = "api_key";
        final String API_VALUE = BuildConfig.TMDB_API_KEY;
        final String MOVIE_DB_REQUEST_URL = "http://api.themoviedb.org/3/movie/";
        final String POPULAR = "popular?";
        final String TOP_RATED = "top_rated?";

        // Initialize a StringBuilder using the base Movie DB API query URL
        StringBuilder baseString = new StringBuilder(MOVIE_DB_REQUEST_URL);

        // If the sort by value is the default (Popular), append corresponding API query
        if (orderBy.equals(getString(R.string.pref_sort_by_default))) {
            baseString.append(POPULAR);
        } else if (orderBy.equals("top rated")) {
            // Else, apply the Top Rated API query
            baseString.append(TOP_RATED);
        } else {
            return new MovieLoader(getContext(), baseString.toString());
        }

        // Create a Uri and append the API key parameter
        Uri baseUri = Uri.parse(baseString.toString());
        Uri.Builder uriBuilder = baseUri.buildUpon().appendQueryParameter(API_KEY, API_VALUE);

        // Return the MovieLoader using the URI API query
        return new MovieLoader(getContext(), uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {

        // Once the loading is completed, hide the loading spinner
        ProgressBar loadingBar = (ProgressBar) getActivity().findViewById(R.id.loading_spinner);
        loadingBar.setVisibility(View.GONE);

        updateUi(data);
        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore to,
            // do so now.
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        // On loader reset, update the UI with an empty list of movies to refresh the list
        updateUi(new ArrayList<Movie>());
    }

}
