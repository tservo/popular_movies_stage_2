package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

/**
 * This class is a helper class for display functions
 */
public class DisplayHelper {

    // flags for showing large or small thumbnails
    public static final int THUMBNAIL_LARGE = 1;
    public static final int THUMBNAIL_SMALL = 0;

    // these are for showing large size images
    private static final float THRESHOLD_WIDTH = (float) 720.0;
    private static final float THRESHOLD_HEIGHT = (float) 480.0;

    /**
     * utility class to send two floats as a return
     */
    private static class Point {
        float x;
        float y;

        Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private static Point calculateDp(Context context, Boolean twoPane) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        if (twoPane) dpWidth = dpWidth * (2/5);

        return new Point(dpWidth,dpHeight);
    }

    /**
     * As suggested by my mentor
     * Programmatically determines the best number of columns to create
     * for the Grid Layout. This helps to optimize for landscape mode.
     * @param context Android context
     * @param twoPane compensate for the size of the two-pane layout
     * @return int number of columns to use
     */
    public static int calculateNoOfColumns(Context context, Boolean twoPane) {

        float dpWidth = calculateDp(context, twoPane).x;

        int scalingFactor = 185; // the width of the poster

        int noOfColumns = (int) (dpWidth / scalingFactor);
        if(noOfColumns < 2)
            noOfColumns = 2;


        return noOfColumns;
    }

    /**
     * Utility function to calculate the thumbnail image size depending on the view size
     * @param context Android context
     * @param twoPane do we have a two-pane display?
     * @return value corresponding to the thumbnail size
     */
    public static int calculateThumbnailImageSize(Context context, Boolean twoPane) {
        Point dimensions = calculateDp(context, twoPane);
        if (dimensions.x > THRESHOLD_WIDTH && dimensions.y > THRESHOLD_HEIGHT) {
            return THUMBNAIL_LARGE;
        } else {
            return THUMBNAIL_SMALL;
        }
    }
}
