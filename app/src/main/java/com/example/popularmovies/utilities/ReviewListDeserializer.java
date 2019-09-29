package com.example.popularmovies.utilities;

import com.example.popularmovies.model.ResponseList;
import com.example.popularmovies.model.Review;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.yydcdut.markdown.MarkdownProcessor;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.popularmovies.utilities.JsonUtils.optString;

public class ReviewListDeserializer implements JsonDeserializer<ResponseList<Review>> {

    private final MarkdownProcessor markdownProcessor;

    ReviewListDeserializer(MarkdownProcessor markdownProcessor) {
        this.markdownProcessor = markdownProcessor;
    }

    @Override
    public ResponseList<Review> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<Review> reviews = new ArrayList<>();

        final JsonObject moviesObject = json.getAsJsonObject();

        Integer page = moviesObject.get("page").getAsInt();
        Integer totalPages = moviesObject.get("total_pages").getAsInt();
        Integer totalResults = moviesObject.get("total_results").getAsInt();

        JsonArray results = moviesObject.getAsJsonArray("results");
        for (int i = 0; i < results.size(); i++) {
            JsonObject result = results.get(i).getAsJsonObject();
            Review review = getReview(result, markdownProcessor);
            reviews.add(review);
        }

        return new ResponseList<>(page, totalResults, totalPages, reviews);
    }

    private static Review getReview(JsonObject result, MarkdownProcessor markdownProcessor) {
        String id = optString(result.get("id"));
        String author = optString(result.get("author"));
        String content = optString(result.get("content"));

        return new Review(id, author, markdownProcessor.parse(content));
    }

}
