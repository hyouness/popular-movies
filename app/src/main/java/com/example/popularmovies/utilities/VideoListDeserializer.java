package com.example.popularmovies.utilities;

import com.example.popularmovies.model.ResponseList;
import com.example.popularmovies.model.Video;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.popularmovies.utilities.JsonUtils.optString;

public class VideoListDeserializer implements JsonDeserializer<ResponseList<Video>> {

    private static final String YOUTUBE = "youtube";
    private static final String VIDEO_URL = "https://www.%s.com/watch?v=%s";
    private static final String IMAGE_URL = "https://i.ytimg.com/vi/%s/%s.jpg";
    private static final String MEDIUM_QUALITY = "mqdefault";
    private static final String DEFAULT = "default";

    private static Boolean isTablet;

    VideoListDeserializer(Boolean isTablet) {
        VideoListDeserializer.isTablet = isTablet;
    }

    @Override
    public ResponseList<Video> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<Video> videos = new ArrayList<>();

        final JsonObject moviesObject = json.getAsJsonObject();
        JsonArray results = moviesObject.getAsJsonArray("results");
        for (int i = 0; i < results.size(); i++) {
            JsonObject result = results.get(i).getAsJsonObject();
            Video video = getVideo(result);
            if (video.getVideoUrl().contains(YOUTUBE)) {
                videos.add(video);
            }
        }

        return new ResponseList<>(videos);
    }

    private static Video getVideo(JsonObject result) {
        String id = optString(result.get("id"));
        String key = optString(result.get("key"));
        String name = optString(result.get("name"));
        String site = optString(result.get("site"));

        return new Video(id, name, key, getVideoUrl(site, key), getImageUrl(key));
    }

    private static String getImageUrl(String key) {
        return String.format(IMAGE_URL, key, isTablet ? MEDIUM_QUALITY : DEFAULT);
    }

    private static String getVideoUrl(String site, String key) {
        return String.format(VIDEO_URL, site.toLowerCase(), key);
    }

}
