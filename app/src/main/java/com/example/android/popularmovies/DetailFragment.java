package com.example.android.popularmovies;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.utilities.TmdbConnector;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link DetailActivity}
 * on handsets.
 */
public class DetailFragment extends Fragment {
    /**
     * tag
     */
    private static final String TAG = DetailFragment.class.toString();

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_MOVIE = "movie";

    /**
     * The dummy content this fragment is presenting.
     */
    private Movie mMovie;


    /**
     * The reviews list to show
     */
    private List<Review> mReviewList;
    /**
     * The reviews recycler view and layout adapter
     */
    //private RecyclerView mReviewsView;
    //private ReviewItemsAdapter mReviewsAdapter;

    private LinearLayout mReviewLayout;

    /**
     * Loader id's
     */
    private static final int LOADER_REVIEWS = 594;
    private static final int LOADER_TRAILERS = 706;



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_MOVIE)) {
            // get the single movie to fill the view
            mMovie = getArguments().getParcelable(ARG_MOVIE);

        }
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_REVIEWS,null, mReviewLoader);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        // Populate the view if we have a movie
        if (mMovie != null) {
            TextView movieTitle = rootView.findViewById(R.id.tv_movie_title);
            TextView originalTitle = rootView.findViewById(R.id.tv_original_title);
            TextView releaseDate = rootView.findViewById(R.id.tv_release_date);
            TextView movieDescription = rootView.findViewById(R.id.tv_movie_description);
            RatingBar voterRating = rootView.findViewById(R.id.rb_voter_rating);
            ImageView moviePoster = rootView.findViewById(R.id.iv_movie_poster);



            // and now we populate things.
            //setTitle(movie.getTitle());
            movieTitle.setText( String.format(Locale.getDefault(),"%s (%d)",mMovie.getTitle(),mMovie.getReleaseYear()) );
            originalTitle.setText(mMovie.getOriginalTitle());
            movieDescription.setText(mMovie.getOverview());

            // format the date in a form suitable for the default locale
            DateFormat dateFormat = android.text.format.DateFormat.getMediumDateFormat(getContext());
            releaseDate.setText(dateFormat.format(mMovie.getReleaseDate()));

            voterRating.setRating((float)(mMovie.getVoteAverage()/2.0));

            Picasso.get().load(mMovie.getThumbnail()).into(moviePoster);
        }

        return rootView;
    }

    /**
     * set up the recycler views up here.
     * https://gist.github.com/chr33z/2652046522d30ddedd2c
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mReviewLayout = view.findViewById(R.id.review_container);
        getLoaderManager().getLoader(LOADER_REVIEWS).forceLoad();

//        // initialize view, layoutmanager, and adapter
//        mReviewsView = view.findViewById(R.id.review_recyclerview);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(mReviewsView.getContext());
//        mReviewsView.setLayoutManager(layoutManager);
//
//
//
//        mReviewsAdapter = new ReviewItemsAdapter();
//        mReviewsView.setAdapter(mReviewsAdapter);
//
//        // no scrolling, yes fixed size
//        mReviewsView.setNestedScrollingEnabled(false);
//        //mReviewsView.setHasFixedSize(true);
//
//        // add the item divider
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
//                mReviewsView.getContext(), layoutManager.getOrientation()
//        );
//        mReviewsView.addItemDecoration(dividerItemDecoration);
//
//        // pull in data to adapter
//


    }

    /**
     * helper method to add list of review view items
     * into a linear layout.
     *
     */
    private void addReviews() {

        // and now the layout inflater
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        // make view from resource.
        for (Review review : mReviewList) {
            View view = layoutInflater.inflate(R.layout.review_list_item,  mReviewLayout, false);

            View separator = layoutInflater.inflate(R.layout.separator, mReviewLayout, false);

            TextView author = view.findViewById(R.id.tv_review_author);
            author.setText(review.getAuthor());

            TextView content = view.findViewById(R.id.tv_review_content);
            content.setText(review.getContent());

            mReviewLayout.addView(view);
            mReviewLayout.addView(separator);
        }

    }


    /*
        Here are the callbacks and such for the Review AsyncLoader
     */

    /*
        In order to handle the async loading of both reviews and trailers,
        do this:

        https://stackoverflow.com/questions/15643907/multiple-loaders-in-same-activity/20839825#20839825
     */

    /*********
     * Loader Callbacks!
     */
    /**
     * Loader callback for Review list.
     */
    private LoaderManager.LoaderCallbacks<List<Review>> mReviewLoader =
            new LoaderManager.LoaderCallbacks<List<Review>>() {
                @NonNull
                @Override
                public Loader<List<Review>> onCreateLoader(int id, @Nullable Bundle args) {
                    if (id == LOADER_REVIEWS) {
                        return new ReviewsLoader(getContext(), mMovie);
                    } else {
                        throw new IllegalArgumentException(TAG);
                    }
                }

                @Override
                public void onLoadFinished(@NonNull Loader<List<Review>> loader, List<Review> data) {
                    mReviewList = data;
                    addReviews();
                }

                @Override
                public void onLoaderReset(@NonNull Loader<List<Review>> loader) {

                }
            };

    public static class ReviewsLoader extends AsyncTaskLoader<List<Review>>  {
        Movie mMovie;

        public ReviewsLoader(Context context, Movie movie) {
            super(context);
            mMovie = movie;
        }

        @Nullable
        @Override
        public List<Review> loadInBackground() {
            List<Review> reviewsList = TmdbConnector.getReviews(mMovie);
            return reviewsList;
        }
    }
}
