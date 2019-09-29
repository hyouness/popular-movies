package com.example.popularmovies.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.popularmovies.R;
import com.example.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private List<Movie> movies = new ArrayList<>();

    private final OnMovieClickListener onMovieItemClickListener;

    public MoviesAdapter(OnMovieClickListener onMovieItemClickListener) {
        this.onMovieItemClickListener = onMovieItemClickListener;
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.movies_list_item, viewGroup, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder viewHolder, int i) {
        Movie movie = movies.get(i);
        viewHolder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_movie)
        ImageView movieIV;
        @BindView(R.id.tv_movie_title)
        TextView movieTitleTV;

        MoviesViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Movie movie) {
            movieTitleTV.setText(movie.getTitle());
            Picasso.get()
                    .load(movie.getPosterUrl())
                    .placeholder(R.drawable.movie_placeholder)
                    .error(R.drawable.movie_placeholder)
                    .fit()
                    .into(movieIV);
        }

        @OnClick
        void onClick(View view) {
            onMovieItemClickListener.onMovieClick(movies.get(getAdapterPosition()));
        }
    }
}
