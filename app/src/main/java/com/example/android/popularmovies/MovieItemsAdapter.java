package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/*
 A lot of this code was inspired by the Touch Selector App (T12.04) in the course.
 */

public class MovieItemsAdapter extends RecyclerView.Adapter<MovieItemsAdapter.ThumbnailViewHolder>
{
    private static final String TAG = MovieItemsAdapter.class.getSimpleName();

    final private MovieItemClickListener mOnClickListener;
    private List<Movie> mMovieList;

    public interface MovieItemClickListener {
        void onMovieItemClick(Movie movie);
    }

    /**
     * constructor for the adapter
     * receive a click listener so that items can respond to clicks
     * @param movieItemClickListener the click listener
     */
    MovieItemsAdapter(MovieItemClickListener movieItemClickListener) {
        mOnClickListener = movieItemClickListener;
    }

    /**
     * Called on creating a view holder in the recycleview
     * @param parent the parent viewgroup to create viewholders
     * @param viewType not used
     * @return new ViewHolder
     */
    @NonNull
    @Override
    public ThumbnailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int movieThumbId = R.layout.movie_thumb_item;

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(movieThumbId, parent, false);

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


        Picasso.get().load(mMovieList.get(position).getThumbnail()).into(holder.movieThumb);
    }

    @Override
    public int getItemCount() {
        if (null == mMovieList) return 0;
        return mMovieList.size();
    }

    /**
        for now, get the list of movies into the view containers
        this may change in the future
        @param movieList list of Movie objects
     */
    public void getMovies(List<Movie> movieList) {
        mMovieList = movieList;
        notifyDataSetChanged();
    }


    class ThumbnailViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

        final ImageView movieThumb;

        ThumbnailViewHolder(View itemView) {
            super(itemView);
            movieThumb = itemView.findViewById(R.id.movie_thumb);
            itemView.setOnClickListener(this);
        }

        /**
         * handle click on the viewholder
         * by determining which item has been clicked.
         * send out the movie that was selected.
         * @param v unused.
         */
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            Movie movie;

            Log.d(TAG,"onClick: "+String.valueOf(clickedPosition));
            if (clickedPosition == RecyclerView.NO_POSITION || clickedPosition >= mMovieList.size()) {
                movie = null;
            } else {
                movie = mMovieList.get(clickedPosition);
            }

            mOnClickListener.onMovieItemClick(movie);
        }
    }
}

