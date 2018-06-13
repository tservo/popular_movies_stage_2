package com.example.android.popularmovies.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Review {
    private static final String TAG = Review.class.getSimpleName();

    private final String mId; // review's id (hex format)
    private final int mMovieId; // the movie's id
    private final String mAuthor; // review's author
    private final String mContent; // review's content
    private final String mUrl; // url to main review

    public static List<Review> createListFromJson(int movieId, JSONArray jsonReviews) {
        List<Review> reviewList = new ArrayList<>();

        for (int i = 0; i < jsonReviews.length(); i++) {
            try {
                Review review = new Review(movieId, jsonReviews.getJSONObject(i));
                reviewList.add(review);
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
            }
        }
        return reviewList;
    }

    /**
     * constructor
     * @param id
     * @param movieId
     * @param author
     * @param content
     * @param url
     */
    public Review(String id,int movieId, String author, String content, String url) {
        mId = id;
        mMovieId = movieId;
        mAuthor = author;
        mContent = content;
        mUrl = url;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    /**
     * private methods and constructors
     * @param movieId -- the parent movie ID; not provided by json object
     * @param jsonReviewObject -- the review object in json
     * @throws JSONException -- only one method calls this constructor, and it's catching the exception
     */

    private Review(int movieId, JSONObject jsonReviewObject) throws JSONException {
        // read from the JSON
        mMovieId = movieId; // this isn't provided by the json object
        mId = jsonReviewObject.getString("id");
        mAuthor = jsonReviewObject.getString("author");
        mContent = jsonReviewObject.getString("content");
        mUrl = jsonReviewObject.getString("url");
    }
}