package com.mercadopago.android.px.core.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.BuildConfig;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class PicassoLoader {

    @SuppressLint("StaticFieldLeak")
    private static Picasso picasso;

    public static void initialize(@NonNull final Context context) {
        if (picasso == null) {
            picasso = new Picasso.Builder(context)
                .downloader(new CheckoutDownloader(context))
                .indicatorsEnabled(BuildConfig.DEBUG)
                .loggingEnabled(BuildConfig.DEBUG)
                .build();
        }
    }

    private static Picasso getPicasso() {
        if (picasso == null) {
            throw new ExceptionInInitializerError("Picasso is not initialized");
        }
        return picasso;
    }

    public static RequestCreator load(final String url) {
        return getPicasso().load(url);
    }

    public static RequestCreator load(@DrawableRes final int resId) {
        return getPicasso().load(resId);
    }
}
