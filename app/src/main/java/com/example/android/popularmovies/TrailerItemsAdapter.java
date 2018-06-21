package com.example.android.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * @class TrailerItemsAdapter
 * The recyclerview that holds and handles trailer thumbnails
 */

public class TrailerItemsAdapter extends RecyclerView.Adapter<TrailerItemsAdapter.ThumbViewAdpater>{

    @NonNull
    @Override
    public ThumbViewAdpater onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbViewAdpater holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ThumbViewAdpater extends RecyclerView.ViewHolder {
        public ThumbViewAdpater(View itemView) {
            super(itemView);
        }
    }
}
