package com.example.android.popularmovies.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.popularmovies.AppExecutors;
import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Trailer;
import com.example.android.popularmovies.database.AppDatabase;

import java.util.List;

public class DetailMovieViewModel extends ViewModel {

    private AppDatabase mDatabase;
    private MediatorLiveData<Movie> mMovie;
    private MutableLiveData<List<Review>> mReviews;
    private MutableLiveData<List<Trailer>> mTrailers;

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

        final LiveData<Movie> movieLiveData = mDatabase.movieDao().loadMovieById(movie.getId());
        mMovie.addSource(movieLiveData, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                if (null != movie) {
                    mMovie.postValue(movie);
                    mMovie.removeSource(movieLiveData); // we have gotten our work done, let go now
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
