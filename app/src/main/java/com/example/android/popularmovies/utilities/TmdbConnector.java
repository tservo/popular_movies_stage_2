package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TmdbConnector {

    private static final String TAG = TmdbConnector.class.getSimpleName();
    private static final String BASE_URL = "http://api.themoviedb.org";
    private static final String VERSION = "3";
    private static final String POPULAR = "movie/popular";
    private static final String TOP_RATED = "movie/top_rated";

    /**
     * Retrieve list of popular movies in JSON, and create list of Movie objects from them.
     * @param context
     * @return list of Movie objects
     */
    public static List<Movie> getPopularMovies(Context context) {

        // this line is temporary.
        Uri uri = createMovieUri(POPULAR);

        String json = context.getString(R.string.popular_movie_json);
        List<Movie> movieList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonMovies = jsonObject.getJSONArray("results");

            Log.d(TAG,String.valueOf(jsonMovies.length()));
            // make the movie objects here
            for (int i = 0; i < jsonMovies.length(); i++) {

                JSONObject jsonMovieObject = jsonMovies.getJSONObject(i);
                int movieId = jsonMovieObject.getInt("id");
                String title = jsonMovieObject.getString("title");
                String posterPath = jsonMovieObject.getString("poster_path");

                movieList.add(new Movie(movieId,title,posterPath));

            }
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
            return null;
        }

        return movieList;
    }

    /**
     *
     * Helper method to create the Uri needed to access the appropriate information.
     * @param apiCall
     * @return the created uri
     */
    private static Uri createMovieUri(String apiCall) {
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(VERSION)
                .appendEncodedPath(apiCall)
                .appendQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                .build();

        Log.d(TAG,uri.toString());

        return uri;
    }

}
