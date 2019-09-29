package com.example.popularmovies.utilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

final class JsonUtils {
    static String optString(JsonElement jsonElement) {
        return isJsonNotNull(jsonElement) ? jsonElement.getAsString() : "";
    }

    static Long optLong(JsonElement jsonElement) {
        return isJsonNotNull(jsonElement) ? jsonElement.getAsLong() : 0L;
    }

    static Double optDouble(JsonElement jsonElement) {
        return isJsonNotNull(jsonElement) ? jsonElement.getAsDouble() : Double.NaN;
    }

    private static boolean isJsonNotNull(JsonElement jsonElement) {
        return !(jsonElement instanceof JsonNull);
    }
}
