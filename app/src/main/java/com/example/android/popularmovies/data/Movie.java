package com.example.android.popularmovies.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;

public class Movie implements Parcelable {

    private final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p";
    private final String THUMBNAIL_SIZE = "w185";

    private final int mId; // movie id

    private final double mVoteAverage; // average voter score
    private final String mTitle; // movie title
    private final double mPopularity; // movie popularity
    private final String mPosterPath; // stores the poster's base location, subject to size parameters
    private final String mBackdropPath; // stores the backdrop's base location, subject to size parameters
    private final String mOriginalLangCode; // the language code for the original movie language
    private final String mOriginalTitle; // movie's original title
    private final String mOverview; // movie overview
    private final Date mReleaseDate; // movie release date

    private Movie(Parcel source) {
        mId = source.readInt();
        mVoteAverage = source.readDouble();
        mTitle = source.readString();
        mPopularity = source.readDouble();
        mPosterPath = source.readString();
        mBackdropPath = source.readString();
        mOriginalLangCode = source.readString();
        mOriginalTitle = source.readString();
        mOverview = source.readString();
        // using the long is fast and easy way to parcel a Date
        mReleaseDate = new Date(source.readLong());

    }
    /**
     *
     * the main constructor. change as necessary
     * @param id - movie id
     * @param voteAverage - average vote value
     * @param title - movie title
     * @param popularity - how popular the movie by float value
     * @param posterPath - path to movie poster image
     * @param backdropPath - path to movie backdrop image
     * @param originalLangCode - two letter code for movie's original language
     * @param originalTitle - the original title of movie
     * @param releaseDate - date first released
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
                 Date releaseDate
    ) {
        mId = id;
        mVoteAverage = voteAverage;
        mTitle = title;
        mPopularity = popularity;
        mPosterPath = (posterPath.charAt(0) == '/') ? posterPath.substring(1) : posterPath; // remove the leading slash if necessary
        mBackdropPath = (backdropPath.charAt(0) == '/') ? backdropPath.substring(1) : backdropPath; // remove the leading slash if necessary
        mOriginalLangCode = originalLangCode;
        mOriginalTitle = originalTitle;
        mOverview = overview;
        mReleaseDate = releaseDate; // should this really be a date?
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
     * @return the release date.
     */
    public Date getReleaseDate() {
        return mReleaseDate;
    }

    /**
     *
     * @return the movie's release year
     */
    public int getReleaseYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mReleaseDate);
        return calendar.get(Calendar.YEAR);
    }

    /**
     *
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

    public String getOverview() {
        return mOverview;
    }
    /**
     * parcelable method
     * doesn't seem to do much - has to be overridden.
     * @return 0 -- as good an int as any.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * parcelable method
     * write out to parcel.
     * @param dest the destination parcel
     * @param flags unused
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {


        dest.writeInt(mId);
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
}
