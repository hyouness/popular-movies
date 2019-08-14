package com.example.popularmovies.utilities;

import android.content.Context;

import com.example.popularmovies.AppConstants;
import com.example.popularmovies.R;
import com.example.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONUtils {

    private static final String W185 = "w185";
    private static final String W342 = "w342";
    private static final String W500 = "w500";
    private static final String W780 = "w780";
    private static final String ORIGINAL = "original";

    public static List<Movie> getMovies(String json, Context context) {
        List<Movie> movies = new ArrayList<>();

        try {
            JSONObject moviesObject = new JSONObject(json);
            JSONArray results = moviesObject.optJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.optJSONObject(i);
                Movie movie = getMovie(context, result);
                movies.add(movie);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movies;
    }

    private static Movie getMovie(Context context, JSONObject result) {
        String title = result.optString("title");
        String originalTitle = result.optString("original_title");
        String posterPath = result.optString("poster_path");
        String backdropPath = result.optString("backdrop_path");
        double rating = result.optDouble("vote_average");
        String overview = result.optString("overview");
        String releaseDate = result.optString("release_date");

        return Movie.newMovie(title, getImageUrl(posterPath, context))
                .originalTitle(originalTitle)
                .overview(overview)
                .backdropImageUrl(getImageUrl(backdropPath, context))
                .rating(rating)
                .releaseDate(DateUtils.formatDate(releaseDate))
                .build();
    }

    private static String getImageUrl(String path, Context context) {
        boolean isTablet = context.getResources().getBoolean(R.bool.isTablet);
        String imageSize = isTablet ? W185 : W780;
        return AppConstants.IMAGE_BASE_URL + imageSize + path;
    }
}
