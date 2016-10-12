package com.example.android.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Adapter used to populate the GridView in {@link MainActivity} with the movie posters
 *
 * @author Chase Strackbein
 * @version 1.0
 * @since 2016-09-16
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    // Constructor
    public MovieAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;

        // If the current list item is empty, create a new grid_item_layout to fill
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.grid_item_layout, parent, false);
        }

        // Retrieve the current Movie's poster image URL
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image);
        Movie currentMovie = getItem(position);
        // Use Picasso to load the image into the ImageView
        Picasso.with(getContext()).load(currentMovie.getPosterUrl()).resize(100, 150).centerCrop().into(imageView);

        return listItemView;
    }


}