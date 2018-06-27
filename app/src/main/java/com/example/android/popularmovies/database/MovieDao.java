package com.example.android.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.sqlite.SQLiteConstraintException;

import com.example.android.popularmovies.data.Movie;

import java.util.List;

@Dao
public abstract class MovieDao {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    public abstract void insertMovie(Movie movie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateMovie(Movie movie);

    @Delete
    public abstract void deleteMovie(Movie movie);

    @Query("SELECT * from movies where id = :id")
    public abstract LiveData<Movie> loadMovieById(int id);

    @Query("SELECT * from movies where favorite = 1")
    public abstract LiveData<List<Movie>> loadFavoriteMovies();

    /**
     * https://stackoverflow.com/questions/45677230/android-room-persistence-library-upsert
     * used so that when marking movies as favorites, it will insert the new movie if
     * it is not there yet. Else it'll update.
     */
    public void upsertMovie(Movie movie) {
        try {
            insertMovie(movie);
        } catch (SQLiteConstraintException e) {
            updateMovie(movie);
        }
    }
}
