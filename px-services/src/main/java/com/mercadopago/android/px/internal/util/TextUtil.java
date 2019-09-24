package com.mercadopago.android.px.internal.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.util.regex.Pattern;

public final class TextUtil {

    public static final String EMPTY = "";
    public static final CharSequence SPACE = " ";
    private static final String CSV_DELIMITER = ",";

    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d+");

    private TextUtil() {
        throw new AssertionError("Util classes shouldn't be instantiated.");
    }

    public static boolean isEmpty(@Nullable final CharSequence text) {
        return text == null || text.length() <= 0;
    }

    public static boolean isNotEmpty(@Nullable final CharSequence text) {
        return !isEmpty(text);
    }

    public static boolean isDigitsOnly(@Nullable final CharSequence text) {
        return text != null && DIGIT_PATTERN.matcher(text).matches();
    }

    public static String join(@NonNull final Iterable<String> values) {
        return TextUtils.join(CSV_DELIMITER, values);
    }
}