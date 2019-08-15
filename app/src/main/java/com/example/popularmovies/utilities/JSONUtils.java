package com.example.popularmovies.utilities;

import android.content.Context;

import com.example.popularmovies.AppConstants;
import com.example.popularmovies.R;
import com.example.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class JSONUtils {

    private static final String W185 = "w185";
    private static final String W500 = "w500";
    private static final String W780 = "w780";

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
        long id = result.optLong("id");
        String title = result.optString("title");
        String originalTitle = result.optString("original_title");
        String posterPath = result.optString("poster_path");
        String backdropPath = result.optString("backdrop_path");
        String rating = String.format("%s%%", result.optDouble("vote_average") * 10);
        String overview = result.optString("overview");
        String releaseDate = result.optString("release_date");
        String voteCount = getVoteCount(result.optLong("vote_count"));

        return Movie.newMovie(id, title, getImageUrl(posterPath, false, context))
                .originalTitle(originalTitle)
                .overview(overview.isEmpty() ? AppConstants.MISSING_OVERVIEW : overview)
                .backdropImageUrl(getImageUrl(backdropPath, true, context))
                .rating(rating)
                .releaseDate(DateUtils.formatDate(releaseDate))
                .voteCount(voteCount)
                .build();
    }

    private static String getImageUrl(String path, Boolean isBackDrop, Context context) {
        boolean isTablet = context.getResources().getBoolean(R.bool.isTablet);
        String imageSize = !isTablet ? isBackDrop ? W500 : W185 : W780;
        return AppConstants.IMAGE_BASE_URL + imageSize + path;
    }

    // method implementation based on: https://stackoverflow.com/a/52773332/5826864
    private static String getVoteCount(double votes) {
        final String[] units = {"", "k", "m", "b"};
        int i = 0;
        while ((votes / 1000) >= 1) {
            votes = votes / 1000;
            i++;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setRoundingMode(RoundingMode.FLOOR);
        return String.format("%s%s votes", decimalFormat.format(votes), units[i]);
    }
}
