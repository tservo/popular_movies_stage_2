package com.example.android.popularmovies.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Entity(tableName = "movies")
public class Movie implements Parcelable {
    private static final String TAG = Movie.class.getSimpleName();

    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p";
    private static final String THUMBNAIL_SIZE = "w185";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @PrimaryKey(autoGenerate = false)
    private final int id; // movie id

    @ColumnInfo(name = "vote_average")
    private final double mVoteAverage; // average voter score

    @ColumnInfo(name = "title")
    private final String mTitle; // movie title

    @ColumnInfo(name = "popularity")
    private final double mPopularity; // movie popularity

    @ColumnInfo(name = "path_poster")
    private final String mPosterPath; // stores the poster's base location, subject to size parameters

    @ColumnInfo(name = "path_backdrop")
    private final String mBackdropPath; // stores the backdrop's base location, subject to size parameters



    @ColumnInfo(name = "original_lang_code")
    private final String mOriginalLangCode; // the language code for the original movie language

    @ColumnInfo(name = "original_title")
    private final String mOriginalTitle; // movie's original title

    @ColumnInfo(name = "overview")
    private final String mOverview; // movie overview

    @ColumnInfo(name = "release_date")
    private final Date mReleaseDate; // movie release date

    @ColumnInfo(name = "favorite")
    private boolean mFavorite; // is the movie a favorite?

    /**
     * this factory method creates a list of Movies from a JSON Array of Movies
     *
     * @param jsonMovies
     * @return List of Movie objects.
     */
    public static List<Movie> createListFromJSON(JSONArray jsonMovies) {
        // this is how the JSON formats dates

        List<Movie> movieList = new ArrayList<>();
        // make the movie objects here
        for (int i = 0; i < jsonMovies.length(); i++) {
            try {
                // read from the JSON
                Movie movie = new Movie(jsonMovies.getJSONObject(i));
                movieList.add(movie);
            } catch (JSONException | ParseException e) {

                return null;
            }
        }
        Log.d(TAG, jsonMovies.toString());
        return movieList;
    }


    /**
     * the main constructor. change as necessary
     *
     * @param id               - movie id
     * @param voteAverage      - average vote value
     * @param title            - movie title
     * @param popularity       - how popular the movie by float value
     * @param posterPath       - path to movie poster image
     * @param backdropPath     - path to movie backdrop image
     * @param originalLangCode - two letter code for movie's original language
     * @param originalTitle    - the original title of movie
     * @param releaseDate      - date first released
     */
    public Movie(int id,
                 double voteAverage,
                 String title,
                 double popularity,
                 String posterPath,
                 String backdropPath,
                 String originalLangCode,
                 String originalTitle,
                 String overview,
                 Date releaseDate,
                 boolean favorite
    ) {
        this.id = id;
        this.mVoteAverage = voteAverage;
        this.mTitle = title;
        this.mPopularity = popularity;
        this.mPosterPath = removeLeadingSlash(posterPath); // remove the leading slash if necessary
        this.mBackdropPath = removeLeadingSlash(backdropPath); // remove the leading slash if necessary
        this.mOriginalLangCode = originalLangCode;
        this.mOriginalTitle = originalTitle;
        this.mOverview = overview;
        this.mReleaseDate = releaseDate; // should this really be a date?
        this.mFavorite = favorite;
    }

    /*
      Getters for the member variables
     */
    /**
     * @return the movie id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the movie's title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * @return the movie's popularity
     */
    public double getPopularity() {
        return mPopularity;
    }

    /**
     * @return the movie's poster on the server
     */
    public String getPosterPath() {
        return mPosterPath;
    }

    /**
     * @return the backdrop path on the server
     */
    public String getBackdropPath() {
        return mBackdropPath;
    }
    /**
     * @return the release date.
     */
    public Date getReleaseDate() {
        return mReleaseDate;
    }

