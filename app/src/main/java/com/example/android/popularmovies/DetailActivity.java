package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Class used to retrieve and populate information in the details screen
 *
 * @author Chase Strackbein
 * @version 1.0
 * @since 2016-09-16
 */
public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Retrieve the intent used to call this activity
        Intent intent = this.getIntent();
        // If there is an intent and contains data, extract the Movie info
        if(intent != null && intent.hasExtra("Movie")) {
            mMovie = (Movie) intent.getSerializableExtra("Movie");
            ((TextView) findViewById(R.id.title_text_view)).setText(mMovie.getTitle());
            ((TextView) findViewById(R.id.release_text_view)).setText(mMovie.getYear());
            ((TextView) findViewById(R.id.rating_text_view)).setText(mMovie.getRatingAsString());
            ((TextView) findViewById(R.id.synopsis_text_view)).setText(mMovie.getSynopsis());
            Picasso.with(getApplicationContext()).load(mMovie.getPosterUrl()).
                    into((ImageView) findViewById(R.id.poster_image_view));

        }
    }
}
