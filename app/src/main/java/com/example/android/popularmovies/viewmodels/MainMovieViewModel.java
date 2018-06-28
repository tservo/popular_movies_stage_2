package com.example.android.popularmovies.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.popularmovies.AppExecutors;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.database.AppDatabase;
import com.example.android.popularmovies.utilities.TmdbConnector;

import java.util.List;

/**
 * Features a lot from this example:
 * https://github.com/googlesamples/android-architecture-components/blob/master/BasicSample
 * /app/src/main/java/com/example/android/persistence/viewmodel/ProductListViewModel.java
 */
public class MainMovieViewModel extends AndroidViewModel {
    private static final String TAG = MainMovieViewModel.class.getSimpleName();


    private final MediatorLiveData<List<Movie>> movies; // movies to be seen
    private final LiveData<List<Movie>> favoriteMovies; // movies stored as favorites
    private final AppDatabase database;

    // these will allow the async nature of the call to the db. ugly.
    private boolean dataReady = false;
    private boolean wantFavoriteData = false;

    public MainMovieViewModel(@NonNull Application application) {
        super(application);

        // here we store the movies we will expose to the activity
        movies = new MediatorLiveData<>();

        database = AppDatabase.getInstance(this.getApplication());
        favoriteMovies = database.movieDao().loadFavoriteMovies();

        // this is so we can get the results of FavoriteMovies.
        movies.addSource(favoriteMovies, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movieList) {
                if (wantFavoriteData) {
                    movies.postValue(movieList);
                }

                dataReady = true;
                // and we just need this source once.
                movies.removeSource(favoriteMovies);
            }
        });


    }

    /**
     * this is an attempt to change the movies live data
     * by
     * @param source the data source requested.
     */
    public void setDataSource(String source) {
        wantFavoriteData = false;

        Application application = getApplication();
        if (source.equals(application.getString(R.string.favorite_value))) {
            if (dataReady) {
                movies.setValue(favoriteMovies.getValue());
            }
            wantFavoriteData = true; // so we can immediately use the data if necessary.
        } else if (source.equals(application.getString(R.string.popular_value))) {
            AppExecutors.getInstance().networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    movies.postValue(TmdbConnector.getPopularMovies());
                }
            });
        } else if (source.equals(application.getString(R.string.top_rated_value))) {
            AppExecutors.getInstance().networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    movies.postValue(TmdbConnector.getTopRatedMovies());
                }
            });
        } else {
            throw new IllegalArgumentException(TAG + ": " + source + " invalid option");
        }
    }

    /**
     *
     * @return the movies list live data
     */
    public MutableLiveData<List<Movie>> loadMovies() {
        return movies;
    }
}
