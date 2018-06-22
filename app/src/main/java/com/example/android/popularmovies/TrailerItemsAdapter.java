package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.data.Trailer;
import com.example.android.popularmovies.utilities.DisplayHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * TrailerItemsAdapter
 * The recyclerview that holds and handles trailer thumbnails
 */

public class TrailerItemsAdapter extends RecyclerView.Adapter<TrailerItemsAdapter.ThumbnailViewHolder>{

    private static final String TAG = TrailerItemsAdapter.class.getSimpleName();

    private static final float ASPECT_RATIO = (float) (4.0/3.0); // aspect ratio of the trailer thumbnail
    private static final int NUM_THUMBNAILS = 4; // how many trailer thumbnails to show at once.

    // the storage for the trailers
    private List<Trailer> mTrailerList;

    /**
     * stores whether we are showing two panes at once
     */
    private boolean mTwoPane;

    // store size of image here

    private int mWidth;
    private int mHeight;

    // the click listener
    final private TrailerItemClickListener mOnClickListener;

    /**
     * interface for implementing a click listener
     */
    interface TrailerItemClickListener {
        void onTrailerItemClick(Trailer trailer);
    }

    /**
     * constructor so we can get the click listener in
     *
     */
    TrailerItemsAdapter(TrailerItemClickListener onClickListener, boolean twoPane) {
        mOnClickListener = onClickListener;
        mTwoPane = twoPane;
    }

    /**
     * Creating a new view holder
     * @param parent parent view to attach
     * @param viewType not used here
     * @return view holder
     */
    @NonNull
    @Override
    public ThumbnailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG, "called here");
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = layoutInflater.inflate(R.layout.trailer_thumb_item, parent, false);
        if (DisplayHelper.calculateThumbnailImageSize(context, mTwoPane) == DisplayHelper.THUMBNAIL_LARGE) {
            mWidth = 480;
            mHeight = 360;
        } else {
            mWidth = 240;
            mHeight = 180;
        }

        //RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
        //layoutParams.width = mWidth;
        //layoutParams.height = mHeight;
        //view.setLayoutParams(layoutParams);

        return new ThumbnailViewHolder(view);
    }

    /**
     * Assign new information to the viewholder.
     * @param holder the viewholder
     * @param position the position
     */
    @Override
    public void onBindViewHolder(@NonNull ThumbnailViewHolder holder, int position) {
        Trailer trailer = mTrailerList.get(position);

        Picasso.get().load(trailer.getThumbnail())
                .resize(mWidth,mHeight)
                .centerCrop()
                .into(holder.trailerThumb);

        holder.trailerThumb.setContentDescription(trailer.getName());
    }

    @Override
    public int getItemCount() {
        if (null == mTrailerList) return 0;

        return mTrailerList.size();
    }

    /**
     * pull in new list of trailers received from outside.
     * @param trailerList the list received.
     */
    public void getTrailers(List<Trailer> trailerList) {
        mTrailerList = trailerList;
        notifyDataSetChanged();
    }

    public class ThumbnailViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
        final ImageView trailerThumb;

        ThumbnailViewHolder(View itemView) {
            super(itemView);
            trailerThumb = itemView.findViewById(R.id.trailer_thumb);
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
            Trailer trailer;

            Log.d(TAG,"onClick: "+String.valueOf(clickedPosition));
            if (clickedPosition == RecyclerView.NO_POSITION || clickedPosition >= mTrailerList.size()) {
                trailer = null;
            } else {
                trailer = mTrailerList.get(clickedPosition);
            }

            mOnClickListener.onTrailerItemClick(trailer);
        }
    }
}
