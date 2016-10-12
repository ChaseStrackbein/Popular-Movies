package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;

/**
 * A useful class used for SharedPreference-related interactions
 *
 * @author Chase Strackbein
 * @version 1.0
 * @since 2016-09-29
 */

public class Utility {

    // Used for quickly getting the preferred sort method
    public static String getPreferredSort(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(context.getString(R.string.pref_sort_by_key),
                context.getString(R.string.pref_sort_by_default));
    }

    // Used for quickly getting the set of Favorites
    public static Set<String> getFavorites(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return new HashSet<>(preferences.getStringSet(context.getString(R.string.pref_fav_key),
                new HashSet<String>()));
    }

    // Used to quickly save the new set of Favorites
    public static void saveFavorites(Context context, Set<String> favorites) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putStringSet(context.getString(R.string.pref_fav_key), favorites).apply();
    }

    // Used to quickly check if a Movie's ID is currently in the Favorites
    public static boolean isFavorite(Context context, String movieId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getStringSet(context.getString(R.string.pref_fav_key), new HashSet<String>()).contains(movieId)) {
            return true;
        }
        return false;
    }
}
