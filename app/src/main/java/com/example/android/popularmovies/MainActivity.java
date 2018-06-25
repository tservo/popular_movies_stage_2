package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.database.AppDatabase;
import com.example.android.popularmovies.utilities.DisplayHelper;
import com.example.android.popularmovies.utilities.PreferencesHelper;
import com.example.android.popularmovies.utilities.TmdbConnector;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.squareup.picasso.Picasso;

import java.util.List;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity
        implements  AdapterView.OnItemSelectedListener,
                    MovieItemsAdapter.MovieItemClickListener,
                    SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    // for the recycler view
    private RecyclerView mMovieThumbRecyclerView;
    private MovieItemsAdapter mAdapter;

    // the spinner to show which movies are shown
    private Spinner mSpinner;

    private Parcelable mThumbState; // hold the state of the movie thumb list

    private boolean mTwoPane; // hold whether or not we are going to use both list and detail view.

    private AppDatabase mDb; // handle to the database object



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // add the stetho diagnostic tools
        Stetho.initializeWithDefaults(this);
        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();


        setContentView(R.layout.activity_main);


        // get the database handle
        mDb = AppDatabase.getInstance(this);

        // do we have a large enough screen for the two pane layout?
        mTwoPane = (findViewById(R.id.movie_detail_container) != null);

        // get our recyclerview
        mMovieThumbRecyclerView = findViewById(R.id.movie_thumb_recyclerview);

        // make a grid layout manager to store items in a grid
        GridLayoutManager layoutManager = new GridLayoutManager(this, DisplayHelper.calculateNoOfColumns(this, mTwoPane));
        mMovieThumbRecyclerView.setLayoutManager(layoutManager);

        // set up the adapter
        mAdapter = new MovieItemsAdapter(this);
        mMovieThumbRecyclerView.setAdapter(mAdapter);

        // set up the spinner
        mSpinner = findViewById(R.id.movie_list_spinner);
        mSpinner.setOnItemSelectedListener(this);
        initializeSpinner();

        // restore the grid layout if possible
        if (savedInstanceState != null) {
            mThumbState = savedInstanceState.getParcelable("ListState");
        }

        Picasso.get().setLoggingEnabled(true);

        // pref manager setup
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();

        mThumbState = mMovieThumbRecyclerView.getLayoutManager().onSaveInstanceState();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // do our instance saving
        outState.putParcelable("ListState",mMovieThumbRecyclerView.getLayoutManager().onSaveInstanceState());
        // and pass to parent
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);

    }

    // ----------------------------------------
    // these methods allow the spinner to work

    /**
     * helper method to get the position in the spinner of the value
     * @param value : string value to find
     * @return spinner position
     */
    private int getPosition(String value) {
        int count = mSpinner.getAdapter().getCount();
        for(int i = 0; i < count; i++) {
            if (value.equals(mSpinner.getItemAtPosition(i))) {
                return i;
            }
        }
        return 0;
    }

    private void initializeSpinner() {
        // pull in the value from prefs
        String savedListValue = PreferencesHelper.getListingPreference(this);
        mSpinner.setSelection(getPosition(savedListValue));

        // and populate the screen.
        new TMDBQueryTask().execute(savedListValue);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);

        // this will implicitly trigger the query to load
        // in case we use
        PreferencesHelper.setListingPreference(this,item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // this should never happen
        new TMDBQueryTask().execute(getString(R.string.popular_value));
    }

    // -----------------------------------------
    // this method implements the click listener for the view.
    @Override
    public void onMovieItemClick(Movie movie) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.ARG_MOVIE,movie);
            arguments.putBoolean(DetailFragment.ARG_TWO_PANE,true);
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment)
                    .commit();

        } else {
            // go over to the detail page
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            // in here, we have to pass the movie over.
            intent.putExtra(DetailFragment.ARG_MOVIE,movie);
            startActivity(intent);
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.movie_list_key))) {
            String item = sharedPreferences.getString(key,getString(R.string.popular_value));
            Log.d(TAG,"Shared preference: " + item);
            new TMDBQueryTask().execute(item);
        }
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
            } else if (call.equals(getString(R.string.favorite_value))) {
                movieList = mDb.movieDao().loadFavoriteMovies();
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
            mMovieThumbRecyclerView.getLayoutManager().onRestoreInstanceState(mThumbState);
        }
    }

}

