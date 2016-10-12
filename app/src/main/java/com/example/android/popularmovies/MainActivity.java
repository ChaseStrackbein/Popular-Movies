package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Main class for the Popular Movies app. Orchestrates populating the MainFragment with information
 * pulled from The Movie DB and if in tablet mode will display the selected movie details, too.
 *
 * @author Chase Strackbein
 * @version 2.1
 * @since 2016-09-14
 */
public class MainActivity extends AppCompatActivity implements
        MainFragment.Callback, DetailFragment.Callback {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;
    private String mSortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the sort order
        mSortBy = Utility.getPreferredSort(this);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be in
            // two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by adding or replacing
            // the detail fragment using a fragment transaction
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
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

    @Override
    protected void onResume() {
        super.onResume();

        // When the app is resumed, reload the information in the MainFragment to take care of sort
        // order change as well as network connectivity changes
        MainFragment mainFragment = (MainFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_main);
        if (mainFragment != null) {
            mainFragment.onSortOrderChanged();
        }

        // Only refresh the movie details if the sort order has changed
        String sortBy = Utility.getPreferredSort(this);
        if (sortBy != null && !sortBy.equals(mSortBy)) {
            DetailFragment detailFragment = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (detailFragment != null) {
                // Clear out the previously selected movie
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.detach(detailFragment);
                fragmentTransaction.attach(detailFragment);
                fragmentTransaction.commit();
            }
            mSortBy = sortBy;
        }
    }

    @Override
    public void onItemSelected(Movie movie) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by adding or replacing
            // the detail fragment using a fragment transaction
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_MOVIE, movie);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            // Otherwise, create a new intent to open the DetailActivity
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailFragment.DETAIL_MOVIE, movie);
            startActivity(intent);
        }
    }

    /*
     * Handles when a Movie has been removed from Favorites in two-pane mode if the Favorites
     * are currently opened
     */
    @Override
    public void onUnfavorite() {
        if (mTwoPane) {
            if (Utility.getPreferredSort(this).equals("favorites")) {
                MainFragment mainFragment = (MainFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_main);
                mainFragment.onSortOrderChanged();
            }
        }
    }
}
