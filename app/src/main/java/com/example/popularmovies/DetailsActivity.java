package com.example.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Movie movie = getIntent().getParcelableExtra(AppConstants.SELECTED_MOVIE);

        setTitle(movie.getTitle());

        populatePosterView(movie.getPosterUrl());

        populateBackdropView(movie.getBackdropImageUrl());

        populateReleaseDateView(movie.getReleaseDate());

        populateOriginalTitleView(movie.getOriginalTitle());

        populateOverviewView(movie.getOverview());

        // Credit: idea behind formatting the rating in % form next to a heart with number of votes
        // was inspired by: https://is1-ssl.mzstatic.com/image/thumb/Purple49/v4/c0/6b/35/c06b358f-33b2-b1d5-530c-c3c36babda85/pr_source.png/696x696bb.png
        populateRatingView(String.valueOf(movie.getRating()));
        populateVoteCountView(movie.getVoteCount());
    }

    private void populateVoteCountView(String voteCount) {
        TextView voteCountTV = findViewById(R.id.vote_count_tv);
        voteCountTV.setText(voteCount);
    }

    private void populateOverviewView(String overview) {
        TextView overviewTV = findViewById(R.id.overview_tv);
        overviewTV.setText(overview);
    }

    private void populateReleaseDateView(String releaseDate) {
        TextView releaseDateTV = findViewById(R.id.release_date_tv);
        releaseDateTV.setText(releaseDate);
    }

    private void populateBackdropView(String backdropUrl) {
        ImageView backdropIV = findViewById(R.id.iv_backdrop_image);
        backdropIV.setAlpha(0.8f);
        Picasso.get()
                .load(backdropUrl)
                .placeholder(R.drawable.movie_placeholder)
                .error(R.drawable.movie_placeholder)
                .into(backdropIV);
    }

    private void populateOriginalTitleView(String originalTitle) {
        TextView originalTitleTV = findViewById(R.id.original_title_tv);
        originalTitleTV.setText(originalTitle);
    }

    private void populateRatingView(String rating) {
        TextView ratingTV = findViewById(R.id.rating_tv);
        ratingTV.setText(rating);
    }

    private void populatePosterView(String posterUrl) {
        ImageView posterIV = findViewById(R.id.iv_movie_poster);
        Picasso.get()
                .load(posterUrl)
                .placeholder(R.drawable.movie_placeholder)
                .error(R.drawable.movie_placeholder)
                .fit()
                .into(posterIV);
    }
}
