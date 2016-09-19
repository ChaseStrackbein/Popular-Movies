package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Main class for the Popular Movies app. Orchestrates populating the GridView with information
 * pulled from The Movie DB and allows pressing on a Movie to view its details
 *
 * @author Chase Strackbein
 * @version 1.0
 * @since 2016-09-14
 */
public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final int MOVIE_LOADER_ID = 1;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    private MovieAdapter mMovieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve the current connection information
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is currently a network connection, populate the GridView
        if(networkInfo != null && networkInfo.isConnected()) {

            // Show the loading spinner while the information is being received
            ProgressBar loadingBar = (ProgressBar) findViewById(R.id.loading_spinner);
            loadingBar.setVisibility(View.VISIBLE);

            // Initialize the loader used to retrieve the movie information
            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);

            // Get the preferences
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

            // Create an OnPreferenceChange listener to reset the loader
            prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    // If the Sort By key has been changed, reset the loader
                    if (key.equals(getString(R.string.pref_sort_by_key))) {
                        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, MainActivity.this);
                    }
                }
            };
            // Attach the OnPreferenceChange listener
            sharedPrefs.registerOnSharedPreferenceChangeListener(prefListener);

            GridView gridview = (GridView) findViewById(R.id.gridview);
            // If the gird is not empty, attach onItemClickListeners to send an intent to
            // open the DetailActivity
            if (gridview != null) {
                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        Movie movie = mMovieAdapter.getItem(position);
                        Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
                        detailIntent.putExtra("Movie", movie);
                        startActivity(detailIntent);
                    }
                });
            }

        } else {
            // Else if there is no internet connection, display the no connection text
            TextView noNetwork = (TextView) findViewById(R.id.no_connection_textview);
            noNetwork.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Updates the GridView
     * @param movies a List of Movies to populate the GridView with
     */
    private void updateUi(final List<Movie> movies) {

        GridView gridview = (GridView) findViewById(R.id.gridview);
        mMovieAdapter = new MovieAdapter(this, movies);

        // If the GridView exists, set the adapter
        if (gridview != null) {
            gridview.setAdapter(mMovieAdapter);
        } else {
            // Else, set the empty view text
            gridview.setEmptyView(findViewById(R.id.no_content_textview));
        }
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {

        // Get the preferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Find out which method the user would like to sort by
        String orderBy = sharedPrefs.getString(
                getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_default)
        );

        // String values to append to the request URL
        final String API_KEY = "api_key";
        final String API_VALUE = ""; // Insert TheMovieDB API key here
        final String MOVIE_DB_REQUEST_URL = "http://api.themoviedb.org/3/movie/";
        final String POPULAR = "popular?";
        final String TOP_RATED = "top_rated?";

        // Initialize a StringBuilder using the base Movie DB API query URL
        StringBuilder baseString = new StringBuilder(MOVIE_DB_REQUEST_URL);

        // If the sort by value is the default (Popular), append corresponding API query
        if (orderBy.equals(getString(R.string.pref_sort_by_default))) {
            baseString.append(POPULAR);
        } else {
            // Else, apply the Top Rated API query
            baseString.append(TOP_RATED);
        }

        // Create a Uri and append the API key parameter
        Uri baseUri = Uri.parse(baseString.toString());
        Uri.Builder uriBuilder = baseUri.buildUpon().appendQueryParameter(API_KEY, API_VALUE);

        // Return the MovieLoader using the URI API query
        return new MovieLoader(this, uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {

        if (data == null) {
            return;
        }

        // Once the loading is completed, hide the loading spinner
        ProgressBar loadingBar = (ProgressBar) findViewById(R.id.loading_spinner);
        loadingBar.setVisibility(View.GONE);

        // and update the UI with the newly retrieved data
        updateUi(data);

    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        // On loader reset, update the UI with an empty list of movies to refresh the list
        updateUi(new ArrayList<Movie>());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Create the options menu
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // If the Settings option is selected, open the SettingsActivity
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
