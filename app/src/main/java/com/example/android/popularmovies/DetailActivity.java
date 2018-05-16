package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private TextView mMovieTitle;
    private TextView mMovieDescription;
    private ImageView mMoviePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mMovieTitle = findViewById(R.id.tv_movie_title);
        mMovieDescription = findViewById(R.id.tv_movie_description);
        mMoviePoster = findViewById(R.id.iv_movie_poster);

        // we need to get the movie passed to us.
        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra("movie");

        // and now we populate things.
        setTitle(movie.getTitle());
        mMovieTitle.setText(movie.getTitle());
        mMovieDescription.setText(movie.getOverview());

        Picasso.get().load(movie.getThumbnail()).into(mMoviePoster);
    }
}
