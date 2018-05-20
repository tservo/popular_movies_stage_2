package com.example.android.popularmovies;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Locale;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link DetailActivity}
 * on handsets.
 */
public class DetailFragment extends Fragment {
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
}
