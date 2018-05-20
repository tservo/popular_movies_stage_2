package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.android.popularmovies.R;

public class PreferencesHelper {

    /**
     * Set the value of the movie list preference
     * @param context context which handles the shared prefs
     * @param prefValue value to set
     */
    public static void setListingPreference(Context context, String prefValue) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(context.getString(R.string.movie_list_key),prefValue);
        editor.apply();
    }

    /**
     * returns the movie list preference as saved in sharedpreferences
     * @param context the context which handles the shared prefs
     * @return the listing value (or default to popular movies if no value)
     */
    public static String getListingPreference(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(context.getString(R.string.movie_list_key),
                context.getString(R.string.popular_value));
    }
}
