package com.example.android.popularmovies.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.database.AppDatabase;


/**
 * boilerplate factory from T09b.10, Google Android developer website, and many other places.
 */
public class DetailMovieViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    // completed (2) Add two member variables. One for the database and one for the taskId
    private AppDatabase mDb;
    private Movie mMovie;

    // completed (3) Initialize the member variables in the constructor with the parameters received
    public DetailMovieViewModelFactory(AppDatabase database, Movie movie) {
        mDb = database;
        mMovie = movie;
    }
    // completed (4) Uncomment the following method
    // Note: This can be reused with minor modifications
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new DetailMovieViewModel(mDb, mMovie);
    }
}
