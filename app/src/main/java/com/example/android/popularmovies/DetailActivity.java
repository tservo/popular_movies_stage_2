package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private TextView mMovieTitle;
    private TextView mOriginalTitle;
    private TextView mMovieDescription;
    private RatingBar mVoterRating;
    private ImageView mMoviePoster;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mMovieTitle = findViewById(R.id.tv_movie_title);
        mOriginalTitle = findViewById(R.id.tv_original_title);
        mMovieDescription = findViewById(R.id.tv_movie_description);
        mVoterRating = findViewById(R.id.rb_voter_rating);
        mMoviePoster = findViewById(R.id.iv_movie_poster);

        // we need to get the movie passed to us.
        Intent intent = getIntent();
        Movie movie = intent.getParcelableExtra("movie");

        // and now we populate things.
        setTitle(movie.getTitle());
        mMovieTitle.setText( String.format(Locale.getDefault(),"%s (%d)",movie.getTitle(),movie.getReleaseYear()) );
        mOriginalTitle.setText(movie.getOriginalTitle());
        mMovieDescription.setText(movie.getOverview());

        mVoterRating.setRating((float)(movie.getVoteAverage()/2.0));

        Picasso.get().load(movie.getThumbnail()).into(mMoviePoster);
    }
}
