package com.example.android.popularmovies.data;

import android.net.Uri;

public class Movie {

    private final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p";
    private final String THUMBNAIL_SIZE = "w185";

    private String mTitle;
    private String mPosterPath; // stores the poster's base location, subject to size parameters
    private int mId;

    public Movie(int id, String title, String posterPath) {
        mId = id;
        mTitle = title;
        mPosterPath = (posterPath.charAt(0) == '/') ? posterPath.substring(1) : posterPath; // remove the leading slash if necessary
    }

    /**
     *
     * @return the movie id
     */
    public int getId() {
        return mId;
    }

    /**
     *
     * @return the movie's title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     *
     * @return the URL to get the movie thumbnail
     */
    public String getThumbnail() {
        Uri thumbnailURL = Uri.parse(IMAGE_BASE_URL).buildUpon()
                                .appendPath(THUMBNAIL_SIZE)
                                .appendPath(mPosterPath)
                                .build();
        return thumbnailURL.toString();
    }

}