    /**
     * @return the movie's release year
     */
    public int getReleaseYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mReleaseDate);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * @return the movie's original title
     */
    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    /**
     * @return the movie's voter rating
     */
    public double getVoteAverage() {
        return mVoteAverage;
    }

    /**
     * @return the URL to get the movie thumbnail
     */
    public String getThumbnail() {
        Uri thumbnailURL = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendPath(THUMBNAIL_SIZE)
                .appendPath(mPosterPath)
                .build();
        Log.d(TAG, mPosterPath);
        Log.d(TAG, thumbnailURL.toString());
        return thumbnailURL.toString();
    }

    public String getOverview() {
        return mOverview;
    }

    public String getOriginalLangCode() {
        return mOriginalLangCode;
    }

    public boolean isFavorite() {
        return mFavorite;
    }


    /*
     * Setters
     */

    public void setFavorite(boolean favorite) {
        this.mFavorite = favorite;
    }

    /**
     * parcelable method
     * doesn't seem to do much - has to be overridden.
     *
     * @return 0 -- as good an int as any.
     */
    @Override
    public int describeContents() {
        return 0;
    }



    /**
     * parcelable method
     * write out to parcel.
     *
     * @param dest  the destination parcel
     * @param flags unused
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {


        dest.writeInt(id);
        dest.writeDouble(mVoteAverage);
        dest.writeString(mTitle);
        dest.writeDouble(mPopularity);
        dest.writeString(mPosterPath);
        dest.writeString(mBackdropPath);
        dest.writeString(mOriginalLangCode);
        dest.writeString(mOriginalTitle);
        dest.writeString(mOverview);
        // good idea from https://stackoverflow.com/questions/21017404/reading-and-writing-java-util-date-from-parcelable-class
        // parcel it as a _long_ and
        dest.writeLong(mReleaseDate.getTime());
        dest.writeBooleanArray(new boolean[]{mFavorite}); // can't write just one boolean
    }

    static final Parcelable.Creator<Movie> CREATOR =
            new Parcelable.Creator<Movie>() {

                @Override
                public Movie createFromParcel(Parcel source) {
                    return new Movie(source);
                }

                @Override
                public Movie[] newArray(int size) {
                    return new Movie[size];
                }
            };


    /*
     * private methods and constructors
     */

    /**
     * helper method to remove the leading slash from the path if necessary
     *
     * @param path the path to potentially clean
     * @return path with leading / removed
     */
    private String removeLeadingSlash(String path) {
        return (path.charAt(0) == '/') ? path.substring(1) : path;
    }

    /**
     * constructor to build a movie from a TMDB JSON object
     * not the constructor used by the DB
     */
    @Ignore
    private Movie(JSONObject jsonMovieObject) throws JSONException, ParseException {
        this.id = jsonMovieObject.getInt("id");
        this.mVoteAverage = jsonMovieObject.getDouble("vote_average");
        this.mTitle = jsonMovieObject.getString("title");
        this.mPopularity = jsonMovieObject.getDouble("popularity");
        this.mPosterPath = removeLeadingSlash(jsonMovieObject.getString("poster_path"));
        this.mBackdropPath = removeLeadingSlash(jsonMovieObject.getString("backdrop_path"));
        this.mOriginalLangCode = jsonMovieObject.getString("original_language");
        this.mOriginalTitle = jsonMovieObject.getString("original_title");
        this.mOverview = jsonMovieObject.getString("overview");
        this.mReleaseDate = DATE_FORMAT.parse(jsonMovieObject.getString("release_date"));

        // need logic to get the favorite value
        this.mFavorite = false;
    }

    /**
     * this constructor is necessary for parcelable.
     * Not the constructor used by the database
     * @param source
     */
    @Ignore
    private Movie(Parcel source) {
        this.id = source.readInt();
        this.mVoteAverage = source.readDouble();
        this.mTitle = source.readString();
        this.mPopularity = source.readDouble();
        this.mPosterPath = source.readString();
        this.mBackdropPath = source.readString();
        this.mOriginalLangCode = source.readString();
        this.mOriginalTitle = source.readString();
        this.mOverview = source.readString();
        // using the long is fast and easy way to parcel a Date
        this.mReleaseDate = new Date(source.readLong());

        // use this to get the favorite back
        boolean[] booleanArray = new boolean[1];
        source.readBooleanArray(booleanArray);
        this.mFavorite = booleanArray[0];
    }
}
