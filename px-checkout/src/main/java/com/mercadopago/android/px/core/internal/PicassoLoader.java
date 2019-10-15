package com.mercadopago.android.px.core.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import java.io.File;

public class PicassoLoader {

    @SuppressLint("StaticFieldLeak") private static Picasso picasso;
    private static final long DISK_CACHE_SIZE = 5000000; //5MB

    public static void initialize(@NonNull final Context context) {
        if (picasso == null) {
            picasso = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(getCacheDir(context), DISK_CACHE_SIZE))
                .indicatorsEnabled(true)
                .build();
        }
    }

    private static Picasso getPicasso() {
        if (picasso == null) {
            throw new ExceptionInInitializerError("Picasso is not initialized");
        }
        return picasso;
    }

    private static File getCacheDir(@NonNull final Context context) {
        File file = context.getExternalFilesDir(null);
        return new File(file, "px-picasso/cache");
    }

    public static RequestCreator load(final String url) {
        return getPicasso().load(url);
    }

    public static RequestCreator load(@DrawableRes final int resId) {
        return getPicasso().load(resId);
    }
}
