package com.example.android.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    ImageView mMovieThumb;
    RecyclerView mMovieThumbRecyclerView;
    MovieItemsAdapter mAdapter;

    private static final int GRID_ROWS = 2;
    private static final int GRID_COLS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // get our recyclerview
        mMovieThumbRecyclerView = findViewById(R.id.movie_thumb_recyclerview);

        // make a grid layout manager to store items in a grid
        GridLayoutManager layoutManager = new GridLayoutManager(this, GRID_COLS);
        mMovieThumbRecyclerView.setLayoutManager(layoutManager);

        // set up the adapter
        mAdapter = new MovieItemsAdapter();
        mMovieThumbRecyclerView.setAdapter(mAdapter);


        Picasso.get().setLoggingEnabled(true);

    }
}

