package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.popularmovies.data.Movie;

public class DetailActivity extends AppCompatActivity {

    private TextView mMovieTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mMovieTitle = findViewById(R.id.tv_movie_title);

        // we need to get the movie passed to us.
        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra("movie");

        mMovieTitle.setText(movie.getTitle());
    }
}
