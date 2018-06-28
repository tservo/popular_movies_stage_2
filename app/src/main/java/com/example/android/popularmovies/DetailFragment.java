package com.example.android.popularmovies;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Trailer;
import com.example.android.popularmovies.database.AppDatabase;
import com.example.android.popularmovies.utilities.TmdbConnector;
import com.example.android.popularmovies.viewmodels.DetailMovieViewModel;
import com.example.android.popularmovies.viewmodels.DetailMovieViewModelFactory;
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
public class DetailFragment extends Fragment
    implements TrailerItemsAdapter.TrailerItemClickListener {
    /**
     * tag
     */
    private static final String TAG = DetailFragment.class.getSimpleName();

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_MOVIE = "movie";

    /**
     * the fragment argument representing that we're showing both main and detail panes together
     */
    public static final String ARG_TWO_PANE = "twopane";
    /**
     * stores if we're showing the main and the detail screens together
     */
    private boolean mTwoPane;

    /**
     * our database instance
     */
    private AppDatabase mDb;
    private DetailMovieViewModel mViewModel;

    /**
     * The dummy content this fragment is presenting.
     */
    private Movie mMovie;

    /**
     * The trailers to show
     */
    private RecyclerView mTrailersView;
    private TrailerItemsAdapter mTrailersAdapter;

    /**
     * The reviews recycler view and layout adapter
     */
    //private RecyclerView mReviewsView;
    //private ReviewItemsAdapter mReviewsAdapter;

    private LinearLayout mReviewLayout;


    /**
     * the view components for the detail screen
     */
    private TextView  mMovieTitle;
    private TextView  mOriginalTitle;
    private TextView  mReleaseDate;
    private TextView  mMovieDescription;
    private RatingBar mVoterRating;
    private ImageView mMoviePoster;
    private ToggleButton mToggleButton;

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

        mDb = AppDatabase.getInstance(getContext());

        if (getArguments().containsKey(ARG_MOVIE)) {
            // get the single movie to fill the view
            mMovie = getArguments().getParcelable(ARG_MOVIE);
            mTwoPane = getArguments().getBoolean(ARG_TWO_PANE);

        }

        DetailMovieViewModelFactory factory = new DetailMovieViewModelFactory(mDb,mMovie);
        mViewModel = ViewModelProviders.of(this,factory).get(DetailMovieViewModel.class);

        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_REVIEWS,null, mReviewLoader);
        lm.initLoader(LOADER_TRAILERS, null, mTrailerLoader);
    }

    private void populateMovieUI(Movie movie) {

        // and now we populate things.
        //setTitle(movie.getTitle());
        mMovieTitle.setText( String.format(Locale.getDefault(),"%s (%d)",movie.getTitle(),movie.getReleaseYear()) );
        mOriginalTitle.setText(movie.getOriginalTitle());
        mMovieDescription.setText(movie.getOverview());

        // format the date in a form suitable for the default locale
        DateFormat dateFormat = android.text.format.DateFormat.getMediumDateFormat(getContext());
        mReleaseDate.setText(dateFormat.format(movie.getReleaseDate()));

        mVoterRating.setRating((float)(movie.getVoteAverage()/2.0));

        Picasso.get().load(movie.getThumbnail()).into(mMoviePoster);
        mMoviePoster.setContentDescription(movie.getTitle());
        mToggleButton.setChecked(movie.isFavorite());

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        mMovieTitle       = rootView.findViewById(R.id.tv_movie_title);
        mOriginalTitle    = rootView.findViewById(R.id.tv_original_title);
        mReleaseDate      = rootView.findViewById(R.id.tv_release_date);
        mMovieDescription = rootView.findViewById(R.id.tv_movie_description);
        mVoterRating      = rootView.findViewById(R.id.rb_voter_rating);
        mMoviePoster      = rootView.findViewById(R.id.iv_movie_poster);
        mToggleButton     = rootView.findViewById(R.id.btn_favorite);

        mViewModel.getMovie().observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                // changed so update it.
                Log.d(TAG,"observer called");
                populateMovieUI(movie);
            }
        });

        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // toggle favorite and write to db.
                Log.d(TAG,"writing to DB");
                mViewModel.setFavoriteMovie(isChecked);
            }
        });

        return rootView;
    }

    /**
     * set up the recycler views up here.
     * https://gist.github.com/chr33z/2652046522d30ddedd2c
     * @param view when the fragment's view is created
     * @param savedInstanceState not currently used
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize trailer recycler view
        mTrailersView = view.findViewById(R.id.trailer_container);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mTrailersView.getContext(),
                1, GridLayoutManager.HORIZONTAL, false);

        mTrailersView.setLayoutManager(gridLayoutManager);
        mTrailersAdapter = new TrailerItemsAdapter(this, mTwoPane);
        mTrailersView.setAdapter(mTrailersAdapter);

        Log.d(TAG,"trailersview");

        mReviewLayout = view.findViewById(R.id.review_container);
        getLoaderManager().getLoader(LOADER_REVIEWS).forceLoad();

        getLoaderManager().getLoader(LOADER_TRAILERS).forceLoad();




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
    private void addReviews(List<Review> reviewList) {

        // don't bother if it's null
        if (null == reviewList) return;


        // and now the layout inflater
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        // make view from resource.
        for (Review review : reviewList) {
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

    private void watchVideo(Trailer trailer) {
        String appUri = trailer.getAppUri();
        String webUri = trailer.getWebUri();

        // use the app's intent first, then try to access through the web.
        boolean activityProcessed = false;
        if (null != appUri) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(appUri));
                startActivity(intent);
                activityProcessed = true;
            } catch (ActivityNotFoundException e) {
                // nothing to do
            }
        }
        if ( (!activityProcessed) && (null != webUri) ) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUri));
            startActivity(intent);
        }

    }

    /**
     *
     * @param trailer the trailer in question
     */
    @Override
    public void onTrailerItemClick(Trailer trailer) {
        Log.d(TAG,trailer.getName());
        watchVideo(trailer); // I might want to do more here
    }


    /*
        Here are the callbacks and such for the Review AsyncLoader
     */

    /*
        In order to handle the async loading of both reviews and trailers,
        do this:

        https://stackoverflow.com/questions/15643907/multiple-loaders-in-same-activity/20839825#20839825
     */

    /********
     Loader Callbacks!
     */
    /**
     * Loader callback for Review list.
     */
    private final LoaderManager.LoaderCallbacks<List<Review>> mReviewLoader =
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
                    addReviews(data);
                }

                @Override
                public void onLoaderReset(@NonNull Loader<List<Review>> loader) {

                }
            };

    private final LoaderManager.LoaderCallbacks<List<Trailer>> mTrailerLoader =
            new LoaderManager.LoaderCallbacks<List<Trailer>>() {

                @NonNull
                @Override
                public Loader<List<Trailer>> onCreateLoader(int id, @Nullable Bundle args) {
                    if (id == LOADER_TRAILERS) {
                        return new TrailersLoader(getContext(), mMovie);
                    } else {
                        throw new IllegalArgumentException(TAG);
                    }
                }

                @Override
                public void onLoadFinished(@NonNull Loader<List<Trailer>> loader, List<Trailer> trailers) {
                    mTrailersAdapter.getTrailers(trailers);
                }

                @Override
                public void onLoaderReset(@NonNull Loader<List<Trailer>> loader) {

                }
            };



    static class ReviewsLoader extends AsyncTaskLoader<List<Review>>  {
        final Movie mMovie;

        ReviewsLoader(Context context, Movie movie) {
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

    static class TrailersLoader extends AsyncTaskLoader<List<Trailer>>  {
        final Movie mMovie;

        TrailersLoader(Context context, Movie movie) {
            super(context);
            mMovie = movie;
        }

        @Nullable
        @Override
        public List<Trailer> loadInBackground() {
            List<Trailer> trailersList = TmdbConnector.getTrailers(mMovie);
            return trailersList;
        }
    }
}
