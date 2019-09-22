package com.example.popularmovies.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final class DateUtils {
    private static final SimpleDateFormat originalSDF = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final SimpleDateFormat updatedSDF = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);

    private DateUtils() {}

    static String formatDate(String releaseDate) {
        try {
            Date date = originalSDF.parse(releaseDate);
            return updatedSDF.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
