package com.example.popularmovies;

import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.popularmovies.adapter.OnMovieDetailsClickListener;
import com.example.popularmovies.adapter.ReviewsAdapter;
import com.example.popularmovies.database.AppDatabase;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.ResponseList;
import com.example.popularmovies.model.Review;
import com.example.popularmovies.model.Video;
import com.example.popularmovies.utilities.AppExecutors;
import com.example.popularmovies.utilities.RetrofitUtils;
import com.example.popularmovies.viewmodel.DetailsViewModel;
import com.example.popularmovies.viewmodel.DetailsViewModelFactory;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements OnMovieDetailsClickListener {

    public static final String REVIEW_WRITTEN_BY_X = "Review written by: %s";

    @BindView(R.id.nested_scroll_view)
    NestedScrollView scrollView;
    @BindView(R.id.pb_rv)
    ProgressBar progressBar;
    @BindView(R.id.tv_error_message)
    TextView errorMessageTV;
    @BindView(R.id.rv_reviews)
    RecyclerView reviewsRV;

    @BindView(R.id.favorite_movie)
    ImageButton imageButton;
    @BindView(R.id.vote_count_tv)
    TextView voteCountTV;
    @BindView(R.id.overview_tv)
    TextView overviewTV;
    @BindView(R.id.release_date_tv)
    TextView releaseDateTV;
    @BindView(R.id.original_title_tv)
    TextView originalTitleTV;
    @BindView(R.id.rating_tv)
    TextView ratingTV;

    ReviewsAdapter reviewsAdapter;
    private boolean isLoading = false;
    private DetailsViewModel viewModel;
    private AlertDialog.Builder builder;
    private Snackbar snackBar;
    @BindView(R.id.movie_details_layout)
    LinearLayout movieDetailsLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        scrollView.setNestedScrollingEnabled(false);

        Movie movie = getIntent().getParcelableExtra(AppConstants.SELECTED_MOVIE);
        bindMovie(movie);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        reviewsRV = findViewById(R.id.rv_reviews);
        reviewsAdapter = new ReviewsAdapter(this);
        reviewsRV.setLayoutManager(layoutManager);
        reviewsRV.setAdapter(reviewsAdapter);

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (canLoadReviews(v, scrollY, oldScrollY)) {
                    if (RetrofitUtils.isOnline(getApplicationContext())) {
                        showProgressBar();
                        viewModel.getReviews(viewModel.getCurrentPage() + 1);
                    } else {
                        checkInternetConnection();
                    }
                }
            }

            private boolean canLoadReviews(NestedScrollView v, int scrollY, int oldScrollY) {
                View lastChild = v.getChildAt(v.getChildCount() - 1);
                boolean isLastReviewVisible = lastChild != null && (scrollY >= (lastChild.getMeasuredHeight() - v.getMeasuredHeight())) &&
                        scrollY > oldScrollY;
                return !isLoading && viewModel.isNotOnLastPage() && isLastReviewVisible;
            }

        });

        DetailsViewModelFactory viewModelFactory = new DetailsViewModelFactory(getApplicationContext(), movie.getId());
        viewModel = viewModelFactory.create(DetailsViewModel.class);

        if (RetrofitUtils.isOnline(getApplicationContext())) {
            showProgressBar();
            viewModel.getReviews(viewModel.getCurrentPage()).observe(this, new Observer<ResponseList<Review>>() {
                @Override
                public void onChanged(@Nullable ResponseList<Review> reviewResponseList) {
                    hideProgressBar();
                    if (reviewResponseList != null && !reviewResponseList.getItems().isEmpty()) {
                        updateRecyclerView(reviewResponseList.getItems());
                    } else if (viewModel.getCurrentPage() == 1 && RetrofitUtils.isOnline(getApplicationContext())) {
                        showErrorMessage(R.string.no_reviews);
                    } else {
                        checkInternetConnection();
                    }
                }
            });
        } else if (viewModel.getReviews().getValue() == null || viewModel.getReviews().getValue().getItems().isEmpty()){
            showErrorMessage(R.string.error_message);
        }

    }

    void updateRecyclerView(List<Review> reviews) {
        hideErrorMessage();
        reviewsAdapter.setReviews(reviews);
    }

    private void bindMovie(Movie movie) {
        setTitle(movie.getTitle());
        voteCountTV.setText(movie.getVoteCount());
        overviewTV.setText(movie.getOverview());
        releaseDateTV.setText(movie.getReleaseDate());
        originalTitleTV.setText(movie.getOriginalTitle());
        ratingTV.setText(String.valueOf(movie.getRating()));
        initFavoriteButton(movie);
        populatePosterView(movie.getPosterUrl());
        populateBackdropView(movie.getBackdropImageUrl());
    }

    private void initFavoriteButton(final Movie movie) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                boolean isFavorite = AppDatabase.getInstance(getApplicationContext()).movieDao().isFavoriteMovie(movie.getId());
                int drawableId = isFavorite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off;
                imageButton.setImageResource(drawableId);
                imageButton.setTag(drawableId);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int drawableId = (int) imageButton.getTag();
                        if (drawableId == android.R.drawable.btn_star_big_off) {
                            drawableId = android.R.drawable.btn_star_big_on;
                            movie.setBookmarkDate(new Date());
                            AppDatabase.getInstance(getApplicationContext()).movieDao().insertMovie(movie);
                        } else {
                            drawableId = android.R.drawable.btn_star_big_off;
                            AppDatabase.getInstance(getApplicationContext()).movieDao().deleteMovie(movie);
                        }
                        final int finalDrawableId = drawableId;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageButton.setImageResource(finalDrawableId);
                                imageButton.setTag(finalDrawableId);
                            }
                        });
                    }
                });
            }
        });
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

    private void populatePosterView(String posterUrl) {
        ImageView posterIV = findViewById(R.id.iv_movie_poster);
        Picasso.get()
                .load(posterUrl)
                .placeholder(R.drawable.movie_placeholder)
                .error(R.drawable.movie_placeholder)
                .fit()
                .into(posterIV);
    }

    void showProgressBar() {
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        isLoading = false;
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void checkInternetConnection() {
        if (snackBar == null) {
            snackBar = Snackbar.make(movieDetailsLayout, "Check your internet connection and Retry.", Snackbar.LENGTH_LONG);
        }
        snackBar.show();
    }

    private void showErrorMessage(int message) {
        isLoading = false;
        reviewsRV.setVisibility(View.INVISIBLE);
        errorMessageTV.setText(message);
        errorMessageTV.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessage() {
        errorMessageTV.setVisibility(View.INVISIBLE);
        reviewsRV.setVisibility(View.VISIBLE);
    }

    @Override
    public void onVideoClick(Video video) {

    }

    @Override
    public void onReviewClick(Review review) {
        if (builder == null) {
            builder = new AlertDialog.Builder(this)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
        }
        builder.setTitle(String.format(REVIEW_WRITTEN_BY_X, review.getAuthor()))
                .setMessage(review.getContent())
                .show();
    }
}
