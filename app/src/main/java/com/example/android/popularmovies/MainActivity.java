package com.example.android.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.utilities.TmdbConnector;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private RecyclerView mMovieThumbRecyclerView;
    private MovieItemsAdapter mAdapter;
    private Spinner mSpinner;

    private static final String TAG = MainActivity.class.getSimpleName();
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

        // set up the spinner
        mSpinner = findViewById(R.id.movie_list_spinner);
        mSpinner.setOnItemSelectedListener(this);


        Picasso.get().setLoggingEnabled(true);
    }

    // these methods allow the spinner to work
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        new TMDBQueryTask().execute(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // default to popularity list
        new TMDBQueryTask().execute(getString(R.string.popular_value));
    }

    /**
     *     https://stackoverflow.com/questions/44309241/warning-this-asynctask-class-should-be-static-or-leaks-might-occur
     */
    class TMDBQueryTask extends AsyncTask<String,Void, List<Movie>> {

        private final String TAG = TMDBQueryTask.class.getSimpleName();

        /**
         *
         * @param calls (there will be only one) the request to the network
         * @return the list of movies returned from the network call.
         */
        @Override
        protected List<Movie> doInBackground(String... calls) {
            List<Movie> movieList;

            String call = calls[0];

            if (call.equals(getString(R.string.popular_value))) {
                movieList = TmdbConnector.getPopularMovies();

            } else if (call.equals(getString(R.string.top_rated_value))) {
                movieList = TmdbConnector.getTopRatedMovies();
            } else {
                throw new IllegalArgumentException(TAG + ": " + call + " invalid option");
            }

            return movieList;
        }

        /**
         *  After getting the movie list from the network call, add to the adapter.
            @param movieList - the list returned from doInBackground
         */
        @Override
        protected void onPostExecute(List<Movie> movieList) {
            if (null != movieList) {
                Log.d(TAG,String.valueOf(movieList.size()));

            }
            mAdapter.getMovies(movieList);
        }
    }

}

