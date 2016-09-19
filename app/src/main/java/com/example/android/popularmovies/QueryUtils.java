package com.example.android.popularmovies;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Class filled with useful methods for connecting to a URL, sending a query, retrieving the
 * JSON response, and parsing the response into Movie objects that may be used to populate the
 * {@link MainActivity}.
 *
 * @author Chase Strackbein
 * @version 1.0
 * @since 2016-09-14
 */
public final class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * A private constructor is used because a {@link QueryUtils} object should never be created.
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Movie} objects that have been built up from parsing a JSON response
     * @param moviedbData a JSON response to be parsed
     * @return a list of {@link Movie} objects that have been retrieved from the JSON response
     */
    public static List<Movie> extractMovies(String moviedbData) {

        // Create an empty ArrayList to add movies to
        List<Movie> movies = new ArrayList<>();

        // Try to parse the JSON response. If there's a problem with the way the JSON is formatted,
        // catch the thrown JSONException.
        try {

            // Base url for TheMovieDB poster images
            final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185";

            // Create JSONObject out of JSON response string
            JSONObject rootData = new JSONObject(moviedbData);
            // Extract array of movies from results
            JSONArray results = rootData.getJSONArray("results");

            // For each movie, extract its title, poster URL, synopsis, release date, and
            // user rating, then add a new Movie using those parameters to the ArrayList
            for (int i=0; i<results.length(); i++) {
                JSONObject movie = results.getJSONObject(i);
                String title = movie.getString("original_title");
                String posterUrl = BASE_IMAGE_URL + movie.getString("poster_path");
                String synopsis = movie.getString("overview");
                String releaseDate = movie.getString("release_date");
                double userRating = movie.getDouble("vote_average");

                movies.add(new Movie(title, posterUrl, synopsis, releaseDate, userRating));
            }

        } catch (JSONException e) {
            // Catch any JSONException errors and print them to the log
            Log.e(LOG_TAG, "Problem parsing the movie JSON results", e);
        }

        // Return completed array of movies
        return movies;
    }

    /**
     * Facilitates the creation of the query URL, URL connection, and JSON parsing
     * @param requestUrl a String to be used as the query URL
     * @return a list of {@link Movie} objects that have been parsed from the JSON response
     */
    public static List<Movie> fetchMovieData(String requestUrl) {

        // Create a URL object from the String
        URL url = createUrl(requestUrl);

        // Initialize response to null
        String jsonResponse = null;

        // Attempt to connect to the URL and read the response
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            // Catch and log any IOExceptions
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Return the list of extracted movies
        return extractMovies(jsonResponse);
    }

    /**
     * Creates a URL object from a String
     * @param stringUrl a String to be used as the URL
     * @return a URL object created from a String
     */
    private static URL createUrl(String stringUrl) {

        // Initialize the URL to null
        URL url = null;

        // Attempt to create a URL from the String
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            // Catch and log any bad URLs
            Log.e(LOG_TAG, "Error with creating URL", e);
        }

        // Return the resulting URL object
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response
     * @param url a URL object to connect to
     * @return a String containing the JSON response
     * @throws IOException
     */
    private static String makeHttpRequest(URL url) throws IOException {

        // Initialize the JSON response to nothing
        String jsonResponse = "";

        // If the URL is null, return early
        if (url == null) {
            return jsonResponse;
        }

        // Initialize the connection and input stream to null
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        // Attempt to connect to the given URL
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                // Else, log the response code
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            // Catch and log any problems with connecting to and retrieving the JSON results
            Log.e(LOG_TAG, "Problem retrieving the movie JSON results", e);
        } finally {
            // If a connection was made, disconnect from the URL and close the input stream
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        // Return the JSON response from the URL
        return jsonResponse;

    }

    /**
     * Reads the JSON response from the input stream
     * @param inputStream an InputStream for reading the JSON response
     * @return the JSON response
     * @throws IOException
     */
    private static String readFromStream(InputStream inputStream) throws IOException {

        // Create a new StringBuilder
        StringBuilder output = new StringBuilder();

        // If the input stream is open, read from it
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            // Read a line from the input stream
            String line = reader.readLine();
            // Keep reading the input stream until the end is reached
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        // Return the resulting JSON response
        return output.toString();

    }
}
