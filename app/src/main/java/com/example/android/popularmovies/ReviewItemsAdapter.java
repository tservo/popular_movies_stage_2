package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.data.Review;

import java.util.List;

public class ReviewItemsAdapter extends RecyclerView.Adapter<ReviewItemsAdapter.ListViewHolder> {

    private static final String TAG = ReviewItemsAdapter.class.toString();

    private List<Review> mReviewList;


    public ReviewItemsAdapter() {

    }

    @NonNull
    @Override

    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // first get the context for later use
        Context context = parent.getContext();

        // and the resource id of the review item layout
        int reviewItemId = R.layout.review_list_item;

        // and now the layout inflater
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        // make view from resource.
        View view = layoutInflater.inflate(reviewItemId, parent, false);
        return new ListViewHolder(view);
    }

    /**
     *   bind data to one of the views that has already been created.
     *   @param holder the Viewholder assigned to be updated with contents
     *   @param position the position within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Log.d(TAG,"#" + position);

        Review review = mReviewList.get(position);
        holder.mAuthorView.setText(review.getAuthor());
        holder.mContentView.setText(review.getContent());

    }

    @Override
    public int getItemCount() {
        if (null == mReviewList) return 0;
        return mReviewList.size();
    }

    /**
     * pull in reviews to the views
     */
    public void getReviews(List<Review> reviewList) {
        mReviewList = reviewList;
        notifyDataSetChanged();
    }

    /**
     * The View Holder for Reviews List
     */
    public static class ListViewHolder extends RecyclerView.ViewHolder {

        final TextView mAuthorView;
        final TextView mContentView;

        public ListViewHolder(View itemView) {
            super(itemView);

            mAuthorView = itemView.findViewById(R.id.tv_review_author);
            mContentView = itemView.findViewById(R.id.tv_review_content);
        }
    }
}
