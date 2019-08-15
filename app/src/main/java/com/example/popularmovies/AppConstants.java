package com.example.popularmovies;

public class AppConstants {
    private static final String API_KEY = "";
    public static final String MOVIES_URL = "https://api.themoviedb.org/3/movie/%s?page=%s&api_key=" + API_KEY;
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";

    static final String SELECTED_MOVIE = "selected_movie";
    public static final String MISSING_OVERVIEW = "This movie does not appear to have an overview yet.";
}
