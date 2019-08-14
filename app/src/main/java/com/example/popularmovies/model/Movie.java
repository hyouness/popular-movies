package com.example.popularmovies.model;

public class Movie {

    private String title;
    private String originalTitle;
    private String posterUrl;
    private String backdropImageUrl;
    private String overview;
    private double rating;
    private String releaseDate;

    private Movie() {

    }

    public static Builder newMovie(String title, String posterUrl) {
        return new Builder(title, posterUrl);
    }

    public String getTitle() {
        return title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getBackdropImageUrl() {
        return backdropImageUrl;
    }

    public String getOverview() {
        return overview;
    }

    public double getRating() {
        return rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public static class Builder {
        private String title;
        private String originalTitle;
        private String posterUrl;
        private String backdropImageUrl;
        private String overview;
        private double rating;
        private String releaseDate;

        Builder (String title, String posterUrl) {
            this.title = title;
            this.posterUrl = posterUrl;
        }

        public Builder originalTitle(String originalTitle) {
            this.originalTitle = originalTitle;
            return this;
        }

        public Builder backdropImageUrl(String backdropImageUrl) {
            this.backdropImageUrl = backdropImageUrl;
            return this;
        }

        public Builder overview(String overview) {
            this.overview = overview;
            return this;
        }

        public Builder rating(double rating) {
            this.rating = rating;
            return this;
        }

        public Builder releaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public Movie build() {
            Movie movie = new Movie();
            movie.title = title;
            movie.originalTitle = originalTitle;
            movie.posterUrl = posterUrl;
            movie.overview = overview;
            movie.backdropImageUrl = backdropImageUrl;
            movie.rating = rating;
            movie.releaseDate = releaseDate;
            return movie;
        }
    }
}
