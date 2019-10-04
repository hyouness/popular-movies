package com.example.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.popularmovies.adapter.ReviewsAdapter;
import com.example.popularmovies.model.Review;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsViewHolder extends RecyclerView.ViewHolder {
    private ReviewsAdapter reviewsAdapter;
    @BindView(R.id.btn_expand)
    ImageButton expandButton;
    @BindView(R.id.tv_author)
    TextView authorTV;
    @BindView(R.id.tv_content)
    TextView contentTV;

    public ReviewsViewHolder(ReviewsAdapter reviewsAdapter, @NonNull View itemView) {
        super(itemView);
        this.reviewsAdapter = reviewsAdapter;
        ButterKnife.bind(this, itemView);
    }

    public void bind(Review review) {
        authorTV.setText(review.getAuthor());
        contentTV.setText(review.getContent());

        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewsAdapter.onReviewClick(reviewsAdapter.getReviews().get(getAdapterPosition()));
            }
        });
    }

}
