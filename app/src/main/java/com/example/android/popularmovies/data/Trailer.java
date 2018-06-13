package com.example.android.popularmovies.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Trailer {
    private static final String TAG = Trailer.class.getSimpleName();

    private final String mId; // trailer id
    private final int mMovieId; // the movie id
    private final String mKey; // video key
    private final String mName; // trailer name
    private final String mSite; // site
    private final int mSize; // video size
    private final String mType; // trailer type

    public static List<Trailer> createListFromJson(int movieId, JSONArray jsonTrailers) {
        List<Trailer> trailerList = new ArrayList<>();

        // read and add all trailers
        for (int i = 0; i < jsonTrailers.length(); i++) {
            try {
                Trailer trailer = new Trailer(movieId, jsonTrailers.getJSONObject(i) );

                trailerList.add(trailer);
            } catch (JSONException e) {
                // issue with the json, return null.
                Log.w(TAG, e.getMessage());
                return null;
            }
        }
        return trailerList;
    }

    /**
     * Constructor
     * @param id trailer id
     * @param movieId movie id
     * @param key video key
     * @param name trailer name
     * @param site video site
     * @param size video size
     * @param type trailer type
     */
    public Trailer(String id,
                   int movieId,
                   String key,
                   String name,
                   String site,
                   int size,
                   String type
    ) {
        mId = id;
        mMovieId = movieId;
        mKey = key;
        mName = name;
        mSite = site;
        mSize = size;
        mType = type;
    }

    /**
     * getter for name var
     * @return name
     */
    public String getName() {
        return mName;
    }

    /**
     * getter for size type
     * @return video size
     */
    public int getSize() {
        return mSize;
    }

    /**
     * private methods and constructors
     */
    private Trailer(int movieId, JSONObject jsonTrailerObject) throws JSONException {
        mId = jsonTrailerObject.getString("id");
        mMovieId = movieId;
        mKey = jsonTrailerObject.getString("key");
        mName = jsonTrailerObject.getString("name");
        mSite = jsonTrailerObject.getString("site");
        mSize = jsonTrailerObject.getInt("size");
        mType = jsonTrailerObject.getString("type");
    }
}
