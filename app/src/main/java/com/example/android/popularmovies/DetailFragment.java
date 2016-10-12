package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;

/**
 * Fragment used to receive and display details about the selected movie
 *
 * @author Chase Strackbein
 * @version 1.0
 * @since 2016-09-29
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<String>> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_MOVIE = "movie";

    private Movie mMovie;

    private static final int DETAIL_LOADER = 1;

    private ImageView mPosterView;
    private TextView mTitleView;
    private TextView mReleaseDateView;
    private TextView mRatingView;
    private TextView mSynopsisView;
    private boolean mMovieIsFavorite;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when a movie has been removed from Favorites.
         */
        void onUnfavorite();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Retrieve the Movie which will be displayed
        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovie = arguments.getParcelable(DETAIL_MOVIE);
        }

        // Find views
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mPosterView = (ImageView) rootView.findViewById(R.id.poster_image_view);
        mTitleView = (TextView) rootView.findViewById(R.id.title_text_view);
        mReleaseDateView = (TextView) rootView.findViewById(R.id.release_text_view);
        mRatingView = (TextView) rootView.findViewById(R.id.rating_text_view);
        mSynopsisView = (TextView) rootView.findViewById(R.id.synopsis_text_view);
        TextView emptyView = (TextView) rootView.findViewById(R.id.movie_detail_no_content_text_view);

        // If the Movie is not null, populate the fragment
        if (mMovie != null) {
            emptyView.setVisibility(View.GONE);
            Picasso.with(getContext()).load(mMovie.getPosterUrl()).into(mPosterView);
            mTitleView.setText(mMovie.getTitle());
            mReleaseDateView.setText(mMovie.getYear());
            mRatingView.setText(mMovie.getRatingAsString());
            mSynopsisView.setText(mMovie.getSynopsis());

            final Button fav = (Button) rootView.findViewById(R.id.favorite_button);
            mMovieIsFavorite = Utility.isFavorite(getContext(), mMovie.getMovieId());
            // Depending on if the movie is in Favorites, set button text accordingly
            if (mMovieIsFavorite) {
                fav.setText(getString(R.string.unfavorite));
            } else {
                fav.setText(getString(R.string.favorite));
            }
            // Attach OnClickListener to Favorites button to handle adding/removing from Favorites
            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Set<String> favorites = Utility.getFavorites(getContext());
                    if (mMovieIsFavorite) {
                        favorites.remove(mMovie.getMovieId());
                        mMovieIsFavorite = false;
                        Toast.makeText(getContext(), mMovie.getTitle() + " has been removed from your favorites", Toast.LENGTH_SHORT).show();
                        fav.setText(getString(R.string.favorite));
                        if (getActivity() instanceof MainActivity) {
                            ((Callback) getActivity()).onUnfavorite();
                        }
                    } else {
                        favorites.add(mMovie.getMovieId());
                        mMovieIsFavorite = true;
                        Toast.makeText(getContext(), mMovie.getTitle() + " has been added to your favorites", Toast.LENGTH_SHORT).show();
                        fav.setText(getString(R.string.unfavorite));
                    }
                    Utility.saveFavorites(getContext(), favorites);
                }
            });
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // Initialize the loader when the activity is created
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<List<String>> onCreateLoader(int id, Bundle args) {
        // If the Movie is not null, create a MovieExtrasLoader to retrieve the movie's videos and
        // reviews from TheMovieDB API
        if (mMovie != null) {
            // Now create and return a MovieExtraLoader that will take care of creating a List<String>
            // for the data being displayed.
            return new MovieExtrasLoader(getContext(), mMovie.getMovieId());
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
        // If the data received is not null, parse it and populate the trailer and review views
        if (data != null) {

            // Try to parse the JSON response. If there's a problem with the way the JSON is formatted,
            // catch the thrown JSONException.
            try {
                // Create JSONObject out of JSON response string
                JSONObject rootData = new JSONObject(data.get(0));
                // Extract array of trailers from result
                JSONArray results = rootData.getJSONArray("results");

                // Prep the ViewGroup in which the trailers will be inserted
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup videoRoot = (ViewGroup) getActivity().findViewById(R.id.trailers_linear_layout);

                // If there are no trailers, insert the placeholder view to alert the user
                if (results.length() == 0) {
                    View view = inflater.inflate(R.layout.trailer_no_items_layout, null);
                    videoRoot.addView(view, 1, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
                // For each trailer, extract its title and URL and populate a view with this information
                for (int i=0; i<results.length(); i++) {

                    final JSONObject video = results.getJSONObject(i);

                    View view = inflater.inflate(R.layout.trailer_item_layout, null);
                    ((TextView) view.findViewById(R.id.trailer_name_textview)).setText(video.getString("name"));

                    videoRoot.addView(view, i+1, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                    final String youtubeKey = video.getString("key");

                    // Attach an OnClickListener to create the intent to open the video
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://www.youtube.com/watch?v=" + youtubeKey));
                            startActivity(intent);
                        }
                    });

                }

            } catch (JSONException e) {
                // Catch any JSONException errors and print them to the log
                Log.e(LOG_TAG, "Problem parsing the video JSON results", e);
            }

            // Try to parse the JSON response. If there's a problem with the way the JSON is formatted,
            // catch the thrown JSONException.
            try {
                // Create JSONObject out of JSON response string
                JSONObject rootData = new JSONObject(data.get(1));
                // Extract array of reviews from results
                JSONArray results = rootData.getJSONArray("results");

                // Prep the ViewGroup in which the reviews will be inserted
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewGroup reviewRoot = (ViewGroup) getActivity().findViewById(R.id.reviews_linear_layout);

                // If there are no reviews, insert the placeholder view to alert the user
                if (results.length() == 0) {
                    View view = inflater.inflate(R.layout.reviews_no_items_layout, null);
                    reviewRoot.addView(view, 1, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }

                // For each review, extract its author and content and populate a view with this information
                for (int i=0; i<results.length(); i++) {

                    final JSONObject video = results.getJSONObject(i);

                    View view = inflater.inflate(R.layout.review_item_layout, null);

                    ((TextView) view.findViewById(R.id.author_textview)).setText(video.getString("author"));
                    ((TextView) view.findViewById(R.id.content_textview)).setText(video.getString("content"));

                    reviewRoot.addView(view, i+1, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                }

            } catch (JSONException e) {
                // Catch any JSONException errors and print them to the log
                Log.e(LOG_TAG, "Problem parsing the review JSON results", e);
            }

            // Destroy the loader to prevent it from reloading the information unnecessarily
            getLoaderManager().destroyLoader(DETAIL_LOADER);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<String>> loader) {

    }
}
