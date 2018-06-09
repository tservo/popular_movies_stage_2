package com.example.android.popularmovies.data;

public class Review {

    private final String id; // review's id (hex format)
    private final int mId; // the movie's id
    private final String author; // review's author
    private final String content; // review's content
    private final String url; // url to main review

    public Review(String id,int mId, String author, String content, String url) {
        this.id = id;
        this.mId = mId;
        this.author = author;
        this.content = content;
        this.url = url;
    }
}