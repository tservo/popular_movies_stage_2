package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class ReviewItemsAdapter extends RecyclerView.Adapter<ReviewItemsAdapter.ListViewHolder> {

    public ReviewItemsAdapter() {

    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {

        public ListViewHolder(View itemView) {
            super(itemView);
        }
    }
}
