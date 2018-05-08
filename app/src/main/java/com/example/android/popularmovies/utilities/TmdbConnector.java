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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TmdbConnector {

    private static final String TAG = TmdbConnector.class.getSimpleName();
    private static final String BASE_URL = "http://api.themoviedb.org";
    private static final String VERSION = "3";
    private static final String POPULAR = "movie/popular";
    private static final String TOP_RATED = "movie/top_rated";

    /**
     * Retrieve list of popular movies
     * @return list of Movie objects
     */
    public static List<Movie> getPopularMovies() {

        // attempt to get the movies from the api call to "popular"
        List<Movie> movieList = null;
        try {
            String json = getNetworkResponse(createMovieURL(POPULAR));
            movieList = getMovieListFromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return movieList;
    }

    /**
     * Retrieve list of top rated movies
     * @return list of Movie objects
     */
    public static List<Movie> getTopRatedMovies() {
        // attempt to get the movies from the api call to "top rated"
        List<Movie> movieList = null;
        try {
            String json = getNetworkResponse(createMovieURL(TOP_RATED));
            movieList = getMovieListFromJson(json);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return movieList;
    }

    /**
     * helper method to parse json into movie objects.
     * @param json the source data in json
     * @return list of movie objects
     */
    private static List<Movie> getMovieListFromJson(String json) {
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
     * @param apiCall the api call to build into a url
     * @return the created url
     */
    private static URL createMovieURL(String apiCall) {
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(VERSION)
                .appendEncodedPath(apiCall)
                .appendQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                .build();

        Log.d(TAG,uri.toString());

        URL url=null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Get the response from the server as a string
     * this code is from T02.04 of the ud851-Exercises
     * @param url the URL to read
     * @return the response as a string
     * @throws IOException couldn't read it
     */
    private static String getNetworkResponse(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            // attempt to get the stream of data and put it into a string.
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                String json = scanner.next();
                Log.d(TAG,json);
                return json;
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
