package com.example.android.popularmovies;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Class used to populate the settings menu
 *
 * @author Chase Strackbein
 * @version 1.0
 * @since 2016-09-16
 */
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the preferences
        addPreferencesFromResource(R.xml.pref_general);

        // Attach an OnPreferenceChange listener to update the preference summary when
        // the value is changed
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_by_key)));
    }

    /**
     * Attach an OnPreferenceChange listener to update the summary text
     * of the preference when changed
     * @param preference the Preference object to attach the listener to
     */
    private void bindPreferenceSummaryToValue (Preference preference) {

        preference.setOnPreferenceChangeListener(this);

        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        // Retrieve the string value of the new preference value
        String stringValue = newValue.toString();

        // If the preference changed is of a ListPreference type, set its corresponding
        // summary to the corresponding entry value
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // Else update the summary with the new value
            preference.setSummary(stringValue);
        }

        return true;
    }
}
