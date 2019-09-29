package com.example.popularmovies.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.popularmovies.R;
import com.example.popularmovies.model.Review;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {

    private List<Review> reviews = new ArrayList<>();

    private final OnMovieDetailsClickListener onReviewItemClickListener;

    public ReviewsAdapter(OnMovieDetailsClickListener onReviewItemClickListener) {
        this.onReviewItemClickListener = onReviewItemClickListener;
    }

    @NonNull
    @Override
    public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.reviews_list_item, viewGroup, false);
        return new ReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsViewHolder viewHolder, int i) {
        Review review = reviews.get(i);
        viewHolder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    class ReviewsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.btn_expand)
        ImageButton expandButton;
        @BindView(R.id.tv_author)
        TextView authorTV;
        @BindView(R.id.tv_content)
        TextView contentTV;

        ReviewsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Review review) {
            authorTV.setText(review.getAuthor());
            contentTV.setText(review.getContent());

            expandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onReviewItemClickListener.onReviewClick(reviews.get(getAdapterPosition()));
                }
            });
        }

    }
}
