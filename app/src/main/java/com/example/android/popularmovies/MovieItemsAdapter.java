package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/*
 A lot of this code was inspired by the Touch Selector App (T12.04) in the course.
 */

public class MovieItemsAdapter extends RecyclerView.Adapter<MovieItemsAdapter.ThumbnailViewHolder> {
    private static final String TAG = MovieItemsAdapter.class.getSimpleName();

    @NonNull
    @Override
    public ThumbnailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int movieThumbId = R.layout.movie_thumb_item;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        boolean attachToRoot = false; // is this what we really want?

        View view = layoutInflater.inflate(movieThumbId, parent, attachToRoot);

        return new ThumbnailViewHolder(view);
    }

    /**
        bind data to one of the views that has already been created.
        @param holder the Viewholder assigned to be updated with contents
        @param position the position within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ThumbnailViewHolder holder, int position) {
        Log.d(TAG,"#" + position);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class ThumbnailViewHolder extends RecyclerView.ViewHolder {

        ImageView mMovieThumb;

        ThumbnailViewHolder(View itemView) {
            super(itemView);

            mMovieThumb = itemView.findViewById(R.id.movie_thumb);
            Picasso.get().load("http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg").into(mMovieThumb);
            

        }
    }
}

