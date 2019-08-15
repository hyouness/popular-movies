package com.example.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.PopularMoviesViewHolder> {

    private List<Movie> movies = new ArrayList<>();

    private final OnMovieItemClickListener onMovieItemClickListener;

    PopularMoviesAdapter(OnMovieItemClickListener onMovieItemClickListener) {
        this.onMovieItemClickListener = onMovieItemClickListener;
    }

    @NonNull
    @Override
    public PopularMoviesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.movies_list_item, viewGroup, false);
        return new PopularMoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularMoviesViewHolder viewHolder, int i) {
        Movie movie = movies.get(i);
        viewHolder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    private void addMovie(Movie movie) {
        movies.add(movie);
        notifyItemInserted(movies.size() - 1);
    }

    void addAll(List<Movie> newMovies) {
        for (Movie movie : newMovies) {
            addMovie(movie);
        }
    }

    List<Movie> getMovies() {
        return movies;
    }

    class PopularMoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView movieIV;
        final TextView movieTitleTV;

        PopularMoviesViewHolder(@NonNull View itemView) {
            super(itemView);

            movieIV = itemView.findViewById(R.id.iv_movie);
            movieTitleTV = itemView.findViewById(R.id.tv_movie_title);

            itemView.setOnClickListener(this);
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

        @Override
        public void onClick(View view) {
            onMovieItemClickListener.onMovieClick(movies.get(getAdapterPosition()));
        }
    }
}
