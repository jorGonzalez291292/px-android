package com.mercadopago.android.px.internal.util;

import android.support.annotation.NonNull;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;

public final class JsonUtil {

    private static final Gson gson;

    static {
        gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .serializeNulls()
            .registerTypeAdapterFactory(ObjectMapTypeAdapter.FACTORY)
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .create();
    }

    public static <T> T fromJson(@NonNull final String json, @NonNull final Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(@NonNull final String json, @NonNull final Type classOfT) {
        return gson.fromJson(json, classOfT);
    }

    private static Map<String, Object> getMapFromJson(@NonNull final String json) {
        return gson.fromJson(
            json, new TypeToken<ObjectMapTypeAdapter.ObjectMapType>() {
            }.getType()
        );
    }

    public static Map<String, Object> getMapFromObject(@NonNull final Object src) {
        return getMapFromJson(gson.toJson(src));
    }

    public static String toJson(final Object src) {
        return gson.toJson(src);
    }

    public static Gson getGson() {
        return gson;
    }

}
