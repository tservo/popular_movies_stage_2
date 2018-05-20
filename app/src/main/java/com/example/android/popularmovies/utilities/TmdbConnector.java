package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.data.Movie;

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
        List<Movie> movieList;

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
        List<Movie> movieList;

        String json = getNetworkResponse(createMovieURL(TOP_RATED));
        movieList = getMovieListFromJson(json);

        return movieList;
    }

    /**
     * helper method to parse json into movie objects.
     * @param json the source data in json
     * @return list of movie objects. if none, it should be of length 0.  null means network error.
     */
    private static List<Movie> getMovieListFromJson(String json) {
        // this is how the JSON formats dates
        final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        if (null == json ) return null; // got nothing, so have to return nothing.

        List<Movie> movieList = new ArrayList<>();
        try {

            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonMovies = jsonObject.getJSONArray("results");

            Log.d(TAG,String.valueOf(jsonMovies.length()));
            // make the movie objects here
            for (int i = 0; i < jsonMovies.length(); i++) {

                // read from the JSON
                JSONObject jsonMovieObject = jsonMovies.getJSONObject(i);
                int movieId = jsonMovieObject.getInt("id");
                double voteAverage = jsonMovieObject.getDouble("vote_average");
                String title = jsonMovieObject.getString("title");
                double popularity = jsonMovieObject.getDouble("popularity");
                String posterPath = jsonMovieObject.getString("poster_path");
                String backdropPath = jsonMovieObject.getString("backdrop_path");
                String originalLangCode = jsonMovieObject.getString("original_language");
                String originalTitle = jsonMovieObject.getString("original_title");
                String overview = jsonMovieObject.getString("overview");
                Date releaseDate = DATE_FORMAT.parse(jsonMovieObject.getString("release_date"));

                // create and add the Movie object to the list.
                movieList.add( new Movie(   movieId,
                                            voteAverage,
                                            title,
                                            popularity,
                                            posterPath,
                                            backdropPath,
                                            originalLangCode,
                                            originalTitle,
                                            overview,
                                            releaseDate
                ));

            }
        } catch (JSONException | ParseException e) {
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
