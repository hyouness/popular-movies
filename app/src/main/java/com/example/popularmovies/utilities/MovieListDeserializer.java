package com.example.popularmovies.utilities;

import com.example.popularmovies.AppConstants;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.ResponseList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.popularmovies.utilities.JsonUtils.optDouble;
import static com.example.popularmovies.utilities.JsonUtils.optLong;
import static com.example.popularmovies.utilities.JsonUtils.optString;

public class MovieListDeserializer implements JsonDeserializer<ResponseList<Movie>> {

    private static final String W185 = "w185";
    private static final String W500 = "w500";
    private static final String W780 = "w780";

    private static Boolean isTablet;

    MovieListDeserializer(Boolean isTablet) {
        MovieListDeserializer.isTablet = isTablet;
    }

    @Override
    public ResponseList<Movie> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<Movie> movies = new ArrayList<>();

        final JsonObject moviesObject = json.getAsJsonObject();

        Integer page = moviesObject.get("page").getAsInt();
        Integer totalPages = moviesObject.get("total_pages").getAsInt();
        Integer totalResults = moviesObject.get("total_results").getAsInt();

        JsonArray results = moviesObject.getAsJsonArray("results");
        for (int i = 0; i < results.size(); i++) {
            JsonObject result = results.get(i).getAsJsonObject();
            Movie movie = getMovie(result);
            movies.add(movie);
        }

        return new ResponseList<>(page, totalResults, totalPages, movies);
    }

    private static Movie getMovie(JsonObject result) {
        long id = optLong(result.get("id"));
        String title = optString(result.get("title"));
        String originalTitle = optString(result.get("original_title"));
        String posterPath = optString(result.get("poster_path"));
        String backdropPath = optString(result.get("backdrop_path"));
        String rating = String.format("%s%%", optDouble(result.get("vote_average")) * 10);
        String overview = optString(result.get("overview"));
        String releaseDate = optString(result.get("release_date"));
        String voteCount = getVoteCount(optLong(result.get("vote_count")));

        return Movie.newMovie(id, title, getImageUrl(posterPath, false))
                .originalTitle(originalTitle)
                .overview(overview.isEmpty() ? AppConstants.MISSING_OVERVIEW : overview)
                .backdropImageUrl(getImageUrl(backdropPath, true))
                .rating(rating)
                .releaseDate(DateUtils.formatDate(releaseDate))
                .voteCount(voteCount)
                .build();
    }

    private static String getImageUrl(String path, Boolean isBackDrop) {
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
