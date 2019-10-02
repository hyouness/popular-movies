package com.example.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.example.popularmovies.adapter.VideosAdapter;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements OnMovieDetailsClickListener {

    public static final String REVIEW_WRITTEN_BY_X = "Review written by: %s";

    @BindView(R.id.nested_scroll_view)
    NestedScrollView scrollView;

    @BindView(R.id.movie_details_layout)
    LinearLayout movieDetailsLayout;
    @BindView(R.id.favorite_movie)
    ImageButton favoriteButton;
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


    @BindView(R.id.rv_videos)
    RecyclerView videosRV;
    @BindView(R.id.rv_reviews)
    RecyclerView reviewsRV;

    @BindView(R.id.tv_error_message)
    TextView errorMessageTV;
    @BindView(R.id.tv_error_message_videos)
    TextView videosErrorMessageTV;

    @BindView(R.id.pb_videos)
    ProgressBar videosPB;
    @BindView(R.id.pb_reviews)
    ProgressBar reviewsPB;

    VideosAdapter videosAdapter;
    ReviewsAdapter reviewsAdapter;

    private boolean isLoading = false;
    private DetailsViewModel viewModel;
    private AlertDialog.Builder builder;
    private Snackbar snackBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        scrollView.setNestedScrollingEnabled(false);

        Movie movie = getIntent().getParcelableExtra(AppConstants.SELECTED_MOVIE);
        bindMovie(movie);

        final LinearLayoutManager videosLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        videosAdapter = new VideosAdapter(this);
        videosRV.setLayoutManager(videosLayoutManager);
        videosRV.setAdapter(videosAdapter);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        reviewsAdapter = new ReviewsAdapter(this);
        reviewsRV.setLayoutManager(layoutManager);
        reviewsRV.setAdapter(reviewsAdapter);

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (canLoadReviews(v, scrollY, oldScrollY)) {
                    if (RetrofitUtils.isOnline(getApplicationContext())) {
                        isLoading = true;
                        showProgressBar(reviewsPB);
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
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailsViewModel.class);

        loadVideos();
        loadReviews();
    }

    private void loadVideos() {
        if (RetrofitUtils.isOnline(getApplicationContext()) || viewModel.getVideos().getValue() != null) {
            showProgressBar(videosPB);
            viewModel.getVideos().observe(this, new Observer<ResponseList<Video>>() {
                @Override
                public void onChanged(@Nullable ResponseList<Video> videoResponseList) {
                    hideProgressBar(videosPB);
                    if (videoResponseList != null && !videoResponseList.getItems().isEmpty()) {
                        hideErrorMessage(videosErrorMessageTV, videosRV);
                        videosAdapter.setVideos(videoResponseList.getItems());
                    } else if (RetrofitUtils.isOnline(getApplicationContext())) {
                        showErrorMessage(videosErrorMessageTV, R.string.no_videos, videosRV);
                    } else {
                        checkInternetConnection();
                    }
                }
            });
            if (viewModel.getVideos().getValue() != null) {
                viewModel.getVideos().setValue(viewModel.getVideos().getValue());
            }
        } else {
            showErrorMessage(videosErrorMessageTV, R.string.error_message, videosRV);
        }
    }

    private void loadReviews() {
        if (RetrofitUtils.isOnline(getApplicationContext()) || viewModel.getReviews().getValue() != null) {
            isLoading = true;
            showProgressBar(reviewsPB);
            viewModel.getReviews(viewModel.getCurrentPage()).observe(this, new Observer<ResponseList<Review>>() {
                @Override
                public void onChanged(@Nullable ResponseList<Review> reviewResponseList) {
                    isLoading = false;
                    hideProgressBar(reviewsPB);
                    if (reviewResponseList != null && !reviewResponseList.getItems().isEmpty()) {
                        hideErrorMessage(errorMessageTV, reviewsRV);
                        reviewsAdapter.setReviews(reviewResponseList.getItems());
                    } else if (viewModel.getCurrentPage() == 1 && RetrofitUtils.isOnline(getApplicationContext())) {
                        showErrorMessage(errorMessageTV, R.string.no_reviews, reviewsRV);
                    } else {
                        checkInternetConnection();
                    }
                }
            });
            if (viewModel.getReviews().getValue() != null) {
                viewModel.getReviews().setValue(viewModel.getReviews().getValue());
            }
        } else if (viewModel.getReviews().getValue() == null || viewModel.getReviews().getValue().getItems().isEmpty()){
            showErrorMessage(errorMessageTV, R.string.error_message, reviewsRV);
        }
    }

    private void bindMovie(Movie movie) {
        setTitle(movie.getTitle());
        voteCountTV.setText(movie.getVoteCount());
        overviewTV.setText(movie.getOverview());
        releaseDateTV.setText(movie.getReleaseDate());
        originalTitleTV.setText(movie.getOriginalTitle());
        ratingTV.setText(String.valueOf(movie.getRating()));
        initFavoriteButton(movie);
        populateImageView(movie.getPosterUrl(), R.id.iv_movie_poster);
        populateImageView(movie.getBackdropImageUrl(), R.id.iv_backdrop_image);
    }

    private void initFavoriteButton(final Movie movie) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                boolean isFavorite = AppDatabase.getInstance(getApplicationContext()).movieDao().isFavoriteMovie(movie.getId());
                int drawableId = isFavorite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off;
                favoriteButton.setImageResource(drawableId);
                favoriteButton.setTag(drawableId);
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int drawableId = (int) favoriteButton.getTag();
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
                                favoriteButton.setImageResource(finalDrawableId);
                                favoriteButton.setTag(finalDrawableId);
                            }
                        });
                    }
                });
            }
        });
    }

    private void populateImageView(String imageUrl, int id) {
        ImageView imageView = findViewById(id);
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.movie_placeholder)
                .error(R.drawable.movie_placeholder)
                .into(imageView);
    }

    void showProgressBar(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(ProgressBar progressBar) {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void checkInternetConnection() {
        if (snackBar == null) {
            snackBar = Snackbar.make(movieDetailsLayout, "Check your internet connection and Retry.", Snackbar.LENGTH_LONG);
        }
        snackBar.show();
    }

    private void showErrorMessage(TextView textView, int message, RecyclerView recyclerView) {
        recyclerView.setVisibility(View.INVISIBLE);
        textView.setText(message);
        textView.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessage(TextView textView, RecyclerView recyclerView) {
        textView.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onVideoClick(Video video) {
        try {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video.getKey()));
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getVideoUrl()));
            startActivity(webIntent);
        }
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
