package com.example.android.popularmovies.utilities;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class TmdbConnector {

    private static final String TAG = TmdbConnector.class.getSimpleName();
    private static final String BASE_URL = "http://api.themoviedb.org";
    private static final String VERSION = "3";
    private static final String POPULAR = "movie/popular";
    private static final String TOP_RATED = "movie/top_rated";
    private static final String REVIEWS = "movie/%d/reviews"; // replace %d with actual movie id.
    private static final String TRAILERS = "movie/%d/videos";

    /**
     * Find out if we have a reliable connection to TMDB
     * from https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     * @return can we connect?
     */
    private static boolean isOnline() {
        try {
            int timeoutMs = 1500;
            final int HTTP = 80;
            Socket sock = new Socket();
            InetSocketAddress inetSocketAddress = new InetSocketAddress("api.themoviedb.org",HTTP);

            sock.connect(inetSocketAddress,timeoutMs);
            sock.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Retrieve list of popular movies
     * @return list of Movie objects
     */
    public static List<Movie> getPopularMovies() {

        // attempt to get the movies from the api call to "popular"
        final List<Movie> movieList;

        String json = getNetworkResponse(createMovieURL(POPULAR));
        movieList = getMovieListFromJson(json);

        return movieList;
    }

    /**
     * Retrieve list of top rated movies
     * @return list of Movie objects
     */
    public static List<Movie> getTopRatedMovies() {
        // attempt to get the movies from the api call to "top rated"
        final List<Movie> movieList;

        String json = getNetworkResponse(createMovieURL(TOP_RATED));
        movieList = getMovieListFromJson(json);

        return movieList;
    }

    public static List<Review> getReviews(Movie movie) {
        List<Review> reviewList;

        String json = getNetworkResponse(createMovieURL( String.format(Locale.getDefault(),REVIEWS,movie.getId()) ));
        reviewList = getReviewListFromJson(json);


        return reviewList;
    }

    public static List<Trailer> getTrailers(Movie movie) {
        List<Trailer> trailerList;

        String json = getNetworkResponse(createMovieURL( String.format(Locale.getDefault(),TRAILERS,movie.getId()) ));
        trailerList = getTrailerListFromJson(json);
        return trailerList;
    }

    /**
     * helper method to parse json into movie objects.
     * @param json the source data in json
     * @return list of movie objects. if none, it should be of length 0.  null means network error.
     */
    private static List<Movie> getMovieListFromJson(String json) {
        if (null == json ) return null; // got nothing, so have to return nothing.

        try {

            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonMovies = jsonObject.getJSONArray("results");

            return Movie.createListFromJSON(jsonMovies);

        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
            return null;
        }
    }

    /**
     * Helper method to retrieve list of movie reviews
     * from JSON input.
     * @param json source json
     * @return list of reviews
     */
    private static List<Review> getReviewListFromJson(String json) {
        if (null == json ) return null; // got nothing, so have to return nothing.

        try {

            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonReviews = jsonObject.getJSONArray("results");
            int movieId = jsonObject.getInt("id");

            return Review.createListFromJson(movieId,jsonReviews);

        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
            return null;
        }
    }

    /**
     * Helper method to retrieve list of movie trailers
     * from JSON input.
     * @param json source json
     * @return list of trailers
     */
    private static List<Trailer> getTrailerListFromJson(String json) {
        if (null == json ) return null; // got nothing, so have to return nothing.

        try {

            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonTrailers = jsonObject.getJSONArray("results");
            int movieId = jsonObject.getInt("id");

            return Trailer.createListFromJson(movieId,jsonTrailers);

        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
            return null;
        }
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
     */
    private static String getNetworkResponse(URL url) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            Log.d(TAG,"before calling "+url.toString());

            // attempt to get the stream of data and put it into a string.
            if (!isOnline()) throw new IOException("Network is unreachable");
            InputStream in = urlConnection.getInputStream();
            Log.d(TAG,"calling "+url.toString());

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
        } catch(IOException e) {
            e.printStackTrace();
            Log.w(TAG,e.toString());
            return null;
        } finally {
            if (null != urlConnection) urlConnection.disconnect();
        }
    }
}
