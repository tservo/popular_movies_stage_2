package com.example.android.popularmovies.viewmodels;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.example.android.popularmovies.AppExecutors;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.database.AppDatabase;

import java.util.List;

public class DetailMovieViewModel extends ViewModel {

    private AppDatabase mDatabase;
    private MediatorLiveData<Movie> mMovie;

    private LiveData<Movie> mMovieLiveData; // store the call from the db here

    // I could put the Reviews and Trailers here, perhaps with LiveData, but it's
    // a bit complicated.

    /**
     *
     * @param database handle to our app's database
     * @param movie the movie passed to us from the main activity
     */
    public DetailMovieViewModel(AppDatabase database, final Movie movie) {
        mDatabase = database;
        mMovie = new MediatorLiveData<>();

        // stay with original movie, but replace with the database call if possible.
        mMovie.setValue(movie);

        /*
            here we attempt to find the movie in the database.
            the db call is asynchronous, so try to update the value
            by adding a source to the db's livedata query.
            if the database gives up information, then we can

         */

        mMovieLiveData = mDatabase.movieDao().loadMovieById(movie.getId());
        mMovie.addSource(mMovieLiveData, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                if (null != movie) {
                    mMovie.postValue(movie);
                    mMovie.removeSource(mMovieLiveData); // we have gotten our work done, let go now
                }
            }
        });
    }

    /**
     * toggle the value in the movie
     * @param isFavorite boolean is the movie a favorite?
     */
    public void setFavoriteMovie(boolean isFavorite) {
        Movie movie = mMovie.getValue();

        // short circuit if there is no change to be made.
        if (movie.isFavorite() == isFavorite) return;

        movie.setFavorite(isFavorite);
        mMovie.setValue(movie);
        writeMovie(movie);
    }

    /**
     * writes the movie to the database -- useful for storing new movies there
     * and to toggle the favorites flag.
     * @param movie
     */
    public void writeMovie(final Movie movie) {
        // forward this to the database
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.movieDao().upsertMovie(movie);
            }
        });
    }

    /**
     * get the liveData movie. for observing
     * @return the movie object
     */
    public LiveData<Movie> getMovie() {
        return mMovie;
    }

}
