package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * This class is a helper class for display functions
 */
public class DisplayHelper {
    /**
     * As suggested by my mentor
     * Programmatically determines the best number of columns to create
     * for the Grid Layout. This helps to optimize for landscape mode.
     * @param context Android context
     * @return int number of columns to use
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 185;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if(noOfColumns < 2)
            noOfColumns = 2;
        return noOfColumns;
    }
}
